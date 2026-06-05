<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.DonneeDAO" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

<div class="form-container" style="padding: 20px; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); font-family: 'Segoe UI', Arial, sans-serif;">
    <h2 style="color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px;">
        Étape 2 : Faisabilité Conditionnement
    </h2>
        
    <form action="${pageContext.request.contextPath}/etapeController" method="post">     
        <%-- Context ID --%>
        <input type="hidden" name="id_workflow" value="${param.id_workflow}">
        <input type="hidden" name="current_n" value="2">

        <%-- BLOC 1 : CONFIGURATION & COMPATIBILITÉ MACHINE --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px; background-color: #f9f9f9;">
            <input type="hidden" name="type_machine" value="Configuration Machine">
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Routage machine (Attribut) :</label>
            <input type="text" name="attr_machine" placeholder="Ex: Ligne 4, Cellule B..." style="width: 100%; padding: 10px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">	
            
            <%-- Ajout Compatibilité Machine --%>
            <input type="hidden" name="type_compatibilite" value="Compatibilité machine">
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Compatibilité machine * :</label>
            <select name="attr_compatibilite" required style="width: 100%; padding: 10px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 4px;">
                <option value="">-- Sélectionner la compatibilité --</option>
                <option value="Compatible">Compatible</option>
                <option value="Compatible sous condition">Compatible sous condition</option>
                <option value="Incompatible">Incompatible</option>
            </select>

            <%-- Ajout Capacitaire --%>
            <input type="hidden" name="type_capacitaire" value="Capacitaire">
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Capacitaire / Cadence prévue :</label>
            <input type="text" name="attr_capacitaire" placeholder="Ex: 45 coups/min, Volume OK..." style="width: 100%; padding: 10px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">

            <%-- Ajout Adaptations Nécessaires --%>
            <input type="hidden" name="type_adaptations" value="Adaptations nécessaires">
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Adaptations nécessaires (Outillages, formats...) :</label>
            <textarea name="comm_adaptations" placeholder="Précisez les modifications ou outillages à prévoir..." style="width: 100%; padding: 10px; height: 60px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>

            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire technique général :</label>
            <textarea name="comm_machine" style="width: 100%; padding: 10px; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
        </div>

        <%-- --- BLOC 2 : BESOIN D'ESSAIS --- --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px;">
            <input type="hidden" name="type_essais" value="Besoin d'essais"> 
            <input type="hidden" name="ref_essais" value="Bool"> 
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Besoin d'essais ? (Attribut) :</label>
            <select name="attr_essais" id="selectEssais" onchange="gererObligationDate()" required style="width: 100%; padding: 10px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 4px;">
                <option value="">-- Sélectionner (Oui/Non) --</option>
                <c:forEach var="opt" items="${optionsBool}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Détails (Commentaire) :</label>
            <textarea name="comm_essais" style="width: 100%; padding: 10px; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
            
            <label style="display: block; font-weight: bold; margin-top: 10px; margin-bottom: 5px;">
                Date prévisionnelle <span id="asterisqueDate" style="color: red; display: none;">*</span> :
            </label>
            <input type="date" name="date_essais" id="inputDate" style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">
        </div>

        <%-- --- BLOC 3 : AVIS CONDITIONNEMENT --- --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px;">
            <input type="hidden" name="type_avis" value="avis CONDITIONNEMNT"> 
            <input type="hidden" name="ref_avis" value="avis"> 
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Décision finale (Attribut) :</label>
            <select name="attr_avis" required style="width: 100%; padding: 10px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 4px;">
                <option value="">-- Choisir un avis --</option>
                <c:forEach var="opt" items="${optionsAvis}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire de l'avis :</label>
            <textarea name="comm_avis" style="width: 100%; padding: 10px; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
            <label style="display: block; font-weight: bold; margin-top: 10px; margin-bottom: 5px;">Date de validation :</label>
            <input type="hidden" name="date_avis" value="CURRENT_DATE">
        </div>

        <%-- BOUTON DE VALIDATION --%>
        <div style="text-align: right; margin-top: 20px;">
            <button type="submit" style="background-color: #27ae60; color: white; padding: 15px 40px; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; font-size: 16px; transition: background 0.3s;">
                 Enregistrer les données
            </button>
        </div>
    </form>
</div>

<script>
function gererObligationDate() {
    var selectEssais = document.getElementById("selectEssais");
    var inputDate = document.getElementById("inputDate");
    var asterisque = document.getElementById("asterisqueDate");

    if (selectEssais.value === "Oui") {
        inputDate.setAttribute("required", "required");
        asterisque.style.display = "inline"; // Affiche l'astérisque rouge
    } else {
        inputDate.removeAttribute("required");
        asterisque.style.display = "none";  // Cache l'astérisque rouge
        
        // Optionnel : vide le champ date si l'utilisateur change d'avis et met "Non"
        if (selectEssais.value === "Non") {
            inputDate.value = ""; 
        }
    }
}

// Lancement au chargement de la page pour initialiser le comportement si le formulaire contient déjà des données
document.addEventListener("DOMContentLoaded", function() {
    gererObligationDate();
});
</script>