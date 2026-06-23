<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Administration des Rôles</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .table-admin {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
            background: white;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 4px 12px rgba(0,0,0,0.08);
        }
        .table-admin th, .table-admin td {
            padding: 14px 18px;
            text-align: left;
            border-bottom: 1px solid #edf2f7;
            vertical-align: middle;
        }
        .table-admin th {
            background-color: #2c3e50;
            color: white;
            font-weight: 600;
            text-transform: uppercase;
            font-size: 13px;
            letter-spacing: 0.5px;
        }
        .table-admin tr:hover {
            background-color: #f8f9fa;
        }
        .select-role {
            padding: 8px 12px;
            border: 1px solid #cbd5e0;
            border-radius: 5px;
            background-color: #fff;
            font-family: inherit;
            color: #2d3748;
            width: 200px;
            font-weight: 500;
        }
        .btn-sauvegarder {
            background-color: #2ecc71;
            color: white;
            border: none;
            padding: 8px 16px;
            border-radius: 5px;
            cursor: pointer;
            font-weight: bold;
            font-size: 13px;
            transition: background 0.2s;
        }
        .btn-sauvegarder:hover {
            background-color: #27ae60;
        }
        .badge-role {
            background-color: #e2e8f0;
            color: #4a5568;
            padding: 4px 10px;
            border-radius: 20px;
            font-size: 12px;
            font-weight: bold;
            display: inline-block;
        }
        .alert {
            padding: 12px 18px;
            border-radius: 6px;
            margin-bottom: 22px;
            font-weight: bold;
            font-size: 14px;
        }
        .alert-success { background-color: #d4edda; color: #155724; border: 1px solid #c3e6cb; }
        .alert-danger { background-color: #f8d7da; color: #721c24; border: 1px solid #f5c6cb; }
    </style>
</head>
<body>

    <div class="sidebar">
        <h3>Actions</h3>
        <ul>
            <li><a href="${pageContext.request.contextPath}/home">🏠 Retour Accueil</a></li>
        </ul>
    </div>

    <div class="main-container">
        <div class="header">
            <h1>Panneau d'Administration</h1>
        </div>

        <div style="padding: 20px;">
            <h2 style="color: #2c3e50; margin-top: 0;">Gestion Globale des Droits Utilisateurs</h2>
            
            <%-- Messages de retour d'action --%>
            <c:if test="${not empty sessionScope.messageSucces}">
                <div class="alert alert-success">
                    ${sessionScope.messageSucces}
                    <% session.removeAttribute("messageSucces"); %>
                </div>
            </c:if>
            <c:if test="${not empty sessionScope.messageErreur}">
                <div class="alert alert-danger">
                    ${sessionScope.messageErreur}
                    <% session.removeAttribute("messageErreur"); %>
                </div>
            </c:if>

            <table class="table-admin">
                <thead>
                    <tr>
                        <th style="width: 7%">ID</th>
                        <th style="width: 13%">Identifiant</th>
                        <th style="width: 22%">Nom Complet</th>
                        <th style="width: 18%">Rôle Actuel</th>
                        <th style="width: 25%">Attribuer Nouveau Rôle</th>
                        <th style="width: 15%">Validation</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="u" items="${utilisateurs}">
                        <tr>
                            <td><b>#${u.id}</b></td>
                            <td><code style="background: #edf2f7; padding: 3px 8px; border-radius: 4px; font-size: 13px;">${u.login}</code></td>
                            <td><span style="font-weight: 600; color: #2d3748;">${u.nom}</span></td>
                            
                            <%-- Nouvelle colonne : Traduction textuelle du rôle actuel en base --%>
                            <td>
                                <span class="badge-role">
                                    <c:choose>
                                        <c:when test="${u.role == 1}">COMMERCE</c:when>
                                        <c:when test="${u.role == 2}">CONDITIONNEMENT</c:when>
                                        <c:when test="${u.role == 3}">APPROVISIONNEMENT</c:when>
                                        <c:when test="${u.role == 4}">SCM</c:when>
                                        <c:when test="${u.role == 5}">LOGISTIQUE</c:when>
                                        <c:when test="${u.role == 6}">QHE</c:when>
                                        <c:when test="${u.role == 7}">DOP</c:when>
                                        <c:when test="${u.role == 8}">METHODES</c:when>
                                        <c:when test="${u.role == 9}">CDG</c:when>
                                        <c:when test="${u.role == 10}">DCM</c:when>
                                        <c:when test="${u.role == 11}">PATRON</c:when>
                                        <c:when test="${u.role == 12}">INVITE</c:when>
                                        <c:otherwise>NON DÉFINI (${u.role})</c:otherwise>
                                    </c:choose>
                                </span>
                            </td>

                            <%-- Formulaire individuel de modification --%>
                            <form action="${pageContext.request.contextPath}/modifierRole" method="post">
                                <input type="hidden" name="id_utilisateur" value="${u.id}">
                                
                                <td>
                                    <select name="id_role" class="select-role" required>
                                        <c:forEach var="r" items="${roles}">
                                            <option value="${r.id}" ${r.id == u.role ? 'selected="selected"' : ''}>
                                                ${r.role}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </td>
                                <td>
                                    <button type="submit" class="btn-sauvegarder">
                                        Enregistrer
                                    </button>
                                </td>
                            </form>
                            
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

</body>
</html>