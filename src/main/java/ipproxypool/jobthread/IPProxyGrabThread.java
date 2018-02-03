package ipproxypool.jobthread;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.ReadWriteLock;

/**
 * Created by hg_yi on 17-8-11.
 *
 * @Description: 创建IP代理池抓取线程
 */
public class IPProxyGrabThread implements Runnable {
    // 所有线程共享任务队列
    private Queue<String> urls;
    private CreateIPProxyPool createIpProxyPool;
    private Object taskLock;

    public IPProxyGrabThread(Queue<String> urls, CreateIPProxyPool createIpProxyPool, Object taskLock) {
        this.urls = urls;
        this.createIpProxyPool = createIpProxyPool;
        this.taskLock = taskLock;
    }

    @Override
    public void run() {
        createIpProxyPool.saveIP(urls, taskLock);
    }
}
