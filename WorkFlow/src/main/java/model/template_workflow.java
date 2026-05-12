package model;

import java.util.Date;

import org.eclipse.tags.shaded.org.apache.xpath.operations.Bool;

public class template_workflow {
    private int id;
    private String titre;
    private int version;
    private String commentaire;
    private Date dateCreation;
    private String idUtilisateur;
    private Bool est_actif;

    public template_workflow() {
    }
    
    /*
    public Workflow(int id, String titre) {
        this.id = id;
        this.titre = titre;
    }*/
    
    //@ManyToOne
    private Utilisateur createur;
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }

    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }

    public Utilisateur getCreateur() { return createur; }
    public void setCreateur(Utilisateur createur) { this.createur = createur; }
    
    public String getIdUtilisateur() { 
        return idUtilisateur; 
    }
    public void setIdUtilisateur(String idUtilisateur) { 
        this.idUtilisateur = idUtilisateur; 
    }
}
