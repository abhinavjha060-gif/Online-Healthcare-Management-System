package com.healthcare.model;

/** Represents one row of the `users` table (the person who logged in). */
public class User {

    private final int userId;
    private final String fullName;
    private final String email;
    private final String role;

    public User(int userId, String fullName, String email, String role) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.role = role;
    }

    public int getUserId()      { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail()    { return email; }
    public String getRole()     { return role; }
}
