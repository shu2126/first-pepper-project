package com.example.myfirstrobot;

public class DataItem {
    private String content;
    private int viewType;

    public DataItem(String content, int viewType){
        this.content = content;
        this.viewType = viewType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

}
