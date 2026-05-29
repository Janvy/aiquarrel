package com.aiquarrel.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ShareImageRequest {
    @NotBlank(message = "记录ID不能为空")
    private String id;
}
