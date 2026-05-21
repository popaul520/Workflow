package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Donnee;
import model.Etape;
import model.template_donnee;

public class DonneeDAO {

	public boolean insertDonnee(Donnee d, int idWorkflow, int nbEtape) {
		String sql = "INSERT INTO donnee (type, attribut, commentaire, date, id_workflow, nb_etape, type_contraint_ref) "
				+ "VALUES (?, ?, ?, ?, ?, ?, ?)";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setString(1, d.getType());
			ps.setString(2, d.getAttribut());
			ps.setString(3, d.getCommentaire());
			ps.setDate(4, d.getDate());
			ps.setInt(5, idWorkflow);
			ps.setInt(6, nbEtape);

			ps.setString(7, d.getRefTypeContraint());

			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void deleteDonneesByEtape(int idWorkflow, int nbEtape) {
		String sql = "DELETE FROM donnee WHERE id_workflow = ? AND nb_etape = ?";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, idWorkflow);
			ps.setInt(2, nbEtape);
			ps.executeUpdate();

			System.out.println("Nettoyage effectué pour l'étape " + nbEtape + " du WF " + idWorkflow);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public List<Donnee> getDonneesByWorkflow(int idWorkflow) {
		List<Donnee> liste = new ArrayList<>();
		// On récupère les données triées par numéro d'étape
		String sql = "SELECT * FROM donnee WHERE id_workflow = ? ORDER BY nb_etape ASC";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, idWorkflow);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Donnee d = new Donnee();
				d.setType(rs.getString("type"));
				d.setAttribut(rs.getString("attribut"));
				d.setCommentaire(rs.getString("commentaire"));
				d.setDate(rs.getDate("date"));

				// On crée un objet Etape fictif pour porter le numéro dans la JSP
				Etape e = new Etape();
				e.setNbEtape(rs.getInt("nb_etape"));
				d.setEtape(e);

				liste.add(d);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return liste;
	}

	public List<Donnee> getDonneesByEtape(int idWorkflow, int nbEtape) {
		List<Donnee> liste = new ArrayList<>();
		String sql = "SELECT * FROM donnee WHERE id_workflow = ? AND nb_etape = ?";
		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			ps.setInt(1, idWorkflow);
			ps.setInt(2, nbEtape);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					Donnee d = new Donnee();
					d.setIdDonne(rs.getLong("id_donne"));

					d.setType(rs.getString("type"));
					d.setAttribut(rs.getString("attribut"));
					d.setCommentaire(rs.getString("commentaire"));
					d.setDate(rs.getDate("date"));
					d.setRefTypeContraint(rs.getString("type_contraint_ref"));

					liste.add(d);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return liste;
	}

