package parse;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by hg_yi on 17-5-28.
 *
 * @Description: 对商品搜索页面进行解析
 */

public class CommoditySearchPage {
    // 得到搜索商品所占的页面总数
    public static int getPagesCount(String html) {
        int pageCount = 0;
        // 使用正则表达式将本商品所占的页数解析出来
        Pattern pattern = Pattern.compile("\"totalPage\":[0-9]*?,");
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            // matcher.group()返回匹配到的子字符串
            String string = matcher.group();
            int start = string.indexOf(":");
            pageCount = Integer.parseInt(string.substring(start+1, string.length()-1));
        }

        return pageCount;
    }

    // 拿到本分类商品中每个商品的id以构建商品详情页的url
    public static List<String> getGoodsId(String html, List<String> id) {
        String string = null;

        // 使用正则表达式将本页所有商品的id提取出来（json数据串）
        Pattern pattern = Pattern.compile("\"allNids\":\\[.*?\\]");
        Matcher matcher = pattern.matcher(html);

        if (matcher.find()) {
            // matcher.group()返回匹配到的子字符串
            string = matcher.group();
        }

        // 将每个商品id拿到,并构造为商品详情页的url
        int start = string.indexOf('[');
        String idStr = string.substring(start+1, string.length()-1);

        for (String idStars : idStr.split(",")) {
            String singleId = idStars.substring(1, idStars.length()-1);

            String goodsUrl = "https://item.taobao.com/item.htm?id=" + singleId;

            id.add(goodsUrl);
        }

        return id;
    }
}
