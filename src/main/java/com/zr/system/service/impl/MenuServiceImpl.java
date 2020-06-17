package com.zr.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zr.system.common.Constant;
import com.zr.system.common.DataGridView;
import com.zr.system.domain.Role;
import com.zr.system.mapper.RoleMapper;
import com.zr.system.vo.MenuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zr.system.mapper.MenuMapper;
import com.zr.system.domain.Menu;
import com.zr.system.service.MenuService;
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService{

    @Autowired
    private MenuMapper menuMapper;
    @Autowired
    private RoleMapper roleMapper;

    @Override
    public List<Menu> queryAllMenuForList() {
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("available", Constant.AVAILABLE_TRUE);
        queryWrapper.and(new Consumer<QueryWrapper<Menu>>() {
            @Override
            public void accept(QueryWrapper<Menu> menuQueryWrapper) {
                menuQueryWrapper.eq("type",Constant.MENU_TYPE_TOP)
                        .or().eq("type",Constant.MENU_TYPE_LEFT);
            }
        });
        queryWrapper.orderByAsc("ordernum");
        return this.menuMapper.selectList(queryWrapper);
    }

    /**
     * 根据用户id查询菜单权限
     * @param id 用户id
     * @return
     */
    @Override
    public List<Menu> queryMenuForListByUserId(Integer id) {
        //根据uid查询rid
        List<Integer> rids = this.roleMapper.queryRidsByUid(id);
        //根据rid查询mid
        if(null!=rids&&rids.size()>0){
            List<Integer> mids = this.roleMapper.queryMidsByRids(rids);
            //根据mid查询菜单
            if (null!=mids&&mids.size()>0){
                QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("available",Constant.AVAILABLE_TRUE);
                queryWrapper.and(new Consumer<QueryWrapper<Menu>>() {
                    @Override
                    public void accept(QueryWrapper<Menu> menuQueryWrapper) {
                        menuQueryWrapper.eq("type",Constant.MENU_TYPE_TOP)
                                .or().eq("type",Constant.MENU_TYPE_LEFT);
                    }
                });
                queryWrapper.in("id",mids);
                queryWrapper.orderByAsc("ordernum");
                List<Menu> menus = this.menuMapper.selectList(queryWrapper);
                return menus;
            }else {
                return new ArrayList<>();
            }
        }else {
            return new ArrayList<>();
        }
    }

    /**
     * 加载所有菜单构造菜单树
     * @param menuVo
     * @return
     */
    @Override
    public DataGridView queryAllMenu(MenuVo menuVo) {
        QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(menuVo.getAvailable()!=null,"available",menuVo.getAvailable());
        queryWrapper.orderByAsc("ordernum");
        List<Menu> menus = this.menuMapper.selectList(queryWrapper);
        return new DataGridView(Long.valueOf(menus.size()),menus);
    }

    /**
     * 根据Id更新菜单
     * @param menu
     * @return
     */
    @CachePut(cacheNames = "com.zr.system.service.impl.MenuServiceImpl",key = "#result.id")
    @Override
    public Menu updateMenu(Menu menu) {
        this.menuMapper.updateById(menu);
        //解决部分更新时信息丢失问题。（是否可用，是否展开）
        Menu select = this.menuMapper.selectById(menu.getId());
        return select;
    }

    /**
     * 添加菜单
     * @param menu
     * @return
     */
    @Override
    @CachePut(cacheNames = "com.zr.system.service.impl.MenuServiceImpl" ,key = "#result.id")
    public Menu saveMenu(Menu menu) {
        this.menuMapper.insert(menu);
        return menu;
    }

    /**
     * 查询一个父级菜单
     * @param id
     * @return
     */
    @Override
    @Cacheable(cacheNames = "com.zr.system.service.impl.MenuServiceImpl" ,key = "#id")
    public Menu getById(Serializable id) {
        return super.getById(id);
    }

    /**
     * 根据ID删除菜单
     * @param id
     * @return
     */
    @Override
    @CacheEvict(cacheNames = "com.zr.system.service.impl.MenuServiceImpl",key = "#id")
    public boolean removeById(Serializable id) {
        //根据角色ID删除角色与菜单之间的关系
        this.roleMapper.deleteRoleMenuByMid(id);
        return super.removeById(id);
    }

    /**
     * 加载最大排序码
     * @return
     */
    @Override
    public Integer queryMenuMaxOrdernum() {
        return this.menuMapper.queryMenuMaxOrdernum();
    }

    /**
     * 查询菜单子节点数
     * @param id
     * @return
     */
    @Override
    public Integer queryMenuChildrenCountById(Integer id) {
        return this.menuMapper.queryMenuChildrenCountById(id);
    }

    /**
     * 根据用户ID查询权限编码 realm用
     * @param id
     * @return
     */
    @Override
    public List<String> queryPermissioncodesByUid(Integer id) {
        //根据用户id查询角色id
        List<Integer> rids = this.roleMapper.queryRidsByUid(id);
        //根据角色id查询菜单
        if (null!=rids&&rids.size()>0){
            List<Integer> mids = this.roleMapper.queryMidsByRids(rids);
            if (null!=mids&&mids.size()>0){
                QueryWrapper<Menu> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("available", Constant.AVAILABLE_TRUE);
                queryWrapper.eq("type",Constant.MENU_TYPE_PERMISSION);
                queryWrapper.in("id",mids);
                List<Menu> menus = this.menuMapper.selectList(queryWrapper);
                List<String> permissions = new ArrayList<>();
                for (Menu menu : menus) {
                    permissions.add(menu.getTypecode());
                }
                return permissions;
            }else {
                return null;
            }
        }else {
            return null;
        }
    }


}
