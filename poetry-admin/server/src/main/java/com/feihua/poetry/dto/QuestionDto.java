package com.feihua.poetry.dto;

import lombok.Data;

import java.util.List;

@Data
public class QuestionDto {
    private String questionId;
    private String type;
    private String keyword;
    private String poemTitle;
    private String author;
    private List<LineOption> lines;
    private String displayLine;
    private String displayPinyin;
    private Integer blankIndex;
    private List<String> options;

    @Data
    public static class LineOption {
        private Long id;
        private String content;
        private String pinyin;
        private Long poemId;
        private String poemTitle;
    }
}
