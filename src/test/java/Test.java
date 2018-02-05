import database.MySQL;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/**
 * @Author: spider_hgyi
 * @Date: Created in 下午6:05 18-1-31.
 * @Modified By:
 * @Description:
 */
public class Test {
    public static void main(String[] args) {
        Set<Thread> threads = Thread.getAllStackTraces().keySet();

        for (Thread thread : threads) {
            System.out.println(thread.getName());
        }
    }
}
