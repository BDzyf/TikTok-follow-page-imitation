package com.demo.follow.ui;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import com.demo.follow.db.FollowUser;
import java.util.Objects;

/**
 * 关注列表差异计算回调
 * 实现DiffUtil.ItemCallback，用于AsyncListDiffer高效计算列表数据差异
 * 优化策略：仅比较影响UI显示的字段，减少不必要的刷新
 */
public class FollowDiffUtil extends DiffUtil.ItemCallback<FollowUser> {

    @Override
    public boolean areItemsTheSame(@NonNull FollowUser oldItem, @NonNull FollowUser newItem) {
        // 使用douyinId作为唯一标识符判断是否为同一用户
        return Objects.equals(oldItem.douyinId, newItem.douyinId);
    }

    @Override
    public boolean areContentsTheSame(@NonNull FollowUser oldItem, @NonNull FollowUser newItem) {
        // 比较所有显示字段，任一不同则触发刷新
        return oldItem.status == newItem.status
                && oldItem.isSpecial == newItem.isSpecial
                && Objects.equals(oldItem.remark, newItem.remark)
                && Objects.equals(oldItem.nick, newItem.nick)
                && Objects.equals(oldItem.avatar, newItem.avatar);
    }
}