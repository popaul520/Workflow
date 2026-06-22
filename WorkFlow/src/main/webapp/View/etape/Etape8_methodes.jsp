<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<div class="form-container" style="padding: 20px; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); font-family: 'Segoe UI', Arial, sans-serif;">
    <h2 style="color: #34495e; border-bottom: 2px solid #34495e; padding-bottom: 10px;">
        Étape 8 : Actions Méthodes
    </h2>
        
    <form action="${pageContext.request.contextPath}/etapeController" method="post" id="formMethode">     
        <input type="hidden" name="id_workflow" value="${param.id_workflow}">
        <input type="hidden" name="current_n" value="8">

        <%-- --- BLOC 1 : CRÉATION CODE(S) --- --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px; background-color: #f8f9fa;">
            <input type="hidden" name="type_codes" value="Création code(s)"> 
            <input type="hidden" name="ref_codes" value="Bool"> 
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Création code(s) :</label>
            <select name="attr_codes" required style="width: 100%; padding: 10px; margin-bottom: 10px; border: 1px solid #ccc; border-radius: 4px;">
                <option value="">-- Sélectionner --</option>
                <c:forEach var="opt" items="${optionsBool}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire(s) codes :</label>
            <textarea name="comm_codes" placeholder="Codes articles..." style="width: 100%; padding: 10px; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
        </div>

        <%-- --- BLOC 2 : CRÉATION NOMENCLATURE --- --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px;">
            <input type="hidden" name="type_nomen" value="Création nomenclature"> 
            <input type="hidden" name="ref_nomen" value="Bool"> 
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Création nomenclature :</label>
            <select name="attr_nomen" required style="width: 100%; padding: 10px; margin-bottom: 10px; border: 1px solid #ccc; border-radius: 4px;">
                <option value="">-- Sélectionner --</option>
                <c:forEach var="opt" items="${optionsBool}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire(s) nomenclature :</label>
            <textarea name="comm_nomen" placeholder="Détails des composants..." style="width: 100%; padding: 10px; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
        </div>

        <%-- --- BLOC 3 : CRÉATION GAMME --- --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px; background-color: #f8f9fa;">
            <input type="hidden" name="type_gamme" value="Création gamme"> 
            <input type="hidden" name="ref_gamme" value="Bool"> 
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Création gamme :</label>
            <select name="attr_gamme" required style="width: 100%; padding: 10px; margin-bottom: 10px; border: 1px solid #ccc; border-radius: 4px;">
                <option value="">-- Sélectionner --</option>
                <c:forEach var="opt" items="${optionsBool}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire(s) gamme :</label>
            <textarea name="comm_gamme" placeholder="Étapes de production..." style="width: 100%; padding: 10px; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
        </div>

        <%-- --- SÉLECTION DU SERVICE DE PRODUCTION --- --%>
        <div id="blocSelectionService" class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #34495e; border-radius: 5px; background-color: #f1f2f6;">
            <label style="display: block; font-weight: bold; margin-bottom: 5px; color: #2c3e50;">Service Production Concerné :</label>
            <select id="selectService" onchange="
                var service = this.value;
                var b = document.getElementById('blocProduction');
                if(service !== '' && service !== 'aucun') {
                    b.style.display = 'block';
                    document.getElementById('titreServiceSelectionne').textContent = 'Données de production pour : ' + service;
                } else {
                    b.style.display = 'none';
                    document.getElementById('attr_qte_ref').value = '';
                    document.getElementById('attr_cadence').value = '';
                    document.getElementById('attr_mod').value = '';
                    document.getElementById('comm_prod').value = '';
                }
            " style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px;">
                <option value="">-- Sélectionner un service --</option>
                <option value="Reconditionnement">Reconditionnement</option>
                <option value="Conditionnement">Conditionnement</option>
                <option value="Décrochage">Décrochage</option>
                <option value="Fumage/Emrobage">Fumage/Emrobage</option>
                <option value="Embassage">Embassage</option>
                <option value="Hachage">Hachage</option>
                <option value="Epices">Epices</option>
                <option value="aucun">Aucun</option>
            </select>
        </div>

        <%-- --- BLOC 4 : DONNÉES DE RÉFÉRENCE (CORRIGÉ : sans required dynamique) --- --%>
        <div id="blocProduction" style="display: none; margin-bottom: 20px; padding: 15px; border: 2px dashed #34495e; border-radius: 5px; background-color: #fdfdfd;">
            <h3 id="titreServiceSelectionne" style="margin-top: 0; color: #2980b9; font-size: 15px; text-transform: uppercase;"></h3>
            
            <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 15px;">
                <div>
                    <label style="display: block; font-weight: bold; margin-bottom: 5px;">Quantité de réf UEA :</label>
                    <input type="number" id="attr_qte_ref" placeholder="Quantité" style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">
                </div>
                <div>
                    <label style="display: block; font-weight: bold; margin-bottom: 5px;">Cadence :</label>
                    <input type="text" id="attr_cadence" placeholder="ex: 50 u/min" style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">
                </div>
                <div>
                    <label style="display: block; font-weight: bold; margin-bottom: 5px;">M.O.D. :</label>
                    <input type="text" id="attr_mod" placeholder="Main d'oeuvre" style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">
                </div>
            </div>
            <div style="margin-top: 15px;"> 
                <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire(s) production :</label>
                <textarea id="comm_prod" placeholder="Précisions sur les cadences..." style="width: 100%; padding: 10px; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
            </div>
            
            <div style="text-align: right; margin-top: 15px;">
                <button type="button" onclick="
                    var sSelect = document.getElementById('selectService');
                    var service = sSelect.value;
                    var qte = document.getElementById('attr_qte_ref').value.trim();
                    var cad = document.getElementById('attr_cadence').value.trim();
                    var mod = document.getElementById('attr_mod').value.trim();
                    var com = document.getElementById('comm_prod').value.trim();

                    if (!qte || !cad || !mod) {
                        alert('Veuillez remplir tous les champs de production avant d\'ajouter ce service.');
                        return;
                    }

                    var badge = document.createElement('div');
                    badge.style = 'background: #2ecc71; color: white; padding: 10px; margin-bottom: 5px; border-radius: 4px; font-size: 14px; font-weight: bold; display: flex; justify-content: space-between; align-items: center;';
                    badge.innerHTML = '<span>✓ <b>' + service + '</b> — Qté: ' + qte + ' | Cadence: ' + cad + ' | M.O.D: ' + mod + '</span>';
                    document.getElementById('listeServicesAjoutes').appendChild(badge);

                    var container = document.getElementById('inputsMasquesConteneur');
                    var names = ['prod_service_nom[]', 'prod_qte[]', 'prod_cadence[]', 'prod_mod[]', 'prod_comm[]'];
                    var vals = [service, qte, cad, mod, com];
                    for(var i=0; i<5; i++){
                        var inp = document.createElement('input'); inp.type = 'hidden'; inp.name = names[i]; inp.value = vals[i];
                        container.appendChild(inp);
                    }

                    var opt = sSelect.querySelector('option[value=\'' + service + '\']');
                    if (opt) { opt.remove(); }

                    sSelect.value = '';
                    document.getElementById('blocProduction').style.display = 'none';
                    document.getElementById('attr_qte_ref').value = '';
                    document.getElementById('attr_cadence').value = '';
                    document.getElementById('attr_mod').value = '';
                    document.getElementById('comm_prod').value = '';
                " style="background-color: #27ae60; color: white; padding: 8px 20px; border: none; border-radius: 4px; cursor: pointer; font-weight: bold;">
                    Valider et ajouter ce service
                </button>
            </div>
        </div>

        <div id="listeServicesAjoutes" style="margin-bottom: 20px;"></div>
        
        <div id="inputsMasquesConteneur">
            <input type="hidden" name="type_production" value="Données de Production">
        </div>
        <div style="text-align: right; margin-top: 20px;">
            <button type="submit" style="background-color: #34495e; color: white; padding: 15px 40px; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; font-size: 16px;">
                 Finaliser les Actions Méthodes
            </button>
        </div>
    </form>
</div>