package urlbuild;

import database.MySQL;
import mythread.GoodsDetailsUrlThread;
import utilclass.BloomFilter;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by hg_yi on 17-5-28.
 *
 * @Description: 得到商品详情页的url
 */

public class GoodsUrl {
    public static void getGoodsDetailsPageUrl(Object lock, Object tagBasicPageURLsCacheLock) {
        MySQL mySQL = new MySQL();
        BloomFilter bloomFilter = new BloomFilter();
        List<Thread> threads = new ArrayList<>();
        Queue<String> tagBasicPageUrls = mySQL.getTagBasicPageUrlsFromTagsSearchUrl();

        System.out.println("tagBasicPageUrls-size: " + tagBasicPageUrls.size());

        // 创建20个线程，用于解析任务队列
        for (int i = 0; i < 20; i++) {
            Thread thread = new Thread(new GoodsDetailsUrlThread(lock, tagBasicPageURLsCacheLock, tagBasicPageUrls,
                    bloomFilter));
            thread.setName("thread-GoodsDetailsUrl-" + i);
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}