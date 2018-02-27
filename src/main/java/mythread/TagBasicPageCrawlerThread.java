package mythread;

import database.MySQL;
import httpbrower.HttpRequest;
import ipproxypool.ipmodel.IPMessage;
import database.MyRedis;
import parse.CommoditySearchPage;

import java.util.LinkedList;
import java.util.Queue;

/**
 * @Author: spider_hgyi
 * @Date: Created in 下午1:01 18-2-1.
 * @Modified By:
 * @Description: 得到带有分页参数的主分类搜索页面的URL
 */
public class TagBasicPageCrawlerThread implements Runnable {
    private final Object lock;              // 有关生产者、消费者的锁
    private Queue<String> tagBasicUrls;     // 任务队列（共享变量）
    private final Object taskLock;          // 有关任务队列的锁
    private Queue<String> tagBasicPageUrls; // 存放所有线程抓取结果的共享队列（共享变量）

    public TagBasicPageCrawlerThread(Queue<String> tagBasicUrls, Object lock, Queue<String> tagBasicPageUrls,
                                     Object taskLock) {
        this.tagBasicUrls = tagBasicUrls;
        this.lock = lock;
        this.tagBasicPageUrls = tagBasicPageUrls;
        this.taskLock = taskLock;
    }

    @Override
    public void run() {
        MyRedis myRedis = new MyRedis();
        MySQL mySQL = new MySQL();

        String tagBasicUrl;
        IPMessage ipMessage = null;
        // 此flag用于--->如果ip可以进行抓取，则一直使用此ip，不在ip代理池中重新拿取新ip的逻辑判断
        boolean flag = true;

        // 每个URL用单独的代理IP进行分析
        while (true) {
            if (flag) {
                synchronized (lock) {
                    while (myRedis.isEmpty()) {
                        try {
                            System.out.println("当前线程：" + Thread.currentThread().getName() + ", " +
                                    "发现ip-proxy-pool已空, 开始进行等待... ...");
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    ipMessage = myRedis.getIPByList();
                }
            }

            if (ipMessage.getUseCount() >= 30) {
                System.out.println("当前线程：" + Thread.currentThread().getName() + ", 发现此ip：" +
                        ipMessage.getIPAddress() + ":" + ipMessage.getIPPort() + ", 已经连续30次不能使用, 进行舍弃");
                continue;
            }

            // 任务队列是一个共享变量，必须对其进行正确的同步
            synchronized (taskLock) {
                if (tagBasicUrls.isEmpty()) {
                    System.out.println("当前线程：" + Thread.currentThread().getName() + ", 发现任务队列已空");
                    break;
                }

                tagBasicUrl = tagBasicUrls.poll();
            }

            String html = HttpRequest.getHtmlByProxy(tagBasicUrl, ipMessage, lock);
            if (html != null) {
                int pageCount = CommoditySearchPage.getPagesCount(html);
                // 抓取的带页面参数的主分类搜索页面URL暂存器
                Queue<String> tempUrlsStorage = new LinkedList<>();
                tempUrlsStorage.offer(tagBasicUrl);
                if (pageCount >= 2) {
                    tempUrlsStorage.offer(tagBasicUrl + "&s=44");
                }

                // 将本次解析出来的页面，直接写入MySQL数据库
                synchronized (tagBasicPageUrls) {
                    mySQL.saveTagBasicPageUrlsToTagsSearchUrl(tempUrlsStorage);
                }

                flag = false;
            } else {
                synchronized (taskLock) {
                    tagBasicUrls.offer(tagBasicUrl);
                }
                flag = true;
            }
        }

        // 对此线程抓取下来的带有页面参数的URL进行合并
//        synchronized (tagBasicPageUrls) {
//            System.out.println("当前线程：" + Thread.currentThread().getName() + ", 已进入合并区, " +
//                    "待合并大小 tempUrlStorage：" + tempUrlsStorage.size());
//            tagBasicPageUrls.addAll(tempUrlsStorage);
//            System.out.println("当前线程：" + Thread.currentThread().getName() + ", 已成功合并, " +
//                    "合并后tagBasicPageUrls大小：" + tagBasicPageUrls.size());
//        }

        System.out.println("当前线程：" + Thread.currentThread().getName() + ", 已完成任务，将要退出");
    }
}