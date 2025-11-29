package com.demo.follow.api;

import com.google.gson.annotations.SerializedName;

/**
 * 更新备注请求模型
 * 用于向服务端发送用户备注更新请求
 */
public class UpdateRemarkRequest {

    /**
     * 备注内容
     */
    @SerializedName("remark")
    private String remark;

    /**
     * 构造函数
     * @param remark 新的备注内容
     */
    public UpdateRemarkRequest(String remark) {
        this.remark = remark;
    }
}