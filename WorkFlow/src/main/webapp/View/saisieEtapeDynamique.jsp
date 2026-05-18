<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Espace de Travail Dynamique - ${workflow.titre}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        /* Grille globale style 'Détail' */
        .grid-boutons { display: grid; grid-template-columns: repeat(auto-fill, minmax(160px, 1fr)); gap: 12px; margin: 20px 0; }
        .btn-etape { border: none; border-radius: 6px; padding: 12px; color: white !important; text-align: center; cursor: pointer; min-height: 70px; display: flex; flex-direction: column; justify-content: center; transition: all 0.2s; width: 100%; }
        .state-locked { background-color: #bdc3c7 !important; cursor: not-allowed; opacity: 0.6; }
        .state-validated { background-color: #2ecc71 !important; }
        .state-modifiable { background-color: #3498db !important; box-shadow: 0 4px 6px rgba(52, 152, 219, 0.3); }
        .state-active { background-color: #2c3e50 !important; border: 3px solid #f1c40f; box-shadow: 0 4px 10px rgba(0,0,0,0.15); }
        
        /* Conteneur formulaire dynamique */
        .visu-container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); margin-top: 30px; }
        .visu-row { display: flex; align-items: center; padding: 15px 12px; border-bottom: 1px solid #edf2f7; transition: background 0.2s; }
        .visu-row:hover { background-color: #f8fafc; }
        .form-control-dyn { width: 100%; padding: 8px 12px; border: 1px solid #cbd5e0; border-radius: 4px; font-size: 14px; }
        
        .status-banner { padding: 15px; border-radius: 8px; margin-bottom: 20px; display: flex; align-items: center; }
        .btn-action { padding: 10px 22px; border: none; border-radius: 4px; font-weight: bold; cursor: pointer; transition: 0.2s; }
    </style>
</head>
<body>

    <div class="sidebar">
        <h3>Actions</h3>
        <ul>
            <li><a href="home">🏠 Retour Accueil</a></li>
        </ul>
    </div>

    <div class="main-container" style="padding: 40px; font-family: 'Segoe UI', sans-serif;">
        
        <%-- Entête du Dossier --%>
        <div class="header" style="margin-bottom: 25px;">
            <h1>Suivi & Remplissage Dossier <span class="status-badge">#${workflow.id}</span></h1>
            <p class="text-muted" style="font-size: 1.1em;">Modèle de référence : <strong style="color: #2c3e50;">${workflow.titre}</strong></p>
        </div>

        <%-- BLOC 1 : La Grille de Suivi (Cartographie des Étapes du Template) --%>
        <div class="navigation-etapes" style="background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.05);">
            <h3 style="border-left: 5px solid #3498db; padding-left: 15px; margin-top: 0;">Cycle de validation requis</h3>
            
            <div class="grid-boutons">
                <c:forEach var="etape" items="${etapesTemplate}">
                    <c:set var="isValidated" value="${etapesValidees.contains(etape.attentePlace)}" />
                    <c:set var="isLocked" value="${etape.attentePlace > 1 && !etapesValidees.contains(etape.attentePlace - 1) && !isValidated}" />

                    <%-- Attribution des classes graphiques dynamiques --%>
                    <c:choose>
                        <c:when test="${etape.attentePlace == numEtapeActive}">
                            <c:set var="cssClass" value="state-active" /> <%-- Étape actuellement ouverte sous vos yeux --%>
                        </c:when>
                        <c:when test="${isValidated}">
                            <c:set var="cssClass" value="state-validated" /> <%-- Étape complétée --%>
                        </c:when>
                        <c:when test="${isLocked}">
                            <c:set var="cssClass" value="state-locked" /> <%-- Étape non accessible --%>
                        </c:when>
                        <c:otherwise>
                            <c:set var="cssClass" value="state-modifiable" /> <%-- Étape accessible en lecture/édition --%>
                        </c:otherwise>
                    </c:choose>

                    <button type="button" 
                            <c:if test="${!isLocked}">
                                onclick="window.location.href='saisie-etape?id_workflow=${workflow.id}&num_etape=${etape.attentePlace}'"
                            </c:if>
                            class="btn-etape ${cssClass}">
                        <div class="step-number" style="font-size: 0.8em; opacity: 0.8;">ÉTAPE ${etape.attentePlace}</div>
                        <div class="step-role" style="font-weight: bold; margin-top: 3px;">${etape.nomEtape}</div>
                    </button>
                </c:forEach>
            </div>
        </div>

        <%-- BLOC 2 : Formulaire de Saisie / Visualisation dynamique de l'étape sélectionnée --%>
        <div class="visu-container">

            <%-- Bannière Dossier Clôturé (Si applicable) --%>
            <c:if test="${isClosed}">
                <div class="status-banner" style="background-color: #fff5f5; border: 1px solid #feb2b2;">
                    <span style="font-size: 24px; margin-right: 15px;">🔒</span>
                    <div>
                        <strong style="color: #c53030;">Dossier Clôturé et figé</strong><br>
                        <small style="color: #4a5568;">Finalisé le : <fmt:formatDate value="${workflow.dateFinalisation}" pattern="dd/MM/yyyy"/></small>
                    </div>
                </div>
            </c:if>

            <c:choose>
                <%-- Cas où l'étape n'a pas encore de données générées et qu'on ne peut pas l'éditer --%>
                <c:when test="${empty donneesEtape && !canEdit}">
                    <div class="info-box" style="text-align: center; padding: 40px; color: #718096; background: #f7fafc; border-radius: 6px;">
                        <p>💡 Ce service n'a pas encore renseigné les informations requises pour l'étape ${numEtapeActive}.</p>
                    </div>
                </c:when>
                
                <c:otherwise>
                    <%-- Formulaire d'envoi dynamique vers le contrôleur de validation --%>
                    <form action="${pageContext.request.contextPath}/etapeController" method="post">
                        <input type="hidden" name="id_workflow" value="${workflow.id}">
                        <input type="hidden" name="current_n" value="${numEtapeActive}">
                        <div class="step-header" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px; border-bottom: 2px solid #edf2f7; padding-bottom: 15px;">
                            <h2 style="margin: 0; color: #2d3748;">Saisie active : ${currentEtape.nomEtape} <span style="font-size: 0.6em; color: #718096;">(Étape ${numEtapeActive})</span></h2>
                            <div>
                                <c:if test="${canEdit}">
                                    <button type="button" id="btn-modifier" onclick="activerEdition()" class="btn-action" style="background: #3182ce; color: white;">📝 Modifier les champs</button>
                                    <button type="submit" id="btn-enregistrer" class="btn-action" style="display: none; background: #38a169; color: white;">💾 Enregistrer & Valider l'Étape</button>
                                </c:if>
                            </div>
                        </div>

                        <%-- Conteneur de tous les attributs rattachés à cette étape --%>
                        <fieldset id="fs-edition" ${canEdit ? 'disabled' : ''} style="border:none; padding:0; margin:0;">
                            <c:forEach var="d" items="${donneesEtape}" varStatus="status">
                                <div class="visu-row">
                                    
                                    <%-- Injection des ID techniques indispensables pour l'enregistrement SQL --%>
                                    <input type="hidden" name="idDonne_${status.index}" value="${d.idDonne}">
                                    <input type="hidden" name="ref_${status.index}" value="${d.refTypeContraint}">
                                    <input type="hidden" name="type_${status.index}" value="${d.type}">
                                    
                                    <%-- Libellé dynamique de la donnée (ex: "Rayon", "Marque", "Avis D.O.P") --%>
                                    <div style="flex: 1; font-weight: 600; color: #4a5568; padding-right: 15px;">${d.type}</div>

                                    <%-- Saisie de la Valeur (S'adapte dynamiquement selon la contrainte de type) --%>
                                    <div style="flex: 1.5; padding-right: 15px;">
                                        <span class="view-mode" style="font-size: 15px; color: #2d3748;">${not empty d.attribut ? d.attribut : '<em>(Non renseigné)</em>'}</span>
                                        
                                        <div class="edit-mode" style="display: none;">
                                            <c:choose>
                                                <%-- Traitement des listes d'avis --%>
                                                <c:when test="${d.refTypeContraint == 'avis'}">
                                                    <select name="attr_${status.index}" class="form-control-dyn">
                                                        <c:forEach var="opt" items="${optionsAvis}">
                                                            <option value="${opt}" ${d.attribut == opt ? 'selected' : ''}>${opt}</option>
                                                        </c:forEach>
                                                    </select>
                                                </c:when>
                                                
                                                <%-- Traitement des types Booléens --%>
                                                <c:when test="${d.refTypeContraint == 'Bool'}">
                                                    <select name="attr_${status.index}" class="form-control-dyn">
                                                        <option value="OUI" ${d.attribut == 'OUI' ? 'selected' : ''}>OUI</option>
                                                        <option value="NON" ${d.attribut == 'NON' ? 'selected' : ''}>NON</option>
                                                    </select>
                                                </c:when>
                                                
                                                <%-- Input text classique par défaut pour toutes les autres contraintes --%>
                                                <c:otherwise>
                                                    <input type="text" name="attr_${status.index}" value="${d.attribut}" class="form-control-dyn">
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>

                                    <%-- Colonne Observations & Commentaires --%>
                                    <div style="flex: 1.5;">
                                        <div class="view-mode" style="color: #718096; font-size: 0.9em; italic">${empty d.commentaire ? 'Pas de remarque' : d.commentaire}</div>
                                        <div class="edit-mode" style="display: none;">
                                            <input type="text" name="comm_${status.index}" value="${d.commentaire}" class="form-control-dyn" placeholder="Note ou remarque...">
                                        </div>
                                    </div>

                                </div>
                            </c:forEach>
                        </fieldset>
                    </form>
                </c:otherwise>
           </c:choose>
        </div>
    </div>

    <script>
        // Permet de basculer instantanément l'affichage du mode Lecture vers le mode Modification
        function activerEdition() {
            document.querySelectorAll('.view-mode').forEach(el => el.style.display = 'none');
            document.querySelectorAll('.edit-mode').forEach(el => el.style.display = 'block');
            document.getElementById('btn-modifier').style.display = 'none';
            document.getElementById('btn-enregistrer').style.display = 'inline-block';
            const fs = document.getElementById('fs-edition');
            if(fs) fs.removeAttribute('disabled');
        }
    </script>
</body>
</html>
