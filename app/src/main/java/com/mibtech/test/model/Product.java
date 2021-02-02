package com.mibtech.optical.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Product implements Serializable {

    private String id, name, slug, subcategory_id, image, other_images, description, status, date_added, category_id, indicator;
    private ArrayList<PriceVariation> priceVariations;
    public double globalStock;

    public Product() {
    }

    public Product(String id, String name, String slug, String subcategory_id, String image, String other_images, String description, String status, String date_added, String category_id, ArrayList<PriceVariation> priceVariations, String indicator) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.subcategory_id = subcategory_id;
        this.image = image;
        this.other_images = other_images;
        this.description = description;
        this.status = status;
        this.date_added = date_added;
        this.priceVariations = priceVariations;
        this.category_id = category_id;
        this.indicator = indicator;
    }

    public String getIndicator() {
        return indicator;
    }

    public String getCategory_id() {
        return category_id;
    }

    public String getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSubcategory_id(String subcategory_id) {
        this.subcategory_id = subcategory_id;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setOther_images(String other_images) {
        this.other_images = other_images;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }

    public String getSubcategory_id() {
        return subcategory_id;
    }

    public String getImage() {
        return image;
    }

    public String getOther_images() {
        return other_images;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public String getDate_added() {
        return date_added;
    }

    public double getGlobalStock() {
        return globalStock;
    }

    public void setGlobalStock(double globalStock) {
        this.globalStock = globalStock;
    }

    public ArrayList<PriceVariation> getPriceVariations() {
        return priceVariations;
    }
}
