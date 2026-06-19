<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ taglib uri="jakarta.tags.functions" prefix="fn"%>

<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Configuration Avancée des Contraintes</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <style>
        .main-container { padding: 40px; font-family: 'Segoe UI', sans-serif; max-width: 1300px; margin: auto; }
        .card { background: white; padding: 25px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); margin-bottom: 25px; }
        .grid-split { display: grid; grid-template-columns: 1fr 1.5fr; gap: 30px; }
        .form-group { display: flex; flex-direction: column; margin-bottom: 15px; }
        label { font-weight: 600; margin-bottom: 5px; color: #4a5568; font-size: 0.9em; }
        input, select { padding: 10px; border: 1px solid #cbd5e0; border-radius: 4px; font-size: 14px; background-color: #fff; }
        .btn { padding: 10px 20px; border: none; border-radius: 4px; font-weight: bold; cursor: pointer; transition: 0.2s; text-align: center; }
        .btn-success { background: #38a169; color: white; }
        .btn-primary { background: #3182ce; color: white; }
        .btn-danger { background: #e53e3e; color: white; }
        .btn-secondary { background: #a0aec0; color: white; }
        
        .box-contrainte { border: 1px solid #e2e8f0; background: #f7fafc; padding: 15px; border-radius: 6px; margin-bottom: 15px; display: flex; justify-content: space-between; align-items: center; }
        .info-metier { font-size: 0.95em; color: #2d3748; line-height: 1.5; }
        .info-etape { font-size: 0.8em; font-weight: bold; color: #718096; text-transform: uppercase; }
        .expression-logique { font-family: 'Courier New', Courier, monospace; font-weight: bold; font-size: 1.1em; color: #2b6cb0; margin-top: 4px; }
    </style>
</head>
<body class="bg-light">

    <div class="main-container">
        <div class="card" style="border-left: 5px solid #3182ce;">
            <h2>Configuration des règles sur le champ : <span style="color: #3182ce;">${donneeActive.nomChamp}</span></h2>
            <p class="text-muted" style="margin: 5px 0 0 0;">Étape Actuelle : <strong>Étape ${etapeActive.place}</strong> | Ordre d'affichage : <strong>${donneeActive.ordreAffichage}</strong></p>
        </div>

<div class="grid-boutons">
    <c:forEach var="etape" items="${etapesTemplate}">
        
        <%-- Construit la clé de recherche, ex: "[1]" ou "[2]" --%>
        <c:set var="cleEtape" value="[${etape.place}]" />
        
        <%-- 1. Une étape est validée UNIQUEMENT si son identifiant de place est présent dans l'historique BDD --%>
        <c:set var="isValidee" value="${fn:contains(etapesValideesChaine, cleEtape)}" />

        <%-- 2. GESTION DU DÉBLOCAGE STRICT (attente_place) --%>
        <c:choose>
            <%-- L'étape 1 est toujours accessible au départ --%>
            <c:when test="${etape.place == 1}">
                <c:set var="parentFait" value="true" />
            </c:when>
            
            <%-- Pas de contrainte d'attente spécifiée -> Dépend de la validation de l'étape précédente (ex: place - 1) --%>
            <c:when test="${empty etape.attentePlace || etape.attentePlace == 0}">
                <c:set var="clePrecedente" value="[${etape.place - 1}]" />
                <c:set var="parentFait" value="${fn:contains(etapesValideesChaine, clePrecedente)}" />
            </c:when>
            
            <%-- Contrainte présente -> Débloqué uniquement si l'étape attendue spécifique est validée en BDD --%>
            <c:otherwise>
                <c:set var="cleAttente" value="[${etape.attentePlace}]" />
                <c:set var="parentFait" value="${fn:contains(etapesValideesChaine, cleAttente)}" />
            </c:otherwise>
        </c:choose>

        <%-- 3. Déduction de l'état visuel et des verrous d'accès --%>
        <c:set var="isEnCours" value="${!isValidee && parentFait}" />
        <c:set var="isBloquee" value="${!isValidee && !parentFait}" />

        <c:choose>
            <c:when test="${isValidee}"><c:set var="colorClass" value="etape-validee" /></c:when>
            <c:when test="${isEnCours}"><c:set var="colorClass" value="etape-non-faite" /></c:when>
            <c:otherwise><c:set var="colorClass" value="etape-bloquee" /></c:otherwise>
        </c:choose>

        <c:set var="activeFocusClass" value="${etape.place == numEtapeActive ? 'state-active-focus' : ''}" />

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

            <div class="card">
                <h3 style="margin-top: 0;">Contraintes actives requises</h3>
                
                <c:choose>
                    <c:when test="${empty contraintesAssociees}">
                        <div style="text-align: center; padding: 30px; color: #a0aec0; border: 1px dashed #cbd5e0; border-radius: 6px;">
                            Aucune règle de contrainte définie pour ce champ.
                        </div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="c" items="${contraintesAssociees}">
                            <div class="box-contrainte">
                                <div class="info-metier">
                                    <span class="info-etape">Étape ${c.placeEtapeSource}</span><br>
                                    Champ : <strong>${c.nomChampSource}</strong>
                                    <div class="expression-logique">
                                        ${donneeActive.nomChamp} doit être ${c.symboleCondition} ${c.valeurAttendue}
                                    </div>
                                </div>
                                <div style="display: flex; gap: 8px;">
                                    <button type="button" class="btn btn-primary" style="padding: 6px 12px; font-size: 0.85em;"
                                            onclick="chargerModification('${c.idContrainte}', '${c.idEtapeSource}', '${c.idDonneeSource}', '${c.idCondition}', '${c.valeurAttendue}')">
                                        Modifier
                                    </button>
                                    <a href="action-contrainte?action=DELETE&id_donnee=${donneeActive.id}&id_contrainte=${c.idContrainte}" 
                                       class="btn btn-danger" style="padding: 6px 12px; font-size: 0.85em; text-decoration: none;"
                                       onclick="return confirm('Supprimer cette règle ?');">
                                        Supprimer
                                    </a>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <script>
        // 1. Récupération et tri du référentiel des conditions
        const conditionsBrutes = [
            <c:forEach var="cond" items="${conditionsReferentiel}" varStatus="status">
                { id: "${cond.id}", symbole: "${cond.condition}" }${!status.last ? ',' : ''}
            </c:forEach>
        ];

        const ordreSouhaite = ["1", "3", "4", "5", "6", "2"];

        const conditionsRef = conditionsBrutes
            .map(c => {
                if (c.id === "2") c.symbole = "!=";
                return c;
            })
            .sort((a, b) => ordreSouhaite.indexOf(a.id) - ordreSouhaite.indexOf(b.id));

        // 2. Base de données des champs du workflow
        const baseDonnees = [
            <c:forEach var="d" items="${toutesDonnees}" varStatus="status">
                { id: "${d.id}", idEtape: "${d.id_template_etape}", nom: "${d.nom_champ}", composant: "${d.type_composant}", ordre: ${d.ordre_affichage}, ref: "${d.ref_contrainte}" }${!status.last ? ',' : ''}
            </c:forEach>
        ];

        // 3. Catalogues de contraintes physiques
        const cataloguesContraintes = {
            <c:forEach var="entry" items="${mapCatalogues}" varStatus="status">
                "${entry.key}": [ <c:forEach var="val" items="${entry.value}" varStatus="vStatus">"${val}"${!vStatus.last ? ',' : ''}</c:forEach> ]${!status.last ? ',' : ''}
            </c:forEach>
        };

        const idEtActuelle = "${etapeActive.id}";
        const ordreActuel = Number("${donneeActive.ordreAffichage}");

        // 4. Moteur de filtrage des données sources
        function filtrerDonnees() {
            const selectEtape = document.getElementById('select_etape');
            const selectDonnee = document.getElementById('select_donnee');
            const etapeSelectionnee = selectEtape.value;
            
            selectDonnee.innerHTML = '<option value="" disabled selected>-- Choisir la donnée --</option>';
            
            const donneesFiltrees = baseDonnees.filter(d => {
                if (String(d.idEtape) === String(etapeSelectionnee)) {
                    if (String(etapeSelectionnee) === String(idEtActuelle)) {
                        return Number(d.ordre) < ordreActuel;
                    }
                    return true;
                }
                return false;
            });

            donneesFiltrees.forEach(d => {
                let opt = document.createElement('option');
                opt.value = d.id;
                opt.innerText = d.nom;
                selectDonnee.appendChild(opt);
            });

            if (donneesFiltrees.length > 0) {
                selectDonnee.disabled = false;
            } else {
                selectDonnee.disabled = true;
                let opt = document.createElement('option');
                opt.value = "";
                opt.disabled = true;
                opt.selected = true;
                opt.innerText = "-- Aucune donnée valide trouvée --";
                selectDonnee.appendChild(opt);
            }
            
            document.getElementById('select_condition').disabled = true;
        }

        // 5. Moteur de filtrage des opérateurs et génération des inputs
        function filtrerConditionsEtInputs() {
            const idDonneeSel = document.getElementById('select_donnee').value;
            const donneeObj = baseDonnees.find(d => String(d.id) === String(idDonneeSel));
            const selectCondition = document.getElementById('select_condition');
            const conteneurInput = document.getElementById('conteneur_input_dynamique');
            
            selectCondition.innerHTML = '<option value="" disabled selected>-- Choisir la condition --</option>';
            
            if (!donneeObj) return;

            let estTexte = (donneeObj.composant.toLowerCase().includes('text') || donneeObj.composant.toLowerCase().includes('select'));
            
            conditionsRef.forEach(c => {
                if (estTexte && c.id !== "1" && c.id !== "2") return; 
                
                let opt = document.createElement('option');
                opt.value = c.id;
                opt.innerText = c.symbole;
                selectCondition.appendChild(opt);
            });
            selectCondition.disabled = false;

            if (donneeObj.ref && cataloguesContraintes[donneeObj.ref]) {
                let selectDyn = document.createElement('select');
                selectDyn.name = "valeur_contrainte";
                selectDyn.id = "input_valeur";
                selectDyn.required = true;
                
                cataloguesContraintes[donneeObj.ref].forEach(valeur => {
                    let o = document.createElement('option');
                    o.value = valeur;
                    o.innerText = valeur;
                    selectDyn.appendChild(o);
                });
                conteneurInput.innerHTML = "";
                conteneurInput.appendChild(selectDyn);
            } else {
                conteneurInput.innerHTML = '<input type="text" name="valeur_contrainte" id="input_valeur" required placeholder="Entrez la valeur...">';
            }
        }

        function chargerModification(idContrainte, idEtape, idDonnee, idCondition, valeurAttendue) {
            document.getElementById('form-title').innerText = "Modifier la Contrainte";
            document.getElementById('form-action').value = "PUT";
            document.getElementById('id_jointure').value = idContrainte; 
            document.getElementById('btn-cancel').style.display = "inline-block";
            document.getElementById('btn-submit').innerText = "Enregistrer";

            document.getElementById('select_etape').value = idEtape;
            filtrerDonnees();
            document.getElementById('select_donnee').value = idDonnee;
            filtrerConditionsEtInputs();
            document.getElementById('select_condition').value = idCondition;
            document.getElementById('input_valeur').value = valeurAttendue;
        }

        function annulerModif() {
            document.getElementById('regleForm').reset();
            document.getElementById('form-title').innerText = "Ajouter une contrainte de validation";
            document.getElementById('form-action').value = "POST";
            document.getElementById('btn-cancel').style.display = "none";
            document.getElementById('btn-submit').innerText = "Valider la contrainte";
            document.getElementById('select_donnee').disabled = true;
            document.getElementById('select_condition').disabled = true;
            document.getElementById('conteneur_input_dynamique').innerHTML = '<input type="text" name="valeur_contrainte" id="input_valeur" required placeholder="Saisir une valeur...">';
        }
    </script>
</body>
</html>