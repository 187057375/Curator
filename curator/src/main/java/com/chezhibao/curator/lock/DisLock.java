/*
 * Copyright (C), 2014-2017, 江苏乐博国际投资发展有限公司
 * FileName: DisLock.java
 * Author:   zhangdanji
 * Date:     2017年12月12日
 * Description:
 */
package com.chezhibao.curator.lock;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.CountDownLatch;

/**
 * @author zhangdanji
 */
public class DisLock {

    private static final String CONNECT_ADDR = "follower1:2181,follower2:2181,follower3:2181";
    private static final int SESSION_OUTTIME = 10000;

    @SuppressWarnings("AlibabaAvoidManuallyCreateThread")
    public static void main(String[] args) throws InterruptedException {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString(CONNECT_ADDR)
                .sessionTimeoutMs(SESSION_OUTTIME)
                .retryPolicy(retryPolicy)
                .build();
        cf.start();
        final InterProcessMutex lock = new InterProcessMutex(cf,"/lock");
        for(int i = 0; i < 5; i++){
            new Thread(new Runnable() {

                public void run() {
                    try {
                        countDownLatch.await();
                        lock.acquire();
                        System.out.println(Thread.currentThread().getName() + "执行...");
                        Thread.sleep(10000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        try {
                             if(lock.isAcquiredInThisProcess()){
                                lock.release();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            },"t-" + i).start();
            Thread.sleep(2000);
            countDownLatch.countDown();
        }
    }
}
