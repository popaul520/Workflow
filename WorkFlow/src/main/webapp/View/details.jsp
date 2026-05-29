<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>
<%@ page import="model.Utilisateur" %>
<%@ page import="dao.ValidationDAO" %>
<%@ page import="java.util.List"%>
<%@ page import="java.util.ArrayList" %>

<%
    model.Workflow wf = (model.Workflow) request.getAttribute("wf");
    Utilisateur user = (Utilisateur) session.getAttribute("user");

    dao.ValidationDAO valDao = new dao.ValidationDAO();
    int etapeMax = 0;
    boolean bloc1a6Complet = false;
    List<Integer> etapesValidees = new ArrayList<>();
    int etapeMaxPost6 = 0;

    if (wf != null) {
        try {
            etapeMax = valDao.getDerniereEtapeValidee(wf.getId());
            bloc1a6Complet = valDao.sontEtapes1a6Validees(wf.getId());
            etapesValidees = valDao.getListeEtapesValidees(wf.getId());
            for(int e : etapesValidees) {
                if(e > etapeMaxPost6) etapeMaxPost6 = e;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }  
    }
    
    boolean isFinalise = (wf != null && wf.getDateFinalisation() != null);
    
    dao.DonneeDAO dDao = new dao.DonneeDAO();
    List<model.Donnee> dataE1 = dDao.getDonneesByEtape(wf.getId(), 1);
    List<model.Donnee> dataE7 = dDao.getDonneesByEtape(wf.getId(), 7);
    List<model.Donnee> dataE10 = dDao.getDonneesByEtape(wf.getId(), 10);

    List<model.Donnee> tousLesAvisPrecedents = new ArrayList<>();
    if (wf != null) {
        for (int i = 2; i <= 7; i++) {
            List<model.Donnee> dataEtape = dDao.getDonneesByEtape(wf.getId(), i);
            for (model.Donnee d : dataEtape) {
                if (d.getType() != null && d.getType().toLowerCase().contains("avis")) {
                    tousLesAvisPrecedents.add(d);
                }
            }
        }
    }
    request.setAttribute("avisPrecedents", tousLesAvisPrecedents);
%>
<style>
    .grid-boutons {
        display: grid;
        grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
        gap: 12px;
        margin: 20px 0;
    }
    .btn-etape {
        border: none;
        border-radius: 6px;
        padding: 12px;
        color: white !important;
        text-align: center;
        cursor: pointer;
        min-height: 70px;
        display: flex;
        flex-direction: column;
        justify-content: center;
        transition: all 0.2s;
    }
    .state-locked { background-color: #bdc3c7 !important; cursor: not-allowed; opacity: 0.8; box-shadow: none !important; }
    .state-validated { background-color: #2ecc71 !important; }
    .state-modifiable { background-color: #3498db !important; box-shadow: 0 4px 6px rgba(52, 152, 219, 0.3); }
    .btn-etape strong { display: block; font-size: 0.9em; margin-top: 5px; }

    .panneau-avis-consultation {
        display: none; 
        background: #f8f9fa;
        border-left: 5px solid #6f42c1;
        padding: 15px;
        margin-bottom: 20px;
        border-radius: 4px;
        box-shadow: inset 0 1px 3px rgba(0,0,0,0.05);
    }
    
    .table-avis-mini, .table-recap {
        width: 100%;
        border-collapse: collapse;
        margin-top: 10px;
        font-size: 14px;
        table-layout: fixed;
    }
    .table-avis-mini th, .table-recap th {
        text-align: left;
        color: #718093;
        padding-bottom: 8px;
        border-bottom: 1px solid #dcdde1;
    }
    .table-avis-mini td, .table-recap td {
        padding: 10px 8px;
        border-bottom: 1px solid #f1f2f6;
        vertical-align: top;
        white-space: normal;
        overflow-wrap: break-word;
        word-wrap: break-word;
        word-break: break-word; 
    }
    .col-xs { width: 10%; }
    .col-sm { width: 20%; }
    .col-md { width: 30%; }
    .col-lg { width: 40%; }

    .badge-avis {
        padding: 3px 8px;
        border-radius: 4px;
        font-weight: bold;
        font-size: 11px;
        color: white;
        display: inline-block;
    }
    .badge-faisable { background-color: #2ecc71; }
    .badge-nonfaisable { background-color: #e74c3c; }
    .badge-reserve { background-color: #f39c12; }
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
                    <div class="value" style="word-break: break-word;">${empty wf.commentaire ? 'Aucun' : wf.commentaire}</div>
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
                <c:forEach var="i" begin="2" end="10">
                    <%
                        int iValue = (Integer) pageContext.getAttribute("i");
                        boolean isValidated = etapesValidees.contains(iValue);
                        boolean isLocked = false;
                        
                        //  Si le dossier est clos, on ne verrouille que les étapes qui n'ont pas été faites.
                        if (isFinalise) {
                            isLocked = !isValidated; 
                        } else {
                            // Logique normale si le dossier est toujours actif
                            if (iValue >= 2 && iValue <= 6) {
                                isLocked = false; 
                            } else if (iValue == 7) {
                                int count1to6 = 0;
                                for(int e : etapesValidees) if(e >= 1 && e <= 6) count1to6++;
                                isLocked = (count1to6 < 6);
                            } else {
                                isLocked = ((iValue > etapeMaxPost6 + 1) || ("Non faisable".equalsIgnoreCase(dDao.getValeurAttribut(wf.getId(), 7, "Avis D.O.P."))));
                            }
                        }
                        
                        String cssClass = "btn-etape";
                        if (isLocked) {
                            cssClass += " state-locked";
                        } else if (isValidated) {
                            cssClass += " state-validated";
                        } else {
                            cssClass += " state-modifiable";
                        }
                    %>
                    <button type="button" 
                            <%= isLocked ? "disabled='disabled'" : "" %>
                            onclick="chargerEtape(<%= iValue %>, <%= wf.getId() %>)"
                            class="<%= cssClass %>">
                        <div class="step-number">ÉTAPE <%= iValue %></div>
                        <div class="step-role"><%= model.Utilisateur.getRole(iValue) %></div>
                    </button>
                </c:forEach>
            </div>
        </div>

        <div id="affichage-dynamique-etape">
            
            <div id="panneau-avis-recaps" class="panneau-avis-consultation">
                <h4 style="margin: 0 0 10px 0; color: #6f42c1; font-size: 15px;">Synthèse des Avis Recueillis (Avis Commerciaux & Techniques)</h4>
                <table class="table-avis-mini">
                    <thead>
                        <tr>
                            <th class="col-md">Entité / Étape</th>
                            <th class="col-sm">Avis émis</th>
                            <th class="col-lg">Note / Commentaire</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="avis" items="${avisPrecedents}">
                            <tr>
                                <td><strong>${avis.type}</strong> <span style="font-size:11px; color:#95a5a6;">(Étape ${avis.etape.nbEtape})</span></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${avis.attribut == 'Faisable' || avis.attribut == 'Favorable' || avis.attribut == 'Oui'}">
                                            <span class="badge-avis badge-faisable">${avis.attribut}</span>
                                        </c:when>
                                        <c:when test="${avis.attribut == 'Non faisable' || avis.attribut == 'Défavorable' || avis.attribut == 'Non'}">
                                            <span class="badge-avis badge-nonfaisable">${avis.attribut}</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="badge-avis badge-reserve">${avis.attribut}</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td><span style="color:#555; font-style:italic;">${empty avis.commentaire ? 'Aucune observation' : avis.commentaire}</span></td>
                            </tr>
                        </c:forEach>
                        <c:if test="${empty avisPrecedents}">
                            <tr>
                                <td colspan="3" style="color:#7f8c8d; font-style:italic;">Aucun avis n'a encore été formalisé en base.</td>
                            </tr>
                        </c:if>
                    </tbody>
                </table>
            </div>

            <%--On laisse le bloc de détails accessible dans tous les cas pour afficher les étapes validées --%>
            <h3 id="titre-etape" style="color: var(--primary); margin-top: 0;">Détails de l'étape</h3>
            <div id="contenu-etape"></div>
        </div>

        <div class="visualisation-donnees" style="margin-top: 50px;">
            <h3 style="border-left: 5px solid var(--accent); padding-left: 15px;">Récapitulatif des données saisies</h3>
            <c:choose>
                <c:when test="${not empty historique}">
                    <table class="table-recap">
                        <thead>
                            <tr>
                                <th class="col-xs">Étape</th>
                                <th class="col-md">Type / Libellé</th>
                                <th class="col-lg">Valeur saisie</th>
                                <th class="col-sm">Date de saisie</th>
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
                Télécharger le récapitulatif PDF
            </a>
        </div>
    </div>

    <script>
        let etapeOuverte = null;

        function chargerEtape(n, idWf, forceSaisie = false) {
            const zone = document.getElementById('affichage-dynamique-etape');
            const contenu = document.getElementById('contenu-etape');
            const panneauAvis = document.getElementById('panneau-avis-recaps');

            if (etapeOuverte === n && zone.style.display === 'block' && !forceSaisie) {
                zone.style.display = 'none';
                panneauAvis.style.display = 'none';
                etapeOuverte = null;
                return;
            }

            zone.style.display = 'block';
            
            if (n === 7 || n === 10) {
                panneauAvis.style.display = 'block';
            } else {
                panneauAvis.style.display = 'none';
            }

            if(contenu) {
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
        }

        function activerEdition() {
            // L'édition ne s'active pas du tout si le dossier est clos
            if (<%= isFinalise %>) return;
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