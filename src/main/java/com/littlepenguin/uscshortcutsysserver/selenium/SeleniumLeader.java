package com.littlepenguin.uscshortcutsysserver.selenium;

import com.littlepenguin.uscshortcutsysserver.Containers;

import java.util.HashSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.locks.LockSupport;

/**
 * @author littlepenguin
 * @date 2022/05/04
 * @version 1.0
 * selenium.SeleniumLeader 领导类负责领导SeleniumGroup
 * 实际上只负责发送信号和在不暴露的情况下创建SeleniumGroup
 */
public class SeleniumLeader implements Runnable{

    /**
     * SK最大数量
     */
    public static final int MAX_SK_NUMBER = 50;
    /**
     * 核心工人数为2
     */
    public static final Integer CORE_WORKERS_NUMBER=2;
    /**
     * 最大工人数为5
     */
    private static final Integer MAX_WORKERS_NUMBER=5;
    private final BlockingQueue<Signal> signalQueue = Containers.SIGNAL_QUEUE;
    private final SeleniumGroup group;
    private final Thread leadThread;
    private Signal currentSignal;

    private void lead() throws InterruptedException {
        while((currentSignal = signalQueue.take())!=null){
            analyseSignalAndSendWorkToGroup();
            doLeaderShouldDo();
            afterWork();
        }
    }


    @Override
    public void run() {
        for(;;) {
            try {
                lead();
            } catch (InterruptedException e) {
                //领导线程不会被interrupt 重置即可
                Thread.interrupted();
                e.printStackTrace();
            }
        }
    }

    /**
     * 分析并将工作信号送往工作组 主要逻辑都在这里
     */
    private void analyseSignalAndSendWorkToGroup() {
        final long currentTime = System.currentTimeMillis();
        if(currentSignal == Signal.ADJUST_FRESH) {
            //初始化两个新Selenium 关闭旧Selenium
            HashSet<SeleniumGroup.SeleniumWorker> set = group.getWorkers();
            for (int i = 0; i < CORE_WORKERS_NUMBER; i++) {
                group.addWorker();
            }
            for (SeleniumGroup.SeleniumWorker worker : set) {
                if(worker.birthTime<currentTime) {
                    group.deleteWorker(worker);
                    //小于当前时间非新建，需要被interrupt掉
                }
            }
        }else if(currentSignal == Signal.ADJUST_PREHEAT) {
            //初始化到五个Selenium
            int currentNumber = group.getWorkerCounter();
            for (int i = currentNumber; i < MAX_WORKERS_NUMBER; i++) {
                group.addWorker();
            }
        }else if(currentSignal == Signal.ADJUST_RELEASE){
            //释放到两个Selenium,若超过两个worker在工作可能不成功
            int deleteNumber = group.getWorkerCounter() - CORE_WORKERS_NUMBER;
            HashSet<SeleniumGroup.SeleniumWorker> set = group.getWorkers();
            for (SeleniumGroup.SeleniumWorker worker : set) {
                if(worker.isWorking()) {
                    group.deleteWorker(worker);
                    if (--deleteNumber == 0) {
                        break;
                    }
                }
            }

        }else {
            //通知工人干活
            int targetNumber = 0;
            if(currentSignal == Signal.WORK_REPLACE){
                targetNumber = MAX_SK_NUMBER;
            }else if(currentSignal == Signal.WORK_SUPPLEMENT) {
                //TODO SK容器
                targetNumber = MAX_SK_NUMBER - 1;
            }else{
                //TODO 若有新功能 则在此添加
            }
            group.setTargetNumber(targetNumber);
            awakeWorkers();
        }
    }

    private void doLeaderShouldDo() {
        if(currentSignal == Signal.ADJUST_FRESH) {
            //领导进行下一轮任务获取
        }else if(currentSignal == Signal.ADJUST_PREHEAT) {
            //领导进行下一轮任务获取
        }else if(currentSignal == Signal.ADJUST_RELEASE){
            //领导进行下一轮任务获取
        }
        else{
            leaderSleep();
        }
    }


    /**
     * 善后工作，此时已经解锁
     */
    private void afterWork() {
        if(currentSignal == Signal.ADJUST_FRESH) {
            //领导进行下一轮任务获取
        }else if(currentSignal == Signal.ADJUST_PREHEAT) {
            //领导进行下一轮任务获取
        }else {
            while(signalQueue.size()>MAX_SK_NUMBER){
                try {
                    signalQueue.take();
                } catch (InterruptedException e) {
                    //领导线程不会被interrupt 重置即可
                    Thread.interrupted();
                    e.printStackTrace();
                }
            }
            if(currentSignal == Signal.WORK_REPLACE){
                //替换全部后需要把多余的工人开除 给出释放信号即可 因此是空循环
                while(!signalQueue.offer(Signal.ADJUST_RELEASE)){}
            }
        }
    }

    /**
     * 领导睡觉等待工人们干完活
     */
    private void leaderSleep() {
        LockSupport.park();
    }

    /**
     * 依次唤醒工人
     */
    private void awakeWorkers() {
        HashSet<SeleniumGroup.SeleniumWorker> set = group.getWorkers();
        for (SeleniumGroup.SeleniumWorker worker : set) {
            LockSupport.unpark(worker.thread);
        }
    }

    SeleniumLeader(){
        leadThread = Thread.currentThread();
        group = new SeleniumGroup(leadThread);
        currentSignal = null;
        try {
            signalQueue.put(Signal.ADJUST_PREHEAT);
            signalQueue.put(Signal.WORK_REPLACE);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
