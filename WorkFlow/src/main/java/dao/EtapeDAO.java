package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import model.Etape;

public class EtapeDAO {

    // Trouver une étape précise par Workflow et Numéro
    public Etape findEtape(int idWorkflow, int nbEtape) {
        Etape etape = null;
        String sql = "SELECT * FROM etape WHERE id_workflow = ? AND nb_etape = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idWorkflow);
            ps.setInt(2, nbEtape);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                etape = new Etape();
                etape.setNbEtape(rs.getInt("nb_etape"));
                etape.setRole(rs.getString("role"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return etape;
    }
    
    
}