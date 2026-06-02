package model;
import java.util.Date;
//@Entity
public class Workflow {
   // @Id
   // @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String titre;
    private Date dateCreation;
    private Date dateFinalisation;
    private String commentaire;
    private String idUtilisateur;
    private String statut;

    public Workflow() {
    }
    
    public Workflow(int id, String titre) {
        this.id = id;
        this.titre = titre;
    }
    
    //@ManyToOne
    private Utilisateur createur;
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }

    public String getStatut() {return statut;}
    public void setStatut(String statut) {this.statut = statut;}

    public Date getDateCreation() { return dateCreation; }
    public void setDateCreation(Date dateCreation) { this.dateCreation = dateCreation; }

    public Date getDateFinalisation() { return dateFinalisation; }
    public void setDateFinalisation(Date dateFinalisation) { this.dateFinalisation = dateFinalisation; }

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
