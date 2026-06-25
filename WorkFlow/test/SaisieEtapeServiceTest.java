import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import model.Utilisateur;
import service.SaisieEtapeService;
/*
class SaisieEtapeServiceTest {

    private SaisieEtapeService etapeService;

    @BeforeEach
    void setUp() {
        etapeService = new SaisieEtapeService();
    }

    // =========================================================================
    // 1. CAS PARTICULIERS : verifierContrainteServeur
    // =========================================================================

    @Test
    void testVerifierContrainteServeur_CasLimitesEtParticuliers() {
        // Cas 1 : Valeurs nulles ou vides avec contrainte active (Doit masquer / false)
        assertFalse(SaisieEtapeService.verifierContrainteServeur(true, 2, 1, null, "==", "Manuel"));
        assertFalse(SaisieEtapeService.verifierContrainteServeur(true, 2, 1, "   ", "==", "Manuel"));
        assertFalse(SaisieEtapeService.verifierContrainteServeur(true, 2, 1, "Manuel", "==", null));
        assertFalse(SaisieEtapeService.verifierContrainteServeur(true, 2, 1, "Manuel", null, "Manuel"));

        // Cas 2 : Tolérance des espaces aux extrémités (Trim) via equalsIgnoreCase
        assertTrue(SaisieEtapeService.verifierContrainteServeur(true, 2, 1, " Manuel ", "==", "Manuel"));
        assertTrue(SaisieEtapeService.verifierContrainteServeur(true, 2, 1, "Bagueuse", "==", " bagueuse \n"));

        // Cas 3 : Opérateur de différence '!=' avec chaîne vide
        assertFalse(SaisieEtapeService.verifierContrainteServeur(true, 2, 1, "", "!=", "Manuel"), 
                "Une valeur vide ne doit pas valider l'inégalité par sécurité");

        // Cas 4 : Opérateurs numériques avec virgules à la place des points
        assertTrue(SaisieEtapeService.verifierContrainteServeur(true, 2, 1, "150,5", ">", "100,2"));
        assertTrue(SaisieEtapeService.verifierContrainteServeur(true, 2, 1, "42.0", "<=", "42,0"));

        // Cas 5 : Crash de conversion numérique (NumberFormatException -> Doit masquer / false)
        assertFalse(SaisieEtapeService.verifierContrainteServeur(true, 2, 1, "Cent-Cinquante", ">", "100"));
        assertFalse(SaisieEtapeService.verifierContrainteServeur(true, 2, 1, "150", ">", "Inconnu"));
    }

    // =========================================================================
    // 2. CAS PARTICULIERS : filtrerDonneesEtape
    // =========================================================================

    @Test
    void testFiltrerDonneesEtape_CasParticuliers() {
        int testIdWorkflow = 1;

        // Cas 1 : Liste de données d'entrée nulle ou vide
        assertNotNull(etapeService.filtrerDonneesEtape(null, testIdWorkflow));
        assertTrue(etapeService.filtrerDonneesEtape(new ArrayList<>(), testIdWorkflow).isEmpty());

        // Cas 2 : Données corrompues ou structures inattendues (pas une Map)
        List<Object> donneesInvalides = new ArrayList<>();
        donneesInvalides.add("Une simple chaîne de texte au lieu d'une Map");
        donneesInvalides.add(12345);
        
        List<Object> resInvalides = etapeService.filtrerDonneesEtape(donneesInvalides, testIdWorkflow);
        assertTrue(resInvalides.isEmpty(), "Les objets qui ne sont pas des structures Map doivent être ignorés");

        // Cas 3 : Vérification de la compatibilité des différentes clés d'ID ("id", "id_template_donnee", "idDonnee")
        List<Map<String, Object>> donneesVariées = new ArrayList<>();
        
        Map<String, Object> map1 = new HashMap<>();
        map1.put("id", 101);
        donneesVariées.add(map1);

        Map<String, Object> map2 = new HashMap<>();
        map2.put("id_template_donnee", 102);
        donneesVariées.add(map2);

        Map<String, Object> map3 = new HashMap<>();
        map3.put("idDonnee", 103);
        donneesVariées.add(map3);

        // Si aucune contrainte n'est stockée en base pour ces IDs, ils doivent tous traverser le filtre
        List<Object> resVariés = etapeService.filtrerDonneesEtape(donneesVariées, testIdWorkflow);
        assertEquals(3, resVariés.size(), "Le filtre doit reconnaître les 3 variantes de clés d'identifiants");
    }

    // =========================================================================
    // 3. TESTS GENERAUX (CONTEXTE / ACCES BASE)
    // =========================================================================

    @Test
    void testGetEtapeSaisieContext2() {
        int testIdWf = 1;
        int testNumEtape = 1;
        Utilisateur testUser = new Utilisateur();
        testUser.setRole(11); // Profil Admin

        assertDoesNotThrow(() -> {
            Map<String, Object> context = SaisieEtapeService.getEtapeSaisieContext2(testIdWf, testNumEtape, testUser);
            if (context != null) {
                assertTrue(context.containsKey("workflow"));
                assertTrue(context.containsKey("donneesEtape"));
                assertTrue(context.containsKey("canEdit"));
            }
        });
    }

    @Test
    void testGetValeursSaisiesPourWorkflow() {
        int testIdWorkflow = 1;
        Map<Integer, String> valeurs = etapeService.getValeursSaisiesPourWorkflow(testIdWorkflow);
        assertNotNull(valeurs);
    }

    @Test
    void testGetContraintesParDonnee() {
        int testIdDonnee = 1;
        List<model.Contrainte> contraintes = etapeService.getContraintesParDonnee(testIdDonnee);
        assertNotNull(contraintes);
    }

    @Test
    void testGetMapTousCataloguesContraintes() {
        Map<String, List<String>> catalogues = etapeService.getMapTousCataloguesContraintes();
        assertNotNull(catalogues);
    }

    @Test
    void testCloturerWorkflowStructurel_IdInexistant() {
        int unknownIdWf = -999; 
        // Doit s'exécuter sans lever d'exception même si 0 ligne n'est modifiée en base
        assertDoesNotThrow(() -> etapeService.cloturerWorkflowStructurel(unknownIdWf, "Faisable", "Test", new Utilisateur()));
    }
}*/