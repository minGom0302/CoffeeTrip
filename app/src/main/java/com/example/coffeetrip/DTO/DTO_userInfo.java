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
    @SerializedName("sex")
    public String sex;
    @SerializedName("birth")
    public String birth;
    @SerializedName("secAddress")
    public String secAddress;

    public DTO_userInfo(String id, String pw, String nm, String nickname, String address, String phone) {
        this.id = id;
        this.pw = pw;
        this.nm = nm;
        this.nickname = nickname;
        this.address = address;
        this.phone = phone;
    }

    public DTO_userInfo(String id, String pw, String nm, String nickname, String address, String phone, String sex, String birth, String secAddress) {
        this.id = id;
        this.pw = pw;
        this.nm = nm;
        this.nickname = nickname;
        this.address = address;
        this.phone = phone;
        this.sex = sex;
        this.birth = birth;
        this.secAddress = secAddress;
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

    public String getPhone() { return phone; }

    public String getSex() { return sex; }

    public String getBirth() { return birth; }

    public String getSecAddress() { return secAddress; }
}
