package com.zr.system.utils;

import org.apache.shiro.crypto.hash.Md5Hash;

import java.util.Random;
import java.util.UUID;

/**
 * @Author: 张忍
 * @Date: 2020-03-16 2:35
 */
public class MD5Util {
    /**
     * 生成uuid
     * @return
     */
    public static String createUUID(){
        return UUID.randomUUID().toString().replace("-","");
    }

    /**
     * 加密
     * @param source
     * @param salt
     * @param hashInterations
     * @return
     */
    public static String md5(String source,String salt,Integer hashInterations){
        Md5Hash hash = new Md5Hash(source,salt,hashInterations);
        return hash.toString();
    }

    public static String createRandom(){
        String code = "";
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int i1 = random.nextInt(10);
            code+=i1;
        }
        return code;
    }
}
