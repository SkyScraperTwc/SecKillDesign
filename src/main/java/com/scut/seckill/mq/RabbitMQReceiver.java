package com.scut.seckill.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.scut.seckill.entity.Record;
import com.scut.seckill.mapper.SecKillMapper;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RabbitListener(queues = "seckillQueue")
public class RabbitMQReceiver {

    @Autowired
    private SecKillMapper secKillMapper;

    @RabbitHandler
    public void process(String message) throws Exception {
        Record record = JSON.parseObject(message, new TypeReference<Record>(){});
        //插入record
        secKillMapper.insertRecord(record);
        //更改物品库存
        secKillMapper.updateByAsynPattern(record.getProduct());
    }
}
