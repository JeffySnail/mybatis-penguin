package com.coder.enhance;

import com.coder.enhance.annotation.Column;
import com.coder.enhance.annotation.Table;

/**
 * @author jeffy
 * @date 2019/1/22
 **/
@Table(name = "blog_user", comment = "blog user table")
public class BlogUser {

    @Column(name = "user_id", isKey = true, type = "int")
    private int userId;

    @Column(name = "user_name", isKey = true, type = "String")
    private String userName;
    @Column(name = "user_sex", isKey = true, type = "String")
    private int userSex;
    @Column(name = "user_address", isKey = true, type = "String")
    private String userAddress;
    @Column(name = "user_nation", isKey = true, type = "String")
    private String userNation;


    @Override
    public String toString() {
        return "BlogUser{" +
                "userId=" + userId +
                ", userName='" + userName + '\'' +
                ", userSex=" + userSex +
                ", userAddress='" + userAddress + '\'' +
                ", userNation='" + userNation + '\'' +
                '}';
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getUserSex() {
        return userSex;
    }

    public void setUserSex(int userSex) {
        this.userSex = userSex;
    }

    public String getUserAddress() {
        return userAddress;
    }

    public void setUserAddress(String userAddress) {
        this.userAddress = userAddress;
    }

    public String getUserNation() {
        return userNation;
    }

    public void setUserNation(String userNation) {
        this.userNation = userNation;
    }
}
