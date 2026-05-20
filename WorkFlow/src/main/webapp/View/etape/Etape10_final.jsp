<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.DonneeDAO" %>

<%
    // 1. Initialisation du DAO pour charger les référentiels
    DonneeDAO donneeDao = new DonneeDAO();
    
    // 2. Récupération des options pour les listes déroulantes (Avis Final)
    List<String> optionsAvis = donneeDao.getValeursContraintes("avis");
    
    // 3. Mise à disposition pour JSTL
    request.setAttribute("optionsAvis", optionsAvis);
%>

<div class="form-container" style="padding: 20px; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); font-family: 'Segoe UI', Arial, sans-serif;">
    <h2 style="color: #2980b9; border-bottom: 2px solid #2980b9; padding-bottom: 10px;">
        Étape 10 : Décision D.C.D.
    </h2>
        
    <form action="${pageContext.request.contextPath}/etapeController" method="post">     
        <%-- Context ID --%>
        <input type="hidden" name="id_workflow" value="${param.id_workflow}">
        <input type="hidden" name="current_n" value="10">

        <%-- --- BLOC UNIQUE : AVIS D.C.D. (La date est gérée automatiquement côté serveur) --- --%>
        <div class="bloc-donnee" style="margin-bottom: 25px; padding: 25px; border: 2px solid #2980b9; border-radius: 5px; background-color: #f0f7fc;">
            <input type="hidden" name="type_avis" value="Avis D.C.D."> 
            <input type="hidden" name="ref_avis" value="avis"> 
            
            <label style="display: block; font-weight: bold; margin-bottom: 10px; color: #2980b9; font-size: 1.2em;">Décision Finale D.C.D. :</label>
            <select name="attr_avis" required style="width: 100%; padding: 12px; margin-bottom: 20px; border: 1px solid #2980b9; border-radius: 4px; font-weight: bold; font-size: 1.1em;">
                <option value="">-- Sélectionner l'avis final du Développement --</option>
                <c:forEach var="opt" items="${optionsAvis}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            
            <label style="display: block; font-weight: bold; margin-bottom: 8px;">Commentaire(s) de clôture :</label>
            <textarea name="comm_avis" placeholder="Synthèse finale, conditions de mise en marché, ou raisons du refus..." 
                      style="width: 100%; padding: 12px; height: 150px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; font-size: 14px;"></textarea>
        </div>

        <%-- BOUTON DE VALIDATION FINALE --%>
        <div style="text-align: right; margin-top: 20px;">
            <button type="submit" 
                    style="background-color: #2980b9; color: white; padding: 18px 50px; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; font-size: 16px; transition: background 0.3s; box-shadow: 0 4px 6px rgba(41, 128, 185, 0.3);">
                 VALIDER ET CLÔTURER LE WORKFLOW
            </button>
        </div>
    </form>
</div>