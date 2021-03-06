package com.git.onedayrex.nettybio.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.CountDownLatch;

/**
 * created by onedayrex
 * 2018/3/3
 **/
public class ExecutorSocketClient {

    private static final Logger logger = LoggerFactory.getLogger(SocketClient.class);

    public static void main(String[] args) {
        CountDownLatch countDownLatch = new CountDownLatch(3);
        for (int i = 0; i < 1000; i++) {
            new Thread(()->{
                String host = "127.0.0.1";
                int port = 8080;
                try (Socket socket = new Socket(host, port);
                     InputStream in = socket.getInputStream()) {
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                    String result = bufferedReader.readLine();
                    logger.info("receive server==>{}", result);
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
