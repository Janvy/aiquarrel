package com.aiquarrel.model.mapper;

import com.aiquarrel.model.entity.FavoriteRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FavoriteMapper extends BaseMapper<FavoriteRecord> {
}
