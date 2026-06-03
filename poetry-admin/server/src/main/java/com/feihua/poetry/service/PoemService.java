package com.feihua.poetry.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.feihua.poetry.common.PageResult;
import com.feihua.poetry.entity.*;
import com.feihua.poetry.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PoemService {

    @Autowired
    private PoemMapper poemMapper;
    @Autowired
    private PoemLineMapper poemLineMapper;
    @Autowired
    private PoemKeywordMapper poemKeywordMapper;

    public PageResult<Poem> page(int page, int size, Integer grade) {
        LambdaQueryWrapper<Poem> wrapper = new LambdaQueryWrapper<>();
        if (grade != null) {
            wrapper.eq(Poem::getGrade, grade);
        }
        wrapper.orderByDesc(Poem::getId);
        Page<Poem> p = poemMapper.selectPage(new Page<>(page, size), wrapper);
        p.getRecords().forEach(this::fillLines);
        return new PageResult<>(p.getRecords(), p.getTotal(), p.getCurrent(), p.getSize());
    }

    public PageResult<Poem> pageByKeyword(int page, int size, Integer grade, Long keywordId) {
        LambdaQueryWrapper<Poem> wrapper = new LambdaQueryWrapper<>();
        if (grade != null) {
            wrapper.eq(Poem::getGrade, grade);
        }
        if (keywordId != null) {
            List<Long> poemIds = poemKeywordMapper.selectList(
                    new LambdaQueryWrapper<PoemKeyword>().eq(PoemKeyword::getKeywordId, keywordId))
                    .stream().map(PoemKeyword::getPoemId).distinct().collect(Collectors.toList());
            if (poemIds.isEmpty()) {
                return new PageResult<>(Collections.emptyList(), 0, page, size);
            }
            wrapper.in(Poem::getId, poemIds);
        }
        wrapper.orderByDesc(Poem::getId);
        Page<Poem> p = poemMapper.selectPage(new Page<>(page, size), wrapper);
        p.getRecords().forEach(this::fillLines);
        return new PageResult<>(p.getRecords(), p.getTotal(), p.getCurrent(), p.getSize());
    }

    public Poem getById(Long id) {
        Poem poem = poemMapper.selectById(id);
        if (poem != null) {
            fillLines(poem);
        }
        return poem;
    }

    @Transactional
    public Poem create(Poem poem) {
        poemMapper.insert(poem);
        saveLines(poem.getId(), poem.getLines());
        return getById(poem.getId());
    }

    @Transactional
    public Poem update(Long id, Poem poem) {
        poem.setId(id);
        poemMapper.updateById(poem);
        poemLineMapper.delete(new LambdaQueryWrapper<PoemLine>().eq(PoemLine::getPoemId, id));
        saveLines(id, poem.getLines());
        return getById(id);
    }

    @Transactional
    public void delete(Long id) {
        poemMapper.deleteById(id);
        poemLineMapper.delete(new LambdaQueryWrapper<PoemLine>().eq(PoemLine::getPoemId, id));
    }

    public long count() {
        return poemMapper.selectCount(null);
    }

    private void fillLines(Poem poem) {
        List<PoemLine> lines = poemLineMapper.selectList(
                new LambdaQueryWrapper<PoemLine>()
                        .eq(PoemLine::getPoemId, poem.getId())
                        .orderByAsc(PoemLine::getSortOrder));
        poem.setLines(lines);
    }

    private void saveLines(Long poemId, List<PoemLine> lines) {
        if (lines == null) {
            return;
        }
        int order = 0;
        for (PoemLine line : lines) {
            line.setId(null);
            line.setPoemId(poemId);
            line.setSortOrder(order++);
            if (line.getLineNo() == null) {
                line.setLineNo(order);
            }
            poemLineMapper.insert(line);
        }
    }
}
