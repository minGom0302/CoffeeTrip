package com.example.coffeetrip.DTO;

import com.google.gson.annotations.SerializedName;

public class DTO_detail_menu {
    @SerializedName("seq")
    public int seq;
    @SerializedName("shopSeq")
    public int shopSeq;
    @SerializedName("imageName")
    public String imageName;
    @SerializedName("imagePath")
    public String imagePath;
    @SerializedName("menuName")
    public String menuName;
    @SerializedName("menuPrice")
    public String menuPrice;
    @SerializedName("type")
    public int type; // 0은 커피, 1은 디저트

    public DTO_detail_menu(int seq, int shopSeq, String imageName, String imagePath, String menuName, String menuPrice, int type) {
        this.seq = seq;
        this.shopSeq = shopSeq;
        this.imageName = imageName;
        this.imagePath = imagePath;
        this.menuName = menuName;
        this.menuPrice = menuPrice;
        this.type = type;
    }

    public int getSeq() { return seq; }

    public int getShopSeq() { return shopSeq; }

    public String getImageName() {
        return imageName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getMenuName() {
        return menuName;
    }

    public String getMenuPrice() {
        return menuPrice;
    }

    public int getType() { return type; }
}
