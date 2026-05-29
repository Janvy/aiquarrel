package com.aiquarrel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerateResponse {
    private String id;
    private String scene;
    private String style;
    private String content;
    private boolean favorited;
    private String createdAt;
}
