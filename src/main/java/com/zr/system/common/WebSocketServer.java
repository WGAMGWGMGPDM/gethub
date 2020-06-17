package com.zr.system.common;

import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.stereotype.Component;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @Author: 张忍
 * @Date: 2020-04-05 22:29
 */
@ServerEndpoint("/api/sysInfo") 
@Component
public class WebSocketServer {
    static Log log= LogFactory.get(WebSocketServer.class);
    /**concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。*/
    private static CopyOnWriteArraySet<WebSocketServer> webSocketSet = new CopyOnWriteArraySet<WebSocketServer>();
    /**与某个客户端的连接会话，需要通过它来给客户端发送数据*/
    private Session session;


    /**
     * 连接建立成功调用的方法*/
    @OnOpen
    public void onOpen(Session session)  {
        this.session = session;
        webSocketSet.add(this);
        log.info("WebSocketServer连接成功");
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        webSocketSet.remove(this);
        log.info("WebSocketServer退出连接");
    }

    /**
     *
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocketServer错误，原因："+error.getMessage());
        error.printStackTrace();
    }
    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException{
        this.session.getBasicRemote().sendText(message);
    }



    /**
     * 发送自定义消息
     * */
    public static void sendInfo(String message) throws IOException{
        log.info("发送消息报文:"+message);
        for (WebSocketServer webSocketServer : webSocketSet) {
            webSocketServer.sendMessage(message);
        }
    }


}
