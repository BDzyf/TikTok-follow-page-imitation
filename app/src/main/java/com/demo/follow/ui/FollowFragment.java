package com.demo.follow.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import com.demo.follow.R;
import com.demo.follow.db.FollowUser;
import com.demo.follow.repository.FollowRepository;

/**
 * 关注列表 Fragment
 * 展示已关注的用户列表，支持下拉刷新和取消关注操作
 */
public class FollowFragment extends Fragment {

    /**
     * 负责关注数据的管理
     */
    private FollowRepository repository;

    /**
     * 列表适配器
     */
    private FollowAdapter adapter;

    /**
     * 标题文本，显示关注数量
     */
    private TextView tvCount;

    /**
     * 下拉刷新控件
     */
    private SwipeRefreshLayout swipeRefresh;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_follow, container, false);

        // 初始化数据仓库
        repository = new FollowRepository(requireContext());

        // 初始化视图
        initViews(rootView);

        // 设置 RecyclerView
        setupRecyclerView(rootView);

        // 设置数据观察
        setupObservers();

        // 设置事件监听
        setupListeners();

        return rootView;
    }

    /**
     * 初始化视图组件
     */
    private void initViews(View rootView) {
        tvCount = rootView.findViewById(R.id.tv_title);
        swipeRefresh = rootView.findViewById(R.id.swipe);
    }

    /**
     * 配置 RecyclerView
     */
    private void setupRecyclerView(View rootView) {
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setItemViewCacheSize(20);

        adapter = new FollowAdapter(
                repository,
                douyinId -> {
                    FollowUser user = repository.getUserByDouyinIdLive(douyinId).getValue();
                    if (user != null && user.status == 0) {
                        Toast.makeText(requireContext(), "已取关，无法使用", Toast.LENGTH_SHORT).show();
                    } else {
                        UserActionBottomSheet.showForUser(getChildFragmentManager(), douyinId);
                    }
                }
        );
        recyclerView.setAdapter(adapter);
    }

    /**
     * 设置数据观察
     */
    private void setupObservers() {
        repository.getAll().observe(getViewLifecycleOwner(), list -> {
            adapter.submitList(list);
            swipeRefresh.setRefreshing(false);
        });

        repository.getCount().observe(getViewLifecycleOwner(), count ->
                tvCount.setText("我的关注（" + count + "人）")
        );
    }

    /**
     * 设置事件监听
     */
    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(() -> {
            repository.refreshData();
            // 延迟 1 秒结束刷新动画，提升用户体验
            swipeRefresh.postDelayed(() -> swipeRefresh.setRefreshing(false), 1000);
        });
    }
}