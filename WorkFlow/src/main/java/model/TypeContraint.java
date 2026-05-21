package model;

public class TypeContraint {
    private int id;
    private String type;
    private String valeur;

    // Constructeurs
    public TypeContraint() {}

    public TypeContraint(int id, String type, String valeur) {
        this.id = id;
        this.type = type;
        this.valeur = valeur;
    }

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getValeur() { return valeur; }
    public void setValeur(String valeur) { this.valeur = valeur; }
}