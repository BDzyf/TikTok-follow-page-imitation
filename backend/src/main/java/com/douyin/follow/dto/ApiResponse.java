package com.douyin.follow.dto;

/**
 * 统一API响应封装类
 * 用于标准化所有接口的返回格式，包含状态码、消息、数据和时间戳
 * @param <T> 响应数据的泛型类型
 */
public class ApiResponse<T> {

    /**
     * 业务响应码，200表示成功，500表示失败
     */
    private int code;

    /**
     * 响应消息描述
     */
    private String message;

    /**
     * 响应数据载体
     */
    private T data;

    /**
     * 响应时间戳（毫秒）
     */
    private long timestamp;

    /**
     * 构造函数
     * @param code 响应状态码
     * @param message 响应消息
     * @param data 响应数据
     */
    public ApiResponse(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * 构建成功响应
     * @param data 业务数据
     * @param <T> 数据类型
     * @return 成功的ApiResponse实例
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "success", data);
    }

    /**
     * 构建失败响应
     * @param message 错误消息
     * @param <T> 数据类型
     * @return 失败的ApiResponse实例
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(500, message, null);
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}