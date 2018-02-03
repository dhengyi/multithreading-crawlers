package ipproxypool.operation;

import ipproxypool.timer.MyTimer;

/**
 * @Author: spider_hgyi
 * @Date: Created in 上午11:43 18-1-31.
 * @Modified By:
 * @Description: 创建执行IP代理池的后台线程
 */
public class IPProxyPoolThread implements Runnable {
    private final Object lock;

    public IPProxyPoolThread(Object lock) {
        this.lock = lock;
    }

    @Override
    public void run() {
        MyTimer.startIPProxyPool(lock);
    }
}
