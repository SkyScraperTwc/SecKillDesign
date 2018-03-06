package com.scut.seckill.concurrent;

import com.scut.seckill.entity.Product;
import com.scut.seckill.mapper.SecKillMapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Data
@Component
public class AtomicStock {

    private AtomicInteger samsungInteger = new AtomicInteger();

    private AtomicInteger huaweiInteger = new AtomicInteger();

    private AtomicInteger xiaomiInteger = new AtomicInteger();

    private AtomicInteger iphoneInteger = new AtomicInteger();

    @Autowired
    private SecKillMapper secKillMapper;

    @PostConstruct
    public void initAtomicInteger() {
        List<Product> productList = secKillMapper.getAllProduct();
        for (Product product : productList) {
            getAtomicInteger(product.getProductName()).set(product.getStock());

        }
    }

    public AtomicInteger getAtomicInteger(String productName) {
        AtomicInteger ai = null;
        if (productName != null && !productName.isEmpty()){
            switch (productName){
                case "iphone":
                    ai = iphoneInteger;
                    break;
                case "huawei":
                    ai = huaweiInteger;
                    break;
                case "samsung":
                    ai = samsungInteger;
                    break;
                case "xiaomi":
                    ai = xiaomiInteger;
                    break;
            }
        }
        return ai;
    }
}
