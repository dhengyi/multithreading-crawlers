package ipproxypool.operation;

/**
 * @Author: spider_hgyi
 * @Date: Created in 上午11:53 18-1-31.
 * @Modified By:
 * @Description: 执行IP代理池这个后台线程
 */
public class IPProxyPool {
    public static void startExecute(Object lock) {
        Thread ipProxyPool = new Thread(new IPProxyPoolThread(lock));
        ipProxyPool.setName("ip-proxy-pool");
        ipProxyPool.start();
    }
}
