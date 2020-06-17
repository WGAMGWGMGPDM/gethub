package com.zr.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zr.system.domain.Useraddr;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: 张忍
 * @Date: 2020-05-19 19:18
 */
public interface UseraddrMapper extends BaseMapper<Useraddr> {
    void incrementAddrNum(@Param("addr") String addr);

    void decrementAddrNum(@Param("addr") String addr);
}