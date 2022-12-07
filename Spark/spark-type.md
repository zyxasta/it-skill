spark 的四大组件
1、SparkStreaming:针对实时数据进行流式计算的组件
2、SparkSQL:用来操作结构化数据的组件
3、GraphX:Spark面向图计算提供的框架与算法库
4、MLlib:一个机器学习算法库。


Spark的组件
A.DAGScheduler
C.TaskScheduler
D.SparkContext

 

////
窄转化/窄依赖(Narrow Dependency):指父RDD的每个分区只被子RDD的一个分区所使用,
例如map、filter等这些算子 一个RDD,对它的父RDD只有简单的一对一的关系,
RDD的每个partition仅仅依赖于父RDD 中的一个partition,
父RDD和子RDD的partition之间的对应关系,是一对一的。

宽转化/(Shuffle Dependency):父RDD的每个分区都可能被子RDD的多个分区使用,
例如groupByKey、 reduceByKey,sortBykey等算子,这些算子其实都会产生shuffle操作,
每一个父RDD的partition中的数据都可能会传输一部分到下一个RDD的每个partition中。
此时就会出现,父RDD和子RDD的partition之间,具有错综复杂的关系,这种情况就叫做两个RDD 之间是宽依赖,
同时,他们之间会发生shuffle操作。


5. 下面哪个操作是窄依赖
B. filter

6.下面哪个操作肯定是宽依赖
C. reduceByKey



////Spark支持3种集群部署模式
Standalone、Yarn、Mesos
7.Spark 的集群部署模式不包括
D. Local



////rdd特点
RDD:弹性分布式数据集 (Resilient Distributed DataSet)
分区:每一个 RDD 包含的数据被存储在系统的不同节点上。
逻辑上我们可以将 RDD 理解成一个大的数组,数组中的每个元素就代表一个分区 (Partition) 。
在物理存储中,每个分区指向一个存储在内存或者硬盘中的数据块 (Block) ,
其实这个数据块就是每个 task 计算出的数据块,它们可以分布在不同的节点上。
所以,RDD 只是抽象意义的数据集合,分区内部并不会存储具体的数据,
只会存储它在该 RDD 中的 index,通过该 RDD 的 ID 和分区的 index 可以唯一确定对应数据块的编号,
然后通过底层存储层的接口提取到数据进行处理。
在集群中,各个节点上的数据块会尽可能的存储在内存中,只有当内存没有空间时才会放入硬盘存储,
这样可以最大化的减少硬盘 IO 的开销。


不可变:不可变性是指每个 RDD 都是只读的,它所包含的分区信息是不可变的。
      由于已有的 RDD 是不可变的,所以我们只有对现有的 RDD 进行转化 (Transformation) 操作,才能得到新的 RDD 
      ,一步一步的计算出我们想要的结果。

      这样会带来这样的好处：我们在 RDD 的计算过程中,不需要立刻去存储计算出的数据本身,
      我们只要记录每个 RDD 是经过哪些转化操作得来的,即：依赖关系,这样一方面可以提高计算效率,
      一方面是错误恢复会更加容易。如果在计算过程中,第 N 步输出的 RDD 的节点发生故障,数据丢失,
      那么可以根据依赖关系从第 N-1 步去重新计算出该 RDD,
      这也是 RDD 叫做"弹性"分布式数据集的一个原因。

并行操作:因为 RDD 的分区特性,所以其天然支持并行处理的特性。
        即不同节点上的数据可以分别被处理,然后生成一个新的 RDD。

8. 下面哪个不是 RDD 的特点
C. 可修改

//RDD持久化 action算子 触发
////缓存
可以控制存储级别(内存、磁盘等)来进行缓存。 如果在应用程序中多次使用同一个RDD,
可以将该RDD缓存起来,该RDD只有在第一次计算的时候会根据血缘关系得到分区的数据,
在后续其他地方用到该RDD的时候,会直接从缓存处取而不用再根据血缘关系计算,这样就加速后期的重用。

// 数据缓存。  
wordToOneRdd.cache()  
  
// 可以更改存储级别  
mapRdd.persist(StorageLevel.MEMORY_AND_DISK_2) 

