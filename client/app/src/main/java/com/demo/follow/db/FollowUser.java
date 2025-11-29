package com.demo.follow.db;

import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "follow", indices = {@Index(value = {"douyinId"}, unique = true)})
public class FollowUser implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    public long uid;

    @NonNull
    public String douyinId;

    public String nick;

    public String avatar;

    public boolean isSpecial;

    @Nullable
    public String remark;

    public long followTime;

    public int status = 1;

    // ===== 新增字段1：不存入数据库 =====
    // 临时存储服务端头像URL，Room会忽略这个字段
    @Ignore
    public String avatarUrl;

    // ===== 新增字段2：需要存入数据库 =====
    // 是否已同步到服务端，需要数据库迁移
    public Boolean synced;

    // ===== 构造函数：Room必须的无参构造 =====
    public FollowUser() {
    }

    // ===== 新增构造函数：方便临时创建对象（带avatarUrl）=====
    // Room会忽略这个构造函数，因为参数不匹配数据库列
    @Ignore
    public FollowUser(String douyinId, String nick, String avatar, String avatarUrl,
                      boolean isSpecial, long followTime) {
        this.douyinId = douyinId;
        this.nick = nick;
        this.avatar = avatar;      // 本地头像路径
        this.avatarUrl = avatarUrl; // 临时服务端URL
        this.isSpecial = isSpecial;
        this.followTime = followTime;
        this.status = 1;
        this.synced = false;      // 默认未同步
    }

    // ===== Parcelable构造函数 =====
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
        synced = in.readByte() != 0;      // 读取synced字段
        // avatarUrl不需要Parcel传递，因为它是临时的
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeLong(uid);
        dest.writeString(douyinId);
        dest.writeString(nick);
        dest.writeString(avatar);
        dest.writeByte((byte) (isSpecial ? 1 : 0));
        dest.writeString(remark);
        dest.writeLong(followTime);
        dest.writeInt(status);
        dest.writeByte((byte) (synced ? 1 : 0)); // 写入synced字段
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

    @Override
    public int describeContents() {
        return 0;
    }
}