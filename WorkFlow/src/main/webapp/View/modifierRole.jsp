<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Modifier Accès : ${roleName}</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .delete-btn {
            background: #fff5f5;
            color: #c53030;
            border: 1px solid #feb2b2;
            padding: 5px 12px;
            border-radius: 4px;
            cursor: pointer;
            font-weight: bold;
            transition: 0.2s;
        }
        .delete-btn:hover { background: #c53030; color: white; }
        
        .add-box {
            margin-top: 30px;
            padding-top: 20px;
            border-top: 2px dashed #edf2f7;
        }
        .input-number {
            padding: 8px;
            border: 1px solid #cbd5e0;
            border-radius: 4px;
            width: 80px;
        }
    </style>
</head>
<body>

    <div class="sidebar">
        <h3>Workflow</h3>
        <ul>
            <li><a href="home">🏠 Accueil</a></li>
            <li class="active"><a href="admin-roles">⚙️ Gestion Rôles</a></li>
        </ul>
    </div>

    <div class="main-container">
        <div class="header-flex">
            <h1>Détails des accès : <span style="color: var(--accent);">${roleName}</span></h1>
            <a href="admin-roles" class="btn-view" style="border-color: #718096; color: #718096;">← Retour</a>
        </div>

        <div class="box">
            <h3>Étapes actuellement configurées</h3>
            <p style="color: #718096; font-size: 0.9em; margin-bottom: 20px;">Ce rôle peut voir et traiter les dossiers arrivant aux étapes listées ci-dessous.</p>
            
            <table style="max-width: 500px;">
                <c:choose>
                    <c:when test="${empty roleEtapes}">
                        <tr><td colspan="2" style="text-align: center; padding: 20px; color: #a0aec0;">Aucune étape assignée.</td></tr>
                    </c:when>
                    <c:otherwise>
                       <c:forEach var="etape" items="${roleEtapes}">
						    <tr>
						        <td><strong>Étape ${etape}</strong></td>
						        <td>
						            <%-- On cherche le nom dans la map grace à l'ID (l'étape) --%>
						            <span class="badge-role">${allRolesMap[etape]}</span>
						        </td>
						        <td style="text-align: right;">
						            <form action="admin-roles" method="post" style="display:inline;">
						                <input type="hidden" name="dbAction" value="delete">
						                <input type="hidden" name="roleId" value="${roleId}">
						                <input type="hidden" name="etape" value="${etape}">
						                <button type="submit" class="delete-btn">Supprimer</button>
						            </form>
						        </td>
						    </tr>
						</c:forEach>
                    </c:otherwise>
                </c:choose>
            </table>

			<div class="add-box">
			    <h3>Ajouter un nouvel accès</h3>
			    
			    <c:choose>
			        <c:when test="${not empty etapesDisponibles}">
			            <form action="admin-roles" method="post" style="display: flex; align-items: center; gap: 15px;">
			                <input type="hidden" name="dbAction" value="add">
			                <input type="hidden" name="roleId" value="${roleId}">
			                <input type="hidden" name="roleName" value="${roleName}">
			                
			                <label for="etape">Choisir une étape :</label>
							<select name="etape" id="etape" required class="input-number" style="width: auto; min-width: 250px;">
							    <option value="" disabled selected>Choisir une étape à ajouter...</option>
							    <c:forEach var="num" items="${etapesDisponibles}">
							        <option value="${num}">Étape ${num} - ${allRolesMap[num]}</option>
							    </c:forEach>
							</select>
			                
			                <button type="submit" class="profile-btn" style="border: none; cursor: pointer;">Confirmer l'ajout</button>
			            </form>
			        </c:when>
			        <c:otherwise>
			            <p style="color: #48bb78; font-weight: bold;">✅ Ce rôle possède déjà tous les accès (étapes 1 à 10).</p>
			        </c:otherwise>
			    </c:choose>
			</div>
        </div>
    </div>
    
</body>
</html>