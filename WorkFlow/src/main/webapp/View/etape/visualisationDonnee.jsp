<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="model.Utilisateur, model.Workflow, java.util.List" %>
<%@ page import="dao.DonneeDAO" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>
<%

    // 1. Initialisation automatique du DAO pour charger les options dynamiques des listes
    DonneeDAO donneeDao = new DonneeDAO();
    request.setAttribute("optionsBool", donneeDao.getValeursContraintes("Bool")); 
    request.setAttribute("optionsAvis", donneeDao.getValeursContraintes("avis"));
    request.setAttribute("optionsSaisonalite", donneeDao.getValeursContraintes("saisonalite"));
    // 2. Récupération des attributs de contexte passés par le contrôleur
    Workflow wf = (Workflow) request.getAttribute("wf");
    Utilisateur user = (Utilisateur) session.getAttribute("user");
    int nEtape = (Integer) request.getAttribute("numEtape");
    
    boolean isAdmin = (Boolean) request.getAttribute("isAdmin");
    boolean canEdit = (Boolean) request.getAttribute("canEdit");
    boolean isClosed = (Boolean) request.getAttribute("isClosed");
    
    // Détection automatique : si l'étape est présente dans la table validation
    boolean isEtapeValidee = (request.getAttribute("isEtapeValidee") != null) ? (Boolean) request.getAttribute("isEtapeValidee") : false;
%>

