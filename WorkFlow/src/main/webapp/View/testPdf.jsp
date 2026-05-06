<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Test Export PDF</title>
    <style>
        body { font-family: Arial, sans-serif; display: flex; justify-content: center; align-items: center; height: 100vh; background: #f4f7f6; margin: 0; }
        .card { background: white; padding: 40px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); text-align: center; }
        .btn-download {
            background-color: #e74c3c;
            color: white;
            padding: 12px 24px;
            text-decoration: none;
            border-radius: 4px;
            font-weight: bold;
            display: inline-block;
            transition: background 0.3s;
        }
        .btn-download:hover { background-color: #c0392b; }
    </style>
</head>
<body>

    <div class="card">
        <h2>Récapitulatif de Workflow</h2>
        <p>Cliquez sur le bouton pour générer le fichier PDF.</p>
        
        <a href="${pageContext.request.contextPath}/downloadPdf?id=1" class="btn-download">
            📥 Télécharger le PDF
        </a>
    </div>

</body>
</html>