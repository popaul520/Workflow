<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Gestion des Référentiels (Types Contraints)</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
</head>
<body class="bg-light">

<div class="container mt-5 mb-5">
    
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2><i class="bi bi-gear-wide-connected text-primary"></i> Référentiels de Données</h2>
        <a href="home" class="btn btn-outline-secondary btn-sm">🏠 Retour Accueil</a>
    </div>

    <div class="card mb-4 shadow-sm border-primary">
        <div class="card-header bg-primary text-white d-flex justify-content-between align-items-center">
            <span id="form-title" class="fw-bold"><i class="bi bi-plus-circle"></i> Ajouter un nouvel élément de contrainte</span>
            <button class="btn btn-sm btn-light" onclick="resetForm()" id="btn-cancel" style="display:none;">Annuler la modification</button>
        </div>
        <div class="card-body">
            <form action="type-contraint" method="post" class="row g-3 align-items-end">
                <input type="hidden" name="id_contraint" id="id_contraint" value="0">
                
                <div class="col-md-4">
                    <label class="form-label small fw-bold text-secondary">Famille / Type de la contrainte</label>
                    <input type="text" name="type_nom" id="type_nom" class="form-control" placeholder="Ex: client, Bool, rayon, etc." required>
                </div>
                
                <div class="col-md-5">
                    <label class="form-label small fw-bold text-secondary">Valeur associée</label>
                    <input type="text" name="valeur_nom" id="valeur_nom" class="form-control" placeholder="Ex: Oui, Non, Marketing, France..." required>
                </div>
                
                <div class="col-md-3">
                    <button type="submit" id="btn-submit" class="btn btn-primary w-100 fw-bold">
                        <i class="bi bi-check-circle"></i> Enregistrer
                    </button>
                </div>
            </form>
        </div>
    </div>

    <div class="card shadow-sm border-0">
        <div class="card-body p-0">
            <table class="table table-hover align-middle mb-0">
                <thead class="table-dark table-striped">
                    <tr>
                        <th width="100" class="text-center">ID DB A SUPPRIMER INUtiLE FOR USER</th>
                        <th>Type (Famille)</th>
                        <th>Valeur possible du type</th>
                        <th width="150" class="text-center">Actions</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="item" items="${listeContraintes}">
                        <tr>
                            <td class="text-center text-muted fw-bold small">${item.id}</td>
                            <td>
                                <span class="badge bg-secondary px-3 py-2 fs-6 opacity-75">${item.type}</span>
                            </td>
                            <td class="fw-bold text-dark">${item.valeur}</td>
                            <td class="text-center">
                                <div class="btn-group">
                                    <button class="btn btn-sm btn-outline-warning" 
                                            onclick="prepareEdit('${item.id}', '${(item.type)}', '${(item.valeur)}')">
                                        <i class="bi bi-pencil-square"></i>
                                    </button>
                                    <a href="type-contraint?action=delete&id=${item.id}" 
                                       class="btn btn-sm btn-outline-danger" 
                                       onclick="return confirm('Êtes-vous certain de vouloir supprimer l\'option « ${item.valeur} » du type « ${item.type} » ?')">
                                        <i class="bi bi-trash"></i>
                                    </a>
                                </div>
                            </td>
                        </tr>
                    </c:forEach>
                    <c:if test="${empty listeContraintes}">
                    
                        <tr>
                            <td colspan="4" class="text-center p-4 text-muted">Il n'a pas de donnée dans la base ;)</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>
</div>

<script>
    // Bascule l'UI de la carte supérieure en mode édition
    function prepareEdit(id, type, valeur) {
        document.getElementById('id_contraint').value = id;
        document.getElementById('type_nom').value = type;
        document.getElementById('valeur_nom').value = valeur;
        
        // Changements cosmétiques du bloc formulaire
        document.getElementById('form-title').innerHTML = "<i class='bi bi-pencil-square text-warning'></i> Modification de l'élément ID : " + id;
        document.getElementById('btn-submit').className = "btn btn-warning w-100 fw-bold text-dark";
        document.getElementById('btn-submit').innerHTML = "<i class='bi bi-arrow-clockwise'></i> Mettre à jour";
        document.getElementById('btn-cancel').style.display = "inline-block";
        
        // Remonte doucement vers le haut du formulaire
        window.scrollTo({top: 0, behavior: 'smooth'});
    }

    // Réinitialise le formulaire en mode insertion standard
    function resetForm() {
        document.getElementById('id_contraint').value = "0";
        document.getElementById('type_nom').value = "";
        document.getElementById('valeur_nom').value = "";
        document.getElementById('form-title').innerHTML = "<i class='bi bi-plus-circle'></i> Ajouter un nouvel élément de contrainte";
        document.getElementById('btn-submit').className = "btn btn-primary w-100 fw-bold";
        document.getElementById('btn-submit').innerHTML = "<i class='bi bi-check-circle'></i> Enregistrer";
        document.getElementById('btn-cancel').style.display = "none";
    }
</script>
</body>
</html>