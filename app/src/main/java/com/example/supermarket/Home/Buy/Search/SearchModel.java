package com.example.supermarket.Home.Buy.Search;

//class utk menampung attribute item list recycler view
public class SearchModel {

    private String recent, id;

    public SearchModel(String recent, String id) {
        this.recent = recent;
        this.id = id;
    }

    public String getRecent() {
        return recent;
    }

    public void setRecent(String recent) {
        this.recent = recent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
