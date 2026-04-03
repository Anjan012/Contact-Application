package com.example.smartcontacts.model;

/**
 * Represents a single contact stored in the database.
 */
public class Contact {

    private int id;
    private String firstName;
    private String lastName;
    private String company;
    private String phone;
    private String email;

    // Avatar color index (0-4), assigned based on contact ID so it's consistent
    private int colorIndex;

    // ── Constructors ──────────────────────────────────────────────────────────

    public Contact() {}

    /** Constructor for inserting a new contact (no ID yet). */
    public Contact(String firstName, String lastName, String company,
                   String phone, String email) {
        this.firstName = firstName;
        this.lastName  = lastName;
        this.company   = company;
        this.phone     = phone;
        this.email     = email;
    }

    /** Constructor for reading from the database (ID assigned by SQLite). */
    public Contact(int id, String firstName, String lastName, String company,
                   String phone, String email) {
        this.id        = id;
        this.firstName = firstName;
        this.lastName  = lastName;
        this.company   = company;
        this.phone     = phone;
        this.email     = email;
        this.colorIndex = id % 5; // deterministic color per contact
    }

    // ── Getters & Setters ─────────────────────────────────────────────────────

    public int    getId()             { return id; }
    public void   setId(int id)       { this.id = id; this.colorIndex = id % 5; }

    public String getFirstName()                  { return firstName; }
    public void   setFirstName(String firstName)  { this.firstName = firstName; }

    public String getLastName()                   { return lastName; }
    public void   setLastName(String lastName)    { this.lastName = lastName; }

    public String getCompany()                    { return company; }
    public void   setCompany(String company)      { this.company = company; }

    public String getPhone()                      { return phone; }
    public void   setPhone(String phone)          { this.phone = phone; }

    public String getEmail()                      { return email; }
    public void   setEmail(String email)          { this.email = email; }

    public int    getColorIndex()                 { return colorIndex; }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Returns "FirstName LastName" trimmed and concatenated. */
    public String getFullName() {
        String f = (firstName != null) ? firstName.trim() : "";
        String l = (lastName  != null) ? lastName.trim()  : "";
        if (f.isEmpty()) return l;
        if (l.isEmpty()) return f;
        return f + " " + l;
    }

    /**
     * Returns up to 2 initials for the avatar circle.
     * "John Doe" → "JD", "Alice" → "A"
     */
    public String getInitials() {
        StringBuilder sb = new StringBuilder();
        if (firstName != null && !firstName.trim().isEmpty())
            sb.append(Character.toUpperCase(firstName.trim().charAt(0)));
        if (lastName != null && !lastName.trim().isEmpty())
            sb.append(Character.toUpperCase(lastName.trim().charAt(0)));
        return sb.length() > 0 ? sb.toString() : "?";
    }

    /** Returns true if neither firstName nor phone are empty. */
    public boolean isValid() {
        return firstName != null && !firstName.trim().isEmpty()
                && phone != null && !phone.trim().isEmpty();
    }

    @Override
    public String toString() {
        return "Contact{id=" + id + ", name=" + getFullName() + ", phone=" + phone + "}";
    }
}
