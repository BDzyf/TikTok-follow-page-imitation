package com.demo.follow.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import com.demo.follow.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

/**
 * 关系页面 Fragment
 * 包含四个标签页：互关、关注、粉丝、朋友，使用 ViewPager2 实现滑动切换
 */
public class RelationshipsFragment extends Fragment {

    /**
     * ViewPager2 预加载页面数量
     * 预加载 3 个非当前页面，避免重复创建 Fragment，提升滑动流畅度
     */
    private static final int OFFSCREEN_PAGE_LIMIT = 3;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_relationships, container, false);

        // 初始化并配置 ViewPager2 和 TabLayout
        setupViewPager(rootView);

        return rootView;
    }

    /**
     * 初始化并配置 ViewPager2 和 TabLayout
     * @param rootView 根布局视图
     */
    private void setupViewPager(View rootView) {
        ViewPager2 viewPager = rootView.findViewById(R.id.viewpager);
        TabLayout tabLayout = rootView.findViewById(R.id.tab_layout);

        // 设置适配器并预加载页面
        RelationshipsPagerAdapter adapter = new RelationshipsPagerAdapter(this);
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(OFFSCREEN_PAGE_LIMIT);

        // 关联 TabLayout 和 ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            tab.setText(adapter.getPageTitle(position));
        }).attach();
    }
}
