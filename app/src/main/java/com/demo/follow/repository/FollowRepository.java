package com.demo.follow.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.demo.follow.db.FollowDao;
import com.demo.follow.db.FollowUser;
import com.demo.follow.db.AppDatabase;
import com.demo.follow.util.AppExecutors;

import java.util.List;

/**
 * 数据管理层
 * 统一处理关注相关的数据操作和线程调度
 */
public class FollowRepository {
    private final FollowDao dao;
    private final AppExecutors executors;

    public FollowRepository(Context context) {
        dao = AppDatabase.get(context).followDao();
        executors = AppExecutors.getInstance();
    }

    // ==================== 公共查询方法 ====================

    public LiveData<List<FollowUser>> getAll() {
        return dao.getAllFollow();
    }

    public LiveData<Integer> getCount() {
        return dao.getFollowCount();
    }

    public LiveData<FollowUser> getUserByDouyinIdLive(String douyinId) {
        return dao.getUserByDouyinIdLive(douyinId);
    }

    // ==================== 私有执行方法 ====================

    /**
     * 在 IO 线程中执行数据库操作
     */
    private void execute(Runnable action) {
        executors.diskIO().execute(action);
    }

    // ==================== 数据操作方法 ====================

    public void refreshData() {
        execute(() -> dao.deleteUnfollowedUsers());
    }

    /**
     * 取消关注用户（通过抖音号）
     * @param douyinId 抖音号
     */
    public void unfollowByDouyinId(String douyinId) {
        execute(() -> {
            FollowUser user = dao.getUserByDouyinId(douyinId);
            if (user != null) {
                dao.unfollow(user.uid);
            }
        });
    }

    /**
     * 更新用户备注（通过抖音号）
     * @param douyinId 抖音号
     * @param remark 备注内容
     */
    public void updateRemark(String douyinId, String remark) {
        execute(() -> {
            FollowUser user = dao.getUserByDouyinId(douyinId);
            if (user != null) {
                dao.setRemark(user.uid, remark);
            }
        });
    }

    /**
     * 切换特别关注状态（通过抖音号）
     * @param douyinId 抖音号
     */
    public void toggleSpecial(String douyinId) {
        execute(() -> {
            FollowUser user = dao.getUserByDouyinId(douyinId);
            if (user != null) {
                dao.setSpecial(user.uid, !user.isSpecial);
            }
        });
    }

    /**
     * 切换关注状态（通过抖音号）
     * @param douyinId 抖音号
     */
    public void toggleFollow(String douyinId) {
        execute(() -> {
            FollowUser user = dao.getUserByDouyinId(douyinId);
            if (user == null) {
                return;
            }

            if (user.status == 1) {
                dao.unfollow(user.uid);
            } else {
                user.status = 1;
                dao.insert(user);
            }
        });
    }
}