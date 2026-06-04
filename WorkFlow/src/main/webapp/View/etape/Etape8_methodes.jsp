<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.DonneeDAO" %>

<%
    // 1. Initialisation du DAO pour charger les référentiels
    DonneeDAO donneeDao = new DonneeDAO();
    
    // 2. Récupération des options Booléennes
    List<String> optionsBool = donneeDao.getValeursContraintes("Bool");
    
    // 3. Mise à disposition pour JSTL
    request.setAttribute("optionsBool", optionsBool);
%>

<div class="form-container" style="padding: 20px; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); font-family: 'Segoe UI', Arial, sans-serif;">
    <h2 style="color: #34495e; border-bottom: 2px solid #34495e; padding-bottom: 10px;">
        Étape 8 : Actions Méthodes
    </h2>
        
    <form action="${pageContext.request.contextPath}/etapeController" method="post">     
        <%-- Context ID --%>
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
            <textarea name="comm_codes" placeholder="Codes articles, codes EAN, codes composants..." 
                      style="width: 100%; padding: 10px; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
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
            <textarea name="comm_nomen" placeholder="Détails des composants, pertes théoriques..." 
                      style="width: 100%; padding: 10px; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
        </div>

        <%-- --- BLOC 3 : CRÉATION GAMME --- --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px; background-color: #f8f9fa;">
            <input type="hidden" name="type_gamme" value="Création gamme"> 
            <input type="hidden" name="ref_gamme" value="Bool"> 
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Création gamme  :</label>
            <select name="attr_gamme" required style="width: 100%; padding: 10px; margin-bottom: 10px; border: 1px solid #ccc; border-radius: 4px;">
                <option value="">-- Sélectionner --</option>
                <c:forEach var="opt" items="${optionsBool}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire(s) gamme :</label>
            <textarea name="comm_gamme" placeholder="Étapes de production, temps de réglage..." 
                      style="width: 100%; padding: 10px; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
        </div>

        <%-- --- BLOC 4 : DONNÉES DE RÉFÉRENCE (Cadence / M.O.D) --- --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #34495e; border-radius: 5px; background-color: #fdfdfd;">
            <input type="hidden" name="type_prod" value="Données de production">
            
            <div style="display: grid; grid-template-columns: repeat(3, 1fr); gap: 15px;">
                <div>
                    <label style="display: block; font-weight: bold; margin-bottom: 5px;">Quantité de réf. :</label>
                    <input type="number" name="attr_qte_ref" placeholder="Quantité" 
                           style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">
                </div>
                <div>
                    <label style="display: block; font-weight: bold; margin-bottom: 5px;">Cadence :</label>
                    <input type="text" name="attr_cadence" placeholder="ex: 50 u/min" 
                           style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">
                </div>
                <div>
                    <label style="display: block; font-weight: bold; margin-bottom: 5px;">M.O.D. :</label>
                    <input type="text" name="attr_mod" placeholder="Main d'oeuvre" 
                           style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">
                </div>
            </div>
            <div style="margin-top: 15px;">
                <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire(s) production :</label>
                <textarea name="comm_prod" placeholder="Précisions sur les cadences ou besoins MOD spécifiques..." 
                          style="width: 100%; padding: 10px; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
            </div>
        </div>

        <%-- BOUTON DE FINALISATION --%>
        <div style="text-align: right; margin-top: 20px;">
            <button type="submit" 
                    style="background-color: #34495e; color: white; padding: 15px 40px; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; font-size: 16px; transition: background 0.3s;">
                 Finaliser les Actions Méthodes
            </button>
        </div>
    </form>
</div>