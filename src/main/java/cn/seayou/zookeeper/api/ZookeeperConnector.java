package cn.seayou.zookeeper.api;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/*
 * Watcher接口，process方法
 */
public class ZookeeperConnector implements Watcher {

    CountDownLatch countDownLatch = new CountDownLatch(1);

    public ZooKeeper connect(String ipPort, int timeout) throws IOException {

        // 创建zookeeper的连接，必须指定一个监听器Watcher
        /*
         * zookeeper的连接和数据库的连接不同，数据库通过DriverManager的getConnect方法就可以直接获取到连接，
         * 但zookeeper在获取连接的过程中，使用了Future。也就意味着，new之后所拿到的仅仅是一个zookeeper对象，
         * 而这个对象可能还没有连接到zookeeper服务。
         * 这么说，zookeeper的连接过程可能会受到网络，zookeeper集群各种问题的影响，连接的过程可能会比较慢，
         * 因此，为了提高程序的执行性能，在new的时候就给你一个协议，你可以通过这个协议，来拿到连接。
         * 这里其实就是一个Futrue模式，如果对Future不是很了解的朋友可以去网络上找些资料学习。
         */
        ZooKeeper zooKeeper = new ZooKeeper(ipPort, timeout, this); //不阻塞的
        try{
            // 等待连接创建完毕
            countDownLatch.await();
            System.out.println("zookeeper connected");
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return zooKeeper;
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        if(watchedEvent.getState()== Event.KeeperState.SyncConnected){
            //连接穿件完毕，叫醒等待的线程
            countDownLatch.countDown();
            System.out.println("打印连接信息");
        }
    }

    public static void main(String[] args) throws IOException {
        ZooKeeper zooKeeper = new ZookeeperConnector().connect("192.168.176.128:2181", 5000);
        System.out.println(zooKeeper);
    }
}
