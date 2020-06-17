package com.zr.repo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zr.repo.domain.Project;
import com.zr.repo.mapper.ProjectMapper;
import com.zr.repo.service.ProjectService;
import com.zr.repo.vo.RepoVo;
import com.zr.system.common.ActiveUser;
import com.zr.system.common.Constant;
import com.zr.system.common.DataGridView;
import com.zr.system.common.WebSocketServer;
import com.zr.system.domain.User;
import com.zr.system.service.UserService;
import com.zr.system.utils.AppUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zr.repo.domain.Repository;
import com.zr.repo.mapper.RepositoryMapper;
import com.zr.repo.service.RepositoryService;

/**
* @Author: 张忍
* @Date: 2020-03-19 23:19
*/
@Service
public class RepositoryServiceImpl extends ServiceImpl<RepositoryMapper, Repository> implements RepositoryService{

    @Autowired
    private RepositoryMapper repositoryMapper;

    /**
     * 加载拥有的所有仓库
     * @param id
     * @return
     */
    @Override
    public DataGridView queryOwnRepo(Integer id) {
        QueryWrapper<Repository> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("master",id);
        queryWrapper.eq("available", Constant.AVAILABLE_TRUE);
        queryWrapper.orderByDesc("modifytime");
        List<Repository> repositories = this.repositoryMapper.selectList(queryWrapper);
        return new DataGridView(repositories);
    }


    /**
     * 添加仓库
     * @param repository
     * @return
     */
    @CachePut(cacheNames = "com.zr.repo.service.impl.RepositoryServiceImpl",key = "#result.id")
    @Override
    public Repository saveRepo(Repository repository) throws IOException {
        this.repositoryMapper.insert(repository);

        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        User user = activeUser.getUser();
        QueryWrapper<Repository> repositoryQueryWrapper = new QueryWrapper<>();
        repositoryQueryWrapper.eq("master",user.getId());
        repositoryQueryWrapper.eq("available",Constant.AVAILABLE_TRUE);
        int myRepoCount = this.count(repositoryQueryWrapper);
        WebSocketServer.sendInfo("{'myRepoCount':"+myRepoCount+"}");
        return repository;
    }

    /**
     * 修改仓库
     * @param repository
     * @return
     */
    @CachePut(cacheNames = "com.zr.repo.service.impl.RepositoryServiceImpl",key = "#result.id")
    @Override
    public Repository updateRepo(Repository repository) {
        if (repository.getAvailable()!=null&&repository.getAvailable().equals(Constant.AVAILABLE_FALSE)){
            //仓库项目设不可用
            QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("repo",repository.getId());
            ProjectService projectService = AppUtil.getContext().getBean(ProjectService.class);
            List<Project> projectList = projectService.list(queryWrapper);
            for (Project project : projectList) {
                project.setAvailable(Constant.AVAILABLE_FALSE);
                projectService.updateProject(project);
            }
        }

        if (repository.getType()!=null&&repository.getType().equals(Constant.AVAILABLE_FALSE)){
            //仓库项目设不可用
            QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("repo",repository.getId());
            ProjectService projectService = AppUtil.getContext().getBean(ProjectService.class);
            List<Project> projectList = projectService.list(queryWrapper);
            for (Project project : projectList) {
                project.setAvailable(Constant.AVAILABLE_FALSE);
                projectService.updateProject(project);
            }
        }

        this.repositoryMapper.updateById(repository);
        Repository select = this.repositoryMapper.selectById(repository.getId());
        return select;
    }

    /**
     * 删除仓库
     * @param id
     * @return
     */
    @CacheEvict(cacheNames = "com.zr.repo.service.impl.RepositoryServiceImpl",key = "#id")
    @SneakyThrows
    @Override
    public boolean removeById(Serializable id) {
        //将仓库项目也删除
        ProjectService projectService = AppUtil.getContext().getBean(ProjectService.class);
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("repo",id);
        List<Project> projects = projectService.list(queryWrapper);
        for (Project project : projects) {
            projectService.removeById(project.getId());
        }

        boolean res = super.removeById(id);

        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        User user = activeUser.getUser();
        QueryWrapper<Repository> repositoryQueryWrapper = new QueryWrapper<>();
        repositoryQueryWrapper.eq("master",user.getId());
        repositoryQueryWrapper.eq("available",Constant.AVAILABLE_TRUE);
        int myRepoCount = this.count(repositoryQueryWrapper);
        WebSocketServer.sendInfo("{'myRepoCount':"+myRepoCount+"}");
        return res;
    }

    @Cacheable(cacheNames = "com.zr.repo.service.impl.RepositoryServiceImpl",key = "#id")
    @Override
    public Repository getById(Serializable id) {
        return super.getById(id);
    }

    @Override
    public Boolean incrementProjectNum(Integer reopid) {
        this.repositoryMapper.incrementProjectNum(reopid);
        return null;
    }

    @Override
    public Boolean decrementProjectNum(Integer reopid) {
        this.repositoryMapper.decrementProjectNum(reopid);
        return null;
    }

    /**
     * 查询所有仓库
     * @param repoVo
     * @return
     */
    @Override
    public DataGridView queryAllRepo(RepoVo repoVo) {
        IPage<Repository> page = new Page<>(repoVo.getPage(),repoVo.getLimit());
        QueryWrapper<Repository> queryWrapper = new QueryWrapper<>();

        queryWrapper.like(StringUtils.isNotBlank(repoVo.getRepoName()),"name",repoVo.getRepoName());

        if (StringUtils.isNotBlank(repoVo.getMasterName())){
            List<Integer> uids = new ArrayList<>();
            QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.like("name",repoVo.getMasterName());
            UserService userService = AppUtil.getContext().getBean(UserService.class);
            List<User> users = userService.list(queryWrapper1);
            if (null!=users&&users.size()>0){
                for (User user : users) {
                    uids.add(user.getId());
                }
                queryWrapper.in("master",uids);
            }else {
                queryWrapper.in("master",-1);
            }
        }
        queryWrapper.eq(null!=repoVo.getAvailable(),"available",repoVo.getAvailable());
        queryWrapper.orderByDesc("modifytime");
        this.repositoryMapper.selectPage(page,queryWrapper);

        List<Repository> records = page.getRecords();
        UserService userService = AppUtil.getContext().getBean(UserService.class);
        for (Repository record : records) {
            User user = userService.getById(record.getMaster());
            record.setUserName(user.getName());
        }
        return new DataGridView(page.getTotal(),page.getRecords());
    }
}
