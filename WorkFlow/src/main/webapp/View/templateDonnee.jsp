<%@ page contentType="text/html;charset=UTF-8" language="java"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<!DOCTYPE html>
<html>
<head>
<title>Design Workflow Expert</title>
<link
	href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
	rel="stylesheet">
<link rel="stylesheet"
	href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.10.0/font/bootstrap-icons.css">
<script
	src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
<style>
.accordion-button:not(.collapsed) {
	background-color: #e7f1ff;
	color: #0c63e4;
}

.data-row:hover {
	background-color: #f8f9fa;
}
.etape-validee {
	background-color: #d1e7dd !important;
	color: #0f5132 !important;
	border-left: 5px solid #198754 !important;
}
.etape-en-cours {
	background-color: #fff3cd !important;
	color: #664d03 !important;
	border-left: 5px solid #ffc107 !important;
	font-weight: bold;
}

.etape-bloquee {
	background-color: #f8d7da !important;
	color: #842029 !important;
	opacity: 0.6;
	cursor: not-allowed;
}
</style>
</head>
<body class="bg-light">
	<div class="container mt-5 mb-5">

		<div class="d-flex justify-content-between align-items-center mb-4">
			<h2>
				<i class="bi bi-diagram-3-fill"></i> Workflow : ${workflow.titre}
			</h2>
			<a href="template-list" class="btn btn-outline-secondary btn-sm">Retour
				à la liste</a>
			<button type="button" class="btn btn-outline-primary btn-sm"
			        onClick="window.location.href='${pageContext.request.contextPath}/catalogues'">
			    <i class="bi bi-gear-fill"></i> + Nouveau typelist
			</button>
		</div>

		<div class="card mb-4 shadow-sm border-primary">
			<div
				class="card-header bg-primary text-white d-flex justify-content-between">
				<span id="etape-form-title">Ajouter une étape</span>
				<button class="btn btn-sm btn-light" onclick="resetEtapeForm()"
					id="btn-reset-etape" style="display: none;">Annuler la
					modification</button>
			</div>
			<div class="card-body">
				<form action="workflow-design" method="post" class="row g-3">
					<input type="hidden" name="type" value="etape"> <input
						type="hidden" name="id_workflow" value="${workflow.id}"> <input
						type="hidden" name="id_etape" id="id_etape" value="0">

					<div class="col-md-3">
						<label class="form-label small fw-bold">Nom de l'étape</label> <input
							type="text" name="nom_etape" id="nom_etape" class="form-control"
							required>
					</div>
					<div class="col-md-2">
						<label class="form-label small fw-bold">Ordre (Place)</label> <input
							type="number" name="place" id="place" class="form-control"
							value="${fn:length(etapes) + 1}" min="1"
							max="${fn:length(etapes) + 1}">
					</div>
					<div class="col-md-2">
						<label class="form-label small fw-bold">Rôle Responsable</label> <select
							name="role_associe" id="role_associe" class="form-select">
							<c:forEach var="r" items="${roles}">
								<option value="${r.key}">${r.value}</option>
							</c:forEach>
						</select>
					</div>
					<div class="col-md-3">
						<label class="form-label small fw-bold">Attendre l'étape
							(Optionnel)</label> <select name="attente_place" id="attente_place"
							class="form-select">
							<option value="-1">-- Déroulement linéaire --</option>
							<c:forEach var="prev" items="${etapes}">
								<option value="${prev.place}">Étape ${prev.place} :
									${prev.nomEtape}</option>
							</c:forEach>
						</select>
					</div>
					<div class="col-md-1 text-center">
						<label class="form-label small fw-bold">Validation</label><br>
						<input type="checkbox" name="est_finale" id="est_finale"
							class="form-check-input">
					</div>
					<div class="col-md-1 d-flex align-items-end">
						<button type="submit" class="btn btn-primary w-100">Enregistrer</button>
					</div>
				</form>
			</div>
		</div>

		<div class="accordion" id="workflowAccordion">
			<c:forEach var="e" items="${etapes}">
				<div class="accordion-item mb-3 shadow-sm border-0">
					<h2 class="accordion-header">
						<button class="accordion-button collapsed" type="button"
							data-bs-toggle="collapse" data-bs-target="#collapse${e.id}">
							<span class="badge bg-dark me-2">${e.place}</span> <strong>${e.nomEtape}</strong>
							<span
								class="ms-auto badge rounded-pill bg-light text-dark border me-3 small">${roles[e.roleAssocie]}</span>
							<c:if test="${e.estFinale}">
								<span class="badge bg-danger me-2">FINALE</span>
							</c:if>
						</button>
					</h2>
					<div id="collapse${e.id}" class="accordion-collapse collapse"
						data-bs-parent="#workflowAccordion">
						<div class="accordion-body bg-white border-top">

							<div class="d-flex gap-2 mb-4">
								<button class="btn btn-sm btn-outline-warning"
									onclick="prepareEditEtape('${e.id}', '${e.nomEtape}', '${e.place}', '${e.roleAssocie}', ${e.estFinale})">
									<i class="bi bi-pencil"></i> Modifier l'étape
								</button>
								<a
									href="workflow-design?action=deleteEtape&id_etape=${e.id}&id_workflow=${workflow.id}"
									class="btn btn-sm btn-outline-danger"
									onclick="return confirm('Supprimer cette étape et ses champs ?')">
									<i class="bi bi-trash"></i> Supprimer
								</a>
							</div>

							<h6 class="border-bottom pb-2 mb-3 text-secondary">
								<i class="bi bi-list-ul"></i> Configuration des champs de saisie
							</h6>

							<table class="table table-hover align-middle shadow-sm">
								<thead class="table-light small">
									<tr>
										<th width="80" class="text-center">Ordre</th>
										<th>Libellé du champ</th>
										<th>Type / Référentiel lié</th>
										<th class="text-center">Options</th>
										<th width="150" class="text-center">Actions</th>
									</tr>
								</thead>
								<tbody>
									<c:forEach var="d" items="${mapDonnees[e.id]}">
										<tr class="data-row">
											<td class="text-center fw-bold text-muted">${d.ordreAffichage}</td>
											<td class="fw-bold">${d.nomChamp}</td>
											<td><span class="badge bg-secondary opacity-75">${d.typeComposant}</span>
												<c:if test="${not empty d.refTypeContraint}">
													<span class="badge bg-info text-dark"><i
														class="bi bi-link-45deg"></i> ${d.refTypeContraint}</span>
												</c:if></td>
											<td class="text-center"><c:if
													test="${d.estObligatoire == true}">
													<i class="bi bi-asterisk text-danger" title="Requis"></i>
												</c:if> <c:if test="${d.aCommentaire == true}">
													<i class="bi bi-chat-left-dots text-primary"
														title="Commentaire"></i>
												</c:if> <c:if test="${d.aDate == true}">
													<i class="bi bi-calendar-event text-success" title="Date"></i>
												</c:if></td>
											<td class="text-center">
												<button class="btn btn-sm btn-light text-warning"
													onclick="prepareEditDonnee('${e.id}', '${d.id}', '${fn:escapeXml(d.nomChamp)}', '${d.typeComposant}', '${d.ordreAffichage}', ${d.aCommentaire}, ${d.aDate}, ${d.estObligatoire}, '${d.refTypeContraint}')">
													<i class="bi bi-pencil-square"></i>
												</button> <a
												href="workflow-design?action=deleteDonnee&id_donnee=${d.id}&id_workflow=${workflow.id}"
												class="btn btn-sm btn-light text-danger"> <i
													class="bi bi-x-circle"></i>
											</a>
											</td>
										</tr>
									</c:forEach>
								</tbody>
							</table>

							<div class="p-3 bg-light rounded border">
								<form action="workflow-design" method="post"
									id="form-donnee-${e.id}">
									<input type="hidden" name="type" value="donnee"> <input
										type="hidden" name="id_workflow" value="${workflow.id}">
									<input type="hidden" name="id_etape" value="${e.id}"> <input
										type="hidden" name="id_donnee" id="id_donnee_${e.id}"
										value="0">

									<div class="row g-2 align-items-end">
										<div class="col-md-3">
											<label class="small fw-bold">Nom du champ</label> <input
												type="text" name="nom_champ" id="nom_champ_${e.id}"
												class="form-control form-control-sm" required>
										</div>
										<div class="col-md-2">
											<label class="small fw-bold">Type composant</label> <select
												name="type_composant" id="type_comp_${e.id}"
												class="form-select form-select-sm">
												<option value="text">Texte</option>
												<option value="number">Nombre</option>
												<option value="date">Date seule</option>
												<option value="textarea">Zone Longue</option>
												<option value="select">Liste déroulante</option>
											</select>
										</div>

										<%-- Remplace le bloc de sélection du type associé par celui-ci --%>
										<div class="col-md-2">
										    <label class="small fw-bold text-primary">Associer type</label> 
										    <select name="ref_type_contraint" id="ref_type_${e.id}" class="form-select form-select-sm">
										        <option value="">-- Aucun --</option>
										        <c:forEach var="tc" items="${listeUniqueTypesContraints}">
										            <option value="${tc}">${tc}</option>
										        </c:forEach>
										    </select>
										</div>
										<div class="col-md-1">
											<label class="small fw-bold">Ordre</label> <input
												type="number" name="ordre_affichage" id="ordre_${e.id}"
												class="form-control form-control-sm"
												value="${fn:length(mapDonnees[e.id]) + 1}"
												max="${fn:length(mapDonnees[e.id]) + 1}" min="1">
										</div>
										<div class="col-md-2 py-1 text-center">
											<div class="form-check form-check-inline small">
												<input type="checkbox" name="est_obligatoire"
													id="oblig_${e.id}" class="form-check-input"> <label>Req.</label>
											</div>
											<div class="form-check form-check-inline small">
												<input type="checkbox" name="a_commentaire"
													id="comm_${e.id}" class="form-check-input"> <label>+
													Com</label>
											</div>
											<div class="form-check form-check-inline small">
												<input type="checkbox" name="a_date" id="date_opt_${e.id}"
													class="form-check-input"> <label>+ Dat</label>
											</div>
										</div>
										<div class="col-md-2">
											<button type="submit" class="btn btn-sm btn-dark w-100"
												id="btn_submit_donnee_${e.id}">Ajouter champ</button>
											<button type="button"
												class="btn btn-sm btn-outline-secondary w-100 mt-1"
												id="btn_cancel_donnee_${e.id}" style="display: none;"
												onclick="resetDonneeForm('${e.id}', ${fn:length(mapDonnees[e.id]) + 1})">Annuler</button>
										</div>
									</div>
								</form>
							</div>

						</div>
					</div>
				</div>
			</c:forEach>
		</div>
	</div>

	<div class="modal fade" id="modalQuickType" tabindex="-1"
		aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered">
			<div class="modal-content shadow">
				<div class="modal-header bg-dark text-white py-2">
					<h6 class="modal-title">
						<i class="bi bi-plus-circle-fill text-info"></i> Ajouter un Type
						Contraint
					</h6>
					<button type="button" class="btn-close btn-close-white"
						data-bs-dismiss="modal" aria-label="Close"></button>
				</div>
				<form action="workflow-design" method="post">
					<input type="hidden" name="type" value="quick_type_contraint">
					<input type="hidden" name="id_workflow" value="${workflow.id}">
					<div class="modal-body py-3">
						<div class="mb-2">
							<label class="form-label small fw-bold text-muted m-0">Nom
								de la famille (Type)</label> <input type="text" name="type_nom"
								class="form-control form-control-sm"
								placeholder="Ex: client, Bool, rayon" required>
						</div>
						<div class="mb-1">
							<label class="form-label small fw-bold text-muted m-0">Valeur
								associée</label> <input type="text" name="valeur_nom"
								class="form-control form-control-sm"
								placeholder="Ex: Oui, France, Électronique" required>
						</div>
					</div>
					<div class="modal-footer bg-light py-1">
						<button type="button" class="btn btn-xs btn-secondary btn-sm"
							data-bs-dismiss="modal">Fermer</button>
						<button type="submit"
							class="btn btn-xs btn-primary btn-sm fw-bold">Ajouter au
							catalogue</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<script>
