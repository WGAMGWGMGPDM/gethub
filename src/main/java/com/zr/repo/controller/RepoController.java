package com.zr.repo.controller;

import com.zr.repo.domain.Repository;
import com.zr.repo.service.RepositoryService;
import com.zr.repo.vo.RepoVo;
import com.zr.system.common.ActiveUser;
import com.zr.system.common.Constant;
import com.zr.system.common.ResultObj;
import com.zr.system.domain.Role;
import com.zr.system.domain.User;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;

/**
 * @Author: 张忍
 * @Date: 2020-03-19 23:22
 */
@RestController
@RequestMapping("api/repo")
public class RepoController {

    @Autowired
    private RepositoryService repositoryService;

    /**
     * 加载仓库
     * @return
     */
    @RequestMapping("loadAllRepo")
    public Object loadAllRepo(RepoVo repoVo){
        return this.repositoryService.queryAllRepo(repoVo);
    }


    /**
     * 加载拥有的仓库
     * @return
     */
    @RequestMapping("queryOwnRepo")
    public Object queryOwnRepo(){
        //获得当前登录用户
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        User user = activeUser.getUser();
        return this.repositoryService.queryOwnRepo(user.getId());

    }

    /**
     * 添加仓库
     * @param repository
     * @return
     */
    @RequestMapping("addRepo")
    public ResultObj addRepo(Repository repository){
        try {
            //获得当前登录用户
            ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            User user = activeUser.getUser();

            repository.setCreatetime(new Date());
            repository.setModifytime(new Date());
            repository.setMaster(user.getId());
            repository.setPronum(Constant.DEFAULT_PROJECT_NUM);
            repository.setAvailable(Constant.AVAILABLE_TRUE);

            this.repositoryService.saveRepo(repository);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }

    /**
     * 修改仓库
     * @param repository
     * @return
     */
    @RequestMapping("updateRepo")
    public ResultObj updateRepo(Repository repository){
        try {
            repository.setModifytime(new Date());
            this.repositoryService.updateRepo(repository);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }

    /**
     * 删除
     */
    @RequestMapping("/deleteRepo")
    public ResultObj deleteRepo(Integer id){
        try {
            this.repositoryService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }
}