////checkpoint
虽然RDD的血缘关系天然地可以实现容错,当RDD的某个分区数据失败或丢失,可以通过血缘关系重建。
但是于长时间迭代型应用来说,随着迭代的进行,RDDs之间的血缘关系会越来越长,一旦在后续迭代过程中出错,
则需要通过非常长的血缘关系去重建,势必影响性能。
RDD支持checkpoint将数据保存到持久化的存储中,这样就可以切断之前的血缘关系,
因为checkpoint后的RDD不需要知道它的父RDDs了,它可以从checkpoint处拿到数据。

检查点其实就是通过将RDD 中间结果写入磁盘
wordToOneRdd.checkpoint()  

缓存和检查点区别
	Cache 缓存只是将数据保存起来,不切断血缘依赖。Checkpoint 检查点切断血缘依赖。
	Cache 缓存的数据通常存储在磁盘、内存等地方,可靠性低。Checkpoint 的数据通常存储在HDFS 等容错、高可用的文件系统,可靠性高。
	建议对checkpoint()的RDD 使用Cache 缓存,这样 checkpoint 的 job 只需从 Cache 缓存中读取数据即可,否则需要再从头计算一次RDD。
18.RDD有哪些缺陷?
A. 不支持细粒度的写和更新操作(如网络爬虫)
D. 不支持增量迭代计算

9. 下列哪个不是 RDD 的缓存方法
C. Memory()


10. Spark默认的存储级别
A. MEMORY_ONLY


11.在RDD行动算子中,用于返回数组的第一个元素的行动算子为()
A、first()


12.在Spark2.0 版本之前,Spark SQL中创建DataFrame和执行SQL的入口是()

C、SQLContext

13.在DataFrame的操作中,用于实现对列名进行重命名的操作是()
A、select()

14. DataFrame 和 RDD 最大的区别
B. 多了 schema


//Spark的四大特点
一、速度快
由于Apache Spark支持内存计算,并且通过DAG(有向无环图)
执行引擎支持无环数据流,所以官方宣称其在内存中的运算速度要比Hadoop的MapReduce快100倍,在硬盘中要快10倍
二、易于使用
Spark的版本已经更新到了Spark3.1.2(截止日期2021.06.01),
支持了包括Java、Scala、Python、R和SQL语言在内的多种语言。为了兼容Spark2.x企业级应用场景,Spark仍然持续更新Spark2版本
三、通用性强
在Spark的基础上,Spark还提供了包括Spark SQL、Spark Streaming、MLib及GraphX在内的多个工具库,
我们可以在一个应用中无缝的使用这些工具库
四、运行方式
Spark支持多种运行方式,包括在Hadoop和Mesos上,也支持Standalone的独立运行模式,同时也可以运行在云Kubernets(Spark2.3开始支持)上
对于数据源而言,Spark支持从HDFS、HBase、Cassandra及Kafka等多种途径获取和数据


15.spark的特点包括
A. 快速
B. 通用
D. 兼容性


//



//Stage
4.Stage 的 Task 的数量由什么决定
A.Partition

6.简述Spark Stage 的划分原理
Job->Stage->Task
开发完一个应用以后,把这个应用提交到Spark集群,这个应用叫Application。
这个应用里面开发了很多代码,这些代码里面凡是遇到一个action操作,就会产生一个job任务。
一个Application有一个或多个job任务。job任务被DAGScheduler划分为不同stage去执行,stage是一组Task任务。
Task分别计算每个分区partition上的数据,Task数量=分区partition数量。

Spark如何划分Stage:
会从执行action的最后一个RDD开始向前推,首先为最后一个RDD创建一个stage,
向前遇到某个RDD是宽依赖,再划分一个stage。如下图,从宽依赖处划分为2个stage。
原理的应用场景:
1.通过监控界面上每个stage及其内部task运行情况,找到对应的代码段做性能调优。
2.指定RDD的分区数参数,实际也调整了task的数量,在数据量较大时适当调整增加并行度。


3. Task 运行在下来哪里个选项中 Executor 上的工作单元
C. worker node

16. Spark driver的功能是什么
A. 是作业的主进程
B. 负责了作业的调度
D. 负责作业的解析

17. SparkContext可以从哪些位置读取数据
A.本地磁盘
C.hdfs
D.内存

