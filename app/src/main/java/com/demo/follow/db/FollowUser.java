package com.demo.follow.db;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/**
 * 关注用户实体类
 * 对应数据库中的 "follow" 表
 * uid: 内部自增主键
 * douyinId: 抖音号，唯一标识
 */
@Entity(tableName = "follow", indices = {@Index(value = {"douyinId"}, unique = true)})
public class FollowUser implements Parcelable {

    /**
     * 内部自增ID（主键）
     */
    @PrimaryKey(autoGenerate = true)
    public long uid;

    /**
     * 抖音号（唯一）
     */
    @NonNull
    public String douyinId;

    /**
     * 用户昵称
     */
    public String nick;

    /**
     * 用户头像路径
     * 格式：assets/avatars/xxx.jpg
     */
    public String avatar;

    /**
     * 是否特别关注
     */
    public boolean isSpecial;

    /**
     * 用户备注
     */
    @Nullable
    public String remark;

    /**
     * 关注时间（时间戳）
     */
    public long followTime;

    /**
     * 关注状态
     * 1 = 已关注
     * 0 = 已取消
     */
    public int status = 1;

    /**
     * Room 使用的默认构造函数
     */
    public FollowUser() {
    }

    /**
     * Parcelable 构造函数
     * @param in Parcel 数据
     */
    @Ignore
    protected FollowUser(@NonNull Parcel in) {
        uid = in.readLong();
        douyinId = in.readString();
        nick = in.readString();
        avatar = in.readString();
        isSpecial = in.readByte() != 0;
        remark = in.readString();
        followTime = in.readLong();
        status = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        // 写入顺序必须与构造函数读取顺序完全一致
        dest.writeLong(uid);
        dest.writeString(douyinId);
        dest.writeString(nick);
        dest.writeString(avatar);
        dest.writeByte((byte) (isSpecial ? 1 : 0));
        dest.writeString(remark);
        dest.writeLong(followTime);
        dest.writeInt(status);
    }

    public static final Creator<FollowUser> CREATOR = new Creator<FollowUser>() {
        @Override
        public FollowUser createFromParcel(@NonNull Parcel in) {
            return new FollowUser(in);
        }

        @Override
        public FollowUser[] newArray(int size) {
            return new FollowUser[size];
        }
    };
}