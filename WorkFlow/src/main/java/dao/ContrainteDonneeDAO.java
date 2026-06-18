package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dao.DBConnection;

public class ContrainteDonneeDAO {

    /**
     * 1. Récupère toutes les étapes d'un workflow spécifique basé sur l'étape de la donnée actuelle
     */
    public List<Map<String, Object>> getEtapesByDonnee(int idDonneeCible) throws Exception {
        List<Map<String, Object>> liste = new ArrayList<>();
        String sql = "SELECT te.id, te.nom_etape, te.place " +
                     "FROM template_etape te " +
                     "WHERE te.id_template_workflow = (" +
                     "    SELECT id_template_workflow FROM template_etape " +
                     "    WHERE id = (SELECT id_template_etape FROM template_donnee WHERE id = ?)" +
                     ") ORDER BY te.place ASC";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDonneeCible);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("nomEtape", rs.getString("nom_etape"));
                    row.put("place", rs.getInt("place"));
                    liste.add(row);
                }
            }
        }
        return liste;
    }

    /**
     * 2. Récupère l'intégralité des champs du même Template de Workflow pour l'arbre JavaScript
     */
    public List<Map<String, Object>> getToutesDonneesDuWorkflow(int idDonneeCible) throws Exception {
        List<Map<String, Object>> liste = new ArrayList<>();
        String sql = "SELECT id, id_template_etape, nom_champ, type_composant, ordre_affichage, ref_contrainte " +
                     "FROM template_donnee " +
                     "WHERE id_template_etape IN (" +
                     "    SELECT id FROM template_etape WHERE id_template_workflow = (" +
                     "        SELECT id_template_workflow FROM template_etape WHERE id = (" +
                     "            SELECT id_template_etape FROM template_donnee WHERE id = ?" +
                     "        )" +
                     "    )" +
                     ")";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, idDonneeCible);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("id", rs.getInt("id"));
                    row.put("id_template_etape", rs.getInt("id_template_etape"));
                    row.put("nom_champ", rs.getString("nom_champ"));
                    row.put("type_composant", rs.getString("type_composant"));
                    row.put("ordre_affichage", rs.getInt("ordre_affichage"));
                    row.put("ref_contrainte", rs.getString("ref_contrainte"));
                    liste.add(row);
                }
            }
        }
        return liste;
    }

    /**
     * 3. Récupère le référentiel complet de la table condition
     */
    public List<Map<String, Object>> getConditionsReferentiel() throws Exception {
        List<Map<String, Object>> liste = new ArrayList<>();
        String sql = "SELECT id, condition FROM condition ORDER BY id ASC";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("id", rs.getInt("id"));
                row.put("condition", rs.getString("condition"));
                liste.add(row);
            }
        }
        return liste;
    }

    /**
     * 4. Crée une contrainte et l'associe à la donnée cible
     */
