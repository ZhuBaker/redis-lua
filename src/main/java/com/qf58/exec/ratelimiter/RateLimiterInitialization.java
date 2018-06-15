package com.qf58.exec.ratelimiter;

import com.qf58.exec.redis.RedisJava;
import org.apache.commons.io.FileUtils;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.nio.charset.Charset;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description:
 * @time: 2018年06月14日
 * @modifytime:
 */
public class RateLimiterInitialization {

    public static void main(String[] args) throws Exception {
        Jedis jedis = RedisJava.getResource();
        String path = jedis.getClass().getResource("/").getPath() + "ratelimiter/rate_limiter.lua";
        String lua = FileUtils.readFileToString(new File(path), Charset.forName("utf-8"));
        String key = "rate:limiter";
        String maxPermits = "28";
        String rate = "2";
        String apps = "zhangsan,lisi,wangwu,zhaoliu,qianqi";
        String option = "init";

        Object eval = jedis.eval(lua, 4, key, maxPermits,rate,apps,option);
        System.out.println(eval);
    }
}
