package cn.seayou.zookeeper.clusterManage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.I0Itec.zkclient.ZkClient;
import org.apache.zookeeper.CreateMode;

import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Agent {
    private String server = "192.168.176.128:2181";
    ZkClient zkClient;
    private static Agent instance;
    private static final String rootPath = "/cluster-manager";
    private static final String servicePath= rootPath + "service";
    private String nodePath;
    private Thread stateThread;
    List<OsBean> list = new ArrayList<>();

    public static void premain(String args, Instrumentation instrumentation){
        instance = new Agent();
        if(args != null){
            instance.server = args;
        }
        instance.init();
    }

    private void init() {
        zkClient = new ZkClient(server, 5000, 10000);
        System.out.println("zk连接成功" + server);

        /*
         * 1、构建根节点，根节点持久节点，server节点才是临时节点
         */
        buildRoot();

        /*
         * 2、生成服务节点，临时节点
         */
        createServerNode();
        stateThread = new Thread(() -> {
            while (true) {
                /*
                 * 3、监听服务节点状态改变
                 */
                updateServerNode();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }, "zk_stateThread");
        stateThread.setDaemon(true);
        stateThread.start();
    }

    /*
     * 监听服务节点状态改变
     */
    private void updateServerNode() {
        zkClient.writeData(nodePath, getOsInfo());
    }

    private void createServerNode() {
        nodePath = zkClient.createEphemeralSequential(servicePath, getOsInfo());
        System.out.println("create node：" + nodePath);
    }

    private void buildRoot() {
        if(!zkClient.exists(rootPath)){
            zkClient.createPersistent(rootPath);
        }

    }

    public String getOsInfo() {
        OsBean bean = new OsBean();
        bean.lastUpdateTime = System.currentTimeMillis();
        bean.ip = getLocalIp();
        bean.cpu = CPUMonitorCalc.getInstance().getProcessCpu();
        MemoryUsage memoryUsag = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
        bean.usableMemorySize = memoryUsag.getUsed() / 1024 / 1024;
        bean.usableMemorySize = memoryUsag.getMax() / 1024 / 1024;
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writeValueAsString(bean);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getLocalIp() {
        InetAddress addr = null;
        try {
            addr = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
        return addr.getHostAddress();
    }

}
