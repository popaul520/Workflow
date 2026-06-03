package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ValidationDAO {

	public void validerEtape(int idWf, int idUser, int nEtape) throws SQLException {
		// Nettoyage de la requête : plus besoin de CAST en SQL si on envoie le bon type
		// Java
		String sql;
		if(idUser == -1) {
			 sql = "INSERT INTO validation (id_workflow, etape, date) "
					+ "VALUES (?, ?, CURRENT_DATE) " + "ON CONFLICT (id_workflow, etape) "
					+ "DO UPDATE SET  date = CURRENT_DATE";
		}else {
			 sql = "INSERT INTO validation (id_workflow,  etape, id_personne,  date) "
					+ "VALUES (?, ?, ?, CURRENT_DATE) " + "ON CONFLICT (id_workflow, etape) "
					+ "DO UPDATE SET id_personne = EXCLUDED.id_personne, date = CURRENT_DATE";
		}


		try (Connection conn = dao.DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {

			ps.setInt(1, idWf);
			if(idUser == -1) {

			}else {
				ps.setInt(3, idUser);

			}
			ps.setInt(2, nEtape); 

			ps.executeUpdate();
		}
	}

	public int getDerniereEtapeValidee(int idWf) throws SQLException {
		String sql = "SELECT MAX(CAST(etape AS INTEGER)) FROM validation WHERE id_workflow = ?";
		try (Connection conn = DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, idWf);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1); // Retourne 0 si aucune ligne
			}
		}
		return 0;
	}

	public boolean sontEtapes1a6Validees(int idWf) throws SQLException {
		String sql = "SELECT COUNT(DISTINCT etape) FROM validation WHERE id_workflow = ? AND CAST(etape AS INTEGER) BETWEEN 1 AND 6";
		try (Connection conn = dao.DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, idWf);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return rs.getInt(1) == 6; // Vrai si on a 6 étapes distinctes validées
			}
		}
		return false;
	}

	public List<Integer> getListeEtapesValidees(int idWf) throws SQLException {
		List<Integer> etapes = new ArrayList<>();
		String sql = "SELECT DISTINCT CAST(etape AS INTEGER) FROM validation WHERE id_workflow = ?";
		try (Connection conn = dao.DBConnection.getConnection(); PreparedStatement ps = conn.prepareStatement(sql)) {
			ps.setInt(1, idWf);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				etapes.add(rs.getInt(1));
			}
		}
		return etapes;
	}

}
