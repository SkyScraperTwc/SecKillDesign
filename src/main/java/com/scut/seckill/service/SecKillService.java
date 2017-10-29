package com.scut.seckill.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.scut.seckill.cache.RedisCacheHandle;
import com.scut.seckill.common.SecKillEnum;
import com.scut.seckill.entity.Product;
import com.scut.seckill.entity.Record;
import com.scut.seckill.entity.User;
import com.scut.seckill.exception.SecKillException;
import com.scut.seckill.mapper.SecKillMapper;
import com.scut.seckill.utils.SecKillUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;

import java.util.Date;
import java.util.Map;
@Slf4j
@Service
public class SecKillService {

    @Autowired
    private RedisCacheHandle redisCacheHandle;

    @Autowired
    private SecKillMapper secKillMapper;

    /**
     * MySQL数据库悲观锁
     * @param paramMap
     * @return
     */
    @Transactional
    public SecKillEnum handleByPessLockInMySQL(Map<String, Object> paramMap) {
        Jedis jedis = redisCacheHandle.getJedis();
        Record record = null;
        Integer userId = (Integer) paramMap.get("userId");
        Integer productId = (Integer)paramMap.get("productId");
        User user = new User(userId);
        Map<String,String> map = jedis.hgetAll("product_"+productId);
        Product product = JSON.parseObject(JSON.toJSONString(map),new TypeReference<Product>(){});
        String hasBySetKey = SecKillUtils.getRedisHasBySetKey(product.getProductName());

        //判断是否重复购买
        boolean isBuy = jedis.sismember(hasBySetKey, user.getId().toString());
        if (isBuy){
            //重复秒杀
            throw new SecKillException(SecKillEnum.REPEAT);
        }
        boolean secKillSuccess = secKillMapper.updatePessLockInMySQL(product);
        if (!secKillSuccess){
            //秒杀失败
            throw new SecKillException(SecKillEnum.FAIL);
        }

        //秒杀成功
        record = new Record(null,user,product,SecKillEnum.SUCCESS.getCode(),SecKillEnum.SUCCESS.getMessage(),new Date());
        log.info(record.toString());
        //todo 添加record到消息队列rabbitMq
        boolean insertFlag =  secKillMapper.insertRecord(record);
        //插入record成功
        if (insertFlag){
            long addResult = jedis.sadd(hasBySetKey,user.getId().toString());
            if (addResult>0){
                return SecKillEnum.SUCCESS;
            }else {
                throw new SecKillException(SecKillEnum.REPEAT);
            }
        }else {
            throw new SecKillException(SecKillEnum.SYSTEM_EXCEPTION);
        }
    }
}
