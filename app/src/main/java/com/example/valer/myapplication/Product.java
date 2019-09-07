package com.example.valer.myapplication;

public class Product {
    private String name;
    private int count;
    private String unit;
    private String unit2;
    private String unit3;
    private String unit4;
    private int bImg;
    private int bImg2;
    private int bImg3;
    private int bImg4;
    private String idTag;

    Product(String name, String unit, String unit2, String unit3, String unit4, int bImg, int bImg2, int bImg3, int bImg4, String idTag){
        this.name = name;
        this.count=0;
        this.unit = unit;
        this.unit2 = unit2;
        this.unit3 = unit3;
        this.unit4 = unit4;
        this.bImg = bImg;
        this.bImg2 = bImg2;
        this.bImg3 = bImg3;
        this.bImg4 = bImg4;
        this.idTag = idTag;
    }
    public String getUnit() {
        return this.unit;
    }
    public String getUnit2() {
        return this.unit2;
    }
    public String getUnit3() { return this.unit3; }
    public String getUnit4() {
        return this.unit4;
    }

    public int getBimg() {
        return this.bImg;
    }
    public int getBimg2() {
        return this.bImg2;
    }
    public int getBimg3() {
        return this.bImg3;
    }
    public int getBimg4() {
        return this.bImg4;
    }

    public void setCount(int count) {
        this.count = count;
    }
    public int getCount() {
        return count;
    }

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }

    public String getIdTag(){
        return this.idTag;
    }
}