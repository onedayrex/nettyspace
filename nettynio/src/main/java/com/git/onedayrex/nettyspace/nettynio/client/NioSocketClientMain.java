package com.git.onedayrex.nettyspace.nettynio.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * created by onedayrex
 * 2018/3/4
 **/
public class NioSocketClientMain {
    private static final Logger logger = LoggerFactory.getLogger(NioSocketClientMain.class);

    public static void main(String[] args) {
        new Thread(new NioSocketClient()).start();
    }
}
