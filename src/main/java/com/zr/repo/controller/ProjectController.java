package com.zr.repo.controller;

import com.zr.blog.vo.ArticleVo;
import com.zr.repo.domain.Project;
import com.zr.repo.service.FileService;
import com.zr.repo.service.ProjectService;
import com.zr.repo.utils.HttpUtil;
import com.zr.repo.vo.ProjectVo;
import com.zr.system.common.ActiveUser;
import com.zr.system.common.Constant;
import com.zr.system.common.DataGridView;
import com.zr.system.common.ResultObj;
import com.zr.system.common.file.UploadProperties;
import com.zr.system.common.file.UploadService;
import com.zr.system.domain.User;
import com.zr.system.service.LangugeService;
import com.zr.system.vo.LanguageVo;
import com.zr.system.vo.UserVo;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * @Author: 张忍
 * @Date: 2020-03-20 14:24
 */
@RestController
@RequestMapping("api/project")
public class ProjectController {
    @Autowired
    private ProjectService projectService;
    @Autowired
    private LangugeService langugeService;
    @Autowired
    private UploadService uploadService;
    @Autowired
    private FileService fileService;

    /**
     * 根据仓库id加载项目
     * @param projectVo
     * @return
     */
    @RequestMapping("loadProjectByRepoId")
    public Object loadProjectByRepoId(ProjectVo projectVo){
        return this.projectService.loadProjectByRepoId(projectVo);
    }

    /**
     * 根据项目id加载项目历史版本
     * @param projectVo
     * @return
     */
    @RequestMapping("loadArchiveProjectByPid")
    public Object loadArchiveProjectByPid(ProjectVo projectVo){
        return this.projectService.loadArchiveProjectByPid(projectVo);
    }

    /**
     * 修改项目
     * @param project
     * @return
     */
    @RequestMapping("updateProject")
    public ResultObj updateProject(Project project){
        try {
            project.setModifytime(new Date());
            this.projectService.updateProject(project);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_ERROR;
        }
    }
    /**
     * 加载项目详细
     * @param projectVo
     * @return
     */
    @RequestMapping("loadProjectInfo")
    public Object loadProjectInfo(ProjectVo projectVo){
        return this.projectService.queryProjectInfo(projectVo);
    }

    /**
     * 根据项目id加载项目所属人id
     * @param id
     * @return
     */
    @GetMapping("loadProjectMaster")
    public Object loadProjectMaster(Integer id){
        return this.projectService.queryProjectMaster(id);
    }
    /**
     * 加载项目详细文件夹
     * @param projectVo
     * @return
     */
    @RequestMapping("loadProjectInfoFolder")
    public Object loadProjectInfoFolder(ProjectVo projectVo){
        return this.projectService.queryProjectInfoFolder(projectVo);
    }


    /**
     * 加载文件
     * @param fileid
     * @return
     */
    @RequestMapping("loadFileInfo")
    public Object loadProjectInfo(Integer fileid){
        return this.projectService.queryFileInfo(fileid);
    }


    /**
     * 添加项目
     * @param project
     * @return
     */
    @RequestMapping("addProject")
    public ResultObj addProject(Project project){
        try {
            //项目信息存数据库
            project.setCreatetime(new Date());
            project.setModifytime(new Date());
            project.setVersion(Constant.DEFAULT_VERSION);
            project.setAvailable(Constant.AVAILABLE_TRUE);
            //获得当前登录用户
            ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            User user = activeUser.getUser();
            project.setMaster(user.getId());
            this.projectService.saveProject(project);

            //新建一个项目实体存到fastfds里
            if(project.getOpt().equals(Constant.OPT_README)){//以readme.md初始化文件
                File file = HttpUtil.getNetUrl(Constant.DEFAULT_FILE_README);
                //存fastfds进去
                String url = uploadService.uploadFile(file);
                com.zr.repo.domain.File file1 = new com.zr.repo.domain.File();
                file1.setName("Readme.md");
                file1.setFilepath(url);
                file1.setModifytime(new Date());
                file1.setParentid(0);
                file1.setProjectid(project.getId());
                //设置类型为初始化类型
                file1.setType(Constant.FILE_TYPE_README);
                this.fileService.saveFile(file1);
                file.delete();
            }
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }

    /**
     * 删除项目
     */
    @RequestMapping("/deleteProject")
    public ResultObj deleteProject(Integer id){
        try {
            this.projectService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }
    /**
     * 删除归档项目
     */
    @RequestMapping("/deleteArchiveProject")
    public ResultObj deleteArchiveProject(Integer id){
        try {
            this.projectService.removeArchiveProject(id);
            return ResultObj.DELETE_SUCCESS;
        }catch (Exception e){
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }

    /**
     * 加载所有语言
     * @return
     */
    @RequestMapping("loadLanguge")
    public Object loadLanguge(){
        return this.langugeService.queryAllLanguageNoPage();
    }

    /**
     * 下载项目
     * @param project
     * @return
     * @throws IOException
     */
    @RequestMapping("downloadProject")
    public void downloadProject(Project project, HttpServletResponse response) throws IOException {
        Integer id = project.getId();
        this.projectService.downloadProject(id, response);
    }

    /**
     * 存档项目
     * @param projectVo
     * @return
     */
    @RequestMapping("archivedProject")
    public ResultObj archivedProject(ProjectVo projectVo){
        try {
            this.projectService.archivedProject(projectVo);
            //

            return ResultObj.ARCHIVE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ARCHIVE_ERROR;
        }
    }

    /**
     * 搜索项目
     * @param projectVo
     */
    @RequestMapping("searchProject")
    public DataGridView searchProject(ProjectVo projectVo){
        return this.projectService.loadProjectBySearchkey(projectVo);
    }

    /**
     * 加载我的项目
     * @param projectVo
     */
    @RequestMapping("loadMyProject")
    public DataGridView loadMyProject(ProjectVo projectVo){
        return this.projectService.loadMyProject(projectVo);
    }

    /**
     * 加载全部项目
     * @param projectVo
     */
    @RequestMapping("loadAllProject")
    public DataGridView loadAllProject(ProjectVo projectVo){
        return this.projectService.loadAllProject(projectVo);
    }


    /**
     * 标星项目
     * @return
     */
    @RequestMapping("starProject")
    public ResultObj starProject(Integer id){
        try {
            ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            Integer uid = activeUser.getUser().getId();
            this.projectService.starProject(uid,id);
            return ResultObj.STAR_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.STAR_ERROR;
        }
    }

    /**
     * 取消标星项目
     * @return
     */
    @RequestMapping("unStarProject")
    public ResultObj unStarProject(Integer id){
        try {
            ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
            Integer uid = activeUser.getUser().getId();
            this.projectService.unStarProject(uid,id);
            return ResultObj.UNSTAR_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UNSTAR_ERROR;
        }
    }

    /**
     * 查询项目是否已标星
     * @return
     */
    @RequestMapping("queryStar")
    public ResultObj queryStar(Integer id){
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        Integer uid = activeUser.getUser().getId();
        return this.projectService.queryStar(uid,id);
    }

    /**
     * 查询标星项目
     * @return
     */
    @GetMapping("loadStarProject")
    public Object loadStarProject(ProjectVo projectVo){
        return this.projectService.loadStarProject(projectVo);
    }




}
