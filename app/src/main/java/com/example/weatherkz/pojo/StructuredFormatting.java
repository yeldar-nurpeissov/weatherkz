package com.example.weatherkz.pojo;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class StructuredFormatting {
    @SerializedName("main_text")
    @Expose
    private String mainText;

    public String getMainText() {
        return mainText;
    }

    public void setMainText(String mainText) {
        this.mainText = mainText;
    }
}