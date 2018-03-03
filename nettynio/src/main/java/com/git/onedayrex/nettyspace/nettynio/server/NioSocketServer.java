package com.git.onedayrex.nettyspace.nettynio.server;

import com.xiaoleilu.hutool.setting.Setting;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

/**
 * created by onedayrex
 * 2018/3/4
 **/
public class NioSocketServer implements Runnable{
    private static final Logger logger = LoggerFactory.getLogger(NioSocketServer.class);

    private Selector selector = null;

    private ServerSocketChannel serverSocketChannel = null;

    private String host;

    private int port;

    public NioSocketServer() {
        //从配置中读取host与ip
        Setting setting = new Setting("config.setting");
        this.host = setting.getStr("host","nio","127.0.0.1");
        this.port = setting.getInt("ip","nio");
        try {
            //创建selector 多路复用
            this.selector = Selector.open();
            //创建通过
            this.serverSocketChannel = ServerSocketChannel.open();
            //设置为非阻塞
            serverSocketChannel.configureBlocking(false);
            //绑定端口
            serverSocketChannel.socket().bind(new InetSocketAddress(port),1024);
            //注册到多路复用中
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            logger.info("open nio server listen port ==>{}", port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void run() {
        //每秒轮训一次selector
        while (true) {
            try {
                selector.select(1000);
                Set<SelectionKey> selectionKeys = selector.selectedKeys();
                Iterator<SelectionKey> iterator = selectionKeys.iterator();
                SelectionKey key = null;
                while (iterator.hasNext()) {
                    //如果有符合条件的channel，则处理channel
                    key = iterator.next();
                    //从列表中取出后移除
                    iterator.remove();
                    this.business(key);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void business(SelectionKey key) throws IOException {
        //判断channel没有关闭
        if (key.isValid()) {
            //判断c是否是连接的channel状态
            if (key.isAcceptable()) {
                ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                SocketChannel accept = channel.accept();
                accept.configureBlocking(false);
                //再次把需要读取read状态的channel注册到selector，注册为读取时成立
                accept.register(selector, SelectionKey.OP_READ);
            }
            //判断是读取channel状态 处理读取数据
            if (key.isReadable()) {
                SocketChannel channel = (SocketChannel) key.channel();
                ByteBuffer buffer = ByteBuffer.allocate(1024);
                int read = channel.read(buffer);
                if (read > 0) {
                    //设置buffer为读取模式
                    buffer.flip();
                    //读取到数据
                    byte[] bytes = new byte[buffer.remaining()];
                    buffer.get(bytes);
                    String result = new String(bytes, "UTF-8");
                    logger.info("server receive message ==> [{}]", result);
                    this.sendMessage(channel);
                } else if (read < 0) {
                    //数据读取完成
                    key.cancel();
                    channel.close();
                }
            }
        }
    }

    /**
     * 发送数据给客户端
     * @param channel
     */
    private void sendMessage(SocketChannel channel) throws IOException {
        String date = DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
        ByteBuffer buffer = ByteBuffer.allocate(date.getBytes().length);
        buffer.put(date.getBytes());
        buffer.flip();
        channel.write(buffer);
    }
}
