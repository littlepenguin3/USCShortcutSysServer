package com.littlepenguin.uscshortcutsysserver;


import com.littlepenguin.uscshortcutsysserver.domain.SK;
import org.springframework.stereotype.Service;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.littlepenguin.uscshortcutsysserver.selenium.Signal;

/**
 * @author 38122
 */
@Service
public class Containers {
    /**
     * SK队列
     * 实现类为ArrayBlockingQueue ReentrantLock线程安全
     */
    public static final Queue<SK> SK_QUEUE = new ArrayBlockingQueue<SK>(77,false);

    public static final BlockingQueue<Signal> SIGNAL_QUEUE = new LinkedBlockingQueue<Signal>();

}