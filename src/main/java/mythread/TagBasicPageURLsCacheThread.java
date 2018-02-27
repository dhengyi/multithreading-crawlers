package mythread;

import database.MyRedis;
import database.MySQL;

import java.util.List;

import static java.lang.Thread.MAX_PRIORITY;

/**
 * @Author: spider_hgyi
 * @Date: Created in 上午11:51 18-2-6.
 * @Modified By:
 * @Description: 处理缓存的线程，将 tag-basic-page-urls 中存在的url标记进MySQL数据库中
 */
public class TagBasicPageURLsCacheThread implements Runnable {
    private final Object tagBasicPageURLsCacheLock;

    public TagBasicPageURLsCacheThread(Object tagBasicPageURLsCacheLock) {
        this.tagBasicPageURLsCacheLock = tagBasicPageURLsCacheLock;
    }

    public static void start(Object tagBasicPageURLsCacheLock) {
        Thread thread = new Thread(new TagBasicPageURLsCacheThread(tagBasicPageURLsCacheLock));
        thread.setName("tagBasicPageURLs-cache");
        thread.setPriority(MAX_PRIORITY);           // 将这个线程的优先级设置最大，允许出现误差
        thread.start();
    }

    @Override
    public void run() {
        MyRedis myRedis = new MyRedis();
        MySQL mySQL = new MySQL();

        while (true) {
            synchronized (tagBasicPageURLsCacheLock) {
                while (myRedis.tagBasicPageURLsCacheIsOk()) {
                    System.out.println("当前线程：" + Thread.currentThread().getName() + ", " +
                            "准备开始将 tag-basic-page-urls-cache 中的url在MySQL中进行标记");

                    List<String> tagBasicPageURLs = myRedis.getTagBasicPageURLsFromCache();
                    System.out.println("tagBasicPageURLs-size: " + tagBasicPageURLs.size());

                    // 将MySQL数据库中对应的url标志位置为true
                    mySQL.setFlagFromTagsSearchUrl(tagBasicPageURLs);
                }
            }
        }
    }
}
