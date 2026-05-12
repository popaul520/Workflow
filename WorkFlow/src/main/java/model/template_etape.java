package model;

import org.eclipse.tags.shaded.org.apache.xpath.operations.Bool;

public class template_etape {


	private Long id;
	private Integer nbEtape;
	private String role;
	private int place;
	private int attente_place;
	private String nom_etape;
	private Bool est_finale;

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
