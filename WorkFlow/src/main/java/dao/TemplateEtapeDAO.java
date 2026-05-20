package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.templateWorkflow;
import model.template_etape;

public class TemplateEtapeDAO {

    public List<template_etape> getEtapesByWorkflow(int workflowId) {
        List<template_etape> list = new ArrayList<>();
        String sql = "SELECT * FROM template_etape WHERE id_template_workflow = ? ORDER BY place ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, workflowId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
            	template_etape e = new template_etape();
                e.setId(rs.getInt("id"));
                e.setIdTemplateWorkflow(rs.getInt("id_template_workflow"));
                e.setRoleAssocie(rs.getInt("role_associe"));
                e.setPlace(rs.getInt("place"));
                e.setAttentePlace(rs.getInt("attente_place"));
                e.setNomEtape(rs.getString("nom_etape"));
                e.setEstFinale(rs.getBoolean("est_finale"));
                list.add(e);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    
    public void delete(int id) {
        String sql = "DELETE FROM template_etape WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }
    
    public template_etape getEtapeById(int id) {
        String sql = "SELECT * FROM template_etape WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTemplate(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private template_etape mapResultSetToTemplate(ResultSet rs) throws SQLException {
    	template_etape t  = new template_etape();
        t.setId(rs.getInt("id"));
        t.setIdTemplateWorkflow(rs.getInt("id_template_workflow"));
        t.setRoleAssocie(rs.getInt("role_associe"));
        t.setPlace(rs.getInt("place"));
        t.setAttentePlace(rs.getInt("attente_place"));
        t.setNomEtape(rs.getString("nom_etape"));
        t.setEstFinale(rs.getBoolean("est_finale"));
        return t;
    }
    
    public void decalerPlacesA_PartirDe(int idWorkflow, int placeSaisie) {
        String sql = "UPDATE template_etape SET place = place + 1 " +
                     "WHERE id_template_workflow = ? AND place >= ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idWorkflow);
            ps.setInt(2, placeSaisie);
            ps.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void echangerPlaces(int idWorkflow, int placeCible, int placeOrigine) {
        // Si l'étape est nouvelle (placeOrigine = 0), on ne peut pas intervertir
        // cette méthode ne s'applique que pour une modification (Update).
        if (placeOrigine <= 0) return;

        String sql = "UPDATE template_etape SET place = ? " +
                     "WHERE id_template_workflow = ? AND place = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, placeOrigine);
            ps.setInt(2, idWorkflow);
            ps.setInt(3, placeCible);   
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void ajusterPositionEtSauvegarder(template_etape etape) {
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            int idWf = etape.getIdTemplateWorkflow();
            int cible = etape.getPlace();
            int id = etape.getId();

            // 1. Récupérer la place d'origine en BDD
            int placeOrigine = 0;
            String sqlGetOrigine = "SELECT place FROM template_etape WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(sqlGetOrigine)) {
                ps.setInt(1, id);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        placeOrigine = rs.getInt("place");
                    }
                }
            }

            // 2. Appliquer le glissement si l'étape existe et qu'elle change de place
            if (id != 0 && placeOrigine != 0 && placeOrigine != cible) {
                
                if (placeOrigine < cible) {
                    // Toutes les étapes entre (origine + 1) et cible reculent de -1 (le 6 va à 5, etc.)
                    String sqlGlissementBas = "UPDATE template_etape SET place = place - 1 " +
                                              "WHERE id_template_workflow = ? AND place > ? AND place <= ?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlGlissementBas)) {
                        ps.setInt(1, idWf);
                        ps.setInt(2, placeOrigine);
                        ps.setInt(3, cible);
                        ps.executeUpdate();
                    }
                } else {
                    // Toutes les étapes entre cible et (origine - 1) avancent de +1 (le 2 va à 3, etc.)
                    String sqlGlissementHaut = "UPDATE template_etape SET place = place + 1 " +
                                               "WHERE id_template_workflow = ? AND place >= ? AND place < ?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlGlissementHaut)) {
                        ps.setInt(1, idWf);
                        ps.setInt(2, cible);
                        ps.setInt(3, placeOrigine);
                        ps.executeUpdate();
                    }
                }
            } 
            //  On fait juste de la place à partir de l'index cible
            else if (id == 0) {
                String sqlInsertDecaler = "UPDATE template_etape SET place = place + 1 " +
                                          "WHERE id_template_workflow = ? AND place >= ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlInsertDecaler)) {
                    ps.setInt(1, idWf);
                    ps.setInt(2, cible);
                    ps.executeUpdate();
                }
            }

            // 3. SAUVEGARDE FINALE DE L'ÉTAPE MODIFIÉE OU CRÉÉE
            String sqlSave = (id == 0) 
                ? "INSERT INTO template_etape (nom_etape, place, role_associe, est_finale, attente_place, id_template_workflow) VALUES (?, ?, ?, ?, ?, ?)"
                : "UPDATE template_etape SET nom_etape = ?, place = ?, role_associe = ?, est_finale = ?, attente_place = ? WHERE id = ?";

            try (PreparedStatement ps = conn.prepareStatement(sqlSave)) {
                ps.setString(1, etape.getNomEtape());
                ps.setInt(2, cible);
                ps.setInt(3, etape.getRoleAssocie());
                ps.setBoolean(4, etape.isEstFinale());
                
                if (etape == null || etape.getAttentePlace() == 0) {
                    ps.setNull(5, Types.INTEGER);
                } else {
                    ps.setInt(5, etape.getAttentePlace());
                }
                
                ps.setInt(6, (id == 0) ? idWf : id);
                ps.executeUpdate();
            }

            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void saveOrUpdate(template_etape etape) {
        try (Connection conn = DBConnection.getConnection()) {
            if (etape.getId() == 0) {
                // ---- INSERTION ----
                String sql = "INSERT INTO template_etape (id_template_workflow, nom_etape, place, role_associe, est_finale, attente_place) VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, etape.getIdTemplateWorkflow());
                    ps.setString(2, etape.getNomEtape());
                    ps.setInt(3, etape.getPlace());
                    ps.setInt(4, etape.getRoleAssocie());
                    ps.setBoolean(5, etape.isEstFinale());
                    
                    // Gestion sécurisée du Integer (NULL) pour PostgreSQL
                    if (etape.getAttentePlace() == 0) {
                        ps.setNull(6, Types.INTEGER);
                    } else {
                        ps.setInt(6, etape.getAttentePlace());
                    }
                    ps.executeUpdate();
                }
            } else {
                String sql = "UPDATE template_etape SET nom_etape = ?, place = ?, role_associe = ?, est_finale = ?, attente_place = ? WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, etape.getNomEtape());
                    ps.setInt(2, etape.getPlace());
                    ps.setInt(3, etape.getRoleAssocie());
                    ps.setBoolean(4, etape.isEstFinale());
                    
                    if (etape.getAttentePlace() == 0) {
                        ps.setNull(5, Types.INTEGER);
                    } else {
                        ps.setInt(5, etape.getAttentePlace());
                    }
                    ps.setInt(6, etape.getId());
                    ps.executeUpdate();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //suprime et actualise la palace des autres 
    public void delete(int idEtape, int idWorkflow) {
        String sqlGetPlace = "SELECT place FROM template_etape WHERE id = ?";
        String sqlResetAttente = "UPDATE template_etape SET attente_place = 0 WHERE id_template_workflow = ? AND attente_place = ?";
        String sqlDelete = "DELETE FROM template_etape WHERE id = ?";
        String sqlDecaler = "UPDATE template_etape SET place = place - 1 WHERE id_template_workflow = ? AND place > ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false); 

            int placeSupprimee = 0;

            try (PreparedStatement ps = conn.prepareStatement(sqlGetPlace)) {
                ps.setInt(1, idEtape);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        placeSupprimee = rs.getInt("place");
                    }
                }
            }

            if (placeSupprimee > 0) {
                try (PreparedStatement ps = conn.prepareStatement(sqlResetAttente)) {
                    ps.setInt(1, idWorkflow);
                    ps.setInt(2, placeSupprimee);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(sqlDelete)) {
                    ps.setInt(1, idEtape);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(sqlDecaler)) {
                    ps.setInt(1, idWorkflow);
                    ps.setInt(2, placeSupprimee);
                    ps.executeUpdate();
                }
            }

            conn.commit(); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



