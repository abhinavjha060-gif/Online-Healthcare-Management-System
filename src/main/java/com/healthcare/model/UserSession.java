package com.healthcare.model;

/**
 * Remembers WHO is logged in, so every screen can show the right name.
 * Set once at login, read by the dashboards.
 */
public class UserSession {

    private static User currentUser;

    private UserSession() { }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }

    public static int getUserId() {
        return currentUser == null ? -1 : currentUser.getUserId();
    }

    public static String getFullName() {
        return currentUser == null ? "Guest" : currentUser.getFullName();
    }

    /** First word of the name, e.g. "Rahul" from "Rahul Sharma". */
    public static String getFirstName() {
        String[] parts = getFullName().trim().split("\\s+");
        return parts[0];
    }

    /** Initials for the avatar circle, e.g. "RS". Skips titles like "Dr.". */
    public static String getInitials() {
        StringBuilder sb = new StringBuilder();
        for (String part : getFullName().trim().split("\\s+")) {
            if (part.equalsIgnoreCase("Dr.") || part.equalsIgnoreCase("Dr")) {
                continue;
            }
            if (!part.isEmpty() && sb.length() < 2) {
                sb.append(Character.toUpperCase(part.charAt(0)));
            }
        }
        return sb.length() == 0 ? "?" : sb.toString();
    }

    public static void clear() {
        currentUser = null;
    }
}
