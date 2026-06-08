package model;

public class WorkflowDisplay {
    private Workflow workflow;
    private String nomDemandeur;
    private int etapeActuelle;
    private String rawAvis;
    
    // Propriétés dynamiques calculées par le Service pour la JSP
    private String badgeBg;
    private String badgeText;
    private String libelleEtape;
    public WorkflowDisplay() {
    }

    public WorkflowDisplay(Workflow workflow, String nomDemandeur, int etapeActuelle, String rawAvis) {
        this.workflow = workflow;
        this.nomDemandeur = nomDemandeur;
        this.etapeActuelle = etapeActuelle;
        this.rawAvis = rawAvis;
    }
    // Getters et Setters
    public Workflow getWorkflow() { return workflow; }
    public void setWorkflow(Workflow workflow) { this.workflow = workflow; }
    public String getNomDemandeur() { return nomDemandeur; }
    public void setNomDemandeur(String nomDemandeur) { this.nomDemandeur = nomDemandeur; }
    public int getEtapeActuelle() { return etapeActuelle; }
    public void setEtapeActuelle(int etapeActuelle) { this.etapeActuelle = etapeActuelle; }
    public String getRawAvis() { return rawAvis; }
    public void setRawAvis(String rawAvis) { this.rawAvis = rawAvis; }
    public String getBadgeBg() { return badgeBg; }
    public void setBadgeBg(String badgeBg) { this.badgeBg = badgeBg; }
    public String getBadgeText() { return badgeText; }
    public void setBadgeText(String badgeText) { this.badgeText = badgeText; }
    public String getLibelleEtape() { return libelleEtape; }
    public void setLibelleEtape(String libelleEtape) { this.libelleEtape = libelleEtape; }
}