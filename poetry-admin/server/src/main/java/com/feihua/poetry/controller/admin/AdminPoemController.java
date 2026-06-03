package com.feihua.poetry.controller.admin;

import com.feihua.poetry.common.PageResult;
import com.feihua.poetry.common.Result;
import com.feihua.poetry.entity.Poem;
import com.feihua.poetry.service.PoemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/poems")
public class AdminPoemController {

    @Autowired
    private PoemService poemService;

    @GetMapping
    public Result<PageResult<Poem>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer grade) {
        return Result.ok(poemService.page(page, size, grade));
    }

    @GetMapping("/{id}")
    public Result<Poem> get(@PathVariable Long id) {
        return Result.ok(poemService.getById(id));
    }

    @PostMapping
    public Result<Poem> create(@RequestBody Poem poem) {
        return Result.ok(poemService.create(poem));
    }

    @PutMapping("/{id}")
    public Result<Poem> update(@PathVariable Long id, @RequestBody Poem poem) {
        return Result.ok(poemService.update(id, poem));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        poemService.delete(id);
        return Result.ok();
    }
}
