package com.scut.seckill.mapper;

import com.scut.seckill.entity.Product;
import org.springframework.stereotype.Repository;

@Repository
public interface SecKillMapper{

    boolean updatePessLockInMySQL(Product product);

}
