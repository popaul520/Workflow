package service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import dao.ContrainteDonneeDAO;
import dao.TemplateDAO; 
import dao.DonneeDAO;

public class ContrainteDonneeService {

    private final ContrainteDonneeDAO dao = new ContrainteDonneeDAO();
    private final TemplateDAO templateDao = new TemplateDAO();
    private final DonneeDAO donneeDao = new DonneeDAO(); // Ton DAO pour getValeursContraintes()

    public Map<String, Object> getPageContext(int idDonneeCible) throws Exception {
        Map<String, Object> context = new HashMap<>();

        // Métadonnées du champ sélectionné
        Map<String, Object> donneeActive = templateDao.getTemplateDonneeById(idDonneeCible);
        context.put("donneeActive", donneeActive);

        // Métadonnées de l'étape associée à ce champ
        int idEtape = (Integer) donneeActive.get("id_template_etape");
        context.put("etapeActive", templateDao.getEtapeById(idEtape)); 

        // Alimentation des listes du formulaire
        context.put("etapesTemplate", dao.getEtapesByDonnee(idDonneeCible));
        context.put("toutesDonnees", dao.getToutesDonneesDuWorkflow(idDonneeCible));
        context.put("conditionsReferentiel", dao.getConditionsReferentiel());
        context.put("contraintesAssociees", dao.getContraintesByDonnee(idDonneeCible));

        // Chargement automatique des dictionnaires pour les ref_contrainte
        Map<String, List<String>> mapCatalogues = new HashMap<>();
        List<Map<String, Object>> donnees = (List<Map<String, Object>>) context.get("toutesDonnees");
        for (Map<String, Object> d : donnees) {
            String ref = (String) d.get("ref_contrainte");
            if (ref != null && !ref.trim().isEmpty() && !mapCatalogues.containsKey(ref)) {
                mapCatalogues.put(ref, donneeDao.getValeursContraintes(ref));
            }
        }
        context.put("mapCatalogues", mapCatalogues);

        return context;
    }

    public void ajouterContrainte(int idDonneeCible, int idDonneeSource, int idCondition, String valeur, int idEtapeCible, int idEtapeSource, int typeContrainte) throws Exception {
    	//int idDonneeCible, int idDonneeSource, int idCondition, String valeurAttendue, int idEtapeCible, int idEtapeSource, int typeContrainte
        dao.ajouterRegleContrainte(idDonneeCible, idDonneeSource, idCondition, valeur, idEtapeCible, idEtapeSource, typeContrainte);
    }

    public void modifierContrainte(int idContrainte, int idDonneeSource, int idCondition, String valeur) throws Exception {
        dao.modifierRegleContrainte(idContrainte, idDonneeSource, idCondition, valeur);
    }

    public void supprimerContrainte(int idDonneeCible, int idContrainte) throws Exception {
        dao.supprimerRegleContrainte(idDonneeCible, idContrainte);
    }
}