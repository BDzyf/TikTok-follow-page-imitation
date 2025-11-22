package com.demo.follow.db;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {FollowUser.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract FollowDao followDao();

    private static volatile AppDatabase INSTANCE;

    /**
     * 获取数据库单例
     * 使用预置的 follow_v1.db 文件初始化
     * @param context 应用上下文
     * @return 数据库实例
     */
    public static AppDatabase get(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context, AppDatabase.class, "follow.db")
                            .createFromAsset("follow_v1.db")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}