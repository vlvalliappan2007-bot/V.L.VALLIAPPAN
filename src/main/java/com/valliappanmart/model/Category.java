package com.valliappanmart.model;

public class Category {
    private Long id;
    private String name;
    private String slug;
    private String description;
    private String iconClass;
    private String bannerImage;
    private int productCount;

    public Category() {
    }

    public Category(Long id, String name, String slug, String description, String iconClass, String bannerImage) {
        this.id = id;
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.iconClass = iconClass;
        this.bannerImage = bannerImage;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getIconClass() {
        return iconClass;
    }

    public void setIconClass(String iconClass) {
        this.iconClass = iconClass;
    }

    public String getBannerImage() {
        return bannerImage;
    }

    public void setBannerImage(String bannerImage) {
        this.bannerImage = bannerImage;
    }

    public int getProductCount() {
        return productCount;
    }

    public void setProductCount(int productCount) {
        this.productCount = productCount;
    }
}
