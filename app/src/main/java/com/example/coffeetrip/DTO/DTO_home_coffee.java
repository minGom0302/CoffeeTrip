package com.example.coffeetrip.DTO;

import com.google.gson.annotations.SerializedName;

public class DTO_home_coffee {
    @SerializedName("seq")
    public int seq;
    @SerializedName("nm")
    public String nm;
    @SerializedName("loca")
    public String loca;
    @SerializedName("gu")
    public String gu;
    @SerializedName("address")
    public String address;
    @SerializedName("tel")
    public String tel;

    public DTO_home_coffee(int seq, String nm, String loca, String gu, String address, String tel) {
        this.seq = seq;
        this.nm = nm;
        this.loca = loca;
        this.gu = gu;
        this.address = address;
        this.tel = tel;
    }

    public int getSeq() { return seq; }
    public String getNm() { return nm; }
    public String getLoca() { return loca; }
    public String getGu() { return gu; }
    public String getAddress() { return address; }
    public String getTel() { return tel; }

    @Override
    public String toString() {
        return "DTO_home_coffee{" +
                "seq=" + seq +
                ", nm='" + nm + '\'' +
                ", loca='" + loca + '\'' +
                ", gu='" + gu + '\'' +
                ", address='" + address + '\'' +
                ", tel='" + tel + '\'' +
                '}';
    }
}
