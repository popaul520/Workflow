<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt" %>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Accueil Workflow</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .header-flex { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
        .search-bar { padding: 10px 15px; border: 1px solid #ddd; border-radius: 20px; width: 300px; outline: none; }
        .user-controls { display: flex; align-items: center; gap: 15px; }
        .profile-btn { background-color: var(--success); color: white; padding: 8px 20px; border-radius: 5px; text-decoration: none; font-weight: bold; font-size: 0.9em; }
        .login-btn { background-color: var(--accent); }
        .logout-link { color: #e74c3c; text-decoration: none; font-size: 0.85em; font-weight: bold; }
        .box { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.05); margin-bottom: 30px; }
        table { width: 100%; border-collapse: collapse; margin-top: 10px; }
        th { text-align: left; background: #f8f9fa; padding: 12px; border-bottom: 2px solid #eee; color: var(--primary); }
        td { padding: 12px; border-bottom: 1px solid #eee; vertical-align: middle; }
        .btn-view { color: var(--accent); text-decoration: none; font-weight: bold; border: 1px solid var(--accent); padding: 5px 12px; border-radius: 4px; transition: 0.2s; }
        .btn-view:hover { background: var(--accent); color: white; }
        h2 { color: var(--primary); border-left: 5px solid var(--accent); padding-left: 15px; margin-bottom: 20px; }
        .active a { font-weight: bold; color: var(--accent) !important; }
    </style>
</head>
<body>
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

            <%-- Accès dministrateur dynamique --%>
            <c:if test="${roleDAO.canAccessEtape(user.role, 11)}">
                <li style="background: #2d3748; margin-top: 10px; border-radius: 4px;">
                    <a href="admin-roles" style="color: #63b3ed; font-weight: bold;">⚙️ Gestion rôle</a>
                </li>
            </c:if>
            
            <c:if test="${roleDAO.canAccessEtape(user.role, 1) || roleDAO.canAccessEtape(user.role, 11)}">
                <li style="margin-top: 30px;">
                    <a href="creer-workflow" style="color: var(--success); font-weight: bold;">➕ Créer Workflow</a>
                </li>
            </c:if>
        </ul>
    </div>

    <div class="main-container">
        <div class="header-flex">
            <h1>Accueil WorkFlow</h1>
            <div class="user-controls">
                <form action="home" method="get">
                    <input type="text" name="q" class="search-bar" placeholder="ID ou Nom du dossier..." value="${param.q}">
                </form>
				<c:choose>
				    <c:when test="${not empty sessionScope.user}">
				        <a href="profil" class="profile-btn">Mon Profil</a>
				        <a href="logout" class="logout-link">Déconnexion</a>
				    </c:when>
				    <c:otherwise>
				        <a href="login" class="profile-btn login-btn">Connexion</a>
				    </c:otherwise>
				</c:choose>
            </div>
        </div>
        <h2>
            <c:choose>
                <c:when test="${currentStatus == 'en_cours'}">Dossiers en cours</c:when>
                <c:when test="${currentStatus == 'termine'}">Dossiers terminés</c:when>
                <c:when test="${currentStatus == 'annule'}">Dossiers annulés</c:when>
                <c:otherwise>Tous les dossiers</c:otherwise>
            </c:choose>
        </h2>

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
                    <c:choose>
                        <c:when test="${not empty workflows}">
                            <c:forEach var="wd" items="${workflows}">
                                <tr>
                                    <td><strong>#${wd.workflow.id}</strong></td>
                                    <td>${wd.workflow.titre}</td>
                                    <td>
                                        <span style="color: #666; font-size: 0.9em;">
                                            <c:choose>
                                                <c:when test="${not empty wd.workflow.dateCreation}">
                                                    <fmt:formatDate value="${wd.workflow.dateCreation}" pattern="dd/MM/yyyy"/>
                                                </c:when>
                                                <c:otherwise>N/C</c:otherwise>
                                            </c:choose>
                                        </span>
                                    </td>
                                    <td><span style="color: #888; font-size: 0.9em;">${wd.nomDemandeur}</span></td>
                                    <td>
                                        <span style="font-size: 0.85em; background: ${wd.badgeBg}; color: ${wd.badgeText}; padding: 4px 12px; border-radius: 12px; font-weight: bold; display: inline-block; min-width: 100px; text-align: center; border: 1px solid rgba(0,0,0,0.05);">
                                            ${wd.libelleEtape}
                                        </span>
                                    </td>
                                    <td><a href="details?id=${wd.workflow.id}" class="btn-view">Voir</a></td>
                                </tr> 
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                            <tr><td colspan="6" style="text-align: center; padding: 30px; color: #999;">Aucun workflow trouvé.</td></tr>
                        </c:otherwise>
                    </c:choose>
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
                                     Aucune action requise pour votre rôle (${sessionScope.user.role}).
                                </td>
                            </tr>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>