package com.example.hotels.data;

import java.io.Serializable;

public class CartItem implements Serializable {

    private String id;
    private Hotel hotel;
    private int numberOfDays;
    private String roomType;
    private double totalPrice; // New field for total calculated price

    // Constructor
    public CartItem(Hotel hotel, int numberOfDays, String roomType, double totalPrice) {
        this.hotel = hotel;
        this.numberOfDays = numberOfDays;
        this.roomType = roomType;
        this.totalPrice = totalPrice;
    }

    public CartItem(){}
    public CartItem(String itemName, double totalPrice) {
        this.hotel.setName(  itemName);
        this.totalPrice = totalPrice;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    // Getters and Setters
    public Hotel getHotel() {
        return hotel;
    }

    public void setHotel(Hotel hotel) {
        this.hotel = hotel;
    }

    public int getNumberOfDays() {
        return numberOfDays;
    }

    public void setNumberOfDays(int numberOfDays) {
        this.numberOfDays = numberOfDays;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
