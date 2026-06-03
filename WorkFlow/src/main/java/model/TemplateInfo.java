package model;

public class TemplateInfo {
    private String nom;
    private String version;
    private String commentaire;

    public TemplateInfo(String nom, String version, String commentaire) {
        this.nom = nom;
        this.version = version;
        this.commentaire = commentaire;
    }
    
    public TemplateInfo() {
    }

    public String getNom() { return nom; }
    public String getVersion() { return version; }
    public String getCommentaire() { return commentaire; }
    public void setNom(String nom) { this.nom = nom; }
    public void setVersion(String version) { this.version = version; }
    public void setCommentaire(String commentaire) { this.nom = commentaire; }
}