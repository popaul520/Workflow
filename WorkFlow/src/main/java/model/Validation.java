package model;

import java.sql.Date;


public class Validation {
    private Long id;
    private Date date;
    private String etape;

    private Utilisateur personne;
    private Utilisateur destinataire1;
    private Utilisateur destinataire2;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public String getEtape() { return etape; }
    public void setEtape(String etape) { this.etape = etape; }

    public Utilisateur getPersonne() { return personne; }
    public void setPersonne(Utilisateur personne) { this.personne = personne; }

    public Utilisateur getDestinataire1() { return destinataire1; }
    public void setDestinataire1(Utilisateur d1) { this.destinataire1 = d1; }

    public Utilisateur getDestinataire2() { return destinataire2; }
    public void setDestinataire2(Utilisateur d2) { this.destinataire2 = d2; }
}