19. 要读取people.json文件生成DataFrame,可以使用下列那些命令
A. spark.read.json("people.json")
C. spark.read.format("json").load("people.json")

20. 从RDD转换得到DataFrame包含两种典型的方法,分别是
A.利用反射机制推断RDD模式
B.使用编程方式定义RDD模式



简述Spark的架构与作业提交流程
1.Spark 部署方式
主要是说明一下Spark 的架构,和Spark on yarn client 和 spark on yarn cluster的架构

2.请列举Spark的action算子,并简述功能(5个以上)
1.collect  :  将数据从各个Executor节点中收集到Driver端
2.foreach : 分布式遍历RDD中数据
3.count : 统计数RDD中据个数
4.take :显示前N个RDD的元素
5.saveAsTextFile : 将RDD数据保存到集群或者本地,文件格式为txt格式。
6.reduce :指定聚合逻辑,对RDD所有元素进行聚合操作
7.aggregare : 分别指定分区内与分区间聚合逻辑,对RDD所有元素进行聚合操作
8. countByKey : 统计RDD中所有Key出现的次数
9.countByvalue : 统计RDD中各个元素值出现的次数。

3.Repartition和Coalesce关系与区别
repartition和coalesce两个都是对RDD的分区进行重新划分,
repartition只是coalesce接口中shuffle为true的简易实现。
假设RDD有N个分区,需要重新划分成M个分区,有以下几种情况
1.N小于M
一般情况下,N个分区有数据分布不均匀的状况,利用hashPartitioner函数将数据重新分区为M个,
这时需要将shuffle设置为true。
2.N大于M且和M相差不多
假如N是1000,M是100,那么久可以将N个分区中的若干个分区合并成一个新的分区,
最终合并为M个分区,这时可以将shuffle设置为false,在shuffle为false的情况下,
如果M>N时,coalesce为无效的,不进行shuffle过程,父RDD和子RDD之间是窄依赖关系。
3.N大于M且和M相差悬殊
这时如果将shuffle设置为false,父子RDD是窄依赖关系,他们同处在一个stage中,
就可能造成spark程序的并行度不够,从而影响性能,如果在M为1的时候,
为了时coalesce之前的操作有更好的并行度,可以将shuffle设置为true。
总之,如果shuffle为false时,如果传入的参数大于现有的分区数目,RDD的分区数不变,
就是说不经过shuffle,是无法将RDD的分区数变多的。


4.map和mappartitions的区别
1、数据处理角度
Map 算子是分区内一个数据一个数据的执行,类似于串行操作。而 mapPartitions 算子是以分区为单位进行批处理操作。
2、 功能的角度
Map 算子主要目的将数据源中的数据进行转换和改变。但是不会减少或增多数据。
MapPartitions 算子需要传递一个迭代器,返回一个迭代器,没有要求的元素的个数保持不变,所以可以增加或减少数据。
3、性能的角度
Map 算子因为类似于串行操作,所以性能比较低,而是 mapPartitions 算子类似于批处理,所以性能较高。
但是 mapPartitions 算子会长时间占用内存,那么这样会导致内存可能不够用,出现内存溢出的错误。所以在内存有限的情况下,不推荐使用 mapPartitions,可使用 map 操作。

5.简述Spark中共享变量(广播变量和累加器)的基本原理与用途
广播变量:共享只读变量
基本原理是:将变量所有数据拉取到Driver端,然后制作副本发送到给各个Executor储存内存中,共享给各个Task读取。
它的用途是:在join时,可以将小表作为广播变量发送到各个Executor中,然后与大表的数据进行本地map join,从而使得执行性能提升。

累加器:共享只写变量
其原理是:在Driver端定义一个累加器,然后累加器制作副本发送到各个Executor内存中,然后经过计算之后得到一个中间结果,多个Executor端将中间结果返回给Driver端,进行最终聚合操作。
它的用途:可用于在不同节点之间进行累加计算,例如求和操作。



