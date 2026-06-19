package service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import dao.DBConnection;
import dao.DonneeDAO;
import dao.TemplateDAO;
import dao.ValidationDAO;
import dao.WorkflowDAO;
import model.Utilisateur;
import model.Workflow;
import model.template_etape;

public class SaisieEtapeService {

    private final WorkflowDAO wfDao = new WorkflowDAO();
    private final TemplateDAO templateDao = new TemplateDAO();
    private final ValidationDAO validationDao = new ValidationDAO();
    private final DonneeDAO donneeDao = new DonneeDAO();

    /**
     * Prépare toutes les données requises pour l'affichage de l'étape
     */
    /**
     * Prépare toutes les données requises pour l'affichage de l'étape
     */
    /**
     * Prépare toutes les données requises pour l'affichage de l'étape
     */
    public Map<String, Object> getEtapeSaisieContext(int idWf, int numEtape, Utilisateur user) throws Exception {
        Map<String, Object> context = new HashMap<>();
        // 1. Récupération du Workflow
        Workflow wf = wfDao.getById(idWf);
        if (wf == null) return null;

        boolean isAdmin = (user != null && user.getRole() == 11);
        boolean hasAccess = false;

        // 2. Configuration de l'étape courante 
        template_etape configEtape = templateDao.getEtapeConfig(wf.getIdTemplateWorkflow(), numEtape);
        if (user != null) {
            if (isAdmin) {
                hasAccess = true;
            } else if (configEtape != null) {
                hasAccess = (user.getRole() == configEtape.getRoleAssocie());
            }
        }
        // 3. Récupération des étapes validées UNIQUEMENT pour ce dossier (idWf)
        List<Integer> etapesValidees = new ArrayList<>();
        try {
            etapesValidees = validationDao.getEtapesValidees(idWf); // Assurez-vous que ce DAO filtre par id_workflow !
        } catch (Exception e) {
            e.printStackTrace();
        }

        boolean isClosed = (wf.getDateFinalisation() != null);
        
        // 4. Logique de déblocage basée sur la table template_etape et le champ attente_place
        boolean conditionAttenteRespectee = false;
        
        if (configEtape != null) {
            Integer attente = configEtape.getAttentePlace(); // Doit être un objet Integer dans votre modèle pour gérer le NULL

            if (configEtape.getPlace() == 1) {
                // L'étape 1 n'a pas de contrainte amont par défaut
                conditionAttenteRespectee = true;
            } else if (attente != null && attente > 0) {
                // Si attente_place est défini, on vérifie qu'il existe dans l'historique de validation de ce workflow
                conditionAttenteRespectee = etapesValidees.contains(attente);
            } else {
                // Sans prérequis (NULL ou 0), l'étape est ouverte à la saisie
                conditionAttenteRespectee = true;
            }
        }

        // Détermination des droits d'édition
        boolean canEdit = (hasAccess && !isClosed && conditionAttenteRespectee);

        // 5. Chargement et filtrage des champs dynamiques (Sécurité d'accès)
        List<Map<String, Object>> donneesEtape = new ArrayList<>();
        
        if (conditionAttenteRespectee || isAdmin) {
            // L'utilisateur a le droit de voir/remplir les données
            donneesEtape = templateDao.getChampsEtDonnees(idWf, wf.getIdTemplateWorkflow(), numEtape);

            // Application des contraintes d'affichage conditionnelles (champs maîtres / esclaves)
            Iterator<Map<String, Object>> iterator = donneesEtape.iterator();
            while (iterator.hasNext()) {
                Map<String, Object> champ = iterator.next();
                
                Boolean hasContrainte = (Boolean) champ.get("hasContrainte");
                if (hasContrainte != null && hasContrainte) {
                    int etapeMaitre = (int) champ.get("etapeMaitre");
                    int etapeCourante = numEtape;
                    
                    if (etapeMaitre < etapeCourante) {
                        String valeurCible = (String) champ.get("valeurCible");
                        String valeurActuelleMaitre = (String) champ.get("valeurActuelleMaitre");
                        
                        if (valeurActuelleMaitre == null || !valeurActuelleMaitre.equals(valeurCible)) {
                            iterator.remove(); 
                        }
                    }
                }
            }
        } else {
            // Étape bloquée : On n'envoie aucun champ pour empêcher la lecture/saisie forcée par l'URL
            canEdit = false;
        }
        
        // 6. Chargement des catalogues globaux
        Map<String, List<String>> mapCatalogues = this.loadCataloguesContraints();
        context.put("mapCatalogues", mapCatalogues);

        // 7. Formatage de la chaîne de validation au format [1][2][5] pour le tag fn:contains de la JSP
        StringBuilder sb = new StringBuilder();
        if (etapesValidees != null) {
            for (Integer e : etapesValidees) {
                sb.append("[").append(e).append("]");
            }
        }
        
        // 8. Injection des variables dans le contexte de retour
        context.put("workflow", wf);
        context.put("donneesEtape", donneesEtape);
        context.put("numEtapeActive", numEtape);
        context.put("currentEtape", configEtape);
        context.put("etapesTemplate", templateDao.getEtapesByTemplate(wf.getIdTemplateWorkflow()));
        context.put("etapesValideesChaine", sb.toString());        
        context.put("isAdmin", isAdmin);
        context.put("hasAccess", hasAccess);
        context.put("isClosed", isClosed);
        context.put("canEdit", canEdit);
        context.put("optionsAvis", Arrays.asList("Faisable", "Non faisable", "À l'étude", "Sous réserve"));

        return context;
    }
    /**
     * Gère la sauvegarde transactionnelle des formulaires dynamiques
     */
    public void saveEtapeDonnees(int idWorkflow, int nbEtape, int totalChamps, 
                                 jakarta.servlet.http.HttpServletRequest request, Utilisateur user) throws Exception {
        
        int idUser = (user != null && user.getId() != -1) ? user.getId() : 9;
        String avisSaisi = null;

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            for (int i = 0; i < totalChamps; i++) {
                String idDonneStr = request.getParameter("id_donne_" + i);
                String idTemplateDonneeStr = request.getParameter("id_template_donnee_" + i);
                String type = request.getParameter("type_" + i);
                String refContrainte = request.getParameter("ref_" + i);
                String attribut = request.getParameter("attr_" + i);
                String commentaire = request.getParameter("comm_" + i);
                String date = request.getParameter("date_" + i);

                String attrClean = (attribut != null) ? attribut.trim() : "";
                String commClean = (commentaire != null) ? commentaire.trim() : "";
                String dateClean = (date != null) ? date.trim() : "";

                if ("avis".equalsIgnoreCase(refContrainte) || "avis".equalsIgnoreCase(type)) {
                    avisSaisi = attrClean;
                }

                boolean isNew = (idDonneStr == null || idDonneStr.trim().isEmpty() || "0".equals(idDonneStr));
                java.sql.Date sqlDate = null;
                if (!dateClean.isEmpty()) {
                    try {
                        sqlDate = java.sql.Date.valueOf(dateClean);
                    } catch (IllegalArgumentException e) {
                        sqlDate = null;
                    }
                }

                int idTemplateDonnee = (idTemplateDonneeStr != null) ? Integer.parseInt(idTemplateDonneeStr) : 0;

                if (isNew) {
                    if (attrClean.isEmpty() && commClean.isEmpty() && dateClean.isEmpty()) {
                        continue;
                    }
                    donneeDao.insertDonnee(conn, type, !attrClean.isEmpty() ? attrClean : null, 
                            !commClean.isEmpty() ? commClean : null, sqlDate, idWorkflow, nbEtape, refContrainte, idTemplateDonnee);
                } else {
                    int idDonne = Integer.parseInt(idDonneStr);
                    donneeDao.updateDonnee(conn, !attrClean.isEmpty() ? attrClean : null, 
                            !commClean.isEmpty() ? commClean : null, sqlDate, idDonne);
                }
            }

            // Gestion des validations et finalisation automatique
            Workflow wf = wfDao.getById(idWorkflow);
            if (wf != null) {
                template_etape configEtape = templateDao.getEtapeConfig(wf.getIdTemplateWorkflow(), nbEtape);
                if (configEtape != null) {
                    validationDao.validerEtape(idWorkflow, idUser, configEtape.getPlace());

                    if (avisSaisi != null && configEtape.isEstFinale()) {
                        boolean isDefavorable = "Non faisable".equalsIgnoreCase(avisSaisi)
                                || "Défavorable".equalsIgnoreCase(avisSaisi)
                                || "Sous réserve".equalsIgnoreCase(avisSaisi);
                        if (isDefavorable) {
                            wfDao.finaliserWorkflow(idWorkflow);
                        }
                    }
                }
            } 
            
            conn.commit();
        }
    }

    /**
     * Charge l'ensemble des catalogues d'options depuis la table type_contraint
     * Organise les données sous forme de Map : Clef = type (ex: 'client'), Valeur = Liste d'options textuelles
     */
    private Map<String, List<String>> loadCataloguesContraints() {
        Map<String, List<String>> mapCatalogues = new HashMap<>();
        
        // CORRECTION : Sélection de la colonne "type" à la place de "id" pour mapper correctement avec d.refContrainte
        String sql = "SELECT type, valeur FROM public.type_contraint ORDER BY type, id ASC";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String refContrainte = rs.getString("type"); // Pivot corrigé ici ('client', etc.)
                String valeur = rs.getString("valeur");

                if (refContrainte != null && valeur != null) {
                    mapCatalogues.computeIfAbsent(refContrainte, k -> new ArrayList<>()).add(valeur);
                }
            }
        } catch (Exception e) {
            System.err.println("❌ Erreur lors du chargement de la table type_contraint : " + e.getMessage());
            e.printStackTrace();
        }
        return mapCatalogues;
    }

    /**
     * Méthode d'écriture de clôture globale appelée à l'envoi de l'étape finale.
     * Met à jour la date de finalisation (date courante système), le commentaire de clôture et le statut global.
     */
    public void cloturerWorkflowStructurel(int idWorkflow, String decision, String commentaire, Utilisateur user) throws Exception {
        String sql = "UPDATE public.workflow SET date_finalisation = CURRENT_DATE, commentaire = ?, statut = ? WHERE id = ?";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, commentaire != null ? commentaire.trim() : "");
            ps.setString(2, decision != null ? decision.trim() : "");
            ps.setInt(3, idWorkflow);
            int rowsUpdated = ps.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("Le dossier #" + idWorkflow + " a été structurellement clôturé avec le verdict : " + decision);
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la clôture structurelle du workflow #" + idWorkflow);
            throw e;
        }
    }
    
    public List<Integer> getEtapesValideesPourWorkflow(int idWorkflow) throws Exception {
        // Utilise ton instance de DAO existante (adapte le nom de la variable si nécessaire, ex: validationDAO)
        ValidationDAO validationDao = new ValidationDAO();
        return validationDao.getEtapesValidees(idWorkflow);
    }
}