package com.feihua.poetry.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.feihua.poetry.entity.Keyword;
import com.feihua.poetry.mapper.KeywordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KeywordService {

    @Autowired
    private KeywordMapper keywordMapper;

    public List<Keyword> listAll() {
        return keywordMapper.selectList(new LambdaQueryWrapper<Keyword>().orderByAsc(Keyword::getId));
    }

    public Keyword getById(Long id) {
        return keywordMapper.selectById(id);
    }

    public Keyword create(Keyword keyword) {
        keywordMapper.insert(keyword);
        return keyword;
    }

    public Keyword update(Long id, Keyword keyword) {
        keyword.setId(id);
        keywordMapper.updateById(keyword);
        return keywordMapper.selectById(id);
    }

    public void delete(Long id) {
        keywordMapper.deleteById(id);
    }

    public long count() {
        return keywordMapper.selectCount(null);
    }
}
