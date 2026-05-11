package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.Utilisateur;

public class UtilisateurDAO {

    public Utilisateur login(String login, String mdp) {

        try {
            Connection con = DBConnection.getConnection();

            String sql = "SELECT * FROM utilisateur WHERE login=? AND mdp=?";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, login);
            ps.setString(2, mdp);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Utilisateur u = new Utilisateur();
                u.setId(rs.getInt("id"));
                u.setLogin(rs.getString("login"));
                u.setNom(rs.getString("nom"));
                u.setPrenom(rs.getString("prenom"));
                u.setMail(rs.getString("mail"));
                
                // --- TRÈS IMPORTANT : Récupérer le rôle ---
                // C'est cette ligne qui permet à user.canAccessStep() de fonctionner
                u.setRole(rs.getInt("role"));
                return u;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
    
    public boolean register(Utilisateur u) {

        try {
            Connection con = DBConnection.getConnection();

            String sql = "INSERT INTO utilisateur(login, mdp, nom, prenom, mail) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setString(1, u.getLogin());
            ps.setString(2, u.getMdp());
            ps.setString(3, u.getNom());
            ps.setString(4, u.getPrenom());
            ps.setString(5, u.getMail());

            ps.executeUpdate();
            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    


    
    public Utilisateur findByid(int id) {

        String sql = "SELECT * FROM utilisateur WHERE id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Utilisateur u = new Utilisateur();
                    u.setId(rs.getInt("id"));
                    u.setLogin(rs.getString("login"));
                    u.setNom(rs.getString("nom"));
                    u.setPrenom(rs.getString("prenom"));
                    u.setMail(rs.getString("mail"));
                    u.setRole(rs.getInt("role"));
                    return u;
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur findByLogin : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
    
    
    public Utilisateur findByLogin(String login) {

        String sql = "SELECT * FROM utilisateur WHERE login = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, login);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Utilisateur u = new Utilisateur();
                    u.setId(rs.getInt("id"));
                    u.setLogin(rs.getString("login"));
                    u.setNom(rs.getString("nom"));
                    u.setPrenom(rs.getString("prenom"));
                    u.setMail(rs.getString("mail"));
                    u.setRole(rs.getInt("role"));
                    return u;
                }
            }

        } catch (Exception e) {
            System.err.println("Erreur findByLogin : " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Insertion d'un utilisateur venant de l'AD
     */
    public boolean insert(Utilisateur u) {

        String sql = """
            INSERT INTO utilisateur (login, nom, prenom, mail, role)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getLogin());
            ps.setString(2, u.getNom());
            ps.setString(3, u.getPrenom());
            ps.setString(4, u.getMail());
            ps.setInt(5, u.getRole());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("Erreur insert utilisateur : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mise à jour des infos AD (utile si rôle ou mail changent)
     */
    public boolean updateFromAD(Utilisateur u) {

        String sql = """
            UPDATE utilisateur
            SET nom = ?, prenom = ?, mail = ?, role = ?
            WHERE login = ?
        """;

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getMail());
            ps.setInt(4, u.getRole());
            ps.setString(5, u.getLogin());

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            System.err.println("Erreur updateFromAD : " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    public List<Integer> getEtapesByUserId(int id_user) {
        List<Integer> etapes = new ArrayList<>();
        String sql = "SELECT d.etape " +
                     "FROM utilisateur u " +
                     "JOIN droit d ON u.role = d.role " +
                     "WHERE u.id = ?";

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id_user);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    etapes.add(rs.getInt("etape"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur getEtapesByUserId : " + e.getMessage());
            e.printStackTrace();
        }
        return etapes;
    }

}