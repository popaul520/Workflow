package model;

import java.sql.Date;

import org.eclipse.tags.shaded.org.apache.xpath.operations.Bool;

public class template_donnee {
    private Long idDonne;
    private Etape etape;
    private String type;
    private Bool attribut;
    private Bool commentaire;
    private Date date;
    private String idTypeContraint;
    private Bool est_obligatoire;
    private int type_contraint; 
    
    
    public template_donnee() {
    }

    public Long getIdDonne() { return idDonne; }
    public void setIdDonne(Long idDonne) { this.idDonne = idDonne; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Bool getAttribut() { return attribut; }
    public void setAttribut(Bool attribut) { this.attribut = attribut; }

    public Bool getCommentaire() { return commentaire; }
    public void setCommentaire(Bool commentaire) { this.commentaire = commentaire; }

    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }

    public Etape getEtape() { return etape; }
    public void setEtape(Etape etape) { this.etape = etape; }
    
    public String getRefTypeContraint() {return idTypeContraint;}
    public void setRefTypeContraint(String idTypeContraint) {
        this.idTypeContraint = idTypeContraint;
    }
}
