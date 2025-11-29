package com.douyin.follow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 更新特别关注状态请求数据传输对象
 * 用于接收客户端发起的用户特别关注状态变更请求
 */
public class UpdateSpecialRequest {

    /**
     * 是否设置为特别关注
     */
    @JsonProperty("isSpecial")
    private Boolean isSpecial;

    /**
     * 获取特别关注状态
     * @return true表示特别关注，false表示普通关注
     */
    public Boolean getIsSpecial() {
        return isSpecial;
    }

    /**
     * 设置特别关注状态
     * @param isSpecial 特别关注标志
     */
    public void setIsSpecial(Boolean isSpecial) {
        this.isSpecial = isSpecial;
    }
}