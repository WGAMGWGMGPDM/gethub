package com.zr.repo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zr.repo.domain.Repository;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: 张忍
 * @Date: 2020-03-19 23:19
 */
public interface RepositoryMapper extends BaseMapper<Repository> {
    void incrementProjectNum(@Param("reopid") Integer reopid);

    void decrementProjectNum(@Param("reopid") Integer reopid);
}