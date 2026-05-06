<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*, model.Workflow" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">

<head>
    <meta charset="UTF-8">
    <title>Accueil Workflow</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    
    <style>
        /* CSS UNIQUE à la page d'accueil */
        .header-flex { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
        
        .search-bar { 
            padding: 10px 15px; 
            border: 1px solid #ddd; 
            border-radius: 20px; 
            width: 300px; 
            outline: none;
        }
        .user-controls { display: flex; align-items: center; gap: 15px; }
        .profile-btn { 
            background-color: var(--success); 
            color: white; 
            padding: 8px 20px; 
            border-radius: 5px; 
            text-decoration: none; 
            font-weight: bold;
            font-size: 0.9em;
        }

        .login-btn { background-color: var(--accent); }

        .logout-link { color: #e74c3c; text-decoration: none; font-size: 0.85em; font-weight: bold; }

        /* Style de la table */
        .box { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); margin-bottom: 30px; }
        
        table { width: 100%; border-collapse: collapse; margin-top: 10px; }
        th { text-align: left; background: #f8f9fa; padding: 12px; border-bottom: 2px solid #eee; color: var(--primary); }
        td { padding: 12px; border-bottom: 1px solid #eee; vertical-align: middle; }
        
        .btn-view { 
            color: var(--accent); 
            text-decoration: none; 
            font-weight: bold; 
            border: 1px solid var(--accent); 
            padding: 5px 12px; 
            border-radius: 4px; 
            transition: 0.2s;
        }
        .btn-view:hover { background: var(--accent); color: white; }

        h2 { color: var(--primary); border-left: 5px solid var(--accent); padding-left: 15px; margin-bottom: 20px; }
    </style>
</head> fskfpsosfp s
 

<body>

    <div class="sidebar">
        <h3>Workflow</h3>
        <ul>
            <li><a href="home" style="color: white;">🏠 Accueil</a></li>
            <li><a href="#">⏳ En cours</a></li>
            <li><a href="#">✅ Terminé</a></li>
            <li><a href="#">❌ Annulé</a></li>
            <li style="margin-top: 30px;"><a href="creer-workflow" style="color: var(--success); font-weight: bold;">➕ Créer Workflow</a></li>
        </ul>
    </div>

    <div class="main-container">
        
        <div class="header-flex">
            <h1>Accueil WorkFlow</h1>
            
            <div class="user-controls">
				<form action="search" method="get">
				    <input type="text" name="q" class="search-bar" placeholder="ID ou Nom du dossier..." value="<%= request.getParameter("q") != null ? request.getParameter("q") : "" %>">
				</form>
                <% if (session.getAttribute("user") != null) { %>
                    <a href="profil" class="profile-btn">Mon Profil</a>
                    <a href="logout" class="logout-link">Déconnexion</a>
                <% } else { %>
                    <a href="login" class="profile-btn login-btn">Connexion</a>
                <% } %>
            </div>
        </div>

        <h2>Dossiers en cours</h2>
        <div class="box">
            <table>
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Titre</th>
                        <th>Date</th>
                        <th>Demandeur</th>
                        <th>Progression</th>
                        <th>Action</th>
                    </tr>
                </thead>
<tbody>
    <% 
        List<model.Workflow> list = (List<model.Workflow>) request.getAttribute("workflows");
        dao.ValidationDAO vDao = new dao.ValidationDAO(); 
        dao.DonneeDAO dDao = new dao.DonneeDAO(); // Nécessaire pour lire l'avis
        
        if(list != null && !list.isEmpty()) {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd/MM/yyyy");
            
            for(model.Workflow wf : list) {
                int etape = 0;
                try { etape = vDao.getDerniereEtapeValidee(wf.getId()) ; } catch(Exception e) { etape = 1; }

                String badgeBg = "#ebf8ff"; // Défaut Bleu
                String badgeText = "#2c5282";
                String libelleEtape = "Étape " + etape + "/9";
                // LOGIQUE DE DÉCISION (Étape 10 ou Étape 7)
                if (etape == 10 || (wf.getDateFinalisation() != null && etape == 7)) {
                    // On récupère l'avis (DCD à l'étape 10, sinon DOP à l'étape 7)
                    int etapeDecision = (etape == 10) ? 10 : 7; 
                    System.out.println(etapeDecision);
                    String avis = dDao.getValeurAttribut(wf.getId(), etapeDecision, "Avis D.O.P."); 
                    if(avis == ""){
                        avis = dDao.getValeurAttribut(wf.getId(), etapeDecision, "Avis D.C.D."); 
                    }

                    System.out.println(avis);
                    if ("faisable".equalsIgnoreCase(avis)) {
                        badgeBg = "#c6f6d5"; // VERT
                        badgeText = "#22543d";
                        libelleEtape = (etape == 10) ? "Terminé" : "Faisable";
                    } 
                    else if ("Faisable sous condition".equalsIgnoreCase(avis)) {
                        badgeBg = "#feebc8"; // ORANGE
                        badgeText = "#744210";
                        libelleEtape = "Faisable s.c.";
                    } 
                    
                    else if ("Non faisable".equalsIgnoreCase(avis)) {
                        badgeBg = "#fed7d7"; // ROUGE
                        badgeText = "#822727";
                        libelleEtape = (etape == 10) ? "Refusé" : "Non Faisable";
                    }
                } 
                // --- CAS EN COURS (Pas encore de décision finale) ---
                else if (etape >= 7) {
                    badgeBg = "#e2e8f0"; // Gris bleuté pour "En cours de décision"
                    badgeText = "#4a5568";
                    libelleEtape = "Décision...";
                }

    %>
        <tr>
            <td><strong>#<%= wf.getId() %></strong></td>
            <td><%= wf.getTitre() %></td>
            <td>
                <span style="color: #666; font-size: 0.9em;">
                    <%= (wf.getDateCreation() != null) ? sdf.format(wf.getDateCreation()) : "N/C" %>
                </span>
            </td>
            <td><span style="color: #888; font-size: 0.9em;">Admin</span></td>
            <td>
                <span style="font-size: 0.85em; background: <%= badgeBg %>; color: <%= badgeText %>; padding: 4px 12px; border-radius: 12px; font-weight: bold; display: inline-block; min-width: 100px; text-align: center; border: 1px solid rgba(0,0,0,0.05);">
                    <%= libelleEtape %>
                </span>
            </td>
            <td><a href="details?id=<%= wf.getId() %>" class="btn-view">Voir</a></td>
        </tr>
    <%
            }
        } else { 
    %>
        <tr><td colspan="6" style="text-align: center; padding: 30px; color: #999;">Aucun workflow en cours.</td></tr>
    <% } %>
</tbody>
            </table>
        </div>

<h2>⚠️ En attente de votre action</h2>
<div class="box">
    <table style="border-left: 5px solid #e74c3c;">
        <thead>
            <tr style="background: #fff5f5;">
                <th>ID</th>
                <th>Titre</th>
                <th>Étape à traiter</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
            <%-- On récupère la liste préparée par le contrôleur --%>
            <c:choose>
                <c:when test="${not empty pendingList}">
                    <c:forEach var="wfAction" items="${pendingList}">
                        <tr>
                            <td><strong>#${wfAction.id}</strong></td>
                            <td>${wfAction.titre}</td>
                            <td>
                                <span class="badge-urgent" style="background: #fed7d7; color: #822727; padding: 5px 10px; border-radius: 15px; font-weight: bold; font-size: 0.8em;">
                                    ÉTAPE SUIVANTE
                                </span>
                            </td>
                            <td>
                                <a href="details?id=${wfAction.id}" class="btn-view" style="background: #e74c3c; color: white; border: none;">
                                    Compléter
                                </a>
                            </td>
                        </tr>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <tr>
                        <td colspan="4" style="text-align: center; padding: 20px; color: #999;">
                            ✅ Aucune action requise pour votre rôle (${sessionScope.user.role}).
                        </td>
                    </tr>
                </c:otherwise>
            </c:choose>
        </tbody>
    </table>
</div>
    <div class="sidebar">
    <h3>Workflow</h3>
    <ul>
        <li class="${currentStatus == 'tous' ? 'active' : ''}">
            <a href="home?status=tous">🏠 Tous les dossiers</a>
        </li>
        <li class="${currentStatus == 'en_cours' ? 'active' : ''}">
            <a href="home?status=en_cours">⏳ En cours</a>
        </li>
        <li class="${currentStatus == 'termine' ? 'active' : ''}">
            <a href="home?status=termine">✅ Terminé</a>
        </li>
        <li class="${currentStatus == 'annule' ? 'active' : ''}">
            <a href="home?status=annule">❌ Annulé</a>
        </li>
    </ul>
</div>
</body>
</html>