<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="UTF-8">
<title>Lancer un Workflow - Mode Dynamique</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
<style>
.sidebar {
	width: 250px;
	background: #2c3e50;
	color: white;
	min-height: 100vh;
	padding: 20px;
	position: fixed;
}

.sidebar h3 {
	border-bottom: 1px solid #34495e;
	padding-bottom: 10px;
}

.sidebar ul {
	list-style: none;
	padding: 0;
}

.sidebar ul li {
	margin: 15px 0;
}

.sidebar ul li a {
	color: #bdc3c7;
	text-decoration: none;
}

.main-container {
	flex: 1;
	margin-left: 250px;
	padding: 40px;
}

.form-card {
	background: white;
	padding: 30px;
	border-radius: 8px;
	box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
	max-width: 900px;
	margin: auto;
}

/* Stepper Navigation */
.stepper {
	display: flex;
	justify-content: space-between;
	margin-bottom: 30px;
	border-bottom: 2px solid #eee;
	padding-bottom: 10px;
}

.step-head {
	color: #ccc;
	font-weight: bold;
	font-size: 0.9em;
	transition: 0.3s;
	padding-bottom: 5px;
}

.step-head.active {
	color: #3498db;
	border-bottom: 2px solid #3498db;
}

.form-step {
	display: none;
}

.form-step.active {
	display: block;
	animation: fadeIn 0.4s;
}

.grid-form {
	display: grid;
	grid-template-columns: repeat(2, 1fr);
	gap: 20px;
}

.form-group {
	display: flex;
	flex-direction: column;
	margin-bottom: 15px;
}

.full {
	grid-column: span 2;
}

label {
	font-weight: 600;
	margin-bottom: 5px;
	color: #555;
	font-size: 0.9em;
}

input, select, textarea {
	padding: 10px;
	border: 1px solid #ddd;
	border-radius: 4px;
	font-size: 14px;
	background-color: #fafafa;
}

input:focus, select:focus, textarea:focus {
	border-color: #3498db;
	outline: none;
	background-color: #fff;
}

.button-group {
	display: flex;
	justify-content: space-between;
	margin-top: 30px;
	border-top: 1px solid #eee;
	padding-top: 20px;
}

.btn {
	padding: 12px 25px;
	border: none;
	border-radius: 4px;
	cursor: pointer;
	font-weight: bold;
	transition: 0.3s;
}

.btn-next {
	background: #3498db;
	color: white;
}

.btn-prev {
	background: #95a5a6;
	color: white;
}

.btn-submit {
	background: #27ae60;
	color: white;
}

.btn:hover {
	opacity: 0.9;
	transform: translateY(-1px);
}

.alert-danger {
	background-color: #f8d7da;
	color: #721c24;
	padding: 12px;
	border-radius: 4px;
	margin-bottom: 20px;
	border-left: 4px solid #dc3545;
}

@keyframes fadeIn {
	from { opacity:0; transform: translateY(10px); }
	to { opacity: 1; transform: translateY(0); }
}
</style>
</head>
<body>

	<div class="sidebar">
		<h3>Actions</h3>
		<ul>
			<li><a href="home">🏠 Retour Accueil</a></li>
			<li><a href="template-list">⚙️ Liste des Templates</a></li>
		</ul>
	</div>

	<div class="main-container">
		<div class="form-card">

			<div class="stepper">
				<span class="step-head active" id="h-step-1">1. SÉLECTION DU MODÈLE</span> 
				<span class="step-head" id="h-step-2">2. CONFIGURATION INITIALE</span>
			</div>

			<c:if test="${not empty erreur}">
				<div class="alert-danger">${erreur}</div>
			</c:if>

			<form action="creer-workflow" method="post" id="creationForm">
				<div class="form-step active" id="step-1">
					<h2>1 - Spécifications de base du projet</h2>
					<div class="grid-form">
						<div class="form-group full">
							<label>Libellé de l'article / Nom du Projet *</label> 
							<input type="text" name="titre" id="wf-titre" required placeholder="Ex: Saucisson Sec 200g">
						</div>

						<div class="form-group full">
							<label>Modèle de cycle d'approbation (Template) *</label> 
							<select name="id_template" id="id_template" required onchange="chargerChampsDynamiques(this.value)">
								<option value="">-- Choisir un modèle --</option>
								<c:forEach var="t" items="${templates}">
									<option value="${t.id}">${t.titre}</option>
								</c:forEach>
							</select>
						</div>

						<div class="form-group full">
							<label>Description / Objectif du projet</label>
							<textarea name="commentaire_initial" rows="3" placeholder="Notes complémentaires à l'ouverture..."></textarea>
						</div>
					</div>

					<div class="button-group">
						<span></span>
						<button type="button" class="btn btn-next" id="btn-passer-step2" onclick="versEtapeSaisie()" disabled>Champs Étape 1 ➔</button>
					</div>
				</div>

				<div class="form-step" id="step-2">
					<h2 id="titre-etape-dynamique">2 - Saisie des données obligatoires (Étape 1)</h2>

					<div class="grid-form" id="conteneur-dynamique"></div>

					<div class="button-group">
						<button type="button" class="btn btn-prev" onclick="changerEtapeVisuelle(1)">⬅ Retour</button>
						<button type="submit" class="btn btn-submit">🚀 LANCER LE WORKFLOW</button>
					</div>
				</div>

			</form>
		</div>
	</div>

