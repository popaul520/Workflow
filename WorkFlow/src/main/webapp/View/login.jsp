<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Connexion - Système Workflow</title>
    <style>
        body {
            margin: 0;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f0f2f5;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
        .login-box {
            background: white;
            padding: 40px;
            border-radius: 8px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            width: 320px;
            text-align: center;
        }
        h2 { color: #1c1e21; margin-bottom: 20px; }
        input {
            width: 100%;
            padding: 12px;
            margin: 10px 0;
            border: 1px solid #dddfe2;
            border-radius: 6px;
            box-sizing: border-box; /* Évite que l'input dépasse */
        }
        button {
            width: 100%;
            padding: 12px;
            background: #4a6fa5;
            color: white;
            border: none;
            border-radius: 6px;
            font-weight: bold;
            cursor: pointer;
            transition: background 0.2s;
        }
        button:hover { background: #3b5984; }
        .error {
            color: #d93025;
            background: #fce8e6;
            padding: 10px;
            margin-top: 15px;
            border-radius: 4px;
            font-size: 0.9em;
            /* N'affiche le bloc que s'il y a une erreur */
            display: ${not empty error ? 'block' : 'none'};
        }
    </style>
</head>
<body>
<%-- Ajoute l'import du DAO pour charger les rôles au chargement de la page --%>
<%@ page import="dao.RoleDAO, java.util.Map" %>
<%
    RoleDAO roleDao = new RoleDAO();
    Map<Integer, String> roles = roleDao.getAllRoleNames();
    request.setAttribute("listeRoles", roles);
%>

<div class="login-box">
    <h2>Connexion</h2>
    
    <form action="${pageContext.request.contextPath}/login" method="post">
        <input type="text" name="login" placeholder="Identifiant" required>
        <input type="password" name="mdp" placeholder="Mot de passe" required>
        
        <select name="roleId" required style="width: 100%; padding: 12px; margin: 10px 0; border: 1px solid #dddfe2; border-radius: 6px;">
            <c:forEach var="r" items="${listeRoles}">
                <option value="${r.key}">${r.value}</option>
            </c:forEach>
        </select>

        <button type="submit" name="action" value="connect">Se connecter</button>
        
        <button type="submit" name="action" value="guest" 
                style="margin-top: 10px; background: #6c757d; border: none;" 
                formnovalidate>
            Accéder en tant qu'invité
        </button>
    </form>

    <div class="error">${error}</div>
</div>

</body>
</html>