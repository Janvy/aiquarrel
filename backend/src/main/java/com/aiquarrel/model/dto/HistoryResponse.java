package com.aiquarrel.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryResponse {
    private List<HistoryItem> list;
    private long total;
    private int page;
    private int pageSize;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HistoryItem {
        private String id;
        private String scene;
        private String style;
        private String styleName;
        private String contentPreview;
        private boolean favorited;
        private String createdAt;
    }
}
