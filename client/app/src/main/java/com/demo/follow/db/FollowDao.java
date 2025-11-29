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
 * 封装对本地数据库中关注用户表的所有操作，包括增删改查及状态管理
 */
@Dao
public interface FollowDao {

    /**
     * 获取所有关注用户列表，按特别关注降序、关注时间降序排列
     * @return 实时更新的关注用户列表
     */
    @Query("SELECT * FROM follow ORDER BY isSpecial DESC, followTime DESC")
    LiveData<List<FollowUser>> getAllFollow();

    /**
     * 获取有效关注用户的数量
     * @return 实时更新的用户数量
     */
    @Query("SELECT COUNT(*) FROM follow WHERE status = 1")
    LiveData<Integer> getFollowCount();

    /**
     * 删除已取消关注的用户记录
     */
    @Query("DELETE FROM follow WHERE status = 0")
    void deleteUnfollowedUsers();

    /**
     * 插入或替换单个用户数据
     * @param user 待保存的用户对象
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FollowUser user);

    /**
     * 更新用户数据
     * @param user 待更新的用户对象
     */
    @Update
    void update(FollowUser user);

    /**
     * 设置用户的特别关注状态
     * @param uid 用户唯一标识
     * @param special 是否设为特别关注
     */
    @Query("UPDATE follow SET isSpecial = :special WHERE uid = :uid")
    void setSpecial(long uid, boolean special);

    /**
     * 设置用户备注信息
     * @param uid 用户唯一标识
     * @param remark 备注内容
     */
    @Query("UPDATE follow SET remark = :remark WHERE uid = :uid")
    void setRemark(long uid, String remark);

    /**
     * 取消关注用户（将状态置为0）
     * @param uid 用户唯一标识
     */
    @Query("UPDATE follow SET status = 0 WHERE uid = :uid")
    void unfollow(long uid);

    /**
     * 根据抖音号同步查询用户
     * @param douyinId 抖音号
     * @return 用户对象，不存在时返回null
     */
    @Query("SELECT * FROM follow WHERE douyinId = :douyinId")
    FollowUser getUserByDouyinId(String douyinId);

    /**
     * 根据抖音号异步查询用户
     * @param douyinId 抖音号
     * @return 实时更新的用户对象
     */
    @Query("SELECT * FROM follow WHERE douyinId = :douyinId")
    LiveData<FollowUser> getUserByDouyinIdLive(String douyinId);

    /**
     * 批量插入或替换用户数据
     * @param users 待保存的用户列表
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<FollowUser> users);

    /**
     * 获取已同步到服务端的用户数量
     * @return 实时更新的已同步用户数量
     */
    @Query("SELECT COUNT(*) FROM follow WHERE synced = 1")
    LiveData<Integer> getSyncedCount();

    /**
     * 获取未同步的旧数据（用于数据清理和同步补偿）
     * @return 未同步的用户列表
     */
    @Query("SELECT * FROM follow WHERE synced = 0 AND status = 1")
    List<FollowUser> getUnsyncedUsers();

    /**
     * 同步方式获取所有关注用户（用于后台任务和特殊场景）
     * @return 关注用户列表
     */
    @Query("SELECT * FROM follow ORDER BY isSpecial DESC, followTime DESC")
    List<FollowUser> getAllFollowSync();
}