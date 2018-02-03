package database;

import java.sql.*;
import java.util.ResourceBundle;

/**
 * @Author: spider_hgyi
 * @Date: Created in 下午5:48 18-2-1.
 * @Modified By:
 * @Description: JDBC的资源准备
 */
public class MySQLDB {
    private static String URL;
    private static String USERNAME;
    private static String PASSWORD;

    // 加载配置文件
    private static ResourceBundle resourceBundle = ResourceBundle.getBundle("mysql-config");

    // 静态代码块在加载类时只执行一次
    static {
        URL = resourceBundle.getString("jdbc.url");
        USERNAME = resourceBundle.getString("jdbc.username");
        PASSWORD = resourceBundle.getString("jdbc.password");
        String driverClassName = resourceBundle.getString("jdbc.driverClassName");

        try {
            Class.forName(driverClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 得到数据库来链接
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    // 关闭数据库链接
    public static void closeConnection(ResultSet rs, Statement stat, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
            if (stat != null) {
                stat.close();
            }

            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}