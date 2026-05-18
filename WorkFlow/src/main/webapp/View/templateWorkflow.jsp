

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>${not empty template ? 'Modifier' : 'Créer'} un Workflow</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        body { background-color: #f8f9fa; }
        .card { border: none; box-shadow: 0 0.5rem 1rem rgba(0, 0, 0, 0.15); }
        .header-gradient {
            background: linear-gradient(90deg, #4a6fa5, #3b5984);
            color: white;
            border-radius: 8px 8px 0 0;
            padding: 20px;
        }
    </style>
</head>
<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-8">
            <div class="card">
                <div class="header-gradient">
                    <h3 class="mb-0">
                        <i class="fa-solid fa-gears me-2"></i>
                        ${not empty template ? 'Modification du Template' : 'Nouveau Template de Workflow'}
                    </h3>
                </div>
                <div class="card-body p-4">
                    
                    <form action="${pageContext.request.contextPath}/template-workflow" method="post">
                        
                        <c:if test="${not empty template}">
                            <input type="hidden" name="id" value="${template.id}">
                        </c:if>

                        <div class="row mb-3">
                            <div class="col-md-8">
                                <label class="form-label fw-bold">Nom du Workflow</label>
                                <input type="text" name="nom" class="form-control" 
                                       placeholder="ex: Demande de dodo ..." 
                                       value="${template.nom}" required>
                            </div>
                            <div class="col-md-4">
                                <label class="form-label fw-bold">Version</label>
                                <input type="number" name="version" class="form-control" 
                                       value="${not empty template ? template.version : '1'}" required>
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label fw-bold">Description</label>
                            <textarea name="description" class="form-control" rows="4" 
                                      placeholder="Décrivez l'utilité de ce processus...">${template.description}</textarea>
                        </div>

                        <div class="row mb-4">
                            <div class="col-md-6">
                                <div class="form-check form-switch mt-2">
                                    <input class="form-check-input" type="checkbox" name="est_actif" 
                                           id="activeSwitch" ${template.est_actif ? 'checked' : (empty template ? 'checked' : '')}>
                                    <label class="form-check-label" for="activeSwitch">Template activé</label>
                                </div>
                            </div>
                            <c:if test="${not empty template}">
                                <div class="col-md-6 text-end">
                                    <small class="text-muted">Créé le : ${template.date_creation}</small>
                                </div>
                            </c:if>
                        </div>

                        <hr>

                        <div class="d-flex justify-content-between">
                            <a href="home" class="btn btn-outline-secondary">
                                <i class="fa-solid fa-arrow-left me-1"></i> Annuler
                            </a>
                            <button type="submit" class="btn btn-primary px-4">
                                <i class="fa-solid fa-floppy-disk me-1"></i>
                                ${not empty template ? 'Mettre à jour' : 'Enregistrer le template'}
                            </button>
                        </div>
                    </form>

                </div>
            </div>            
            <c:if test="${not empty template}">
                <div class="mt-4 text-center">
                    <a href="workflow-design?id=${template.id}" class="btn btn-info text-white">
                        <i class="fa-solid fa-list-check me-1"></i> Configurer les étapes et les droits
                    </a>
                </div>
            </c:if>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>