package com.feihua.poetry.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.feihua.poetry.entity.AdminUser;
import com.feihua.poetry.mapper.AdminUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 启动时确保 admin 账号存在且密码为 admin123（避免 seed SQL 中 BCrypt 不一致导致无法登录）
 */
@Component
public class AdminUserInitializer implements ApplicationRunner {

    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "admin123";

    @Autowired
    private AdminUserMapper adminUserMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(ApplicationArguments args) {
        AdminUser user = adminUserMapper.selectOne(
                new LambdaQueryWrapper<AdminUser>().eq(AdminUser::getUsername, DEFAULT_USERNAME));
        String encoded = passwordEncoder.encode(DEFAULT_PASSWORD);
        if (user == null) {
            user = new AdminUser();
            user.setUsername(DEFAULT_USERNAME);
            user.setPassword(encoded);
            adminUserMapper.insert(user);
        } else if (!passwordEncoder.matches(DEFAULT_PASSWORD, user.getPassword())) {
            user.setPassword(encoded);
            adminUserMapper.updateById(user);
        }
    }
}
