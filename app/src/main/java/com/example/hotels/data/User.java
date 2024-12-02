package com.example.hotels.data;

public class User extends  Person{

    private String image; // URL or path to the user's profile image

    // Constructor with image
    public User(String name, String email, String password, String address, String image) {
      super(name, email, password, address);
        this.image = image;
    }

    // Default Constructor
    public User() {
        super();
    }

    // Getters


    public String getImage() {
        return image;
    }

    // Setters


    public void setImage(String image) {
        this.image = image;
    }


}
