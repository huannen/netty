分布式集群的管理，管理集群

原始需求：
1、线上有多少个节点
    每个节点启动的时候，就往zookeeper里面创建临时节点
2、多少节点正在运行
    查看临时节点
3、节点资源使用状态是什么，比如CPU，内存
    定时更新zookeeper存储的CPU和内存信息
4、如果资源超出阈值，或节点宕掉，实时收到警报
    监听临时节点的资源状态

zookeeper数据结构设计
临时序号节点
    1.cluster-manger // 根节点
    a.server00001 :<json> //服务节点 1
    b.server00002 :<json>//服务节点 2
    c.server........n :<json>//服务节点 n
        服务状态信息:
        a.ip
        b.cpu
        c.memory
        d.disk

流程设计
1、当server启动的时候，往zookeeper里面创建临时节点，并且创建一个thread不断的往zookeeper里面更新server节点的状态
2、创建一个monitor，首先能实时查询server节点，其次添加一个监听监听server节点的状态


Agent：监控monitor
