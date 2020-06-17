package com.zr.system.common;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
/**
 * @Author: 张忍
 * @Date: 2020-06-08 21:08
 * @Description:webSocket定时发送消息类
 * @statement: 以<60s的频率发送给websocket连接的对象，以防止反向代理的60s超时限制
 */
@Configuration      //1.主要用于标记配置类，兼备Component的效果。
@EnableScheduling   // 2.开启定时任务
public class SaticScheduleTask {
    //3.添加定时任务,55秒是考虑5秒的延迟,从而保证60s的心跳
    //@Scheduled(cron = "0/55 * * * * ?")
    //或直接指定时间间隔，例如：55秒
    @Scheduled(fixedRate=55*1000)
    private void configureTasks() throws Exception{
        WebSocketServer.sendInfo("呼叫动拐,呼叫动拐!");
    }
}
