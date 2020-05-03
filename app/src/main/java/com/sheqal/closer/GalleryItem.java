package com.sheqal.closer;

public class GalleryItem {

    private int galleryImage;
    private String galleryDate;

    public GalleryItem(int _galleryImage, String _galleryDate){
        galleryImage = _galleryImage;
        galleryDate = _galleryDate;
    }

    public int getGalleryImage() {
        return galleryImage;
    }

    public void setGalleryImage(int galleryImage) {
        this.galleryImage = galleryImage;
    }

    public String getGalleryDate() {
        return galleryDate;
    }

    public void setGalleryDate(String galleryDate) {
        this.galleryDate = galleryDate;
    }
}
