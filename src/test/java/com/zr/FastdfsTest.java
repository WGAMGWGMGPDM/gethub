package com.zr;

import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.zr.system.common.file.UploadService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @Author: 张忍
 * @Date: 2020-03-25 18:51
 */
public class FastdfsTest {
    @Test
    void delete(){
//        UploadService uploadService = new UploadService();
//        uploadService.deleteFile("http://101.200.130.83/group1/M00/00/00/rBEwVV57G8GANm5YAAAADyslEMg580.txt");

    }
    @Test
    void tempfile() throws IOException {
        File file = new File("C:\\Users\\renre\\AppData\\Local\\Temp\\project108338697724358645006\\zxr.txt");
        //FileUtil.writeString("你好",file,"utf-8");
    file.createNewFile();
        //Path zxr = Files.createTempDirectory("zxr");
        System.out.println(file.getAbsolutePath());
        //String property = System.getProperty("java.io.tmpdir");
        //System.out.println(property);
    }


}
