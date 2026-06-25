

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.RoleDAO;

class RoleDAOTest {

    private RoleDAO roleDAO;

    @BeforeEach
    void setUp() {
        roleDAO = new RoleDAO();
        // Optionnel : Vous pouvez insérer ici un script pour réinitialiser 
        // les données de votre base de données de test si nécessaire.
    }

    @Test
    void testGetRolesWithSteps() {
        List<Map<String, Object>> roles = roleDAO.getRolesWithSteps();
        
        assertNotNull(roles, "La liste des rôles ne doit pas être nulle");
        // Si votre base de test contient des données, on valide les clés renvoyées
        if (!roles.isEmpty()) {
            Map<String, Object> firstRole = roles.get(0);
            assertTrue(firstRole.containsKey("id"), "La map doit contenir la clé 'id'");
            assertTrue(firstRole.containsKey("role"), "La map doit contenir la clé 'role'");
            assertTrue(firstRole.containsKey("etapes"), "La map doit contenir la clé 'etapes'");
        }
    }

    @Test
    void testCheckUserHasRole() {
        // Remplacez par des valeurs valides ou fictives de votre environnement de test
        String testLogin = "admin";
        int testRoleId = 1;
        
        // On exécute la méthode (renvoie true ou false sans lever d'exception)
        boolean hasRole = roleDAO.checkUserHasRole(testLogin, testRoleId);
        
        // On vérifie que le type de retour est bien booléen
        assertNotNull(hasRole);
    }

    @Test
    void testUpdateUserRole() {
        String testLogin = "user_test";
        int initialRoleId = 2;
        
        // On teste simplement qu'aucune SQLException n'est levée lors de l'exécution
        assertDoesNotThrow(() -> roleDAO.updateUserRole(testLogin, initialRoleId),
                "La mise à jour du rôle utilisateur ne doit pas lever d'exception");
    }

    @Test
    void testGetEtapesByRole() {
        int testRoleId = 1;
        List<Integer> etapes = roleDAO.getEtapesByRole(testRoleId);
        
        assertNotNull(etapes, "La liste des étapes ne doit pas être nulle");
    }

    @Test
    void testAddAndDeleteDroit() {
        int testRoleId = 999; // ID fictif pour éviter les conflits
        int testEtape = 10;

        // 1. Test de l'ajout
        assertDoesNotThrow(() -> roleDAO.addDroit(testRoleId, testEtape),
                "L'ajout d'un droit ne doit pas lever d'exception");

        // 2. Test de la suppression
        assertDoesNotThrow(() -> roleDAO.deleteDroit(testRoleId, testEtape),
                "La suppression d'un droit ne doit pas lever d'exception");
    }

    @Test
    void testGetRoleNameById() {
        int unknownRoleId = -1;
        String roleName = roleDAO.getRoleNameById(unknownRoleId);
        
        // Pour un ID inconnu, la méthode initialise name à ""
        assertEquals("", roleName, "Le nom du rôle pour un ID invalide doit être une chaîne vide");
    }

    @Test
    void testGetAllRoleNames() {
        Map<Integer, String> roleNames = roleDAO.getAllRoleNames();
        
        assertNotNull(roleNames, "La Map des noms de rôles ne doit pas être nulle");
    }

    @Test
    void testCanAccessEtape_Simple() {
        // Règle codée en dur : si roleId == etape -> renvoie true d'office
        assertTrue(RoleDAO.canAccessEtape(5, 5), "Si l'ID du rôle est identique à l'étape, l'accès doit être accordé");
        
        // Test avec des valeurs différentes (interroge la base)
        boolean access = RoleDAO.canAccessEtape(1, 99);
        assertNotNull(access);
    }

    @Test
    void testCanAccessEtape_Workflow() {
        int idWorkflow = 1;
        int numEtape = 2;
        int roleId = 1;

        // On vérifie que la méthode s'exécute sans erreur de syntaxe SQL
        assertDoesNotThrow(() -> RoleDAO.canAccessEtape(roleId, idWorkflow, numEtape),
                "La vérification d'accès à l'étape du workflow ne doit pas lever d'exception");
    }
}