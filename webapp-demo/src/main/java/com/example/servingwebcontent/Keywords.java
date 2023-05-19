package com.example.servingwebcontent;

public class Keywords {
    private String keywords;
    private int index;

    public Keywords() {}

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public int getIndex() {
        return index;
    }

    public int indexPlus1(){
        return index++;
    }
    public int indexMinus1(){
        return index--;
    }

    public void setIndex(int index) {
        this.index = index;
    }
}
