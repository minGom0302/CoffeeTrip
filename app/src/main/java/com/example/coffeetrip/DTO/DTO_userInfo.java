package com.example.coffeetrip.DTO;

import com.google.gson.annotations.SerializedName;

public class DTO_userInfo {
    @SerializedName("id")
    public String id;
    @SerializedName("pw")
    public String pw;
    @SerializedName("nm")
    public String nm;
    @SerializedName("address")
    public String address;

    public DTO_userInfo(String id, String pw, String nm, String address) {
        this.id = id;
        this.pw = pw;
        this.nm = nm;
        this.address = address;
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

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "DTO_userInfo{" +
                "id='" + id + '\'' +
                ", pw='" + pw + '\'' +
                ", nm='" + nm + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
