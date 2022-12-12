package com.example.coffeetrip.DTO;

import com.google.gson.annotations.SerializedName;

public class DTO_detail_review {
    @SerializedName("seq")
    public int seq;
    @SerializedName("shopSeq")
    public int shopSeq;
    @SerializedName("userId")
    public String userId;
    @SerializedName("nickName")
    public String nickName;
    @SerializedName("review")
    public String review;
    @SerializedName("imagePath")
    public String imagePath;
    @SerializedName("imageName")
    public String imageName;
    @SerializedName("rating")
    public float rating;
    @SerializedName("date")
    public String date;

    public DTO_detail_review(int seq, int shopSeq, String userId, String nickName, String review, String imagePath, String imageName, float rating, String date) {
        this.seq = seq;
        this.shopSeq = shopSeq;
        this.userId = userId;
        this.nickName = nickName;
        this.review = review;
        this.imagePath = imagePath;
        this.imageName = imageName;
        this.rating = rating;
        this.date = date;
    }

    public int getSeq() {
        return seq;
    }

    public int getShopSeq() {
        return shopSeq;
    }

    public String getUserId() {
        return userId;
    }

    public String getNickName() {
        return nickName;
    }

    public String getReview() {
        return review;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getImageName() {
        return imageName;
    }

    public float getRating() {
        return rating;
    }

    public String getDate() { return date; }
}
