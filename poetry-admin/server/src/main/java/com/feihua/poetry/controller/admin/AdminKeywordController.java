package com.feihua.poetry.controller.admin;

import com.feihua.poetry.common.Result;
import com.feihua.poetry.entity.Keyword;
import com.feihua.poetry.service.KeywordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/keywords")
public class AdminKeywordController {

    @Autowired
    private KeywordService keywordService;

    @GetMapping
    public Result<List<Keyword>> list() {
        return Result.ok(keywordService.listAll());
    }

    @GetMapping("/{id}")
    public Result<Keyword> get(@PathVariable Long id) {
        return Result.ok(keywordService.getById(id));
    }

    @PostMapping
    public Result<Keyword> create(@RequestBody Keyword keyword) {
        return Result.ok(keywordService.create(keyword));
    }

    @PutMapping("/{id}")
    public Result<Keyword> update(@PathVariable Long id, @RequestBody Keyword keyword) {
        return Result.ok(keywordService.update(id, keyword));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        keywordService.delete(id);
        return Result.ok();
    }
}
