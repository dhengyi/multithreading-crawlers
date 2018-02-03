import ipproxypool.ipmodel.IPMessage;
import database.MyRedis;

/**
 * @Author: spider_hgyi
 * @Date: Created in 下午6:05 18-1-31.
 * @Modified By:
 * @Description:
 */
public class Test {
    public static void main(String[] args) {
        Object lock = new Object();

        synchronized (lock) {
        }
    }
}
