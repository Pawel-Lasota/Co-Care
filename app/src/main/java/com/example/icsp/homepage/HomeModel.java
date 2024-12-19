package com.example.icsp.homepage;

/**
 * HomeModel Model Class
 * <p>
 * This class is responsible for structuring the homepage dashboard elements.
 * Each dashboard element has a name and an image attached to it.
 */
public class HomeModel {
    private String name;
    private int imageId;
    public HomeModel(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImageId() {
        return imageId;
    }
}
