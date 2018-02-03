package parse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by hg_yi on 17-5-23.
 *
 * @Description: 负责从淘宝分类页面提取出所有的基本分类名，并构造相关URL
 */

public class TagsPage {
    // 提取所有分类商品的源链接
    public static Queue<String> getTagURLs(String html) {
        Queue<String> tagUrls = new LinkedList<>();

        Document document = Jsoup.parse(html);
        Elements elements = document.select("div[class=" +
                "home-category-list J_Module]");

        for(Element element : elements) {
            //在多个div标签中提取多个li标签
            Elements lis = element.select("ul[class=category-list]").first().
                select("li");

            for(Element li : lis) {
                //在多个li标签中提取多个a标签
                Elements as = li.select("div[class=category-items]").first().
                        select("a[class=category-name]");

                //将a标签中的关于商品分类的关键字提取出来,并进行url的构造
                for(Element a : as) {
                    String name = a.text().replaceAll(" ", "");
                    String url = "https://s.taobao.com/search?q=" + name + "&style=grid";

                    tagUrls.offer(url);
                }
            }
        }

        return tagUrls;
    }
}
