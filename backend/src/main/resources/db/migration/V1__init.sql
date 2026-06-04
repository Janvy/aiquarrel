-- AI吵架生成器 V1 初始化 DDL
-- H2 MySQL 兼容模式: jdbc:h2:file:./data/aiquarrel;MODE=MySQL;DATABASE_TO_LOWER=TRUE
-- 升级 MySQL 时: 仅需加回 ENGINE/CHARSET/COLLATE 子句

-- 生成记录表
CREATE TABLE IF NOT EXISTS t_generation_record (
    id          VARCHAR(32)  NOT NULL PRIMARY KEY,
    device_id   VARCHAR(64)  NOT NULL,
    scene       VARCHAR(200) NOT NULL,
    style       VARCHAR(32)  NOT NULL,
    content     TEXT         NOT NULL,
    favorited   TINYINT      NOT NULL DEFAULT 0,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_device_created ON t_generation_record (device_id, created_at);
CREATE INDEX IF NOT EXISTS idx_device_favorite ON t_generation_record (device_id, favorited);

-- 收藏记录表
CREATE TABLE IF NOT EXISTS t_favorite (
    id             BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    device_id      VARCHAR(64)  NOT NULL,
    generation_id  VARCHAR(32)  NOT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_device_gen ON t_favorite (device_id, generation_id);
CREATE INDEX IF NOT EXISTS idx_device ON t_favorite (device_id, created_at);

-- 设备信息表
CREATE TABLE IF NOT EXISTS t_device (
    id           BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    device_id    VARCHAR(64)  NOT NULL UNIQUE,
    daily_count  INT          NOT NULL DEFAULT 0,
    daily_date   DATE,
    total_count  INT          NOT NULL DEFAULT 0,
    created_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_device_date ON t_device (device_id, daily_date);

-- 敏感词库
CREATE TABLE IF NOT EXISTS t_sensitive_word (
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    word        VARCHAR(100) NOT NULL,
    level       TINYINT      NOT NULL DEFAULT 1,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_word ON t_sensitive_word (word);

-- 初始化敏感词数据
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('杀人', 1);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('自杀', 1);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('毒品', 1);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('赌博', 1);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('嫖娼', 1);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('卖淫', 1);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('恐怖袭击', 1);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('颠覆国家', 1);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('分裂国家', 1);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('法轮功', 1);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('傻逼', 2);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('操你', 2);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('妈的', 2);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('去死', 2);
MERGE INTO t_sensitive_word (word, level) KEY (word) VALUES ('废物', 2);
