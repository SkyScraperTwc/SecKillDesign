package com.scut.seckill.common;


import com.scut.seckill.constant.SecKillStateConst;

/**
 * @author twc
 */

public enum SecKillEnum {
    /**
     * 服务级错误
     */
    SUCCESS(SecKillStateConst.SUCCESS,"秒杀成功！"),
    FAIL(SecKillStateConst.FAIL, "秒杀失败！"),
    REPEAT(SecKillStateConst.REPEAT, "重复秒杀！"),
    SYSTEM_EXCEPTION(SecKillStateConst.SYSTEM_EXCEPTION, "系统错误！"),
    ;

    private String code;

    private String msg;

    SecKillEnum(String code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
