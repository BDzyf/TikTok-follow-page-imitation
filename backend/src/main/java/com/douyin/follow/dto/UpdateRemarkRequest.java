package com.douyin.follow.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 更新备注请求数据传输对象
 * 用于接收客户端发起的用户备注修改请求
 */
public class UpdateRemarkRequest {

    /**
     * 新的备注内容
     */
    @JsonProperty("remark")
    private String remark;

    /**
     * 获取备注内容
     * @return 备注文本
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注内容
     * @param remark 备注文本
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }
}