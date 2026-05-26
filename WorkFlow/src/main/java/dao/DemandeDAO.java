package dao;
import java.sql.*;
import java.util.*;

import model.Demande;

public class DemandeDAO {

    public static Map<Integer, List<Demande>> getDemandesParRole() throws Exception {

        Map<Integer, List<Demande>> map = new HashMap<>();

        String sql = "SELECT * FROM demandes WHERE status = 'VALIDE' AND email_envoye = false";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                int role = rs.getInt("role_cible");

                Demande d = new Demande();
                d.setId(rs.getInt("id"));
                d.setNom(rs.getString("nom"));
                d.setDescription(rs.getString("description"));
                d.setStatus(rs.getString("status"));
                d.setRole_cible(role);

                map.computeIfAbsent(role, k -> new ArrayList<>()).add(d);
            }
        }

        return map;
    }

    public static void marquerEnvoye(int id) throws Exception {

        String sql = "UPDATE demandes SET email_envoye = true WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}