package model;

public class Droit {
	int id;
	int role;
	int etape;
	
	public Droit() {
		
	}
	
	public Droit(int role, int etape) {
		this.setRole(role);
		this.setEtape(etape);
	}

	public int getId() { return id; }
	public void setId(int id) {this.id = id; }

	
	public int getRole() { return role; }
	public void setRole(int role) {this.role = role; }
	
	public int getEtape() { return etape; }
	public void setEtape(int etape) {this.etape = etape; }
}
