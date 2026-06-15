<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="model.Utilisateur, model.Workflow, java.util.List" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn" %>

<style>
/* Structure Générale */
.visu-container {
    padding: 24px; 
    font-family: 'Segoe UI', system-ui, -apple-system, sans-serif;
    color: #2d3748;
    background-color: #f7fafc;
    border-radius: 12px;
}


/* Grille responsive à 2 colonnes de blocs de cartes pour une lecture aérée */
.grid-form-dispositif {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: 20px;
    width: 100%;
}

/* Système de cartes pour chaque élément de donnée */
.carte-element {
    background: #ffffff;
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    padding: 18px;
    box-shadow: 0 1px 3px rgba(0,0,0,0.05);
    display: flex;
    flex-direction: column;
    gap: 12px;
    transition: box-shadow 0.2s ease;
}

.carte-element:hover {
    box-shadow: 0 4px 6px rgba(0,0,0,0.05);
}

/* Bloc d'une seule colonne pleine largeur pour les commentaires et textes longs */
.champs-long { 
    grid-column: span 2; 
}
/* Labels et Titres */
.label-titre {
    font-weight: 600; 
    color: #4a5568;
    font-size: 0.9em;
    text-transform: uppercase;
    letter-spacing: 0.05em;
    border-left: 3px solid #3182ce;
    padding-left: 8px;
}

/* Mode Lecture Amélioré */
.valeur-lecture {
    font-size: 15px;
    color: #1a202c;
    padding: 6px 0;
    font-weight: 500;
}

/* Champs de saisie ergonomiques */
.input-dynamique, .textarea-dynamique {
    width: 100%;
    padding: 10px;
    border: 1px solid #cbd5e0;
    border-radius: 6px;
    font-size: 14px;
    font-family: inherit;
    background-color: #fff;
    box-sizing: border-box;
    transition: border-color 0.15s ease, box-shadow 0.15s ease;
}

.input-dynamique:focus, .textarea-dynamique:focus {
    border-color: #3182ce;
    outline: none;
    box-shadow: 0 0 0 3px rgba(66, 153, 225, 0.15);
}

.textarea-dynamique {
    resize: vertical;
    min-height: 60px;
}

/* Boutons d'action */
.btn-action {
    padding: 8px 16px;
    border: none;
    border-radius: 6px;
    cursor: pointer;
    font-weight: 600;
    font-size: 14px;
    display: inline-flex;
    align-items: center;
    gap: 6px;
    transition: background 0.2s ease;
}
</style>

<%
    Workflow wf = (Workflow) request.getAttribute("wf");
    Utilisateur user = (Utilisateur) session.getAttribute("user");
    int nEtape = (Integer) request.getAttribute("numEtape");
    
    boolean isClosed = (Boolean) request.getAttribute("isClosed");
    boolean canEdit = (Boolean) request.getAttribute("canEdit");
    boolean isAdmin = (Boolean) request.getAttribute("isAdmin");
    
    List<?> listeDonnees = (List<?>) request.getAttribute("donneesEtape");
    boolean hasData = (listeDonnees != null && !listeDonnees.isEmpty());
%>

