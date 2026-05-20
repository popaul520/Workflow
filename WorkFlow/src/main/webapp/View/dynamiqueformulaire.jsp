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
        .btn-etape strong { display: block; font-size: 0.9em; margin-top: 5px; }
        
        .form-card { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0,0,0,0.05); margin-top: 30px; border-top: 4px solid #3498db; }
        .grid-form { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; }
        .form-group { display: flex; flex-direction: column; }
        .full { grid-column: span 2; }
    </style>
</head>
<body>

    <div class="sidebar">
        <h3>Actions</h3>
        <ul>
            <li><a href="home">🏠 Retour Accueil</a></li>
        </ul>
    </div>

    <div class="main-container">
        
        <div class="header">
            <h1>Suivi & Saisie Dossier <span class="status-badge">#${workflow.id}</span></h1>
            <p class="text-muted">Projet en cours : <strong>${workflow.titre}</strong></p>
        </div>

        <div class="navigation-etapes">
            <h3 style="border-left: 5px solid #3498db; padding-left: 15px;">Cycle de validation du modèle</h3>
            
            <div class="grid-boutons">
                <c:forEach var="etape" items="${etapesTemplate}">
                    
                    <c:set var="isValidated" value="${etapesValidees.contains(etape.attentePlace)}" />
                    <%-- Règle d'accès : bloqué si l'étape précédente n'est pas validée et qu'elle n'est pas elle-même validée --%>
                    <c:set var="isLocked" value="${etape.attentePlace > 1 && !etapesValidees.contains(etape.attentePlace - 1) && !isValidated}" />

                    <c:choose>
                        <c:when test="${isValidated}">
                            <c:set var="cssClass" value="state-validated" />
                        </c:when> 
                        <c:when test="${etape.attentePlace == numEtapeActive}">
                            <c:set var="cssClass" value="state-modifiable" /> </c:when>
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
                        <div class="step-number">ÉTAPE ${etape.attentePlace}</div>
                        <div class="step-role">${etape.nomEtape}</div>
                    </button>
                </c:forEach>
            </div>
        </div>

        <div class="form-card">
            <h2 style="color: #2c3e50; margin-top:0;">Saisie : ${currentEtape.nomEtape} (Étape ${numEtapeActive})</h2>
            <p style="color: #7f8c8d; margin-bottom: 20px;">Veuillez renseigner les informations requises pour ce service avant de valider.</p>

            <form action="valider-etape" method="post">
                <input type="hidden" name="id_workflow" value="${workflow.id}">
                <input type="hidden" name="ordre_etape" value="${currentEtape.attentePlace}">

                <div class="grid-form">
                    <div class="form-group full">
                        <label>Observations / Notes pour le service associé</label>
                        <textarea name="commentaire" rows="4" class="form-control" placeholder="Ajouter une remarque textuelle..."></textarea>
                    </div>
                </div>
                <div class="button-group" style="margin-top:20px; text-align:right;">
                    <button type="submit" class="btn btn-submit" style="background:#27ae60; color:white; padding:12px 25px; border:none; border-radius:4px; font-weight:bold; cursor:pointer;">
                        Enregistrer et Valider l'étape ${currentEtape.attentePlace}
                    </button>
                </div>
            </form>
        </div>

    </div>

</body>
</html>