<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<html>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<head>
    <meta charset="UTF-8">
    <title>Modifier l'Étape</title>
    <style>
        body { font-family: sans-serif; margin: 20px; }
        .form-group { margin-bottom: 15px; }
        label { display: block; font-weight: bold; }
        input, select { padding: 8px; width: 300px; }
        button { padding: 10px 20px; background-color: #007bff; color: white; border: none; cursor: pointer; }
    </style>
</head>
<body>
    <h2>Modification de l'étape n° ${etape.nbEtape}</h2>
    <form action="etapeController" method="post">
 
        <input type="hidden" name="etape_id" value="${currentEtape.id}">
        <input type="hidden" name="etape_type" value="1_info_generale">
        <input type="hidden" name="current_n" value="1"> <div class="form-group">
            <label>Numéro de l'étape :</label>
            <input type="number" name="nbEtape" value="${etape.nbEtape}" required>
        </div>

        <div class="form-group">
            <label>Rôle concerné :</label>
            <input type="text" name="role" value="${etape.role}" required>
        </div> 

        <div class="form-group">
            <label>ID Workflow associé :</label>
            <input type="text" name="workflowId" value="${etape.workflow.id}" readonly>
        </div>
    


    <button type="submit">Valider et Continuer</button>
</form>
</body>
</html>

