package com.example.supermarket.Ongoing;

//class utk menampung attribute2 yang dibutuhkan oleh item list recycler view
public class OngoingModel {

    private String id, date, name, payMethod, address;

    public OngoingModel(String id, String date, String name, String payMethod, String address) {
        this.id = id;
        this.date = date;
        this.name = name;
        this.payMethod = payMethod;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPayMethod() {
        return payMethod;
    }

    public void setPayMethod(String payMethod) {
        this.payMethod = payMethod;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
