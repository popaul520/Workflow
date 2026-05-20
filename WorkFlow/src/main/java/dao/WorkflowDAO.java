package dao;

import java.sql.*;
import java.util.*;
import model.Workflow;

public class WorkflowDAO {

	public List<Workflow> getAll() {
	    List<Workflow> list = new ArrayList<>();
	    String sql = "SELECT id, titre FROM workflow"; 

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            list.add(new Workflow(rs.getInt("id"), rs.getString("titre")));
	        }
	        System.out.println("DAO DEBUG: J'ai trouvé " + list.size() + " lignes");

	    } catch (Exception e) {
	        System.err.println("ERREUR DAO: " + e.getMessage());
	        e.printStackTrace();
	    }
	    return list;
	}
	
	public boolean save(String titre) {
	    String sql = "INSERT INTO workflow (titre) VALUES (?)";
	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	        
	        ps.setString(1, titre);
	        int rowsAffected = ps.executeUpdate();
	        return rowsAffected > 0;
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	        return false;
	    }
	}
	
	public boolean saveFull(String titre, String demandeur, String client, String marque) {
	    String sqlWorkflow = "INSERT INTO workflow (titre) VALUES (?)";
	    String sqlDemande = "INSERT INTO demande (workflow_id, demandeur, client, marque, date_demande) VALUES (?, ?, ?, ?, CURRENT_DATE)";
	    try (Connection con = DBConnection.getConnection()) {
	        con.setAutoCommit(false); // On démarre une transaction
	        try (PreparedStatement psW = con.prepareStatement(sqlWorkflow, Statement.RETURN_GENERATED_KEYS)) {
	            psW.setString(1, titre);
	            psW.executeUpdate();
	            // On récupère l'ID du workflow qui vient d'être créé
	            ResultSet rs = psW.getGeneratedKeys();
	            if (rs.next()) {
	                int newId = rs.getInt(1);
	                try (PreparedStatement psD = con.prepareStatement(sqlDemande)) {
	                    psD.setInt(1, newId);
	                    psD.setString(2, demandeur);
	                    psD.setString(3, client);
	                    psD.setString(4, marque);
	                    psD.executeUpdate();
	                }
	            }
	            con.commit();
	            return true;
	        } catch (Exception e) {
	            con.rollback();
	            e.printStackTrace();
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return false;
	}

	
	public static Workflow getById(int id) {
	    Workflow wf = null;
	    // Ajout de date_finalisation dans la requête SQL
	    String sql = "SELECT id, titre, date_creation, date_finalisation, commentaire, id_utilisateur, id_template_workflow FROM workflow WHERE id = ?"; 
	    
	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	        
	        ps.setInt(1, id);
	        try (ResultSet rs = ps.executeQuery()) {
	            if (rs.next()) {
	                wf = new Workflow();
	                wf.setId(rs.getInt("id"));
	                wf.setTitre(rs.getString("titre"));
	                wf.setDateCreation(rs.getDate("date_creation"));
	                wf.setDateFinalisation(rs.getDate("date_finalisation")); 
	                wf.setCommentaire(rs.getString("commentaire"));
	                wf.setIdUtilisateur(rs.getString("id_utilisateur")); 
	             // DANS WorkflowDAO.java (méthode de mapping ou getById)
	               wf.setIdTemplateWorkflow((rs.getInt("id_template_workflow")));; // Vérifie le nom exact de ta colonne SQL
	            }
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return wf;
	}
	
public int create(model.Workflow wf) {
    // On n'utilise que les colonnes qui existent vraiment dans ta table : titre et date_creation
    String sql = "INSERT INTO workflow (titre, date_creation, id_template_workflow) VALUES (?, ?, ?)";
    int generatedId = -1;

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        
        // Paramètre 1 : titre
        ps.setString(1, wf.getTitre());
        
        // Paramètre 2 : date_creation
        ps.setTimestamp(2, new java.sql.Timestamp(wf.getDateCreation().getTime()));

        ps.setInt(3, wf.getIdTemplateWorkflow());
        ps.executeUpdate();

        try (ResultSet rs = ps.getGeneratedKeys()) {
            if (rs.next()) {
                generatedId = rs.getInt(1);
            }
        }
    } catch (SQLException e) {
        System.err.println("Erreur lors de la création du workflow : " + e.getMessage());
        e.printStackTrace();
    }
    return generatedId;
}
	/**
	 * Marque le workflow comme terminé en enregistrant la date de finalisation.
	 * @param idWf L'identifiant du workflow à clôturer
	 */
	public void finaliserWorkflow(int idWf) {
	    String sql = "UPDATE workflow SET date_finalisation = CURRENT_DATE WHERE id = ?";
	    
	    // Utilisation du try-with-resources pour fermer automatiquement la connexion
	    try (java.sql.Connection conn = dao.DBConnection.getConnection(); 
	         java.sql.PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        ps.setInt(1, idWf);
	        int rowsUpdated = ps.executeUpdate();
	        
	        if (rowsUpdated > 0) {
	            System.out.println("Workflow #" + idWf + " finalisé avec succès.");
	        }
	    } catch (java.sql.SQLException e) {
	        System.err.println("Erreur lors de la finalisation du workflow #" + idWf);
	        e.printStackTrace();
	    }
	}
	
	// Récupère les dossiers finis (Clôturés)
	public List<Workflow> getWorkflowsTermines() {
	    List<Workflow> list = new ArrayList<>();
	    String sql = "SELECT * FROM workflow WHERE date_finalisation IS NOT NULL ORDER BY date_finalisation DESC";
	    
	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        
	        while (rs.next()) {
	           list.add(mapResultSetToWorkflow(rs)); // Ta méthode habituelle pour créer l'objet
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return list;
	}

	
	// Récupère uniquement les dossiers non finalisés
	public List<Workflow> getWorkflowsEnCours() {
	    List<Workflow> list = new ArrayList<>();
	    String sql = "SELECT * FROM workflow WHERE date_finalisation IS NULL ORDER BY date_creation DESC";
	    
	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        while (rs.next()) {
	            list.add(mapResultSetToWorkflow(rs));
	        }
	    } catch (SQLException e) { e.printStackTrace(); }
	    return list;
	}

	// Récupère les dossiers finalisés (Terminés ET Annulés)
	public List<Workflow> getWorkflowsFinalises() {
	    List<Workflow> list = new ArrayList<>();
	    String sql = "SELECT * FROM workflow WHERE date_finalisation IS NOT NULL ORDER BY date_finalisation DESC";
	    
	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        while (rs.next()) {
	            list.add(mapResultSetToWorkflow(rs));
	        }
	    } catch (SQLException e) { e.printStackTrace(); }
	    return list;
	}
	
	private Workflow mapResultSetToWorkflow(ResultSet rs) throws SQLException {
	    Workflow wf = new Workflow();
	    
	    // Mapping des colonnes de la table 'workflow' vers les attributs de l'objet
	    wf.setId(rs.getInt("id"));
	    wf.setTitre(rs.getString("titre"));
	    
	    // Gestion des dates (on utilise getTimestamp pour garder heure/minute si besoin)
	    // java.sql.Timestamp est compatible avec java.util.Date
	    wf.setDateCreation(rs.getTimestamp("date_creation"));
	    wf.setDateFinalisation(rs.getTimestamp("date_finalisation"));
	    
	    // Si tu as d'autres colonnes comme 'description' ou 'id_createur' :
	    // wf.setDescription(rs.getString("description"));
	    
	    return wf;
	}
	
	
	public List<Workflow> searchWorkflows(String query) {
	    List<Workflow> list = new ArrayList<>();
	    // On cherche si l'ID contient la chaîne OU si le titre contient la chaîne
	    String sql = "SELECT * FROM workflow WHERE CAST(id AS CHAR) LIKE ? OR titre LIKE ? ORDER BY date_creation DESC";
	    
	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql)) {
	        
	        String searchTerm = "%" + query + "%";
	        ps.setString(1, searchTerm);
	        ps.setString(2, searchTerm);
	        
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                list.add(mapResultSetToWorkflow(rs));
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return list;
	}
	
	

}

