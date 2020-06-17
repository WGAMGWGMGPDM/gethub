package com.zr.repo.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zr.repo.domain.File;
import com.zr.repo.domain.Project;
import com.zr.repo.service.FileService;
import com.zr.repo.service.ProjectService;
import com.zr.repo.service.RepositoryService;
import com.zr.repo.utils.UploadUtil;
import com.zr.repo.vo.FileVo;
import com.zr.system.common.Constant;
import com.zr.system.common.ResultObj;
import com.zr.system.common.file.UploadService;
import com.zr.system.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: 张忍
 * @Date: 2020-03-24 8:59
 */
@RestController
@RequestMapping("api/profile")
public class ProFileController {
    @Autowired
    private UploadService uploadService;
    @Autowired
    private FileService fileService;
    @Autowired
    private ProjectService projectService;

    /**
     * 上传单个文件到fastdfs
     */
    @RequestMapping("doUploadOneToDFS")
    public Map<String,Object> doUploadOneToDFS(MultipartFile mf) throws IOException {
        Map<String, Object> map = new HashMap<>();
        //MD5校验
        InputStream inputStream = mf.getInputStream();
        String md5code = DigestUtil.md5Hex(inputStream);
        //文件类型校验
        String contentType = mf.getContentType();
        //上传到fastdfs
        String filename = mf.getOriginalFilename();
        String filepath = uploadService.uploadMultipartFile(mf);
        map.put("path",filepath);
        map.put("filename",filename);
        map.put("md5code",md5code);
        map.put("contentType",contentType);
        return map;
    }
    /**
     * 上传单个文件到数据库
     */
    @RequestMapping("doUploadOneToDatabase")
    public ResultObj doUploadOneToDatabase(File file){
        try {
            //判断文件类型
            String contentType = file.getContentType();
            String substring = contentType.substring(0, contentType.indexOf("/"));
            if (substring.equals("image")){
                file.setType(Constant.FILE_TYPE_IMG);
            }else if (substring.equals("audio")){
                file.setType(Constant.FILE_TYPE_AUDIO);
            }else if (substring.equals("video")){
                file.setType(Constant.FILE_TYPE_VIDEO);
            }else {
                file.setType(Constant.FILE_TYPE_NORMAL);
            }

            if (file.getParentid()==null){
                file.setParentid(Constant.FOLDER_ROOT);
            }
            file.setModifytime(new Date());
            this.fileService.saveFile(file);

            //修改项目信息与仓库信息
            Project project = new Project();
            project.setId(file.getProjectid());
            project.setModifytime(new Date());
            this.projectService.updateProject(project);

            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }
    /**
     * 新建文件夹
     */
    @RequestMapping("doAddFolder")
    public ResultObj doAddFolder(File file){
        try {
            if (file.getParentid()==null){
                file.setParentid(Constant.FOLDER_ROOT);
            }
            file.setType(Constant.FILE_TYPE_FOLDER);
            file.setModifytime(new Date());
            this.fileService.saveFile(file);

            //修改项目信息与仓库信息
            Project project = new Project();
            project.setId(file.getProjectid());
            project.setModifytime(new Date());
            this.projectService.updateProject(project);

            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }
    /**
     * 新建文件
     */
    @RequestMapping("doAddFile")
    public ResultObj doAddFile(File file){
        try {
            //默认新建在根目录
            if (file.getParentid()==null){
                file.setParentid(Constant.FOLDER_ROOT);
            }
            file.setType(Constant.FILE_TYPE_NORMAL);
            file.setModifytime(new Date());

            //上传文件
            this.fileService.addFile(file);

            //修改项目信息与仓库信息
            Project project = new Project();
            project.setId(file.getProjectid());
            project.setModifytime(new Date());
            this.projectService.updateProject(project);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }


    /**
     * 上传文件夹
     * @param file
     * @return
     * @throws IOException
     */
    @RequestMapping("doUploadFolder")
    public ResultObj doUploadFolder(File file){
        try {
            this.fileService.uploadFoler(file);

            //修改项目信息与仓库信息
            Project project = new Project();
            project.setId(file.getProjectid());
            project.setModifytime(new Date());
            this.projectService.updateProject(project);
            return ResultObj.ADD_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.ADD_ERROR;
        }
    }

    /**
     * 删除文件或文件夹
     * @param id
     * @return
     * @throws IOException
     */
    @RequestMapping("deleteFileOrFolder")
    public ResultObj deleteFileOrFolder(Integer id){
        try {
            //修改项目信息与仓库信息
            File byId = this.fileService.getById(id);
            Project project = new Project();
            project.setId(byId.getProjectid());
            project.setModifytime(new Date());
            this.projectService.updateProject(project);

            this.fileService.removeById(id);
            return ResultObj.DELETE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.DELETE_ERROR;
        }
    }
    /**
     * 重命名文件
     * @param file
     * @return
     *
     */
    @RequestMapping("renameFile")
    public ResultObj renameFile(File file){
        try {
            //修改项目信息与仓库信息
            File byId = this.fileService.getById(file.getId());
            Project project = new Project();
            project.setId(byId.getProjectid());
            project.setModifytime(new Date());
            this.projectService.updateProject(project);

            file.setModifytime(new Date());
            this.fileService.updateById(file);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_SUCCESS;
        }
    }

    /**
     * 修改文件
     * @param fileVo
     * @return
     *
     */
    @RequestMapping("editFile")
    public ResultObj editFile(FileVo fileVo){
        try {
            //修改项目信息与仓库信息
            File byId = this.fileService.getById(fileVo.getId());
            Project project = new Project();
            project.setId(byId.getProjectid());
            project.setModifytime(new Date());
            this.projectService.updateProject(project);

            this.fileService.editFile(fileVo);
            return ResultObj.UPDATE_SUCCESS;
        } catch (Exception e) {
            e.printStackTrace();
            return ResultObj.UPDATE_SUCCESS;
        }
    }




}
