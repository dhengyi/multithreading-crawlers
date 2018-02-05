//package crawlerthread;
//
//import urlbuild.GoodsUrlsToRedis;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.concurrent.ExecutorService;
//import java.util.concurrent.Executors;
//
///**
// * Created by hg_yi on 17-6-16.
// */
//public class ThreadPoolCrawler {
//    //分配线程池，进行详情页的url的抓取
//    public static void getGoodsTasks(List<String> strings) {
//        //创建固定容量的大小的缓冲池
//        int threadNum = 20, num = 0;
//        ExecutorService executor = Executors.newFixedThreadPool(threadNum);
//        //平均每个线程所分配的任务
//        int averTaskNum = strings.size() / threadNum;
//
//        for (int i = 1; i <= threadNum; i++) {
//            if (i == threadNum) {
//                averTaskNum = strings.size() - averTaskNum * (threadNum-1);
//            }
//
//            //将每个线程所需要解析的url收集起来分配个每个线程
//            int loop = 1;
//            List<String> urls = new ArrayList<String>();
//            while (loop <= averTaskNum) {
//                urls.add(strings.get(num++));
//                loop++;
//            }
//
//            Runnable thread = new GoodsUrlsToRedis(urls);
//            executor.execute(thread);
//
//            break;
//        }
//    }
//}
