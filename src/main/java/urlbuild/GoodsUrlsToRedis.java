//package urlbuild;
//
//import database.MyRedis;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by hg_yi on 17-5-31.
// *
// * @Description: 使用HashSet的同时已经进行了排重操作，但是因为每个HashSet给redis数据库
// * 里面写东西的时候并不是同时的，所以redis数据库中有可能出现重复的url，我们
// * 使用布隆过滤其进行url排重操作
// */
//
//public class GoodsUrlsToRedis implements Runnable {
//    List<String> urls = new ArrayList<String>();
//    //由于要实现全局锁，所以这里为静态锁
//    private static Object lock = new Object();
//    //得到Jedis
//    MyRedis myRedis = new MyRedis();
//
//    //构造器
//    public GoodsUrlsToRedis(List<String> urls) {
//        this.urls = urls;
//    }
//
//    public void run() {
//        for (int i = 0; i < urls.size(); i++) {
//            //得到本分类商品所占的页面总数, 并直接拿到本分类商品中每个商品的id以构建商品详情页的url
//            List<String> GoodsDetailUrls = GoodsUrl.getGoodsDetailUrls(urls.get(i));
//
//            //将得到的url写入redis数据库中
//            synchronized (lock) {
////                myRedis.setUrlsToRedis(GoodsDetailUrls);
////                myJedis.getUrlsFromRedis();
//            }
//
//            break;
//        }
//    }
//}
