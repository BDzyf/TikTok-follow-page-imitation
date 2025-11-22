package com.demo.follow.util;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 应用线程执行器
 * 提供全局的线程池管理，统一处理异步任务
 * 采用单例模式，确保整个应用使用相同的线程配置
 */
public class AppExecutors {

    /**
     * 单例实例
     */
    private static final AppExecutors INSTANCE = new AppExecutors();

    /**
     * 获取单例实例
     * @return AppExecutors 实例
     */
    public static AppExecutors getInstance() {
        return INSTANCE;
    }

    /**
     * IO 操作线程执行器（用于数据库、文件等耗时操作）
     * 使用单线程池，保证任务顺序执行，避免并发问题
     */
    private final Executor diskIO = Executors.newSingleThreadExecutor();

    /**
     * 获取磁盘 IO 线程执行器
     * @return Executor 实例
     */
    public Executor diskIO() {
        return diskIO;
    }

    /**
     * 私有构造函数，防止外部实例化
     */
    private AppExecutors() {
        // 单例模式
    }
}
