package com.zr.repo.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.file.FileReader;
import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zr.blog.domain.Article;
import com.zr.repo.common.FileTreeNode;
import com.zr.repo.domain.File;
import com.zr.repo.domain.Repository;
import com.zr.repo.mapper.FileMapper;
import com.zr.repo.service.FileService;
import com.zr.repo.service.RepositoryService;
import com.zr.repo.utils.HttpUtil;
import com.zr.repo.utils.UploadUtil;
import com.zr.repo.vo.ProjectVo;
import com.zr.system.common.*;
import com.zr.system.common.file.UploadProperties;
import com.zr.system.common.file.UploadService;
import com.zr.system.domain.Languge;
import com.zr.system.domain.User;
import com.zr.system.service.LangugeService;
import com.zr.system.service.UserService;
import com.zr.system.utils.AppUtil;
import com.zr.system.utils.MD5Util;
import com.zr.system.utils.StringUtil;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zr.repo.mapper.ProjectMapper;
import com.zr.repo.domain.Project;
import com.zr.repo.service.ProjectService;
import org.springframework.transaction.annotation.Transactional;

/**
* @Author: 张忍
* @Date: 2020-03-20 14:23
*/
@Service
@EnableConfigurationProperties(TempProperties.class)
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService{


    @Autowired
    private FileMapper fileMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private LangugeService langugeService;
    @Autowired
    private UploadService uploadService;
    @Autowired
    private FileService fileService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private UploadProperties uploadProperties;
    @Autowired
    private TempProperties tempProperties;

    /**
     * 根据仓库ID加载项目
     * @param projectVo
     * @return
     */
    @Override
    public DataGridView loadProjectByRepoId(ProjectVo projectVo) {
        IPage<Project> page = new Page<>(projectVo.getPage(), projectVo.getLimit());
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("available",Constant.AVAILABLE_TRUE,Constant.AVAILABLE_FALSE);
        queryWrapper.eq("repo",projectVo.getRepoId());
        queryWrapper.orderByDesc("modifytime");
        this.baseMapper.selectPage(page,queryWrapper);
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    /**
     * 根据项目id加载历史版本项目
     * @param projectVo
     * @return
     */
    @Override
    public DataGridView loadArchiveProjectByPid(ProjectVo projectVo) {
        IPage<Project> page = new Page<>(projectVo.getPage(), projectVo.getLimit());
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("available",Constant.AVAILABLE_ARCHIVE);
        queryWrapper.eq("faid",projectVo.getId());
        queryWrapper.orderByDesc("modifytime");
        this.baseMapper.selectPage(page,queryWrapper);
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    /**
     * 根据id删除归档文件
     * @param id
     */
    @CacheEvict(cacheNames = "com.zr.repo.service.impl.ProjectServiceImpl" ,key = "#id")
    @Override
    public void removeArchiveProject(Integer id) {

        Project project = this.getById(id);
        //删除文件实体
        this.uploadService.deleteFile(project.getProjectpath());
        //删除文件信息
        this.baseMapper.deleteById(id);

        Repository repository = new Repository();
        repository.setId(project.getRepo());
        repository.setModifytime(new Date());
        this.repositoryService.updateById(repository);
    }

    /**
     * 加载项目提交数量信息
     * @param id
     * @param year
     * @return
     */
    @Override
    public DataGridView queryProjectSubmit(Integer id, String year) {
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("master",id);
        queryWrapper.eq("available",Constant.AVAILABLE_TRUE);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<String> resList = new ArrayList<>();
        try {
            Date startDate = simpleDateFormat.parse(year + "-01-01");
            Date endDate = simpleDateFormat.parse(year + "-12-31");
            queryWrapper.le("createtime",endDate);
            queryWrapper.ge("createtime",startDate);
            queryWrapper.select("createtime");
            List<Project> projects = this.projectMapper.selectList(queryWrapper);
            for (Project project : projects) {
                Date createtime = project.getCreatetime();
                String format = simpleDateFormat.format(createtime);
                resList.add(format);
            }
            //统计List数据
            Map<String, Integer> stringIntegerMap = StringUtil.frequencyOfListElements(resList);
            Object[][] array = MapUtil.toObjectArray(stringIntegerMap);
            return new DataGridView(array);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据id查询项目主人id
     * @param id
     * @return
     */
    @Override
    public DataGridView queryProjectMaster(Integer id) {
        ProjectService projectService = AppUtil.getContext().getBean(ProjectService.class);
        Project byId = projectService.getById(id);
        return new DataGridView(byId.getMaster());
    }

    @Override
    public List<Project> queryProjectByUid(Integer id) {
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        //查询项目
        queryWrapper.eq("master",id);
        queryWrapper.eq("available",Constant.AVAILABLE_TRUE);
        queryWrapper.orderByDesc("modifytime");
        List<Project> projects = this.baseMapper.selectList(queryWrapper);
        return projects;
    }



    /**
     * 修改项目
     * @param project
     * @return
     */
    @CachePut(cacheNames = "com.zr.repo.service.impl.ProjectServiceImpl",key = "#result.id")
    @Override
    public Project updateProject(Project project) {
        this.projectMapper.updateById(project);
        Project select = this.projectMapper.selectById(project.getId());

        Repository repository = new Repository();
        repository.setId(select.getRepo());
        repository.setModifytime(new Date());
        this.repositoryService.updateById(repository);
        return select;
    }

    /**
     * 新建项目
     * @param project
     * @return
     * @throws IOException
     */
    @CachePut(cacheNames = "com.zr.repo.service.impl.ProjectServiceImpl",key = "#result.id")
    @Override
    public Project saveProject(Project project) throws IOException {
        this.projectMapper.insert(project);
        //修改仓库信息
        this.repositoryService.incrementProjectNum(project.getRepo());
        Repository repository = new Repository();
        repository.setId(project.getRepo());
        repository.setModifytime(new Date());
        this.repositoryService.updateById(repository);
        //更新语言数量信息
        this.langugeService.incrementProjectNum(project.getLanguge());

        //更新实时数据
        QueryWrapper<Project> proQueryWrapper = new QueryWrapper();
        proQueryWrapper.eq("available",Constant.AVAILABLE_TRUE);
        int count = this.count(proQueryWrapper);
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        User user = activeUser.getUser();
        proQueryWrapper.eq("master",user.getId());
        int myProjectCount = this.count(proQueryWrapper);
        WebSocketServer.sendInfo("{'projectCount':"+count+",'myProjectCount':"+myProjectCount+"}");
        return project;
    }


    /**
     * 根据项目id加载项目详细
     * @param projectVo
     * @return
     */
    @Override
    public DataGridView queryProjectInfo(ProjectVo projectVo) {
        Map<String,Object> map = new HashMap<>();
        Project project = this.projectMapper.selectById(projectVo.getId());
        Languge languge = this.langugeService.getById(project.getLanguge());
        Integer starNum = this.projectMapper.queryStarNumByPid(projectVo.getId());
        map.put("language",languge.getName());
        map.put("starNum",starNum);
        //获得项目内文件
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("projectid",projectVo.getId());
        //得到项目全部文件
        List<File> files = this.fileMapper.selectList(queryWrapper);


        if (null!=files&&files.size()>0){
            for (File file : files) {
                if (file.getType().equals(Constant.FILE_TYPE_README)){
                    File readme = this.fileMapper.selectById(file.getId());
                    //根据网络url拿到文件
                    java.io.File fileContent = HttpUtil.getNetUrl(this.uploadProperties.getBaseUrl()+readme.getFilepath());
                    //读取文件内容
                    FileReader fileReader = new FileReader(fileContent);
                    String content = fileReader.readString();
                    map.put("readme",content);
                    fileContent.delete();
                }
            }
            map.put("files",files);
            return new DataGridView(map);
        }else {//空项目
            map.put("files","");
            return new DataGridView(map);
        }
    }

    /**
     * 根据项目id加载项目详细文件夹
     * @param projectVo
     * @return
     */
    @Override
    public DataGridView queryProjectInfoFolder(ProjectVo projectVo) {
        //获得项目内文件
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("projectid",projectVo.getId());
        queryWrapper.eq("type", Constant.FILE_TYPE_FOLDER);
        //得到项目全部文件
        List<File> files = this.fileMapper.selectList(queryWrapper);
        return new DataGridView(Long.valueOf(files.size()),files);
    }

    /**
     * 根据项目ID打包下载项目
     * @param id
     * @return
     */
    @Override
    public ResultObj downloadProject(Integer id, HttpServletResponse response){
        try {
            //项目临时文件夹路径
            String tempDirectory = this.tempProperties.getPath()+"DownloadProject"+id+'-'+ MD5Util.createUUID();

            //获得项目内文件
            QueryWrapper<File> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("projectid",id);
            List<File> files = this.fileMapper.selectList(queryWrapper);
            //将FileList渲染为FileTreeNodeList
            List<FileTreeNode> treeNodes = new ArrayList<>();
            for (File file : files) {
                treeNodes.add(new FileTreeNode(file.getId(),file.getName(),file.getType(),file.getParentid(),this.uploadProperties.getBaseUrl()+file.getFilepath()));
            }
            //将扁平化FileTreeNodeList渲染为树形FileTreeNodeList
            List<FileTreeNode> fileTree = FileTreeNode.FileTreeNodeBuilder.build(treeNodes, Constant.FOLDER_ROOT);
            Project project = this.projectMapper.selectById(id);
            FileTreeNode root = new FileTreeNode();
            root.setName(project.getName());
            root.setType(Constant.FILE_TYPE_FOLDER);
            root.setChild(fileTree);
            root.setId(project.getId());
            //写入临时文件夹
            FileTreeNode.FileTreeNodeBuilder.FindAllPaths(root,tempDirectory+"/");
            //打包压缩
            java.io.File prozip = new java.io.File(tempDirectory+"/"+project.getName()+".zip");
            FileOutputStream fos1 = new FileOutputStream(prozip);
            com.zr.repo.utils.ZipUtil.toZip(tempDirectory+"/"+project.getName(),fos1,true);
            //写到前台
            // 取得文件名。
            String filename = prozip.getName();
            // 取得文件的后缀名。
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();
            // 设置response的Header
            //response.reset();
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" +new String( filename.getBytes("UTF-8"), "ISO8859-1" ));
            response.setHeader("Content-Length","" + prozip.length());

            // 以流的形式下载文件。
            byte[] bytes = FileUtil.readBytes(prozip);
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes);
            outputStream.flush();
            outputStream.close();
            //删除临时文件
            UploadUtil.delete(tempDirectory);
            return ResultObj.DOWNLOAD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DOWNLOAD_ERROR;

        }
    }

    /**
     * 根据项目ID存档项目
     * @param projectVo
     */
    @Override
    public void archivedProject(ProjectVo projectVo) {
        try {
            Integer id = projectVo.getId();


            //项目临时文件夹路径
            String tempDirectory = this.tempProperties.getPath()+"ArchiveProject"+id+'-'+ MD5Util.createUUID();

            //获得项目内文件
            QueryWrapper<File> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("projectid",id);
            List<File> files = this.fileMapper.selectList(queryWrapper);
            //将FileList渲染为FileTreeNodeList
            List<FileTreeNode> treeNodes = new ArrayList<>();
            for (File file : files) {
                treeNodes.add(new FileTreeNode(file.getId(),file.getName(),file.getType(),file.getParentid(),this.uploadProperties.getBaseUrl()+file.getFilepath()));
            }
            //将扁平化FileTreeNodeList渲染为树形FileTreeNodeList
            List<FileTreeNode> fileTree = FileTreeNode.FileTreeNodeBuilder.build(treeNodes, Constant.FOLDER_ROOT);

            //查询项目信息
            Project project = this.baseMapper.selectById(id);
            FileTreeNode root = new FileTreeNode();
            root.setName(project.getName());
            root.setType(Constant.FILE_TYPE_FOLDER);
            root.setChild(fileTree);
            root.setId(project.getId());

            //写入临时文件夹
            FileTreeNode.FileTreeNodeBuilder.FindAllPaths(root,tempDirectory+"/");

            //打包压缩
            java.io.File prozip = new java.io.File(tempDirectory+"/"+project.getName()+".zip");
            FileOutputStream fos1 = new FileOutputStream(prozip);
            com.zr.repo.utils.ZipUtil.toZip(tempDirectory+"/"+project.getName(),fos1,true);

            //存储归档文件
            String url = this.uploadService.uploadFile(prozip);
            Project archiveProject = new Project();
            archiveProject.setFaid(id);
            archiveProject.setProjectpath(url);
            archiveProject.setCreatetime(new Date());
            archiveProject.setModifytime(archiveProject.getCreatetime());
            archiveProject.setAvailable(Constant.AVAILABLE_ARCHIVE);
            archiveProject.setVersion(projectVo.getVersion());
            archiveProject.setLanguge(project.getLanguge());
            archiveProject.setRemark(projectVo.getRemark());
            archiveProject.setName(project.getName());
            archiveProject.setRepo(project.getRepo());
            //解决调用本类项目注解不生效问题
            ProjectService projectService = AppUtil.getContext().getBean(ProjectService.class);
            projectService.saveProject(archiveProject);

            //更新项目信息
            project.setModifytime(archiveProject.getCreatetime());
            project.setVersion(archiveProject.getVersion());
            projectService.updateProject(project);
            //更新仓库信息
            Repository repository = new Repository();
            repository.setId(project.getRepo());
            repository.setModifytime(new Date());
            this.repositoryService.updateById(repository);

            //删除临时文件
            UploadUtil.delete(tempDirectory);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 加载全部项目
     * @param projectVo
     * @return
     */
    @Override
    public DataGridView loadAllProject(ProjectVo projectVo) {
        IPage<Project> page = new Page<>(projectVo.getPage(), projectVo.getLimit());
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();

        queryWrapper.like(StringUtils.isNotBlank(projectVo.getName()),"name",projectVo.getName());

        if (StringUtils.isNotBlank(projectVo.getMasterName())){
            List<Integer> uids = new ArrayList<>();
            QueryWrapper<User> queryWrapper1 = new QueryWrapper<>();
            queryWrapper1.like("name",projectVo.getMasterName());
            List<User> users = this.userService.list(queryWrapper1);
            if (null!=users&&users.size()>0){
                for (User user : users) {
                    uids.add(user.getId());
                }
                queryWrapper.in("master",uids);
            }else {
                queryWrapper.in("master",-1);
            }
        }
        if (null!=projectVo.getAvailable()){
            queryWrapper.eq("available",projectVo.getAvailable());
        }else {
            queryWrapper.in("available",Constant.AVAILABLE_TRUE,Constant.AVAILABLE_FALSE);
        }
        queryWrapper.orderByDesc("modifytime");


        this.baseMapper.selectPage(page,queryWrapper);

        List<Project> projectList = page.getRecords();
        for (Project project : projectList) {
            //添加仓库所属人信息
            User user = this.userService.getById(project.getMaster());
            project.setMasterName(user.getName());
            //添加语言标签信息
            Languge languge = this.langugeService.getById(project.getLanguge());
            project.setTag(languge.getName());
        }
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    /**
     * 标星项目
     * @param uid
     * @param pid
     */
    @Override
    public void starProject(Integer uid, Integer pid) {
        this.projectMapper.insertStar(uid,pid);
    }

    /**
     * 取消标星项目
     * @param uid
     * @param pid
     */
    @Override
    public void unStarProject(Integer uid, Integer pid) {
        this.projectMapper.deleteStar(uid,pid);
    }

    /**
     * 查询标星状态
     * @param uid
     * @param pid
     * @return
     */
    @Override
    public ResultObj queryStar(Integer uid, Integer pid) {
        Integer count = this.projectMapper.queryStar(uid, pid);
        return count==1?ResultObj.STAR_TRUE:ResultObj.STAR_FALSE;
    }

    /**
     * 查询标星项目
     * @param projectVo
     * @return
     */
    @Override
    public DataGridView loadStarProject(ProjectVo projectVo) {

        IPage<Project> page = new Page<>(projectVo.getPage(),projectVo.getLimit());
        Integer uid = projectVo.getUser();
        List<Integer> pids = this.projectMapper.queryPidsByUid(uid);

        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        if (null!=pids&&pids.size()>0){
            queryWrapper.in("id",pids);
            queryWrapper.eq("available",Constant.AVAILABLE_TRUE);
            queryWrapper.like(StringUtils.isNotBlank(projectVo.getName()),"name",projectVo.getName());
            this.projectMapper.selectPage(page,queryWrapper);
        }
        return new DataGridView(page.getTotal(),page.getRecords());

    }


    /**
     * 根据搜索关键字加载项目
     * @param projectVo
     * @return
     */
    @Override
    public DataGridView loadProjectBySearchkey(ProjectVo projectVo) {
        IPage<Project> page = new Page<>(projectVo.getPage(), projectVo.getLimit());
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(projectVo.getSearchkey())){
            queryWrapper.and(qw->qw.like("name",projectVo.getSearchkey()).or().like("remark",projectVo.getSearchkey()));
        }

//        queryWrapper.like(StringUtils.isNotBlank(projectVo.getSearchkey()),"name",projectVo.getSearchkey())
//                .or().like(StringUtils.isNotBlank(projectVo.getSearchkey()),"remark",projectVo.getSearchkey());
        queryWrapper.eq("available",Constant.AVAILABLE_TRUE);
        queryWrapper.orderByDesc("modifytime");
        this.baseMapper.selectPage(page,queryWrapper);

        List<Project> projectList = page.getRecords();

        for (Project project : projectList) {
            //添加仓库所属人信息
            User user = this.userService.getById(project.getMaster());
            project.setMasterName(user.getName());
            //添加语言标签信息
            Languge languge = this.langugeService.getById(project.getLanguge());
            project.setTag(languge.getName());
        }
        return new DataGridView(page.getTotal(),page.getRecords());
    }

    /**
     * 加载我的项目
     * @param projectVo
     * @return
     */
    @Override
    public DataGridView loadMyProject(ProjectVo projectVo) {
        IPage<Project> page = new Page<>(projectVo.getPage(), projectVo.getLimit());
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        //查询项目
        queryWrapper.eq("master",projectVo.getUser());
        queryWrapper.like(StringUtils.isNotBlank(projectVo.getName()),"name",projectVo.getName());
        queryWrapper.like(StringUtils.isNotBlank(projectVo.getRemark()),"remark",projectVo.getRemark());
        queryWrapper.eq(projectVo.getLanguage()!=null,"languge",projectVo.getLanguage());
        if(projectVo.getAvailable()!=null){
            queryWrapper.eq("available",projectVo.getAvailable());
        }else {
            queryWrapper.in("available",Constant.AVAILABLE_TRUE,Constant.AVAILABLE_FALSE);
        }
        queryWrapper.orderByDesc("modifytime");
        this.baseMapper.selectPage(page,queryWrapper);

        List<Project> projectList = page.getRecords();

        for (Project project : projectList) {
            //添加所属人信息
            User user = this.userService.getById(projectVo.getUser());
            project.setMasterName(user.getName());
            //添加语言标签信息
            Languge languge = this.langugeService.getById(project.getLanguge());
            project.setTag(languge.getName());
            //添加仓库信息
            Repository repository = this.repositoryService.getById(project.getRepo());
            project.setRepoName(repository.getName());
        }
        return new DataGridView(page.getTotal(),page.getRecords());
    }



    /**
     * 根据项目ID删除项目
     * @param id
     * @return
     */
    @CacheEvict(cacheNames = "com.zr.repo.service.impl.ProjectServiceImpl",key = "#id")
    @SneakyThrows
    @Override
    public boolean removeById(Serializable id) {
        ProjectService projectService = AppUtil.getContext().getBean(ProjectService.class);
        Project byId = projectService.getById(id);
        //更新语言数量信息
        this.langugeService.decrementProjectNum(byId.getLanguge());
        //删除用户项目标星信息
        this.projectMapper.deleteStarByPid(id);
        //更新项目仓库数量与修改时间
        this.repositoryService.decrementProjectNum(byId.getRepo());
        Repository repository = new Repository();
        repository.setId(byId.getRepo());
        repository.setModifytime(new Date());
        this.repositoryService.updateById(repository);
        //删除历史版本实体
        QueryWrapper<Project> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("available",Constant.AVAILABLE_ARCHIVE);
        queryWrapper.eq("faid",id);
        List<Project> projects = this.baseMapper.selectList(queryWrapper);
        if (projects.size()>0&&null!=projects){
            for (Project project : projects) {
                this.uploadService.deleteFile(project.getProjectpath());
            }
        }
        //删除历史版本信息
        this.baseMapper.delete(queryWrapper);
        QueryWrapper<File> fileQueryWrapper = new QueryWrapper<>();
        fileQueryWrapper.eq("projectid",id);
        fileQueryWrapper.in("type",Constant.FILE_TYPE_NORMAL,Constant.FILE_TYPE_README);
        //删除项目内文件实体
        List<File> files = this.fileService.list(fileQueryWrapper);
        if (files.size()>0&&null!=files){
            for (File file : files) {
                this.uploadService.deleteFile(file.getFilepath());
            }
        }
        //删除项目内文件信息
        fileQueryWrapper.in("type",Constant.FILE_TYPE_FOLDER,Constant.FILE_TYPE_NORMAL,Constant.FILE_TYPE_README);
        this.fileMapper.delete(fileQueryWrapper);
        boolean res = super.removeById(id);


        QueryWrapper<Project> proQueryWrapper = new QueryWrapper();
        proQueryWrapper.eq("available",Constant.AVAILABLE_TRUE);
        int count = this.count(proQueryWrapper);


        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        User user = activeUser.getUser();
        proQueryWrapper.eq("master",user.getId());
        int myProjectCount = this.count(proQueryWrapper);
        WebSocketServer.sendInfo("{'projectCount':"+count+",'myProjectCount':"+myProjectCount+"}");
        return res;
    }

    /**
     * 根据文件id加载文件
     * @param fileid
     * @return
     */
    @Override
    public DataGridView queryFileInfo(Integer fileid) {
        Map<String,Object> map = new HashMap<>();
        //获得项目内文件
        File file = this.fileMapper.selectById(fileid);
        if(file.getType().equals(Constant.FILE_TYPE_IMG)){
            map.put("type",2);
            map.put("path",this.uploadProperties.getBaseUrl()+file.getFilepath());
            return new DataGridView(map);
        }else if(file.getType().equals(Constant.FILE_TYPE_AUDIO)){
            map.put("type",3);
            map.put("path",this.uploadProperties.getBaseUrl()+file.getFilepath());
            return new DataGridView(map);
        }else if(file.getType().equals(Constant.FILE_TYPE_VIDEO)){
            map.put("type",4);
            map.put("path",this.uploadProperties.getBaseUrl()+file.getFilepath());
            return new DataGridView(map);
        }else {
            if(file.getType().equals(Constant.FILE_TYPE_README)){
                map.put("type",0);
            }else {
                map.put("type",1);
            }
            java.io.File contentfile = HttpUtil.getNetUrl(this.uploadProperties.getBaseUrl()+file.getFilepath());

            FileReader fileReader = new FileReader(contentfile);
            String content = fileReader.readString();
            contentfile.delete();
            map.put("content",content);
            return new DataGridView(map);
        }


    }
    @Cacheable(cacheNames = "com.zr.repo.service.impl.ProjectServiceImpl",key = "#id")
    @Override
    public Project getById(Serializable id) {
        return super.getById(id);
    }
}
