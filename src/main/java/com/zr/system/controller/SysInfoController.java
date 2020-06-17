package com.zr.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zr.blog.domain.Article;
import com.zr.blog.service.ArticleService;
import com.zr.repo.domain.Project;
import com.zr.repo.domain.Repository;
import com.zr.repo.service.ProjectService;
import com.zr.repo.service.RepositoryService;
import com.zr.system.common.ActiveUser;
import com.zr.system.common.Constant;
import com.zr.system.common.DataGridView;
import com.zr.system.domain.User;
import com.zr.system.service.LangugeService;
import com.zr.system.service.UserService;
import com.zr.system.service.UseraddrService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 张忍
 * @Date: 2020-04-09 12:51
 */
@RestController
@RequestMapping("api/sysInfo")
public class SysInfoController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private ArticleService articleService;
    @Autowired
    private UserService userService;
    @Autowired
    private LangugeService langugeService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private UseraddrService useraddrService;


    //加载项目贡献量
    @RequestMapping("loadProjectSubmit")
    public Object loadProjectSubmit(Integer id,String year){
        return this.projectService.queryProjectSubmit(id,year);
    }
    //加载博客贡献量
    @RequestMapping("loadBlogSubmit")
    public Object loadBlogSubmit(Integer id,String year){
        return this.articleService.queryArticleSubmit(id,year);
    }
    //加载系统内项目语言信息
    @RequestMapping("loadProjectLanguage")
    public Object loadProjectLanguage(){
        return this.langugeService.queryProjectLanguage();
    }
    //根据用户id加载推荐文章
    @RequestMapping("loadRecommendBlog")
    public Object loadRecommendBlog(Integer uid){
        return this.articleService.queryRecommendArticle(uid);
    }

    /**
     * 加载用户地址
     * @return
     */
    @RequestMapping("loadUserAddr")
    public Object loadUserAddr(){
        return this.useraddrService.loadUserAddr();
    }

    /**
     * 加载系统数据信息
     * @return
     */
    @RequestMapping("loadSysInfo")
    public Object loadSysInfo() {
        //获得当前登录用户
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        Integer id = activeUser.getUser().getId();

        int languageCount = this.langugeService.count();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("type", Constant.USER_TYPE_NORMAL,Constant.USER_TYPE_OFFICIAL);
        int userCount = this.userService.count(queryWrapper);

        QueryWrapper<Article> articleQueryWrapper = new QueryWrapper<>();
        articleQueryWrapper.in("available",Constant.AVAILABLE_TRUE);
        int articleCount = this.articleService.count();

        QueryWrapper<Project> proQueryWrapper = new QueryWrapper();
        proQueryWrapper.eq("available",Constant.AVAILABLE_TRUE);
        int projectCount = this.projectService.count(proQueryWrapper);

        Map<String,Integer> map= new HashMap<>();
        map.put("userCount",userCount);
        map.put("articleCount",articleCount);
        map.put("projectCount",projectCount);
        map.put("languageCount",languageCount);

        articleQueryWrapper.eq("author",id);
        int myArticleCount = this.articleService.count(articleQueryWrapper);

        proQueryWrapper.eq("master",id);
        int myProjectCount = this.projectService.count(proQueryWrapper);

        int myFollowCount = this.userService.queryFollowCountByUid(id);

        QueryWrapper<Repository> repositoryQueryWrapper = new QueryWrapper<>();
        repositoryQueryWrapper.eq("master",id);
        repositoryQueryWrapper.eq("available",Constant.AVAILABLE_TRUE);
        int myRepoCount = this.repositoryService.count(repositoryQueryWrapper);

        map.put("myArticleCount",myArticleCount);
        map.put("myProjectCount",myProjectCount);
        map.put("myFollowCount",myFollowCount);
        map.put("myRepoCount",myRepoCount);

        return new DataGridView(map);
    }
}
