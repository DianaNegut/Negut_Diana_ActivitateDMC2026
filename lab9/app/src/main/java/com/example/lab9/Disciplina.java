package com.example.lab9;

public class Disciplina {
    private final String imageUrl;
    private final String description;
    private final String webLink;

    public Disciplina(String imageUrl, String description, String webLink) {
        this.imageUrl = imageUrl;
        this.description = description;
        this.webLink = webLink;
    }

    public String getImageUrl() { return imageUrl; }
    public String getDescription() { return description; }
    public String getWebLink() { return webLink; }
}
