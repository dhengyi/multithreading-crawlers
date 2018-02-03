package ipproxypool.jobthread;

import ipproxypool.grabutils.URLFecter;
import ipproxypool.ipfilter.IPFilter;
import ipproxypool.ipmodel.IPMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by hg_yi on 17-8-11.
 *
 * @Description: 抓取xici代理网的分配线程
 * 抓取不同页面的xici代理网的html源码，就使用不同的代理IP，在对IP进行过滤之后进行合并
 */
public class CreateIPProxyPool {
    // 成员变量（非线程安全）
    private List<IPMessage> ipMessages;
    // 创建供上述变量使用的读写锁
    private ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    public CreateIPProxyPool(List<IPMessage> ipMessages) {
        this.ipMessages = ipMessages;
    }

    public void saveIP(Queue<String> urls, Object taskLock) {
        int rand = 0;
        readWriteLock.writeLock().lock();
        String ipAddress = ipMessages.get(rand).getIPAddress();
        String ipPort = ipMessages.get(rand).getIPPort();
        readWriteLock.writeLock().unlock();

        while (true) {
            /**
             * 随机挑选代理IP(本步骤由于其他线程有可能在位置确定之后对ipMessages数量进行
             * 增加，虽说不会改变已经选择的ip代理的位置，但合情合理还是在对共享变量进行读写的时候要保证
             * 其原子性，否则极易发生脏读)
             */
            // 每个线程先将自己抓取下来的ip保存下来并进行过滤
            List<IPMessage> ipMessages1 = new ArrayList<>();
            String url;

            // 任务队列是共享变量，对其的读写必须进行正确的同步
            synchronized (taskLock) {
                if (urls.isEmpty()) {
                    System.out.println("当前线程：" + Thread.currentThread().getName() + ", 发现任务队列已空");
                    break;
                }
                url = urls.poll();
            }

            boolean success = URLFecter.urlParse(url, ipAddress, ipPort, ipMessages1);
            // 如果ip代理池里面的ip不能用，或本页抓取失败，则切换下一个IP对本页进行重新抓取
            if (!success) {
                // 当抓取失败的时候重新拿取代理ip
                readWriteLock.writeLock().lock();
                rand = (int) (Math.random() * ipMessages.size());
                ipAddress = ipMessages.get(rand).getIPAddress();
                ipPort = ipMessages.get(rand).getIPPort();
                readWriteLock.writeLock().unlock();

                synchronized (taskLock) {
                    urls.offer(url);
                }
                continue;
            }

            // 对ip重新进行过滤，只要速度在三秒以内的并且类型为HTTPS的
            ipMessages1 = IPFilter.Filter(ipMessages1);

            // 将质量合格的ip合并到共享变量ipMessages中，进行合并的时候保证原子性
            readWriteLock.writeLock().lock();
            System.out.println("当前线程：" + Thread.currentThread().getName() + ", 已进入合并区, " +
                    "待合并大小 ipMessages1：" + ipMessages1.size());
            ipMessages.addAll(ipMessages1);
            System.out.println("当前线程：" + Thread.currentThread().getName() + ", 已成功合并, " +
                    "合并后ipMessage大小：" + ipMessages.size());
            readWriteLock.writeLock().unlock();
        }
    }
}