7.Spark中的缓存机制cache与checkpoint机制,并指出两者的区别与联系
cache缓存和checkpoint都是Spark的缓存机制,将RDD的结果缓存到内存或者磁盘中,以供后面调用。
区别是:
cache缓存不切断血缘关系,而checkepoint切断血缘关系。
cache是缓存到内存中或者磁盘临时文件中,安全性低,checkpoint是持久化到高可用的分布式文件系统或者本地,安全性高。
cache缓存的是当前RDD已经计算出的结果,而checkpoint为了保证数据的准确性,会从头开始计算得到结果后再缓存,也就是说cache时RDD链只计算一次,而checkpoint时RDD链需要计算两次。

8.简述Spark 中 RDD、DataFrame、DataSet三者之间的区别与三者之间怎么相互转换
RDD<=>DF
定义schema信息后,rdd.DF(schema)
DF.toRDD

RDD<=>DS
首先定义一个样例类,参数中明确字段名称、字段类型
RDD首先结构转换为kv类型的数据
然后RDD.toDS(样例类名)
DS.toRDD

DF<=>DS
DF.toDS(样例类名)
DS.toDF

9.简述Spark 执行一条SQL,主要经过了那些阶段(结合Catalyst描述)
7.Spark SQL 原理[4.Spark Catalyst优化器详解]

10.简述Spark AQE 和 Spark DPP 原理和使用场景
AQE:Spark的动态优化机制
3.0版本主要有以下几个内容:自动分区合并、自动join倾斜处理、动态调整join策略
自动分区合并:
其原理是针对小分区进行合并,从而减少task数量,进而导致输出文件数量减少。
用户需要设置合并后分区尺寸推荐值以及合并后分区数量最小值。
常用于输出小文件过多、task数量过多而数据量较少的场景。

自动join倾斜处理:
其原理是针对分区尺寸进行判断,当判断为倾斜分区时,就将分区拆分多个小分区,从而避免task粒度的数据倾斜
判断依据是两个值,分区尺寸大于这两个值,那么就为倾斜分区
①用户设定的最低阈值
②分区尺寸正序排序后的中位数分区尺寸*用户设定的比例系数
此功能常用于出现task粒度的数据倾斜时,开启以解决。

动态调整join策略:
其原理是,一开始join策略并非BHJ,随着查询的进行,在join之前,当表容量小于broadcast join 设定的阈值时,将join策略自动降级为broadcast join 
此功能常用于当大小表join且小表尺寸大于BHJ阈值的场景,开启此功能后,后续一旦小表数据经过过滤后尺寸小于阈值,即可自动修改BHJ,提高join性能。

DPP:动态分区裁剪
其原理是两表join时,将一个表的过滤条件套用在另一张表上,使得join的数据量减少,且不影响结果准确性。
其适用于两个分区表join且分区字段在关联条件中且其中一张表存在过滤条件的场景。

1.对于Spark 参数的优化,主要有哪些参数,它们对应调优的场景是什么？(5个以上)
①调整reduce端并行度
CPU利用率低说明其处理的每个task数据量过大,例如最大能处理4个task,但是两个task就把Executor内存占满,从而导致剩下两个CPU空转。
此时增大并行度,使得task数据量变小
针对CPU利用率较低的场景。

②开启堆外内存、设置cache缓存优先使用堆外内存、设置缓存压缩
可减少Executor端堆内内存溢出情况

③增加shuffle write溢写时缓冲大小、shuffle read缓冲容量大小
提高溢写效率、增加read阶段数据吞吐量,减少拉取数据次数,各场景均适用。

④增加任务失败后重试次数、重试时间间隔
增加重试次数与重试间隔后,能够使得任务稳定性增加,适用于网络经常波动导致任务失败次数过多时的调优

2.对于Spark SQL来说,大表关联小表的优化方式有哪些？(分多场景作答,小表可广播、小表太大不能广播)
当小表读到内存后,表容量小于广播变量阈值时,那么程序在join时,会自动选择broadcast hash join策略。
当大于阈值不多的时候,我们可以人为的评估一下,采用广播变量是否可行,表容量是否超过executor的储存内存,若没超过则可以选择强制广播
当大于阈值过多不能广播的时候,那么可以指定join策略为shuffle hash join
某些特定的需求,当小表超过阈值过多时,也可以人为的减少数据量来使得其满足broadcast的需求: 例如对大表进行维度补充。
维度表事实表重复字段很多时,可以选择将关联字段全部用concat连接起来,再用MD5进行加密,使得字段值从一个较长的字符串变成一个较短的字符串,此时对小表建立一个子查询,里面仅有关联字段和待补充进维度表的字段,使得数据量减少,从而触发broadcast join。
针对大小表优化,3.0版本可以开启AQE功能中的join策略自动调整,使得符合要求的自动被优化。

