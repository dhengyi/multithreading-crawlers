package database;

import ipproxypool.ipmodel.IPMessage;
import utilclass.SerializeUtil;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by hg_yi on 17-8-9.
 *
 * @Description: 集成对Redis数据库的操作
 */
public class MyRedis {
    private final Jedis jedis = RedisDB.getJedis();
    // 创建一个读写锁
    private static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    // 将单个ip信息保存在Redis列表中
    public void setIPToList(IPMessage ipMessage) {
        // 首先将ipMessage进行序列化
        byte[] bytes = SerializeUtil.serialize(ipMessage);

//        readWriteLock.writeLock().lock();
        jedis.rpush("ip-proxy-pool".getBytes(), bytes);
//        readWriteLock.writeLock().unlock();
    }

    // 将多个ip信息保存在Redis列表中
    public void setIPToList(List<IPMessage> ipMessages) {
        for (IPMessage ipMessage : ipMessages) {
            // 首先将ipMessage进行序列化
            byte[] bytes = SerializeUtil.serialize(ipMessage);

            readWriteLock.writeLock().lock();
            jedis.rpush("ip-proxy-pool".getBytes(), bytes);
            readWriteLock.writeLock().unlock();
        }
    }

    // 将Redis中保存的对象进行反序列化
    public IPMessage getIPByList() {
//        readWriteLock.writeLock().lock();
        Object o = SerializeUtil.unserialize(jedis.lpop("ip-proxy-pool".getBytes()));
//        readWriteLock.writeLock().unlock();

        return (IPMessage) o;
    }

    // 判断IP代理池是否为空
    public boolean isEmpty() {
        readWriteLock.readLock().lock();
        Long flag = jedis.llen("ip-proxy-pool".getBytes());
        readWriteLock.readLock().unlock();

        return flag <= 0;
    }

    public void deleteKey(String key) {
        jedis.del(key);
    }

    public void close() {
        RedisDB.close(jedis);
    }
}