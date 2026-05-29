package com.aiquarrel.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("t_device")
public class DeviceInfo {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String deviceId;
    private Integer dailyCount;
    private LocalDate dailyDate;
    private Integer totalCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
