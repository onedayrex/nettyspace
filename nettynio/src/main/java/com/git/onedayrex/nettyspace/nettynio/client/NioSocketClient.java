package com.git.onedayrex.nettyspace.nettynio.client;

import com.xiaoleilu.hutool.setting.Setting;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * created by onedayrex
 * 2018/3/4
 **/
public class NioSocketClient implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(NioSocketClient.class);

    private Selector selector = null;

    private SocketChannel socketChannel = null;

    private String host;

    private int port;

    public NioSocketClient() {
        Setting setting = new Setting("config.setting");
        host = setting.getStr("host", "nio", "127.0.0.1");
        port = setting.getInt("ip", "nio");
        try {
            selector = Selector.open();
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            if (socketChannel.connect(new InetSocketAddress(host, port))) {
                //如果连接成功，则注册读取channel到selector
                socketChannel.register(selector, SelectionKey.OP_READ);
                this.sendMessage(socketChannel);
            }else {
                //绑定连接
                socketChannel.register(selector, SelectionKey.OP_CONNECT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        while (true) {
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                if (iterator.hasNext()) {
                    SelectionKey key = iterator.next();
                    iterator.remove();
                    this.business(key);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void business(SelectionKey key) throws IOException {
        //判断key可用
        if (key.isValid()) {
            //如果是连接channel
            if (key.isConnectable()) {
                SocketChannel channel = (SocketChannel) key.channel();
                if (channel.finishConnect()) {
                    //如果连接成功，则注册读取channel到selector
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    this.sendMessage(socketChannel);
                }
            }
            //如果有可读取数据
            if (key.isReadable()) {
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                int read = socketChannel.read(buffer);
                if (read > 0) {
                    //有可读取数据
                    buffer.flip();
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    String result = new String(bytes, "UTF-8");
                    logger.info("receive message ==> [{}]", result);
                    this.sendMessage(socketChannel);
                } else if (read < 0) {
                    key.cancel();
                    socketChannel.close();
                }
            }
        }
    }

    private void sendMessage(SocketChannel channel) throws IOException {
        String date = "QUERY-->";
        ByteBuffer buffer = ByteBuffer.allocate(date.getBytes().length);
        buffer.put(date.getBytes());
        buffer.flip();
        channel.write(buffer);
        if (!buffer.hasRemaining()) {
             logger.info("has date send");
        }
    }
}
