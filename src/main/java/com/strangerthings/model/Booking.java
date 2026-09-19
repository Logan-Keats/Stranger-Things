package com.strangerthings.model;

public class Booking {

    private int id;
    private String resourceName;
    private String borrowerName;
    private BookingStatus status;

    public Booking(int id, String resourceName, String borrowerName,
                   BookingStatus status) {
        this.id = id;
        this.resourceName = resourceName;
        this.borrowerName = borrowerName;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getResourceName() {
        return resourceName;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}