<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Liste des Templates de Workflow</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        .table-container { background: white; border-radius: 10px; padding: 20px; shadow: 0 4px 6px rgba(0,0,0,0.1); }
        .status-badge { font-size: 0.8rem; }
    </style>
</head>
<body class="bg-light">

<div class="container mt-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2><i class="fa-solid fa-list-check me-2 text-primary"></i>Templates de Workflow</h2>
        <a href="template-workflow" class="btn btn-success">
            <i class="fa-solid fa-plus me-1"></i> Créer un nouveau Template
        </a>
    </div>

    <div class="table-container shadow-sm">
        <table class="table table-hover align-middle">
            <thead class="table-light">
                <tr>
                    <th>ID</th>
                    <th>Nom du Template</th>
                    <th>Version</th>
                    <th>Statut</th>
                    <th class="text-center">Actions</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="t" items="${templates}">
                    <tr>
                        <td><span class="text-muted">#${t.id}</span></td>
                        <td>
                            <div class="fw-bold">${t.titre}</div>
                            <small class="text-muted">${t.commentaire}</small>
                        </td>
                        <td><span class="badge bg-secondary">v${t.version}</span></td>

                       
                        <td class="text-center">
                            <div class="btn-group" role="group">
                                <a href="template-workflow?id=${t.id}" class="btn btn-outline-warning btn-sm" title="Modifier infos">
                                    <i class="fa-solid fa-pen"></i>
                                </a>
                                
                                <a href="workflow-design?id_workflow=${t.id}" class="btn btn-primary btn-sm" title="Designer le workflow">
                                    <i class="fa-solid fa-diagram-project me-1"></i> Designer
                                </a>

                                <a href="template-workflow?action=delete&id=${t.id}" 
                                   class="btn btn-outline-danger btn-sm" 
                                   onclick="return confirm('Supprimer définitivement ce template ?')">
                                    <i class="fa-solid fa-trash"></i>
                                </a>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                
                <c:if test="${empty templates}">
                    <tr>
                        <td colspan="5" class="text-center py-4 text-muted">
                            Aucun template trouvé. Commencez par en créer un !
                        </td>
                    </tr>
                </c:if>
            </tbody>
        </table>
    </div>

</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>