package com.feihua.poetry.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.feihua.poetry.common.PageResult;
import com.feihua.poetry.entity.Poem;
import com.feihua.poetry.entity.PoemKeyword;
import com.feihua.poetry.entity.PoemLine;
import com.feihua.poetry.mapper.PoemKeywordMapper;
import com.feihua.poetry.mapper.PoemLineMapper;
import com.feihua.poetry.mapper.PoemMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 诗词 Redis 缓存：单首 feihua:poem:{id}，全量列表 feihua:poem:all
 */
@Service
public class PoemCacheService {

    private static final Logger log = LoggerFactory.getLogger(PoemCacheService.class);

    private static final String POEM_KEY_PREFIX = "feihua:poem:";
    private static final String POEM_ALL_KEY = "feihua:poem:all";
    private static final String POEM_IDS_KEY = "feihua:poem:ids";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private ObjectMapper redisObjectMapper;

    @Autowired
    private PoemMapper poemMapper;

    @Autowired
    private PoemLineMapper poemLineMapper;

    @Autowired
    private PoemKeywordMapper poemKeywordMapper;

    /**
     * 从 MySQL 全量加载诗词（含诗句）并写入 Redis
     */
    public void reloadFromDatabase() {
        List<Poem> poems = poemMapper.selectList(
                new LambdaQueryWrapper<Poem>().orderByAsc(Poem::getId));
        for (Poem poem : poems) {
            fillLines(poem);
        }

        try {
            Set<String> oldKeys = stringRedisTemplate.keys(POEM_KEY_PREFIX + "*");
            if (oldKeys != null && !oldKeys.isEmpty()) {
                stringRedisTemplate.delete(oldKeys);
            }
            stringRedisTemplate.delete(POEM_ALL_KEY);
            stringRedisTemplate.delete(POEM_IDS_KEY);

            for (Poem poem : poems) {
                String json = redisObjectMapper.writeValueAsString(poem);
                stringRedisTemplate.opsForValue().set(POEM_KEY_PREFIX + poem.getId(), json);
                stringRedisTemplate.opsForSet().add(POEM_IDS_KEY, String.valueOf(poem.getId()));
            }
            stringRedisTemplate.opsForValue().set(POEM_ALL_KEY, redisObjectMapper.writeValueAsString(poems));
            log.info("诗词缓存已刷新，共 {} 首", poems.size());
        } catch (Exception e) {
            log.error("写入 Redis 诗词缓存失败", e);
            throw new IllegalStateException("诗词缓存刷新失败: " + e.getMessage());
        }
    }

    public Poem getById(Long id) {
        if (id == null) {
            return null;
        }
        try {
            String json = stringRedisTemplate.opsForValue().get(POEM_KEY_PREFIX + id);
            if (StringUtils.hasText(json)) {
                return redisObjectMapper.readValue(json, Poem.class);
            }
        } catch (Exception e) {
            log.warn("读取 Redis 诗词缓存失败 id={}", id, e);
        }
        return null;
    }

    public List<Poem> getAll() {
        try {
            String json = stringRedisTemplate.opsForValue().get(POEM_ALL_KEY);
            if (StringUtils.hasText(json)) {
                return redisObjectMapper.readValue(json, new TypeReference<List<Poem>>() {});
            }
        } catch (Exception e) {
            log.warn("读取 Redis 诗词全量列表失败", e);
        }
        return null;
    }

    public PageResult<Poem> page(int page, int size, Integer grade, Long keywordId) {
        List<Poem> source = getAll();
        if (source == null || source.isEmpty()) {
            return null;
        }

        List<Poem> filtered = new ArrayList<>(source);
        if (grade != null) {
            filtered = filtered.stream()
                    .filter(p -> grade.equals(p.getGrade()))
                    .collect(Collectors.toList());
        }
        if (keywordId != null) {
            Set<Long> poemIds = poemKeywordMapper.selectList(
                    new LambdaQueryWrapper<PoemKeyword>().eq(PoemKeyword::getKeywordId, keywordId))
                    .stream().map(PoemKeyword::getPoemId).collect(Collectors.toSet());
            if (poemIds.isEmpty()) {
                return new PageResult<>(Collections.emptyList(), 0, page, size);
            }
            filtered = filtered.stream()
                    .filter(p -> poemIds.contains(p.getId()))
                    .collect(Collectors.toList());
        }

        filtered.sort((a, b) -> Long.compare(b.getId(), a.getId()));
        long total = filtered.size();
        int from = Math.max(0, (page - 1) * size);
        if (from >= filtered.size()) {
            return new PageResult<>(Collections.emptyList(), total, page, size);
        }
        int to = Math.min(from + size, filtered.size());
        return new PageResult<>(filtered.subList(from, to), total, page, size);
    }

    public void evictPoem(Long id) {
        if (id != null) {
            stringRedisTemplate.delete(POEM_KEY_PREFIX + id);
            stringRedisTemplate.opsForSet().remove(POEM_IDS_KEY, String.valueOf(id));
        }
        stringRedisTemplate.delete(POEM_ALL_KEY);
    }

    private void fillLines(Poem poem) {
        List<PoemLine> lines = poemLineMapper.selectList(
                new LambdaQueryWrapper<PoemLine>()
                        .eq(PoemLine::getPoemId, poem.getId())
                        .orderByAsc(PoemLine::getSortOrder));
        poem.setLines(lines);
    }
}
