package com.xyd.splider;

import java.util.LinkedList;

/**
 * 队列
 * Created by DXM_0020 on 2017/5/25.
 */
public class Queue {
    private LinkedList queue = new LinkedList();

    //入队列
    public void enQueue(Object object){
        queue.add(object);
    }

    //出队列
    public Object deQueue(){
        return queue.removeFirst();
    }

    //判断队列是否为空
    public boolean isEmpty(){
        return queue.isEmpty();
    }

    //判断队列是否含有t
    public boolean cotains(Object object){
        return queue.contains(object);
    }
}
