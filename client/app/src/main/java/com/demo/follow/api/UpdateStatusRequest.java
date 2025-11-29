package com.demo.follow.api;

import com.google.gson.annotations.SerializedName;

/**
 * 更新状态请求模型
 * 用于向服务端发送用户关注状态变更请求
 */
public class UpdateStatusRequest {

    /**
     * 关注状态（1:关注, 0:取消关注）
     */
    @SerializedName("status")
    private int status;

    /**
     * 关注时间戳（毫秒），取消关注时为null
     */
    @SerializedName("followTime")
    private Long followTime;

    /**
     * 构造函数
     * @param status 新的关注状态
     * @param followTime 关注时间戳，取消关注时传null
     */
    public UpdateStatusRequest(int status, Long followTime) {
        this.status = status;
        this.followTime = followTime;
    }
}