3.对于Spark SQL来说,大表关联大表的优化方式有哪些？
大表 join 大表
先看是不是有数据倾斜情况,针对两个表的关联字段进行抽样。
若不存在倾斜情况:
①可以分而治之
大表一般是有分区字段的,可以根据分区字段对内表进行拆分,然后将每一部分跟大表join,使得join策略为broadcast join 
最后再将每部分跟外表join的结果联合起来。形成最终结果。

若内/外表存在数据倾斜情况:
①分而治之+两阶段shuffle
抽样将大key定位出来
然后对原表筛选,变成俩部分:1A是仅有大key的表,2A是不存在大key的表
然后将2A跟另一张表B进行join 
再将1A跟表B进行join,join时需要加盐和减盐操作
最后再将结果union联合起来。

4.举例说明Spark 数据倾斜有哪些场景,对应的解决方案是什么？
即某个或多个task的数据量过大,导致执行速度过慢的情况。
单表聚合操作出现倾斜:
可开启AQE的join自动倾斜处理
或者提高在reduce端并行度
或者两阶段聚合:对大key加随机数前缀、再去除随机数前缀后最终聚合聚合

大小表join数据倾斜:
可以避免shuffle:采用BHJ策略
可以采用分而治之:将大key加随机数与另一张表关联,再将小key与另一张表关联,最后结果union

大表与大表join产生的数据倾斜:
分而治之,两阶段shuffle

5.Spark 的OOM会发生在Spark 那个组件上?对应的解决方案是？
一般发生在Driver端
当拉取数据到river端时,可能导致数据量太大从而使得OOM,例如RDD的collect算子
解决方案:
①增加Driver端内存
②开启堆外内存
③调整river端执行内存和储存内存的比例




val ipSchema = "ip_start string,ip_end string,long_ip_start long,long_ip_end long,country string,province string"
val ipDF = spark.read.schema(ipSchema).option("header", true).csv("/user/ds_teacher/raw/spark_test/ip_china.csv")

ipDF.createOrReplaceTempView("view_ip_china")

spark.sql(
  """
    |create table ds_stu5.ods_ip_china_xb
    |(
    |ip_start string,
    |ip_end string,
    |long_ip_start long,
    |long_ip_end long,
    |country string,
    |province string
    |)
    |stored as parquetfile
    |
    |""".stripMargin)

spark.sql(
  """
    |insert overwrite table ds_stu5.ods_ip_china_xb
    |select * from view_ip_china
    |
    |""".stripMargin)

///
val loginSchema = "logtime string,account_id long,ip string"
val loginDF = spark.read.schema(loginSchema).option("header", true).csv("/user/ds_teacher/raw/spark_test/login_data.csv")

loginDF.createOrReplaceTempView("view_login_data")

spark.sql(
  """
    |create external table ds_stu5.ods_login_data_xb
    |(
    |logtime string,
    |account_id long,
    |ip string
    |)
    |partitioned by (dt string)
    |stored as parquetfile
    |location '/user/hive/warehouse/ds_stu5.db/ods_login_data_xb'
    |""".stripMargin)

spark.conf.set("hive.exec.dynamic.partition.mode", "nonstrict")

spark.sql(
  """
    |insert overwrite table ds_stu5.ods_login_data_xb partition(dt)
    |select logtime,account_id, ip, date_format(logtime,'yyyy-MM-dd') dt from view_login_data
    |
    |""".stripMargin)

///
val ipRangeAndLocation = spark.sql(
  """
    |
    |select
    |long_ip_start,
    |long_ip_end,
    |country,
    |province
    |from
    |ds_stu5.ods_ip_china_xb
    |
    |""".stripMargin).collect()


def ipToLong(ip: String): Long = {
  // aaa.bbb.ccc.ddd
  val fourParts: Array[String] = ip.split("[.]")

  val aaa: Long = fourParts(0).toLong * 256 * 256 * 256
  val bbb: Long = fourParts(1).toLong * 256 * 256
  val ccc: Long = fourParts(2).toLong * 256
  val ddd: Long = fourParts(3).toLong
  val long_ip: Long = aaa + bbb + ccc + ddd
  long_ip
}

