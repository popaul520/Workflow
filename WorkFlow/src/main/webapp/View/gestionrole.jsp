<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Gestion des Rôles | WorkFlow</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        /* Styles spécifiques pour l'admin */
        .badge-etape {
            background: #e2e8f0;
            color: #4a5568;
            padding: 2px 8px;
            border-radius: 10px;
            font-size: 0.85em;
            margin-right: 5px;
            display: inline-block;
        }
        .table-admin th { background: #edf2f7; }
    </style>
</head>
<body>

    <div class="sidebar">
        <h3>Workflow</h3>
        <ul>
            <li><a href="home">🏠 Accueil</a></li>
            <li class="active"><a href="admin-roles">⚙️ Gestion Rôles</a></li>
            <li style="margin-top: 30px;"><a href="creer-workflow" style="color: var(--success); font-weight: bold;">➕ Créer Workflow</a></li>
        </ul>
    </div>

    <div class="main-container">
        <div class="header-flex">
            <h1>Configuration des Accès</h1>
        </div>
        <h2>Gestion des Accès par Rôle</h2>
        <div class="box">
            <table class="table-admin">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Rôle</th>
                        <th>Étapes autorisées</th>
                        <th style="text-align: center;">Action</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="r" items="${roles}">
                        <tr>
                            <td>#${r.id}</td>
                            <td><strong>${r.role}</strong></td>
                            <td>
                                <c:choose>
                                    <c:when test="${empty r.etapes}">
                                        <span style="color:#a0aec0; font-style: italic;">Aucun accès configuré</span>
                                    </c:when>
                                    <c:otherwise>
                                        <c:set var="stepsArray" value="${r.etapes.split(', ')}" />
                                        <c:forEach var="step" items="${stepsArray}">
                                            <span class="badge-etape">Étape ${step}</span>
                                        </c:forEach>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                            <td style="text-align: center;">
                                <a href="admin-roles?action=edit&id=${r.id}&name=${r.role}" class="btn-view">Modifier</a>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </div>
</body>
</html>