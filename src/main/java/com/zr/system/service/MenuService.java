package com.zr.system.service;

import com.zr.system.common.DataGridView;
import com.zr.system.domain.Menu;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zr.system.vo.MenuVo;

import java.util.List;

public interface MenuService extends IService<Menu>{


    List<Menu> queryAllMenuForList();

    List<Menu> queryMenuForListByUserId(Integer id);

    DataGridView queryAllMenu(MenuVo menuVo);

    Menu updateMenu(Menu menu);

    Menu saveMenu(Menu menu);

    Integer queryMenuMaxOrdernum();

    Integer queryMenuChildrenCountById(Integer id);

    List<String> queryPermissioncodesByUid(Integer id);
}
