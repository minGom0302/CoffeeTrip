package com.example.coffeetrip.DTO;

import com.google.gson.annotations.SerializedName;

public class DTO_magnifier {
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
    @SerializedName("lat")
    public double lat;
    @SerializedName("lng")
    public double lng;

    public DTO_magnifier(int seq, String nm, String loca, String gu, String address, String tel, double lat, double lng) {
        this.seq = seq;
        this.nm = nm;
        this.loca = loca;
        this.gu = gu;
        this.address = address;
        this.tel = tel;
        this.lat = lat;
        this.lng = lng;
    }

    public int getSeq() { return seq; }
    public String getNm() { return nm; }
    public String getLoca() { return loca; }
    public String getGu() { return gu; }
    public String getAddress() { return address; }
    public String getTel() { return tel; }
    public double getLat() { return lat; }
    public double getLng() { return lng; }

    @Override
    public String toString() {
        return "DTO_magnifier{" +
                "seq=" + seq +
                ", nm='" + nm + '\'' +
                ", loca='" + loca + '\'' +
                ", gu='" + gu + '\'' +
                ", address='" + address + '\'' +
                ", tel='" + tel + '\'' +
                ", lat=" + lat +
                ", lng=" + lng +
                '}';
    }
}
