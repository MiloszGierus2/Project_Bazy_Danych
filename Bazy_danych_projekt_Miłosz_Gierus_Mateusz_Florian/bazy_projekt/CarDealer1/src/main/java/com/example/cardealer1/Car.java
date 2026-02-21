package com.example.cardealer1;

import java.time.LocalDate;

public class Car {
    private String make;
    private String model;
    private int year;
    private double price;
    private String buyerName;
    private String buyerEmail;
    private String buyerPhone;
    private LocalDate saleDate;

    // Konstruktor dla nowego samochodu bez informacji o kupującym i dacie sprzedaży
    public Car(String make, String model, int year, double price) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.price = price;
    }

    // Konstruktor z pełnymi informacjami, używany np. przy odświeżaniu widoku z bazy danych
    public Car(String make, String model, int year, double price, String buyerName, String buyerEmail, String buyerPhone, LocalDate saleDate) {
        this.make = make;
        this.model = model;
        this.year = year;
        this.price = price;
        this.buyerName = buyerName;
        this.buyerEmail = buyerEmail;
        this.buyerPhone = buyerPhone;
        this.saleDate = saleDate;
    }

    // Gettery i settery dla pól
    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getBuyerName() {
        return buyerName;
    }

    public void setBuyerName(String buyerName) {
        this.buyerName = buyerName;
    }

    public String getBuyerEmail() {
        return buyerEmail;
    }

    public void setBuyerEmail(String buyerEmail) {
        this.buyerEmail = buyerEmail;
    }

    public String getBuyerPhone() {
        return buyerPhone;
    }

    public void setBuyerPhone(String buyerPhone) {
        this.buyerPhone = buyerPhone;
    }

    public LocalDate getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDate saleDate) {
        this.saleDate = saleDate;
    }
}