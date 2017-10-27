package com.scut.seckill.common;

import lombok.Data;

/**
 * @author twc
 */
@Data
public class Head {

    /**
     * 状态码，0成功，1系统异常，2参数异常
     */
    private String statusCode;

    /**
     * 状态信息
     */
    private String statusMessage;

}
