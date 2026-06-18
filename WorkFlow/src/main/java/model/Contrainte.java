package model;

public class Contrainte {
    private Integer id;
    private String contrainte;
    private Boolean texte;
    private Condition condition; // Correspond à id_condition (Jointure)
    private Integer id_donnee;
    // Constructeurs
    public Contrainte() {}

    public Contrainte(Integer id, String contrainte, Boolean texte, Condition condition) {
        this.id = id;
        this.contrainte = contrainte;
        this.texte = texte;
        this.condition = condition;
    }

    // Getters et Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getContrainte() { return contrainte; }
    public void setContrainte(String contrainte) { this.contrainte = contrainte; }

    public Boolean getTexte() { return texte; }
    public void setTexte(Boolean texte) { this.texte = texte; }

    public Condition getCondition() { return condition; }
    public void setCondition(Condition condition) { this.condition = condition; }
    
    public Integer getDonnee() { return id_donnee; }
    public void setIdDonnee(Integer id) { this.id_donnee = id_donnee; }

}