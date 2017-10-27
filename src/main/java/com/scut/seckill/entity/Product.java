package com.scut.seckill.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
	
	/**
	 * id
	 */
	private Integer id;
	/**
	 * 产品名称 
	 */
	private String name;
	/**
	 * 价格
	 */
	private BigDecimal price;
	/**
	 * 库存
	 */
	private Integer stock;

	public Product(Integer id){
		this.id = id;
	}
}
  