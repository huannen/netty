package cn.seayou.zookeeper.api;

import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class OriginApiBasicUsage {
    /*
     * 主要使用知识点
     * 1.初始连接
     * 2.创建、查看节点
     * 3.监听节点
     * 4.设置节点权限
     * 5.第三方客户端ZkClient
     */
    public static void main(String[] args) {
        /*
         * 1、初始连接
         *
         */

        try {
            ZooKeeper zooKeeper = new ZooKeeper("192.168.176.128:2181",
                    2000,
                    watchedEvent -> {
                        System.out.println("hello, watch out! you are be watched!");
                    }
            );

        } catch (IOException e) {
            e.printStackTrace();
        } finally {

        }
    }
}
