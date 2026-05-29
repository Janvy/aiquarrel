package com.aiquarrel.model.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("t_generation_record")
public class GenerationRecord {
    @TableId
    private String id;
    private String deviceId;
    private String scene;
    private String style;
    private String content;
    private Integer favorited;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
