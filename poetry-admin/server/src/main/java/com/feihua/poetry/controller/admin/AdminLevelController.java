package com.feihua.poetry.controller.admin;

import com.feihua.poetry.common.Result;
import com.feihua.poetry.entity.LevelConfig;
import com.feihua.poetry.service.LevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/levels")
public class AdminLevelController {

    @Autowired
    private LevelService levelService;

    @GetMapping
    public Result<List<LevelConfig>> list() {
        return Result.ok(levelService.listAll());
    }

    @GetMapping("/{id}")
    public Result<LevelConfig> get(@PathVariable Long id) {
        return Result.ok(levelService.getById(id));
    }

    @PostMapping
    public Result<LevelConfig> create(@RequestBody LevelConfig level) {
        return Result.ok(levelService.create(level));
    }

    @PutMapping("/{id}")
    public Result<LevelConfig> update(@PathVariable Long id, @RequestBody LevelConfig level) {
        return Result.ok(levelService.update(id, level));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        levelService.delete(id);
        return Result.ok();
    }
}
