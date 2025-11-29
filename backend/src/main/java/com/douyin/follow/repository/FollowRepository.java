package com.douyin.follow.repository;

import com.douyin.follow.entity.FollowUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

/**
 * 关注用户数据访问接口
 * 继承JpaRepository获得CRUD能力，通过方法名约定自动生成SQL查询
 */
@Repository
public interface FollowRepository extends JpaRepository<FollowUser, Long> {

    /**
     * 按状态分页查询用户列表，并按特别关注降序、关注时间降序排序
     * 自动生成SQL：SELECT * FROM follow WHERE status = ? ORDER BY is_special DESC, follow_time DESC
     * @param status 关注状态（1:正常关注, 0:已取消）
     * @param pageable 分页参数
     * @return 分页结果
     */
    Page<FollowUser> findByStatusOrderByIsSpecialDescFollowTimeDesc(Integer status, Pageable pageable);

    /**
     * 根据用户ID查询用户
     * @param uid 用户唯一标识
     * @return Optional包装的用户对象
     */
    Optional<FollowUser> findByUid(Long uid);

    /**
     * 统计指定状态的用户数量
     * @param status 关注状态
     * @return 用户数量
     */
    long countByStatus(Integer status);
}