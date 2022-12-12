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
    @SerializedName("favorite")
    public int favorite;
    @SerializedName("fileName")
    public String fileName;
    @SerializedName("lat")
    public double lat;
    @SerializedName("lng")
    public double lng;
    @SerializedName("favoriteYN")
    public boolean favoriteYN;
    @SerializedName("rating")
    public float rating;

    public DTO_home_coffee(int seq, String nm, String loca, String gu, String address, String tel, int favorite, String fileName) {
        this.seq = seq;
        this.nm = nm;
        this.loca = loca;
        this.gu = gu;
        this.address = address;
        this.tel = tel;
        this.favorite = favorite;
        this.fileName = fileName;
    }

    public DTO_home_coffee(int seq, String nm, String loca, String gu, String address, String tel, int favorite, double lat, double lng, boolean favoriteYN, float rating) {
        this.seq = seq;
        this.nm = nm;
        this.loca = loca;
        this.gu = gu;
        this.address = address;
        this.tel = tel;
        this.favorite = favorite;
        this.lat = lat;
        this.lng = lng;
        this.favoriteYN = favoriteYN;
        this.rating = rating;
    }

    public DTO_home_coffee(int seq, String nm, String address, float rating, String fileName) {
        this.seq = seq;
        this.nm = nm;
        this.address = address;
        this.rating = rating;
        this.fileName = fileName;
    }

    public int getSeq() { return seq; }
    public String getNm() { return nm; }
    public String getLoca() { return loca; }
    public String getGu() { return gu; }
    public String getAddress() { return address; }
    public String getTel() { return tel; }
    public int getFavorite() { return favorite; }
    public String getFileName() { return fileName; }
    public float getRating() { return rating; }
}
