package com.zr.system.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baidu.aip.nlp.AipNlp;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: 张忍
 * @Date: 2020-03-08 21:58
 */
public class HttpUtil {

    /**
     * 获取token
     * @param API_Key
     * @param Secret_Key
     * @return
     */
    public static String getToken(String API_Key,String Secret_Key){
        Map<String,Object> params = new HashMap<>();
        params.put("client_id",API_Key);
        params.put("client_secret",Secret_Key);
        params.put("grant_type","client_credentials");
        String res = cn.hutool.http.HttpUtil.post("https://aip.baidubce.com/oauth/2.0/token", params);
        JSONObject jsonObject = JSON.parseObject(res);
        String access_token = (String)jsonObject.get("access_token");
        return access_token;

    }

    /**
     * 根据IP获得地址
     * @param ip
     * @return
     */
    public static String IptoAddress(String ip){
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("ak", "G0eyc4vMYZzhnQLo0T81itnIXB3kXvBy");
        paramMap.put("ip", ip);
        String result = cn.hutool.http.HttpUtil.post("http://api.map.baidu.com/location/ip", paramMap);
        JSONObject jsonObject = JSON.parseObject(result);
        String address = (String) jsonObject.get("address");
        System.out.println(address);
        return address;
    }


}
