package com.zr.repo.utils;

import cn.hutool.core.io.FileUtil;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.UUID;

/**
 * @Author: 张忍
 * @Date: 2020-03-18 16:41
 */
public class UploadUtil {


    /**
     * 文件夹上传到项目临时文件夹下
     * @param filename
     * @param mf
     * @throws Exception
     */
    public static String uploadTemp(String temp, String filename, MultipartFile mf) {
        try {
            //获取文件的各级目录
            List<String> separatedPath = getSeparatedPath(filename);
            // 扫描文件目录结构

            for (int j = 0; j < separatedPath.size() - 1; j++) {
                temp += "/" + separatedPath.get(j);
                // 若父级目录目录不存在，创建之
                if (!new File(temp).exists()) {
                    new File(temp).mkdir();
                }
            }
            // 写入文件
            writeFile( mf, temp, separatedPath.get(separatedPath.size() - 1));
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
            return "上传失败";
        }
    }

    /**
     * 文件夹上传到项目临时文件夹下
     * @param filename
     * @param file
     * @throws Exception
     */
    public static String uploadTemp(String temp, String filename, File file) {
        try {
            //获取文件的各级目录
            List<String> separatedPath = getSeparatedPath(filename);
            // 扫描文件目录结构

            for (int j = 0; j < separatedPath.size() - 1; j++) {
                temp += "/" + separatedPath.get(j);
                // 若父级目录目录不存在，创建之
                if (!new File(temp).exists()) {
                    new File(temp).mkdir();
                }
            }
            // 写入文件
            File file1 = new File(temp,separatedPath.get(separatedPath.size() - 1));
            file.renameTo(file1);
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
            return "上传失败";
        }
    }

    /**
     * 获取项目的绝对路径
     * @return
     * @throws FileNotFoundException
     */
    public static String getBasePath() throws FileNotFoundException {
        //获取根目录
        File path = new File(ResourceUtils.getURL("classpath:").getPath());
        if(!path.exists()) path = new File("");
        //如果上传目录为/static/images/upload/，则可以如下获取：
        File upload = new File(path.getAbsolutePath(),"static/project/"+ UUID.randomUUID().toString().replace("-","")+"/");
        if(!upload.exists()) upload.mkdirs();
        String floder = upload.getAbsolutePath();
        //在开发测试模式时，得到的地址为：{项目跟目录}/target/static/images/upload/
        //在打包成jar正式发布时，得到的地址为：{发布jar包目录}/static/images/upload/
        return floder;
    }


    /**
     * 向磁盘写入文件
     *
     * @param fileItem 文件项，包含文件内容
     * @param filePath 文件路径，不包含文件名
     * @param fileName 文件名，不包含路径
     */
    private static void writeFile(MultipartFile fileItem, String filePath, String fileName) throws Exception {
        File file = new File(filePath, fileName);
        fileItem.transferTo(file);
    }

    /**
     * 从文件路径中获取文件名及各级父目录
     *
     * @param filePath 文件相对项目目录的路径
     * @return List<String> (0 ~ n - 2) :各级父目录名称
     * n - 1       :不包含路径的文件名
     * 示例：filePath --- taskName/src/main/java/test.java
     * 返回["taskName", "src", "main", "java", "test.java"]
     */
    public static List<String> getSeparatedPath(String filePath) {
        List<String> result = new ArrayList<>();
        StringTokenizer tokenizer = new StringTokenizer(filePath, "/");
        while (tokenizer.hasMoreElements()) {
            result.add(tokenizer.nextToken());
        }
        return result;
    }

    /**
     * 递归删除文件夹
     * @param path
     * @return
     */
    public static boolean delete(String path){
        File file = new File(path);
        if(!file.exists()){
            return false;
        }
        if(file.isFile()){
            return file.delete();
        }
        File[] files = file.listFiles();
        for (File f : files) {
            if(f.isFile()){
                if(!f.delete()){
                    System.out.println(f.getAbsolutePath()+" delete error!");
                    return false;
                }
            }else{
                if(!UploadUtil.delete(f.getAbsolutePath())){
                    return false;
                }
            }
        }
        return file.delete();
    }

}
