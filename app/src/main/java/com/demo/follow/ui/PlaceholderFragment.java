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
 * 占位 Fragment
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_TEXT = "text";
    private static final String DEFAULT_TEXT = "暂无内容";
    private static final int TEXT_COLOR = 0xFF000000; // 黑色
    private static final float TEXT_SIZE_SP = 16f;
    private static final int BACKGROUND_COLOR = 0xFFF8F8F8;
    private static final int GRAVITY_CENTER = Gravity.CENTER;

    /**
     * 创建新的占位 Fragment 实例
     * @param text 要显示的文本内容
     * @return PlaceholderFragment 实例
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
        // 获取文本内容
        String text = (getArguments() != null)
                ? getArguments().getString(ARG_TEXT)
                : DEFAULT_TEXT;

        // 创建并配置 TextView
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

        // 创建并配置 FrameLayout 容器
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
