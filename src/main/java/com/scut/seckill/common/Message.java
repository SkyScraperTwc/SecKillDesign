package com.scut.seckill.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message<T> {

    private Head head;

    private T body;

    public Message(SecKillEnum resultEnum, T body){
        this.head = new Head();
        this.head.setStatusCode(resultEnum.getCode());
        this.head.setStatusMessage(resultEnum.getMsg());
        this.body = body;
    }

    public Message(SecKillEnum resultEnum){
        this.head = new Head();
        this.head.setStatusCode(resultEnum.getCode());
        this.head.setStatusMessage(resultEnum.getMsg());
    }

}
