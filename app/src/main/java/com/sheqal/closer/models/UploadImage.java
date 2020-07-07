package com.sheqal.closer.models;

public class UploadImage {

    private String mImageURL;

    public UploadImage(){
        //empty constructor
    }

    public UploadImage(String imageURL){
        mImageURL = imageURL;
    }

    public String getmImageURL(){
        return mImageURL;
    }

    public void setmImageURL(String imageURL){
        mImageURL = imageURL;
    }
}
