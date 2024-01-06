package lk.xrontech.watchparadise.model;

public class Product {
    private String documentId;
    private String title;
    private Double price;
    private Integer quantity;
    private String description;
    private String imagePath;
    private Boolean status;
    private Brand brand;

    public Product() {
    }

    public Product(String documentId, String title, Double price, Integer quantity, String description, String imagePath, Boolean status, Brand brand) {
        this.documentId = documentId;
        this.title = title;
        this.price = price;
        this.quantity = quantity;
        this.description = description;
        this.imagePath = imagePath;
        this.status = status;
        this.brand = brand;
    }

    public String getDocumentId() {
        return documentId;
    }

    public void setDocumentId(String documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand = brand;
    }
}