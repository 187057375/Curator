/*
 * Copyright (C), 2014-2017, 江苏乐博国际投资发展有限公司
 * FileName: Watcher1.java
 * Author:   zhangdanji
 * Date:     2017年12月09日
 * Description:
 */
package com.chezhibao.curator.watcher;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.NodeCache;
import org.apache.curator.framework.recipes.cache.NodeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
 * @author zhangdanji
 */
public class Watcher1 {

    private static final String CONNECT_ADDR = "follower1:2181,follower2:2181,follower3:2181";
    private static final int SESSION_OUTTIME = 10000;

    public static void main(String[] args) throws Exception {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 10);
        CuratorFramework cf = CuratorFrameworkFactory.builder()
                .connectString(CONNECT_ADDR)
                .sessionTimeoutMs(SESSION_OUTTIME)
                .retryPolicy(retryPolicy)
                .build();
        cf.start();
        final NodeCache cache = new NodeCache(cf,"/super",false);
        cache.start(true);
        cache.getListenable().addListener(new NodeCacheListener() {
            public void nodeChanged() throws Exception {
                System.out.println(cache.getCurrentData().getPath());
                System.out.println(new String(cache.getCurrentData().getData(),"UTF-8"));
                System.out.println(cache.getCurrentData().getStat());
            }
        });
        Thread.sleep(1000);
        cf.create().withMode(CreateMode.PERSISTENT).forPath("/super", "super".getBytes());
        Thread.sleep(Integer.MAX_VALUE);
    }
}
