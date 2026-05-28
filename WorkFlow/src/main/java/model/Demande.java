package model;

public class Demande {
    int id ;
    String nom ;
    String description ;
    String status ;
    int role_cible;
    String email_envoye ;
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public int getRole_cible() { return role_cible; }
    public void setRole_cible(int role_cible) { this.role_cible = role_cible; }
    
    public String getEmail() { return email_envoye; }
    public void setEmail(String email_envoye) { this.email_envoye = email_envoye; }
}