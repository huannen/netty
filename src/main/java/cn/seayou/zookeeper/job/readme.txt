参考博客：https://blog.csdn.net/qq_40378034/article/details/117014648

实际上属于分布式选举项目

需求：
现在有三个服务器正在运行，也就是说生产上一个应用部署了三台实例，
现在需要每天凌晨2点跑一个程序来生成报表。如果三台服务器都去跑这个日终程序，显然没必要也不合适
只需一台去执行这个日终报表生成程序即可，

解决思路：
三台机器一起协商一下，由其中一台机器去执行报表生成程序。

1、三台机器在启动的时候，在config.ini里面设置下master=true，只有master去生成报表
2、那么如果master挂机了怎么办，凡是思考下怎么高可用

可以利用zookeeper来进行选举一个master，这样当master挂机了，zookeeper会重新发起选举

解决步骤：
1、在zookeeper创建master_root根节点
2、每台机器启动的时候，在master_root节点下添加一个临时节点
3、创建完临时节点之后，紧接着获取master_root当前的子节点，是不是有master节点
    如果有master，那新建的节点就是slave
    如果没有master，那就选择当前master_root节点下序号最小的节点为master
