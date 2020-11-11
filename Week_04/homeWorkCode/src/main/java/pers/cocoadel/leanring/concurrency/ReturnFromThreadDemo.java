package pers.cocoadel.leanring.concurrency;

import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 在 main 函数启动一个新线程，运行一个方法，拿到这个方法的返回值后，退出主线程
 */
public class ReturnFromThreadDemo {
    private static volatile String value = "";

    public static void main(String[] args) {
        for (int i = 0; i < 5; i++) {
//            methodFuture();
//            methodExchanger();
//            methodVolatileAndWhen();
//            methodCallback();
//            methodVolatileAndThreadJoin();
//            methodVolatileAndCountDownLatch();
//            methodVolatileAndCyclicBarrier();
//            methodVolatileAndLockSupport();
//            methodVolatileAndSemaphore();
//            methodVolatileAndWait();
            methodLockCondition();

        }
    }

    /**
     * 使用Future
     */
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

    /**
     * 使用Exchanger
     */
    private static void methodExchanger(){
        Exchanger<String> exchanger = new Exchanger<>();
        Thread thread = new Thread(() -> {
            try {
                exchanger.exchange(doSomeThing());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            String value = exchanger.exchange("");
            System.out.println("return value:" + value);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 使用回调
     */
    private static void methodCallback() {
        final CallBack callBack = result -> {
            System.out.println("return value：" + result);
        };
        Thread thread = new Thread(() -> {
            doSomeThing(callBack);
        });
        thread.start();
    }

    /**
     * 使用volatile共享变量+when轮询
     */
    private static void methodVolatileAndWhen() {
        Thread thread = new Thread(() -> {
            value = doSomeThing();
        });
        thread.start();
        while ("".equals(value)) ;
        System.out.println("return value:" + value);
        value = "";
    }

    /**
     * 使用volatile共享变量 + Thread.join()
     */
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

    /**
     * 使用volatile共享变量 + CountDownLatch
     */
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

    /**
     * 使用volatile共享变量 + CyclicBarrier
     */
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

    /**
     * 使用volatile共享变量 + LockSupport
     */
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

    /**
     * 使用volatile共享变量 + Semaphore
     */
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

    /**
     * 使用volatile共享变量 + Condition
     */
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

    /**
     * 使用volatile共享变量 + synchronized + obj.wait()/notifyAll()
     */
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

    private static String doSomeThing() {
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Thread.currentThread().getName();
    }

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

}
