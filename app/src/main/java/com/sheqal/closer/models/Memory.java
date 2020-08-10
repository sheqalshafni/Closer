package com.sheqal.closer.models;

import com.google.gson.annotations.SerializedName;

public class Memory {

    @SerializedName("MemoryDate")
    private String MemoryDate;
    @SerializedName("MemoryPhoto")
    private String MemoryPhoto;

    public Memory(){

    }

    public Memory(String memoryDate, String memoryPhoto){
        this.MemoryDate = memoryDate;
        this.MemoryPhoto = memoryPhoto;
    }

    public String getMemoryDate() {
        return MemoryDate;
    }

    public void setMemoryDate(String memoryDate) {
        MemoryDate = memoryDate;
    }

    public String getMemoryPhoto() {
        return MemoryPhoto;
    }

    public void setMemoryPhoto(String memoryPhoto) {
        MemoryPhoto = memoryPhoto;
    }
}
