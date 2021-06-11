package cn.seayou.zookeeper.api;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

public class ZookeeperConnector implements Watcher {

    CountDownLatch countDownLatch = new CountDownLatch(1);

    public ZooKeeper connect(String ipPort, int timeout) throws IOException {
        ZooKeeper zooKeeper = new ZooKeeper(ipPort, timeout, this);
        try{
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
            countDownLatch.countDown();
        }
    }

    public static void main(String[] args) throws IOException {
        ZooKeeper zooKeeper = new ZookeeperConnector().connect("192.168.176.128:2181", 5000);
        System.out.println(zooKeeper);
    }
}
