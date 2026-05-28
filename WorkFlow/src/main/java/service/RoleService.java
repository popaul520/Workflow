package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import dao.RoleDAO;
import model.Utilisateur;

public class RoleService {

    private final RoleDAO roleDao = new RoleDAO();

    /**
     * Récupère la liste globale de tous les rôles avec leurs étapes associées.
     */
    public List<Map<String, Object>> getAllRolesWithSteps() throws Exception {
        return roleDao.getRolesWithSteps();
    }

    /**
     * Récupère le nom d'un rôle par son ID.
     */
    public String getRoleName(int roleId) throws Exception {
        return roleDao.getRoleNameById(roleId);
    }

    /**
     * Récupère les étapes déjà associées à un rôle.
     */
    public List<Integer> getStepsForRole(int roleId) throws Exception {
        return roleDao.getEtapesByRole(roleId);
    }

    /**
     * Récupère la carte de tous les rôles en excluant le rôle en cours d'édition.
     */
    public Map<Integer, String> getAllOtherRolesMap(int currentRoleId) throws Exception {
        Map<Integer, String> allRolesMap = roleDao.getAllRoleNames();
        allRolesMap.remove(currentRoleId);
        return allRolesMap;
    }

    /**
     * Calcule de manière dynamique les étapes disponibles pour l'attribution.
     */
    public List<Integer> calculateAvailableSteps(int currentRoleId, List<Integer> existingRoleSteps, Utilisateur user) {
        List<Integer> availableSteps = new ArrayList<>();
        
        // Logique de filtrage d'origine déportée
        for (int i = 1; i <= 12; i++) { 
            if (i == currentRoleId) {
                continue; 
            }
            if (!existingRoleSteps.contains(i) || !(user.getRole() == i)) {
                availableSteps.add(i);
            }
        }
        return availableSteps;
    }

    /**
     * Ajoute ou supprime un droit (association rôle / étape).
     */
    public void manageDroit(String dbAction, int roleId, int etape) throws Exception {
        if ("add".equals(dbAction)) {
            roleDao.addDroit(roleId, etape);
        } else if ("delete".equals(dbAction)) {
            roleDao.deleteDroit(roleId, etape);
        }
    }
}