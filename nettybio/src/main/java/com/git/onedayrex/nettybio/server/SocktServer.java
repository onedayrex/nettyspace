package com.git.onedayrex.nettybio.server;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

/**
 * created by onedayrex
 * 2018/3/3
 **/
public class SocktServer {
    private static final Logger logger = LoggerFactory.getLogger(SocktServer.class);

    public static void main(String[] args){
        //绑定ip
        int port = 8080;
        try (ServerSocket server = new ServerSocket(port)){
            logger.info("监听端口==>{}", port);
            Socket accept = null;
            while (true) {
                accept = server.accept();
                Socket finalAccept = accept;
                new Thread(()->{
                    try (OutputStream outputStream = finalAccept.getOutputStream()){
                        String date = DateFormatUtils.format(new Date(), "yyyyMMdd");
                        outputStream.write(date.getBytes());
                        logger.info("发送时间成功==>{}",date);
                    } catch (IOException e) {
                    }
                }).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
