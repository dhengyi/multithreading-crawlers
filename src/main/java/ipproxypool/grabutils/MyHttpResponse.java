package ipproxypool.grabutils;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;

/**
 * Created by hg_yi on 17-8-11.
 *
 * @Description: 抓取xici代理网站的html源码
 */
public class MyHttpResponse {
    // 使用本机IP进行网站抓取
    public static String getHtml(String url) {
        String entity = null;
        int httpStatus;
        CloseableHttpResponse httpResponse = null;
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // 设置超时处理(猜测setConnectTimeout是与网站建立HTTP链接的时间，setSocketTimeout是从网站获取数据的时间)
        RequestConfig config = RequestConfig.custom().setConnectTimeout(3000).
                setSocketTimeout(3000).build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(config);

        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;" +
                "q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Cache-Control", "no-cache");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Host", "www.xicidaili.com");
        httpGet.setHeader("Pragma", "no-cache");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");

        try {
            // 客户端执行httpGet方法，返回响应
            httpResponse = httpClient.execute(httpGet);

            // 得到服务响应状态码
            httpStatus = httpResponse.getStatusLine().getStatusCode();
            if (httpStatus == 200) {
                entity = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
            } else {
                System.out.println("本机IP抓取xici代理网第一页IP返回状态码：" + httpStatus);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return entity;
    }

    // 对上一个方法的重载，使用代理进行网站爬取
    public static String getHtml(String url, String ip, String port) {
        String entity = null;
        int httpStatus;
        CloseableHttpClient httpClient = HttpClients.createDefault();
        CloseableHttpResponse httpResponse = null;

        // 设置代理访问和超时处理
        HttpHost proxy = new HttpHost(ip, Integer.parseInt(port));
        RequestConfig config = RequestConfig.custom().setProxy(proxy).setConnectTimeout(1000).
                setSocketTimeout(1000).build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(config);

        httpGet.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;" +
                "q=0.9,image/webp,*/*;q=0.8");
        httpGet.setHeader("Accept-Encoding", "gzip, deflate, sdch");
        httpGet.setHeader("Accept-Language", "zh-CN,zh;q=0.8");
        httpGet.setHeader("Cache-Control", "no-cache");
        httpGet.setHeader("Connection", "keep-alive");
        httpGet.setHeader("Host", "www.xicidaili.com");
        httpGet.setHeader("Pragma", "no-cache");
        httpGet.setHeader("Upgrade-Insecure-Requests", "1");
        httpGet.setHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/55.0.2883.87 Safari/537.36");

        try {
            // 客户端执行httpGet方法，返回响应
            httpResponse = httpClient.execute(httpGet);

            // 得到服务响应状态码
            httpStatus = httpResponse.getStatusLine().getStatusCode();
            if (httpStatus == 200) {
                entity = EntityUtils.toString(httpResponse.getEntity(), "utf-8");
                System.out.println("当前线程：" + Thread.currentThread().getName() + ", 使用的代理IP：" +
                        ip + ":" + port + ", 成功抓取xici代理网：" + url);
            } else {
                System.out.println("当前线程：" + Thread.currentThread().getName() + ", 使用的代理IP：" +
                        ip + ":" + port + ", 抓取xici代理网：" + url + ", 返回状态码：" + httpStatus);
            }
        } catch (IOException e) {
            entity = null;
        } finally {
            try {
                if (httpResponse != null) {
                    httpResponse.close();
                }
                httpClient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return entity;
    }
}