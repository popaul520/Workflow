<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>
<%@ taglib uri="jakarta.tags.fmt" prefix="fmt"%>
<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="UTF-8">
<title>Espace de Saisie - ${workflow.titre}</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<style>
.grid-boutons { display: grid; grid-template-columns: repeat(auto-fill, minmax(180px, 1fr)); gap: 12px; margin: 20px 0; }
.btn-etape { border: none; border-radius: 6px; padding: 12px; text-align: center; cursor: pointer; min-height: 75px; display: flex; flex-direction: column; justify-content: center; align-items: center; transition: all 0.2s; width: 100%; border: 1px solid #cbd5e0; box-shadow: 0 2px 4px rgba(0, 0, 0, 0.02); }
.btn-etape:hover:not(.etape-bloquee) { transform: translateY(-2px); box-shadow: 0 4px 6px rgba(0, 0, 0, 0.08); }
.etape-validee { background-color: #d1e7dd !important; color: #0f5132 !important; border-left: 6px solid #198754 !important; font-weight: bold; }
.etape-non-faite { background-color: #cfe2ff !important; color: #084298 !important; border-left: 6px solid #0d6efd !important; }
.etape-bloquee { background-color: #e2e8f0 !important; color: #94a3b8 !important; opacity: 0.5; cursor: not-allowed !important; border-left: 6px solid #cbd5e0 !important; }
.state-active-focus { outline: 3px solid #0d6efd !important; outline-offset: 2px; font-weight: bold; }
.visu-container { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05); margin-top: 30px; }
.visu-row { display: flex; align-items: center; padding: 15px 12px; border-bottom: 1px solid #edf2f7; }
.visu-row:hover { background-color: #f8fafc; }
.form-control-dyn { width: 100%; padding: 8px 12px; border: 1px solid #cbd5e0; border-radius: 4px; font-size: 14px; box-sizing: border-box; }
.status-banner { padding: 15px; border-radius: 8px; margin-bottom: 20px; display: flex; align-items: center; }
.btn-action { padding: 10px 22px; border: none; border-radius: 4px; font-weight: bold; cursor: pointer; transition: 0.2s; }
.required-star { color: #e53e3e; margin-left: 3px; }
.step-badge { font-size: 0.75em; padding: 2px 6px; border-radius: 4px; background: rgba(0, 0, 0, 0.06); margin-top: 4px; font-weight: 500; }
.detail-card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0,0,0,0.05); margin-bottom: 25px; }
.grid-info { display: grid; grid-template-columns: repeat(3, 1fr); gap: 15px; }
.label { font-size: 0.85em; color: #718096; font-weight: bold; text-transform: uppercase; }
.value { font-size: 1em; color: #2d3748; margin-top: 4px; }
.btn-pdf { display: inline-block; margin-top: 20px; background: #e74c3c; color: white; padding: 10px 20px; text-decoration: none; border-radius: 4px; font-weight: bold; }
</style>
</head>
<body class="bg-light">

	<div class="sidebar">
		<h3>Actions</h3>
		<ul> 
			<li><a href="homeport">🏠 Retour Accueil</a></li>
			<li><a href="template-list">⚙️ Liste des Templates</a></li>
		</ul>
	</div>

	<div class="main-container" style="padding: 40px; font-family: 'Segoe UI', sans-serif;">

		<div class="header" style="margin-bottom: 25px;">
			<h1>
				Suivi & Saisie Dossier <span class="status-badge" style="background: #2c3e50; color: white; padding: 2px 8px; border-radius: 4px;">#${workflow.id}</span>
			</h1>
			<p class="text-muted">Projet en cours : <strong>${workflow.titre}</strong></p>
		</div>

		<div class="detail-card">
			<h2 style="color: var(--accent); margin-top: 0;">${workflow.titre}</h2>
			<hr style="border: 0; border-top: 1px solid #eee; margin: 20px 0;">
			<div class="grid-info">
				<div class="info-group">
					<div class="label">Créateur</div>
					<div class="value">ID User: ${workflow.idUtilisateur}</div>
				</div>
				<div class="info-group">
					<div class="label">Date Création</div>
					<div class="value"><fmt:formatDate value="${workflow.dateCreation}" pattern="dd/MM/yyyy" /></div>
				</div>
				<div class="info-group">
					<div class="label">Commentaire</div>
					<div class="value">${empty workflow.commentaire ? 'Aucun' : workflow.commentaire}</div>
				</div>
			</div>
		</div>

		<div class="navigation-etapes" style="background: #fff; padding: 20px; border-radius: 8px; box-shadow: 0 2px 5px rgba(0, 0, 0, 0.05);">
			<h3 style="border-left: 5px solid #3498db; padding-left: 15px; margin-top: 0;">Cycle de validation du modèle</h3>

<div class="grid-boutons">
    <c:forEach var="etape" items="${etapesTemplate}">
        
        <%-- 1. Est-ce que l'étape elle-même est déjà validée ? --%>
        <c:set var="cleEtape" value="[${etape.place}]" />
        <c:set var="isValidee" value="${fn:contains(etapesValideesChaine, cleEtape)}" />

        <%-- 2. APPLICATION STRICTE DE TA RÈGLE DE DÉBLOCAGE SUR ATTENTE_PLACE --%>
        <c:choose>
            <%-- Cas A : Pas de prérequis configuré (NULL ou <= 0) -> Débloquée d'office --%>
            <c:when test="${empty etape.attentePlace || etape.attentePlace <= 0}">
                <c:set var="parentFait" value="true" />
            </c:when>
            
            <%-- Cas B : Un prérequis existe -> On vérifie s'il est présent dans la chaîne des étapes validées --%>
            <c:otherwise>
                <c:set var="cleAttente" value="[${etape.attentePlace}]" />
                <c:set var="parentFait" value="${fn:contains(etapesValideesChaine, cleAttente)}" />
            </c:otherwise>
        </c:choose>
        
        <%-- 3. Déduction des états d'affichage --%>
        <c:set var="isEnCours" value="${!isValidee && parentFait}" />
        <c:set var="isBloquee" value="${!isValidee && !parentFait}" />

        <%-- 4. Attribution des classes CSS --%>
        <c:choose>
            <c:when test="${isValidee}"><c:set var="colorClass" value="etape-validee" /></c:when>
            <c:when test="${isEnCours}"><c:set var="colorClass" value="etape-non-faite" /></c:when>
            <c:otherwise><c:set var="colorClass" value="etape-bloquee" /></c:otherwise>
        </c:choose>

        <c:set var="activeFocusClass" value="${etape.place == numEtapeActive ? 'state-active-focus' : ''}" />

        <%-- 5. Rendu du bouton --%>
        <button type="button"
            <c:if test="${!isBloquee}">onclick="window.location.href='saisie-etape?id_workflow=${workflow.id}&num_etape=${etape.place}'"</c:if>
            class="btn-etape ${colorClass} ${activeFocusClass}"
            <c:if test="${isBloquee}">disabled="disabled" style="cursor: not-allowed;"</c:if>>
            <div style="font-size: 0.85em; font-weight: bold; opacity: 0.8;">Étape ${etape.place}</div>
            <div class="step-role" style="font-size: 0.95em; text-align: center;">${etape.nomEtape}</div>
            
            <c:if test="${isBloquee}">
                <div style="font-size: 0.75em; color: #e53e3e; margin-top: 4px; font-weight: bold;">Bloqué</div>
            </c:if>
            <c:if test="${isEnCours}">
                <div style="font-size: 0.75em; color: #0d6efd; margin-top: 4px; font-weight: bold;">À renseigner</div>
            </c:if>
        </button>
    </c:forEach>
</div>
		<div class="visu-container">
			<c:if test="${isClosed}">
				<div class="status-banner" style="background-color: #fff5f5; border: 1px solid #feb2b2;">
					<span style="font-size: 24px; margin-right: 15px;">🔒</span>
					<div>
						<strong style="color: #c53030;">Dossier Clôturé</strong><br>
						<small style="color: #4a5568;">Finalisé le : <fmt:formatDate value="${workflow.dateFinalisation}" pattern="dd/MM/yyyy" /></small>
					</div>
				</div>
			</c:if>

			<c:choose>
				<c:when test="${empty donneesEtape && !canEdit && !currentEtape.estFinale}">
					<div class="info-box" style="text-align: center; padding: 40px; color: #718096; background: #f7fafc; border-radius: 6px;">
						<p>Cette étape n'a pas encore de données renseignées ou vous n'avez pas le rôle requis pour y accéder.</p>
					</div>
				</c:when>

				<c:otherwise>
					<c:set var="modeEditionForce" value="true" />
					<c:forEach var="d" items="${donneesEtape}">
						<c:if test="${not empty d.attribut}">
							<c:set var="modeEditionForce" value="false" />
						</c:if>
					</c:forEach>
					<c:if test="${isClosed}">
						<c:set var="modeEditionForce" value="false" />
					</c:if>

					<form action="${pageContext.request.contextPath}/saisie-etape" method="post">
						<input type="hidden" name="id_workflow" value="${workflow.id}">
						<input type="hidden" name="current_n" value="${numEtapeActive}">
						<input type="hidden" name="total_champs" value="${donneesEtape.size()}">
						<input type="hidden" name="is_etape_finale" value="${currentEtape.estFinale}">

						<div class="step-header" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 25px; border-bottom: 2px solid #edf2f7; padding-bottom: 15px;">
							<h2 style="margin: 0; color: #2d3748;">Saisie : ${currentEtape.nomEtape} (Étape ${numEtapeActive})</h2>

							<div>
								<c:choose>
									<c:when test="${canEdit && !isClosed}">
										<button type="button" id="btn-modifier" onclick="activerEdition()" class="btn-action" 
												style="background: #3182ce; color: white; ${modeEditionForce ? 'display: none;' : ''}">Modifier</button>
										<button type="submit" id="btn-enregistrer" class="btn-action" 
												style="background: #38a169; color: white; ${modeEditionForce ? 'display: inline-block;' : 'display: none;'}">Enregistrer et Valider</button>
									</c:when>
									<c:otherwise>
										<span style="color: #e53e3e; font-weight: bold; font-size: 0.9em;">🔒 Consultation uniquement</span>
									</c:otherwise>
								</c:choose>
							</div>
						</div>

						<fieldset id="fs-edition" ${canEdit && modeEditionForce ? '' : 'disabled'} style="border: none; padding: 0; margin: 0;">
							
							<c:forEach var="d" items="${donneesEtape}" varStatus="status">
								<c:set var="isChampConditionnel" value="${d.hasContrainte && d.etapeMaitre == numEtapeActive}" />
								<c:set var="doitEtreMasque" value="${isChampConditionnel && d.valeurActuelleMaitre != d.valeurCible}" />

								<div class="visu-row row-champ" 
									 id="row-${d.idTemplateDonnee}"
									 style="${doitEtreMasque ? 'display: none;' : 'display: flex;'}"
									 <c:if test="${isChampConditionnel}">
										data-depend-de="${d.idTemplateMaitre}"
										data-valeur-cible="${d.valeurCible}"
									 </c:if>>
									 
									<input type="hidden" name="id_donne_${status.index}" value="${d.idDonne}"> 
									<input type="hidden" name="id_template_donnee_${status.index}" value="${d.idTemplateDonnee}"> 
									<input type="hidden" name="type_${status.index}" value="${d.nomChamp}"> 
									<input type="hidden" name="ref_${status.index}" value="${d.refContrainte}">

									<div style="flex: 1; font-weight: 600; color: #4a5568; padding-right: 15px;">
										${d.nomChamp}
										<c:if test="${d.estObligatoire}">
											<span class="required-star">*</span>
										</c:if>
									</div>

									<div style="flex: 1.5; padding-right: 15px;">
										<span class="view-mode" style="font-size: 15px; color: #2d3748; ${modeEditionForce ? 'display: none;' : ''}">
											${not empty d.attribut ? d.attribut : '<em>(Vide)</em>'}
										</span>

										<div class="edit-mode" style="${modeEditionForce ? 'display: block;' : 'display: none;'}">
											<c:choose>
												<c:when test="${d.refContrainte == 'Bool'}">
													<select name="attr_${status.index}" class="form-control-dyn champ-declencheur" data-id-template="${d.idTemplateDonnee}" ${d.estObligatoire ? 'required' : ''}>
														<option value="">-- Sélectionner --</option>
														<option value="Oui" ${d.attribut == 'Oui' ? 'selected' : ''}>OUI</option>
														<option value="Non" ${d.attribut == 'Non' ? 'selected' : ''}>NON</option>
													</select>
												</c:when>

												<c:when test="${not empty d.refContrainte && not empty mapCatalogues[d.refContrainte]}">
													<select name="attr_${status.index}" class="form-control-dyn champ-declencheur" data-id-template="${d.idTemplateDonnee}" ${d.estObligatoire ? 'required' : ''}>
														<option value="">-- Sélectionner un(e) ${d.refContrainte} --</option>
														<c:forEach var="optionValeur" items="${mapCatalogues[d.refContrainte]}">
															<option value="${optionValeur}" ${d.attribut == optionValeur ? 'selected' : ''}>${optionValeur}</option>
														</c:forEach>
													</select>
												</c:when>

												<c:otherwise>
													<c:choose>
														<c:when test="${d.typeComposant == 'textarea'}">
															<textarea name="attr_${status.index}" class="form-control-dyn champ-declencheur" data-id-template="${d.idTemplateDonnee}" ${d.estObligatoire ? 'required' : ''} placeholder="Saisir...">${d.attribut}</textarea>
														</c:when>
														<c:otherwise>
															<input type="${not empty d.typeComposant ? d.typeComposant : 'text'}"
																name="attr_${status.index}" value="${d.attribut}" class="form-control-dyn champ-declencheur" data-id-template="${d.idTemplateDonnee}"
																${d.estObligatoire ? 'required' : ''} placeholder="Saisir...">
														</c:otherwise>
													</c:choose>
												</c:otherwise>
											</c:choose>
										</div>
									</div>

									<div style="flex: 1.5; display: flex; flex-direction: column; gap: 6px;">
										<c:if test="${d.aCommentaire}">
											<div>
												<div class="view-mode" style="color: #718096; font-size: 0.85em; font-style: italic; ${modeEditionForce ? 'display: none;' : ''}">
													Com. : ${not empty d.commentaire ? d.commentaire : '(Aucun)'}
												</div>
												<div class="edit-mode" style="${modeEditionForce ? 'display: block;' : 'display: none;'}">
													<input type="text" name="comm_${status.index}" value="${d.commentaire}" class="form-control-dyn" placeholder="Ajouter une remarque...">
												</div>
											</div>
										</c:if>

										<c:if test="${d.aDate}">
											<div style="margin-top: 4px;">
												<div class="view-mode" style="font-size: 0.8em; color: #4a5568; ${modeEditionForce ? 'display: none;' : ''}">Date : ${not empty d.date ? d.date : '(Non renseignée)'}</div>
												<div class="edit-mode" style="${modeEditionForce ? 'display: block;' : 'display: none;'}">
													<input type="date" name="date_${status.index}" value="${d.date}" class="form-control-dyn">
												</div>
											</div>
										</c:if>
									</div>
								</div>
							</c:forEach>

							<c:if test="${currentEtape.estFinale}">
								<div class="visu-row" style="background-color: #fffaf0; border-top: 2px dashed #feebc8; margin-top: 20px; padding: 20px 12px;">
									<div style="flex: 1; font-weight: bold; color: #7b341e;">
										Avis de Clôture Définitif
										<span class="required-star">*</span>
										<div class="step-badge" style="background: #feebc8; color: #744210;">Action Finale</div>
									</div>

									<div style="flex: 1.5; padding-right: 15px;">
										<span class="view-mode" style="font-weight: bold; color: #2c5282; ${modeEditionForce ? 'display: none;' : ''}">
											<c:choose>
												<c:when test="${not empty workflow.dateFinalisation}">Dossier Traité / Clôturé</c:when>
												<c:otherwise><em>Clôture en attente de saisie</em></c:otherwise>
											</c:choose>
										</span>
										
										<div class="edit-mode" style="${modeEditionForce ? 'display: block;' : 'display: none;'}">
											<select name="decision_finale" class="form-control-dyn" required>
												<option value="">-- Choisir le verdict final --</option>
												<option value="Faisable">Faisable (Validation)</option>
												<option value="Non Faisable">Non Faisable (Refus global)</option>
												<option value="Faisable sous condition">Faisable sous condition</option>
											</select>
										</div>
									</div>

									<div style="flex: 1.5; display: flex; flex-direction: column; gap: 8px;">
										<div class="view-mode" style="font-size: 0.9em; color: #4a5568; ${modeEditionForce ? 'display: none;' : ''}">
											${not empty workflow.commentaire ? workflow.commentaire : ''}
										</div>
										<div class="edit-mode" style="${modeEditionForce ? 'display: block;' : 'display: none;'}">
											<textarea name="commentaire_final" class="form-control-dyn" rows="2" placeholder="Renseigner le motif de clôture obligatoire..." required></textarea>
										</div>
										<div class="edit-mode" style="${modeEditionForce ? 'display: block;' : 'display: none;'} margin-top: 4px;">
											<span style="font-size: 0.8em; color: #718096; font-weight: bold;">Date de Clôture (Système) :</span>
											<input type="date" name="date_finalisation" value="${currentDateIso}" class="form-control-dyn" readonly style="background: #e2e8f0; color: #4a5568;">
										</div>
									</div>
								</div>
							</c:if>

						</fieldset>
					</form>
				</c:otherwise>
			</c:choose>
		</div>
		
		<div style="margin-top: 20px;">
			<a href="${pageContext.request.contextPath}/downloadPdf?id=${workflow.id}" class="btn-pdf">
				📄 Télécharger le récapitulatif PDF
			</a>
		</div>
	</div>

	<script>
        function activerEdition() {
            document.querySelectorAll('.view-mode').forEach(el => el.style.display = 'none');
            document.querySelectorAll('.edit-mode').forEach(el => el.style.display = 'block');
            document.getElementById('btn-modifier').style.display = 'none';
            document.getElementById('btn-enregistrer').style.display = 'inline-block';
            const fs = document.getElementById('fs-edition');
            if(fs) fs.removeAttribute('disabled');
        }

        document.addEventListener("DOMContentLoaded", function() {
            function evaluerDependances() {
                document.querySelectorAll('.row-champ[data-depend-de]').forEach(function(row) {
                    const idMaitre = row.getAttribute('data-depend-de');
                    const valeurCible = row.getAttribute('data-valeur-cible');
                    const champMaitre = document.querySelector('.champ-declencheur[data-id-template="' + idMaitre + '"]');
                    
                    if (champMaitre) {
                        if (champMaitre.value === valeurCible) {
                            row.style.display = 'flex';
                            row.querySelectorAll('input, select, textarea').forEach(el => el.removeAttribute('disabled'));
                        } else {
                            row.style.setProperty('display', 'none', 'important');
                            row.querySelectorAll('input, select, textarea').forEach(el => el.setAttribute('disabled', 'disabled'));
                        }
                    }
                });
            }

            document.querySelectorAll('.champ-declencheur').forEach(function(champ) {
                champ.addEventListener('change', evaluerDependances);
                champ.addEventListener('input', evaluerDependances);
            });

            evaluerDependances();
        });
    </script>
</body>
</html>