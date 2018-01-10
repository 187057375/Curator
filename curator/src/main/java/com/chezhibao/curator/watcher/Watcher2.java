/*
 * Copyright (C), 2014-2017, 江苏乐博国际投资发展有限公司
 * FileName: Watcher2.java
 * Author:   zhangdanji
 * Date:     2017年12月09日
 * Description:
 */
package com.chezhibao.curator.watcher;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.PathChildrenCache;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheEvent;
import org.apache.curator.framework.recipes.cache.PathChildrenCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;

/**
 * @author zhangdanji
 */
public class Watcher2 {

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
        PathChildrenCache cache = new PathChildrenCache(cf,"/super",true);
        cache.start(PathChildrenCache.StartMode.POST_INITIALIZED_EVENT);
        cache.getListenable().addListener(new PathChildrenCacheListener() {
            public void childEvent(CuratorFramework client, PathChildrenCacheEvent event) throws Exception {
                switch (event.getType()){
                    case CHILD_ADDED:
                        System.out.println("add " + event.getData().getPath());
                        break;
                    case CHILD_UPDATED:
                        System.out.println("update " + event.getData().getPath());
                        break;
                    case CHILD_REMOVED:
                        System.out.println("removed " + event.getData().getPath());
                        break;
                    default :
                        break;
                }
                System.out.println(new String(event.getData().getData(),"UTF-8"));
            }
        });
    }
}
