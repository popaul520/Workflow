<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>Configuration des Étapes</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
<div class="container mt-5">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <h2>Étapes du Workflow : ${workflow.titre}</h2>
        <a href="template-list" class="btn btn-secondary">Retour aux templates</a>
    </div>

    <div class="card mb-4">
        <div class="card-header bg-primary text-white">Ajouter / Modifier une étape</div>
        <div class="card-body">
            <form action="template-etapes" method="post" class="row g-3">
                <input type="hidden" name="action" value="save">
                <input type="hidden" name="id_workflow" value="${workflow.id}">
                <input type="hidden" name="id_etape" id="id_etape" value="0">

                <div class="col-md-3">
                    <label class="form-label">Nom de l'étape</label>
                    <input type="text" name="nom_etape" id="nom_etape" class="form-control" required>
                </div>
                <div class="col-md-2">
                    <label class="form-label">Place (Ordre)</label>
                    <input type="number" name="place" id="place" class="form-control" required>
                </div>
                <div class="col-md-2">
                    <label class="form-label">Attente Place</label>
                    <input type="number" name="attente_place" id="attente_place" class="form-control" value="0">
                </div>
                <div class="col-md-2">
                    <label class="form-label">Rôle requis</label>
                    <select name="role_associe" id="role_associe" class="form-select">
                        <c:forEach var="r" items="${roles}">
                            <option value="${r.key}">${r.value}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="col-md-1">
                    <label class="form-label">Finale ?</label>
                    <div class="form-check mt-2">
                        <input type="checkbox" name="est_finale" id="est_finale" class="form-check-input">
                    </div>
                </div>
                <div class="col-md-2 d-flex align-items-end">
                    <button type="submit" class="btn btn-success w-100">Enregistrer</button>
                </div>
            </form>
        </div>
    </div>

    <table class="table table-hover bg-white shadow-sm">
        <thead class="table-dark">
            <tr>
                <th>Ordre</th>
                <th>Nom</th>
                <th>Rôle</th>
                <th>Attente</th>
                <th>Statut</th>
                <th>Actions</th>
                <th>ajout de données</th>
                
            </tr>
        </thead>
        <tbody>
            <c:forEach var="e" items="${etapes}">
                <tr>
                    <td><strong>${e.place}</strong></td>
                    <td>${e.nomEtape}</td>
                    <td>${roles[e.roleAssocie]}</td>
                    <td>${e.attentePlace}</td>
                    <td>
                        <c:if test="${e.estFinale}"><span class="badge bg-danger">FINALE</span></c:if>
                    </td>
                    <td>
                        <button class="btn btn-sm btn-warning" onclick="editEtape('${e.id}', '${e.nomEtape}', '${e.place}', '${e.attentePlace}', '${e.roleAssocie}', ${e.estFinale})">Modifier</button>
                        <a href="template-etapes?action=delete&id_etape=${e.id}&id_workflow=${workflow.id}" class="btn btn-sm btn-danger" onclick="return confirm('Supprimer ?')">Supprimer</a>
                    </td>
                <td>
                    <a href="workflow-design?id_workflow=${t.id}" class="btn btn-primary btn-sm">
                        <i class="fa-solid fa-pen-ruler"></i> Configurer le Design
                    </a>
                </td>
                </tr>
            </c:forEach>
        </tbody>
    </table>
</div>

<script>
function editEtape(id, nom, place, attente, role, finale) {
    document.getElementById('id_etape').value = id;
    document.getElementById('nom_etape').value = nom;
    document.getElementById('place').value = place;
    document.getElementById('attente_place').value = attente;
    document.getElementById('role_associe').value = role;
    document.getElementById('est_finale').checked = finale;
}
</script>
</body>

</html>