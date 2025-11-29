package com.douyin.follow.controller;

import com.douyin.follow.dto.ApiResponse;
import com.douyin.follow.dto.UpdateRemarkRequest;
import com.douyin.follow.dto.UpdateSpecialRequest;
import com.douyin.follow.dto.UpdateStatusRequest;
import com.douyin.follow.entity.FollowUser;
import com.douyin.follow.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 关注关系控制器
 * 处理客户端关于用户关注、取消关注、设置特别关注、修改备注等请求
 * 提供分页查询关注列表和统计功能
 */
@RestController
@RequestMapping("/follows")
@CrossOrigin(origins = "*")
public class FollowController {

    @Autowired
    private FollowService followService;

    /**
     * 分页获取关注列表
     * @param page 页码，从0开始，默认为0
     * @param size 每页数量，默认为10
     * @return 包含分页数据的统一响应
     */
    @GetMapping
    public ApiResponse<Page<FollowUser>> getFollows(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<FollowUser> follows = followService.getFollows(page, size);
        return ApiResponse.success(follows);
    }

    /**
     * 更新用户关注状态
     * @param uid 用户唯一标识
     * @param request 包含新状态和关注时间的请求体
     * @return 操作结果的统一响应
     */
    @PutMapping("/{uid}/status")
    public ApiResponse<Void> updateStatus(@PathVariable Long uid, @RequestBody UpdateStatusRequest request) {
        return followService.updateStatus(uid, request.getStatus(), request.getFollowTime());
    }

    /**
     * 更新用户特别关注状态
     * @param uid 用户唯一标识
     * @param request 包含特别关注标志的请求体
     * @return 操作结果的统一响应
     */
    @PutMapping("/{uid}/special")
    public ApiResponse<Void> updateSpecial(@PathVariable Long uid, @RequestBody UpdateSpecialRequest request) {
        return followService.updateSpecial(uid, request.getIsSpecial());
    }

    /**
     * 更新用户备注信息
     * @param uid 用户唯一标识
     * @param request 包含备注内容的请求体
     * @return 操作结果的统一响应
     */
    @PutMapping("/{uid}/remark")
    public ApiResponse<Void> updateRemark(@PathVariable Long uid, @RequestBody UpdateRemarkRequest request) {
        return followService.updateRemark(uid, request.getRemark());
    }

    /**
     * 统计指定状态的关联用户数量
     * @param status 关注状态（1:已关注, 0:取消关注）
     * @return 包含统计数量的统一响应
     */
    @GetMapping("/count")
    public ApiResponse<Map<String, Long>> getCountByStatus(@RequestParam Integer status) {
        return followService.getCountByStatus(status);
    }
}