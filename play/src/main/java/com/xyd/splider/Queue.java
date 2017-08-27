package com.xyd.splider;

import java.util.LinkedList;

/**
 * 队列
 * Created by DXM_0020 on 2017/5/25.
 */
class Queue {
    private LinkedList queue = new LinkedList();

    //入队列
    void enQueue(Object object){
        queue.add(object);
    }

    //出队列
    Object deQueue(){
        return queue.removeFirst();
    }

    //判断队列是否为空
    boolean isEmpty(){
        return queue.isEmpty();
    }

    //判断队列是否含有t
    boolean cotains(Object object){
        return queue.contains(object);
    }
}
