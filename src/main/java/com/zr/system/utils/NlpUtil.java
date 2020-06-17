package com.zr.system.utils;

import com.alibaba.fastjson.JSON;
import com.baidu.aip.nlp.AipNlp;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * @Author: 张忍
 * @Date: 2020-03-30 14:19
 */
public class NlpUtil {
    //设置APPID/AK/SK
    public static final String APP_ID = "19152491";
    public static final String API_KEY = "MiA5SA7aOG2zIOLe6SuMmkSr";
    public static final String SECRET_KEY = "gGk5v6W07MR3pFWVc3zURr6Baz4AEOdC";

    public static Set<String> getTag(String title, String content){
        // 初始化一个AipNlp
        AipNlp client = new AipNlp(APP_ID, API_KEY, SECRET_KEY);
        // 传入可选参数调用接口
        HashMap<String, Object> options = new HashMap<String, Object>();

        // 文章标签
        JSONObject res = client.keyword(title, content, options);
        JSONArray items = res.getJSONArray("items");
        Set<String> tag = new HashSet<>();
        for (int i = 0; i < items.length(); i++) {
            com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(items.get(i).toString());
            tag.add((String) jsonObject.get("tag"));
        }
        return tag;
    }
}
