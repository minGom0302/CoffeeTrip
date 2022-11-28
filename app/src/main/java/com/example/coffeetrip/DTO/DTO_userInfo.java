package com.example.coffeetrip.DTO;

import com.google.gson.annotations.SerializedName;

public class DTO_userInfo {
    @SerializedName("id")
    public String id;
    @SerializedName("pw")
    public String pw;
    @SerializedName("nm")
    public String nm;
    @SerializedName("nickname")
    public String nickname;
    @SerializedName("address")
    public String address;
    @SerializedName("phone")
    public String phone;

    public DTO_userInfo(String id, String pw, String nm, String nickname, String address, String phone) {
        this.id = id;
        this.pw = pw;
        this.nm = nm;
        this.nickname = nickname;
        this.address = address;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public String getPw() {
        return pw;
    }

    public String getNm() {
        return nm;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "DTO_userInfo{" +
                "id='" + id + '\'' +
                ", pw='" + pw + '\'' +
                ", nm='" + nm + '\'' +
                ", nickname='" + nickname + '\'' +
                ", address='" + address + '\'' +
                ", phone='" + phone + '\'' +
                '}';
    }
}
