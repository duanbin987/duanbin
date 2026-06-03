package com.feihua.poetry.dto;

import lombok.Data;

@Data
public class CheckAnswerRequest {
    private String questionId;
    private String answer;
}
