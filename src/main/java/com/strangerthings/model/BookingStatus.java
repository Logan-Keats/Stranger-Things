package com.strangerthings.model;

// Handles all valid stages in enum ensuring safety
public enum BookingStatus
{
    REQUESTED,
    APPROVED,
    REJECTED,
    ON_LOAN,
    RETURNED,
    CANCELLED
}
