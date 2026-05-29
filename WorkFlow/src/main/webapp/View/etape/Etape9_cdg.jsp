<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.DonneeDAO" %>


<div class="form-container" style="padding: 20px; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); font-family: 'Segoe UI', Arial, sans-serif;">
    <h2 style="color: #27ae60; border-bottom: 2px solid #27ae60; padding-bottom: 10px;">
        Étape 9 : Actions C.d.G.
    </h2>
        
    <form action="${pageContext.request.contextPath}/etapeController" method="post">     
        <%-- Context ID --%>
        <input type="hidden" name="id_workflow" value="${param.id_workflow}">
        <input type="hidden" name="current_n" value="9">

        <%-- --- BLOC 1 : CALCUL DU C.R.I. --- --%>
        <div class="bloc-donnee" style="margin-bottom: 25px; padding: 20px; border: 2px solid #27ae60; border-radius: 5px; background-color: #f1f9f5;">
            <input type="hidden" name="type_cri" value="Calcul du C.R.I."> 
            
            <label style="display: block; font-weight: bold; margin-bottom: 8px; color: #27ae60; font-size: 1.1em;">Montant du C.R.I. (Attribut) :</label>
            <div style="position: relative;">
                <input type="text" name="attr_cri" placeholder="0.00" required
                       style="width: 100%; padding: 12px; border: 1px solid #27ae60; border-radius: 4px; font-size: 1.2em; font-weight: bold; box-sizing: border-box;">
                <span style="position: absolute; right: 15px; top: 12px; color: #27ae60; font-weight: bold;">€</span>
            </div>

            <label style="display: block; font-weight: bold; margin-top: 15px; margin-bottom: 5px;">Commentaire(s) C.d.G. / Détails du coût :</label>
            <textarea name="comm_cri" placeholder="Détaillez les coûts matières, main d'œuvre, frais fixes, ou marges spécifiques..." 
                      style="width: 100%; padding: 10px; height: 120px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
        </div>

        <%-- --- BLOC 2 : VALIDATION CALCUL (Optionnel) --- --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px; background-color: #f9f9f9;">
            <input type="hidden" name="type_valid_cdg" value="Validation calcul C.d.G."> 
            <input type="hidden" name="ref_valid_cdg" value="Bool"> 
            <input type="hidden" name="date_avis" value="CURRENT_DATE">
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Calcul finalisé et validé du CRI :</label>
            <select name="attr_valid_cdg" required style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px;">
                <option value="">-- Sélectionner --</option>
                <c:forEach var="opt" items="${optionsBool}">
                    <option value="${opt}">${opt}</option>
                    
                </c:forEach>
                
            </select>
        </div>

        <%-- BOUTON DE FINALISATION --%>
        <div style="text-align: right; margin-top: 20px;">
            <button type="submit" 
                    style="background-color: #27ae60; color: white; padding: 15px 45px; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; font-size: 16px; transition: background 0.3s; box-shadow: 0 4px 6px rgba(39, 174, 96, 0.2);">
                 Clôturer l'analyse C.d.G.
            </button>
        </div>
    </form>
</div>