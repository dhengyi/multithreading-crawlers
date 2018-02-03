package httpbrower;

import ipproxypool.ipmodel.IPMessage;
import database.MyRedis;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by hg_yi on 17-5-23.
 *
 * @@Description: 对淘宝页面的请求，得到页面的源码
 *
 * setConnectTimeout：设置连接超时时间，单位毫秒.
 * setSocketTimeout：请求获取数据的超时时间，单位毫秒.如果访问一个接口，
 * 多少时间内无法返回数据，就直接放弃此次调用。
 */
public class HttpRequest {
    // 请求淘宝商品分类页面，返回页面实体(使用本机IP)
    public static String getHtml(String requestUrl) {
        String html = null;

        // 创建客户端
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

        // 创建请求Get实例
        HttpGet httpGet = new HttpGet(requestUrl);

        // 设置头部信息进行浏览器模拟行为
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml," +
                "application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch, br");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 " +
                "Safari/537.36");

        try {
            // 客户端执行httpGet方法，返回响应
            CloseableHttpResponse closeableHttpResponse = closeableHttpClient.
                    execute(httpGet);

            // 得到服务响应状态码
            if(closeableHttpResponse.getStatusLine().getStatusCode() == 200) {
                // 得到响应实体
                html = EntityUtils.toString (closeableHttpResponse.getEntity(),
                        "utf-8");
            }
            else {
                System.out.println(closeableHttpResponse.getStatusLine().getStatusCode());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return html;
    }

    // 使用代理IP进行网页的获取
    public static String getHtmlByProxy(String requestUrl, IPMessage ipMessage, Object redisLock) {
        MyRedis myRedis = new MyRedis();
        String html = null;
        CloseableHttpResponse httpResponse;

        // 创建客户端
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

        // 设置代理访问和超时处理
        HttpHost proxy = new HttpHost(ipMessage.getIPAddress(), Integer.parseInt(ipMessage.getIPPort()));
        RequestConfig config = RequestConfig.custom().setProxy(proxy).
              setConnectTimeout(1000).setSocketTimeout(1000).build();

        // 创建请求Get实例
        HttpGet httpGet = new HttpGet(requestUrl);
        httpGet.setConfig(config);

        // 设置头部信息进行浏览器模拟行为
        httpGet.setHeader("Accept", "text/html,application/xhtml+xml," +
                "application/xml;q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch, br");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) " +
                "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 " +
                "Safari/537.36");

        try {
            // 客户端执行httpGet方法，返回响应
             httpResponse = closeableHttpClient.execute(httpGet);
             int statusCode = httpResponse.getStatusLine().getStatusCode();

            // 得到服务响应状态码
            if(statusCode == 200) {
                // 得到响应实体
                html = EntityUtils.toString (httpResponse.getEntity(), "utf-8");
                System.out.println("当前线程：" + Thread.currentThread().getName() + ", 使用的代理IP：" +
                        ipMessage.getIPAddress() + ":" + ipMessage.getIPPort() + ", 成功抓取_淘宝_：" + requestUrl);
            }
            else {
                System.out.println("当前线程：" + Thread.currentThread().getName() + ", 使用的代理IP：" +
                        ipMessage.getIPAddress() + ":" + ipMessage.getIPPort() + ", 抓取_淘宝_：" + requestUrl + ", 返回状态码：" + statusCode);
            }

            ipMessage.initCount();
        } catch (IOException e) {
            html = null;
            ipMessage.setUseCount();
            synchronized (redisLock) {
                myRedis.setIPToList(ipMessage);
            }
            System.out.println("代理：" + ipMessage.getIPAddress() + ":" +
                    ipMessage.getIPPort() + ", 目前连续" + ipMessage.getUseCount() + "次不可用");
        }

        return html;
    }
}
