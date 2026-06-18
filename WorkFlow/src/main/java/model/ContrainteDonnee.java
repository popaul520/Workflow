package model;

public class ContrainteDonnee {
    private Integer idDonnee; // Référence à template_donnee(id)
    private Integer idContrainte; // Référence à contrainte(id)
    private Boolean etapeContrainte;

    // Constructeurs
    public ContrainteDonnee() {}

    public ContrainteDonnee(Integer idDonnee, Integer idContrainte, Boolean etapeContrainte) {
        this.idDonnee = idDonnee;
        this.idContrainte = idContrainte;
        this.etapeContrainte = etapeContrainte;
    }
    // Getters et Setters
    public Integer getIdDonnee() { return idDonnee; }
    public void setIdDonnee(Integer idDonnee) { this.idDonnee = idDonnee; }

    public Integer getIdContrainte() { return idContrainte; }
    public void setIdContrainte(Integer idContrainte) { this.idContrainte = idContrainte; }

    public Boolean getEtapeContrainte() { return etapeContrainte; }
    public void setEtapeContrainte(Boolean etapeContrainte) { this.etapeContrainte = etapeContrainte; }
}
