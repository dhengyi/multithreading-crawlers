package database;

import ipproxypool.ipmodel.IPMessage;
import utilclass.SerializeUtil;
import redis.clients.jedis.Jedis;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by hg_yi on 17-8-9.
 *
 * @Description: 集成对Redis数据库的操作
 *
 * 争取将MyRedis设计成一个线程安全的类
 */
public class MyRedis {
    private final Jedis jedis = RedisDB.getJedis();
    // 创建一个读写锁
    private static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    // 创建一个关于 tagBasicPageURLs-cache 的锁
    private static ReadWriteLock tagBasicPageURLsCacheReadWriteLock = new ReentrantReadWriteLock();

    // 将单个ip信息保存在Redis列表中
    public void setIPToList(IPMessage ipMessage) {
        // 首先将ipMessage进行序列化
        byte[] bytes = SerializeUtil.serialize(ipMessage);

        readWriteLock.writeLock().lock();
        jedis.rpush("ip-proxy-pool".getBytes(), bytes);
        readWriteLock.writeLock().unlock();
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
        readWriteLock.writeLock().lock();
        Object o = SerializeUtil.unserialize(jedis.lpop("ip-proxy-pool".getBytes()));
        readWriteLock.writeLock().unlock();

        return (IPMessage) o;
    }

    // 判断IP代理池是否为空
    public boolean isEmpty() {
        readWriteLock.readLock().lock();
        Long flag = jedis.llen("ip-proxy-pool".getBytes());
        readWriteLock.readLock().unlock();

        return flag <= 0;
    }

    // 将url存储到 tagBasicPageURLs-cache 中
    public void setTagBasicPageURLToCache(String tagBasicPageURL) {
        tagBasicPageURLsCacheReadWriteLock.writeLock().lock();
        jedis.rpush("tag-basic-page-urls-cache", tagBasicPageURL);
        tagBasicPageURLsCacheReadWriteLock.writeLock().unlock();
    }

    // 判断 tagBasicPageURLs-cache 中的url数量是否达到100条
    public boolean tagBasicPageURLsCacheIsOk() {
        tagBasicPageURLsCacheReadWriteLock.readLock().lock();
        Long flag = jedis.llen("tag-basic-page-urls-cache");
        tagBasicPageURLsCacheReadWriteLock.readLock().unlock();

        return flag >= 100;
    }

    // 从 tagBasicPageURLs-cache 中将url取出
    public List<String> getTagBasicPageURLsFromCache() {
        List<String> tagBasicPageURLs = new ArrayList<>();

        tagBasicPageURLsCacheReadWriteLock.writeLock().lock();
        Long flag = jedis.llen("tag-basic-page-urls-cache");

        for (int i = 0; i < flag; i++) {
            String o = jedis.lpop("tag-basic-page-urls-cache");
            tagBasicPageURLs.add(o);
        }
        tagBasicPageURLsCacheReadWriteLock.writeLock().unlock();

        return tagBasicPageURLs;
    }

    // 释放Redis资源
    public void close() {
        RedisDB.close(jedis);
    }
}