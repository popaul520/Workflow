
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dao.TemplateDAO;
import model.templateWorkflow;
import model.template_etape;

class TemplateDAOTest {

    private TemplateDAO templateDAO;

    @BeforeEach
    void setUp() {
        templateDAO = new TemplateDAO();
    }

    @Test
    void testCreateAndGetTemplate() {
        String testNom = "Template Test JUnit";
        int testVersion = 1;
        String testDesc = "Description du template de test";
        int idCreateur = 1;
        boolean estActif = true;

        // 1. Validation de la création (Vérification qu'aucune exception SQL n'est levée)
        assertDoesNotThrow(() -> {
            templateDAO.createTemplate(testNom, testVersion, testDesc, idCreateur, estActif);
        }, "La création d'un template ne doit pas lever d'exception");

        // 2. Vérification de la récupération du dernier ID généré
        int lastId = templateDAO.getLastGeneratedId();
        assertTrue(lastId >= 0, "Le dernier ID généré doit être positif ou nul");

        // 3. Test de récupération par ID si un ID valide a pu être trouvé
        if (lastId > 0) {
            templateWorkflow template = templateDAO.getTemplateById(lastId);
            assertNotNull(template, "Le template créé devrait être récupérable par son ID");
            assertEquals(testVersion, template.getVersion());
            assertEquals(idCreateur, template.getCreateur());
        }
    }

    @Test
    void testUpdateTemplate() {
        int lastId = templateDAO.getLastGeneratedId();
        
        if (lastId > 0) {
            assertDoesNotThrow(() -> {
                templateDAO.updateTemplate(lastId, "Template Modifie", 2, "Nouvelle description", false);
            }, "La mise à jour du template ne doit pas lever d'exception");
            
            templateWorkflow modifie = templateDAO.getTemplateById(lastId);
            assertNotNull(modifie);
            assertEquals("Template Modifie", modifie.getTitre());
            assertFalse(modifie.isEstActif());
        }
    }

    @Test
    void testGetAllTemplates() {
        List<templateWorkflow> liste = templateDAO.getAllTemplates();
        assertNotNull(liste, "La liste de tous les templates ne doit pas être nulle");
    }

    @Test
    void testGetTemplateByTitre() {
        // Test avec une valeur qui n'existe pas en BDR (Doit renvoyer null proprement)
        templateWorkflow inconnu = templateDAO.getTemplateByTitre("Titre Totalement Improbable Inexistant");
        assertNull(inconnu, "Rechercher un titre inexistant doit renvoyer null");
    }

    @Test
    void testGetEtapeConfig() {
        // Extraction avec des identifiants fictifs pour tester la robustesse structurelle de la requête SQL
        template_etape config = templateDAO.getEtapeConfig(-1, -1);
        assertNull(config, "Une configuration d'étape invalide doit renvoyer null sans planter");
    }

    @Test
    void testGetEtapesByTemplate() {
        List<template_etape> etapes = templateDAO.getEtapesByTemplate(-1);
        assertNotNull(etapes, "La liste des étapes d'un template inconnu doit être vide mais pas nulle");
        assertTrue(etapes.isEmpty());
    }

    @Test
    void testGetChampsEtDonnees() {
        // Validation que la requête SQL imbriquée complexe s'exécute sans erreur de syntaxe
        List<Map<String, Object>> result = templateDAO.getChampsEtDonnees(-1, -1, 1);
        assertNotNull(result, "Le retour de getChampsEtDonnees ne doit pas être nul");
    }

    @Test
    void testGetTemplatesActifs() {
        List<templateWorkflow> actifs = templateDAO.getTemplatesActifs();
        assertNotNull(actifs, "La liste des templates actifs ne doit pas être nulle");
        for (templateWorkflow t : actifs) {
            assertTrue(t.isEstActif(), "Chaque template de la liste doit être marqué comme actif");
        }
    }

    @Test
    void testFinaliserWorkflow() {
        // Appel d'une méthode statique d'écriture : vérification de la non-régression SQL
        assertDoesNotThrow(() -> TemplateDAO.finaliserWorkflow(-1),
                "La finalisation d'un workflow inexistant ne doit pas lever d'erreur bloquante");
    }

    @Test
    void testGetTemplateDonneeById() {
        // La méthode jette une Exception explicite (throws Exception)
        assertDoesNotThrow(() -> {
            Map<String, Object> champ = templateDAO.getTemplateDonneeById(-1);
            if (champ != null) {
                assertTrue(champ.containsKey("id"));
                assertTrue(champ.containsKey("nomChamp"));
            }
        });
    }

    @Test
    void testGetEtapeById() {
        assertDoesNotThrow(() -> {
            Map<String, Object> etape = templateDAO.getEtapeById(-1);
            if (etape != null) {
                assertTrue(etape.containsKey("id"));
                assertTrue(etape.containsKey("nomEtape"));
            }
        });
    }

    @Test
    void testDeleteTemplate() {
        // On récupère le dernier ID généré pour le supprimer
        int idASupprimer = templateDAO.getLastGeneratedId();
        
        if (idASupprimer > 0) {
            assertDoesNotThrow(() -> templateDAO.deleteTemplate(idASupprimer),
                    "La suppression d'un template ne doit pas lever d'exception");
            
            templateWorkflow supprime = templateDAO.getTemplateById(idASupprimer);
            assertNull(supprime, "Le template supprimé ne devrait plus exister en base de données");
        }
    }
}