package com.scut.seckill.mapper;

import com.scut.seckill.entity.Product;
import com.scut.seckill.entity.Record;
import com.scut.seckill.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecKillMapper{

    boolean updatePessLockInMySQL(Product product);

    boolean insertRecord(Record record);

    List<User> getAllUser();

    List<Product> getAllProduct();
}
