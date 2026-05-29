package com.aiquarrel.service;

import com.aiquarrel.model.dto.FavoriteResponse;
import com.aiquarrel.model.dto.HistoryResponse;

public interface FavoriteService {
    FavoriteResponse toggleFavorite(String deviceId, String generationId);
    HistoryResponse getFavorites(String deviceId, int page, int pageSize);
}
