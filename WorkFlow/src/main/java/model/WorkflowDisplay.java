package model;

public class WorkflowDisplay {
	private Workflow workflow;
	private String badgeBg;
	private String badgeText;
	private String libelleEtape;

	public WorkflowDisplay(Workflow workflow, String badgeBg, String badgeText, String libelleEtape) {
		this.workflow = workflow;
		this.badgeBg = badgeBg;
		this.badgeText = badgeText;
		this.libelleEtape = libelleEtape;
	}

	public Workflow getWorkflow() {
		return workflow;
	}

	public String getBadgeBg() {
		return badgeBg;
	}

	public String getBadgeText() {
		return badgeText;
	}

	public String getLibelleEtape() {
		return libelleEtape;
	}
}
