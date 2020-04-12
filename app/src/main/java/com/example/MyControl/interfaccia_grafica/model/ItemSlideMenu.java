package com.example.MyControl.interfaccia_grafica.model;

public class ItemSlideMenu  {
    private int imgId;
    private String title;

    public ItemSlideMenu(int imgId,String title) {
        this.imgId = imgId;
        this.title=title;
    }

    public int getImgId() {
        return imgId;
    }
    public String getTitle() {
        return title;
    }

}
