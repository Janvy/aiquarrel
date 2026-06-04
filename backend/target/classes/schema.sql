-- AI吵架生成器 DDL（H2 MySQL 兼容模式）
-- 由 Spring Boot spring.sql.init 在启动时自动执行

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

CREATE TABLE IF NOT EXISTS t_favorite (
    id             BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    device_id      VARCHAR(64)  NOT NULL,
    generation_id  VARCHAR(32)  NOT NULL,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_device_gen ON t_favorite (device_id, generation_id);
CREATE INDEX IF NOT EXISTS idx_device ON t_favorite (device_id, created_at);

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

CREATE TABLE IF NOT EXISTS t_sensitive_word (
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    word        VARCHAR(100) NOT NULL,
    level       TINYINT      NOT NULL DEFAULT 1,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX IF NOT EXISTS uk_word ON t_sensitive_word (word);
