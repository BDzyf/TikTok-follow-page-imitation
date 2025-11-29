package com.douyin.follow.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.io.Serializable;

/**
 * 关注用户实体类
 * 映射数据库中的follow表，存储用户关注关系信息
 */
@Entity
@Table(name = "follow")
public class FollowUser implements Serializable {

    /**
     * 主键，自增长
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid")
    private Long uid;

    /**
     * 抖音号，唯一标识，不可重复且不能为空
     */
    @Column(name = "douyin_id", unique = true, nullable = false, length = 50)
    private String douyinId;

    /**
     * 用户昵称
     */
    @Column(name = "nick", nullable = false, length = 100)
    private String nick;

    /**
     * 头像文件路径
     */
    @Column(name = "avatar", length = 200)
    private String avatar;

    /**
     * 是否为特别关注，默认为false
     */
    @Column(name = "is_special")
    @JsonProperty("isSpecial")
    private Boolean isSpecial = false;

    /**
     * 用户备注信息
     */
    @Column(name = "remark", length = 255)
    private String remark;

    /**
     * 关注时间戳（毫秒）
     */
    @Column(name = "follow_time", nullable = false)
    private Long followTime;

    /**
     * 关注状态：1-正常关注，0-已取消关注，默认为1
     */
    @Column(name = "status")
    private Integer status = 1;

    /**
     * JPA要求的无参构造函数
     */
    public FollowUser() {}

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public String getDouyinId() {
        return douyinId;
    }

    public void setDouyinId(String douyinId) {
        this.douyinId = douyinId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    @JsonProperty("isSpecial")
    public Boolean getIsSpecial() {
        return isSpecial;
    }

    public void setIsSpecial(Boolean isSpecial) {
        this.isSpecial = isSpecial;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getFollowTime() {
        return followTime;
    }

    public void setFollowTime(Long followTime) {
        this.followTime = followTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}