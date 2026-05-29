package com.aiquarrel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageResponse {
    private int dailyCount;
    private int totalCount;
    private int dailyLimit;
}
