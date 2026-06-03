package com.feihua.poetry.config;

import com.feihua.poetry.service.PoemCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 应用启动后将诗词全量预热到 Redis
 */
@Component
@Order(20)
public class PoemCacheInitializer implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(PoemCacheInitializer.class);

    @Autowired
    private PoemCacheService poemCacheService;

    @Override
    public void run(ApplicationArguments args) {
        try {
            poemCacheService.reloadFromDatabase();
        } catch (Exception e) {
            log.error("启动时预热诗词 Redis 缓存失败，请检查 Redis 是否已启动（端口 6333）", e);
        }
    }
}
