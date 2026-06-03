package com.feihua.poetry.controller.admin;

import com.feihua.poetry.common.Result;
import com.feihua.poetry.dto.LoginRequest;
import com.feihua.poetry.dto.LoginResponse;
import com.feihua.poetry.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/admin/auth")
public class AdminAuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public Result<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return Result.ok(authService.login(request.getUsername(), request.getPassword()));
    }
}
