package com.demo.follow.api;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 关注关系网络请求接口
 * 定义与后端服务交互的API端点
 */
public interface ApiService {

    /**
     * 获取分页关注列表
     * @param page 页码，从0开始
     * @param size 每页数量
     * @return 包含分页数据和状态信息的响应
     */
    @GET("/api/follows")
    Call<FollowPageResponse> getFollows(@Query("page") int page, @Query("size") int size);

    /**
     * 更新用户关注状态
     * @param uid 用户唯一标识
     * @param request 包含新状态和关注时间的请求体
     * @return 基础响应结果
     */
    @PUT("/api/follows/{uid}/status")
    Call<BaseResponse> updateStatus(@Path("uid") long uid, @Body UpdateStatusRequest request);

    /**
     * 更新用户特别关注状态
     * @param uid 用户唯一标识
     * @param request 包含特别关注标志的请求体
     * @return 基础响应结果
     */
    @PUT("/api/follows/{uid}/special")
    Call<BaseResponse> updateSpecial(@Path("uid") long uid, @Body UpdateSpecialRequest request);

    /**
     * 更新用户备注信息
     * @param uid 用户唯一标识
     * @param request 包含备注内容的请求体
     * @return 基础响应结果
     */
    @PUT("/api/follows/{uid}/remark")
    Call<BaseResponse> updateRemark(@Path("uid") long uid, @Body UpdateRemarkRequest request);

    /**
     * 获取指定状态的关联用户数量
     * @param status 关注状态（1:关注, 0:取消关注）
     * @return 包含统计数量的响应
     */
    @GET("api/follows/count")
    Call<CountResponse> getFollowCount(@Query("status") int status);
}
