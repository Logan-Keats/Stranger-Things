package com.strangerthings.model;
import java.time.LocalDate;

public class Booking
{
    /*
    Handles Single Instance of a Request
    Example: A Screwdriver request from 11th August to 4th September
    */

    private int id;
    private int resourceId;
    private String resourceName;
    // MIGHT CHANGE TO borrowerId LATER
    private String borrowerUsername;
    private LocalDate startDate;
    private LocalDate endDate;
    private BookingStatus status;

    // Booking Constructor
    public Booking(int id, int resourceId, String resourceName, String borrowerUsername, LocalDate startDate, LocalDate endDate, BookingStatus status)
    {
        // Error Catching
        if (startDate == null || endDate == null)
        {
            throw new IllegalArgumentException("Neither Date should be null!");
        }
        if (startDate != null && endDate != null && endDate.isBefore(startDate))
        {
           throw new IllegalArgumentException("End date cannot be before the start date!");
        }
        if (resourceId <0 && id !=-1) { throw new IllegalArgumentException("Resource name cannot be null or blank");}
        if (borrowerUsername == null || borrowerUsername.isBlank()) {
            throw new IllegalArgumentException("Borrower username cannot be null of blank.");}

        this.id = id;
        this.resourceId = resourceId;
        this.resourceName = resourceName;
        this.borrowerUsername = borrowerUsername;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }
    // Constructor for new user booking requests made before id can be generated
    public Booking(int resourceId, String resourceName, String borrowerUsername, LocalDate startDate, LocalDate endDate)
    {
        this(-1, resourceId, resourceName, borrowerUsername, startDate, endDate, BookingStatus.REQUESTED);
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getResourceId() { return resourceId; }
    public void setResourceId(int resourceId) { this.resourceId = resourceId; }

    public String getResourceName() { return resourceName; }
    public void setResourceName(String resourceName) { this.resourceName = resourceName; }

    public String getBorrowerUsername() { return borrowerUsername; }
    public void setBorrowerUsername(String borrowerUsername) { this.borrowerUsername = borrowerUsername; }

    // Compatibility patch
    public String getBorrowerName() { return borrowerUsername; }

    public LocalDate getStartDate() {return  startDate;}
    public void setStartDate(LocalDate startDate) {this.startDate = startDate; }

    public LocalDate getEndDate() {return  endDate;}
    public void setEndDate(LocalDate endDate) {this.endDate = endDate; }

    public BookingStatus getStatus() {return status;}
    public void setStatus(BookingStatus status) { this.status = status; }
}