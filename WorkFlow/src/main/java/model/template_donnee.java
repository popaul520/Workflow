package model;

import java.io.Serializable;

public class template_donnee implements Serializable {
    private int id;
    private int idTemplateEtape;
    private String nomChamp;
    private String typeComposant;
    private int ordreAffichage;
    private boolean aCommentaire;
    private boolean aDate;
    private boolean estObligatoire; 
    private String refContrainte;

    public template_donnee() {}

    public boolean isACommentaire() { return aCommentaire; }
    public boolean getACommentaire() { return aCommentaire; } // Crucial pour Tomcat 10
    public void setACommentaire(boolean aCommentaire) { this.aCommentaire = aCommentaire; }

    public boolean isADate() { return aDate; }
    public boolean getADate() { return aDate; } // Crucial pour Tomcat 10
    public void setADate(boolean aDate) { this.aDate = aDate; }

    public boolean isEstObligatoire() { return estObligatoire; }
    public boolean getEstObligatoire() { return estObligatoire; } // Crucial pour Tomcat 10
    public void setEstObligatoire(boolean estObligatoire) { this.estObligatoire = estObligatoire; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getIdTemplateEtape() { return idTemplateEtape; }
    public void setIdTemplateEtape(int idTemplateEtape) { this.idTemplateEtape = idTemplateEtape; }
    
    public String getNomChamp() { return nomChamp; }
    public void setNomChamp(String nomChamp) { this.nomChamp = nomChamp; }
    
    public String getTypeComposant() { return typeComposant; }
    public void setTypeComposant(String typeComposant) { this.typeComposant = typeComposant; }
    
    public int getOrdreAffichage() { return ordreAffichage; }
    public void setOrdreAffichage(int ordreAffichage) { this.ordreAffichage = ordreAffichage; }
    
    public String getRefContrainte() { return refContrainte; }
    public void setRefContrainte(String refContrainte) { this.refContrainte = refContrainte; }
}