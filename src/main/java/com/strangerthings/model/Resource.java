package com.strangerthings.model;

public class Resource {

    private int id;
    private String name;
    private String category;
    private String status;
    private String ownerUsername;
    private String description;
    private String collectionMethod;
    private double estimatedSavings;
    private double co2AvoidedKg;

    public Resource(
            int id,
            String name,
            String category,
            String status,
            String ownerUsername,
            String description,
            String collectionMethod,
            double estimatedSavings,
            double co2AvoidedKg) {

        this.id = id;
        this.name = name;
        this.category = category;
        this.status = status;
        this.ownerUsername = ownerUsername;
        this.description = description;
        this.collectionMethod = collectionMethod;
        this.estimatedSavings = estimatedSavings;
        this.co2AvoidedKg = co2AvoidedKg;
    }

    public Resource(
            String name,
            String category,
            String status,
            String ownerUsername,
            String description,
            String collectionMethod,
            double estimatedSavings,
            double co2AvoidedKg) {

        this(0, name, category, status, ownerUsername,
                description, collectionMethod,
                estimatedSavings, co2AvoidedKg);
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

    public String getCategory() {
        return category;
    }

    public String getStatus() {
        return status;
    }

    public String getOwnerUsername() {
        return ownerUsername;
    }

    public String getDescription() {
        return description;
    }

    public String getCollectionMethod() {
        return collectionMethod;
    }

    public double getEstimatedSavings() {
        return estimatedSavings;
    }

    public double getCo2AvoidedKg() {
        return co2AvoidedKg;
    }
}