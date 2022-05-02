package com.littlepenguin.uscshortcutsysserver.services;


import com.littlepenguin.uscshortcutsysserver.domain.SK;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class SKMain {
    /**
     * SK队列
     * 实现类为ArrayBlockingQueue ReentrantLock线程安全
     */
    public static final Queue<SK> skQueue = new ArrayBlockingQueue<SK>(77,false);
    /**
     * 线程池
     * 实现类为newFixedThreadPool 定长线程池
     */
    public static final ExecutorService executorService = Executors.newFixedThreadPool(3);
    /**
     * 1. 创建队列容器
     * 2. 创建线程池
     * 3. 提交SK获取
     */

}
