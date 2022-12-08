//算子
转换算子
	Value数据类型的Transformation算子，这种变换不触发提交作业，针对处理的数据项是Value型的数据。
	Key-Value数据类型的Transformation算子，这种变换不触发提交作业，针对处理的数据项是Key-Value型的数据。

行动算子
Action算子—无输出 
Action算子—HDFS 
Action算子—Scala集合及数据类型

Value类型
map ☆☆☆
map是对RDD中的每个元素都执行一个指定函数来产生一个新的RDD。
任何原RDD中的元素在新RDD中都有且只有一个元素与之对应，将处理的数据逐条进行映射转换，
这里的转换可以是类型的转换，也可以是值的转换。
val dataRDD1: RDD[Int] = dataRDD.map(num => num * 2)  

mapPartitions☆☆☆
mapPartitions是map的一个变种。
map的输入函数应用于RDD中的每个元素，
而mapPartitions的输入函数应用于每个分区，也就是把每个分区中的内容作为整体来处理的。
val dataRDD1: RDD[Int] = dataRDD.mapPartitions( datas => datas.filter(_==2) )



mapPartitionsWithIndex☆
将待处理的数据以分区为单位发送到计算节点进行处理，
这里的处理是指可以进行任意的处理，哪怕是过滤数据，在处理时同时可以获取当前分区索引。
val dataRDD1 = dataRDD.mapPartitionsWithIndex( (index, datas) => {  
datas.map(index, _)  
}) 

flatMap☆☆
与map类似，将原RDD中的每个元素通过函数f转换为新的元素，并将这些元素放入一个集合，构成新的RDD，
将处理的数据进行扁平化后再进行映射处理，所以算子也称之为扁平映射
val dataRDD1 = dataRDD.flatMap( list => list) 
data.map(_.split(";")).flatMap(x=>{
      for(i<-0 until x.length-1) yield (x(i)+","+x(i+1),1)
}).reduceByKey(_+_).foreach(println)


glom☆
将同一个分区的数据直接转换为相同类型的内存数组进行处理，分区不变
val dataRDD1:RDD[Array[Int]] = dataRDD.glom()

groupBy☆☆☆
将数据根据指定的规则进行分组, 分区默认不变，但是数据会被打乱重新组合，
我们将这样的操作称之为shuffle。极限情况下，数据可能被分在同一个分区中
一个组的数据在一个分区中，但是并不是说一个分区中只有一个组。
val dataRDD1 = dataRDD.groupBy(  
_%2  
) 

filter☆☆☆
将数据根据指定的规则进行筛选过滤，符合规则的数据保留，不符合规则的数据丢弃。
当数据进行筛选过滤后，分区不变，但是分区内的数据可能不均衡，生产环境下，可能会出现数据倾斜
val dataRDD1 = dataRDD.filter(_%2 == 0) 

sample☆
根据指定的规则从数据集中抽取数据
val dataRDD1 = dataRDD.sample(false, 0.5)

distinct☆☆☆
将数据集中重复的数据去重
val dataRDD1 = dataRDD.distinct()

coalesce☆☆☆
根据数据量缩减分区，用于大数据集过滤后，提高小数据集的执行效率
当 spark 程序中，存在过多的小任务的时候，可以通过 coalesce 方法，收缩合并分区，减少分区的个数，减小任务调度成本
val dataRDD1 = dataRDD.coalesce(2)

repartition☆☆☆
该操作内部其实执行的是 coalesce 操作，参数 shuffle 的默认值为 true。
无论是将分区数多的RDD 转换为分区数少的RDD，还是将分区数少的 RDD 转换为分区数多的RDD，
repartition 操作都可以完成，因为无论如何都会经 shuffle 过程。
val dataRDD1 = dataRDD.repartition(4) 

sortBy☆☆☆
该操作用于排序数据。在排序之前，可以将数据通过 f 函数进行处理，
之后按照 f 函数处理的结果进行排序，默认为升序排列。
排序后新产生的 RDD 的分区数与原RDD 的分区数一致。中间存在 shuffle 的过程
val dataRDD1 = dataRDD.sortBy(num=>num, false, 4)

双Value数据类型
intersection☆☆
对源RDD 和参数RDD 求交集后返回一个新的RDD
val dataRDD = dataRDD1.intersection(dataRDD2) 

union☆☆
对源RDD 和参数RDD 求并集后返回一个新的RDD
val dataRDD = dataRDD1.union(dataRDD2) 

subtract☆☆
以一个 RDD 元素为主，去除两个 RDD 中重复元素，将其他元素保留下来。求差集
val dataRDD = dataRDD1.subtract(dataRDD2)

zip☆
将两个 RDD 中的元素，以键值对的形式进行合并。
其中，键值对中的Key 为第 1 个 RDD中的元素，Value 为第 2 个 RDD 中的相同位置的元素。
val dataRDD = dataRDD1.zip(dataRDD2)

cartesian☆
笛卡尔操作,对输入RDD内的所有元素计算笛卡尔积
x.cartesian(y).collect

Key-Value数据类型

