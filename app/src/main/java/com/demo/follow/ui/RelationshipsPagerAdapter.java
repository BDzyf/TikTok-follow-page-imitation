package com.demo.follow.ui;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import com.demo.follow.ui.FollowFragment;

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
     * 默认提示文本
     */
    private static final String DEFAULT_EMPTY_TEXT = "暂无";
    private static final String DEFAULT_CONTENT_TEXT = "暂无内容";

    public RelationshipsPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return PlaceholderFragment.newInstance(DEFAULT_EMPTY_TEXT + "互关");
            case 1:
                return new FollowFragment();
            case 2:
                return PlaceholderFragment.newInstance(DEFAULT_EMPTY_TEXT + "粉丝");
            case 3:
                return PlaceholderFragment.newInstance(DEFAULT_EMPTY_TEXT + "朋友");
            default:
                return PlaceholderFragment.newInstance(DEFAULT_CONTENT_TEXT);
        }
    }

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