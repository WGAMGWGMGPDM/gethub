package com.zr;

import com.zr.repo.utils.HttpUtil;
import org.junit.jupiter.api.Test;

import java.io.File;

/**
 * @Author: 张忍
 * @Date: 2020-03-22 19:39
 */
public class HttpUrlTest {
    @Test
    void url(){
        File file = HttpUtil.getNetUrl("http://101.200.130.83/group1/M00/00/00/rBEwVV53NCKAI5F-AAAAtOzAe6s7895.md");
        String absolutePath = file.getAbsolutePath();
        String name = file.getName();
        String name1 = file.getParentFile().getName();//Temp
        String parent = file.getParent();//C:\Users\renre\AppData\Local\Temp
        System.out.println(file);//C:\Users\renre\AppData\Local\Temp\net_url8204540448851827831rBEwVV53NCKAI5F-AAAAtOzAe6s7895.md
        System.out.println(name1);
        System.out.println(parent);

    }

}
