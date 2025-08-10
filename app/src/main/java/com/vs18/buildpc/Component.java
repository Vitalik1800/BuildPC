package com.vs18.buildpc;

import java.io.Serializable;

public class Component implements Serializable {

    int id;
    String name;
    String type;
    String socket;
    String brand;
    double price;
    String specs;

    public Component(int id, String name, String type, String socket, String brand, double price, String specs) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.socket = socket;
        this.brand = brand;
        this.price = price;
        this.specs = specs;
    }

    public Component(int id, String name, String type, String brand, double price, String specs){
        this.id = id;
        this.name = name;
        this.type = type;
        this.brand = brand;
        this.price = price;
        this.specs = specs;
    }

    public Component(int dishId, String dishName, int quantity, double price) {
    }

    public Component(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSocket() {
        return socket;
    }

    public void setSocket(String socket) {
        this.socket = socket;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSpecs() {
        return specs;
    }

    public void setSpecs(String specs) {
        this.specs = specs;
    }
}
