package com.aiquarrel.model.mapper;

import com.aiquarrel.model.entity.GenerationRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface GenerationMapper extends BaseMapper<GenerationRecord> {
}
