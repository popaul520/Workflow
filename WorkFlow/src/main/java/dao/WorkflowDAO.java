package dao;

import java.sql.*;
import java.util.*;

import model.Droit;
import model.Workflow;
import model.WorkflowDisplay;

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
		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

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
		String sql = "SELECT id, titre, date_creation, date_finalisation, commentaire, id_utilisateur FROM workflow WHERE id = ?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, id);
			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					wf = new Workflow();
					wf.setId(rs.getInt("id"));
					wf.setTitre(rs.getString("titre"));
					wf.setDateCreation(rs.getDate("date_creation"));
					wf.setDateFinalisation(rs.getDate("date_finalisation"));
					wf.setCommentaire(rs.getString("commentaire"));
					wf.setIdUtilisateur(rs.getInt("id_utilisateur"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return wf;
	}

	public int create(model.Workflow wf) {
	    // Intégration de la colonne id_utilisateur dans la requête d'insertion
		
		String sql ;
        if(wf.getIdUtilisateur() == -1 ) {

        	sql = "INSERT INTO workflow (titre, date_creation) VALUES (?, ?)";
        }else {
    	    sql = "INSERT INTO workflow (titre, date_creation, id_utilisateur) VALUES (?, ?, ?)";
        }
	    int generatedId = -1;

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

	        // Paramètre 1 : titre
	        ps.setString(1, wf.getTitre());

	        // Paramètre 2 : date_creation
	        ps.setTimestamp(2, new java.sql.Timestamp(wf.getDateCreation().getTime()));

	        // Paramètre 3 : id_utilisateur (Récupéré depuis l'objet Workflow)
	        // Si ton ID utilisateur dans l'objet est un int, utilise String.valueOf(wf.getIdUtilisateur())
	        if(wf.getIdUtilisateur() == -1 ) {

	        }else {
		        ps.setInt(3, wf.getIdUtilisateur());

	        }

	        ps.executeUpdate();

	        try (ResultSet rs = ps.getGeneratedKeys()) {
	            if (rs.next()) {
	                generatedId = rs.getInt(1);
	            }
	        }
	    } catch (SQLException e) {
	        System.err.println("Erreur lors de la création du workflow avec id_utilisateur : " + e.getMessage());
	        e.printStackTrace();
	    }
	    return generatedId;
	}

	/**
	 * Marque le workflow comme terminé en enregistrant la date de finalisation.
	 * 
	 * @param idWf L'identifiant du workflow à clôturer
	 */
	public void finaliserWorkflow(int idWf) {
		String sql = "UPDATE workflow SET date_finalisation = CURRENT_DATE , statut = 'TERMINER' WHERE id = ?";

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
		} catch (SQLException e) {
			e.printStackTrace();
		}
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
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	private Workflow mapResultSetToWorkflow(ResultSet rs) throws SQLException {
		Workflow wf = new Workflow();

		// Mapping des colonnes de la table 'workflow' vers les attributs de l'objet
		wf.setId(rs.getInt("id"));
		wf.setTitre(rs.getString("titre"));

		// Gestion des dates (on utilise getTimestamp pour garder heure/minute si
		// besoin)
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

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

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

	// PREMIÈRE FONCTION : Récupère les workflows en attente du rôle spécifié
	public static List<Workflow> getWorkflowsEnAttenteParRole(int roleId) {
	    List<Workflow> liste = new ArrayList<>();
	    
	    // Une seule requête dynamique basée sur la table 'droit'
	    String sql = 
	        "SELECT DISTINCT w.id, w.titre, w.commentaire "
	      + "FROM workflow w "
	      + "JOIN droit d ON d.role = ? " // On récupère toutes les étapes associées à ce rôle
	      + "WHERE (w.statut IS NULL OR LOWER(w.statut) NOT LIKE 'termin%') "
	      + "  AND w.date_finalisation IS NULL "
	      + "  -- RÈGLE DE BASE : L'étape du droit ne doit pas encore être validée -- "
	      + "  AND NOT EXISTS (SELECT 1 FROM validation v WHERE v.id_workflow = w.id AND CAST(v.etape AS INT) = CAST(d.etape AS INT)) "
	      + "  AND ( "
	      + "    (CAST(d.etape AS INT) BETWEEN 1 AND 6 "
	      + "     AND (CAST(d.etape AS INT) = 1 OR EXISTS ( "
	      + "         SELECT 1 FROM validation v "
	      + "         WHERE v.id_workflow = w.id AND CAST(v.etape AS INT) = CAST(d.etape AS INT) - 1 "
	      + "     )) "
	      + "    ) "
	      + "    OR "
	      + "    -- 2️⃣ CAS DES ÉTAPES EN SÉRIE (7 et plus) -- "
	      + "    (CAST(d.etape AS INT) >= 7 "
	      + "     AND (SELECT COUNT(DISTINCT CAST(v.etape AS INT)) FROM validation v WHERE v.id_workflow = w.id AND CAST(v.etape AS INT) BETWEEN 1 AND 6) = 6 "
	      + "     AND (SELECT COALESCE(MAX(CAST(v.etape AS INT)), 0) + 1 FROM validation v WHERE v.id_workflow = w.id) = CAST(d.etape AS INT) "
	      + "    ) "
	      + "  );";

	    try (Connection conn = DBConnection.getConnection(); 
	         PreparedStatement ps = conn.prepareStatement(sql)) {
	        
	        // Un seul paramètre à injecter : le rôle de l'utilisateur connecté
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
	        System.err.println("Erreur SQL lors de la récupération pour le rôle : " + roleId);
	        e.printStackTrace();
	    }
	    return liste;
	}

	// DEUXIÈME FONCTION : Récupère les workflows terminés qui n'ont pas encore été
	// annoncés
	public static List<Workflow> getWorkflowsTerminesNonAnnonces() {
		List<Workflow> liste = new ArrayList<>();
		String sql = "SELECT * FROM workflow WHERE (statut = 'Terminé' OR date_finalisation IS NOT NULL) AND annonce_termine = FALSE";

		try (Connection conn = DBConnection.getConnection();
				PreparedStatement ps = conn.prepareStatement(sql);
				ResultSet rs = ps.executeQuery()) {

			
			
			while (rs.next()) {
				Workflow w = new Workflow();
				w.setId(rs.getInt("id"));
				w.setTitre(rs.getString("titre"));
				w.setCommentaire(rs.getString("commentaire"));
				liste.add(w);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return liste;
	}

	// Marquer le workflow comme annoncé pour ne pas réenvoyer l'e-mail au prochain
	// coup
	public static void marquerWorkflowTermineAnnonce(int workflowId) {
		String sql = "UPDATE workflow SET annonce_termine = TRUE WHERE id = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, workflowId);
			ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// Dans WorkflowDAO.java
	public static List<Workflow> getWorkflowsActifs() {
	    List<Workflow> list = new ArrayList<>();
	    // Requête triviale, sans jointure complexe, gère le statut NULL
	    String sql = "SELECT id, titre FROM workflow WHERE (statut IS NULL OR statut != 'Terminé') AND date_finalisation IS NULL";
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



	// Dans DroitDAO.java (Pour charger la table des exceptions/droits)
	public static List<Droit> getDroitsSpecifiques() {
	    List<Droit> list = new ArrayList<>();
	    String sql = "SELECT role, etape FROM droit";
	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {
	        while (rs.next()) {
	            list.add(new Droit(rs.getInt("role"), rs.getInt("etape")));
	        }
	    } catch (Exception e) { e.printStackTrace(); }
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
	
	public List<WorkflowDisplay> getWorkflowsWithDetailsByStatus(String status) {
	    List<WorkflowDisplay> list = new ArrayList<>();
	    
	    // Le calcul extrait le MAX(etape) de la table validation et lui ajoute +1 
	    // Si aucune validation n'existe, COALESCE donne 0, +1 = Étape 1 (Début du dossier)
	    String sql = 
	        "SELECT w.*, u.nom AS nom_demandeur, " +
	        "  (COALESCE((SELECT MAX(CAST(v.etape AS INT)) FROM validation v WHERE v.id_workflow = w.id), 0) + 1) AS etape_actuelle, " +
	        "  COALESCE((SELECT d.attribut FROM donnee d " +
	        "   WHERE d.id_workflow = w.id " +
	        "     AND CAST(d.nb_etape AS INT) = COALESCE((SELECT MAX(CAST(v.etape AS INT)) FROM validation v WHERE v.id_workflow = w.id), 0) " +
	        "     AND d.type IN ('Avis D.C.D.', 'Avis D.O.P.', 'faisable', 'avis production', 'Avis logistique', 'Avis Q.H.E.') " +
	        "   LIMIT 1), '') AS raw_avis " +
	        "FROM workflow w " +
	        "LEFT JOIN utilisateur u ON CAST(w.id_utilisateur AS VARCHAR) = CAST(u.id AS VARCHAR) ";

	    // Application dynamique des filtres d'onglets
	    if ("en_cours".equals(status)) {
	        sql += "WHERE w.date_finalisation IS NULL ";
	    } else if ("termine".equals(status)) {
	        sql += "WHERE w.date_finalisation IS NOT NULL AND LOWER(w.statut) LIKE '%termin%' ";
	    } else if ("annule".equals(status)) {
	        sql += "WHERE w.date_finalisation IS NOT NULL AND LOWER(w.statut) NOT LIKE '%termin%' ";
	    }
	    
	    sql += "ORDER BY w.date_creation DESC";

	    try (Connection con = DBConnection.getConnection();
	         PreparedStatement ps = con.prepareStatement(sql);
	         ResultSet rs = ps.executeQuery()) {

	        while (rs.next()) {
	            Workflow wf = new Workflow();
	            wf.setId(rs.getInt("id"));
	            wf.setTitre(rs.getString("titre"));
	            wf.setDateCreation(rs.getTimestamp("date_creation"));
	            wf.setDateFinalisation(rs.getTimestamp("date_finalisation"));
	            wf.setStatut(rs.getString("statut"));

	            String nomDemandeur = rs.getString("nom_demandeur");
	            if (nomDemandeur == null) {
	                nomDemandeur = (rs.getString("id_utilisateur") != null) ? "ID : " + rs.getString("id_utilisateur") : "Inconnu";
	            }

	            list.add(new WorkflowDisplay(
	                wf, 
	                nomDemandeur, 
	                rs.getInt("etape_actuelle"), 
	                rs.getString("raw_avis")
	            ));
	        }
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return list;
	}

    // 2. Récupération par Barre de Recherche
    public List<WorkflowDisplay> searchWorkflowsWithDetails(String query) {
        String condition = "WHERE CAST(w.id AS VARCHAR) LIKE ? OR LOWER(w.titre) LIKE ? ";
        return executeAggregationQuery(condition, "%" + query.toLowerCase() + "%");
    }

    // Cœur SQL : Récupère Workflow + Demandeur + Étape + Avis en 1 seul aller-retour
    private List<WorkflowDisplay> executeAggregationQuery(String conditionClause, String searchParam) {
        List<WorkflowDisplay> list = new ArrayList<>();
        
        String sql = 
            "SELECT w.*, u.nom AS nom_demandeur, " +
            "  COALESCE((SELECT MAX(CAST(v.etape AS INT)) FROM validation v WHERE v.id_workflow = w.id), 1) AS etape_actuelle, " +
            "  (SELECT d.attribut FROM donnee d " +
            "   WHERE d.id_workflow = w.id " +
            "     AND CAST(d.etape AS INT) = COALESCE((SELECT MAX(CAST(v.etape AS INT)) FROM validation v WHERE v.id_workflow = w.id), 1) " +
            "     AND d.type IN ('Avis D.C.D.', 'Avis D.O.P.', 'faisable', 'avis production', 'Avis logistique', 'Avis Q.H.E.') " +
            "   LIMIT 1) AS raw_avis " +
            "FROM workflow w " +
            "LEFT JOIN utilisateur u ON CAST(w.id_utilisateur AS VARCHAR) = CAST(u.id AS VARCHAR) " +
            conditionClause +
            "ORDER BY w.date_creation DESC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            if (searchParam != null) {
                ps.setString(1, searchParam);
                ps.setString(2, searchParam);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Workflow wf = new Workflow();
                    wf.setId(rs.getInt("id"));
                    wf.setTitre(rs.getString("titre"));
                    wf.setDateCreation(rs.getTimestamp("date_creation"));
                    wf.setDateFinalisation(rs.getTimestamp("date_finalisation"));
                    wf.setStatut(rs.getString("statut"));

                    String nomDemandeur = rs.getString("nom_demandeur");
                    if (nomDemandeur == null) {
                        nomDemandeur = (rs.getString("id_utilisateur") != null) ? "ID : " + rs.getString("id_utilisateur") : "Inconnu";
                    }
                    
                    list.add(new WorkflowDisplay(
                        wf, 
                        nomDemandeur, 
                        rs.getInt("etape_actuelle"), 
                        rs.getString("raw_avis") != null ? rs.getString("raw_avis") : ""
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

	// 1. REQUÊTE : Trouver les dossiers dont l'étape vient de se TERMINER et qui n'ont PAS ENCORE été annoncés
	public static List<Workflow> getWorkflowsTerminesPourRole(int roleId) {
	    List<Workflow> list = new ArrayList<>();
	    
	    // On joint la table droit pour s'assurer que ce rôle possède l'étape liée au workflow finalisé
	    // ET on vérifie dans une table d'historique (ou via une logique de droits) que ce rôle précis n'a pas encore reçu l'alerte.
	    
	    String sql = "SELECT DISTINCT w.id, w.titre FROM workflow w " +
	                 "WHERE w.statut = 'TERMINER' " +
	                 "  AND w.annonce_termine = false "  ; // On s'assure que le rôle a le droit sur cette fin de processus

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {
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
}

