package model;

public class WorkflowDisplay {
    private final Workflow workflow;
    private final String nomDemandeur;
    private final int etapeActuelle;
    private final String rawAvis;
    
    // Champs calculés pour la vue JSP
    private String badgeBg;
    private String badgeText;
    private String libelleEtape;

    public WorkflowDisplay(Workflow workflow, String nomDemandeur, int etapeActuelle, String rawAvis) {
        this.workflow = workflow;
        this.nomDemandeur = nomDemandeur;
        this.etapeActuelle = etapeActuelle;
        this.rawAvis = rawAvis;
    }

    // Getters et Setters
    public Workflow getWorkflow() { return workflow; }
    public String getNomDemandeur() { return nomDemandeur; }
    public int getEtapeActuelle() { return etapeActuelle; }
    public String getRawAvis() { return rawAvis; }
    
    public String getBadgeBg() { return badgeBg; }
    public void setBadgeBg(String badgeBg) { this.badgeBg = badgeBg; }
    
    public String getBadgeText() { return badgeText; }
    public void setBadgeText(String badgeText) { this.badgeText = badgeText; }
    
    public String getLibelleEtape() { return libelleEtape; }
    public void setLibelleEtape(String libelleEtape) { this.libelleEtape = libelleEtape; }
}