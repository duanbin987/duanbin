package com.feihua.poetry.controller.app;

import com.feihua.poetry.common.PageResult;
import com.feihua.poetry.common.Result;
import com.feihua.poetry.dto.CheckAnswerRequest;
import com.feihua.poetry.dto.CheckAnswerResponse;
import com.feihua.poetry.dto.QuestionDto;
import com.feihua.poetry.engine.QuestionEngine;
import com.feihua.poetry.entity.Keyword;
import com.feihua.poetry.entity.LevelConfig;
import com.feihua.poetry.entity.Poem;
import com.feihua.poetry.mapper.KeywordMapper;
import com.feihua.poetry.service.LevelService;
import com.feihua.poetry.service.PoemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/app")
public class AppController {

    @Autowired
    private LevelService levelService;
    @Autowired
    private QuestionEngine questionEngine;
    @Autowired
    private PoemService poemService;
    @Autowired
    private KeywordMapper keywordMapper;

    @GetMapping("/levels")
    public Result<List<LevelConfig>> levels() {
        return Result.ok(levelService.listAll());
    }

    @GetMapping("/levels/{id}/questions")
    public Result<List<QuestionDto>> questions(@PathVariable Long id,
                                               @RequestParam(defaultValue = "10") int count) {
        return Result.ok(questionEngine.generateQuestions(id, count));
    }

    @PostMapping("/questions/check")
    public Result<CheckAnswerResponse> check(@RequestBody CheckAnswerRequest request) {
        return Result.ok(questionEngine.checkAnswer(request.getQuestionId(), request.getAnswer()));
    }

    @GetMapping("/poems")
    public Result<PageResult<Poem>> poems(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer grade,
            @RequestParam(required = false) Long keywordId) {
        return Result.ok(poemService.pageByKeyword(page, size, grade, keywordId));
    }

    @GetMapping("/poems/{id}")
    public Result<Poem> poemDetail(@PathVariable Long id) {
        return Result.ok(poemService.getById(id));
    }

    @GetMapping("/keywords")
    public Result<List<Keyword>> keywords() {
        return Result.ok(keywordMapper.selectList(null));
    }
}
