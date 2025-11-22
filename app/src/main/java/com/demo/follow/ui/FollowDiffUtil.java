package com.demo.follow.ui;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import com.demo.follow.db.FollowUser;
import java.util.Objects;

/**
 * 关注用户列表的 DiffUtil 回调
 * 用于高效计算列表数据差异，实现 RecyclerView 的局部刷新
 */
public class FollowDiffUtil extends DiffUtil.ItemCallback<FollowUser> {

    @Override
    public boolean areItemsTheSame(@NonNull FollowUser oldItem, @NonNull FollowUser newItem) {
        // 通过唯一 ID 判断是否为同一用户
        return oldItem.uid == newItem.uid;
    }

    @Override
    public boolean areContentsTheSame(@NonNull FollowUser oldItem, @NonNull FollowUser newItem) {
        // 比较所有展示相关的字段
        return oldItem.status == newItem.status
                && oldItem.isSpecial == newItem.isSpecial
                && Objects.equals(oldItem.remark, newItem.remark)
                && Objects.equals(oldItem.nick, newItem.nick);
    }
}