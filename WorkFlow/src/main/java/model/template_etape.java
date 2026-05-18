package model;

import org.eclipse.tags.shaded.org.apache.xpath.operations.Bool;


public class template_etape {
    private int id;
    private int idTemplateWorkflow;
    private int roleAssocie;
    private int place;
    private int attentePlace;
    private String nomEtape;
    private boolean estFinale;

    // Getters et Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public int getIdTemplateWorkflow() { return idTemplateWorkflow; }
    public void setIdTemplateWorkflow(int idTemplateWorkflow) { this.idTemplateWorkflow = idTemplateWorkflow; }
    
    public int getRoleAssocie() { return roleAssocie; }
    public void setRoleAssocie(int roleAssocie) { this.roleAssocie = roleAssocie; }
    
    public int getPlace() { return place; }
    public void setPlace(int place) { this.place = place; }
    
    public int getAttentePlace() { return attentePlace; }
    public void setAttentePlace(int attentePlace) { this.attentePlace = attentePlace; }
    
    public String getNomEtape() { return nomEtape; }
    public void setNomEtape(String nomEtape) { this.nomEtape = nomEtape; }
    
    public boolean isEstFinale() { return estFinale; }
    public void setEstFinale(boolean estFinale) { this.estFinale = estFinale; }
}
