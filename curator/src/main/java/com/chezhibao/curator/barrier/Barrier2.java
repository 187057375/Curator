/*
 * Copyright (C), 2014-2017, 江苏乐博国际投资发展有限公司
 * FileName: Barrier1.java
 * Author:   zhangdanji
 * Date:     2017年12月12日
 * Description:
 */
package com.chezhibao.curator.barrier;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.barriers.DistributedBarrier;
import org.apache.curator.framework.recipes.barriers.DistributedDoubleBarrier;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.CountDownLatch;

/**
 * @author zhangdanji
 */
public class Barrier2 {
    private static final String CONNECT_ADDR = "follower1:2181,follower2:2181,follower3:2181";
    private static final int SESSION_OUTTIME = 10000;
    private static DistributedBarrier barrier;
    public static void main(String[] args) throws Exception {

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        final CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString(CONNECT_ADDR)
                .sessionTimeoutMs(SESSION_OUTTIME)
                .retryPolicy(retryPolicy)
                .build();
        cf.start();

        for(int i = 0; i < 5; i++){
            new Thread(new Runnable() {
                public void run() {
                    barrier = new DistributedBarrier(cf,"/barrier");
                    try {
                        System.out.println(Thread.currentThread().getName() + "设置barrier...");
                        barrier.setBarrier();
                        barrier.waitOnBarrier();
                        Thread.sleep(10000);
                        System.out.println(Thread.currentThread().getName() + "处理");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            },"t-" + i).start();
        }

        Thread.sleep(5000);
        barrier.removeBarrier();
    }
}
