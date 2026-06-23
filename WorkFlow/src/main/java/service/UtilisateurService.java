package service;

import dao.UtilisateurDAO;
import model.Role;
import model.Utilisateur;

import java.util.ArrayList;
import java.util.List;

public class UtilisateurService {
    private UtilisateurDAO utilisateurDAO = new UtilisateurDAO();

    public boolean changerRoleUtilisateur(int idUtilisateur, int idRole) {
        try {
            return utilisateurDAO.modifierRole(idUtilisateur, idRole);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Role> recupererTousLesRoles() {
        try {
            return utilisateurDAO.getListeRoles();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public List<Utilisateur> recupererTousLesUtilisateurs() {
        try {
            return utilisateurDAO.getTousLesUtilisateurs(); // Fait un SELECT id, login, nom, role FROM utilisateur
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}