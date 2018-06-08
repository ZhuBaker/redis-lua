package com.qf58.exec.redpackage;

import com.qf58.exec.redis.RedisJava;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description:
 * @time: 2018年06月07日
 * @modifytime:
 */
public class DrawExec implements Runnable {

    String hashsha = null;
    Integer beginNum = null;
    Integer endNum = null;


    public DrawExec(Integer beginNum, Integer endNum ,  String hashsha ) {
        this.beginNum = beginNum;
        this.endNum = endNum;
        this.hashsha = hashsha;
    }

    @Override
    public void run() {
        System.out.println("BEGIN " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
        Jedis jedis = RedisJava.getResource();
        for(int i = beginNum ; i <= endNum; i++){
            try{
                List<String> userInfo = new ArrayList<>(3);
                userInfo.add(""+i);
                userInfo.add("name" + i);
                userInfo.add("http://xxx" + i);

                Object eval = jedis.evalsha(hashsha, 3, userInfo.toArray(new String[]{}));
                System.out.println(eval);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        jedis.close();
        System.out.println("END " + new SimpleDateFormat("HH:mm:ss").format(new Date()));
    }
}
