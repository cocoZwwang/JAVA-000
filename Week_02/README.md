# 课后总结和作业

## 第一题：使用 GCLogAnalysis.java 自己演练一遍串行 / 并行 /CMS/G1 的案例。

下面我会从日志总体分析、挑选个别日志详细分析、总体图表分析三个步骤分别对串行GC、并行GC、CMS和G1GC进行分析。

- 使用串行GC

  ```cm
  java -XX:+UseSerialGC -Xms512m -Xmx512m -Xloggc:SerialGC.log  -XX:+PrintGCDetails -XX:+PrintGCDateStamps GCLogAnalysis
  ```

  日志总体分析

  > ...表示中间省略了一些GC的记录，完整的记录在同文件夹下的SerialGC.log文件

   ```powershell
  Java HotSpot(TM) 64-Bit Server VM (25.121-b13) for windows-amd64 JRE (1.8.0_121-b13), built on Dec 12 2016 18:21:36 by "java_re" with MS VC++ 10.0 (VS2010)
  Memory: 4k page, physical 16703404k(9489124k free), swap 21159852k(10506880k free)
  CommandLine flags: -XX:InitialHeapSize=536870912 -XX:MaxHeapSize=536870912 -XX:+PrintGC -XX:+PrintGCDateStamps -XX:+PrintGCDetails -XX:+PrintGCTimeStamps -XX:+UseCompressedClassPointers -XX:+UseCompressedOops -XX:-UseLargePagesIndividualAllocation -XX:+UseSerialGC 
  //刚开始的时候，Minor GC只触发了Yang区的垃圾回收，但是堆总的使用量一直在上升，说明Old区不断有对象晋升上来。
  2020-10-27T17:37:55.074+0800: 0.190: [GC (Allocation Failure) 2020-10-27T17:37:55.075+0800: 0.190: [DefNew: 139776K->17472K(157248K), 0.0193327 secs] 139776K->48948K(506816K), 0.0195036 secs] [Times: user=0.00 sys=0.03, real=0.02 secs] 
  //...
  //虽然Yang区的使用量还是17471K，但是这里堆的总使用量已经上升到447923K
  2020-10-27T17:37:55.350+0800: 0.465: [GC (Allocation Failure) 2020-10-27T17:37:55.350+0800: 0.465: [DefNew: 157247K->17471K(157248K), 0.0184998 secs] 447923K->351608K(506816K), 0.0186183 secs] [Times: user=0.01 sys=0.00, real=0.02 secs] 
  
  //从这里开始频繁触发Old区的垃圾回收
  2020-10-27T17:37:55.386+0800: 0.502: [GC (Allocation Failure) 2020-10-27T17:37:55.386+0800: 0.502: [DefNew: 157247K->157247K(157248K), 0.0000225 secs]2020-10-27T17:37:55.386+0800: 0.502: [Tenured: 334137K->267976K(349568K), 0.0356503 secs] 491384K->267976K(506816K), [Metaspace: 2725K->2725K(1056768K)], 0.0357939 secs] [Times: user=0.05 sys=0.00, real=0.04 secs] 
  //...
  2020-10-27T17:37:55.799+0800: 0.915: [GC (Allocation Failure) 2020-10-27T17:37:55.799+0800: 0.915: [DefNew: 139776K->139776K(157248K), 0.0000246 secs]2020-10-27T17:37:55.799+0800: 0.915: [Tenured: 348968K->349510K(349568K), 0.0416643 secs] 488744K->358262K(506816K), [Metaspace: 2725K->2725K(1056768K)], 0.0418038 secs] [Times: user=0.03 sys=0.00, real=0.04 secs] 
  
  //Old区容量到达99%左右时候，开始触发Full GC，但是FUll GC并没有降低堆内存的使用量，因此连续发生了3次的Full GC，此时系统可以说已经出问题了。
  2020-10-27T17:37:55.859+0800: 0.974: [Full GC (Allocation Failure) 2020-10-27T17:37:55.859+0800: 0.974: [Tenured: 349510K->349563K(349568K), 0.0478513 secs] 506157K->365242K(506816K), [Metaspace: 2725K->2725K(1056768K)], 0.0479489 secs] [Times: user=0.06 sys=0.00, real=0.05 secs] 
  2020-10-27T17:37:55.926+0800: 1.041: [Full GC (Allocation Failure) 2020-10-27T17:37:55.926+0800: 1.041: [Tenured: 349563K->349317K(349568K), 0.0560678 secs] 506543K->353154K(506816K), [Metaspace: 2725K->2725K(1056768K)], 0.0561680 secs] [Times: user=0.05 sys=0.00, real=0.06 secs] 
  2020-10-27T17:37:56.001+0800: 1.116: [Full GC (Allocation Failure) 2020-10-27T17:37:56.001+0800: 1.116: [Tenured: 349461K->349535K(349568K), 0.0353033 secs] 506604K->375631K(506816K), [Metaspace: 2725K->2725K(1056768K)], 0.0354059 secs] [Times: user=0.03 sys=0.00, real=0.04 secs] 
  //程序最后的堆内存使用情况，可以看到Old区总体使用率达到了99%。
  Heap
   def new generation   total 157248K, used 31967K [0x00000000e0000000, 0x00000000eaaa0000, 0x00000000eaaa0000)
    eden space 139776K,  22% used [0x00000000e0000000, 0x00000000e1f37d40, 0x00000000e8880000)
    from space 17472K,   0% used [0x00000000e8880000, 0x00000000e8880000, 0x00000000e9990000)
    to   space 17472K,   0% used [0x00000000e9990000, 0x00000000e9990000, 0x00000000eaaa0000)
   tenured generation   total 349568K, used 349535K [0x00000000eaaa0000, 0x0000000100000000, 0x0000000100000000)
     the space 349568K,  99% used [0x00000000eaaa0000, 0x00000000ffff7d30, 0x00000000ffff7e00, 0x0000000100000000)
   Metaspace       used 2732K, capacity 4486K, committed 4864K, reserved 1056768K
    class space    used 297K, capacity 386K, committed 512K, reserved 1048576K
   ```

  - 整个过程生成了9956个对象

  - 堆最后的使用情况

    ```powershell
    Heap
     def new generation   total 157248K, used 15150K [0x00000000e0000000, 0x00000000eaaa0000, 0x00000000eaaa0000)
      eden space 139776K,  10% used [0x00000000e0000000, 0x00000000e0ecbbe8, 0x00000000e8880000)
      from space 17472K,   0% used [0x00000000e9990000, 0x00000000e9990000, 0x00000000eaaa0000)
      to   space 17472K,   0% used [0x00000000e8880000, 0x00000000e8880000, 0x00000000e9990000)
     tenured generation   total 349568K, used 349379K [0x00000000eaaa0000, 0x0000000100000000, 0x0000000100000000)
       the space 349568K,  99% used [0x00000000eaaa0000, 0x00000000fffd0c28, 0x00000000fffd0e00, 0x0000000100000000)
     Metaspace       used 2732K, capacity 4486K, committed 4864K, reserved 1056768K
      class space    used 297K, capacity 386K, committed 512K, reserved 1048576K
    ```

    - def new generation   total 157248K, used 15150K：新生代总容量是157248k（153m），使用了15150K（15M）。
    - eden space 139776K,  10% used：eden区总容量是139776k（136m），使用10%。
    - from space 17472K,   0% used / to   space 17472K,   0% used：两个交换区的容量分别都是17472K（17m），使用都是0%。
    - tenured generation   total 349568K, used 349379K：老年区总容量349568K（342m），使用了349546K（341m），用了将近99%。
    - Metaspace       used 2732K, capacity 4486K：元数据区总容量总容量4486k，使用了2732K。
    -  class space    used 297K, capacity 386K：方法区总容量386k，使用了297k。

  - GC情况

    - 一共发生了19次GC，其中有18次Minor GC，1次Full GC。
    - 前7次Minor GC只是回收了Yang区的内存。
    - 从上面图红色框框可以看出，从第8次Minor GC开始，老年代使用量的开始提高，这应该是Yang区经过前面几次Minor GC之后，部分存活对象开始满足晋升条件，开始提升到Old区。Old区从这里开始经过大概9次Minor GC后，内存使用量逐渐升高到341m左右
    - 在上面Old区满了 之后触发了FullGC，Full GC后Old区内存由 349482K->338800K，几乎没有下降，所以下一次的YGC后，再次触发了FullGC。

  - 日志分析

    这里抓取中间一条YGC日志进行详细分析。

    ```powershell
    2020-10-27T16:47:19.197+0800: [GC (Allocation Failure) 2020-10-27T16:47:19.197+0800: [DefNew: 139776K->17471K(157248K), 0.0187530 secs] 139776K->43571K(506816K), 0.0193031 secs] [Times: user=0.03 sys=0.00, real=0.02 secs]
    ```

    - 2020-10-27T16:47:19.197+0800：发生GC的时间

    - Allocation Failure：GC原因-内存申请失败

    - DefNew: 139776K->17471K(157248K), 0.0187530 secs]：

      - DefNew：表示垃圾收集器的名称。这个名字表示：年轻代使用的单线程、标记-复制、STW 垃圾收集器。

      - 139776K->17471K(157248K)：Yang区总容量是157248K，GC后由139776K下降到17471K，回收了将近87%的内存。
      -  0.0187530 secs：耗时18毫秒左右。

    - 139776K->43571K(506816K), 0.0193031 secs]：堆容量是506816K，使用量从139776K下降到43571K，耗时19毫秒左右

- 使用并行GC

  ```powershell
  
  ```

  

## 第二题：使用压测工具（wrk 或 sb），演练 gateway-server-0.0.1-SNAPSHOT.jar 示例。

## 第三题：根据上述自己对于 1 和 2 的演示，写一段对于不同 GC 的总结，提交到 Github。

## 第四题：运行课上的例子，以及 Netty 的例子，分析相关现象。

## 第五题：写一段代码，使用 HttpClient 或 OkHttp 访问 [http://localhost:8801 ](http://localhost:8801/)