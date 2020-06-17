package com.zr;

import com.zr.system.domain.Menu;
import com.zr.system.service.MenuService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MenuUtils {
    @Autowired
    private MenuService menuService;


    @Test
    void initMenu() {
//        menuService.save(new Menu(1,0,"topmenu","system","系统管理","fa fa-cog","",0,1,1));
//        menuService.save(new Menu(2,0,"topmenu","control","控制台","fa fa-bars","",0,2,1));
//        menuService.save(new Menu(3,0,"topmenu","blog","博客","fa fa-bars","",0,2,1));
//
//        menuService.save(new Menu(4,1,"leftmenu","system","系统管理","fa fa-cog","",0,1,1));
//        menuService.save(new Menu(5,1,"leftmenu","system","其他管理","fa fa-cog","",0,1,1));
//
//        menuService.save(new Menu(6,4,"leftmenu","system","用户管理","fa fa-cog","",0,1,1));
//        menuService.save(new Menu(7,4,"leftmenu","system","角色管理","fa fa-cog","",0,1,1));
//        menuService.save(new Menu(8,4,"leftmenu","system","权限管理","fa fa-cog","",0,1,1));
//        menuService.save(new Menu(9,1,"leftmenu","system","仓库管理","fa fa-cog","",0,1,1));
//        menuService.save(new Menu(10,1,"leftmenu","system","菜单管理","fa fa-cog","",0,1,1));
//
//
//        menuService.save(new Menu(11,5,"leftmenu","system","系统公告","fa fa-cog","",0,1,1));
//        menuService.save(new Menu(12,5,"leftmenu","system","登录日志","fa fa-cog","",0,1,1));
//        menuService.save(new Menu(13,5,"leftmenu","system","缓存管理","fa fa-cog","",0,1,1));
//        menuService.save(new Menu(14,5,"leftmenu","system","数据源监控","fa fa-cog","",0,1,1));
//        menuService.save(new Menu(15,5,"leftmenu","system","图标管理","fa fa-cog","",0,1,1));
//
//
//        menuService.save(new Menu(16,2,"leftmenu","control","我的仓库","fa fa-cog","",0,1,1));
//        menuService.save(new Menu(17,2,"leftmenu","control","上传项目","fa fa-cog","",0,1,1));
//        menuService.save(new Menu(18,2,"leftmenu","control","工作台","fa fa-cog","",0,1,1));
//        menuService.save(new Menu(19,2,"leftmenu","control","我的消息","fa fa-cog","",0,1,1));
//
//        menuService.save(new Menu(20,3,"leftmenu","blog","我的博客","fa fa-cog","",0,1,1));
//        menuService.save(new Menu(21,3,"leftmenu","blog","写博客","fa fa-cog","",0,1,1));
//        System.out.println("初始化成功");
    }


    //权限
    @Test
    void initPermission(){
        menuService.save(new Menu(22,1,"permission","system:query","系统管理查看",23,1));
    }

}
