package com.scut.seckill.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 购买明细记录
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Record {
    /**
     * id
     */
    private Integer id;
    /**
     * 用户
     */
    private User user;
    /**
     * 产品
     */
    private Product product;
    /**
     * 1秒杀成功,0秒杀失败,-1重复秒杀,-2系统异常
     */
    private String state;
    /**
     * 状态的明文标识
     */
    private String stateInfo;
    /**
     * 创建时间
     */
    private Date createTime;
}
