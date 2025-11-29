package com.demo.follow.util;

import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * 应用线程执行器管理类
 * 提供网络IO、磁盘IO和主线程的统一调度能力，实现线程分离，避免主线程阻塞
 * 采用单例模式确保全局唯一性
 */
public class AppExecutors {

    /**
     * 单例实例
     */
    private static final AppExecutors INSTANCE = new AppExecutors();

    /**
     * 网络IO线程池：使用缓存线程池，支持并发网络请求
     */
    private final Executor networkIO = Executors.newCachedThreadPool();

    /**
     * 磁盘IO线程池：使用单线程，保证Room数据库事务的顺序性和线程安全
     */
    private final Executor diskIO = Executors.newSingleThreadExecutor();

    /**
     * 主线程执行器：通过Handler将任务投递到主线程Looper
     */
    private final Executor mainThread = new MainThreadExecutor();

    /**
     * 获取单例实例
     * @return AppExecutors实例
     */
    public static AppExecutors getInstance() {
        return INSTANCE;
    }

    /**
     * 获取网络IO线程执行器
     * @return 网络IO线程执行器
     */
    public Executor networkIO() {
        return networkIO;
    }

    /**
     * 获取磁盘IO线程执行器
     * @return 磁盘IO线程执行器
     */
    public Executor diskIO() {
        return diskIO;
    }

    /**
     * 获取主线程执行器
     * @return 主线程执行器
     */
    public Executor mainThread() {
        return mainThread;
    }

    /**
     * 私有构造函数，防止外部实例化
     */
    private AppExecutors() {}

    /**
     * 主线程执行器内部类
     * 通过Handler将Runnable任务投递到主线程执行
     */
    private static class MainThreadExecutor implements Executor {

        /**
         * 主线程Handler
         */
        private final Handler mainHandler = new Handler(Looper.getMainLooper());

        /**
         * 执行任务
         * @param command 待执行的Runnable任务
         */
        @Override
        public void execute(Runnable command) {
            mainHandler.post(command);
        }
    }
}