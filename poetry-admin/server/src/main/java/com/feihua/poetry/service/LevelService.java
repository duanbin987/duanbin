package com.feihua.poetry.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.feihua.poetry.entity.Keyword;
import com.feihua.poetry.entity.LevelConfig;
import com.feihua.poetry.mapper.KeywordMapper;
import com.feihua.poetry.mapper.LevelConfigMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LevelService {

    @Autowired
    private LevelConfigMapper levelConfigMapper;
    @Autowired
    private KeywordMapper keywordMapper;

    public List<LevelConfig> listAll() {
        List<LevelConfig> levels = levelConfigMapper.selectList(
                new LambdaQueryWrapper<LevelConfig>().orderByAsc(LevelConfig::getSortOrder));
        levels.forEach(this::fillKeyword);
        return levels;
    }

    public LevelConfig getById(Long id) {
        LevelConfig level = levelConfigMapper.selectById(id);
        if (level != null) {
            fillKeyword(level);
        }
        return level;
    }

    public LevelConfig create(LevelConfig level) {
        levelConfigMapper.insert(level);
        fillKeyword(level);
        return level;
    }

    public LevelConfig update(Long id, LevelConfig level) {
        level.setId(id);
        levelConfigMapper.updateById(level);
        return getById(id);
    }

    public void delete(Long id) {
        levelConfigMapper.deleteById(id);
    }

    public long count() {
        return levelConfigMapper.selectCount(null);
    }

    private void fillKeyword(LevelConfig level) {
        Keyword kw = keywordMapper.selectById(level.getKeywordId());
        if (kw != null) {
            level.setKeywordChar(kw.getCharValue());
        }
    }
}
