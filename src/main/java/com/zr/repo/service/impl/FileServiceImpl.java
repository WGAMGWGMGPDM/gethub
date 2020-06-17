package com.zr.repo.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.MD5;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zr.repo.utils.UploadUtil;
import com.zr.repo.vo.FileVo;
import com.zr.system.common.Constant;
import com.zr.system.common.ResultObj;
import com.zr.system.common.file.UploadService;
import com.zr.system.utils.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zr.repo.mapper.FileMapper;
import com.zr.repo.domain.File;
import com.zr.repo.service.FileService;
import org.springframework.web.multipart.MultipartFile;

/**
* @Author: 张忍
* @Date: 2020-03-22 16:28
*/
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService{
    @Autowired
    private UploadService uploadService;

    @Override
    public File saveFile(File file) {
        this.baseMapper.insert(file);
        return file;
    }

    /**
     * 编辑文件
     * @param fileVo
     */
    @Override
    public void editFile(FileVo fileVo) {
        try {

            //新建临时文件
            String[] name = fileVo.getFilename().split("\\.");
            java.io.File edit_file = java.io.File.createTempFile(name[0]+MD5Util.createUUID(),"."+name[1]);
            //写入信息
            FileUtil.writeString(fileVo.getFilecontent(),edit_file,"utf-8");


            File oldFile = this.baseMapper.selectById(fileVo.getId());

            //判断文件变化
            String newmd5Hex = DigestUtil.md5Hex(edit_file);
            if (newmd5Hex.equals(oldFile.getMd5code())){
                System.out.println("文件无变化，不需要修改");
            }else {
                //删除源文件
                this.uploadService.deleteFile(oldFile.getFilepath());
                //上传到fastdfs
                String path = this.uploadService.uploadFile(edit_file);
                //存数据库
                File file = new File();
                file.setId(fileVo.getId());
                file.setModifytime(new Date());
                file.setFilepath(path);
                file.setMd5code(newmd5Hex);
                this.baseMapper.updateById(file);
            }
            //删除临时文件
            edit_file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }




    }

    /**
     * 上传文件夹
     * @param file
     * @return
     */
    @Override
    public Boolean uploadFoler(File file) {
        try {
            MultipartFile[] mfs = file.getMfs();
            if (file.getParentid()==null){
                file.setParentid(Constant.FOLDER_ROOT);
            }
            for (MultipartFile mf : mfs) {
                //得到文件类型
                String contentType = mf.getContentType();
                //得到文件名
                String filename = mf.getOriginalFilename();
                //分割文件名得到层级文件夹名
                Integer pid = file.getParentid();
                List<String> paths = UploadUtil.getSeparatedPath(filename);
                //遍历构造文件夹
                for (int i = 0; i < paths.size() - 1; i++) {
                    File tempfolder = new File();
                    tempfolder.setName(paths.get(i));
                    tempfolder.setProjectid(file.getProjectid());
                    tempfolder.setModifytime(new Date());
                    tempfolder.setType(Constant.FILE_TYPE_FOLDER);
                    tempfolder.setParentid(pid);
                    //查询是否已经存在
                    QueryWrapper<File> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("projectid",tempfolder.getProjectid());
                    queryWrapper.eq("parentid",tempfolder.getParentid());
                    queryWrapper.eq("name",tempfolder.getName());
                    queryWrapper.eq("type",tempfolder.getType());
                    File one = this.baseMapper.selectOne(queryWrapper);
                    if (null==one){
                        File backfile = this.saveFile(tempfolder);
                        pid = backfile.getId();
                    }else {
                        pid = one.getId();
                    }
                }
                //上传文件到fastdfs
                String filepath = this.uploadService.uploadMultipartFile(mf);
                //MD5校验
                InputStream inputStream = mf.getInputStream();
                String md5code = DigestUtil.md5Hex(inputStream);
                //存储文件
                File tempfile = new File();
                tempfile.setName(paths.get(paths.size()-1));
                tempfile.setParentid(pid);
                //判断文件类型
                String substring = contentType.substring(0, contentType.indexOf("/"));
                if (substring.equals("image")){
                    tempfile.setType(Constant.FILE_TYPE_IMG);
                }else if (substring.equals("audio")){
                    tempfile.setType(Constant.FILE_TYPE_AUDIO);
                }else if (substring.equals("video")){
                    tempfile.setType(Constant.FILE_TYPE_VIDEO);
                }else {
                    tempfile.setType(Constant.FILE_TYPE_NORMAL);
                }
                tempfile.setModifytime(new Date());
                tempfile.setProjectid(file.getProjectid());
                tempfile.setFilepath(filepath);
                tempfile.setMd5code(md5code);
                this.saveFile(tempfile);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            log.error("上传文件夹失败"+e);
            return false;
        }
    }

    /**
     * 新建文件
     * @param file
     */
    @Override
    public void addFile(File file) {

        try {
            //新建临时文件
            String[] name = file.getName().split("\\.");
            java.io.File edit_file = java.io.File.createTempFile(name[0]+ MD5Util.createUUID(),"."+name[1]);
            //文件md5码
            String newmd5Hex = DigestUtil.md5Hex(edit_file);
            file.setMd5code(newmd5Hex);
            //上传到fastdfs
            String path = this.uploadService.uploadFile(edit_file);
            file.setFilepath(path);
            //上传数据库
            this.saveFile(file);
            //删除临时文件
            edit_file.delete();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件
     * @param id
     * @return
     */
    @Override
    public boolean removeById(Serializable id) {

        ArrayList<Serializable> ids = new ArrayList<>();
        ids.add(id);
        File ffile = this.baseMapper.selectById(id);
        //删除fastdfs文件实体
        if (null!=ffile.getFilepath()){
            this.uploadService.deleteFile(ffile.getFilepath());
        }
        this.getIds(ids,id);
        int i = this.baseMapper.deleteBatchIds(ids);
        return i>0;
    }

    //递归删除
    private void getIds(ArrayList<Serializable> ids,Serializable oneId){
        //查询二级分类的对象
        QueryWrapper<File> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("parentid",oneId);
        List<File> files = this.baseMapper.selectList(queryWrapper);


        //遍历二级分类的对象，把二级分类的id加入到要删除的集合中
        for (File file : files) {

            //删除fastdfs文件实体
            if (null!=file.getFilepath()){
                this.uploadService.deleteFile(file.getFilepath());
            }
            Integer id = file.getId();
            ids.add(id);
            //把二级分类的每一个ID，查询它下面的子节点
            this.getIds(ids,id);
        }

    }
}
