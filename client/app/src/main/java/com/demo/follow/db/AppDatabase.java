package com.demo.follow.db;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import android.content.Context;

/**
 * 应用数据库管理类
 * 使用Room框架管理本地数据库，包含关注用户表和版本迁移逻辑
 */
@Database(entities = {FollowUser.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    /**
     * 获取关注用户数据访问对象
     * @return FollowDao实例
     */
    public abstract FollowDao followDao();

    /**
     * 数据库单例实例
     */
    private static volatile AppDatabase INSTANCE;

    /**
     * 数据库版本1升级到版本2的迁移配置
     * 添加synced字段用于标记数据是否已同步到服务端
     */
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // 添加synced列，默认值为null（表示未知同步状态）
            database.execSQL("ALTER TABLE follow ADD COLUMN synced INTEGER");
        }
    };

    /**
     * 获取数据库单例
     * @param context 应用上下文
     * @return AppDatabase实例
     */
    public static AppDatabase get(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    AppDatabase.class, "follow.db")
                            // 数据库版本升级时若未定义迁移策略则重建表（开发阶段使用）
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}