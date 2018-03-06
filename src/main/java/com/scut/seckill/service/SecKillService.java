package com.scut.seckill.service;

import com.alibaba.fastjson.JSON;
import com.scut.seckill.cache.RedisCacheHandle;
import com.scut.seckill.common.SecKillEnum;
import com.scut.seckill.concurrent.AtomicStock;
import com.scut.seckill.entity.Product;
import com.scut.seckill.entity.Record;
import com.scut.seckill.entity.User;
import com.scut.seckill.exception.SecKillException;
import com.scut.seckill.mapper.SecKillMapper;
import com.scut.seckill.mq.RabbitMQSender;
import com.scut.seckill.utils.SecKillUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.Transaction;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class SecKillService {

    @Autowired
    private RedisCacheHandle redisCacheHandle;

    @Autowired
    private SecKillMapper secKillMapper;

    @Autowired
    private RabbitMQSender rabbitMQSender;

    @Autowired
    private AtomicStock atomicStock;

    /**
     * 利用MySQL的update行锁实现悲观锁
     * @param paramMap
     * @return
     */
    @Transactional
    public SecKillEnum handleByPessLockInMySQL(Map<String, Object> paramMap) {
        Jedis jedis = redisCacheHandle.getJedis();
        Record record;

        Integer userId = (Integer) paramMap.get("userId");
        Integer productId = (Integer)paramMap.get("productId");
        User user = secKillMapper.getUserById(userId);
        Product product = secKillMapper.getProductById(productId);

        String hasBoughtSetKey = SecKillUtils.getRedisHasBoughtSetKey(product.getProductName());
        //判断是否重复购买
        boolean isBuy = jedis.sismember(hasBoughtSetKey, user.getId().toString());
        if (isBuy){
            log.error("用户:"+user.getUsername()+"重复购买商品"+product.getProductName());
            throw new SecKillException(SecKillEnum.REPEAT);
        }
        boolean secKillSuccess = secKillMapper.updatePessLockInMySQL(product);
        if (!secKillSuccess){
            log.error("商品:"+product.getProductName()+"库存不足!");
            throw new SecKillException(SecKillEnum.LOW_STOCKS);
        }

        long result = jedis.sadd(hasBoughtSetKey,user.getId().toString());
        if (result > 0){
            record = new Record(null,user,product,SecKillEnum.SUCCESS.getCode(),SecKillEnum.SUCCESS.getMessage(),new Date());
            log.info(record.toString());
            boolean insertFlag =  secKillMapper.insertRecord(record);
            if (insertFlag){
                log.info("用户:"+user.getUsername()+"秒杀商品："+product.getProductName()+"成功!");
                return SecKillEnum.SUCCESS;
            }else {
                log.error("系统错误!");
                throw new SecKillException(SecKillEnum.SYSTEM_EXCEPTION);
            }
        }else {
            log.error("用户:"+user.getUsername()+"重复秒杀商品"+product.getProductName());
            throw new SecKillException(SecKillEnum.REPEAT);
        }
    }

    /**
     * MySQL加字段version实现乐观锁
     * @param paramMap
     * @return
     */
    @Transactional
    public SecKillEnum handleByPosiLockInMySQL(Map<String, Object> paramMap){
        Jedis jedis = redisCacheHandle.getJedis();
        Record record;

        Integer userId = (Integer) paramMap.get("userId");
        Integer productId = (Integer)paramMap.get("productId");
        User user = secKillMapper.getUserById(userId);
        Product product = secKillMapper.getProductById(productId);

        String hasBoughtSetKey = SecKillUtils.getRedisHasBoughtSetKey(product.getProductName());
        boolean isBuy = jedis.sismember(hasBoughtSetKey, user.getId().toString());
        if (isBuy){
            log.error("用户:"+user.getUsername()+"重复购买商品"+product.getProductName());
            throw new SecKillException(SecKillEnum.REPEAT);
        }

        //库存手动减一
        int lastStock = product.getStock()-1;
        if (lastStock >= 0){
            product.setStock(lastStock);
            boolean secKillSuccess = secKillMapper.updatePosiLockInMySQL(product);
            if (!secKillSuccess){
                log.error("用户:"+user.getUsername()+"秒杀商品"+product.getProductName()+"失败!");
                throw new SecKillException(SecKillEnum.FAIL);
            }
        } else {
            log.error("商品:"+product.getProductName()+"库存不足!");
            throw new SecKillException(SecKillEnum.LOW_STOCKS);
        }

        long addResult = jedis.sadd(hasBoughtSetKey,user.getId().toString());
        if (addResult > 0){
            record = new Record(null,user,product,SecKillEnum.SUCCESS.getCode(),SecKillEnum.SUCCESS.getMessage(),new Date());
            log.info(record.toString());
            boolean insertFlag = secKillMapper.insertRecord(record);
            if (insertFlag){
                log.info("用户:"+user.getUsername()+"秒杀商品"+product.getProductName()+"成功!");
                return SecKillEnum.SUCCESS;
            } else {
                throw new SecKillException(SecKillEnum.SYSTEM_EXCEPTION);
            }
        } else {
            log.error("用户:"+user.getUsername()+"重复秒杀商品:"+product.getProductName());
            throw new SecKillException(SecKillEnum.REPEAT);
        }


    }

    /**
     * redis的watch监控
     * @param paramMap
     * @return
     */
    public SecKillEnum handleByRedisWatch(Map<String, Object> paramMap) {
        Jedis jedis = redisCacheHandle.getJedis();
        Record record;

        Integer userId = (Integer) paramMap.get("userId");
        Integer productId = (Integer)paramMap.get("productId");
        User user = secKillMapper.getUserById(userId);
        Product product = secKillMapper.getProductById(productId);

        String productStockCacheKey = product.getProductName()+"_stock";
        String hasBoughtSetKey = SecKillUtils.getRedisHasBoughtSetKey(product.getProductName());

        //watch开启监控
        jedis.watch(productStockCacheKey);

        //判断是否重复购买，注意这里高并发情形下并不安全
        boolean isBuy = jedis.sismember(hasBoughtSetKey, user.getId().toString());
        if (isBuy){
            log.error("用户:"+user.getUsername()+"重复购买商品"+product.getProductName());
            throw new SecKillException(SecKillEnum.REPEAT);
        }

        String stock = jedis.get(productStockCacheKey);
        if (Integer.parseInt(stock) <= 0) {
            log.error("商品:"+product.getProductName()+"库存不足!");
            throw new SecKillException(SecKillEnum.LOW_STOCKS);
        }

        //开启Redis事务
        Transaction tx = jedis.multi();
        //库存减一
        tx.decrBy(productStockCacheKey,1);
        //执行事务
        List<Object> resultList = tx.exec();

        if (resultList == null || resultList.isEmpty()) {
            jedis.unwatch();
            //watch监控被更改过----物品抢购失败;
            log.error("商品:"+product.getProductName()+",watch监控被更改,物品抢购失败");
            throw new SecKillException(SecKillEnum.FAIL);
        }

        //添加到已买队列
        long addResult = jedis.sadd(hasBoughtSetKey,user.getId().toString());
        if (addResult>0){
            //秒杀成功
            record =  new Record(null,user,product,SecKillEnum.SUCCESS.getCode(),SecKillEnum.SUCCESS.getMessage(),new Date());
            //添加record到rabbitMQ消息队列
            rabbitMQSender.send(JSON.toJSONString(record));
            //返回秒杀成功
            return SecKillEnum.SUCCESS;
        } else {
            //重复秒杀
            //这里抛出RuntimeException异常，redis的decr操作并不会回滚，所以需要手动incr回去
            jedis.incrBy(productStockCacheKey,1);
            throw new SecKillException(SecKillEnum.REPEAT);
        }
    }

    /**
     * AtomicInteger的CAS机制
     * @param paramMap
     * @return
     */
    @Transactional
    public SecKillEnum handleByAtomicInteger(Map<String, Object> paramMap) {
        Jedis jedis = redisCacheHandle.getJedis();
        Record record;

        Integer userId = (Integer) paramMap.get("userId");
        Integer productId = (Integer)paramMap.get("productId");
        User user = secKillMapper.getUserById(userId);
        Product product = secKillMapper.getProductById(productId);

        String hasBoughtSetKey = SecKillUtils.getRedisHasBoughtSetKey(product.getProductName());
        //判断是否重复购买
        boolean isBuy = jedis.sismember(hasBoughtSetKey, user.getId().toString());
        if (isBuy){
            log.error("用户:"+user.getUsername()+"重复购买商品"+product.getProductName());
            throw new SecKillException(SecKillEnum.REPEAT);
        }

        AtomicInteger atomicInteger = atomicStock.getAtomicInteger(product.getProductName());
        int stock = atomicInteger.decrementAndGet();

        if (stock < 0) {
            log.error("商品:"+product.getProductName()+"库存不足, 抢购失败!");
            throw new SecKillException(SecKillEnum.LOW_STOCKS);
        }


        long result = jedis.sadd(hasBoughtSetKey,user.getId().toString());
        if (result > 0){
            record = new Record(null,user,product,SecKillEnum.SUCCESS.getCode(),SecKillEnum.SUCCESS.getMessage(),new Date());
            log.info(record.toString());
            boolean insertFlag =  secKillMapper.insertRecord(record);
            if (insertFlag) {
                //更改物品库存
                secKillMapper.updateByAsynPattern(record.getProduct());
                log.info("用户:"+user.getUsername()+"秒杀商品"+product.getProductName()+"成功!");
                return SecKillEnum.SUCCESS;
            } else {
                log.error("系统错误!");
                throw new SecKillException(SecKillEnum.SYSTEM_EXCEPTION);
            }
        } else {
            log.error("用户:"+user.getUsername()+"重复秒杀商品"+product.getProductName());
            atomicInteger.incrementAndGet();
            throw new SecKillException(SecKillEnum.REPEAT);
        }
    }
}
