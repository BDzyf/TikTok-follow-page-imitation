package com.demo.follow.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.demo.follow.R;
import com.demo.follow.db.FollowUser;
import com.demo.follow.repository.FollowRepository;

import java.util.List;

/**
 * 关注列表Fragment
 * 负责展示关注的抖音用户列表，支持下拉刷新、分页加载和点击交互
 * 采用延迟加载策略优化首帧渲染性能
 */
public class FollowFragment extends Fragment {

    /**
     * 数据仓库
     */
    private FollowRepository repository;

    /**
     * 列表适配器
     */
    private FollowAdapter adapter;

    /**
     * 标题栏，显示关注数量
     */
    private TextView tvCount;

    /**
     * 下拉刷新控件
     */
    private SwipeRefreshLayout swipeRefresh;

    /**
     * 是否正在刷新中（防重复刷新）
     */
    private boolean isRefreshing = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_follow, container, false);
        repository = new FollowRepository(requireContext());
        initViews(rootView);
        setupRecyclerView(rootView);

        // 延迟到布局完成后初始化，确保首帧流畅
        rootView.post(() -> {
            setupObservers();
            setupListeners();
        });

        return rootView;
    }

    /**
     * 初始化视图组件
     * @param rootView 根布局
     */
    private void initViews(View rootView) {
        tvCount = rootView.findViewById(R.id.tv_title);
        swipeRefresh = rootView.findViewById(R.id.swipe);
    }

    /**
     * 配置RecyclerView列表
     * 设置布局管理器、缓存策略和滚动监听器
     * @param rootView 根布局
     */
    private void setupRecyclerView(View rootView) {
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext());
        recyclerView.setLayoutManager(layoutManager);

        // 配置缓存策略：预加载20个视图，避免滚动时创建
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setHasFixedSize(true);

        // 预取下一屏数据，提升滑动流畅度
        layoutManager.setInitialPrefetchItemCount(10);

        // 滚动监听：距离底部8个位置时触发加载更多
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int totalItemCount = layoutManager.getItemCount();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();

                if (!repository.isLoading() && !repository.isLastPage()
                        && lastVisibleItemPosition >= totalItemCount - 8) {
                    loadMoreData();
                }
            }
        });

        // 初始化适配器
        adapter = new FollowAdapter(repository, (douyinId, status) -> {
            if (status == 0) {
                Toast.makeText(requireContext(), "已取关，无法使用", Toast.LENGTH_SHORT).show();
            } else {
                UserActionBottomSheet.showForUser(getChildFragmentManager(), douyinId);
            }
        });
        recyclerView.setAdapter(adapter);
    }

    /**
     * 设置数据观察者
     * 监听关注列表数据和数量的变化
     */
    private void setupObservers() {
        // 监听服务端关注数量
        repository.getServerFollowCount().observe(getViewLifecycleOwner(), count -> {
            tvCount.setText("我的关注（" + count + "人）");
        });
        repository.loadFollowCountFromServer();

        // 监听本地关注列表数据
        repository.getAll().observe(getViewLifecycleOwner(), list -> {
            adapter.submitList(list);
            if (swipeRefresh.isRefreshing()) {
                swipeRefresh.setRefreshing(false);
                isRefreshing = false;
            }
        });

        // 延迟500ms启动首次加载，确保首帧渲染完成
        swipeRefresh.postDelayed(() -> {
            if (adapter.getItemCount() == 0) {
                swipeRefresh.setRefreshing(true);
                loadMoreData();
            }
        }, 500);
    }

    /**
     * 设置事件监听器
     * 配置下拉刷新功能
     */
    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(() -> {
            // 防重复刷新检查
            if (isRefreshing) {
                Toast.makeText(requireContext(), "正在刷新中，请稍候", Toast.LENGTH_SHORT).show();
                swipeRefresh.setRefreshing(false);
                return;
            }

            isRefreshing = true;
            repository.loadFollowCountFromServer();

            // 清理已取消关注的用户，然后重置分页并重新加载
            repository.refreshData(new FollowRepository.RefreshCallback() {
                @Override
                public void onComplete() {
                    repository.resetPaging();
                    // 在主线程空闲时执行加载
                    swipeRefresh.post(() -> loadMoreData());
                }

                @Override
                public void onError(Exception e) {
                    swipeRefresh.post(() -> {
                        Toast.makeText(requireContext(), "刷新失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                        isRefreshing = false;
                    });
                }
            });
        });
    }

    /**
     * 加载更多数据
     * 从服务端分页获取关注列表
     */
    private void loadMoreData() {
        repository.syncFromServer(new FollowRepository.LoadCallback() {
            @Override
            public void onLoaded(List<FollowUser> users, boolean isLast) {
                swipeRefresh.setRefreshing(false);
                isRefreshing = false;
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(requireContext(), "加载失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                swipeRefresh.setRefreshing(false);
                isRefreshing = false;
            }
        });
    }
}