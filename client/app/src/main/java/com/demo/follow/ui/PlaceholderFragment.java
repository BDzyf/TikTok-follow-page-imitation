package com.demo.follow.ui;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * 占位内容Fragment
 * 用于展示简单的文本占位信息，常用于未实现或空页面场景
 */
public class PlaceholderFragment extends Fragment {

    /**
     * 参数键：文本内容
     */
    private static final String ARG_TEXT = "text";

    /**
     * 默认显示的文本
     */
    private static final String DEFAULT_TEXT = "暂无内容";

    /**
     * 默认文本颜色：黑色
     */
    private static final int TEXT_COLOR = 0xFF000000;

    /**
     * 默认文本大小：16sp
     */
    private static final float TEXT_SIZE_SP = 16f;

    /**
     * 默认背景色：浅灰色
     */
    private static final int BACKGROUND_COLOR = 0xFFF8F8F8;

    /**
     * 文本对齐方式：居中
     */
    private static final int GRAVITY_CENTER = Gravity.CENTER;

    /**
     * 创建新的占位Fragment实例
     * @param text 要显示的文本内容
     * @return PlaceholderFragment实例
     */
    public static PlaceholderFragment newInstance(String text) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        String text = (getArguments() != null)
                ? getArguments().getString(ARG_TEXT)
                : DEFAULT_TEXT;

        // 创建并配置TextView
        TextView textView = new TextView(requireContext());
        textView.setText(text);
        textView.setTextColor(TEXT_COLOR);
        textView.setTextSize(TEXT_SIZE_SP);

        FrameLayout.LayoutParams textLayoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                GRAVITY_CENTER
        );
        textView.setLayoutParams(textLayoutParams);

        // 创建并配置FrameLayout容器
        FrameLayout containerLayout = new FrameLayout(requireContext());
        containerLayout.setBackgroundColor(BACKGROUND_COLOR);
        containerLayout.addView(textView);
        containerLayout.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));

        return containerLayout;
    }
}