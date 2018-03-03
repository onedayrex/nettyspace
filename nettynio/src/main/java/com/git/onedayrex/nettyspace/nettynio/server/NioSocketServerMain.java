package com.git.onedayrex.nettyspace.nettynio.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by onedayrex
 * 2018/3/4
 **/
public class NioSocketServerMain {
    private static final Logger logger = LoggerFactory.getLogger(NioSocketServerMain.class);

    public static void main(String[] args) {
        new Thread(new NioSocketServer()).start();
    }
}
