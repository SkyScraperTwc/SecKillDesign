package com.scut.seckill.common;

import com.scut.seckill.exception.SecKillException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = SecKillException.class)
    @ResponseBody
    public Message handleSecKillException(SecKillException secKillException){
        log.info(secKillException.getSecKillEnum().getMessage());
        return new Message(secKillException.getSecKillEnum());
    }
}
