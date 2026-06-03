package com.feihua.poetry.dto;

import lombok.Data;

@Data
public class CheckAnswerResponse {
    private boolean correct;
    private String correctAnswer;

    public CheckAnswerResponse(boolean correct, String correctAnswer) {
        this.correct = correct;
        this.correctAnswer = correctAnswer;
    }
}