public void ajouterRegleContrainte(int idDonneeCible, int idDonneeSource, int idCondition, String valeurAttendue, int idEtapeCible, int idEtapeSource, int typeContrainte) throws Exception {
    Connection conn = null;
    PreparedStatement psContrainte = null;
    PreparedStatement psJointure = null;
    ResultSet rs = null;
    
    try {
        conn = DBConnection.getConnection();
        conn.setAutoCommit(false); // Mode transactionnel

        // A. Insertion dans 'contrainte' (L'id va maintenant se générer automatiquement grâce au DEFAULT)
        String sqlContrainte = "INSERT INTO contrainte (contrainte, id_condition, id_donnee, texte) VALUES (?, ?, ?, false) RETURNING id";
        psContrainte = conn.prepareStatement(sqlContrainte);
        psContrainte.setString(1, valeurAttendue);
        psContrainte.setInt(2, idCondition);
        psContrainte.setInt(3, idDonneeSource);
        
        rs = psContrainte.executeQuery();
        int idContrainteGenere = 0;
        if (rs.next()) {
            idContrainteGenere = rs.getInt("id");
        }

        // B. Détermination de etape_contrainte : true si la condition porte sur une étape antérieure
        boolean isEtapeContrainte = (idEtapeSource != idEtapeCible);

        // C. Liaison dans 'contrainte_donnee' avec etape_contrainte et type_contrainte
        String sqlJointure = "INSERT INTO contrainte_donnee (id_donnee, id_contrainte, etape_contrainte, type_contrainte) VALUES (?, ?, ?, ?)";
        psJointure = conn.prepareStatement(sqlJointure);
        psJointure.setInt(1, idDonneeCible);
        psJointure.setInt(2, idContrainteGenere);
        psJointure.setBoolean(3, isEtapeContrainte);
        psJointure.setInt(4, typeContrainte); // Même int pour identifier le type
        
        psJointure.executeUpdate();

        conn.commit();
    } catch (Exception e) {
        if (conn != null) conn.rollback();
        throw e;
    } finally {
        if (rs != null) rs.close();
        if (psContrainte != null) psContrainte.close();
        if (psJointure != null) psJointure.close();
        if (conn != null) conn.close();
    }
}

    /**
     * 5. Met à jour la table 'contrainte' lors d'une modification
     */
    public void modifierRegleContrainte(int idContrainte, int idDonneeSource, int idCondition, String valeurAttendue) throws Exception {
        String sql = "UPDATE contrainte SET contrainte = ?, id_condition = ?, id_donnee = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, valeurAttendue);
            ps.setInt(2, idCondition); 
            ps.setInt(3, idDonneeSource);
            ps.setInt(4, idContrainte);
            ps.executeUpdate();
        }
    }

    /**
     * 6. Suppression en cascade physique d'une règle
     */
    public void supprimerRegleContrainte(int idDonneeCible, int idContrainte) throws Exception {
        Connection conn = null;
        PreparedStatement psJointure = null;
        PreparedStatement psContrainte = null;
        try {
            conn = DBConnection.getConnection();
            conn.setAutoCommit(false);

            String sql1 = "DELETE FROM contrainte_donnee WHERE id_donnee = ? AND id_contrainte = ?";
            psJointure = conn.prepareStatement(sql1);
            psJointure.setInt(1, idDonneeCible);
            psJointure.setInt(2, idContrainte);
            psJointure.executeUpdate();

            String sql2 = "DELETE FROM contrainte WHERE id = ?";
            psContrainte = conn.prepareStatement(sql2);
            psContrainte.setInt(1, idContrainte);
            psContrainte.executeUpdate();

            conn.commit();
        } catch (Exception e) {
            if (conn != null) conn.rollback();
            throw e;
        } finally {
            if (psJointure != null) psJointure.close();
            if (psContrainte != null) psContrainte.close();
            if (conn != null) conn.close();
        }
    }
    
    /**
     * 3. getContraintesByDonnee : Note importante
     * Cette méthode extrait les contraintes appliquées à ton champ.
     * Pour respecter la cohérence de ton architecture, elle se place idéalement 
     * dans 'ContrainteDonneeDAO.java' (comme écrit dans la réponse précédente), 
     * mais si tu as besoin de la centraliser ici dans ton TemplateDao, voici son code exact :
     */
    public java.util.List<Map<String, Object>> getContraintesByDonnee(int idDonneeCible) throws Exception {
        java.util.List<Map<String, Object>> liste = new java.util.ArrayList<>();
        String sql = "SELECT " +
                     "  cd.id_contrainte, " +
                     "  cd.etape_contrainte, " +
                     "  c.id_donnee AS id_donnee_source, " +
                     "  c.id_condition, " +
                     "  c.contrainte AS valeur_attendue, " + 
                     "  td_source.nom_champ AS nom_champ_source, " +
                     "  te_source.id AS id_etape_source, " +
                     "  te_source.place AS place_etape_source, " +
                     "  cond.condition AS symbole_condition " +
                     "FROM contrainte_donnee cd " +
                     "JOIN contrainte c ON cd.id_contrainte = c.id " +
                     "JOIN template_donnee td_source ON c.id_donnee = td_source.id " +
                     "JOIN template_etape te_source ON td_source.id_template_etape = te_source.id " +
                     "JOIN condition cond ON c.id_condition = cond.id " +
                     "WHERE cd.id_donnee = ?";
                     
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, idDonneeCible);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Map<String, Object> row = new HashMap<>();
                    row.put("idContrainte", rs.getInt("id_contrainte"));
                    row.put("idDonneeCible", idDonneeCible);
                    row.put("idDonneeSource", rs.getInt("id_donnee_source"));
                    row.put("idCondition", rs.getInt("id_condition"));
                    row.put("valeurAttendue", rs.getString("valeur_attendue"));
                    row.put("nomChampSource", rs.getString("nom_champ_source"));
                    row.put("idEtapeSource", rs.getInt("id_etape_source"));
                    row.put("placeEtapeSource", rs.getInt("place_etape_source"));
                    row.put("symboleCondition", rs.getString("symbole_condition"));
                    row.put("etapeContrainte", rs.getBoolean("etape_contrainte"));
                    liste.add(row);
                }
            }
        }
        return liste;
    }
    
}