<script>
let structureChamps = [];

function chargerChampsDynamiques(idTemplate) {
    const btn = document.getElementById('btn-passer-step2');

    if (!idTemplate) {
        btn.disabled = true;
        return;
    }

    fetch('creer-workflow?action=getChampsJson&id_template=' + idTemplate)
        .then(response => response.json())
        .then(data => {
            console.log("DATA :", data);
            structureChamps = data;

            // Toujours reconstruire le formulaire dynamique
            construireFormulaireDynamique();

            // Activer le bouton de l'étape 2
            btn.disabled = false;
            
            // Si l'étape 1 est vide de données, on transforme le bouton en soumission directe
            if (!structureChamps || structureChamps.length === 0) {
                btn.innerHTML = "🚀 LANCER LE WORKFLOW DIRECTEMENT";
                btn.className = "btn btn-submit";
            } else {
                btn.innerHTML = "Champs Étape 1 ➔";
                btn.className = "btn btn-next";
            }
        })
        .catch(err => {
            console.error("Erreur :", err);
        });
}

function construireFormulaireDynamique() {
    const conteneur = document.getElementById('conteneur-dynamique');
    conteneur.innerHTML = "";

    if (!structureChamps || structureChamps.length === 0) {
        conteneur.innerHTML = "<p class='full' style='color: #7f8c8d; font-style: italic;'>Aucun champ à renseigner pour ce modèle, vous pouvez lancer le projet directement.</p>";
        conteneur.insertAdjacentHTML("beforeend", `<input type="hidden" name="total_champs" value="0">`);
        return;
    }

    conteneur.insertAdjacentHTML("beforeend", `<input type="hidden" name="total_champs" value="${structureChamps.length}">`);

    structureChamps.forEach((champ, index) => {
        // 🛠️ TOUS LES CHAMPS SONT OBLIGATOIRES : On affiche l'astérisque '*' d'office
        let html = `
            <div class="form-group">
                <label>\${champ.nomChamp} *</label>				
                <input type="hidden" name="id_template_donnee_${index}" value="\${champ.idTemplateDonnee}">
                <input type="hidden" name="type_${index}" value="\${champ.nomChamp}">
                <input type="hidden" name="ref_${index}" value="\${champ.refContrainte}">
        `;
        
        // 🛠️ TOUS LES INPUTS ET SELECTS ONT MAINTENANT L'ATTRIBUT 'required' EN DUR
        if (champ.refContrainte === 'Bool') {
            html += `
                <select name="attr_${index}" required>
                    <option value="">-- Choisir --</option>
                    <option value="OUI">OUI</option>
                    <option value="NON">NON</option>
                </select>
            `;
        } else if (champ.refContrainte === 'avis') {
            html += `
                <select name="attr_${index}" required>
                    <option value="">-- Choisir --</option>
                    <option value="Faisable">Faisable</option>
                    <option value="Non faisable">Non faisable</option>
                    <option value="À l'étude">À l'étude</option>
                </select>
            `;
        } else {
            let typeInput = champ.typeComposant ? champ.typeComposant : "text";
            html += `<input type="\${typeInput}" name="attr_${index}" required placeholder="Saisir la valeur...">`;
        }

        // Si le champ demande un commentaire annexe, on le rend obligatoire aussi pour être cohérent
        if (champ.aCommentaire) {
            html += `<input type="text" name="comm_${index}" required placeholder="Commentaire obligatoire..." style="margin-top: 5px;">`;
        }

        // Si le champ demande une date, elle devient obligatoire également
        if (champ.aDate) {
            html += `<input type="date" name="date_${index}" required style="margin-top: 5px;">`;
        }

        html += `</div>`;
        conteneur.insertAdjacentHTML("beforeend", html);
    });
}

function versEtapeSaisie() {
    // Validation native du Titre et du Modèle (Template) sur le premier écran
    const titreField = document.getElementById('wf-titre');
    const templateField = document.getElementById('id_template');

    if (!titreField.reportValidity() || !templateField.reportValidity()) {
        return; // Bloque si le titre ou le template n'est pas choisi
    }

    // Si aucune donnée dynamique, on traite directement l'envoi en base de données
    if (!structureChamps || structureChamps.length === 0) {
        document.getElementById('creationForm').submit();
        return;
    }

    // Passage visuel à l'étape 2 (les champs dynamiques)
    changerEtapeVisuelle(2);
}

function changerEtapeVisuelle(step) {
    document.querySelectorAll('.form-step').forEach(el => el.classList.remove('active'));
    document.querySelectorAll('.step-head').forEach(el => el.classList.remove('active'));

    document.getElementById('step-' + step).classList.add('active');

    for (let i = 1; i <= step; i++) {
        document.getElementById('h-step-' + i).classList.add('active');
    }
}
</script>
</body>
</html>