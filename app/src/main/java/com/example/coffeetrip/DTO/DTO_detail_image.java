package com.example.coffeetrip.DTO;

import com.google.gson.annotations.SerializedName;

public class DTO_detail_image {
    @SerializedName("seq")
    public int seq;
    @SerializedName("fileName")
    public String fileName;
    @SerializedName("imagePath")
    public String imagePath;
    @SerializedName("uploader")
    public String uploader;
    @SerializedName("shopSeq")
    public int shopSeq;
    @SerializedName("type")
    public int type;

    public DTO_detail_image(int seq, String fileName, String imagePath, String uploader, int shopSeq, int type) {
        this.seq = seq;
        this.fileName = fileName;
        this.imagePath = imagePath;
        this.uploader = uploader;
        this.shopSeq = shopSeq;
        this.type = type;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public String getFileName() {
        return fileName;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String getUploader() {
        return uploader;
    }

    public int getShopSeq() {
        return shopSeq;
    }

    public int getType() {
        return type;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public void setUploader(String uploader) {
        this.uploader = uploader;
    }

    public void setShopSeq(int shopSeq) {
        this.shopSeq = shopSeq;
    }

    public void setType(int type) {
        this.type = type;
    }
}

