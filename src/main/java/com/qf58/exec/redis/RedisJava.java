package com.qf58.exec.redis;

import org.apache.commons.io.FileUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Collections;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description:
 * @time: 2018年06月07日
 * @modifytime:
 */
public class RedisJava {

    private static JedisPool pool = null;

    public static JedisPool getPool() {
        if(pool == null){
            JedisPoolConfig config = new JedisPoolConfig();
            //控制一个pool可分配多少个jedis实例，通过pool.getResource()来获取；
            //如果赋值为-1，则表示不限制；如果pool已经分配了maxActive个jedis实例，则此时pool的状态为exhausted(耗尽)。
            config.setMaxTotal(500);
            //控制一个pool最多有多少个状态为idle(空闲的)的jedis实例。
            config.setMinIdle(100);
            //表示当borrow(引入)一个jedis实例时，最大的等待时间，如果超过等待时间，则直接抛出JedisConnectionException；
            config.setMaxWaitMillis(1000*10);
            //在borrow一个jedis实例时，是否提前进行validate操作；如果为true，则得到的jedis实例均是可用的；
            config.setTestOnBorrow(true);

            pool = new JedisPool(config,"172.16.6.95",6379,1000,"hello123");
        }
        return pool;
    }

    public synchronized static Jedis getResource(){
        if(pool == null){
            pool = getPool();
        }
        return pool.getResource();
    }

    // 返还到连接池
    // Deprecated
    // 换成用完之后, redis.close()
    /*
    public static void returnResource(Jedis redis) {
        if (redis != null) {
            pool.returnResource(redis);
        }
    }
    */

    public static void main(String[] args) throws IOException{
        Jedis jedis = getResource();
        /*Object eval = jedis.eval("return {KEYS[1],KEYS[2],ARGV[1],ARGV[2]}", 2, "name", "age", "zhangsan", "20");
        System.out.println(eval);*/

        // Object eval = jedis.eval("return {KEYS[1],KEYS[2],ARGV[1],ARGV[2]}", 2, "name", "age", "zhangsan", "20");

        String path = jedis.getClass().getResource("/").getPath() + "script.lua";
        String s = FileUtils.readFileToString(new File(path), Charset.forName("utf-8"));
        System.out.println(s);
        Object eval = jedis.eval(s, 2, "name", "age", "zhangsan", "20");
        System.out.println(eval);
    }

}
