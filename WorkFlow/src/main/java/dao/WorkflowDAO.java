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
    String sql = "INSERT INTO workflow (titre, date_creation, id_template_workflow, commentaire) VALUES (?, ?, ?, ?)";
    int generatedId = -1;

    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

    	ps.setString(1, wf.getTitre());
        ps.setTimestamp(2, new java.sql.Timestamp(wf.getDateCreation().getTime()));

        ps.setInt(3, wf.getIdTemplateWorkflow());
        ps.setString(4, wf.getCommentaire()); 

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
	    
	    wf.setId(rs.getInt("id"));
	    wf.setTitre(rs.getString("titre"));
	    
	    // Gestion des dates 
	    // java.sql.Timestamp
	    wf.setDateCreation(rs.getTimestamp("date_creation"));
	    wf.setDateFinalisation(rs.getTimestamp("date_finalisation"));
	    
	    // wf.setDescription(rs.getString("description"));
	    
	    return wf;
	}
	
	
	public List<Workflow> searchWorkflows(String query) {
	    List<Workflow> list = new ArrayList<>();
	    // On cherche si l'ID contient la chaîne ou si le titre contient la chaîne
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
	
	// 1. REQUÊTE : Trouver les dossiers qui ATTENDENT une étape précise (et qui n'ont pas encore été signés)
	public static List<Workflow> getWorkflowsEnAttentePourEtape(int etape) {
	    List<Workflow> list = new ArrayList<>();
	    
	    // Règle SQL de base pour filtrer l'étape en cours
	    String conditionEtape = "";
	    if (etape == 1) {
	        conditionEtape = "NOT EXISTS (SELECT 1 FROM validation WHERE id_workflow = w.id AND TRIM(etape) = '1')";
	    } else if (etape >= 2 && etape <= 6) {
	        conditionEtape = "EXISTS (SELECT 1 FROM validation WHERE id_workflow = w.id AND TRIM(etape) = '1') " +
	                         "AND NOT EXISTS (SELECT 1 FROM validation WHERE id_workflow = w.id AND TRIM(etape) = '" + etape + "')";
	    } else if (etape == 7) {
	        conditionEtape = "(SELECT COUNT(DISTINCT TRIM(v.etape)) FROM validation v WHERE v.id_workflow = w.id AND TRIM(v.etape) IN ('1','2','3','4','5','6')) = 6 " +
	                         "AND NOT EXISTS (SELECT 1 FROM validation WHERE id_workflow = w.id AND TRIM(etape) = '7')";
	    } else if (etape >= 8 && etape <= 11) {
	        int etapePrecedente = etape - 1;
	        conditionEtape = "EXISTS (SELECT 1 FROM validation WHERE id_workflow = w.id AND TRIM(etape) = '" + etapePrecedente + "') " +
	                         "AND NOT EXISTS (SELECT 1 FROM validation WHERE id_workflow = w.id AND TRIM(etape) = '" + etape + "')";
	    }

	    String sql = "SELECT id, titre FROM workflow w " +
	                 "WHERE (statut IS NULL OR statut != 'Terminé') AND date_finalisation IS NULL AND (" + conditionEtape + ")";

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        while (rs.next()) {
	            Workflow w = new Workflow();
	            w.setId(rs.getInt("id"));
	            w.setTitre(rs.getString("titre"));
	            list.add(w);
	        }
	    } catch (Exception e) { e.printStackTrace(); }
	    return list;
	}

	// 1. REQUÊTE : Trouver les dossiers dont l'étape vient de se TERMINER et qui n'ont PAS ENCORE été annoncés
	public static List<Workflow> getWorkflowsTerminesPourRole(int roleId) {
	    List<Workflow> list = new ArrayList<>();
	    
	    // On joint la table droit pour s'assurer que ce rôle possède l'étape liée au workflow finalisé
	    // ET on vérifie dans une table d'historique (ou via une logique de droits) que ce rôle précis n'a pas encore reçu l'alerte.
	    
	    String sql = "SELECT DISTINCT w.id, w.titre FROM workflow w " +
	                 "JOIN droit d ON d.etape = 10 " + 
	                 "WHERE w.statut = 'TERMINER' " +
	                 "  AND w.annonce_termine = false " + 
	                 "  AND d.role = ?"; // On s'assure que le rôle a le droit sur cette fin de processus

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        ps.setInt(1, roleId);
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                Workflow w = new Workflow();
	                w.setId(rs.getInt("id"));
	                w.setTitre(rs.getString("titre"));
	                list.add(w);
	            }
	        }
	    } catch (Exception e) { 
	        e.printStackTrace(); 
	    }
	    return list;
	}

	// 2. ACTION : Enregistrer en BDD que l'étape de ce workflow
	public static void marquerAnnonceTerminee(int idWf) {
	    String sql = "UPDATE workflow SET annonce_termine = 't' WHERE id = ?";
	    
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        ps.setInt(1, idWf);
	        ps.executeUpdate();
	        System.out.println("Workflow #" + idWf + " marqué comme annoncé (plus de futurs envois).");
	        
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}
	
	// PREMIÈRE FONCTION : Récupère les workflows en attente du rôle spécifié dynamiquement :)
	public static List<Workflow> getWorkflowsEnAttenteParRole(int roleId) {
	    List<Workflow> liste = new ArrayList<>();
	    
	    String sql = 
	        "SELECT DISTINCT w.id, w.titre, w.commentaire "
	      + "FROM workflow w "
	      + "JOIN template_etape te ON te.id_template_workflow = w.id_template_workflow "
	      + "WHERE te.role_associe = ? "
	      + "  AND w.date_finalisation IS NULL "
	      + "  AND (w.statut IS NULL OR UPPER(w.statut) != 'TERMINER') "
	      + "  "
	      + "  -- 1. L'étape courante n'a pas encore été validée pour ce workflow -- "
	      + "  AND NOT EXISTS ( "
	      + "      SELECT 1 FROM validation v "
	      + "      WHERE v.id_workflow = w.id "
	      + "        AND CAST(v.etape AS INT) = te.place "
	      + "  ) "
	      + "  "
	      + "  -- 2. Règles dynamiques de franchissement des étapes -- "
	      + "  AND ( "
	      + "      -- Cas A : L'étape a une contrainte spécifique (attente_place renseignée et > 0) -- "
	      + "      (te.attente_place IS NOT NULL AND te.attente_place > 0 AND EXISTS ( "
	      + "          SELECT 1 FROM validation v_spec "
	      + "          WHERE v_spec.id_workflow = w.id "
	      + "            AND CAST(v_spec.etape AS INT) = te.attente_place "
	      + "      )) "
	      + "      OR "
	      + "      -- Cas B : C'est la toute première étape du workflow (place = 1 ou position de départ) -- "
	      + "      ((te.attente_place IS NULL OR te.attente_place = 0) AND te.place = 1) "
	      + "      OR "
	      + "      -- Cas C : Séquence standard (attente_place vide/0, s'active si l'étape précédente (place - 1) est validée) -- "
	      + "      ((te.attente_place IS NULL OR te.attente_place = 0) AND te.place > 1 AND EXISTS ( "
	      + "          SELECT 1 FROM validation v_seq "
	      + "          WHERE v_seq.id_workflow = w.id "
	      + "            AND CAST(v_seq.etape AS INT) = te.place - 1 "
	      + "      )) "
	      + "  );";

	    try (Connection conn = DBConnection.getConnection(); 
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        ps.setInt(1, roleId);
	        
	        try (ResultSet rs = ps.executeQuery()) {
	            while (rs.next()) {
	                Workflow w = new Workflow();
	                w.setId(rs.getInt("id"));
	                w.setTitre(rs.getString("titre")); 
	                w.setCommentaire(rs.getString("commentaire"));
	                liste.add(w);
	            }
	        }
	    } catch (Exception e) {
	        System.err.println(" Erreur SQL dynamique V2 pour le rôle : " + roleId);
	        e.printStackTrace();
	    }
	    return liste;
	}
	

}

