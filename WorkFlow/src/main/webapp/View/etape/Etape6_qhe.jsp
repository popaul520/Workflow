<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.DonneeDAO" %>

<%
    // 1. Initialisation du DAO pour charger les référentiels
    DonneeDAO donneeDao = new DonneeDAO();
    
    // 2. Récupération des options pour les listes déroulantes
    List<String> optionsBool = donneeDao.getValeursContraintes("Bool"); 
    List<String> optionsAvis = donneeDao.getValeursContraintes("avis");
    
    // 3. Mise à disposition pour JSTL
    request.setAttribute("optionsBool", optionsBool);
    request.setAttribute("optionsAvis", optionsAvis);
%>

<div class="form-container" style="padding: 20px; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); font-family: 'Segoe UI', Arial, sans-serif;">
    <h2 style="color: #6f42c1; border-bottom: 2px solid #6f42c1; padding-bottom: 10px;">
        Étape 6 : Faisabilité Q.H.E.
    </h2>
        
    <form action="${pageContext.request.contextPath}/etapeController" method="post">     
        <%-- Context ID --%>
        <input type="hidden" name="id_workflow" value="${param.id_workflow}">
        <input type="hidden" name="current_n" value="6">

        <%-- --- BLOC 1 : MARQUAGE TRAÇABILITÉ --- --%>
		<div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px; background-color: #f8f9fa;">
		    <input type="hidden" name="type_tracab" value="Marquage traçabilité"> 
		    <input type="hidden" name="ref_tracab" value="normalite"> 
		    <label style="display: block; font-weight: bold; margin-bottom: 5px;">Spécificité marquage *</label>
		    <select name="attr_tracab" required
		            style="width: 100%; padding: 10px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; background-color: white; font-family: inherit; font-size: 14px;">
		        <option value="" disabled selected>-- Choisissez une spécificité --</option>
		        <c:forEach var="opt" items="${optionsnormalite}">
		            <option value="${opt}">${opt}</option>
		        </c:forEach>
		    </select>	
		    <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire(s) marquage :</label>
		    <textarea name="comm_tracab" placeholder="Précisez le type de jet d'encre, étiquetage, positionnement du lot..." 
		              style="width: 100%; padding: 10px; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; font-family: inherit; resize: vertical;"></textarea>
		</div>
        <%-- --- BLOC 2 : D.D.M. AU CONDI. --- --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px;">
            <input type="hidden" name="type_ddm" value="D.D.M. au condi."> 
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">D.D.M. au conditionnement * :</label>
            <input type="text" name="attr_ddm" required placeholder="Ex: 45 jours" 
                   style="width: 100%; padding: 10px; margin-bottom: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"> 
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire(s) DDM :</label>
            <textarea name="comm_ddm" placeholder="Règle de calcul de la DDM, gestion du report..." 
                      style="width: 100%; padding: 10px; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
        </div>
        <%-- --- BLOC 3 : MÉTROLOGIE --- --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px; background-color: #f8f9fa;">
            <input type="hidden" name="type_metro" value="Métrologie"> 
            <input type="hidden" name="ref_metro" value="normalite"> 
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Validation Métrologie (Poids/Contrôles) :</label>
            <select name="attr_metro" required style="width: 100%; padding: 10px; margin-bottom: 10px; border: 1px solid #ccc; border-radius: 4px;">
                <option value="">-- Sélectionner --</option>
                <c:forEach var="opt" items="${optionsnormalite}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire(s) métrologie :</label>
            <textarea name="comm_metro" placeholder="Tolérances, TU1, TU2, réglage trieuse pondérale..." 
                      style="width: 100%; padding: 10px; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
        </div>

        <%-- --- BLOC 4 : ALLERGÈNE(S) --- --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px;">
            <input type="hidden" name="type_aller" value="Allèrgène(s)"> 
            <input type="hidden" name="ref_aller" value="Bool"> 
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Maîtrise des allergènes :</label>
            <select name="attr_aller" required style="width: 100%; padding: 10px; margin-bottom: 10px; border: 1px solid #ccc; border-radius: 4px;">
                <option value="">-- Sélectionner --</option>
                <c:forEach var="opt" items="${optionsBool}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire(s) allergènes :</label>
            <textarea name="comm_aller" placeholder="Risque de contaminations croisées, nettoyage spécifique requis..." 
                      style="width: 100%; padding: 10px; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
        </div>

        <%-- --- BLOC 5 : SPÉCIFICATION(S) CLIENT --- --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px; background-color: #f8f9fa;">
            <input type="hidden" name="type_spec" value="Spécification(s) client"> 
            <input type="hidden" name="ref_spec" value="Bool"> 
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Conformité CDC / Spécifications client :</label>
            <select name="attr_spec" required style="width: 100%; padding: 10px; margin-bottom: 10px; border: 1px solid #ccc; border-radius: 4px;">
                <option value="">-- Sélectionner --</option>
                <c:forEach var="opt" items="${optionsBool}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire(s) spécifications :</label>
            <textarea name="comm_spec" placeholder="Analyses libératoires, certificats, exigences audit..." 
                      style="width: 100%; padding: 10px; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
        </div>

        <%-- --- BLOC 6 : AVIS Q.H.E. --- --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 2px solid #6f42c1; border-radius: 5px; background-color: #f3f0fd;">
            <input type="hidden" name="type_avis" value="Avis Q.H.E."> 
            <input type="hidden" name="ref_avis" value="avis"> 
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px; color: #6f42c1;">Décision Finale Q.H.E. :</label>
            <select name="attr_avis" required style="width: 100%; padding: 10px; margin-bottom: 10px; border: 1px solid #6f42c1; border-radius: 4px;">
                <option value="">-- Sélectionner l'avis final --</option>
                <c:forEach var="opt" items="${optionsAvis}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire final Qualité :</label>
            <textarea name="comm_avis" style="width: 100%; padding: 10px; height: 80px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
        	<input type="hidden" name="date_avis" value="CURRENT_DATE">
        	
        </div>

        <%-- BOUTON DE VALIDATION --%>
        <div style="text-align: right; margin-top: 20px;">
            <button type="submit" 
                    style="background-color: #6f42c1; color: white; padding: 15px 40px; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; font-size: 16px; transition: background 0.3s;">
                 Valider la faisabilité Qualité
            </button>
            
        </div>
    </form>
</div>