package com.scut.seckill.cache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.scut.seckill.constant.RedisCacheConst;
import com.scut.seckill.entity.Product;
import com.scut.seckill.entity.User;
import com.scut.seckill.mapper.SecKillMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class RedisLoaderListener {

    @Autowired
    private RedisCacheHandle redisCacheHandle;

    @Autowired
    private SecKillMapper secKillMapper;

    @PostConstruct
    public void initRedis(){
        Jedis jedis = redisCacheHandle.getJedis();
        List<Product> productList = secKillMapper.getAllProduct();
        for (Product product:productList) {
            Map<String, String> map = JSON.parseObject(JSON.toJSONString(product), new TypeReference<Map<String, String>>(){});
            jedis.hmset("product_"+product.getId(),map);
        }
        log.info("Redis数据初始化完毕！");
    }
}
