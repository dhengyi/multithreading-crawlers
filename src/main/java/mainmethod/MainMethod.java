package mainmethod;

import database.MySQL;
import mythread.TagBasicPageURLsCacheThread;
import urlbuild.GoodsUrl;
import urlbuild.MainClassifyUrl;
import ipproxypool.operation.IPProxyPool;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Queue;

/**
 * Created by hg_yi on 17-5-23.
 *
 * @Description: 多线程抓取淘宝程序主方法入口
 */

public class MainMethod {
    public static void main(String[] args) throws FileNotFoundException {
        // 设置sout输出至文件
//        PrintStream ps = new PrintStream("/home/hg_yi/temp");
//        System.setOut(ps);

        // 创建生产者（ip-proxy-pool）与消费者（thread-tagBasicPageURL-i）等待/通知机制所需的对象锁
        Object lock = new Object();
        // 创建 tagBasicPageURLs-cache 线程与 thread-GoodsDetailsUrl-i 线程协作所需的对象锁
        Object tagBasicPageURLsCacheLock = new Object();

        // 创建一个 ip-proxy-pool 线程，执行IP代理池
        IPProxyPool.startExecute(lock);

        /**
         * 使用等待/通知机制，如果此时ip-proxy-pool里面没IP，则进行等待，并让IP代理池
         * 生产IP，直到生产完整，通知所有工作的线程继续开始工作
         */

        // 拿到淘宝基本分类商品的源链接（使用本机IP）
//        Queue<String> tagBasicUrls = MainClassifyUrl.getMainClassifyUrls();
//        System.out.println("共有" + tagBasicUrls.size() + "大小的URL待抓取");

        // 拿到带有页面参数的基本分类商品的源链接, 并保存在MySQL数据库中（使用代理IP）
//        Queue<String> tagBasicPageURLs = MainClassifyUrl.getMainClassifyPageUrlsByProxy(tagBasicUrls, lock);

        // 创建一个 tagBasicPageURLs-cache 线程，每抓取成功100个任务，就将MySQL中存储的任务标记为true
        TagBasicPageURLsCacheThread.start(tagBasicPageURLsCacheLock);

        // 得到商品详情页的url，使用布隆过滤器，并及时持久化进MySQL数据库
        GoodsUrl.getGoodsDetailsPageUrl(lock, tagBasicPageURLsCacheLock);
    }
}
