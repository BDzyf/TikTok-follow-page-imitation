package com.demo.follow.api;

import com.google.gson.annotations.SerializedName;

/**
 * 数量统计响应模型
 * 用于封装包含计数数据的API返回结果
 */
public class CountResponse {

    /**
     * 业务响应码
     */
    @SerializedName("code")
    private int code;

    /**
     * 响应消息描述
     */
    @SerializedName("message")
    private String message;

    /**
     * 统计数据的包装对象
     */
    @SerializedName("data")
    private CountData data;

    /**
     * 判断请求是否成功
     * @return true当且仅当code等于200
     */
    public boolean isSuccess() {
        return code == 200;
    }

    /**
     * 获取统计数量
     * @return 数量值，若data为null则返回0
     */
    public int getCount() {
        return data != null ? data.count : 0;
    }

    /**
     * 统计数据内部类
     * 包含实际的计数值
     */
    public static class CountData {

        /**
         * 统计数量
         */
        @SerializedName("count")
        int count;
    }
}
