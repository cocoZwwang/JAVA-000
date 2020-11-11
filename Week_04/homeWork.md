

# 2、思考有多少种方式，在 main 函数启动一个新线程，运行一个方法，拿到这个方法的返回值后，退出主线程？ 写出你的方法，越多越好，提交到 Github。 

#### 使用Future

```java
private static void methodFuture(){
    ExecutorService service = Executors.newSingleThreadExecutor();
    Future<String> future = service.submit((Callable<String>) ReturnFromThreadDemo::doSomeThing);
    try {
        System.out.println("return value:" + future.get());
    } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
    }
    service.shutdown();
}
```

调用方法：

```java
//返回当前线程名称
private static void doSomeThing(CallBack callBack) {
    try {
        TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    if (callBack != null) {
        callBack.onSuccess(Thread.currentThread().getName());
    }
}
```



测试代码：

```java
public static void main(String[] args) {
    //如果5次返回的结果都正确，则通过
    for (int i = 0; i < 5; i++) {
        methodFuture();
    }
}
```

 测试结果：

```
return value:pool-1-thread-1
return value:pool-2-thread-1
return value:pool-3-thread-1
return value:pool-4-thread-1
return value:pool-5-thread-1

Process finished with exit code 0
```

调用方法，和main方法代码都基本一样，所以后面列出的方式就忽略这两部分代码了，直接贴出测试结果

#### 使用Exchanger

```java
private static void methodExchanger(){
    Exchanger<String> exchanger = new Exchanger<>();
    Thread thread = new Thread(() -> {
        try {
            //和另外一个线程交换值
            exchanger.exchange(doSomeThing());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    });
    thread.start();
    try {
        //和另外一个线程交换值
        String value = exchanger.exchange("");
        System.out.println("return value:" + value);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
}
```

测试结果：

```
return value:Thread-0
return value:Thread-1
return value:Thread-2
return value:Thread-3
return value:Thread-4

Process finished with exit code 0
```

#### 使用回调

调用方法添加回调接口:

```java
interface CallBack {
    void onSuccess(String result);
}

private static void doSomeThing(CallBack callBack) {
    try {
        TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    if (callBack != null) {
        callBack.onSuccess(Thread.currentThread().getName());
    }
}
```

```java
//使用回调获取异步线程执行结果
private static void methodCallback() {
    final CallBack callBack = result -> {
        System.out.println("return value：" + result);
    };
    Thread thread = new Thread(() -> {
        doSomeThing(callBack);
    });
    thread.start();
}
```

测试结果：

```
return value：Thread-0
return value：Thread-3
return value：Thread-2
return value：Thread-4
return value：Thread-1

Process finished with exit code 0
```



#### 使用volatile共享变量+when轮询

```java
//共享变量
private static volatile String value = "";
private static void methodVolatileAndWhen() {
    Thread thread = new Thread(() -> {
        value = doSomeThing();
    });
    thread.start();
    while ("".equals(value)) ;
    System.out.println("return value:" + value);
    value = "";
}
```

测试结果：

```java
return value:Thread-0
return value:Thread-1
return value:Thread-2
return value:Thread-3
return value:Thread-4

Process finished with exit code 0
```

#### 使用volatile共享变量 + Thread.join()

```java
private static void methodVolatileAndThreadJoin() {
    Thread thread = new Thread(() -> {
        value = doSomeThing();
    });
    thread.start();
    try {
        thread.join();
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    System.out.println("return value:" + value);
    value = "";
}
```

测试结果：

```
return value:Thread-0
return value:Thread-1
return value:Thread-2
return value:Thread-3
return value:Thread-4

Process finished with exit code 0
```

#### 使用volatile共享变量 + CountDownLatch

```java
private static void methodVolatileAndCountDownLatch() {
    CountDownLatch cdl = new CountDownLatch(1);
    Thread thread = new Thread(() -> {
        value = doSomeThing();
        cdl.countDown();
    });
    thread.start();
    try {
        cdl.await();
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    System.out.println("return value:" + value);
    value = "";
}
```

 测试结果：

