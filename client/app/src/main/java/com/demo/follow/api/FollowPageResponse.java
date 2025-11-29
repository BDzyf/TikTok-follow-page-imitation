package com.demo.follow.api;

import com.demo.follow.db.FollowUser;
import com.google.gson.annotations.SerializedName;
import java.util.ArrayList;
import java.util.List;

/**
 * 分页响应模型
 * 适配网关标准格式，包含分页数据、总页数、当前页码等信息
 */
public class FollowPageResponse {

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
     * 分页数据包装对象
     */
    @SerializedName("data")
    private PageData data;

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
     * 获取分页数据对象
     * @return 分页数据
     */
    public PageData getData() {
        return data;
    }

    /**
     * 判断请求是否成功
     * @return true当且仅当code等于200
     */
    public boolean isSuccess() {
        return code == 200;
    }

    /**
     * 便捷方法：获取当前页内容列表
     * @return 关注用户列表，若数据为空则返回空列表
     */
    public List<FollowUser> getContent() {
        return (data != null && data.getContent() != null) ? data.getContent() : new ArrayList<>();
    }

    /**
     * 便捷方法：判断是否为最后一页
     * @return true当且仅当是最后一页
     */
    public boolean isLast() {
        return data != null && data.isLast();
    }

    /**
     * 分页数据内部类
     * 包含分页内容、总页数和当前页码
     */
    public static class PageData {

        /**
         * 当前页的内容列表
         */
        @SerializedName("content")
        private List<FollowUser> content;

        /**
         * 总页数
         */
        @SerializedName("totalPages")
        private int totalPages;

        /**
         * 当前页码（从0开始）
         */
        @SerializedName("number")
        private int number;

        /**
         * 获取当前页内容列表
         * @return 关注用户列表
         */
        public List<FollowUser> getContent() {
            return content;
        }

        /**
         * 获取总页数
         * @return 总页数
         */
        public int getTotalPages() {
            return totalPages;
        }

        /**
         * 获取当前页码
         * @return 当前页码（从0开始）
         */
        public int getNumber() {
            return number;
        }

        /**
         * 判断是否为最后一页
         * @return true当且仅当当前页码大于等于总页数减1
         */
        public boolean isLast() {
            return number >= totalPages - 1;
        }
    }
}