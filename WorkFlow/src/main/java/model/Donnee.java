

package model;

import java.sql.Date;

public class Donnee {
    private Long idDonne;
    private String type;
    private String attribut;
    private String commentaire;
    private Date date;
    private String idTypeContraint;
    private Etape etape;
    
    
    public Donnee() {
    	
    }

    public Long getIdDonne() { return idDonne; }
    public void setIdDonne(Long idDonne) { this.idDonne = idDonne; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getAttribut() { return attribut; }
    public void setAttribut(String attribut) { this.attribut = attribut; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public Etape getEtape() { return etape; }
    public void setEtape(Etape etape) { this.etape = etape; }
    
    public String getRefTypeContraint() {return idTypeContraint;}
    public void setRefTypeContraint(String idTypeContraint) {
        this.idTypeContraint = idTypeContraint;
    }
}