package model;

public class Role {
    private int id;
    private String role;

    // Constructeur vide
    public Role() {}

    // Constructeur complet
    public Role(int id, String role) {
        this.id = id;
        this.role = role;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}