package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.templateWorkflow;
import model.template_donnee;

public class TemplateDonneeDAO {

    public List<template_donnee> getDonneesByEtape(int idEtape) {
        List<template_donnee> list = new ArrayList<>();
        String sql = "SELECT * FROM template_donnee WHERE id_template_etape = ? ORDER BY ordre_affichage";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idEtape);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
            	template_donnee d = new template_donnee();
                d.setId(rs.getInt("id"));
                d.setNomChamp(rs.getString("nom_champ"));
                d.setTypeComposant(rs.getString("type_composant"));
                d.setOrdreAffichage(rs.getInt("ordre_affichage"));
                d.setACommentaire(rs.getBoolean("a_commentaire"));
                d.setADate(rs.getBoolean("a_date"));
                d.setEstObligatoire(rs.getBoolean("est_obligatoire"));
                d.setRefContrainte(rs.getString("ref_contrainte"));
                list.add(d);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void addDonnee(template_donnee d) {
        String sql = "INSERT INTO template_donnee (id_template_etape, nom_champ, type_composant, ordre_affichage, a_commentaire, a_date, est_obligatoire, ref_contrainte) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, d.getIdTemplateEtape());
            ps.setString(2, d.getNomChamp());
            ps.setString(3, d.getTypeComposant());
            ps.setInt(4, d.getOrdreAffichage());
            ps.setBoolean(5, d.isACommentaire());
            ps.setBoolean(6, d.isADate());
            ps.setBoolean(7, d.isEstObligatoire());
            ps.setString(8, d.getRefContrainte());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }


    public void updateDonnee(template_donnee d) {
        String sql = "UPDATE template_donnee SET nom_champ=?, type_composant=?, ordre_affichage=?, a_commentaire=?, a_date=?, est_obligatoire=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, d.getNomChamp());
            ps.setString(2, d.getTypeComposant());
            ps.setInt(3, d.getOrdreAffichage());
            ps.setBoolean(4, d.isACommentaire());
            ps.setBoolean(5, d.isADate());
            ps.setBoolean(6, d.isEstObligatoire());
            ps.setInt(7, d.getId());
            ps.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
    
    public void ajusterPositionEtSauvegarder(template_donnee donnee) {
        String sqlGetOrigine = "SELECT ordre_affichage FROM template_donnee WHERE id = ?";
        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);
            int idEtape = donnee.getIdTemplateEtape();
            int cible = donnee.getOrdreAffichage();
            int id = donnee.getId();

            // 1. Récupérer l'ancienne position de cette donnée en BDD
            int ordreOrigine = 0;
            if (id != 0) {
                try (PreparedStatement ps = conn.prepareStatement(sqlGetOrigine)) {
                    ps.setInt(1, id);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            ordreOrigine = rs.getInt("ordre_affichage");
                        }
                    }
                }
            }

            // 2. Appliquer le glissement si la donnée existe et change de position
            if (id != 0 && ordreOrigine != 0 && ordreOrigine != cible) {
                if (ordreOrigine < cible) {
                    // Les données intermédiaires remontent de -1
                    String sqlGlissementBas = "UPDATE template_donnee SET ordre_affichage = ordre_affichage - 1 " +
                                              "WHERE id_template_etape = ? AND ordre_affichage > ? AND ordre_affichage <= ?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlGlissementBas)) {
                        ps.setInt(1, idEtape);
                        ps.setInt(2, ordreOrigine);
                        ps.setInt(3, cible);
                        ps.executeUpdate();
                    }
                } else {
                    //Les données intermédiaires descendent de +1
                    String sqlGlissementHaut = "UPDATE template_donnee SET ordre_affichage = ordre_affichage + 1 " +
                                               "WHERE id_template_etape = ? AND ordre_affichage >= ? AND ordre_affichage < ?";
                    try (PreparedStatement ps = conn.prepareStatement(sqlGlissementHaut)) {
                        ps.setInt(1, idEtape);
                        ps.setInt(2, cible);
                        ps.setInt(3, ordreOrigine);
                        ps.executeUpdate();
                    }
                }
            } 
            // On fait de la place à partir de l'index cible
            else if (id == 0) {
                String sqlInsertDecaler = "UPDATE template_donnee SET ordre_affichage = ordre_affichage + 1 " +
                                          "WHERE id_template_etape = ? AND ordre_affichage >= ?";
                try (PreparedStatement ps = conn.prepareStatement(sqlInsertDecaler)) {
                    ps.setInt(1, idEtape);
                    ps.setInt(2, cible);
                    ps.executeUpdate();
                }
            }

            // 3. SAUVEGARDE FINALE DE LA DONNÉE 
            String sqlSave = (id == 0)
                ? "INSERT INTO template_donnee (id_template_etape, nom_champ, type_composant, ordre_affichage, a_commentaire, a_date, est_obligatoire) VALUES (?, ?, ?, ?, ?, ?, ?)"
                : "UPDATE template_donnee SET id_template_etape = ?, nom_champ = ?, type_composant = ?, ordre_affichage = ?, a_commentaire = ?, a_date = ?, est_obligatoire = ? WHERE id = ?";

            try (PreparedStatement ps = conn.prepareStatement(sqlSave)) {
                ps.setInt(1, idEtape);
                ps.setString(2, donnee.getNomChamp());
                ps.setString(3, donnee.getTypeComposant());
                ps.setInt(4, cible);
                ps.setBoolean(5, donnee.isACommentaire());
                ps.setBoolean(6, donnee.isADate());
                ps.setBoolean(7, donnee.isEstObligatoire());
                
                if (id != 0) {
                    ps.setInt(8, id);
                }
                ps.executeUpdate();
            }

            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteDonnee(int idDonnee) {
        String sqlGetInfos = "SELECT id_template_etape, ordre_affichage FROM template_donnee WHERE id = ?";
        String sqlDelete = "DELETE FROM template_donnee WHERE id = ?";
        String sqlDecaler = "UPDATE template_donnee SET ordre_affichage = ordre_affichage - 1 WHERE id_template_etape = ? AND ordre_affichage > ?";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            int idEtape = 0;
            int ordreSupprime = 0;

            try (PreparedStatement ps = conn.prepareStatement(sqlGetInfos)) {
                ps.setInt(1, idDonnee);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        idEtape = rs.getInt("id_template_etape");
                        ordreSupprime = rs.getInt("ordre_affichage");
                    }
                }
            }

            if (idEtape > 0) {
                try (PreparedStatement ps = conn.prepareStatement(sqlDelete)) {
                    ps.setInt(1, idDonnee);
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(sqlDecaler)) {
                    ps.setInt(1, idEtape);
                    ps.setInt(2, ordreSupprime);
                    ps.executeUpdate();
                }
            }

            conn.commit(); 
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}