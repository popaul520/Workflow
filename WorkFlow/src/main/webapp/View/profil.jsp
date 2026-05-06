<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="model.Utilisateur" %>
<%
    // Sécurité : si l'utilisateur n'est pas connecté, on le renvoie au login
    Utilisateur user = (Utilisateur) session.getAttribute("user");
    if (user == null) {
        response.sendRedirect("login");
        return;
    }
%>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Mon Profil</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/inte.css" />
    <style>
        .profile-card {
            background: white;
            padding: 20px;
            border-radius: 8px;
            box-shadow: 0 2px 5px rgba(0,0,0,0.1);
            max-width: 500px;
            margin: 20px auto;
        }
        .info-group { margin-bottom: 15px; border-bottom: 1px solid #eee; padding-bottom: 5px; }
        .label { font-weight: bold; color: #4a6fa5; }
    </style>
</head>
<body>
    <div class="container">
        <div class="main">
            <div class="header">
                <h1>Mon Compte</h1>
                <a href="login">Retour à l'accueil</a>
            </div>

            <div class="profile-card">
                <h2>Informations personnelles</h2>
                <div class="info-group">
                    <span class="label">Nom :</span> <%= user.getNom() %>
                </div>
                <div class="info-group">
                    <span class="label">Login :</span> <%= user.getLogin() %>
                </div>
                <button onclick="window.location.href='logout'" style="background: #cc0000; color: white; border: none; padding: 10px; cursor: pointer; border-radius: 4px;">
                    Se déconnecter
                </button>
            </div>
        </div>
    </div>
</body>
</html>