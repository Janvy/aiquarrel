-- AI吵架生成器 V1 初始化 DDL

CREATE DATABASE IF NOT EXISTS aiquarrel
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE aiquarrel;

-- 生成记录表
CREATE TABLE IF NOT EXISTS t_generation_record (
    id          VARCHAR(32)  NOT NULL PRIMARY KEY COMMENT '生成记录ID (gen_ + yyyyMMdd + 6位序号)',
    device_id   VARCHAR(64)  NOT NULL COMMENT '设备唯一标识 (客户端UUID)',
    scene       VARCHAR(200) NOT NULL COMMENT '用户输入的场景描述',
    style       VARCHAR(32)  NOT NULL COMMENT '风格枚举: diplomatic/passive_aggressive/crazy/literary/bossy',
    content     TEXT         NOT NULL COMMENT 'AI生成的完整文案',
    favorited   TINYINT(1)   NOT NULL DEFAULT 0 COMMENT '是否已收藏: 0=否 1=是',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间',
    updated_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',

    INDEX idx_device_created (device_id, created_at DESC),
    INDEX idx_device_favorite (device_id, favorited)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='AI生成记录表';

-- 收藏记录表
CREATE TABLE IF NOT EXISTS t_favorite (
    id             BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    device_id      VARCHAR(64)  NOT NULL COMMENT '设备唯一标识',
    generation_id  VARCHAR(32)  NOT NULL COMMENT '生成记录ID',
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',

    UNIQUE INDEX uk_device_gen (device_id, generation_id),
    INDEX idx_device (device_id, created_at DESC)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏记录表';

-- 设备信息表
CREATE TABLE IF NOT EXISTS t_device (
    id           BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    device_id    VARCHAR(64)  NOT NULL UNIQUE COMMENT '设备UUID',
    daily_count  INT          NOT NULL DEFAULT 0 COMMENT '当日已生成次数',
    daily_date   DATE         COMMENT '计数日期',
    total_count  INT          NOT NULL DEFAULT 0 COMMENT '累计生成次数',
    created_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_device_date (device_id, daily_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='设备信息表';

-- 敏感词库
CREATE TABLE IF NOT EXISTS t_sensitive_word (
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY,
    word        VARCHAR(100) NOT NULL COMMENT '敏感词',
    level       TINYINT      NOT NULL DEFAULT 1 COMMENT '1=直接拦截 2=替换为***',
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    UNIQUE INDEX uk_word (word)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='敏感词库';

-- 初始化敏感词数据
INSERT IGNORE INTO t_sensitive_word (word, level) VALUES
('杀人', 1),
('自杀', 1),
('毒品', 1),
('赌博', 1),
('嫖娼', 1),
('卖淫', 1),
('恐怖袭击', 1),
('颠覆国家', 1),
('分裂国家', 1),
('法轮功', 1),
('傻逼', 2),
('操你', 2),
('妈的', 2),
('去死', 2),
('废物', 2);
