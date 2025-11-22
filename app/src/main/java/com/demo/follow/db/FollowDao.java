package com.demo.follow.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * 关注用户数据访问对象
 * 提供关注用户表的增删改查操作
 */
@Dao
public interface FollowDao {

    /**
     * 获取所有关注用户列表，按特别关注和时间排序
     * @return 关注用户列表（LiveData）
     */
    @Query("SELECT * FROM follow ORDER BY isSpecial DESC, followTime DESC")
    LiveData<List<FollowUser>> getAllFollow();

    /**
     * 获取有效关注用户数量
     * @return 关注用户数（LiveData）
     */
    @Query("SELECT COUNT(*) FROM follow WHERE status = 1")
    LiveData<Integer> getFollowCount();

    /**
     * 删除已取消关注的用户
     */
    @Query("DELETE FROM follow WHERE status = 0")
    void deleteUnfollowedUsers();

    /**
     * 插入或替换用户数据
     * @param user 用户对象
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FollowUser user);

    /**
     * 更新用户数据
     * @param user 用户对象
     */
    @Update
    void update(FollowUser user);

    /**
     * 设置用户特别关注状态（通过 uid）
     * @param uid 内部用户ID
     * @param special 是否特别关注
     */
    @Query("UPDATE follow SET isSpecial = :special WHERE uid = :uid")
    void setSpecial(long uid, boolean special);

    /**
     * 设置用户备注（通过 uid）
     * @param uid 内部用户ID
     * @param remark 备注内容
     */
    @Query("UPDATE follow SET remark = :remark WHERE uid = :uid")
    void setRemark(long uid, String remark);

    /**
     * 取消关注用户（通过 uid）
     * @param uid 内部用户ID
     */
    @Query("UPDATE follow SET status = 0 WHERE uid = :uid")
    void unfollow(long uid);

    /**
     * 根据抖音号查询用户
     * @param douyinId 抖音号
     * @return 用户对象
     */
    @Query("SELECT * FROM follow WHERE douyinId = :douyinId")
    FollowUser getUserByDouyinId(String douyinId);

    /**
     * 根据抖音号查询用户（LiveData）
     * @param douyinId 抖音号
     * @return 用户对象（LiveData）
     */
    @Query("SELECT * FROM follow WHERE douyinId = :douyinId")
    LiveData<FollowUser> getUserByDouyinIdLive(String douyinId);
}