// --- LOGIQUE ETAPE ---
function prepareEditEtape(id, nom, place, role, finale, attentePlace) {
    document.getElementById('id_etape').value = id;
    document.getElementById('nom_etape').value = nom;
    document.getElementById('place').value = place;
    document.getElementById('role_associe').value = role;
    document.getElementById('est_finale').checked = finale;
    
    var selectAttente = document.getElementById('attente_place');
    if(attentePlace && attentePlace !== 'null' && attentePlace !== '') {
        selectAttente.value = attentePlace;
    } else {
        selectAttente.value = "-1";
    }

    document.getElementById('etape-form-title').innerText = "Modifier l'étape : " + nom;
    document.getElementById('btn-reset-etape').style.display = "block";
    window.scrollTo({top: 0, behavior: 'smooth'});
}

function resetEtapeForm() {
    document.getElementById('id_etape').value = "0";
    document.getElementById('nom_etape').value = "";
    document.getElementById('attente_place').value = "-1";
    document.getElementById('btn-reset-etape').style.display = "none";
    document.getElementById('etape-form-title').innerText = "Ajouter une étape";
}

// --- LOGIQUE DONNEE (Bulle Donnée avec gestion Annuler / Mettre à Jour) ---
function prepareEditDonnee(idEtape, idDonnee, nom, type, ordre, comm, date, oblig, refContraint) {
    document.getElementById('id_donnee_' + idEtape).value = idDonnee;
    document.getElementById('nom_champ_' + idEtape).value = nom;
    document.getElementById('type_comp_' + idEtape).value = type;
    document.getElementById('ordre_' + idEtape).value = ordre;
    document.getElementById('comm_' + idEtape).checked = comm;
    document.getElementById('date_opt_' + idEtape).checked = date;
    document.getElementById('oblig_' + idEtape).checked = oblig;
    
    // Assigner le type contraint sélectionné s'il existe
    var refSelect = document.getElementById('ref_type_' + idEtape);
    if (refContraint && refContraint !== 'null') {
        refSelect.value = refContraint;
    } else {
        refSelect.value = "";
    }
    
    // Changements graphiques pour indiquer la modification en cours
    document.getElementById('btn_submit_donnee_' + idEtape).innerText = "Mettre à jour";
    document.getElementById('btn_submit_donnee_' + idEtape).classList.replace('btn-dark', 'btn-warning');
    document.getElementById('btn_cancel_donnee_' + idEtape).style.display = "block";
}

function resetDonneeForm(idEtape, nextOrder) {
    document.getElementById('id_donnee_' + idEtape).value = "0";
    document.getElementById('nom_champ_' + idEtape).value = "";
    document.getElementById('type_comp_' + idEtape).value = "text";
    document.getElementById('ref_type_' + idEtape).value = "";
    document.getElementById('ordre_' + idEtape).value = nextOrder;
    document.getElementById('comm_' + idEtape).checked = false;
    document.getElementById('date_opt_' + idEtape).checked = false;
    document.getElementById('oblig_' + idEtape).checked = false;

    // Remise à l'état initial
    document.getElementById('btn_submit_donnee_' + idEtape).innerText = "Ajouter champ";
    document.getElementById('btn_submit_donnee_' + idEtape).classList.replace('btn-warning', 'btn-dark');
    document.getElementById('btn_cancel_donnee_' + idEtape).style.display = "none";
}
</script>
</body>
</html>