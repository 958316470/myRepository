package com.netty.virtualnio;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author 95831
 *
 * 伪I/O模式处理socket请求
 *由于线程池和消息队列是有界的，因此客户端并发再多
 * 不会导致线程个数膨胀或内存溢出
 */
public class TimeServerHandlerExecutePool {

        private ExecutorService executorService;

        public TimeServerHandlerExecutePool(int maxPoolSize, int queueSize) {
            executorService = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), maxPoolSize,120L, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<Runnable>(queueSize));
        }

        public void execute(Runnable task) {
            executorService.execute(task);
        }

}
