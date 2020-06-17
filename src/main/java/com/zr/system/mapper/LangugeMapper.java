package com.zr.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zr.system.domain.Languge;
import org.apache.ibatis.annotations.Param;

/**
* @Author: 张忍
* @Date: 2020-03-29 15:21
*/
public interface LangugeMapper extends BaseMapper<Languge> {
    void incrementProjectNum(@Param("langugeId") Integer languge);

    void decrementProjectNum(@Param("langugeId") Integer languge);
}