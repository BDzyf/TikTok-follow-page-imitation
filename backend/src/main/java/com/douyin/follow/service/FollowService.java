package com.douyin.follow.service;

import com.douyin.follow.dto.ApiResponse;
import com.douyin.follow.entity.FollowUser;
import com.douyin.follow.repository.FollowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 关注业务服务
 * 处理用户关注、取消关注、特别关注、备注修改等业务逻辑，保证数据一致性
 */
@Service
public class FollowService {

    /**
     * 数据访问仓库，用于数据库操作
     */
    @Autowired
    private FollowRepository followRepository;

    /**
     * 分页查询关注列表，按特别关注和时间降序排列
     * @param page 页码，从0开始
     * @param size 每页大小
     * @return 分页结果
     */
    public Page<FollowUser> getFollows(int page, int size) {
        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("isSpecial").descending()
                        .and(Sort.by("followTime").descending())
        );
        return followRepository.findByStatusOrderByIsSpecialDescFollowTimeDesc(1, pageable);
    }

    /**
     * 修改用户关注状态，更新状态时自动设置关注时间
     * @param uid 用户唯一标识
     * @param status 目标状态（1:关注, 0:取消关注）
     * @param followTime 关注时间戳（仅在status=1时有效）
     * @return 操作结果的统一响应
     */
    @Transactional
    public ApiResponse<Void> updateStatus(Long uid, Integer status, Long followTime) {
        if (status == null || (status != 0 && status != 1)) {
            return ApiResponse.error("状态值必须为0或1");
        }

        Optional<FollowUser> userOpt = followRepository.findByUid(uid);
        if (!userOpt.isPresent()) {
            return ApiResponse.error("用户不存在: uid=" + uid);
        }

        FollowUser user = userOpt.get();
        if (user.getStatus().equals(status)) {
            return ApiResponse.success(null);
        }

        user.setStatus(status);
        if (status == 1) {
            if (followTime == null) {
                return ApiResponse.error("关注时必须提供followTime");
            }
            user.setFollowTime(followTime);
        }

        followRepository.save(user);
        return ApiResponse.success(null);
    }

    /**
     * 修改用户特别关注状态
     * @param uid 用户唯一标识
     * @param isSpecial 是否设为特别关注
     * @return 操作结果的统一响应
     */
    @Transactional
    public ApiResponse<Void> updateSpecial(Long uid, Boolean isSpecial) {
        if (isSpecial == null) {
            return ApiResponse.error("isSpecial不能为空");
        }

        Optional<FollowUser> userOpt = followRepository.findByUid(uid);
        if (!userOpt.isPresent()) {
            return ApiResponse.error("用户不存在: uid=" + uid);
        }

        FollowUser user = userOpt.get();
        user.setIsSpecial(isSpecial);
        followRepository.save(user);
        return ApiResponse.success(null);
    }

    /**
     * 修改用户备注信息
     * @param uid 用户唯一标识
     * @param remark 新备注内容
     * @return 操作结果的统一响应
     */
    @Transactional
    public ApiResponse<Void> updateRemark(Long uid, String remark) {
        if (remark == null) {
            return ApiResponse.error("remark不能为空");
        }

        Optional<FollowUser> userOpt = followRepository.findByUid(uid);
        if (!userOpt.isPresent()) {
            return ApiResponse.error("用户不存在: uid=" + uid);
        }

        FollowUser user = userOpt.get();
        user.setRemark(remark);
        followRepository.save(user);
        return ApiResponse.success(null);
    }

    /**
     * 统计指定状态的关联用户数量
     * @param status 关注状态（1:已关注, 0:取消关注）
     * @return 包含统计数量的统一响应
     */
    public ApiResponse<Map<String, Long>> getCountByStatus(Integer status) {
        if (status == null) {
            return ApiResponse.error("status参数不能为空");
        }

        long count = followRepository.countByStatus(status);
        Map<String, Long> result = new HashMap<>();
        result.put("count", count);
        return ApiResponse.success(result);
    }
}