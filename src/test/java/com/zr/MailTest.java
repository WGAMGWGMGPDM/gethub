package com.zr;

import com.zr.system.utils.MD5Util;
import com.zr.system.utils.MailUtil;
import org.junit.jupiter.api.Test;

/**
 * @Author: 张忍
 * @Date: 2020-03-16 0:28
 */
public class MailTest {
    @Test
    void contextLoads() {
        //new MailUtil("18753794733@163.com","200").run();
    }

    @Test
    void md5() {
        String createUUID = MD5Util.createUUID();
        System.out.println(createUUID);
    }
}
