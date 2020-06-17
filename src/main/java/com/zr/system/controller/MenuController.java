package com.zr.system.controller;


import com.zr.system.common.Constant;
import com.zr.system.common.DataGridView;
import com.zr.system.common.ResultObj;
import com.zr.system.domain.Menu;
import com.zr.system.service.MenuService;
import com.zr.system.vo.MenuVo;
import io.swagger.models.auth.In;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: 张忍
 * @Date: 2020-03-09 15:27
 */
@RestController
@RequestMapping("api/menu")
public class MenuController {
    @Autowired
    private MenuService menuService;

    /**
     * 加载所有菜单和权限
     * @param menuVo
     * @return
     */
    @RequestMapping("loadAllMenu")
    public Object loadAllMenu(MenuVo menuVo){
        return this.menuService.queryAllMenu(menuVo);
    }

    /**
     * 添加菜单或权限
     * @param menu
     * @return
     */
    @RequestMapping("addMenu")
    @RequiresPermissions("menu:add")
    public ResultObj addMenu(Menu menu){
        try {
            if (menu.getType().equals("topmenu")){
                menu.setPid(0);
            }
            menu.setSpread(Constant.SPREAD_FALSE);
            menu.setAvailable(Constant.AVAILABLE_TRUE);

            this.menuService.saveMenu(menu);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }

    /**
     * 修改菜单和权限
     * @param menu
     * @return
     */
    @RequestMapping("updateMenu")
    @RequiresPermissions("menu:update")
    public ResultObj updateMenu(Menu menu){
        try {
            this.menuService.updateMenu(menu);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 查询菜单
     * @param
     * @return
     */
    @RequestMapping("loadMenu")
    public Object loadMenu(){
        List<Menu> menus = this.menuService.queryAllMenuForList();
        return new DataGridView(Long.valueOf(menus.size()),menus);
    }

    /**
     * 加载最大排序码
     * @return
     */
    @RequestMapping("queryMenuMaxOrdernum")
    public Object queryMenuMaxOrdernum(){
        Integer maxNum = this.menuService.queryMenuMaxOrdernum();
        return new DataGridView(maxNum+1);
    }

    /**
     * 获得一个菜单
     * @param id
     * @return
     */
    @GetMapping("getMenuById")
    public Object getMenuById(Integer id){
        return new DataGridView(this.menuService.getById(id));
    }


    /**
     * 删除
     */
    @RequestMapping("/deleteMenu")
    @RequiresPermissions("menu:delete")
    public ResultObj deleteMenu(Integer id){
        try {
            this.menuService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    /**
     * 根据id查询当前菜单或权限是否有子节点
     * @param id
     * @return
     */
    @GetMapping("getMenuChildrenCountById")
    public Object getMenuChildrenCountById(Integer id){
        Integer couunt = this.menuService.queryMenuChildrenCountById(id);
        return new DataGridView(couunt);
    }
}
