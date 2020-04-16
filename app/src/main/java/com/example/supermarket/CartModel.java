package com.example.supermarket;

public class CartModel {

    private String id_cart, id_produk, nama_produk, category, imageURL;
    private int amount, price;

    public CartModel(String id_cart, String id_produk, String nama_produk, String category, String imageURL, int amount, int price) {
        this.id_cart = id_cart;
        this.id_produk = id_produk;
        this.nama_produk = nama_produk;
        this.category = category;
        this.imageURL = imageURL;
        this.amount = amount;
        this.price = price;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getId_cart() {
        return id_cart;
    }

    public void setId_cart(String id_cart) {
        this.id_cart = id_cart;
    }

    public String getId_produk() {
        return id_produk;
    }

    public void setId_produk(String id_produk) {
        this.id_produk = id_produk;
    }

    public String getNama_produk() {
        return nama_produk;
    }

    public void setNama_produk(String nama_produk) {
        this.nama_produk = nama_produk;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }
}
