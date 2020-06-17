package com.zr.system.controller;


import com.zr.system.common.Constant;
import com.zr.system.common.DataGridView;
import com.zr.system.common.ResultObj;
import com.zr.system.domain.Role;
import com.zr.system.service.RoleService;
import com.zr.system.vo.RoleVo;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: 张忍
 * @Date: 2020-03-09 15:27
 */
@RestController
@RequestMapping("api/role")
public class RoleController {
    @Autowired
    private RoleService roleService;

    /**
     * 加载所有角色
     * @param roleVo
     * @return
     */
    @RequestMapping("loadAllRole")
    public Object loadAllRole(RoleVo roleVo){
        return this.roleService.queryAllRole(roleVo);
    }

    /**
     * 添加角色
     * @param role
     * @return
     */
    @RequestMapping("addRole")
    @RequiresPermissions("role:add")
    public ResultObj addRole(Role role){
        try {
            role.setCreatetime(new Date());
            role.setAvailable(Constant.AVAILABLE_TRUE);

            this.roleService.saveRole(role);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }

    /**
     * 修改角色
     * @param role
     * @return
     */
    @RequestMapping("updateRole")
    @RequiresPermissions("role:update")
    public ResultObj updateRole(Role role){
        try {
            this.roleService.updateRole(role);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 删除
     */
    @RequestMapping("/deleteRole")
    @RequiresPermissions("role:delete")
    public ResultObj deleteRole(Integer id){
        try {
            this.roleService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    /**
     * 批量删除
     */
    @RequestMapping("/batchDeleteRole")
    @RequiresPermissions("role:batchDelete")
    public ResultObj batchDeleteRole(Integer[] ids){
        try {
            if (null!=ids&&ids.length>0){
                List<Integer> idList = new ArrayList<>();
                for (Integer id : ids) {
                    idList.add(id);
                }
                this.roleService.removeByIds(idList);
                return ResultObj.DELETE_SUCCESS;
            }else {
                return new ResultObj(-1,"传入id为空");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    /**
     * 根据角色id查询拥有的菜单权限id
     * @param id
     * @return
     */
    @RequestMapping("queryMidsByRid")
    public Object queryMidsByRid(Integer id){
        List<Integer> mids = this.roleService.queryMidsByRid(id);
        return new DataGridView(mids);
    }

    /**
     * 保存角色的菜单权限
     * @param rid
     * @param mids
     * @return
     */
    @RequestMapping("addRoleMenu")
    @RequiresPermissions("role:dispatch")
    public ResultObj addRoleMenu(Integer rid,Integer[] mids){
        try {
            this.roleService.saveRoleMenu(rid,mids);
            return ResultObj.DISPATCH_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DISPATCH_ERROR;
        }
    }

    /**
     * 查询所有可用角色不分页并选中已拥有的
     * @param roleVo
     * @return
     */
    @RequestMapping("loadAllAvailabeRoleNoPage")
    public Object loadAllAvailabeRoleNoPage(RoleVo roleVo){
        roleVo.setAvailable(Constant.AVAILABLE_TRUE);
        return this.roleService.queryAllAvailabeRoleNoPage(roleVo);

    }







}