	public List<String> getValeursContraintes(String type) {
		List<String> valeurs = new java.util.ArrayList<>();
		String sql = "SELECT valeur FROM type_contraint WHERE type = ?";
		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, type);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					valeurs.add(rs.getString("valeur"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return valeurs;
	}

	public List<Donnee> getAllDonneesByWorkflow(int idWorkflow) {
		List<Donnee> liste = new ArrayList<>();
		String sql = "SELECT * FROM donnee WHERE id_workflow = ? ORDER BY nb_etape ASC";
		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setInt(1, idWorkflow);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				Donnee d = new Donnee();
				d.setIdDonne(rs.getLong("id_donne"));
				d.setType(rs.getString("type"));
				d.setAttribut(rs.getString("attribut"));
				d.setCommentaire(rs.getString("commentaire"));
				d.setDate(rs.getDate("date"));
				Etape e = new Etape();
				e.setNbEtape(rs.getInt("nb_etape"));
				d.setEtape(e);
				liste.add(d);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return liste;
	}

	public boolean updateDonnee(Donnee d) {
		String sql = "UPDATE donnee SET attribut = ?, commentaire = ?, date = ?, type = ?, type_contraint_ref = ? WHERE id_donne = ?";
		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
			ps.setString(1, d.getAttribut());
			ps.setString(2, d.getCommentaire());
			ps.setDate(3, d.getDate());
			ps.setString(4, d.getType());
			ps.setString(5, d.getRefTypeContraint());
			ps.setLong(6, d.getIdDonne());
			return ps.executeUpdate() > 0;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public void creerEtapeWorkflow(int idWorkflow, int nbEtape) {
		// 1. Définition du rôle selon l'étape
		String role = "";
		switch (nbEtape) {
		case 1:
			role = "COMMERCE";
			break;
		case 2:
			role = "PRODUCTION";
			break;
		case 3:
			role = "APPROVISIONNEMENT";
			break;
		case 4:
			role = "S.C.M.";
			break;
		case 5:
			role = "LOGISTIQUE";
			break;
		default:
			role = "ADMIN";
		}

		// 2. La requête SQL
		String sql = "INSERT INTO etape (id_workflow, nb_etape, role) " + "SELECT ?, ?, ? WHERE NOT EXISTS ("
				+ "SELECT 1 FROM etape WHERE id_workflow = ? AND nb_etape = ?)";

		try (Connection con = DBConnection.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

			// Valeurs pour le SELECT
			ps.setInt(1, idWorkflow);
			ps.setInt(2, nbEtape);
			ps.setString(3, role);

			// Valeurs pour le WHERE NOT EXISTS
			ps.setInt(4, idWorkflow);
			ps.setInt(5, nbEtape);

			int rows = ps.executeUpdate();
			if (rows > 0) {
				System.out.println("Nouvelle étape créée : " + role);
			}
		} catch (Exception e) {
			System.err.println("Erreur creerEtapeWorkflow : " + e.getMessage());
			e.printStackTrace();
		}
	}

	public String getValeurAttribut(int idWf, int etape, String typeDonnee) {
		String valeur = "";
		// On cherche l'attribut (la valeur saisie) pour un workflow, une étape et un
		// type précis
		String sql = "SELECT attribut FROM donnee WHERE id_workflow = ? AND nb_etape = ? AND type = ?";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, idWf);
			ps.setInt(2, etape);
			ps.setString(3, typeDonnee);

			try (ResultSet rs = ps.executeQuery()) {
				if (rs.next()) {
					valeur = rs.getString("attribut");
				}
			}
		} catch (SQLException e) {
			System.err.println("Erreur getValeurAttribut : " + e.getMessage());
			e.printStackTrace();
		}

		// On retourne une chaîne vide si rien n'est trouvé, pour éviter les
		// NullPointerException dans la JSP
		return (valeur != null) ? valeur : "";
	}

	public List<template_donnee> getDonneesSaisie(int idWorkflow, int idTemplateWorkflow, int numEtape) {
		List<template_donnee> liste = new ArrayList<>();

		// Cette requête récupère TOUS les champs prévus par le template,
		// et cherche s'il y a déjà une saisie correspondante dans 'donnee'
		String sql = "SELECT " + "  dt.id AS id_template_donnee, " + "  dt.nom_champ, " + "  dt.type_composant, "
				+ "  dt.a_commentaire, " + "  dt.a_date, " + "  dt.est_obligatoire, " + "  dt.ref_contrainte, "
				+ "  d.id_donne, " + "  d.attribut, " + "  d.commentaire, " + "  d.date " + "FROM donnee_template dt "
				+ "LEFT JOIN donnee d ON dt.id = d.id_template_donnee AND d.id_workflow = ? "
				+ "WHERE dt.id_template_etape = ("
				+ "    SELECT te.id FROM template_etape te WHERE te.id_template_workflow = ? AND te.attente_place = ?"
				+ ") " + "ORDER BY dt.ordre_affichage";

		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, idWorkflow);
			ps.setInt(2, idTemplateWorkflow);
			ps.setInt(3, numEtape);

			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					template_donnee df = new template_donnee();
					df.setIdTemplateEtape(rs.getInt("id_template_donnee"));
					df.setNomChamp(rs.getString("nom_champ"));
					df.setTypeComposant(rs.getString("type_composant"));
					df.setACommentaire(rs.getBoolean("a_commentaire"));
					df.setADate(rs.getBoolean("a_date"));
					df.setEstObligatoire(rs.getBoolean("est_obligatoire"));
					df.setRefContrainte(rs.getString("ref_contrainte"));

					// Données de saisie (vaudront 0 ou null au premier affichage)
					df.setId(rs.getInt("id_donne"));
					// df.setAttribut(rs.getString("attribut"));
					df.setACommentaire(rs.getBoolean("commentaire"));
					df.setADate(rs.getBoolean("date"));

					liste.add(df);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return liste;
	}
}