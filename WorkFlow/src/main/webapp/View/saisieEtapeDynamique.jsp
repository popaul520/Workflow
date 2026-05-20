<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Espace de Saisie - ${workflow.titre}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .grid-boutons { display: grid; grid-template-columns: repeat(auto-fill, minmax(160px, 1fr)); gap: 12px; margin: 20px 0; }
        .btn-etape { border: none; border-radius: 6px; padding: 12px; color: white !important; text-align: center; cursor: pointer; min-height: 70px; display: flex; flex-direction: column; justify-content: center; transition: all 0.2s; width: 100%; }
        .state-locked { background-color: #bdc3c7 !important; cursor: not-allowed; opacity: 0.6; }
        .state-validated { background-color: #2ecc71 !important; }
        .state-modifiable { background-color: #3498db !important; box-shadow: 0 4px 6px rgba(52, 152, 219, 0.3); border: 2px solid #2980b9; }
        .state-active { background-color: #2c3e50 !important; border: 3px solid #f1c40f; box-shadow: 0 4px 10px rgba(0,0,0,0.15); }
        
        .visu-container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); margin-top: 30px; }
        .visu-row { display: flex; align-items: center; padding: 15px 12px; border-bottom: 1px solid #edf2f7; }
        .visu-row:hover { background-color: #f8fafc; }
        .form-control-dyn { width: 100%; padding: 8px 12px; border: 1px solid #cbd5e0; border-radius: 4px; font-size: 14px; box-sizing: border-box; }
        .status-banner { padding: 15px; border-radius: 8px; margin-bottom: 20px; display: flex; align-items: center; }
        .btn-action { padding: 10px 22px; border: none; border-radius: 4px; font-weight: bold; cursor: pointer; transition: 0.2s; }
        .required-star { color: #e53e3e; margin-left: 3px; }
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
        
        <div class="header" style="margin-bottom: 25px;">
            <h1>Suivi & Saisie Dossier <span class="status-badge">#${workflow.id}</span></h1>
            <p class="text-muted">Projet en cours : <strong>${workflow.titre}</strong></p>
        </div>

        <div class="navigation-etapes" style="background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.05);">
            <h3 style="border-left: 5px solid #3498db; padding-left: 15px; margin-top: 0;">Cycle de validation du modèle</h3>
            
            <div class="grid-boutons">
                <c:forEach var="etape" items="${etapesTemplate}">
                    <%-- CORRECTION : Utilisation de etape.attentePlace au lieu de etape.place --%>
                    <c:set var="isValidated" value="${etapesValidees.contains(etape.attentePlace)}" />
                    <c:set var="isLocked" value="${etape.attentePlace > 1 && !etapesValidees.contains(etape.attentePlace - 1) && !isValidated}" />
            
                    <c:choose>
                        <c:when test="${etape.attentePlace == numEtapeActive}">
                            <c:set var="cssClass" value="state-active" />
                        </c:when>
                        <c:when test="${isValidated}">
                            <c:set var="cssClass" value="state-validated" />
                        </c:when>
                        <c:when test="${isLocked}">
                            <c:set var="cssClass" value="state-locked" />
                        </c:when>
                        <c:otherwise>
                            <c:set var="cssClass" value="state-modifiable" />
                        </c:otherwise>
                    </c:choose>
            
                    <button type="button" 
                            <c:if test="${!isLocked}">
                                onclick="window.location.href='saisie-etape?id_workflow=${workflow.id}&num_etape=${etape.attentePlace}'"
                            </c:if>
                            class="btn-etape ${cssClass}">
                        <div class="step-role">${etape.nomEtape}</div>
                    </button>
                </c:forEach>
            </div>
        </div>

        <div class="visu-container">

            <c:if test="${isClosed}">
                <div class="status-banner" style="background-color: #fff5f5; border: 1px solid #feb2b2;">
                    <span style="font-size: 24px; margin-right: 15px;">🔒</span>
                    <div>
                        <strong style="color: #c53030;">Dossier Clôturé</strong><br>
                        <small style="color: #4a5568;">Finalisé le : ${workflow.dateFinalisation}</small>
                    </div>
                </div>
            </c:if>

            <c:choose>
                <c:when test="${empty donneesEtape && !canEdit}">
                    <div class="info-box" style="text-align: center; padding: 40px; color: #718096; background: #f7fafc; border-radius: 6px;">
                        <p>Cette étape n'a pas encore été renseignée.</p>
                    </div>
                </c:when>
                
                <c:otherwise>
                    <form action="${pageContext.request.contextPath}/saisie-etape" method="post">
                        <input type="hidden" name="id_workflow" value="${workflow.id}">
                        <input type="hidden" name="current_n" value="${numEtapeActive}">
                        <input type="hidden" name="total_champs" value="${donneesEtape.size()}">

                        <div class="step-header" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px; border-bottom: 2px solid #edf2f7; padding-bottom: 15px;">
                            <h2 style="margin: 0; color: #2d3748;">Saisie : ${currentEtape.nomEtape} (Étape ${numEtapeActive})</h2>
                            <div>
                                <c:if test="${canEdit}">
                                    <button type="button" id="btn-modifier" onclick="activerEdition()" class="btn-action" style="background: #3182ce; color: white;">Modifier</button>
                                    <button type="submit" id="btn-enregistrer" class="btn-action" style="display: none; background: #38a169; color: white;">Enregistrer et Valider</button>
                                </c:if>
                            </div>
                        </div>

                        <fieldset id="fs-edition" ${canEdit ? 'disabled' : ''} style="border:none; padding:0; margin:0;">
                            <c:forEach var="d" items="${donneesEtape}" varStatus="status">
                                <div class="visu-row">
                                    
                                    <input type="hidden" name="id_donne_${status.index}" value="${d.idDonne}">
                                    <input type="hidden" name="id_template_donnee_${status.index}" value="${d.idTemplateDonnee}">
                                    <input type="hidden" name="type_${status.index}" value="${d.nomChamp}">
                                    <input type="hidden" name="ref_${status.index}" value="${d.refContrainte}">
                                    
                                    <div style="flex: 1; font-weight: 600; color: #4a5568; padding-right: 15px;">
                                        ${d.nomChamp}
                                        <c:if test="${d.estObligatoire}"><span class="required-star">*</span></c:if>
                                    </div>

                                    <div style="flex: 1.5; padding-right: 15px;">
                                        <span class="view-mode" style="font-size: 15px; color: #2d3748;">
                                            ${not empty d.attribut ? d.attribut : '<em>(Vide)</em>'}
                                        </span>
                                        
                                        <div class="edit-mode" style="display: none;">
                                            <c:choose>
                                                <c:when test="${d.refContrainte == 'avis'}">
                                                    <select name="attr_${status.index}" class="form-control-dyn" ${d.estObligatoire ? 'required' : ''}>
                                                        <option value="">-- Choisir un avis --</option>
                                                        <c:forEach var="opt" items="${optionsAvis}">
                                                            <option value="${opt}" ${d.attribut == opt ? 'selected' : ''}>${opt}</option>
                                                        </c:forEach>
                                                    </select>
                                                </c:when>
                                                
                                                <c:when test="${d.refContrainte == 'Bool'}">
                                                    <select name="attr_${status.index}" class="form-control-dyn" ${d.estObligatoire ? 'required' : ''}>
                                                        <option value="">-- Sélectionner --</option>
                                                        <option value="OUI" ${d.attribut == 'OUI' ? 'selected' : ''}>OUI</option>
                                                        <option value="NON" ${d.attribut == 'NON' ? 'selected' : ''}>NON</option>
                                                    </select>
                                                </c:when>
                                                
                                                <c:otherwise>
                                                    <input type="${not empty d.typeComposant ? d.typeComposant : 'text'}" 
                                                           name="attr_${status.index}" 
                                                           value="${d.attribut}" 
                                                           class="form-control-dyn" 
                                                           ${d.estObligatoire ? 'required' : ''} 
                                                           placeholder="Saisir...">
                                                </c:otherwise>
                                            </c:choose>
                                        </div>
                                    </div>

                                    <div style="flex: 1.5; display: flex; flex-direction: column; gap: 6px;">
                                        <c:if test="${d.aCommentaire}">
                                            <div>
                                                <div class="view-mode" style="color: #718096; font-size: 0.85em; font-style: italic;">
                                                    Com. : ${not empty d.commentaire ? d.commentaire : '(Aucun)'}
                                                </div>
                                                <div class="edit-mode" style="display: none;">
                                                    <input type="text" name="comm_${status.index}" value="${d.commentaire}" class="form-control-dyn" placeholder="Ajouter une remarque...">
                                                </div>
                                            </div>
                                        </c:if>

                                        <c:if test="${d.aDate}">
                                            <div style="margin-top: 4px;">
                                                <div class="view-mode" style="font-size: 0.8em; color: #4a5568;">
                                                    Date : ${not empty d.date ? d.date : '(Non renseignée)'}
                                                </div>
                                                <div class="edit-mode" style="display: none;">
                                                    <input type="date" name="date_${status.index}" value="${d.date}" class="form-control-dyn">
                                                </div>
                                            </div>
                                        </c:if>
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