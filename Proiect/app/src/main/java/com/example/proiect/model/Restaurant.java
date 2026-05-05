package com.example.proiect.model;

public class Restaurant {
    public int id;
    public String name;
    public String region;
    public String nodeUrl;
    public String storageCapacity;
    public String peerId;
    public String imageUrl;
    public double latitude;
    public double longitude;
    public String pricePerGB;
    public String uptime;
    public String addedBy;

    public Restaurant(int id, String name, String region, String nodeUrl,
                      String storageCapacity, String peerId, String imageUrl,
                      double latitude, double longitude) {
        this.id              = id;
        this.name            = name;
        this.region          = region;
        this.nodeUrl         = nodeUrl;
        this.storageCapacity = storageCapacity;
        this.peerId          = peerId;
        this.imageUrl        = imageUrl;
        this.latitude        = latitude;
        this.longitude       = longitude;
        this.pricePerGB      = "";
        this.uptime          = "";
        this.addedBy         = "";
    }
}
