package com.feihua.poetry.service;

import com.feihua.poetry.dto.DashboardStats;
import com.feihua.poetry.mapper.PoemLineMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    @Autowired
    private PoemService poemService;
    @Autowired
    private KeywordService keywordService;
    @Autowired
    private LevelService levelService;
    @Autowired
    private PoemLineMapper poemLineMapper;

    public DashboardStats getStats() {
        DashboardStats stats = new DashboardStats();
        stats.setPoemCount(poemService.count());
        stats.setKeywordCount(keywordService.count());
        stats.setLevelCount(levelService.count());
        stats.setLineCount(poemLineMapper.selectCount(null));
        return stats;
    }
}
