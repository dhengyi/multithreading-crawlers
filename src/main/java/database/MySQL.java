package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @Author: spider_hgyi
 * @Date: Created in 下午7:37 18-2-1.
 * @Modified By:
 * @Description: 集成对MySQL数据库的操作
 */
public class MySQL {
    private Connection connection = MySQLDB.getConnection();

    private static final String SAVE_TAG_BASIC_PAGE_URLS = "INSERT INTO tags_search_url(url) VALUES (?)";

    private static final String GET_TAG_BASIC_PAGE_URLS = "SELECT url FROM tags_search_url";

    private static final String SET_FLAG_FROM_TAGS_SEARCH_URL = "UPDATE tags_search_url SET flag = TRUE WHERE url = ?";

    private static final String SAVE_GOODS_DETAILS_URLS = "INSERT INTO goods_details_url(url) VALUES (?)";

    // 将抓取下来的带有页面参数的主分类商品URL存储进MySQL数据库中
    public void saveTagBasicPageUrlsToTagsSearchUrl(Queue<String> tagBasicPageUrls) {
        PreparedStatement statement;

        for (String tagBasicPageUrl : tagBasicPageUrls) {
            try {
                statement = connection.prepareStatement(SAVE_TAG_BASIC_PAGE_URLS);

                statement.setString(1, tagBasicPageUrl);
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 将抓取下来的带有页面参数的主分类商品URL取出來
    public Queue<String> getTagBasicPageUrlsFromTagsSearchUrl() {
        Queue<String> tagBasicPageUrls = new LinkedList<>();

        try {
            PreparedStatement statement = connection.prepareStatement(GET_TAG_BASIC_PAGE_URLS);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                tagBasicPageUrls.offer(resultSet.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tagBasicPageUrls;
    }

    // 将 tags_search_url 表中对应的url标志位置换为true
    public void setFlagFromTagsSearchUrl(List<String> tagBasicPageUrls) {
        PreparedStatement statement;

        for (String tagBasicPageUrl : tagBasicPageUrls) {
            try {
                statement = connection.prepareStatement(SET_FLAG_FROM_TAGS_SEARCH_URL);
                statement.setString(1, tagBasicPageUrl);
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // 将商品详情页的url放进 goods_details_url
    public void saveGoodsDetailsUrlsToGoodsDetailsUrl(List<String> goodsDetailsUrls) {
        PreparedStatement statement;

        for (String goodsDetailsUrl : goodsDetailsUrls) {
            try {
                statement = connection.prepareStatement(SAVE_GOODS_DETAILS_URLS);
                statement.setString(1, goodsDetailsUrl);
                statement.execute();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}