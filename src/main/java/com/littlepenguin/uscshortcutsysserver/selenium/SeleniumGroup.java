package com.littlepenguin.uscshortcutsysserver.selenium;

import io.netty.util.concurrent.DefaultThreadFactory;
import lombok.Getter;

import java.util.HashSet;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author littlepenguin
 * @date 2022/05/04
 * @version 1.0
 * selenium.SeleniumGroup 工作组，内部有工人SeleniumWorker
 */
public class SeleniumGroup {
    /**
     * @date 2022/05/04
     * selenium.SeleniumWorker 工人计数器 就绪的工人数并不用关心，因为一旦干活就是所有工人一起干活
     */
    private final AtomicInteger workerCounter = new AtomicInteger(0);

    public int getWorkerCounter(){
        return workerCounter.get();
    }
    /**
     * 默认的线程工厂 new DefaultThreadFactory("Selenium");
     */
    private final ThreadFactory threadFactory;
    /**
     * 存放seleniumWorker的集合
     */
    @Getter
    private final HashSet<SeleniumWorker> workers;
    /**
     * 保护hashSet，线程安全
     */
    private final ReentrantLock hashSetLock;
    /**
     * 领导线程
     */
    private final Thread leadThread;
    /**
     * 生产目标数量
     */
    private AtomicInteger targetNumber;

    public void setTargetNumber(int targetNumber) {
        this.targetNumber = new AtomicInteger(targetNumber);
    }

    /**
     * 工人类
     */
    public class SeleniumWorker
            extends AbstractQueuedSynchronizer
            implements Runnable{
        final Thread thread;
        final long birthTime;
        SeleniumWorker(){
            birthTime = System.currentTimeMillis();
            //-1表示新建，未工作过。初始化成功则为0，此时也为上锁状态
            setState(-1);
            //新线程产生
            thread = threadFactory.newThread(this);
        }

        public void start() {
            thread.start();
        }

        public void interrupt() {
            thread.interrupt();
        }

        public boolean isInterrupted() {
            return thread.isInterrupted();
        }
        @Override
        public void run() {
            runWorker(this);
        }

        //只有工作上锁状态才算被持有
        @Override
        protected boolean isHeldExclusively() {
            return getState() > 0;
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
        public boolean isWorking() { return isLocked(); }
    }

    /**
     * 工人工作调用该方法
     * @param seleniumWorker 工人对象
     */
    private void runWorker(SeleniumWorker seleniumWorker) {
        //init逻辑
        InitWorker();
        //init完毕后状态为0，只有0才是空闲状态
        seleniumWorker.unlock();
        //runGetSK即初始化获取SK逻辑
        work(seleniumWorker);
    }

    private void InitWorker() {
        //不需要获取锁，因为在init中也可以被删除
        //TODO init逻辑
    }

    private void work(SeleniumWorker worker) {
        //等待循环
        WAIT:
        for (;;) {
            LockSupport.park();

            //生产循环
            worker.lock();
            try {
                //workers可能已经被移除了,但是中断信号还没发出
                //如果中断了，直接返回
                if (!workers.contains(worker) ||
                        worker.isInterrupted()) {
                    return;
                }
                while (targetNumber.get() > 0) {
                    //TODO SK获取逻辑
                    //先减数，再干活，可以最好地防止干超
                    int now = targetNumber.get();
                    boolean flag = false;
                    while (now > 0 &&
                            (flag = true) &&
                            !targetNumber.compareAndSet(now, now - 1)) {
                        now = targetNumber.get();
                        flag = false;
                    }
                    if (targetNumber.get() > 0) {
                        //此时flag没用，肯定要生产一个
                        //TODO getSK
                    }
                    //此时只有flag为true 才可以生产最后一个，并且通知领导
                    //如果有多个线程抢最后一个SK flag=true 的是最后一个可以完成工作的那个，其在compareAndSet成功后跳出
                    //其他的再进入循环 在下轮now>0跳出 此时flag=false
                    else if (flag) {
                        //完成者,通知领导
                        LockSupport.park(leadThread);
                    }
                    //任务完成（targetNumber==0）
                    continue WAIT;
                }
            }
            finally {
                worker.unlock();
            }
        }
    }

    /**
     * 添加工人
     */
    public void addWorker(){
        //先加数再真正加Worker,因为线程启动相对加数比较慢
        CAS_ADD:
        for(;;){
            int c = workerCounter.get();
            if(workerCounter.compareAndSet(c,c+1)){
                break CAS_ADD;
            }
        }
        SeleniumWorker w = new SeleniumWorker();
        //防止hashSet线程不安全

        hashSetLock.lock();
        try {
            workers.add(w);
        }
        finally {
            hashSetLock.unlock();
        }
        w.start();
    }

    /**
     * 删除工人
     */
    public void deleteWorker(SeleniumWorker w) {
        CAS_DEL:
        for(;;){
            int c = workerCounter.get();
            if(workerCounter.compareAndSet(c,c-1)){
                break CAS_DEL;
            }
        }
        //防止hashSet线程不安全

        hashSetLock.lock();
        try {
            workers.remove(w);
        }
        finally {
            hashSetLock.unlock();
        }

        //释放空闲工人
        if (!w.isInterrupted() && w.tryLock()) {
            try {
                w.interrupt();
            } finally {
                w.unlock();
            }
        } else {
            //非空闲工人 等到工作结束会释放
            w.interrupt();
        }
    }

    /**
     * 初始化Group添加两个工人
     */
    private void initSeleniumGroup() {
        for (int i = 0; i < SeleniumLeader.CORE_WORKERS_NUMBER; i++) {
            addWorker();
        }
    }

    SeleniumGroup(Thread leadThread){
        this.threadFactory = new DefaultThreadFactory("Selenium");
        this.workers = new HashSet<>();
        this.hashSetLock = new ReentrantLock();
        this.leadThread = leadThread;
        //initSeleniumGroup() Group初始创建一定有至少两个工人
        initSeleniumGroup();
    }
}
