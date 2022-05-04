package com.littlepenguin.uscshortcutsysserver.selenium;

import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author littlepenguin
 * @date 2022/05/04
 * @version 1.0
 * @Description selenium.SeleniumGroup 工作组，内部有工人SeleniumWorker
 */
public class SeleniumGroup {
    /**
     * @date 2022/05/04
     * @Description selenium.SeleniumWorker 工人
     */
    private final AtomicInteger ctl = new AtomicInteger();

    /**
     * 核心工人数为2
     */
    private final Integer CORE_WORKERS_NUMBER=2;
    /**
     * 最大工人数为5
     */
    private final Integer MAX_WORKERS_NUMBER=5;
    /**
     * 默认的线程工厂 new DefaultThreadFactory("Selenium");
     */
    private final ThreadFactory threadFactory;
    /**
     * 存放seleniumWorker的集合
     */
    private final HashSet<SeleniumWorker> workers;
    /**
     * 锁Group的主锁，锁住代表正在工作
     */
    private final ReentrantLock mainLock;
    /**
     * 没工作等待通知Condition
     */
    private final Condition noTask;
    /**
     * SeleniumGroup获取工作信号SKSignal的阻塞队列
     */
    private final BlockingQueue<SKSignal> blockingQueue;
    private class SeleniumWorker
            extends AbstractQueuedSynchronizer
            implements Runnable{
        final Thread thread;

        SeleniumWorker(){
            //-1表示新建，未工作过。初始化成功则为0，此时也为上锁状态
            setState(-1);
            //新线程产生
            thread = threadFactory.newThread(this);
            //新线程执行run方法
        }


        @Override
        public void run() {
            runWorker(this);

        }

        @Override
        protected boolean isHeldExclusively() {
            return getState() != 0;
        }

        @Override
        protected boolean tryAcquire(int unused) {
            if (compareAndSetState(0, 1)) {
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int unused) {
            setExclusiveOwnerThread(null);
            setState(0);
            return true;
        }

        public void lock()        { acquire(1); }
        public boolean tryLock()  { return tryAcquire(1); }
        public void unlock()      { release(1); }
        public boolean isLocked() { return isHeldExclusively(); }
    }

    /**
     * 信号类
     */

    /**
     * 工人工作调用该方法
     * @param seleniumWorker 工人对象
     */
    private void runWorker(SeleniumWorker seleniumWorker) {
        //init逻辑
        InitGetSK();
        //init完毕后状态为0，只有0才是空闲状态
        seleniumWorker.unlock();
        //runGetSK即初始化获取SK逻辑
        runGetSK(seleniumWorker);
    }

    private void runGetSK(SeleniumWorker seleniumWorker) {
        START:
        for(;;) {
            //workers可能已经被移除了,这种情况不需要再工作了，该worker已经被删除了
            if (!workers.contains(seleniumWorker)) {
                return;
            }
            seleniumWorker.lock();
            try {
                //主线程正在工作
                while (mainLock.isLocked()) {
                    //主线程发现taskComplete会解锁mainLock 则直接跳出
                    //TODO SK获取逻辑
                }
                //await()的作用是能够让其他线程访问竞争资源，所以挂起状态就是要释放竞争资源的锁 工作结束，释放锁然后等待下次工作
                noTask.await();
            } catch (InterruptedException e) {
                //TODO 响应中断
                e.printStackTrace();
            } finally {
                seleniumWorker.unlock();
            }
            //worker已经解锁
            continue START;
        }
    }

    private void InitGetSK() {
        //不需要获取锁，因为在init中也可以被删除
        //TODO init逻辑
    }

    /**
     * 添加工人
     */
    private void addWorker(){

    }

    /**
     * 初始化Group逻辑
     */
    private void initSeleniumGroup() {
        for (int i = 0; i < CORE_WORKERS_NUMBER; i++) {
            addWorker();
        }
    }

    SeleniumGroup(){
        threadFactory = new DefaultThreadFactory("Selenium");
        workers = new HashSet<SeleniumWorker>();
        mainLock = new ReentrantLock(false);
        noTask = mainLock.newCondition();
        blockingQueue = new LinkedBlockingQueue<SKSignal>();
        //initSeleniumGroup() Group初始创建一定有至少两个工人
        initSeleniumGroup();
    }


}
