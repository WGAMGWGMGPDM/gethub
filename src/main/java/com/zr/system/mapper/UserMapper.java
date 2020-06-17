package com.zr.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zr.system.domain.User;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

public interface UserMapper extends BaseMapper<User> {
    Integer queryUserMaxOrderNum();

    void saveUserRole(@Param("uid") Integer uid, @Param("rid") Integer rid);

    void insertFollow(@Param("uid") Integer uid, @Param("fid") Integer fid);

    void deleteFollow(@Param("uid")Integer uid, @Param("fid")Integer fid);

    Integer queryFollow(@Param("uid")Integer uid, @Param("fid")Integer fid);

    List<Integer> queryFidsByUid(Integer uid);

    void deleteProjectStarByUid(@Param("uid") Serializable uid);

    void deleteArticleStarByUid(@Param("uid") Serializable uid);

    void deleteFollowByUid(@Param("uid") Serializable id);

    void deleteFollowByFid(@Param("fid") Serializable id);
}