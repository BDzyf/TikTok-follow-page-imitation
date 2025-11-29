package com.demo.follow.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

/**
 * 关系页面 ViewPager2 适配器
 * 管理四个标签页：互关、关注、粉丝、朋友
 */
public class RelationshipsPagerAdapter extends FragmentStateAdapter {

    /**
     * 标签页标题数组
     */
    private static final String[] TITLES = {"互关", "关注", "粉丝", "朋友"};

    /**
     * 默认提示文本前缀
     */
    private static final String DEFAULT_EMPTY_TEXT = "暂无";

    /**
     * 默认内容文本
     */
    private static final String DEFAULT_CONTENT_TEXT = "暂无内容";

    /**
     * 构造函数
     * @param fragment 父Fragment
     */
    public RelationshipsPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    /**
     * 创建指定位置的Fragment页面
     * @param position 页面位置
     * @return Fragment实例
     */
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                // 互关页面（占位）
                return PlaceholderFragment.newInstance(DEFAULT_EMPTY_TEXT + "互关");
            case 1:
                // 关注页面
                return new FollowFragment();
            case 2:
                // 粉丝页面（占位）
                return PlaceholderFragment.newInstance(DEFAULT_EMPTY_TEXT + "粉丝");
            case 3:
                // 朋友页面（占位）
                return PlaceholderFragment.newInstance(DEFAULT_EMPTY_TEXT + "朋友");
            default:
                // 默认占位页面
                return PlaceholderFragment.newInstance(DEFAULT_CONTENT_TEXT);
        }
    }

    /**
     * 获取页面总数
     * @return 页面数量
     */
    @Override
    public int getItemCount() {
        return TITLES.length;
    }

    /**
     * 获取指定位置的页面标题
     * @param position 页面位置
     * @return 标题文本
     */
    public String getPageTitle(int position) {
        return TITLES[position];
    }
}