package com.scut.seckill;

import com.alibaba.fastjson.JSON;
import com.scut.seckill.cache.RedisCacheHandle;
import com.scut.seckill.common.SecKillEnum;
import com.scut.seckill.constant.RedisCacheConst;
import com.scut.seckill.entity.Record;
import com.scut.seckill.entity.User;
import com.scut.seckill.mapper.SecKillMapper;
import com.scut.seckill.utils.SecKillUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Response;
import redis.clients.jedis.Transaction;
import sun.rmi.runtime.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JUnitTest {

    @Autowired
    private SecKillMapper secKillMapper;

    @Autowired
    private RedisCacheHandle redisCacheHandle;

    private Set<String> set = new TreeSet<>();

    @Test
    public void test2() throws InterruptedException {
        Jedis jedis = redisCacheHandle.getJedis();
        boolean isBuy = jedis.sismember(RedisCacheConst.IPHONE_HAS_BOUGHT_SET, "1");
        System.out.println(isBuy);
    }

    @Test
    public void test3() throws InterruptedException {
        String[] array = {"1","2","3","4"};
        Random random = new Random();
        for (int i = 1; i <= 1000; i++) {
            StringBuffer buffer = new StringBuffer("");
            buffer.append(i+",");
            int max=4;
            int min=0;
            int s = random.nextInt(max)%(max-min+1) + min;
            buffer.append(array[s]);
            System.out.println(buffer.toString());
        }
    }

    @Test
    public void createUserSQL() throws IOException {
        List<String> list = new ArrayList<>();
        File file =new File("/usr/twc/gitProject/SecKillDesign/src/main/resources/sql/insert.sql");
        FileWriter fileWritter = new FileWriter(file);
        BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
        for (int i=1;i<=3000;i++){
            StringBuffer buffer = new StringBuffer("INSERT INTO `user` VALUES ('0', ");
            buffer.append("'"+"tom_"+i+"'"+",");
            buffer.append("'13855558888'"+",");
            buffer.append("current_date());");
            list.add(buffer.toString());
        }
        for (String string :list) {
            //INSERT INTO `user` VALUES ('0', 'tom_850','13855558888',current_date());
            System.out.println(string);
            bufferWritter.write(string);
            bufferWritter.newLine();//换行
        }
        bufferWritter.close();
    }

    @Test
    public void test4() throws InterruptedException {
        Jedis jedis = redisCacheHandle.getJedis();
        Transaction tx = jedis.multi();

        Response<Boolean> isBuy = tx.sismember("set", "2");
        System.out.println("isBuy------"+isBuy);

        Response<Long> decrResult = tx.hincrBy("product_1","stock",-1);
        System.out.println("decrResult------"+decrResult);

        tx.sadd("set","1");

        List<Object> resultList = tx.exec();
        System.out.println("-----------");
        System.out.println(Boolean.valueOf(resultList.get(0).toString()));
        System.out.println( Integer.parseInt(resultList.get(1).toString()));
        System.out.println( Integer.parseInt(resultList.get(2).toString()));
    }


    @Test
    public void test5() throws InterruptedException {
        User user = new User(1);
        String json = JSON.toJSONString(user);
        System.out.println(json);
    }
}
