package database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Queue;

/**
 * @Author: spider_hgyi
 * @Date: Created in 下午7:37 18-2-1.
 * @Modified By:
 * @Description: 集成对MySQL数据库的操作
 */
public class MySQL {
    private static Connection connection = MySQLDB.getConnection();

    private static final String SAVE_TAG_BASIC_PAGE_URLS = "INSERT INTO tags_search_url(url) VALUES (?)";

    // 将抓取下来的带有页面参数的主分类商品URL存储进MySQL数据库中
    public synchronized static void saveTagBasicPageUrlsToTagsSearchUrl(Queue<String> tagBasicPageUrls) {
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
}
