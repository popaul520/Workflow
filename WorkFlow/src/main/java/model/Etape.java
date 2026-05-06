package model;
public class Etape {
    private Long id;
    private Integer nbEtape;
    private String role;

    //@ManyToOne
    private Workflow workflow;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Integer getNbEtape() { return nbEtape; }
    public void setNbEtape(Integer nbEtape) { this.nbEtape = nbEtape; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Workflow getWorkflow() { return workflow; }
    public void setWorkflow(Workflow workflow) { this.workflow = workflow; }
}