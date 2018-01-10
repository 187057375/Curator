/*
 * Copyright (C), 2014-2017, 江苏乐博国际投资发展有限公司
 * FileName: Count.java
 * Author:   zhangdanji
 * Date:     2017年12月12日
 * Description:
 */
package com.chezhibao.curator.count;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.atomic.AtomicValue;
import org.apache.curator.framework.recipes.atomic.DistributedAtomicInteger;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.retry.RetryNTimes;

import java.util.concurrent.CountDownLatch;

/**
 * @author zhangdanji
 */
public class Count {
    private static final String CONNECT_ADDR = "follower1:2181,follower2:2181,follower3:2181";
    private static final int SESSION_OUTTIME = 10000;

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        final CountDownLatch countDownLatch = new CountDownLatch(1);
        CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString(CONNECT_ADDR)
                .sessionTimeoutMs(SESSION_OUTTIME)
                .retryPolicy(retryPolicy)
                .build();
        cf.start();

        DistributedAtomicInteger atomicInteger = new DistributedAtomicInteger(cf,"/count",new RetryNTimes(3,1000));
        atomicInteger.increment();
        AtomicValue<Integer> integerAtomicValue = atomicInteger.get();
        System.out.println(integerAtomicValue.succeeded());
        System.out.println(integerAtomicValue.postValue());
        System.out.println(integerAtomicValue.preValue());
    }
}
