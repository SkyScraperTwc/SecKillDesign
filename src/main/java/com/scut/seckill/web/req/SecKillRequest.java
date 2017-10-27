package com.scut.seckill.web.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SecKillRequest {

    private String username;

    private Integer productId;
}
