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

    public void deleteDonnee(int id) {
        String sql = "DELETE FROM template_donnee WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
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

   
}