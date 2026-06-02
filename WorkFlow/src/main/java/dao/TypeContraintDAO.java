package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.TypeContraint;


public class TypeContraintDAO {

    // Récupérer toutes les contraintes ordonnées par type
    public List<TypeContraint> getAll() {
        List<TypeContraint> liste = new ArrayList<>();
        String sql = "SELECT id, type, valeur FROM type_contraint ORDER BY type ASC, valeur ASC";
        
        // Remplace par ton systaème de connexion
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                liste.add(new TypeContraint(
                    rs.getInt("id"),
                    rs.getString("type"),
                    rs.getString("valeur")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }
    // Ajouter une nouvelle valeur de contrainte
    public boolean create(TypeContraint tc) {
        String sql = "INSERT INTO type_contraint (type, valeur) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, tc.getType().trim());
            ps.setString(2, tc.getValeur().trim());
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Modifier une valeur existante
    public boolean update(TypeContraint tc) {
        String sql = "UPDATE type_contraint SET type = ?, valeur = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, tc.getType().trim());
            ps.setString(2, tc.getValeur().trim());
            ps.setInt(3, tc.getId());
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Supprimer une contrainte
    public boolean delete(int id) {
        String sql = "DELETE FROM type_contraint WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}