package com.zr.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.zr.system.domain.Role;
import org.apache.ibatis.annotations.Param;

import java.io.Serializable;
import java.util.List;

/**
 * @Author: 张忍
 * @Date: 2020-03-14 13:23
 */
public interface RoleMapper extends BaseMapper<Role> {
    void deleteRoleMenuByRid(Serializable id);

    void deleteRoleUserByRid(Serializable id);

    void deleteRoleUserByUid(Serializable id);

    void deleteRoleMenuByMid(Serializable id);

    List<Integer> queryMidsByRid(Integer id);

    void insertRoleMenu(@Param("rid") Integer rid, @Param("mid") Integer mid);

    List<Integer> queryRidsByUid(Integer uid);

    List<Integer> queryMidsByRids(@Param("rids") List<Integer> rids);
}