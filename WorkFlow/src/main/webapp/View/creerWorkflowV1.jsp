<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
    <meta charset="UTF-8">
    <title>Lancer un Workflow - Création</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/inte.css" />
    <style>
        /* Sidebar propre */
        .sidebar { width: 250px; background: var(--primary); color: white; min-height: 100vh; padding: 20px; position: fixed; }
        .sidebar h3 { border-bottom: 1px solid #34495e; padding-bottom: 10px; }
        .sidebar ul { list-style: none; padding: 0; }
        .sidebar ul li { margin: 15px 0; }
        .sidebar ul li a { color: #bdc3c7; text-decoration: none; }

        .main-container { flex: 1; margin-left: 250px; padding: 40px; }
        .form-card { background: white; padding: 30px; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); max-width: 900px; margin: auto; }
        
        /* Stepper */
        .stepper { display: flex; justify-content: space-between; margin-bottom: 30px; border-bottom: 2px solid #eee; padding-bottom: 10px; }
        .step-head { color: #ccc; font-weight: bold; font-size: 0.9em; transition: 0.3s; }
        .step-head.active { color: var(--accent); border-bottom: 2px solid var(--accent); }

        /* Form sections bien */
        .form-step { display: none; }
        .form-step.active { display: block; animation: fadeIn 0.4s; }

        .grid-form { display: grid; grid-template-columns: repeat(2, 1fr); gap: 20px; }
        .form-group { display: flex; flex-direction: column; margin-bottom: 15px; }
        .full { grid-column: span 2; }
        
        /* Conteneur masqué pour l'export */
        .export-conditional { display: none; grid-column: span 2; grid-template-columns: repeat(2, 1fr); gap: 20px; }
        
        label { font-weight: 600; margin-bottom: 5px; color: #555; font-size: 0.9em; }
        input, select { padding: 10px; border: 1px solid #ddd; border-radius: 4px; font-size: 14px; }

        .button-group { display: flex; justify-content: space-between; margin-top: 30px; border-top: 1px solid #eee; padding-top: 20px; }
        .btn { padding: 12px 25px; border: none; border-radius: 4px; cursor: pointer; font-weight: bold; transition: 0.3s; }
        .btn-next { background: var(--accent); color: white; }
        .btn-prev { background: #95a5a6; color: white; }
        .btn-submit { background: var(--success); color: white; }
        .btn:hover { opacity: 0.9; transform: translateY(-1px); }

        @keyframes fadeIn { from { opacity: 0; transform: translateY(10px); } to { opacity: 1; transform: translateY(0); } }
    </style>
</head>
<body>

    <div class="sidebar">
        <h3>Actions</h3>
        <ul>
            <li><a href="home">🏠 Retour Accueil</a></li>
            <li><a href="#">📋 Mes Workflows</a></li>
        </ul>
    </div>

    <div class="main-container">
        <div class="form-card">
            
            <div class="stepper">
                <span class="step-head active" id="h-step-1">1. INFOS GÉNÉRALES</span>
                <span class="step-head" id="h-step-2">2. UNITÉ DE VENTE</span>
                <span class="step-head" id="h-step-3">3. LOGISTIQUE</span>
            </div>

            <form action="creer-workflow" method="post" id="multiStepForm">
                
                <div class="form-step active" id="step-1">
                    <h2>1 - Informations générales</h2>
                    <div class="grid-form">
                        <div class="form-group full">
                            <label>Libellé article (Titre du Workflow) *</label>
                            <input type="text" name="titre" required placeholder="Ex: Saucisson Sec 200g">
                        </div>

                        <div class="form-group">
                            <label>Client *</label>
                            <input type="hidden" name="ref_client" value="client">
                            <select name="attr_client" required>
                                <option value="" disabled selected>Sélectionnez un client</option>
                                <c:forEach var="opt" items="${optionsClient}"><option value="${opt}">${opt}</option></c:forEach>
                            </select>
                        </div>

                        <div class="form-group">
                            <label>Marque *</label>
                            <input type="hidden" name="ref_marque" value="Marque">
                            <select name="attr_marque" required>
                                <option value="" disabled selected>Sélectionnez une marque</option>
                                <c:forEach var="opt" items="${optionsMarque}"><option value="${opt}">${opt}</option></c:forEach>
                            </select>
                        </div>

                        <div class="form-group">
                            <label>Priorité réponse *</label>
                            <input type="hidden" name="ref_reponse" value="reponse">
                            <select name="attr_reponse" required>
                                <option value="" disabled selected>Sélectionnez une priorité</option>
                                <c:forEach var="opt" items="${optionsReponse}"><option value="${opt}">${opt}</option></c:forEach>
                            </select>
                        </div>
                    </div>
                    <div class="button-group">
                        <span></span>
                        <button type="button" class="btn btn-next" onclick="validateAndNext(1, 2)">Suivant ➔</button>
                    </div>
                </div>

                <div class="form-step" id="step-2">
                    <h2>Unité de vente consommateur</h2>
                    <div class="grid-form">
                        <div class="form-group">
                            <label>Rayon *</label>
                            <input type="hidden" name="ref_rayon" value="rayon">
                            <select name="attr_rayon" required>
                                <option value="" disabled selected>Sélectionnez un rayon</option>
                                <c:forEach var="opt" items="${optionsRayon}"><option value="${opt}">${opt}</option></c:forEach>
                            </select>
                        </div>

                        <div class="form-group">
                            <label>Famille *</label>
                            <input type="hidden" name="ref_famille" value="famille">
                            <select name="attr_famille" required>
                                <option value="" disabled selected>Sélectionnez une famille</option>
                                <c:forEach var="opt" items="${optionsFamille}"><option value="${opt}">${opt}</option></c:forEach>
                            </select>
                        </div>

                        <div class="form-group">
                            <label>Date départ usine souhaitée *</label>
                            <input type="date" name="attr_date_souhaite" required>
                        </div>

                        <div class="form-group">
                            <label>Code référence *</label>
                            <input type="text" name="attr_code_ref" placeholder="Code interne" required>
                        </div>
                    </div>
                    <div class="button-group">
                        <button type="button" class="btn btn-prev" onclick="nextStep(1)">⬅ Précédent</button>
                        <button type="button" class="btn btn-next" onclick="validateAndNext(2, 3)">Suivant ➔</button>
                    </div>
                </div>

                <div class="form-step" id="step-3">
                    <h2>Unité logistique</h2>
                    <div class="grid-form">
                        <div class="form-group">
                            <label>Unité d'œuvre *</label>
                            <input type="hidden" name="ref_uo" value="unite oeuvre">
                            <select name="attr_uo" required>
                                <option value="" disabled selected>Sélectionnez une UO</option>
                                <c:forEach var="opt" items="${optionsUO}"><option value="${opt}">${opt}</option></c:forEach>
                            </select>
                        </div>

                        <div class="form-group">
                            <label>Création emballage *</label>
                            <input type="hidden" name="ref_emballage" value="Bool">
                            <select name="attr_emballage" required>
                                <option value="" disabled selected>Sélectionnez une option</option>
                                <c:forEach var="opt" items="${optionsBool}"><option value="${opt}">${opt}</option></c:forEach>
                            </select>
                        </div>

                        <div class="form-group">
                            <label>Pièces par colis *</label>
                            <input type="number" name="attr_pcs_colis" min="1" required>
                        </div>

                        <div class="form-group">
                            <label>Marge attendue (%) *</label>
                            <input type="text" name="comm_marge" placeholder="Ex: 15%" required>
                        </div>

                        <div class="form-group full">
                            <label>Produit destiné à l'export ? *</label>
                            <input type="hidden" name="ref_export" value="Bool">
                            <select name="attr_export" id="exportSelect" onchange="toggleExportFields()" required>
                                <option value="" disabled selected>Sélectionnez une option</option>
                                <option value="Oui">Oui</option>
                                <option value="Non">Non</option>
                            </select>
                        </div>

                        <div id="exportFields" class="export-conditional">
                            <div class="form-group">
                                <label>Pays destinataire *</label>
                                <input type="hidden" name="ref_pays_destinataire" value="pays">
                                <input type="text" name="attr_pays_destinataire" id="paysInput" placeholder="Ex: Allemagne, Canada">
                            </div>

                            <div class="form-group">
                                <label>Format D.D.M attendu *</label>
                                <input type="hidden" name="ref_ddm_export" value="ddm">
                                <input type="text" name="attr_ddm_export" id="ddmInput" placeholder="Ex: MM/AAAA ou JJ/MM/AAAA">
                            </div>
                        </div>

                    </div>
                    <div class="button-group">
                        <button type="button" class="btn btn-prev" onclick="nextStep(2)">⬅ Précédent</button>
                        <button type="submit" class="btn btn-submit">🚀 LANCER LE WORKFLOW</button>
                    </div>
                </div>

            </form>
        </div>
    </div>

    <script>
        // Gestion de la navigation entre les étapes
        结构
        function nextStep(step) {
            document.querySelectorAll('.form-step').forEach(el => el.classList.remove('active'));
            document.querySelectorAll('.step-head').forEach(el => el.classList.remove('active'));
            
            document.getElementById('step-' + step).classList.add('active');
            
            for(let i=1; i<=step; i++) {
                document.getElementById('h-step-' + i).classList.add('active');
            }
        }

        // Vérification des champs requis avant de changer d'étape
        function validateAndNext(currentStep, targetStep) {
            const currentStepEl = document.getElementById('step-' + currentStep);
            const inputs = currentStepEl.querySelectorAll('input[required], select[required]');
            let isValid = true;

            inputs.forEach(input => {
                if (!input.value) {
                    isValid = false;
                    input.style.borderColor = 'var(--danger)';
                } else {
                    input.style.borderColor = '#ddd';
                }
            });

            if (isValid) {
                nextStep(targetStep);
            } else {
                alert("Veuillez remplir tous les champs obligatoires de cette étape avant de continuer.");
            }
        }

        // Affichage dynamique des champs Pays et DDM si Export = Oui
        function toggleExportFields() {
            const exportSelect = document.getElementById('exportSelect');
            const exportFields = document.getElementById('exportFields');
            const paysInput = document.getElementById('paysInput');
            const ddmInput = document.getElementById('ddmInput');

            if (exportSelect.value === 'Oui') {
                exportFields.style.display = 'grid';
                paysInput.setAttribute('required', 'required');
                ddmInput.setAttribute('required', 'required');
            } else {
                exportFields.style.display = 'none';
                paysInput.removeAttribute('required');
                ddmInput.removeAttribute('required');
                paysInput.value = '';
                ddmInput.value = '';
            }
        }
    </script>
</body>
</html>