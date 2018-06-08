package com.qf58.exec.redpackage;

import com.qf58.exec.redis.RedisJava;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import redis.clients.jedis.Jedis;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @author: zhubo
 * @description:
 * @time: 2018年06月07日
 * @modifytime:
 */
public class RedDraw {
    public static void main(String[] args) throws Exception{

        Executor executor = new ThreadPoolExecutor(200,200,5000, TimeUnit.MILLISECONDS,new LinkedBlockingDeque<>(100),Executors.defaultThreadFactory());
        Jedis jedis = RedisJava.getResource();
        InputStream inputStream = RedDraw.class.getClassLoader().getResourceAsStream("red/red.lua");
        List<String> strings = IOUtils.readLines(inputStream, Charset.forName("utf-8"));
        StringBuilder sb = new StringBuilder();
        for (String s: strings ) {
            sb.append(s).append("\n");
        }

        String lua = sb.toString();
        String hashsha = jedis.scriptLoad(lua);
        try{
            for (int i = 0; i < 50 ; i++){
                List<String> info = new ArrayList<>();
                info.add(""+i);
                info.add("name" + i);
                info.add("http://www.baidu.com/"+i);
                executor.execute(new DrawExec(i*200+1 , (i+1)*200 , hashsha));
            }
            Thread.sleep(50*1000L);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            jedis.scriptFlush();
        }

    }


}
