package com.zr.system.service;

import com.zr.system.common.DataGridView;
import com.zr.system.domain.Role;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zr.system.vo.RoleVo;

import java.util.List;

/**
* @Author: 张忍
* @Date: 2020-03-14 13:23
*/
public interface RoleService extends IService<Role>{

    DataGridView queryAllRole(RoleVo roleVo);

    Role saveRole(Role role);

    Role updateRole(Role role);

    List<Integer> queryMidsByRid(Integer id);

    void saveRoleMenu(Integer rid, Integer[] mids);

    DataGridView queryAllAvailabeRoleNoPage(RoleVo roleVo);

    List<String> queryRoleNamesByUid(Integer id);
}