<div class="visu-container" style="padding: 20px; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); font-family: 'Segoe UI', sans-serif;">

    <%-- Bannière de statut (Clôturé ou Mode Admin) --%>
    <% if (isClosed) { %>
        <div class="status-banner" style="background-color: <%= isAdmin ? "#ebf8ff" : "#fff5f5" %>; border: 1px solid <%= isAdmin ? "#90cdf4" : "#feb2b2" %>; padding: 15px; border-radius: 8px; margin-bottom: 20px; display: flex; align-items: center;">
            <span style="font-size: 24px; margin-right: 15px;"><%= isAdmin ? "🔓" : "🔒" %></span>
            <div>
                <strong style="color: <%= isAdmin ? "#2c5282" : "#c53030" %>;">
                    <%= isAdmin ? "Mode Maintenance Patron (Admin)" : "Dossier Clôturé" %>
                </strong><br>
                <small style="color: #4a5568;">Finalisé le : ${wf.dateFinalisation}</small>
            </div>
        </div>
    <% } %>
    <form action="${pageContext.request.contextPath}/etapeController" method="post">
        <%-- Contextes cachés obligatoires --%>
        <input type="hidden" name="id_workflow" value="${id_workflow}">
        <input type="hidden" name="current_n" value="<%= nEtape %>">

        <%-- Header avec l'intitulé dynamique de l'étape et les boutons d'actions contextuels --%>
        <div class="step-header" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; border-bottom: 2px solid #edf2f7; padding-bottom: 10px;">
            <h2 style="margin: 0; color: #2c3e50;">Étape <%= nEtape %> : <%= model.Utilisateur.getRole(nEtape) %></h2>
            <div>
                <% if (isEtapeValidee) { %>
                    <%-- CAS 1 : ÉTAPE DÉJÀ VALIDÉE -> Visualisation initiale par défaut --%>
                    <% if (canEdit) { %>
                        <button type="button" id="btn-modifier" onclick="activerEdition()" class="btn" style="background: #3182ce; color: white; padding: 8px 20px; border: none; border-radius: 4px; cursor: pointer; font-weight: bold;">Modifier</button>
                        <button type="submit" id="btn-enregistrer" style="display: none; background: #38a169; color: white; padding: 8px 20px; border: none; border-radius: 4px; cursor: pointer; font-weight: bold;">Enregistrer les modifications</button>
                    <% } %>
                <% } else { %>
                    <%-- CAS 2 : ÉTAPE EN ATTENTE -> Saisie directe ouverte --%>
                    <button type="submit" id="btn-valider" style="background: #27ae60; color: white; padding: 10px 25px; border: none; border-radius: 4px; cursor: pointer; font-weight: bold;">Enregistrer et valider l'étape</button>
                <% } %>
            </div>
        </div>

        <%-- Le conteneur du formulaire s'active ou se désactive selon les droits globaux --%>
        <fieldset id="fs-edition" <%= (isEtapeValidee && !canEdit) ? "disabled" : "" %> style="border:none; padding:0; margin:0;">
            <c:forEach var="d" items="${donneesEtape}" varStatus="status">
                <div class="visu-row" style="display: flex; flex-direction: column; padding: 15px; margin-bottom: 15px; border: 1px solid #eee; border-radius: 6px; background-color: ${status.index % 2 == 0 ? '#f9f9f9' : '#ffffff'};">
                    
                    <%-- Données techniques transmises au contrôleur pour l'indexation --%>
                    <input type="hidden" name="idDonne_${status.index}" value="${d.idDonne}">
                    <input type="hidden" name="ref_${status.index}" value="${d.refTypeContraint}">
                    <input type="hidden" name="type_${status.index}" value="${d.type}">
                    
                    <%-- Libellé du champ dynamique --%>
                    <label style="display: block; font-weight: bold; margin-bottom: 8px; color: #34495e;">${d.type} :</label>

                    <div style="display: flex; gap: 20px; align-items: flex-start; width: 100%;">
                        
                        <%-- ZONE 1 : L'ATTRIBUT (Saisie principale) --%>
                        <div style="flex: 2;">
                            <%-- Mode Visualisation --%>
                            <span class="view-mode" style="display: <%= isEtapeValidee ? "block" : "none" %>; padding: 8px; background: #fff; border-left: 3px solid #3498db; font-weight: 500;">
                                ${not empty d.attribut ? d.attribut : '(Vide)'}
                            </span>
                            
                            <%-- Mode Remplissage Direct / Edition --%>
                            <div class="edit-mode" style="display: <%= isEtapeValidee ? "none" : "block" %>;">
                                <c:choose>
                                    <%-- Cas 1 : Gestionnaire d'Avis Production --%>
                                    <c:when var="isAvis" test="${d.refTypeContraint == 'avis'}">
                                        <select name="attr_${status.index}" required style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px;">
                                            <option value="">-- Choisir un avis --</option>
                                            <c:forEach var="opt" items="${optionsAvis}">
                                                <option value="${opt}" ${d.attribut == opt ? 'selected' : ''}>${opt}</option>
                                            </c:forEach>
                                        </select>
                                    </c:when>

                                    <%-- Cas 2 : Gestionnaire de type Booléen (OUI / NON) --%>
                                    <c:when var="isBool" test="${d.refTypeContraint == 'Bool'}">
                                        <select name="attr_${status.index}" required style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px;">
                                            <option value="">-- Sélectionner (Oui/Non) --</option>
                                            <c:forEach var="opt" items="${optionsBool}">
                                                <option value="${opt}" ${d.attribut == opt ? 'selected' : ''}>${opt}</option>
                                            </c:forEach>
                                        </select>
                                    </c:when>

                                    <%-- Cas 3 : Gestionnaire de Saisonalité --%>
                                    <c:when var="isSaison" test="${d.refTypeContraint == 'saisonalite'}">
                                        <select name="attr_${status.index}" style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px;">
                                            <option value="">-- Choisir une saison --</option>
                                            <c:forEach var="opt" items="${optionsSaisonalite}">
                                                <option value="${opt}" ${d.attribut == opt ? 'selected' : ''}>${opt}</option>
                                            </c:forEach>
                                        </select>
                                    </c:when>

                                    <%-- Cas 4 : Détection numérique pour les Volumes et Prévisions --%>
									<c:when test="${fn:contains(fn:toLowerCase(d.type), 'volume') || 
									                fn:contains(fn:toLowerCase(d.type), 'prévision') || 
									                fn:contains(fn:toLowerCase(d.type), 'estimation')}">
									    <input type="number" name="attr_${status.index}" value="${d.attribut}" min="0" placeholder="Ex: 50000" style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">
									</c:when>

                                    <%-- Cas par défaut : Entrée texte générique libre --%>
                                    <c:otherwise>
                                        <input type="text" name="attr_${status.index}" value="${d.attribut}" placeholder="Renseigner la valeur..." style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div> 

                        <%-- ZONE 2 : COMMENTAIRES ET DATES (Meta) --%>
                        <div style="flex: 2; display: flex; flex-direction: column; gap: 8px;">
                            
                            <%-- Gestion de la zone Commentaire --%>
                            <div>
                                <div class="view-mode" style="display: <%= isEtapeValidee ? "block" : "none" %>; color: #7f8c8d; font-size: 0.9em; font-style: italic; padding: 4px;">
                                    <c:if test="${not empty d.commentaire}"><strong>Commentaire :</strong> ${d.commentaire}</c:if>
                                </div>
                                <div class="edit-mode" style="display: <%= isEtapeValidee ? "none" : "block" %>;">
                                    <input type="text" name="comm_${status.index}" value="${d.commentaire}" placeholder="Ajouter un commentaire ou note technique..." style="width: 100%; padding: 9px; border: 1px solid #ccc; border-radius: 4px;">
                                </div>
                            </div>
                            <%-- Gestion de la zone Date --%>
                            <div>
                                <div class="view-mode" style="display: <%= isEtapeValidee ? "block" : "none" %>; font-size: 0.85em; color: #2c3e50;">
                                    <c:if test="${not empty d.date}"><strong>Date enregistrée :</strong> ${d.date}</c:if>
                                </div>
                                <div class="edit-mode" style="display: <%= isEtapeValidee ? "none" : "block" %>;">
                                    <input type="date" name="date_${status.index}" value="${d.date}" style="width: 100%; padding: 8px; border: 1px solid #ccc; border-radius: 4px;">
                                </div>
                            </div>

                        </div>
                    </div>
                </div>
            </c:forEach>
        </fieldset>
    </form>
</div>
<script>
// Gestion de la bascule dynamique côté Client via JS pour le mode édition
function activerEdition() {
    document.querySelectorAll('.view-mode').forEach(el => el.style.display = 'none');
    document.querySelectorAll('.edit-mode').forEach(el => el.style.display = 'block');
    
    // Switch des boutons de contrôle du header
    document.getElementById('btn-modifier').style.display = 'none';
    document.getElementById('btn-enregistrer').style.display = 'inline-block';
    
    // Déverrouillage des éléments du fieldset
    const fs = document.getElementById('fs-edition');
    if(fs) fs.disabled = false;
}
</script>