import scala.collection.mutable
val startEndCountryProvince = mutable.Buffer[(Long, Long, String, String)]()

for (row <- ipRangeAndLocation) {
  val str: String = row.toString()
  val subStr: String = str.substring(1, str.length - 1)
  val llss: Array[String] = subStr.split(",")
  startEndCountryProvince.append((llss(0).toLong, llss(1).toLong, llss(2), llss(3)))
}
val ipRangeArr: Array[(Long, Long, String, String)] = startEndCountryProvince.toArray

import org.apache.spark.broadcast.Broadcast
val bc: Broadcast[Array[(Long, Long, String, String)]] = spark.sparkContext.broadcast(ipRangeArr)


def longIpMatchCountry(long_ip: Long): String = {

  val arr: Array[(Long, Long, String, String)] = bc.value
  var low: Int = 0
  var high: Int = arr.length - 1
  while (low <= high) {
    val mid: Int = (low + high) / 2

    if ((long_ip >= arr(mid)._1) && (long_ip <= arr(mid)._2)) {

      return arr(mid)._3
    }

    if (long_ip < arr(mid)._1) {
      high = mid - 1
    } else {
      low = mid + 1
    }
  }
  "CannotMatchCountry"
}

def longIpMatchProvince(long_ip: Long): String = {

  val arr: Array[(Long, Long, String, String)] = bc.value
  var low: Int = 0
  var high: Int = arr.length - 1
  while (low <= high) {
    val mid: Int = (low + high) / 2

    if ((long_ip >= arr(mid)._1) && (long_ip <= arr(mid)._2)) {

      return arr(mid)._4
    }

    if (long_ip < arr(mid)._1) {
      high = mid - 1
    } else {
      low = mid + 1
    }
  }
  "CannotMatchProvince"
}

spark.udf.register("ipToLong", ipToLong _)
spark.udf.register("longIpMatchCountry", longIpMatchCountry _)
spark.udf.register("longIpMatchProvince", longIpMatchProvince _)

spark.sql(
  """
    |insert overwrite table ds_stu5.ods_login_data_xb partition(dt)
    | select
    |   logtime,
    |   account_id,
    |   ip,
    |   longIpMatchCountry(ipToLong(ip)) country,
    |   longIpMatchProvince(ipToLong(ip)) province,
    |   dt
    | from
    |  ds_stu5.ods_login_data_xb
    |""".stripMargin)

///
spark.sql(
  """
    |select
    |   dt,
    |   province,
    |   count(distinct account_id) cnt_login
    |from
    |   ds_stu5.ods_login_data_xb
    |group by dt,province
    |order by dt
    |
    |""".stripMargin).show(100,false)

///
spark.sql(
  """
    |with login_info as
    |(select
    |    province,
    |    case when rn == 1 then account_id else "" end as account_id_1,
    |    case when rn == 1 then login_time else "" end as login_time_1,
    |    case when rn == 2 then account_id else "" end as account_id_2,
    |    case when rn == 2 then login_time else "" end as login_time_2,
    |    case when rn == 3 then account_id else "" end as account_id_3,
    |    case when rn == 3 then login_time else "" end as login_time_3
    |from
    |
    |(
    |    select
    |       province,
    |       account_id,
    |       logtime as login_time,
    |       rn
    |       from
    |    (
    |       select
    |          province,
    |          account_id,
    |          logtime,
    |          row_number()over(partition by province order by logtime) rn
    |       from
    |          ds_stu5.ods_login_data_xb
    |    ) rank_table
    |    where rn <=3
    |) earliestTop3
    |)
    |select
    |   province,
    |   max(account_id_1) account_id_1,
    |   max(login_time_1) login_time_1,
    |   max(account_id_2) account_id_2,
    |   max(login_time_2) login_time_2,
    |   max(account_id_3) account_id_3,
    |   max(login_time_3) login_time_3
    |from
    |   login_info
    |   where province <> "CannotMatchProvince"
    |   group by province
    |
    |
    |""".stripMargin).show (100,false)





