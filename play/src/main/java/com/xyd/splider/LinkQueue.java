package com.xyd.splider;

import org.apache.commons.lang3.StringUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * 存放已经访问的URL
 * Created by DXM_0020 on 2017/5/25.
 */
public class LinkQueue {

    //已经访问的URL集合
    private static Set visitedUrl = new HashSet<>();
    //未访问的URL队列
    private static Queue queue = new Queue();

    //添加访问过的URL
    static void addVisitedUrl(String url){
        visitedUrl.add(url);
    }

    //获取未访问的URL
    public static Queue getUnVisitedUrl(){
        return queue;
    }

    //移除访问过的URL
    public static void removeVisitedUrl(String url){
        visitedUrl.remove(url);
    }

    //未访问的URL出队列
    static Object unVisitedUrlDeQueue(){
        return queue.deQueue();
    }

    //获得已经访问的URL数目
    static int getVisitedUrlNum(){
        return visitedUrl.size();
    }
    //保证保证每个 URL 只被访问一次
    static void addUnVisitedUrl(String url){
        if(StringUtils.isNotEmpty(url) && !visitedUrl.contains(url) && !queue.cotains(url)){
            queue.enQueue(url);
        }
    }

    //判断未访问的队列是否为空
    static boolean unVisitedUrlIsEmpty(){
        return queue.isEmpty();
    }
}
