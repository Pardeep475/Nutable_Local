package com.panaceasoft.restaurateur.models;

/**
 * Created by Panacea-Soft on 17/7/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class CategoryRowData {

    private String catId;
    private String catName;
    private String catImage;
    private String subCatId;
    private String subCatName;
    private String subCatImage;

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getCatImage() {
        return catImage;
    }

    public void setCatImage(String catImage) {
        this.catImage = catImage;
    }

    public String getSubCatName() {
        return subCatName;
    }

    public void setSubCatName(String subCatName) {
        this.subCatName = subCatName;
    }

    public String getSubCatImage() {
        return subCatImage;
    }

    public void setSubCatImage(String subCatImage) {
        this.subCatImage = subCatImage;
    }

    public String getSubCatId() {
        return subCatId;
    }

    public void setSubCatId(String subCatId) {
        this.subCatId = subCatId;
    }

}
