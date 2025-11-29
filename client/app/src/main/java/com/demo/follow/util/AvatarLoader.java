package com.demo.follow.util;

import android.content.Context;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.demo.follow.R;

/**
 * 头像加载工具类
 * 封装Glide图片加载配置，提供统一的头像加载能力
 * 采用圆形裁剪、多级缓存策略并禁用动画以提升性能
 */
public final class AvatarLoader {

    /**
     * 头像服务基础URL
     */
    private static final String AVATAR_BASE_URL = "http://10.0.2.2:8080/api";

    /**
     * 头像加载选项配置
     * - 圆形裁剪
     * - 占位图和错误图
     * - 启用磁盘缓存
     * - 禁用动画减少主线程绘制开销
     */
    private static final RequestOptions AVATAR_OPTIONS = new RequestOptions()
            .circleCrop()
            .placeholder(R.drawable.ic_default_head)
            .error(R.drawable.ic_default_head)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .skipMemoryCache(false)
            .dontAnimate();

    /**
     * 私有构造函数，防止实例化
     */
    private AvatarLoader() {}

    /**
     * 加载用户头像
     * @param context 上下文
     * @param imageView 目标ImageView
     * @param fileName 头像文件名，为空时使用默认头像
     */
    public static void load(@NonNull Context context, @NonNull ImageView imageView, String fileName) {
        String loadPath = (fileName != null && !fileName.isEmpty())
                ? AVATAR_BASE_URL + fileName
                : AVATAR_BASE_URL + "/avatars/default.jpg";

        Glide.with(context)
                .load(loadPath)
                .apply(AVATAR_OPTIONS)
                .into(imageView);
    }
}