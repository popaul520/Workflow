package model;

public class TypeContraint {

    private Long id;

    private String type;
    private String valeur;

    public TypeContraint() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getValeur() { return valeur; }
    public void setValeur(String valeur) { this.valeur = valeur; }
}
