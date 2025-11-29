package com.demo.follow.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.AsyncListDiffer;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.demo.follow.R;
import com.demo.follow.db.FollowUser;
import com.demo.follow.repository.FollowRepository;
import com.demo.follow.util.AvatarLoader;
import java.util.List;

/**
 * 关注列表适配器
 * 采用AsyncListDiffer实现异步数据差异计算，优化列表更新性能
 */
public class FollowAdapter extends RecyclerView.Adapter<FollowAdapter.VH> {

    /**
     * 更多操作点击回调接口
     */
    public interface OnMoreClick {
        /**
         * 点击更多按钮时触发
         * @param douyinId 抖音号
         * @param status 当前关注状态
         */
        void click(String douyinId, int status);
    }

    private final FollowRepository repository;
    private final OnMoreClick callback;

    /**
     * 异步列表差异计算工具
     */
    private final AsyncListDiffer<FollowUser> differ = new AsyncListDiffer<>(this, new FollowDiffUtil());

    /**
     * 构造函数
     * @param repository 数据仓库
     * @param callback 更多操作回调
     */
    public FollowAdapter(FollowRepository repository, OnMoreClick callback) {
        this.repository = repository;
        this.callback = callback;
    }

    /**
     * 提交新的数据列表
     * @param list 新的关注用户列表
     */
    public void submitList(List<FollowUser> list) {
        differ.submitList(list);
    }

    @Override
    public int getItemCount() {
        return differ.getCurrentList().size();
    }

    @Override
    public void onViewRecycled(@NonNull VH holder) {
        super.onViewRecycled(holder);
        // 清除Glide缓存，避免图片错位
        Glide.with(holder.itemView.getContext()).clear(holder.avatar);
        holder.cachedDisplayName = null;
        holder.cachedAvatar = null;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_follow, parent, false);
        VH holder = new VH(itemView);

        // 设置关注按钮点击事件
        holder.btn.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                FollowUser user = differ.getCurrentList().get(pos);
                repository.toggleFollow(user.douyinId, new FollowRepository.OperationCallback() {
                    @Override public void onSuccess() {}
                    @Override public void onError(String message) {
                        Toast.makeText(v.getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // 设置更多按钮点击事件
        holder.more.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                FollowUser user = differ.getCurrentList().get(pos);
                callback.click(user.douyinId, user.status);
            }
        });

        // 设置列表项点击事件
        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                FollowUser user = differ.getCurrentList().get(pos);
                String displayName = getDisplayName(user);
                Toast.makeText(v.getContext().getApplicationContext(),
                        "已选中" + displayName, Toast.LENGTH_SHORT).show();
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        FollowUser user = differ.getCurrentList().get(position);

        // 更新显示名称
        String displayName = getDisplayName(user);
        if (!displayName.equals(holder.cachedDisplayName)) {
            holder.name.setText(displayName);
            holder.cachedDisplayName = displayName;
        }

        // 更新特别关注UI状态
        if (holder.lastIsSpecial == null || holder.lastIsSpecial != user.isSpecial) {
            holder.root.setBackgroundColor(user.isSpecial ? 0xFFF5F5F5 : 0xFFFFFFFF);
            holder.tag.setVisibility(user.isSpecial ? View.VISIBLE : View.GONE);
            holder.lastIsSpecial = user.isSpecial;
        }

        // 更新关注按钮状态
        if (holder.lastStatus == null || holder.lastStatus != user.status) {
            holder.btn.setText(user.status == 1 ? "已关注" : "关注");
            holder.btn.setTextColor(user.status == 1 ? 0xFF333333 : 0xFFFFFFFF);
            holder.btn.setBackgroundResource(user.status == 1 ? R.drawable.bg_btn_gray : R.drawable.bg_btn_red);
            holder.lastStatus = user.status;
        }

        // 头像URL变化时才重新加载，避免重复解码
        if (!java.util.Objects.equals(user.avatar, holder.cachedAvatar)) {
            AvatarLoader.load(holder.itemView.getContext(), holder.avatar, user.avatar);
            holder.cachedAvatar = user.avatar;
        }
    }

    /**
     * 获取显示名称（优先使用备注）
     * @param user 用户对象
     * @return 显示的文本
     */
    private String getDisplayName(FollowUser user) {
        if (user.remark != null && !user.remark.isEmpty()
                && !"设置备注".equals(user.remark)
                && !"请输入备注".equals(user.remark)) {
            return user.remark;
        }
        return user.nick;
    }

    /**
     * ViewHolder内部类
     */
    static class VH extends RecyclerView.ViewHolder {
        ViewGroup root;
        TextView name;
        TextView btn;
        TextView tag;
        ImageView avatar;
        ImageView more;

        // 缓存字段，避免重复刷新
        Boolean lastIsSpecial = null;
        Integer lastStatus = null;
        String cachedDisplayName = null;
        String cachedAvatar = null;

        VH(View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            name = itemView.findViewById(R.id.tv_name);
            avatar = itemView.findViewById(R.id.avatar);
            btn = itemView.findViewById(R.id.btn_follow);
            more = itemView.findViewById(R.id.btn_more);
            tag = itemView.findViewById(R.id.tv_tag);
        }
    }
}