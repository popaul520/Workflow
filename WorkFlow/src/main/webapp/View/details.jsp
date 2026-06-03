<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ page import="model.Utilisateur" %>
<%@ page import="dao.ValidationDAO" %>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList" %>


<%
    // Récupération du Workflow depuis la requête (envoyé par ton WorkflowController/DetailsController)
    model.Workflow wf = (model.Workflow) request.getAttribute("wf");
	Utilisateur user = (Utilisateur) session.getAttribute("user");

    // Récupération de l'étape max validée (envoyée par le contrôleur ou calculée ici)
    // Si ton contrôleur ne l'envoie pas, on peut appeler le DAO directement
    dao.ValidationDAO valDao = new dao.ValidationDAO();
    int etapeMax = 0;
    
    if (wf != null) {
        try {
            etapeMax = valDao.getDerniereEtapeValidee(wf.getId());
        } catch (Exception e) {
            e.printStackTrace();
        }  
    }
    
    boolean bloc1a6Complet = false;
    
    if (wf != null) {
        etapeMax = valDao.getDerniereEtapeValidee(wf.getId());
        bloc1a6Complet = valDao.sontEtapes1a6Validees(wf.getId());
    }
    
    List<Integer> etapesValidees = new ArrayList<>();
    int etapeMaxPost6 = 0; // Pour gérer la suite (7, 8, 9, 10)

    if (wf != null) {
        etapesValidees = valDao.getListeEtapesValidees(wf.getId());
        // On cherche l'étape max uniquement pour la phase séquentielle (>=7)
        for(int e : etapesValidees) {
            if(e > etapeMaxPost6) etapeMaxPost6 = e;
        }
    }
    
	boolean isFinalise = (wf != null && wf.getDateFinalisation() != null);
    
    // On récupère les données clés pour l'affichage en haut
    dao.DonneeDAO dDao = new dao.DonneeDAO();
    // Étape 1 : Libellé/Début
    List<model.Donnee> dataE1 = dDao.getDonneesByEtape(wf.getId(), 1);
    // Étape 7 ou 10 : Verdict
    List<model.Donnee> dataE7 = dDao.getDonneesByEtape(wf.getId(), 7);
    List<model.Donnee> dataE10 = dDao.getDonneesByEtape(wf.getId(), 10);
%>
<style>
    /* Structure de la grille */
    .grid-boutons {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
        gap: 12px;
        margin: 20px 0;
    }

    /* Style de base des boutons (on enlève les styles par défaut) */
    .btn-etape {
        border: none;
        border-radius: 6px;
        padding: 12px;
        color: white !important; /* Texte toujours blanc */
        text-align: center;
        cursor: pointer;
        min-height: 70px;
        display: flex;
        flex-direction: column;
        justify-content: center;
        transition: all 0.2s;
    }

    /* ÉTAT : GRIS (Bloqué) */
    .state-locked {
        background-color: #bdc3c7 !important; 
        cursor: not-allowed;
        opacity: 0.8;
    }

    /* ÉTAT : VERT (Déjà validé) */
    .state-validated {
        background-color: #2ecc71 !important;
    }

    /* ÉTAT : BLEU (Modifiable / Prochaine étape) */
    .state-modifiable {
        background-color: #3498db !important;
        box-shadow: 0 4px 6px rgba(52, 152, 219, 0.3);
    }

    .btn-etape strong {
        display: block;
        font-size: 0.9em;
        margin-top: 5px;
    }
</style>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Détails Workflow #${wf.id}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    

</head>
<body>

    <div class="sidebar">
        <h3>Actions</h3>
        <ul>
            <li><a href="home">🏠 Retour Accueil</a></li>
            
             <%-- a faire comme page modif / .... pour rendu propre  --%>
            
            <li><a href="workflow?action=edit&id=${wf.id}">📝 Modifier le titre</a></li>
            <li><a href="workflow?action=delete&id=${wf.id}" style="color: var(--danger);">🗑️ Supprimer</a></li>
            
        </ul>
    </div>

    <div class="main-container">
        
        <div class="header">
            <h1>Dossier Workflow <span class="status-badge">ID #${wf.id}</span></h1>
        </div>

        <div class="detail-card">
            <h2 style="color: var(--accent); margin-top: 0;">${wf.titre}</h2>
            <hr style="border: 0; border-top: 1px solid #eee; margin: 20px 0;">
            <div class="grid-info">
                <div class="info-group">
                    <div class="label">Créateur</div>
                    <div class="value">ID User: ${wf.idUtilisateur}</div>
                </div>
                <div class="info-group">
                    <div class="label">Date Création</div>
                    <div class="value"><fmt:formatDate value="${wf.dateCreation}" pattern="dd/MM/yyyy" /></div>
                </div>
                <div class="info-group">
                    <div class="label">Commentaire</div>
                    <div class="value">${empty wf.commentaire ? 'Aucun' : wf.commentaire}</div>
                </div>
            </div>
        </div>
        
		<div class="header-workflow">		    
		    <div class="info-step-summary">
		        <c:forEach var="d" items="<%= dataE1 %>">
		            <p><strong>${d.type} :</strong> ${d.attribut}</p>
		        </c:forEach>
		    </div>
		    <c:if test="<%= isFinalise %>">
		        <div class="status-banner <%= etapeMax < 10 ? "status-rejete" : "status-valide" %>">
		            <h3>⚠️ DOSSIER CLÔTURÉ LE <fmt:formatDate value="${wf.dateFinalisation}" pattern="dd/MM/yyyy"/></h3>
		            
		            <%-- On affiche l'avis de l'étape 7 ou 10 --%>
		            <c:set var="finalData" value="<%= !dataE10.isEmpty() ? dataE10 : dataE7 %>" />
		            <c:forEach var="d" items="${finalData}">
		                <c:if test="${d.refTypeContraint == 'avis'}">
		                    <p><strong>VERDICT FINAL :</strong> ${d.attribut}</p>
		                    <p><em>"${d.commentaire}"</em></p>
		                </c:if>
		            </c:forEach>
		        </div>
		    </c:if>
		</div>

        <div class="navigation-etapes">
            <h3 style="border-left: 5px solid var(--success); padding-left: 15px;">Suivi des services</h3>
            


