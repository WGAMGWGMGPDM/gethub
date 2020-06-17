package com.zr.repo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zr.repo.domain.Project;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
* @Author: 张忍
* @Date: 2020-03-20 14:23
*/
public interface ProjectMapper extends BaseMapper<Project> {
    Integer queryProjectMaxId();

    void insertStar(@Param("uid") Integer uid, @Param("pid") Integer pid);

    void deleteStar(@Param("uid") Integer uid, @Param("pid") Integer pid);

    Integer queryStar(@Param("uid") Integer uid, @Param("pid") Integer pid);

    List<Integer> queryPidsByUid(@Param("uid") Integer uid);

    void deleteStarByPid(@Param("pid") Serializable id);

    Integer queryStarNumByPid(@Param("pid") Integer pid);


}