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
    <h2 style="color: #2c3e50; border-bottom: 2px solid #e67e22; padding-bottom: 10px;">
        Étape 5 : Faisabilité Logistique
    </h2>
        
    <form action="${pageContext.request.contextPath}/etapeController" method="post">     
        <%-- Context ID --%>
        <input type="hidden" name="id_workflow" value="${param.id_workflow}">
        <input type="hidden" name="current_n" value="5">

        <%-- --- BLOC 1 : CONFORMITÉ PALETTISATION --- --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px; background-color: #fcfcfc;">
            <input type="hidden" name="type_pal" value="Conformité palettisation"> 
            <input type="hidden" name="ref_pal" value="Bool"> 
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Conformité palettisation :</label>
            <select name="attr_pal" required style="width: 100%; padding: 10px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 4px;">
                <option value="">-- Sélectionner --</option>
                <c:forEach var="opt" items="${optionsBool}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire(s) palettisation :</label>
            <textarea name="comm_pal" placeholder="Précisez les dimensions, débord, ou hauteur palette..." 
                      style="width: 100%; padding: 10px; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
        </div>

        <%-- --- BLOC 2 : CONTRAINTE(S) TRANSPORT --- --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px; background-color: #f9f9f9;">
            <input type="hidden" name="type_trans" value="Contrainte(s) transport"> 
            <input type="hidden" name="ref_trans" value="Bool"> 
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Contrainte(s) transport :</label>
            <select name="attr_trans" required style="width: 100%; padding: 10px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 4px;">
                <option value="">-- Sélectionner --</option>
                <c:forEach var="opt" items="${optionsBool}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire(s) transport :</label>
            <textarea name="comm_trans" placeholder="Ex: fragilité, gerbage interdit, température spécifique..." 
                      style="width: 100%; padding: 10px; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
        </div>

        <%-- --- BLOC 3 : GESTION FLUX/STOCKS --- --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px;">
            <input type="hidden" name="type_flux" value="Gestion flux/stocks"> 
            <%-- Ici on peut laisser en saisie libre ou utiliser un Bool --%>
            <input type="hidden" name="ref_flux" value="Bool"> 

            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Alerte gestion flux/stocks :</label>
            <select name="attr_flux" required style="width: 100%; padding: 10px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 4px;">
                <option value="">-- Sélectionner --</option>
                <c:forEach var="opt" items="${optionsBool}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire(s) flux/stocks :</label>
            <textarea name="comm_flux" placeholder="Capacité de stockage, rotation, DLC..." 
                      style="width: 100%; padding: 10px; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
        </div>

        <%-- --- BLOC 4 : AVIS LOGISTIQUE --- --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 2px solid #3498db; border-radius: 5px; background-color: #ebf5fb;">
            <input type="hidden" name="type_avis" value="Avis logistique"> 
            <input type="hidden" name="ref_avis" value="avis"> 
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px; color: #2980b9;">Décision Finale Logistique :</label>
            <select name="attr_avis" required style="width: 100%; padding: 10px; margin-bottom: 15px; border: 1px solid #3498db; border-radius: 4px;">
                <option value="">-- Sélectionner l'avis final --</option>
                <c:forEach var="opt" items="${optionsAvis}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire final :</label>
            <textarea name="comm_avis" style="width: 100%; padding: 10px; height: 80px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
        </div>

        <%-- BOUTON DE VALIDATION --%>
        <div style="text-align: right; margin-top: 20px;">
            <button type="submit" 
                    style="background-color: #e67e22; color: white; padding: 15px 40px; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; font-size: 16px; transition: background 0.3s;">
                 Valider la faisabilité logistique
            </button>
        </div>
    </form>
</div>