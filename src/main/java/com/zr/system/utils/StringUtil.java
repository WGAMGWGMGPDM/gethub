package com.zr.system.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: 张忍
 * @Date: 2020-04-04 21:55
 */
public class StringUtil {

    /**
     * java统计List集合中每个元素出现的次数
     * 例如frequencyOfListElements(["111","111","222"])
     * ->
     * 则返回Map {"111"=2,"222"=1}
     *
     * @param items
     * @return Map<String, Integer>
     * @author wuqx
     */
    public static Map<String, Integer> frequencyOfListElements(List<String> items) {
        if (items == null || items.size() == 0) return null;
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (String temp : items) {
            Integer count = map.get(temp);
            map.put(temp, (count == null) ? 1 : count + 1);
        }
        return map;
    }
}

