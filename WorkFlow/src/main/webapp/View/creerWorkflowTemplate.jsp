<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Initialiser un Workflow</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/inte.css" />
    <style>
        .sidebar { width: 250px; background: #2c3e50; color: white; min-height: 100vh; padding: 20px; position: fixed; }
        .sidebar h3 { border-bottom: 1px solid #34495e; padding-bottom: 10px; }
        .sidebar ul { list-style: none; padding: 0; }
        .sidebar ul li { margin: 15px 0; }
        .sidebar ul li a { color: #bdc3c7; text-decoration: none; }

        .main-container { flex: 1; margin-left: 250px; padding: 40px; }
        .form-card { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); max-width: 650px; margin: auto; }
        
        .form-group { display: flex; flex-direction: column; margin-bottom: 20px; }
        label { font-weight: 600; margin-bottom: 8px; color: #555; font-size: 0.95em; }
        input, select { padding: 12px; border: 1px solid #ddd; border-radius: 4px; font-size: 14px; background-color: #fafafa; }
        input:focus, select:focus { border-color: #3498db; outline: none; background-color: #fff; }

        .button-group { display: flex; justify-content: flex-end; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px; }
        .btn { padding: 12px 25px; border: none; border-radius: 4px; cursor: pointer; font-weight: bold; transition: 0.3s; }
        .btn-submit { background: #27ae60; color: white; }
        .btn-submit:hover { background: #219653; transform: translateY(-1px); }

        .template-info { background: #fdf6e3; padding: 15px; border-left: 4px solid #f1c40f; margin-bottom: 20px; font-size: 0.9em; border-radius: 0 4px 4px 0; line-height: 1.5; }
        .alert-danger { background-color: #f8d7da; color: #721c24; padding: 12px; border-radius: 4px; margin-bottom: 20px; border-left: 4px solid #dc3545; }
    </style>
</head>
<body>

    <div class="sidebar">
        <h3>Actions</h3>
        <ul>
            <li><a href="home">🏠 Retour Accueil</a></li>
            <li><a href="template-list">⚙️ Gérer les Templates</a></li>
        </ul>
    </div>

    <div class="main-container">
        <div class="form-card">
            <h2>🚀 Lancer un nouveau Projet</h2>
            <p style="color: #7f8c8d; margin-bottom: 25px;">Étape initiale obligatoire pour configurer la feuille de route de votre workflow.</p>
            
            <%-- Affichage d'une erreur éventuelle --%>
            <c:if test="${not empty erreur}">
                <div class="alert-danger">${erreur}</div>
            </c:if>

            <form action="creer-workflow" method="post">
                
                <div class="form-group">
                    <label>Nom de l'article / Libellé du Projet *</label>
                    <input type="text" name="titre" required placeholder="Ex: Saucisson Sec 200g">
                </div>

                <div class="form-group">
                    <label>Modèle de cycle d'approbation (Template) *</label>
                    <select name="id_template" required>
                        <option value="">-- Choisir un modèle existant --</option>
                        <c:forEach var="t" items="${templates}">
                            <option value="${t.id}">${t.titre}</option>
                        </c:forEach>
                    </select>
                </div>

                <div class="template-info">
                    <strong>💡 Fonctionnement dynamique :</strong> Dès la validation de ce formulaire, le moteur applicatif va charger l'étape 1 configurée pour ce modèle et affichera les champs correspondants pour enregistrer les données techniques.
                </div>

                <div class="button-group">
                    <button type="submit" class="btn btn-submit">Valider et Ouvrir l'Étape 1 ➔</button>
                </div>

            </form>
        </div>
    </div>

</body>
</html>