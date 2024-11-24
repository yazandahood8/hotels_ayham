package com.example.hotels.data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class Hotel implements Serializable {
    private String name;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    private String phoneNumber;
    private String email;
    private String website;
    private String imageUrl; // New field for hotel image URL

    private int starRating; // Rating from 1 to 5 stars
    private double reviewRating; // Average user rating, e.g., 4.3
    private int totalRooms;
    private int availableRooms;
    private boolean hasParking;
    private boolean hasWifi;
    private boolean hasSwimmingPool;
    private boolean hasGym;
    private boolean hasSpa;
    private boolean hasRestaurant;
    private boolean hasConferenceRooms;

    private double baseRoomPrice; // Base price for a standard room per night
    private String checkInTime;
    private String checkOutTime;

    private List<String> roomTypes; // E.g., "Standard", "Deluxe", "Suite"
    private List<String> services; // E.g., "Room Service", "Laundry", "Airport Shuttle"
    private Map<String, Double> additionalCharges; // E.g., "Breakfast" -> 15.0, "Parking" -> 10.0

    // Constructor
    public Hotel(String name, String address, String city, String state, String zipCode, String country,
                 String phoneNumber, String email, String website, int starRating, double reviewRating,
                 int totalRooms, int availableRooms, boolean hasParking, boolean hasWifi, boolean hasSwimmingPool,
                 boolean hasGym, boolean hasSpa, boolean hasRestaurant, boolean hasConferenceRooms,
                 double baseRoomPrice, String checkInTime, String checkOutTime, List<String> roomTypes,
                 List<String> services, Map<String, Double> additionalCharges, String imageUrl) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.country = country;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.website = website;
        this.starRating = starRating;
        this.reviewRating = reviewRating;
        this.totalRooms = totalRooms;
        this.availableRooms = availableRooms;
        this.hasParking = hasParking;
        this.hasWifi = hasWifi;
        this.hasSwimmingPool = hasSwimmingPool;
        this.hasGym = hasGym;
        this.hasSpa = hasSpa;
        this.hasRestaurant = hasRestaurant;
        this.hasConferenceRooms = hasConferenceRooms;
        this.baseRoomPrice = baseRoomPrice;
        this.checkInTime = checkInTime;
        this.checkOutTime = checkOutTime;
        this.roomTypes = roomTypes;
        this.services = services;
        this.additionalCharges = additionalCharges;
        this.imageUrl = imageUrl;
    }
    public Hotel() {
    }
    // Getters and Setters for all attributes (examples given for key fields)
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public int getStarRating() { return starRating; }
    public void setStarRating(int starRating) { this.starRating = starRating; }

    public double getBaseRoomPrice() { return baseRoomPrice; }
    public void setBaseRoomPrice(double baseRoomPrice) { this.baseRoomPrice = baseRoomPrice; }

    public List<String> getRoomTypes() { return roomTypes; }
    public void setRoomTypes(List<String> roomTypes) { this.roomTypes = roomTypes; }

    public Map<String, Double> getAdditionalCharges() { return additionalCharges; }
    public void setAdditionalCharges(Map<String, Double> additionalCharges) { this.additionalCharges = additionalCharges; }

    // Additional methods for hotel management
    public boolean isRoomAvailable() {
        return availableRooms > 0;
    }

    public void bookRoom() {
        if (availableRooms > 0) {
            availableRooms--;
        } else {
            System.out.println("No rooms available for booking.");
        }
    }

    public void cancelBooking() {
        if (availableRooms < totalRooms) {
            availableRooms++;
        } else {
            System.out.println("All rooms are already available.");
        }
    }
    public String getImageUrl() {
        return imageUrl;
    }
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public double getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(double reviewRating) {
        this.reviewRating = reviewRating;
    }

    public int getTotalRooms() {
        return totalRooms;
    }

    public void setTotalRooms(int totalRooms) {
        this.totalRooms = totalRooms;
    }

    public int getAvailableRooms() {
        return availableRooms;
    }

    public void setAvailableRooms(int availableRooms) {
        this.availableRooms = availableRooms;
    }

    public boolean isHasParking() {
        return hasParking;
    }

    public void setHasParking(boolean hasParking) {
        this.hasParking = hasParking;
    }

    public boolean isHasWifi() {
        return hasWifi;
    }

    public void setHasWifi(boolean hasWifi) {
        this.hasWifi = hasWifi;
    }

    public boolean isHasSwimmingPool() {
        return hasSwimmingPool;
    }

    public void setHasSwimmingPool(boolean hasSwimmingPool) {
        this.hasSwimmingPool = hasSwimmingPool;
    }

    public boolean isHasGym() {
        return hasGym;
    }

    public void setHasGym(boolean hasGym) {
        this.hasGym = hasGym;
    }

    public boolean isHasSpa() {
        return hasSpa;
    }

    public void setHasSpa(boolean hasSpa) {
        this.hasSpa = hasSpa;
    }

    public boolean isHasRestaurant() {
        return hasRestaurant;
    }

    public void setHasRestaurant(boolean hasRestaurant) {
        this.hasRestaurant = hasRestaurant;
    }

    public boolean isHasConferenceRooms() {
        return hasConferenceRooms;
    }

    public void setHasConferenceRooms(boolean hasConferenceRooms) {
        this.hasConferenceRooms = hasConferenceRooms;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public List<String> getServices() {
        return services;
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    @Override
    public String toString() {
        return "Hotel{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", country='" + country + '\'' +
                ", starRating=" + starRating +
                ", reviewRating=" + reviewRating +
                ", availableRooms=" + availableRooms +
                ", baseRoomPrice=" + baseRoomPrice +
                '}';
    }
}

