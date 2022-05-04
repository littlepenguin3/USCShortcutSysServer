package com.littlepenguin.uscshortcutsysserver;

import com.littlepenguin.uscshortcutsysserver.exception.CheckCodeException;
import com.littlepenguin.uscshortcutsysserver.exception.WebDriverNotMatchException;
import com.littlepenguin.uscshortcutsysserver.services.SeleniumService;
import com.littlepenguin.uscshortcutsysserver.utils.SpringContextUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import sun.misc.Lock;

import java.util.Date;

import java.io.IOException;
import java.util.concurrent.locks.LockSupport;


public class test1 {


    private class Sync implements Runnable{
        Object lock = new Object();
        public void sync() throws InterruptedException {
            synchronized (lock) {
                System.out.println("我想再等等");
                lock.wait();
                System.out.println(Thread.currentThread().getName()+"我被通知了");
            }
        }

        public  void _notify(){
            synchronized (lock) {
                lock.notifyAll();
            }
        }
        @Override
        public void run() {
            try {
                sync();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    @Test
    public void test() throws InterruptedException {
        Sync sync = new Sync();
        Thread thread1 = new Thread(sync);
        Thread thread2 = new Thread(sync);
        thread1.start();
        thread2.start();
        Thread.sleep(1000);
        System.out.println("main");
        sync._notify();
        thread1.join();
        thread2.join();
    }

 @Test
    public void testFor(){
     for (int i=0;;i++){
         System.out.println(i);
     }
 }

    private static class ReaderThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            for(;;) {
                LockSupport.park(Thread.currentThread());
                System.out.println("快停下！");
            }
        }
    }
 @Test
    public void TestVolatile() throws InterruptedException {
     ReaderThread readerThread = new ReaderThread();

     readerThread.start();
     LockSupport.unpark(readerThread);
     Thread.sleep(5000);
     readerThread.interrupt();
     readerThread.join();
 }

}

