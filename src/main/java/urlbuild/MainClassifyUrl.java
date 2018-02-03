package urlbuild;

import crawlerthread.tagBasicPageCrawlerThread;
import httpbrower.HttpRequest;
import parse.TagsPage;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by hg_yi on 17-5-23.
 *
 * @Description: 拿到淘宝商品最基本分类中的各个类别的URL
 */

public class MainClassifyUrl {
    // 拿到淘宝所有分类商品的源链接
    public static Queue<String> getMainClassifyUrls() {
        String url = "https://www.taobao.com/tbhome/page/market-list";

        // 得到淘宝分类页面的源代码
        String html = HttpRequest.getHtml(url);

        // 对得到的源代码进行解析，拿到每个分类页面的源链接
        return TagsPage.getTagURLs(html);
    }

    // 拿到淘宝所有分类商品带有页面参数的源链接（使用多线程）
    public static Queue<String> getMainClassifyPageUrlsByProxy(Queue<String> tagBasicUrls, Object lock) {
        // 带页面参数URL解析线程收集器
        List<Thread> threads = new ArrayList<>();
        // 保存所有带有分类商品页面参数源链接的任务队列
        Queue<String> tagBasicPageURLs = new LinkedList<>();
        // 创建一个有关任务队列的对象锁
        Object taskLock = new Object();
        // 创建一个有关Redis的对象锁
        Object redisLock = new Object();

        for (int i = 0; i < 40; i++) {
            Thread thread = new Thread(new tagBasicPageCrawlerThread(tagBasicUrls, lock, tagBasicPageURLs, taskLock, redisLock));
            thread.setName("thread-tagBasicPageURL-" + i);

            threads.add(thread);
            thread.start();
        }

        for (Thread thread1 : threads) {
            try {
                thread1.join();
                System.out.println("当前线程：" + thread1.getName() + ", 已完成抓取任务");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return tagBasicPageURLs;
    }
}