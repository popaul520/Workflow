package model;

public class Utilisateur {
    private int id;
    private String login;
    private String mdp;
    private String nom;
    private String prenom;
    private int role ;
    private String mail;
    
    public Utilisateur() {
    	
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getMdp() { return mdp; }
    public void setMdp(String mdp) { this.mdp = mdp; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public int getRole() { return role; }
    public void setRole(int role) { this.role = role; }

    public String getMail() { return mail; }
    public void setMail(String mail) { this.mail = mail; }
    
    /*
    public boolean canAccessStep(int nbrole) {
        if (role == null) return false;
        if (this.role == (11)) return true;

        switch (nbrole) {
            case 1: return this.role == 1;
            case 2: return this.role == 2;
            case 3: return this.role == 3;
            case 4: return this.role == 4;
            case 5: return this.role == 5;
            case 6: return this.role == 6;
            case 7: return this.role == 7;
            case 8: return this.role == 8;
            case 9: return this.role == 9;
            case 10: return this.role ==10;
            default: return false;
        }
    }*/
    
    public static String getRole(int nbrole) {
        switch (nbrole) {
        case 1: return ("COMMERCE");
        case 2: return ("CONDITIONNEMENT");
        case 3: return ("APPROVISIONNEMENT");
        case 4: return ("SCM");
        case 5: return ("LOGISTIQUE");
        case 6: return ("QHE");
        case 7: return ("DOP");
        case 8: return ("METHODES");
        case 9: return ("CDG");
        case 10: return ("DCD");
      //  default: return false;
    }
        return "error";

    }
 
}