package com.scut.seckill;

import com.scut.seckill.cache.RedisCacheHandle;
import com.scut.seckill.constant.RedisCacheConst;
import com.scut.seckill.entity.User;
import com.scut.seckill.mapper.SecKillMapper;
import com.scut.seckill.utils.SecKillUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;
import sun.rmi.runtime.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JUnitTest {

    @Autowired
    private SecKillMapper secKillMapper;

    @Autowired
    private RedisCacheHandle redisCacheHandle;

    @Test
    public void test2() throws InterruptedException {
        Jedis jedis = redisCacheHandle.getJedis();
        boolean isBuy = jedis.sismember(RedisCacheConst.IPHONE_HAS_BUY_SET, "1");
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
        boolean isBuy = jedis.sismember("iphone_has_buy_set", "1");
        System.out.println(isBuy);
        long result = jedis.sadd("iphone_has_buy_set", "2");
        System.out.println(result);

    }
}
