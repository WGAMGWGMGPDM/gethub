package com.zr.system.controller;

import com.zr.system.common.ActiveUser;
import com.zr.system.common.DataGridView;
import com.zr.system.common.file.UploadService;
import com.zr.system.domain.User;
import com.zr.system.service.UserService;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 张忍
 * @Date: 2020-03-18 23:39
 */

@RestController
@RequestMapping("api/file")
public class FileController {

    @Autowired
    private UploadService uploadService;
    @Autowired
    private UserService userService;

    /**
     * 上传头像
     * @param mf
     * @return
     */
    @RequestMapping("uploadImg")
    public Object uploadImg(MultipartFile mf){
        ActiveUser activeUser = (ActiveUser) SecurityUtils.getSubject().getPrincipal();
        User user = activeUser.getUser();
        //删除旧头像
        User user1 = this.userService.getById(user.getId());
        this.uploadService.deleteFile(user1.getImgpath());
        //上传到fastdfs
        String url = this.uploadService.uploadImage(mf);
        //更新数据库
        user.setImgpath(url);
        this.userService.updateUser(user);
        Map<String,String> map = new HashMap<>();
        map.put("src",url);
        return new DataGridView(map);
    }

    /**
     * 上传博客图片
     * @param mf
     * @return
     */
    @RequestMapping("uploadImgForArticle")
    public Object uploadImgForArticle(MultipartFile mf){
        Map<String,Object> map = new HashMap<>();

        try {
            //上传到fastdfs
            String url = this.uploadService.uploadImage(mf);
            map.put("url",url);
            map.put("success",1);
            map.put("message","上传成功");
            return map;
        } catch (Exception e) {
            e.printStackTrace();
            map.put("success",0);
            map.put("message","上传失败");
            return map;
        }
    }


}