```
return value:Thread-0
return value:Thread-1
return value:Thread-2
return value:Thread-3
return value:Thread-4

Process finished with exit code 0
```

#### 使用volatile共享变量 + CyclicBarrier

```java
private static void methodVolatileAndCyclicBarrier() {
    CyclicBarrier clb = new CyclicBarrier(2);
    Thread thread = new Thread(() -> {
        value = doSomeThing();
        try {
            clb.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    });
    thread.start();
    try {
        clb.await();
    } catch (InterruptedException | BrokenBarrierException e) {
        e.printStackTrace();
    }
    System.out.println("return value:" + value);
    value = "";
}
```

 测试结果：

```
return value:Thread-0
return value:Thread-1
return value:Thread-2
return value:Thread-3
return value:Thread-4

Process finished with exit code 0
```

#### 使用volatile共享变量 + LockSupport

```java
private static void methodVolatileAndLockSupport() {
    final Thread mainThread = Thread.currentThread();
    Thread thread = new Thread(() -> {
        value = doSomeThing();
        LockSupport.unpark(mainThread);
    });
    thread.start();
    LockSupport.park();
    System.out.println("return value:" + value);
    value = "";
}
```

测试结果：

```
return value:Thread-0
return value:Thread-1
return value:Thread-2
return value:Thread-3
return value:Thread-4

Process finished with exit code 0
```

#### 使用volatile共享变量 + Semaphore

```java
private static void methodVolatileAndSemaphore() {
    Semaphore semaphore = new Semaphore(1);
    Thread thread = new Thread(() -> {
        try {
            value = doSomeThing();
            semaphore.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    });
    try {
        semaphore.acquire();
        thread.start();
        semaphore.acquire();
        System.out.println("return value:" + value);
        semaphore.release();
    } catch (InterruptedException e) {
        e.printStackTrace();
    }
    value = "";
}
```

测试结果：

```
return value:Thread-0
return value:Thread-1
return value:Thread-2
return value:Thread-3
return value:Thread-4

Process finished with exit code 0
```

#### 使用volatile共享变量 + synchronized + obj.wait()/notifyAll()

```java
private static void methodVolatileAndWait() {
    final byte[] lock = new byte[0];
    Thread thread = new Thread(() -> {
        synchronized (lock){
            value = doSomeThing();
            lock.notifyAll();
        }
    });
    synchronized (lock){
        thread.start();
        try {
            lock.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    System.out.println("return value:" + value);
    value = "";
}
```

测试结果：

```
return value:Thread-0
return value:Thread-1
return value:Thread-2
return value:Thread-3
return value:Thread-4

Process finished with exit code 0
```

#### 使用volatile共享变量 + Condition

 ```java
private static void methodLockCondition(){
    ReentrantLock lock = new ReentrantLock();
    Condition mainCondition = lock.newCondition();
    Thread thread = new Thread(() -> {
        lock.lock();
        try {
            value = doSomeThing();
            mainCondition.signal();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    });
    lock.lock();
    try {
        thread.start();
        mainCondition.await();
        System.out.println("return value:" + value);
    } catch (InterruptedException e) {
        e.printStackTrace();
    }finally {
        lock.unlock();
    }
    value = "";
}
 ```

测试结果：

```
return value:Thread-0
return value:Thread-1
return value:Thread-2
return value:Thread-3
return value:Thread-4

Process finished with exit code 0
```

#  4、把多线程和并发相关知识带你梳理一遍，画一个脑图，截图上传到 Github 上。

[脑图地址](https://www.edrawsoft.cn/viewer/public/s/583ed084303229)

图片和源文件：Mind文件夹下

脑图总体上把老师讲的东西梳理了一边，但是还有些知识点没完善，这个后续完善，有些图片太多了，要脑图源文件点击才能看，导出的图片和网址可能看不清楚。



这周画脑图用了太多时间，只做了必选题，其他的选作题没做了。。。

