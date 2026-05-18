package dao;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import model.templateWorkflow;
import model.template_donnee;
import model.template_etape;

public class TemplateDAO {

    // 1. Créer un nouveau template
    public void createTemplate(String nom, int version, String description, int idCreateur, boolean estActif) {
        String sql = "INSERT INTO template_workflow (nom, version, description, id_createur, est_actif, date_creation) " +
                     "VALUES (?, ?, ?, ?, ?, CURRENT_TIMESTAMP)";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, nom);
            ps.setInt(2, version);
            ps.setString(3, description);
            ps.setInt(4, idCreateur);
            ps.setBoolean(5, estActif);
            
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 2. Mettre à jour un template existant
    public void updateTemplate(int id, String nom, int version, String description, boolean estActif) {
        String sql = "UPDATE template_workflow SET nom = ?, version = ?, description = ?, est_actif = ? WHERE id = ?";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, nom);
            ps.setInt(2, version);
            ps.setString(3, description);
            ps.setBoolean(4, estActif);
            ps.setInt(5, id);
            
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 3. Récupérer un template par son ID
    public templateWorkflow getTemplateById(int id) {
        String sql = "SELECT * FROM template_workflow WHERE id = ?";
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

    // 4. Lister tous les templates (pour la page de gestion)
    public List<templateWorkflow> getAllTemplates() {
        List<templateWorkflow> list = new ArrayList<>();
        String sql = "SELECT * FROM template_workflow ORDER BY date_creation DESC";
        
        try (Connection con = DBConnection.getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            
            while (rs.next()) {
                list.add(mapResultSetToTemplate(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // 5. Supprimer un template (Attention aux cascades avec les étapes !)
    public void deleteTemplate(int id) {
        String sql = "DELETE FROM template_workflow WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Utilitaire : Transformer une ligne SQL en objet Java
    private templateWorkflow mapResultSetToTemplate(ResultSet rs) throws SQLException {
    	templateWorkflow t = new templateWorkflow();
        t.setId(rs.getInt("id"));
        t.setTitre(rs.getString("nom"));
        t.setVersion(rs.getInt("version"));
        t.setCommentaire(rs.getString("description"));
        t.setDateCreation(rs.getTimestamp("date_creation"));
        t.setCreateur(rs.getInt("id_createur"));
        t.setEstActif(rs.getBoolean("est_actif"));
        return t;
    }
    
    public templateWorkflow getTemplateById() {
        String sql = "SELECT * FROM template_workflow  order by id  desc limit  ";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            
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
    
    
    public int getLastGeneratedId() {
        int id = 0;
        // On utilise LASTVAL() qui retourne le dernier ID généré par une séquence 
        // dans la SESSION actuelle (très important pour la sécurité multi-utilisateur)
        String sql = "SELECT * FROM template_workflow   order by id  desc limit 1";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                id = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }
    
 // Modifie le type de retour ici : template_etape
    public template_etape getEtapeConfig(int idTemplate, int numEtape) {
        template_etape etape = null;
        // On utilise "ordre" ou "attente_place" selon le nom exact dans ta base
        String sql = "SELECT * FROM template_etape WHERE id_template_workflow = ? AND place = ?";
        
        try (Connection conn = DBConnection.getConnection(); 
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idTemplate);
            ps.setInt(2, numEtape);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    etape = new template_etape();
                    etape.setId(rs.getInt("id"));
                    etape.setIdTemplateWorkflow(rs.getInt("id_template_workflow"));
                    etape.setNomEtape(rs.getString("nom_etape"));
                    // Correction selon tes attributs :
                    etape.setAttentePlace(rs.getInt("place")); 
                    etape.setRoleAssocie(rs.getInt("role_associe")); 
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return etape; // Retourne l'objet config de l'étape
    }
    
    
    public List<template_etape> getEtapesByTemplate(int idTemplate) {
        List<template_etape> liste = new java.util.ArrayList<>();
        // On récupère les étapes ordonnées par leur place dans le Workflow
        String sql = "SELECT te.*, r.id FROM template_etape te " +
                     "LEFT JOIN role r ON te.role_associe = r.id " +
                     "WHERE te.id_template_workflow = ? ORDER BY te.place ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idTemplate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    template_etape etape = new template_etape();
                    etape.setId(rs.getInt("id"));
                    etape.setIdTemplateWorkflow(rs.getInt("id_template_workflow"));
                    etape.setNomEtape(rs.getString("nom_etape"));
                    etape.setAttentePlace(rs.getInt("place"));
                    etape.setRoleAssocie(rs.getInt("role_associe"));
                    // Optionnel : stocker le nom du rôle si tu as ajouté un attribut temporaire dans ton modèle
                    // etape.setNomRoleDesignation(rs.getString("nom_role")); 
                    liste.add(etape);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste; 
    }
}