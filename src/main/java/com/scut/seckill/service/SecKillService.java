package com.scut.seckill.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.scut.seckill.common.SecKillEnum;
import com.scut.seckill.entity.Product;
import com.scut.seckill.entity.Record;
import com.scut.seckill.entity.User;
import com.scut.seckill.exception.SecKillException;
import com.scut.seckill.mapper.SecKillMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Date;
import java.util.Map;

@Service
public class SecKillService {

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private SecKillMapper secKillMapper;

    public SecKillEnum handleByPessLockInMySQL(Map<String, Object> paramMap) {
        Jedis jedis = jedisPool.getResource();
        Record record = null;
        String username = (String) paramMap.get("username");
        String productId = (String) paramMap.get("productId");
        Map<String,String> map = jedis.hgetAll(username);
        User user = JSON.parseObject(JSON.toJSONString(map),new TypeReference<User>(){});
        Product product = new Product(Integer.valueOf(productId));

        boolean isBuy = jedis.sismember("usernameSet", username);
        if (isBuy){
            record = new Record(null,user,product,SecKillEnum.REPEAT.getCode(),SecKillEnum.REPEAT.getMsg(),new Date());
            //todo 添加record到消息队列rabbitMq
            throw new SecKillException(SecKillEnum.REPEAT);
        }
        boolean secKillSuccess = secKillMapper.updatePessLockInMySQL(product);
        if (secKillSuccess){
            record = new Record(null,user,product,SecKillEnum.SUCCESS.getCode(),SecKillEnum.SUCCESS.getMsg(),new Date());
            //todo 添加record到消息队列rabbitMq
            return SecKillEnum.SUCCESS;
        }else {
            record = new Record(null,user,product,SecKillEnum.FAIL.getCode(),SecKillEnum.FAIL.getMsg(),new Date());
            //todo 添加record到消息队列rabbitMq
            throw new SecKillException(SecKillEnum.FAIL);
        }
    }
}
