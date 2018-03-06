package com.scut.seckill.mapper;

import com.scut.seckill.entity.Product;
import com.scut.seckill.entity.Record;
import com.scut.seckill.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SecKillMapper{

    List<User> getAllUser();

    User getUserById(Integer id);

    List<Product> getAllProduct();

    Product getProductById(Integer id);

    boolean updatePessLockInMySQL(Product product);

    boolean updatePosiLockInMySQL(Product product);

    boolean insertRecord(Record record);

    boolean updateByAsynPattern(Product product);
}
