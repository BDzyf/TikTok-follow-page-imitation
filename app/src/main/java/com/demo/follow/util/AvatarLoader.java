package com.demo.follow.util;

import android.content.Context;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.demo.follow.R;

/**
 * 头像加载工具类
 * 使用 Glide 从应用的 assets/avatars/ 目录加载圆形头像
 */
public final class AvatarLoader {

    /**
     * 头像文件在 assets 目录中的路径前缀
     * Glide 使用 "file:///android_asset/" 协议访问 assets 资源
     */
    private static final String ASSETS_PREFIX = "file:///android_asset/avatars/";

    /**
     * 私有构造函数，防止实例化
     * 这是一个工具类，所有方法均为静态方法
     */
    private AvatarLoader() {
        // 工具类不需要实例化
    }

    /**
     * 加载并显示圆形头像
     * 从 assets/avatars/ 目录加载图片，自动进行圆形裁剪
     *
     * @param context    Android 上下文，用于 Glide 初始化
     * @param imageView  要显示头像的 ImageView 控件
     * @param fileName   头像文件名，不需要包含路径前缀
     */
    public static void load(@NonNull Context context,
                            @NonNull ImageView imageView,
                            String fileName) {
        Glide.with(context)
                .load(ASSETS_PREFIX + fileName)
                .circleCrop()
                .placeholder(R.drawable.ic_default_head)
                .into(imageView);
    }
}