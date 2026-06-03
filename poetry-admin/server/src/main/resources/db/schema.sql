-- 飞花令诗词库 · MySQL 5.7
CREATE DATABASE IF NOT EXISTS feihua_poetry DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE feihua_poetry;

DROP TABLE IF EXISTS poem_keyword;
DROP TABLE IF EXISTS poem_line;
DROP TABLE IF EXISTS level_config;
DROP TABLE IF EXISTS poem;
DROP TABLE IF EXISTS keyword;
DROP TABLE IF EXISTS admin_user;

CREATE TABLE admin_user (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    password    VARCHAR(100) NOT NULL,
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE poem (
    id              BIGINT PRIMARY KEY AUTO_INCREMENT,
    title           VARCHAR(100) NOT NULL,
    author          VARCHAR(50)  NOT NULL,
    dynasty         VARCHAR(20)  NOT NULL,
    grade           INT          NOT NULL DEFAULT 1 COMMENT '1-6 年级',
    content_summary VARCHAR(500),
    created_at      DATETIME     DEFAULT CURRENT_TIMESTAMP,
    deleted         TINYINT      DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE poem_line (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    poem_id     BIGINT       NOT NULL,
    line_no     INT          NOT NULL,
    content     VARCHAR(200) NOT NULL,
    pinyin      VARCHAR(400) NOT NULL,
    sort_order  INT          NOT NULL DEFAULT 0,
    deleted     TINYINT      DEFAULT 0,
    INDEX idx_poem_id (poem_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE keyword (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    char_value  CHAR(1)      NOT NULL UNIQUE,
    description VARCHAR(100),
    created_at  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    deleted     TINYINT      DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE poem_keyword (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    poem_id     BIGINT NOT NULL,
    keyword_id  BIGINT NOT NULL,
    line_id     BIGINT NOT NULL COMMENT '包含该字的诗句行',
    deleted     TINYINT DEFAULT 0,
    INDEX idx_poem_keyword (poem_id, keyword_id),
    INDEX idx_keyword_id (keyword_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE level_config (
    id          BIGINT PRIMARY KEY AUTO_INCREMENT,
    name        VARCHAR(50)  NOT NULL,
    keyword_id  BIGINT       NOT NULL,
    difficulty  INT          NOT NULL DEFAULT 1 COMMENT '1-简单 2-中等 3-困难',
    poem_count  INT          NOT NULL DEFAULT 5,
    description VARCHAR(200),
    sort_order  INT          NOT NULL DEFAULT 0,
    deleted     TINYINT      DEFAULT 0,
    INDEX idx_keyword_id (keyword_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