<div class="visu-container">

    <%-- 📌 Bannière de Statut Dossier Clôturé --%>
    <% if (isClosed) { %>
        <div style="background-color: <%= isAdmin ? "#ebf8ff" : "#fff5f5" %>; border: 1px solid <%= isAdmin ? "#90cdf4" : "#feb2b2" %>; padding: 16px; border-radius: 8px; margin-bottom: 24px; display: flex; align-items: center; gap: 16px;">
            <span style="font-size: 24px;"><%= isAdmin ? "🔓" : "🔒" %></span>
            <div>
                <strong style="color: <%= isAdmin ? "#2c5282" : "#c53030" %>; font-size: 1.1em;">
                    <%= isAdmin ? "Mode Maintenance Super-Administrateur" : "Dossier Clôturé" %>
                </strong><br>
                <span style="color: #4a5568; font-size: 0.9em;">Les modifications sont verrouillées pour les rôles standards.</span>
            </div>
        </div>
    <% } %>

    <% if (!hasData && !canEdit) { %>
        <div style="text-align: center; padding: 48px; color: #a0aec0; background: #fff; border-radius: 8px; border: 1px dashed #e2e8f0;">
            <p style="font-size: 1.1em; margin: 0;">ℹ️ Cette étape n'a pas encore été renseignée par le service concerné.</p>
        </div>
    <% } else { %>

        <form action="${pageContext.request.contextPath}/etapeController" method="post">
            <input type="hidden" name="id_workflow" value="${id_workflow}">
            <input type="hidden" name="current_n" value="<%= nEtape %>">

            <%-- En-tête de la zone d'étape --%>
            <div style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 24px; border-bottom: 2px solid #e2e8f0; padding-bottom: 12px;">
                <h3 style="margin: 0; color: #1a202c; font-size: 1.3em;">Étape <%= nEtape %> : <%= model.Utilisateur.getRole(nEtape) %></h3>
                <div style="display: flex; gap: 8px;">
                    <% if (canEdit) { %>
                        <button type="button" id="btn-modifier" onclick="activerEdition()" class="btn-action" style="background: #3182ce; color: white;"> Modifier</button>
                        <button type="button" id="btn-annuler" onclick="annulerEdition()" class="btn-action" style="display: none; background: #edf2f7; color: #4a5568;">Annuler</button>
                        <button type="submit" id="btn-enregistrer" class="btn-action" style="display: none; background: #38a169; color: white;">Enregistrer</button>
                    <% } %>
                </div>
            </div>

            <fieldset id="fs-edition" <%= canEdit ? "" : "disabled" %> style="border:none; padding:0; margin:0;">
                <div class="grid-form-dispositif">
                    <c:forEach var="d" items="${donneesEtape}" varStatus="status">
                        <%-- Changement automatique de gabarit si c'est un long bloc de texte --%>
                        <c:set var="isLongText" value="${d.refTypeContraint == 'Commentaire' || d.refTypeContraint == 'TexteLong'}" />
                        
                        <div class="carte-element ${isLongText ? 'champs-long' : ''}">
                            
                            <%-- Données cachées techniques --%>
                            <input type="hidden" name="idDonne_${status.index}" value="${d.idDonne}">
                            <input type="hidden" name="ref_${status.index}" value="${d.refTypeContraint}">
                            <input type="hidden" name="type_${status.index}" value="${d.type}">

                            <%-- Label de la donnée --%>
                            <div class="label-titre">${d.type}</div>
                            
                            <%-- Affichage Mode Lecture --%>
                            <div class="view-mode valeur-lecture">
                                <c:choose>
                                    <c:when test="${not empty d.attribut}">
                                        <c:out value="${d.attribut}"/>
                                    </c:when>
                                    <c:otherwise>
                                        <span style="color: #cbd5e0; font-style: italic;">Non renseigné</span>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                            
                            <%-- Affichage Formulaire Mode Édition --%>
                            <div class="edit-mode" style="display: none;">
                                <c:choose>
                                    <c:when test="${d.refTypeContraint == 'avis'}">
                                        <select name="attr_${status.index}" class="input-dynamique">
                                            <c:forEach var="opt" items="${optionsAvis}">
                                                <option value="${opt}" ${d.attribut == opt ? 'selected' : ''}>${opt}</option>
                                            </c:forEach>
                                        </select>
                                    </c:when>
                                    <c:when test="${d.refTypeContraint == 'Bool'}">
                                        <select name="attr_${status.index}" class="input-dynamique">
                                            <option value="OUI" ${d.attribut == 'OUI' ? 'selected' : ''}>OUI</option>
                                            <option value="NON" ${d.attribut == 'NON' ? 'selected' : ''}>NON</option>
                                        </select>
                                    </c:when>
                                    <c:when test="${isLongText || (not empty d.attribut && fn:length(d.attribut) > 60)}">
                                        <textarea name="attr_${status.index}" class="textarea-dynamique" rows="3">${d.attribut}</textarea>
                                    </c:when>
                                    <c:otherwise>
                                        <input type="text" name="attr_${status.index}" value="${d.attribut}" class="input-dynamique">
                                    </c:otherwise>
                                </c:choose>
                            </div>

                            <%-- Ligne secondaire : Observations et Date système --%>
                            <div style="margin-top: auto; padding-top: 8px; border-top: 1px dashed #edf2f7; display: flex; flex-direction: column; gap: 6px;">
                                
                                <%-- Bloc observation --%>
                                <div class="view-mode" style="color: #718096; font-size: 0.85em; font-style: italic;">
                                    <c:if test="${not empty d.commentaire}">${d.commentaire}</c:if>
                                </div>
                                <div class="edit-mode" style="display: none;">
                                    <label style="font-size: 0.8em; font-weight: 600; color: #718096; display:block; margin-bottom: 4px;">Observations / Précisions :</label>
                                    <textarea name="comm_${status.index}" class="textarea-dynamique" rows="2" placeholder="Ajouter un détail ou une remarque...">${d.commentaire}</textarea>
                                </div>

                                <%-- Bloc Date --%>
                                <div class="view-mode" style="font-size: 0.8em; color: #a0aec0; text-align: right;">
                                    <c:if test="${not empty d.date}">Mis à jour le : ${d.date}</c:if>
                                </div>
                                <div class="edit-mode" style="display: none; text-align: right;">
                                    <label style="font-size: 0.8em; color: #718096; display:inline-block; margin-right: 5px;">Date d'effet :</label>
                                    <input type="date" name="date_${status.index}" value="${d.date}" class="input-dynamique" style="width: auto; display: inline-block; padding: 4px 8px; font-size: 13px;">
                                </div>
                            </div>

                        </div>
                    </c:forEach>
                    
                </div>
            </fieldset>
        </form>
    <% } %>
</div>

<script>
function activerEdition() {
    document.querySelectorAll('.view-mode').forEach(el => el.style.display = 'none');
    document.querySelectorAll('.edit-mode').forEach(el => el.style.display = 'block');
    document.getElementById('btn-modifier').style.display = 'none';
    document.getElementById('btn-annuler').style.display = 'inline-block';
    document.getElementById('btn-enregistrer').style.display = 'inline-block';
    const fs = document.getElementById('fs-edition');
    if(fs) fs.disabled = false;
}

function annulerEdition() {
    document.querySelectorAll('.view-mode').forEach(el => el.style.display = 'block');
    document.querySelectorAll('.edit-mode').forEach(el => el.style.display = 'none');
    document.getElementById('btn-modifier').style.display = 'inline-block';
    document.getElementById('btn-annuler').style.display = 'none';
    document.getElementById('btn-enregistrer').style.display = 'none';
    const fs = document.getElementById('fs-edition');
    if(fs) fs.disabled = true;
}
</script>
prend le style pour que la visualisation soit plus visible et fait en sorte que il y a une bonne visibilité 