package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    // 4. Lister tous les templates
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

    // 5. Supprimer un template
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
    
    // 6. Récupérer le dernier ID de template généré
    public int getLastGeneratedId() {
        int id = 0;
        String sql = "SELECT id FROM template_workflow ORDER BY id DESC LIMIT 1";
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                id = rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }
    
    public templateWorkflow getTemplateByTitre(String titre) {
        templateWorkflow template = null;
        String sql = "SELECT id, nom, description  FROM template_workflow WHERE LOWER(nom) = LOWER(?)";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setString(1, titre);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    template = new templateWorkflow();
                    template.setId(rs.getInt("id"));
                    template.setTitre(rs.getString("nom"));
                    template.setCommentaire(rs.getString("description"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return template;
    }
    
    // 7. Récupérer la configuration d'une étape précise
 // 7. Récupérer la configuration d'une étape précise
    public template_etape getEtapeConfig(int idTemplate, int numEtape) {
        template_etape etape = null;
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
                    
                    etape.setPlace(rs.getInt("place"));         
                    etape.setAttentePlace(rs.getInt("attente_place"));
                    
                    etape.setRoleAssocie(rs.getInt("role_associe")); 
                    etape.setEstFinale(rs.getBoolean("est_finale"));
                }
            }
        } catch (SQLException ex) { 
            ex.printStackTrace(); 
        }
        return etape;
    }
    
    //  Récupérer toutes les étapes d'un template ordonnées
    public List<template_etape> getEtapesByTemplate(int idTemplate) {
        List<template_etape> liste = new ArrayList<>();
        String sql = "SELECT te.* FROM template_etape te WHERE te.id_template_workflow = ? ORDER BY te.place ASC";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, idTemplate);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    template_etape etape = new template_etape();
                    etape.setId(rs.getInt("id"));
                    etape.setIdTemplateWorkflow(rs.getInt("id_template_workflow"));
                    etape.setNomEtape(rs.getString("nom_etape"));
                    
                    // FIX ICI : Utilise setPlace pour que ${etape.place} fonctionne dans la JSP
                    etape.setPlace(rs.getInt("place")); 
                 // Dans getEtapesByTemplate(int idTemplate) :
                    etape.setPlace(rs.getInt("place")); 
                    etape.setAttentePlace(rs.getInt("attente_place")); 
                    etape.setEstFinale(rs.getBoolean("est_finale")); 
                    etape.setRoleAssocie(rs.getInt("role_associe"));
                    liste.add(etape);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste; 
    }
    
    /*
     * Génère la structure des champs et les données associées pour une étape donnée.
     */
    public List<Map<String, Object>> getChampsEtDonnees(int idWorkflow, int idTemplateWorkflow, int numEtape) {
        List<Map<String, Object>> liste = new ArrayList<>();
        
        // Utilisation d'alias en minuscules (snake_case) pour éviter les caprices du driver JDBC PostgreSQL
        String sql = "SELECT "
                + "    td.id AS id_template_donnee, "
                + "    td.nom_champ AS nom_champ, "
                + "    td.type_composant AS type_composant, "
                + "    td.a_commentaire AS a_commentaire, "
                + "    td.a_date AS a_date, "
                + "    td.est_obligatoire AS est_obligatoire, "
                + "    td.ref_contrainte AS ref_contrainte, "
                + "    d.id_donne AS id_donne, " 
                + "    d.attribut AS attribut, "
                + "    d.commentaire AS commentaire, "
                + "    d.date AS date_saisie, " 
                + "    CASE WHEN cd.id_donnee IS NOT NULL THEN true ELSE false END AS has_contrainte, "
                + "    c.id_donnee AS id_template_maitre, " 
                + "    c.contrainte AS valeur_cible, "   
                + "    cond.condition AS operateur_contrainte, " 
                + "    td_maitre.id_template_etape AS etape_maitre " 
                + "FROM public.template_donnee td "
                + "LEFT JOIN public.donnee d ON d.id_template_donnee = td.id AND d.id_workflow = ? "
                + "LEFT JOIN public.contrainte_donnee cd ON cd.id_donnee = td.id " 
                + "LEFT JOIN public.contrainte c ON c.id = cd.id_contrainte " 
                + "LEFT JOIN public.condition cond ON cond.id = c.id_condition " 
                + "LEFT JOIN public.template_donnee td_maitre ON td_maitre.id = c.id_donnee "
                + "LEFT JOIN public.template_etape te ON te.id = td.id_template_etape "
                + "WHERE te.id_template_workflow = ? "
                + "  AND te.place = ? " 
                + "ORDER BY td.ordre_affichage ASC;";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idWorkflow);
            ps.setInt(2, idTemplateWorkflow);
            ps.setInt(3, numEtape);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    
                    // Récupération via les alias en minuscules stables, puis conversion en clés CamelCase attendues par ton service
                    row.put("idTemplateDonnee", rs.getInt("id_template_donnee"));
                    row.put("nomChamp", rs.getString("nom_champ"));
                    row.put("typeComposant", rs.getString("type_composant"));
                    row.put("aCommentaire", rs.getBoolean("a_commentaire"));
                    row.put("aDate", rs.getBoolean("a_date"));
                    row.put("estObligatoire", rs.getBoolean("est_obligatoire"));
                    row.put("refContrainte", rs.getString("ref_contrainte"));

                    row.put("idDonne", rs.getInt("id_donne")); 
                    row.put("attribut", rs.getString("attribut"));
                    row.put("commentaire", rs.getString("commentaire"));
                    row.put("date", rs.getString("date_saisie"));
                    
                    row.put("hasContrainte", rs.getBoolean("has_contrainte"));
                    row.put("idTemplateMaitre", rs.getObject("id_template_maitre") != null ? rs.getInt("id_template_maitre") : null);
                    row.put("valeurCible", rs.getString("valeur_cible"));
                    row.put("operateurContrainte", rs.getString("operateur_contrainte"));
                    row.put("etapeMaitre", rs.getInt("etape_maitre"));
                    
                    liste.add(row);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public List<templateWorkflow> getTemplatesActifs() {
        List<templateWorkflow> list = new ArrayList<>();
        String sql = "SELECT id, nom, version, description, date_creation, id_createur, est_actif " +
                     "FROM template_workflow WHERE est_actif = true";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                templateWorkflow tpl = new templateWorkflow();
                tpl.setId(rs.getInt("id"));
                tpl.setTitre(rs.getString("nom")); // Reste aligné sur le setter de ton model actuel
                tpl.setVersion(rs.getInt("version"));
                tpl.setCommentaire(rs.getString("description")); // Reste aligné sur ton model actuel
                tpl.setDateCreation(rs.getTimestamp("date_creation"));
                tpl.setCreateur(rs.getInt("id_createur"));
                tpl.setEstActif(rs.getBoolean("est_actif"));
                list.add(tpl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    /* pour un workflow terminé et le modifier en base de donnee*/
    public static void finaliserWorkflow(int idWorkflow) {
        String sql = "UPDATE workflow SET date_finalisation = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            ps.setInt(2, idWorkflow);
            ps.executeUpdate();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 1. Récupère les métadonnées complètes d'un champ cible (template_donnee)
     */
    public Map<String, Object> getTemplateDonneeById(int idDonneeCible) throws Exception {
        Map<String, Object> row = null;
        String sql = "SELECT id, id_template_etape, nom_champ, type_composant, ordre_affichage, ref_contrainte " +
                     "FROM template_donnee WHERE id = ?";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idDonneeCible);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("id_template_etape", rs.getInt("id_template_etape"));
                    row.put("nomChamp", rs.getString("nom_champ")); // utilisé comme ${donneeActive.nomChamp}
                    row.put("typeComposant", rs.getString("type_composant"));
                    row.put("ordreAffichage", rs.getInt("ordre_affichage")); // utilisé comme ${donneeActive.ordreAffichage}
                    row.put("ref_contrainte", rs.getString("ref_contrainte"));
                }
            }
        }
        return row;
    }

    /**
     * 2. Récupère les informations d'une étape spécifique (template_etape)
     */
    public Map<String, Object> getEtapeById(int idEtape) throws Exception {
        Map<String, Object> row = null;
        String sql = "SELECT id, nom_etape, place, id_template_workflow FROM template_etape WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idEtape);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("nomEtape", rs.getString("nom_etape"));
                    row.put("place", rs.getInt("place")); // utilisé comme ${etapeActive.place}
                    row.put("idTemplateWorkflow", rs.getInt("id_template_workflow"));
                }
            }
        }
        return row;
    }
    
}