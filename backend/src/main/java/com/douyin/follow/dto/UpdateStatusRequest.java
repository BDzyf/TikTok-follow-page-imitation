package com.douyin.follow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 更新关注状态请求数据传输对象
 * 用于接收客户端发起的用户关注状态变更请求，包含状态和关注时间信息
 */
public class UpdateStatusRequest {

    /**
     * 目标状态值（1:关注, 0:取消关注）
     */
    @JsonProperty("status")
    private Integer status;

    /**
     * 关注时间戳（毫秒），仅在关注操作时有效
     */
    @JsonProperty("followTime")
    private Long followTime;

    /**
     * 获取目标状态值
     * @return 状态值
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * 设置目标状态值
     * @param status 状态值
     */
    public void setStatus(Integer status) {
        this.status = status;
    }

    /**
     * 获取关注时间戳
     * @return 时间戳（毫秒）
     */
    public Long getFollowTime() {
        return followTime;
    }

    /**
     * 设置关注时间戳
     * @param followTime 时间戳（毫秒）
     */
    public void setFollowTime(Long followTime) {
        this.followTime = followTime;
    }
}