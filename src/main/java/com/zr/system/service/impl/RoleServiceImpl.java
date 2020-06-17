package com.zr.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zr.system.common.Constant;
import com.zr.system.common.DataGridView;
import com.zr.system.vo.RoleVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.Serializable;
import java.util.*;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zr.system.mapper.RoleMapper;
import com.zr.system.domain.Role;
import com.zr.system.service.RoleService;
/**
* @Author: 张忍
* @Date: 2020-03-14 13:23
*/
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService{

    @Autowired
    private RoleMapper roleMapper;
    @Override
    public DataGridView queryAllRole(RoleVo roleVo) {
        IPage<Role> page = new Page<>(roleVo.getPage(),roleVo.getLimit());
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(roleVo.getAvailable()!=null,"available",roleVo.getAvailable());
        queryWrapper.like(StringUtils.isNotBlank(roleVo.getName()),"name",roleVo.getName());
        queryWrapper.like(StringUtils.isNotBlank(roleVo.getRemark()),"remark",roleVo.getRemark());
        this.roleMapper.selectPage(page,queryWrapper);
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    @Override
    public Role saveRole(Role role) {
        this.roleMapper.insert(role);
        return role;
    }

    @Override
    public Role updateRole(Role role) {
        this.roleMapper.updateById(role);
        return role;
    }

    @Override
    public List<Integer> queryMidsByRid(Integer id) {
        return this.roleMapper.queryMidsByRid(id);
    }

    @Override
    public void saveRoleMenu(Integer rid, Integer[] mids) {
        //先删除旧的数据
        this.roleMapper.deleteRoleMenuByRid(rid);
        //保存新的数据
        if (null!=mids&&mids.length>0){
            for (Integer mid : mids) {
                this.roleMapper.insertRoleMenu(rid,mid);
            }
        }
    }

    /**
     * 查询所有可用角色不分页并选中已拥有的
     * @param roleVo
     * @return
     */
    @Override
    public DataGridView queryAllAvailabeRoleNoPage(RoleVo roleVo) {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(roleVo.getAvailable()!=null,"available",roleVo.getAvailable());
        List<Role> roles = this.roleMapper.selectList(queryWrapper);
        //根据用户ID查询已拥有的角色ID
        List<Integer> rids = this.roleMapper.queryRidsByUid(roleVo.getUid());

        List<Map<String,Object>> lists = new ArrayList<>();
        for (Role role : roles) {
            Boolean LAY_CHECKED = false;
            for (Integer rid : rids) {
                if (role.getId().equals(rid)){
                    LAY_CHECKED=true;
                    break;
                }
            }
            HashMap<String, Object> map = new HashMap<>();
            map.put("id",role.getId());
            map.put("name",role.getName());
            map.put("remark",role.getRemark());
            map.put("LAY_CHECKED",LAY_CHECKED);
            lists.add(map);
        }
        return new DataGridView(Long.valueOf(lists.size()),lists);
    }

    /**
     * 根据用户Id查询角色名，realm里用
     * @param id
     * @return
     */
    @Override
    public List<String> queryRoleNamesByUid(Integer id) {
        List<Integer> rids = this.roleMapper.queryRidsByUid(id);
        if (null!=rids&&rids.size()>0){
            QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("available", Constant.AVAILABLE_TRUE);
            queryWrapper.in("id",rids);
            List<Role> roles = this.roleMapper.selectList(queryWrapper);
            List<String> roleNames = new ArrayList<>();
            for (Role role : roles) {
                roleNames.add(role.getName());
            }
            return roleNames;
        }else {
            return null;
        }
    }

    @Override
    public boolean removeById(Serializable id) {
        //根据角色ID删除角色与菜单之间的关系
        this.roleMapper.deleteRoleMenuByRid(id);
        //根据角色ID删除角色与用户之间的关系
        this.roleMapper.deleteRoleUserByRid(id);

        return super.removeById(id);
    }

    @Override
    public boolean removeByIds(Collection<? extends Serializable> idList) {
        for (Serializable id : idList) {
            //根据角色ID删除角色与菜单之间的关系
            this.roleMapper.deleteRoleMenuByRid(id);
            //根据角色ID删除角色与用户之间的关系
            this.roleMapper.deleteRoleUserByRid(id);
        }
        return super.removeByIds(idList);
    }
}
