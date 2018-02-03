package ipproxypool.timer;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;

/**
 * Created by hg_yi on 17-8-11.
 *
 * @Description: 设定IP代理池的更新时间
 */
public class MyTimer {
    public static void startIPProxyPool(Object lock) {
        MyTimeJob job = new MyTimeJob(lock);
        Timer timer = new Timer();

        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();

        // 设置定时任务，从现在开始，每24小时执行一次
        timer.schedule(job, date, 24*60*60*1000);
    }
}