package com.demo.follow.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.demo.follow.R;
import com.demo.follow.db.FollowUser;
import com.demo.follow.repository.FollowRepository;
import com.demo.follow.util.AvatarLoader;

/**
 * 关注列表适配器
 * 使用 ListAdapter + DiffUtil 实现高效数据更新
 */
public class FollowAdapter extends ListAdapter<FollowUser, FollowAdapter.VH> {

    /**
     * 更多按钮点击回调接口（传抖音号）
     */
    public interface OnMoreClick {
        void click(String douyinId,int status);
    }

    private final FollowRepository repository;
    private final OnMoreClick callback;

    public FollowAdapter(FollowRepository repository, OnMoreClick callback) {
        super(new FollowDiffUtil());
        this.repository = repository;
        this.callback = callback;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_follow, parent, false);
        return new VH(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        FollowUser user = getItem(position);
        String displayName = getDisplayName(user);

        // 设置用户基本信息
        holder.name.setText(displayName);
        holder.tag.setVisibility(user.isSpecial ? View.VISIBLE : View.GONE);
        holder.root.setBackgroundColor(user.isSpecial ? 0xFFF5F5F5 : 0xFFFFFFFF);
        AvatarLoader.load(holder.itemView.getContext(), holder.avatar, user.avatar);

        // 设置关注按钮状态
        holder.btn.setText(user.status == 1 ? "已关注" : "关注");
        holder.btn.setTextColor(user.status == 1 ? 0xFF333333 : 0xFFFFFFFF);
        holder.btn.setBackgroundResource(user.status == 1 ? R.drawable.bg_btn_gray : R.drawable.bg_btn_red);

        // 设置点击事件
        holder.btn.setOnClickListener(v -> repository.toggleFollow(user.douyinId));
        holder.more.setOnClickListener(v -> callback.click(user.douyinId, user.status));
        holder.itemView.setOnClickListener(v -> {
            Toast.makeText(v.getContext().getApplicationContext(),
                    "已选中" + displayName, Toast.LENGTH_SHORT).show();
        });
    }

    /**
     * 获取显示名称（优先使用备注）
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
     * ViewHolder 类
     */
    static class VH extends RecyclerView.ViewHolder {
        ViewGroup root;
        TextView name;
        TextView btn;
        TextView tag;
        ImageView avatar;
        ImageView more;

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