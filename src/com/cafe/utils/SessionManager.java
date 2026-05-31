package com.cafe.utils;

/**
 * SessionManager.java
 * Module 1: Authentication & Dashboard
 * @author Imman Fatima
 *
 * Keeps track of who is currently logged in.
 * Any module can call SessionManager.getCurrentUser() to find out.
 *
 * This is a simple static class — no need to create an object.
 * Think of it as a global sticky note that says "Ahmad is logged in."
 */
public final class SessionManager {

    // The username of whoever logged in
    private static String currentUser = null;

    // Private constructor — nobody should create a SessionManager object
    private SessionManager() {}

    /** Called by LoginPanel after successful login */
    public static void setCurrentUser(String username) {
        currentUser = username;
    }

    /** Called by any module that wants to know who is logged in */
    public static String getCurrentUser() {
        return currentUser;
    }

    /** Called when the user logs out */
    public static void clearSession() {
        currentUser = null;
    }

    /** Check if anyone is logged in */
    public static boolean isLoggedIn() {
        return currentUser != null;
    }
}
