package model;

import java.util.Date;

import org.eclipse.tags.shaded.org.apache.xpath.operations.Bool;

public class templateWorkflow {
    private int id;
    private String titre;
    private int version;
    private String commentaire;
    private Date dateCreation;
    private String idUtilisateur;
    private boolean est_actif;
    private int createur;

    public templateWorkflow() {
    }
    
    /*
    public Workflow(int id, String titre) {
        this.id = id;
        this.titre = titre;
    }*/
    
    //@ManyToOne
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    
    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public int getCreateur() { return createur; }
    public void setCreateur(int createur) { this.createur = createur; }
    
    public String getIdUtilisateur() { 
        return idUtilisateur; 
    }
    public void setIdUtilisateur(String idUtilisateur) { 
        this.idUtilisateur = idUtilisateur; 
    }
    
    public boolean isEstActif() { return est_actif; }
    public void setEstActif(boolean b) { this.est_actif = b; }
    
}