1．输入输出分区1对1
mapValues☆
顾名思义就是输入函数应用于RDD中KV（Key-Value）类型元素中的Value，
原RDD中的Key保持不变，与新的Value一起组成新的RDD中的元素。
因此，该函数只适用于元素为Key-Value对的RDD
b.mapValues("x" + _ + "x").collect

2．聚集操作 
partitionBy☆
将数据按照指定Partitioner 重新进行分区。Spark 默认的分区器是HashPartitioner
val rdd2: RDD[(Int, String)] = rdd.partitionBy(new HashPartitioner(2))

reduceByKey☆☆☆
可以将数据按照相同的Key 对Value 进行聚合
val dataRDD3 = dataRDD1.reduceByKey(_+_, 2)

groupByKey☆☆☆
可以将数据按照相同的Key 对Value 进行聚合
val dataRDD2 = dataRDD1.groupByKey() 

aggregateByKey☆
将数据根据不同的规则进行分区内计算和分区间计算
取出每个分区内相同 key 的最大值然后分区间相加
val dataRDD2 = dataRDD1.aggregateByKey(0)(_+_,_+_)

foldByKey☆
当分区内计算规则和分区间计算规则相同时，aggregateByKey 就可以简化为foldByKey
val dataRDD2 = dataRDD1.foldByKey(0)(_+_)

combineByKey☆
最通用的对key-value 型 rdd 进行聚集操作的聚集函数（aggregation function）。
类似于aggregate()，combineByKey()允许用户返回值的类型与输入不一致。
val combineRdd: RDD[(String, (Int, Int))] = input.combineByKey( (_, 1),  
(acc: (Int, Int), v) => (acc._1 + v, acc._2 + 1),  
(acc1: (Int, Int), acc2: (Int, Int)) => (acc1._1 + acc2._1, acc1._2 + acc2._2)  
)


sortByKey☆
在一个(K,V)的 RDD 上调用，K 必须实现 Ordered 接口(特质)，返回一个按照 key 进行排序的
val sortRDD1: RDD[(String, Int)] = dataRDD1.sortByKey(false)

cogroup☆
在类型为(K,V)和(K,W)的RDD 上调用，返回一个(K,(Iterable<V>,Iterable<W>))类型的迭代器
val value: RDD[(String, (Iterable[Int], Iterable[Int]))] = dataRDD1.cogroup(dataRDD2)

3．连接
join☆☆
本质是对两个含有KV对元素的RDD进行coGroup算子协同划分，
再通过flatMapValues将合并的数据分散。在类型为(K,V)和(K,W)的RDD 上调用，
返回一个相同 key 对应的所有元素连接在一起的(K,(V,W))的RDD
rdd.join(rdd1).collect().foreach(println) 

leftOuterJoin☆
相当于在join基础上判断一侧的RDD是否为空，如果为空，则填充空，如果有数据，则将数据进行连接计算，然后返回结果。类似于 SQL 语句的左外连接
val rdd: RDD[(String, (Int, Option[Int]))] = dataRDD1.leftOuterJoin(dataRDD2) 

行动算子☆☆☆
Action算子—无输出 
Action算子—HDFS 
Action算子—Scala集合及数据类型
reduce☆
聚集RDD 中的所有元素，先聚合分区内数据，再聚合分区间数据
val reduceResult: Int = rdd.reduce(_+_) 

collect☆☆☆
collect:将RDD分散存储的元素转换为单机上的Scala数组并返回，类似于toArray功能
rdd.collect().foreach(println)


count☆☆☆
返回RDD 中元素的个数
val countResult: Long = rdd.count() 

first☆
返回RDD 中的第一个元素
val firstResult: Int = rdd.first() 

take☆☆
返回一个由RDD 的前 n 个元素组成的数组
val takeResult: Array[Int] = rdd.take(2) 

takeOrdered☆
返回该 RDD 排序后的前 n 个元素组成的数组
val result: Array[Int] = rdd.takeOrdered(2) 

aggregate☆☆☆
允许用户对RDD使用两个不同的reduce函数，
第一个reduce函数对各个分区内的数据聚集，每个分区得到一个结果。
第二个reduce函数对每个分区的结果进行聚集，最终得到一个总的结果。
aggregate相当于对RDD内的元素数据归并聚集，且这种聚集是可以并行的。而fold与reduced的聚集是串行的。
val result: Int = rdd.aggregate(0)(_ + _, _ + _)     //10

fold☆
与reduce类似，不同的是每次对分区内的value聚集时，分区内初始化的值为zero value
val foldResult: Int = rdd1.fold(0)(_+_)

countByKey☆
统计每种 key 的个数
val result: collection.Map[Int, Long] = rdd.countByKey() 

save 相关算子☆☆☆
    saveAsTextFile
    saveAsTextFile:函数将RDD保存为文本至HDFS指定目录，每次输出一行


    saveAsObjectFile
    将RDD分区中每10个元素保存为一个数组并将其序列化，映射为（null, BytesWritable（Y））的元素，以SequenceFile的格式写入HDFS
// 保存成 Text 文件  
rdd1.saveAsTextFile("output")  
  
// 序列化成对象保存到文件  
rdd1.saveAsObjectFile("output1") 


foreach☆☆☆
是对RDD中的每个元素执行无参数的f函数，返回Unit
rdd1.map(num=>num).collect().foreach(println) 




