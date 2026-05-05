package com.example.proiect.model;

public class Review {
    public int id;
    public int providerId;
    public String comment;
    public float rating;
    public String date;
    public boolean recommend;
    public String username;

    public Review(int id, int providerId, String comment,
                  float rating, String date, boolean recommend) {
        this.id         = id;
        this.providerId = providerId;
        this.comment    = comment;
        this.rating     = rating;
        this.date       = date;
        this.recommend  = recommend;
        this.username   = "";
    }

    public Review(int id, int providerId, String comment,
                  float rating, String date, boolean recommend, String username) {
        this.id         = id;
        this.providerId = providerId;
        this.comment    = comment;
        this.rating     = rating;
        this.date       = date;
        this.recommend  = recommend;
        this.username   = username != null ? username : "";
    }
}
