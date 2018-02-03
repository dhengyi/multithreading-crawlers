package urlbuild;

import httpbrower.HttpRequest;
import parse.CommoditySearchPage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hg_yi on 17-5-28.
 *
 * @Description: 综合使用正则表达式与fastjson
 */

public class GoodsUrl {
    public static List<String> getGoodsDetailUrls(String goodsUrl) {
        List<String> id = new ArrayList<String>();

        //请求本页面，得到网页源码
        String html = HttpRequest.getHtml(goodsUrl);

        //将网页源码进行解析，得到本分类商品所占的页面总数
        int pages = CommoditySearchPage.getPagesCount(html);

        //计划得到本分类商品600个
        id = getGoodsUrlResult(pages, goodsUrl, html, id);

        return id;
    }

    public static List<String> getGoodsUrlResult(int pages, String goodsUrl, String html,
                                              List<String> id) {
        if (pages <= 10) {
            for (int i = 1; i <= pages; i++) {
                //使用正则表达式将本页面的所有商品id提取出来并构建为商品详情页的url
                id = CommoditySearchPage.getGoodsId(html, id);

                //构建将要访问的url
                String requestGoodsUrl = goodsUrl + "&s=" + i * 60;

                //返回访问页面的html源码
                html = HttpRequest.getHtml(requestGoodsUrl);
            }
        } else {
            for (int i = 1; i <= 10; i++) {
                //使用正则表达式将本页面的所有商品id提取出来并构建为商品详情页的url
                id = CommoditySearchPage.getGoodsId(html, id);

                //构建将要访问的url
                String requestGoodsUrl = goodsUrl + "&s=" + i * 60;

                //返回访问页面的html源码
                html = HttpRequest.getHtml(requestGoodsUrl);
            }
        }

        return id;
    }
}
