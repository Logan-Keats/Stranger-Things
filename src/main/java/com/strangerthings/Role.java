package com.strangerthings;

/**
 * Application roles used after login (US-1.4 later; set on login now).
 */
public enum Role {
    /** Neighbourhood member — borrow / list resources. */
    MEMBER,
    /** Staff admin — approve bookings and admin screens. */
    ADMIN
}
