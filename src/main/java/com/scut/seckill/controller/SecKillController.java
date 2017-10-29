package com.scut.seckill.controller;

import com.scut.seckill.common.Message;
import com.scut.seckill.common.SecKillEnum;
import com.scut.seckill.service.SecKillService;
import com.scut.seckill.web.req.SecKillRequest;
import com.scut.seckill.web.vo.SecKillResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/seckill")
@RestController
public class SecKillController {

    @Autowired
    private SecKillService secKillService;

    @RequestMapping(value = "/pessLockInMySQL",method = RequestMethod.POST)
    public Message<SecKillResponse> pessLockInMySQL(@RequestBody Message<SecKillRequest> requestMessage){
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId",requestMessage.getBody().getUserId());
        paramMap.put("productId",requestMessage.getBody().getProductId());
        SecKillEnum secKillEnum = secKillService.handleByPessLockInMySQL(paramMap);
        Message<SecKillResponse> responseMessage = new Message<>(secKillEnum,null);
        return responseMessage;
    }

    @RequestMapping(value = "/test",method = RequestMethod.POST)
    public String test(@RequestBody Message<SecKillRequest> requestMessage){
        System.out.println(requestMessage.getBody().getUserId()+","+requestMessage.getBody().getProductId());
        return "success";
    }
}
