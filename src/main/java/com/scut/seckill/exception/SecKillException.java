package com.scut.seckill.exception;

import com.scut.seckill.common.SecKillEnum;
import lombok.Data;

@Data
public class SecKillException extends RuntimeException {

    private SecKillEnum secKillEnum;

    public SecKillException(SecKillEnum secKillEnum){
        this.secKillEnum = secKillEnum;
    }
}
