package com.example.supermarket.Home.Buy;

//class utk menampung attribute2 yang dibutuhkan oleh item list recycler view
public class ItemModel {

    private String id,alamat, imageURL, kategori, kondisi, nama_produk, by, uid, deskripsi;
    private int stok, harga,numOfRating;
    private float rating;

    public ItemModel(String id, String alamat, String imageURL, String kategori, String kondisi, String nama_produk, String by, String uid, int stok, int harga, float rating, int numOfRating, String deskripsi) {
        this.id = id;
        this.alamat = alamat;
        this.imageURL = imageURL;
        this.kategori = kategori;
        this.kondisi = kondisi;
        this.nama_produk = nama_produk;
        this.by = by;
        this.uid = uid;
        this.stok = stok;
        this.harga = harga;
        this.rating = rating;
        this.numOfRating = numOfRating;
        this.deskripsi = deskripsi;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getKategori() {
        return kategori;
    }

    public void setKategori(String kategori) {
        this.kategori = kategori;
    }

    public String getKondisi() {
        return kondisi;
    }

    public void setKondisi(String kondisi) {
        this.kondisi = kondisi;
    }

    public String getNama_produk() {
        return nama_produk;
    }

    public void setNama_produk(String nama_produk) {
        this.nama_produk = nama_produk;
    }

    public int getStok() {
        return stok;
    }

    public void setStok(int stok) {
        this.stok = stok;
    }

    public int getHarga() {
        return harga;
    }

    public void setHarga(int harga) {
        this.harga = harga;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getNumOfRating() {
        return numOfRating;
    }

    public void setNumOfRating(int numOfRating) {
        this.numOfRating = numOfRating;
    }
}
