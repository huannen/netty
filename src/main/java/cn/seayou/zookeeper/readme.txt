参考博客：
https://blog.csdn.net/sky_jiangcheng/article/details/100013824?utm_medium=distribute.pc_relevant.none-task-blog-baidujs_title-1&spm=1001.2101.3001.4242

Zookeeper是一个分布式协调服务，可用于服务发现，分布式锁，分布式领导选举，配置管理等。

这一切的基础，都是Zookeeper提供了一个类似于Linux文件系统的树形结构
（可认为是轻量级的内存文件系统，但只适合存少量信息，完全不适合存储大量文件或者大文件），
同时提供了对于每个节点的监控与通知机制。

思考：
1、作为一个文件系统，Zookeeper如何保证数据一致性；
2、如何进行领导选举，以及数据监控/通知机制的语义保证。
3、zookeeper的羊群效应怎么处理的（和ReentrantLock中的AQS有类似）
4、zookeeper如何实现分布式锁（包括独享锁和共享锁的实现）
5、zookeeper在哪些开源框架中有应用，怎么应用的（比如dubbo中zookeeper的数据结构）
