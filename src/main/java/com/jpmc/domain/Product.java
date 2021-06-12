package com.jpmc.domain;

/**
 * Domain class for Product
 *
 * @author Stanly
 */
public class Product {

    private String name;
    private float price;
    private int quantity;
    private ApplicationEnum type;
    private float adjustPrice;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public ApplicationEnum getType() {
        return type;
    }

    public void setType(ApplicationEnum type) {
        this.type = type;
    }

    public float getAdjustPrice() {
        return adjustPrice;
    }

    public void setAdjustPrice(float adjustPrice) {
        this.adjustPrice = adjustPrice;
    }
}