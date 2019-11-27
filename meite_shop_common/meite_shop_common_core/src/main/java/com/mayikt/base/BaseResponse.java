package com.mayikt.base;

import lombok.Data;


/**
 * 微服务接口统一返回码
 * @param <T>
 */
@Data
public class BaseResponse<T> {
//  returen code  返回码
	private Integer rtnCode;
//	Message 消息
    private String msg;
//    data 返回
	private T data;

	public BaseResponse() {

	}

	public BaseResponse(Integer rtnCode, String msg, T data) {
		super();
		this.rtnCode = rtnCode;
		this.msg = msg;
		this.data = data;
	}

}
