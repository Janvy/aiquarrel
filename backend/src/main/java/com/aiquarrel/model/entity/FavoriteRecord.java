package com.aiquarrel.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_favorite")
public class FavoriteRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String deviceId;
    private String generationId;
    private LocalDateTime createdAt;
}
