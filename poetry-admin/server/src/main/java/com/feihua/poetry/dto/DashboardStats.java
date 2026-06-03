package com.feihua.poetry.dto;

import lombok.Data;

import java.util.Map;

@Data
public class DashboardStats {
    private long poemCount;
    private long keywordCount;
    private long levelCount;
    private long lineCount;
}
