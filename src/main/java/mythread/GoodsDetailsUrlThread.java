package mythread;

import database.MyRedis;
import database.MySQL;
import httpbrower.HttpRequest;
import ipproxypool.ipmodel.IPMessage;
import parse.CommoditySearchPage;
import utilclass.BloomFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @Author: spider_hgyi
 * @Date: Created in 下午4:25 18-2-6.
 * @Modified By:
 * @Description: 负责解析带有页面参数的商品搜索页url，得到本页面中的商品id
 */
public class GoodsDetailsUrlThread implements Runnable {
    private final Object lock;                      // 用于与 ip-proxy-pool 进行协作的锁
    private final Object tagBasicPageURLsCacheLock; // 与 tagBasicPageURLs-cache 线程进行协作的锁
    private Queue<String> tagBasicPageUrls;         // 任务队列
    private BloomFilter bloomFilter;                // 布隆过滤器
    // 有关布隆过滤器的读写锁
    private static ReadWriteLock bloomFilterReadWriteLock = new ReentrantReadWriteLock();
    // 关于MySQL数据库的锁
    private static final Object mySQLLock = new Object();

    public GoodsDetailsUrlThread(Object lock, Object tagBasicPageURLsCacheLock, Queue<String> tagBasicPageUrls,
                                 BloomFilter bloomFilter) {
        this.lock = lock;
        this.tagBasicPageURLsCacheLock = tagBasicPageURLsCacheLock;
        this.tagBasicPageUrls = tagBasicPageUrls;
        this.bloomFilter = bloomFilter;
    }

    @Override
    public void run() {
        MyRedis myRedis = new MyRedis();
        MySQL mySQL = new MySQL();

        IPMessage ipMessage = null;
        String tagBasicPageUrl;
        boolean flag = true;

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

            synchronized (tagBasicPageUrls) {
                if (!tagBasicPageUrls.isEmpty()) {
                    tagBasicPageUrl = tagBasicPageUrls.poll();
                } else {
                    System.out.println("当前线程：" + Thread.currentThread().getName() + ", 发现任务队列已空");
                    break;
                }
            }

            String html = HttpRequest.getHtmlByProxy(tagBasicPageUrl, ipMessage, lock);
            if (html != null) {
                List<String> goodsDetailsIds = CommoditySearchPage.getGoodsId(html);
                List<String> goodsDetailsUrls = new ArrayList<>();

                // 使用布隆过滤器对得到的商品id进行判重
                for (String goodsDetailsId : goodsDetailsIds) {
                    bloomFilterReadWriteLock.readLock().lock();
                    int[] fingerprints = bloomFilter.getFingerprint(goodsDetailsId);
                    boolean exist = bloomFilter.isExist(fingerprints);
                    bloomFilterReadWriteLock.readLock().unlock();

                    if (!exist) {
                        bloomFilterReadWriteLock.writeLock().lock();
                        bloomFilter.saveFingerprints(fingerprints);
                        bloomFilterReadWriteLock.writeLock().unlock();
                    } else {
                        continue;
                    }

                    String goodsDetailsUrl = "https://item.taobao.com/item.htm?id=" + goodsDetailsId;
                    goodsDetailsUrls.add(goodsDetailsUrl);
                }

                // 将goodsDetailsUrls写进MySQL数据库
                synchronized (mySQLLock) {
                    System.out.println("当前线程：" + Thread.currentThread().getName() + ", " +
                            "准备将goodsDetailsUrls写进MySQL数据库, goodsDetailsUrls-size：" + goodsDetailsUrls.size());
                    mySQL.saveGoodsDetailsUrlsToGoodsDetailsUrl(goodsDetailsUrls);
                }

                // 将tagBasicPageUrl写进Redis数据库
                synchronized (tagBasicPageURLsCacheLock) {
                    System.out.println("当前线程：" + Thread.currentThread().getName() + ", " +
                            "准备将tagBasicPageUrl写进Redis数据库, tagBasicPageUrl：" + tagBasicPageUrl);
                    myRedis.setTagBasicPageURLToCache(tagBasicPageUrl);
                }

                flag = false;
            } else {
                synchronized (tagBasicPageUrls) {
                    tagBasicPageUrls.offer(tagBasicPageUrl);
                }
                flag = true;
            }
        }
    }
}