package model;


public class Affectation {
    private Long id;

    private Utilisateur personne;

    private Utilisateur destinataire;

    public Affectation() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Utilisateur getPersonne() { return personne; }
    public void setPersonne(Utilisateur personne) { this.personne = personne; }

    public Utilisateur getDestinataire() { return destinataire; }
    public void setDestinataire(Utilisateur destinataire) { this.destinataire = destinataire; }
}