package com.zr;

import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONReader;
import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

/**
 * @Author: 张忍
 * @Date: 2020-03-08 22:08
 */
public class ApiTest {

    @Test
    void initPermission(){
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("ak", "G0eyc4vMYZzhnQLo0T81itnIXB3kXvBy");
        paramMap.put("ip", "39.183.124.162");

        String result = HttpUtil.post("http://api.map.baidu.com/location/ip", paramMap);
        JSONObject jsonObject = JSON.parseObject(result);
        String content = (String) jsonObject.get("address");
        System.out.println(content);
    }
    @Test
    void tokenTest(){
        String token = com.zr.system.utils.HttpUtil.getToken("MiA5SA7aOG2zIOLe6SuMmkSr", "gGk5v6W07MR3pFWVc3zURr6Baz4AEOdC");
        System.out.println(token);
    }

}
