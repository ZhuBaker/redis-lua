package com.qf58.exec.redpackage;

import com.qf58.exec.redis.RedisJava;
import redis.clients.jedis.Jedis;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description: 红包预生成程序
 * @time: 2018年06月07日
 * @modifytime:
 */
public class PackageGenerage {

    public static void main(String[] args) {
        List<Integer> aPackage = RadPackageUtils.getPackage(10000, 10000000);
        Jedis jedis = RedisJava.getResource();
        List<String> collect = aPackage.stream().map(item -> {
            return item.toString();
        }).collect(Collectors.toList());
        jedis.lpush("red:list",collect.toArray(new String[]{}));
    }
}
