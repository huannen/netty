package cn.seayou.zookeeper.api;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

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
        try {
            /*
             * 1、初始连接，采用下面的方式，也可以调用我们写的接口 @ZookeeperConnector
             */
            ZooKeeper zooKeeper = new ZooKeeper("192.168.176.128:2181",
                    2000,
                    watchedEvent -> {
                        System.out.println("hello, watch out! you are be watched!");
                    }
            );

//            ZooKeeper zooKeeper = new ZookeeperConnector().connect("192.168.176.128:2181", 2000);

            /*
             * 2、创建节点
             * 需要指定的参数：path，content，acl，type
             */
            String path = "/demo/api";
            String content = "hello zookeeper, i am hnj";
            zooKeeper.create(path, content.getBytes(), ZooDefs.Ids.READ_ACL_UNSAFE, CreateMode.PERSISTENT);

            /*
             * 3、查询节点
             * 查询节点的几口有两种类型：异步获取和同步获取，异步获取会多一个回调，
             * Watcher，在数据做了修改之后，zookeeper会发出通知，那么注册的Wacher就可以接收到数据的修改。
             */
            //异步方式，没有返回值，在回调函数中处理
            zooKeeper.getData(path, false, new AsyncCallback.DataCallback() {
                @Override
                public void processResult(int i, String s, Object o, byte[] bytes, Stat stat) {
                    System.out.println("data access successfully");
                    System.out.println("async data:" + new String(bytes, StandardCharsets.UTF_8));
                }
            }, null);
            Thread.sleep(5000);


            //同步方式，返回数据的字节数组
            byte [] data =  zooKeeper.getData(path, false, new Stat());
            System.out.println("sync data:" + new String(data, StandardCharsets.UTF_8));

            /*
             * 4、列出子节点
             */
            List<String> children = zooKeeper.getChildren(path, true);
            Thread.sleep(2000);
            System.out.println(children);

            /*
             * 5、修改节点
             * 和getData类似，setData也有两种模式，一种是直接修改，另外一种是异步修改，
             * 异步修改之后可以通过回调将修改后的数据带回。
             */
            /*
             * 这里的version指的是znode节点的dataVersion的值，每次数据的修改都会更新这个值，
             * 主要是为了保证一致性，具体请了解CAS。通俗来讲就是如果你指定的version比保持的version值小，
             * 则表示已经有其他线程所更新了，你也就不能更新成功了，否则则可以更新成功。
             * 如果你不管别的线程有没有更新成功都要更新这个节点的值，则version可以指定为-1
             */
            Stat stat = zooKeeper.setData(path, content.getBytes(), -1);  //同步
            System.out.println(stat);

            /*
             * 异步修改。
             * 注意：在get的时候，看到有参数为Watcher，这里Watcher是干嘛的呢，就是可以在数据做了修改之后，
             * zookeeper会发出通知，那么注册的Wacher就可以接收到数据的修改。
             */
            zooKeeper.setData(path, content.getBytes(), -1, new AsyncCallback.StatCallback() {
                @Override
                public void processResult(int i, String s, Object o, Stat stat) {
                    System.out.println(i + " " + s);
                }
            }, null);


        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (KeeperException e) {
            e.printStackTrace();
        } finally {

        }
    }
}
