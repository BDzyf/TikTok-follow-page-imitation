package com.demo.follow.api;

import com.google.gson.annotations.SerializedName;

/**
 * 基础响应模型
 * 封装API调用的通用返回结构，包含状态码和消息
 */
public class BaseResponse {

    /**
     * 业务响应码，200表示成功
     */
    @SerializedName("code")
    private int code;

    /**
     * 响应消息描述
     */
    @SerializedName("message")
    private String message;

    /**
     * 获取响应状态码
     * @return 状态码数值
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取响应消息
     * @return 消息文本
     */
    public String getMessage() {
        return message;
    }

    /**
     * 判断请求是否成功
     * @return true当且仅当code等于200
     */
    public boolean isSuccess() {
        return code == 200;
    }
}