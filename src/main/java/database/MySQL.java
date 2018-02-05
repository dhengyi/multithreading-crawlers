package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

    // 将抓取下来的带有页面参数的主分类商品URL存储进MySQL数据库中
    public void saveTagBasicPageUrlsToTagsSearchUrl(Queue<String> tagBasicPageUrls) {
        for (String tagBasicPageUrl : tagBasicPageUrls) {
            try {
                PreparedStatement statement = connection.prepareStatement(SAVE_TAG_BASIC_PAGE_URLS);

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
}