<div class="grid-boutons">
    <c:forEach var="i" begin="1" end="10">
        <%
            int iValue = (Integer) pageContext.getAttribute("i");
            
            boolean isValidated = etapesValidees.contains(iValue);
            boolean isLocked = false;
            
            if (iValue >= 1 && iValue <= 6) {
                isLocked = false; 
            } else if (iValue == 7) {
                int count1to6 = 0;
                for(int e : etapesValidees) if(e >= 1 && e <= 6) count1to6++;
                isLocked = (count1to6 < 6);
            } else {
            	System.out.println(wf.getId());
            	isLocked = ((iValue > etapeMaxPost6 + 1) || ("Non faisable".equalsIgnoreCase( dDao.getValeurAttribut(wf.getId(), 7, "Avis D.O.P."))));
            }
            
            // Détermination de la classe CSS
            String cssClass = "btn-etape";
            if (isLocked) {
                cssClass += " state-locked";      // Gris
            } else if (isValidated) {
                cssClass += " state-validated";   // Vert
            } else {
                cssClass += " state-modifiable";  // Bleu
            }
        %>
        	<button type="button" 
        	    onclick="<%= isLocked ? "" : "chargerEtape(" + iValue + "," + wf.getId() + ")" %>"
                class="<%= cssClass %>">
        	<div class="step-number">ÉTAPE <%= iValue %></div>
            <div class="step-role"><%= model.Utilisateur.getRole(iValue) %></div>
        </button>
    </c:forEach>
</div>
        </div>
        <div id="affichage-dynamique-etape">
            <h3 id="titre-etape" style="color: var(--primary); margin-top: 0;">Détails de l'étape</h3>
            <div id="contenu-etape">
                </div>
        </div>

        <div class="visualisation-donnees" style="margin-top: 50px;">
            <h3 style="border-left: 5px solid var(--accent); padding-left: 15px;">Récapitulatif des données saisies</h3>
            <c:choose>
                <c:when test="${not empty historique}">
                    <table class="table-recap">
                        <thead>
                            <tr>
                                <th>Étape</th>
                                <th>Type / Libellé</th>
                                <th>Valeur saisie</th>
                                <th>Date de saisie</th>
                            </tr>
                        </thead>
                        <tbody>
                            <c:forEach var="d" items="${historique}">
                                <tr>
                                    <td><strong>#${d.etape.nbEtape}</strong></td>
                                    <td>${d.type}</td>
                                    <td>${d.attribut}</td>
                                    <td><fmt:formatDate value="${d.date}" pattern="dd/MM/yyyy" /></td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </c:when>
                <c:otherwise>
                    <p style="background: #fff3cd; padding: 15px; border-radius: 8px; color: #856404;">Aucune donnée enregistrée pour le moment.</p>
                </c:otherwise>
            </c:choose>
            
            <a href="${pageContext.request.contextPath}/downloadPdf?id=${wf.id}" class="btn-pdf">
                📄 Télécharger le récapitulatif PDF
            </a>
        </div>
    </div>

    <script>
        let etapeOuverte = null;

        function chargerEtape(n, idWf, forceSaisie = false) {
            const zone = document.getElementById('affichage-dynamique-etape');
            const contenu = document.getElementById('contenu-etape');

            if (etapeOuverte === n && zone.style.display === 'block' && !forceSaisie) {
                zone.style.display = 'none';
                etapeOuverte = null;
                return;
            }

            zone.style.display = 'block';
            contenu.innerHTML = "<p>Chargement des données en cours...</p>";
            etapeOuverte = n;

            let url = '${pageContext.request.contextPath}/etapeController?n=' + n + '&id_workflow=' + idWf;
            if(forceSaisie) url += '&mode=edit';

            fetch(url)
            .then(response => response.text())
            .then(html => {
                const parser = new DOMParser();
                const doc = parser.parseFromString(html, 'text/html');
                const formRecu = doc.querySelector('form');
                contenu.innerHTML = formRecu ? formRecu.outerHTML : doc.body.innerHTML;
               
            });
        }

        function activerEdition() {
            const container = document.getElementById('affichage-dynamique-etape');
            if (container) {
                container.querySelectorAll('.view-mode').forEach(el => el.style.display = 'none');
                container.querySelectorAll('.edit-mode').forEach(el => el.style.display = 'block');
                const btnModif = container.querySelector('#btn-modifier');
                const btnSave = container.querySelector('#btn-enregistrer');
                if(btnModif) btnModif.style.display = 'none';
                if(btnSave) btnSave.style.display = 'inline-block';
            }
        }
    </script>
</body>
</html>