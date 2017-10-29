package com.scut.seckill.utils;

import com.scut.seckill.constant.RedisCacheConst;

public class SecKillUtils {
    public static String getRedisHasBySetKey(String productName){
        String hasBySet = "";
        if (productName!=null && !productName.isEmpty()){
            switch (productName){
                case "iphone":
                    hasBySet = RedisCacheConst.IPHONE_HAS_BUY_SET;
                    break;
                case "huawei":
                    hasBySet = RedisCacheConst.HUAWEI_HAS_BUY_SET;
                    break;
                case "samsung":
                    hasBySet = RedisCacheConst.SAMSUNG_HAS_BUY_SET;
                    break;
                case "xiaomi":
                    hasBySet = RedisCacheConst.XIAOMI_HAS_BUY_SET;
                    break;
            }
        }
        return hasBySet;
    }
}
