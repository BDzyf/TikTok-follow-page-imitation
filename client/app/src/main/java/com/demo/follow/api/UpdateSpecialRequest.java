package com.demo.follow.api;

import com.google.gson.annotations.SerializedName;

/**
 * 更新特别关注状态请求模型
 * 用于向服务端发送用户特别关注状态变更请求
 */
public class UpdateSpecialRequest {

    /**
     * 是否设置为特别关注
     */
    @SerializedName("isSpecial")
    private boolean isSpecial;

    /**
     * 构造函数
     * @param isSpecial 新的特别关注状态
     */
    public UpdateSpecialRequest(boolean isSpecial) {
        this.isSpecial = isSpecial;
    }
}
