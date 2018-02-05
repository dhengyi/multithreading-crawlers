package urlbuild;

import database.MyRedis;
import database.MySQL;
import httpbrower.HttpRequest;
import ipproxypool.ipmodel.IPMessage;
import parse.CommoditySearchPage;

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
    public void getGoodsDetailsPageUrl(Object lock) {
        Object redisLcok = new Object();
        MySQL mySQL = new MySQL();
        List<String> goodsDetailsPageUrls = new LinkedList<>();
        Queue<String> tagBasicUrls = mySQL.getTagBasicPageUrlsFromTagsSearchUrl();

        MyRedis myRedis = new MyRedis();
        IPMessage ipMessage = myRedis.getIPByList();

        // 对任务队列进行解析
        for (String tagBasicUrl : tagBasicUrls) {
            String html = HttpRequest.getHtmlByProxy(tagBasicUrl, ipMessage, lock);
            CommoditySearchPage.getGoodsUrl(html, goodsDetailsPageUrls);

        }

    }
}
