package mainmethod;

import database.MySQL;
import urlbuild.MainClassifyUrl;
import ipproxypool.operation.IPProxyPool;

import java.util.Queue;

/**
 * Created by hg_yi on 17-5-23.
 *
 * @Description: 多线程抓取淘宝程序主方法入口
 */

public class MainMethod {
    public static void main(String[] args) {
        // 创建生产者（ip-proxy-pool）与消费者 等待/通知机制所需的对象锁
        Object lock = new Object();

        // 创建一个 ip-proxy-pool 线程，执行IP代理池
        IPProxyPool.startExecute(lock);

        /**
         * 使用等待/通知机制，如果此时ip-proxy-pool里面没IP，则进行等待，并让ip代理池
         *  生产IP，直到生产完整，通知所有工作的线程继续开始工作
         */

        // 拿到淘宝基本分类商品的源链接（使用本机IP）
        Queue<String> tagBasicUrls = MainClassifyUrl.getMainClassifyUrls();

        for (String tagBasicUrl : tagBasicUrls) {
            System.out.println(tagBasicUrl);
        }
        System.out.println("共有" + tagBasicUrls.size() + "大小的URL待抓取");

        // 拿到带有页面参数的基本分类商品的源链接（使用代理IP）
        Queue<String> tagBasicPageURLs = MainClassifyUrl.getMainClassifyPageUrlsByProxy(tagBasicUrls, lock);

        // 将带有页面参数的基本分类商品URL进行持久化
        MySQL.saveTagBasicPageUrlsToTagsSearchUrl(tagBasicPageURLs);

        /**
         * 在每个分类下面提取600条商品url链接(使用多线程进行抓取)
         *
         * 基本思路：使用多线程进行网页请求和网页解析，将url链接拿到之后进行该线程对数据库
         * 的写操作（注意同步操作），需要注意的是当一个线程完成任务之后，如何给它分配新的任务
         * 保证线程到最后都没有空闲下来。
         */

        /**
         * 创建线程池，本系统可用的处理器核心数为4，由于爬虫是IO密集型任务，所以我们合理分配
         * 线程池的大小。
         */

        //使用多线程进行商品详情页的url提取
//        ThreadPoolCrawler.getGoodsTasks(strings);
    }
}
