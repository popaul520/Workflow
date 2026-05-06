<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.DonneeDAO" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

<%
    // 1. Initialisation du DAO pour charger les référentiels
    DonneeDAO donneeDao = new DonneeDAO();
    
    // 2. Récupération des options basées sur la colonne 'type' de ta table type_contraint
    List<String> optionsBool = donneeDao.getValeursContraintes("Bool"); 
    List<String> optionsAvis = donneeDao.getValeursContraintes("avis");
    
    // 3. Mise à disposition pour JSTL
    request.setAttribute("optionsBool", optionsBool);
    request.setAttribute("optionsAvis", optionsAvis);
%> aide moi a faire la recupération dans le dopost etapecontrollerglobal pour faire la sauvegarde en base de donnée car cela n'est pas enregistrer en donnée

<div class="form-container" style="padding: 20px; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); font-family: 'Segoe UI', Arial, sans-serif;">
    <h2 style="color: #2c3e50; border-bottom: 2px solid #3498db; padding-bottom: 10px;">
        Étape 2 : Faisabilité Conditionnement
    </h2>
        
    <form action="${pageContext.request.contextPath}/etapeController" method="post">     
        <%-- Context ID --%>
        <input type="hidden" name="id_workflow" value="${param.id_workflow}">
        <input type="hidden" name="current_n" value="2">

        <%-- --- BLOC 1 : CONFIGURATION MACHINE (SAISIE LIBRE) --- --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px; background-color: #f9f9f9;">
            <%-- Ici, type = Configuration Machine, et pas de ref_ car texte libre --%>
            <input type="hidden" name="type_machine" value="Configuration Machine">
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Routage machine (Attribut) :</label>
            <input type="text" name="attr_machine" placeholder="Ex: Ligne 4, Cellule B..." 
                   style="width: 100%; padding: 10px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">	
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire technique :</label>
            <textarea name="comm_machine" style="width: 100%; padding: 10px; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
        </div>

        <%-- --- BLOC 2 : BESOIN D'ESSAIS (DISSOCIÉ : Nom=Besoin d'essais, Ref=Bool) --- --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px;">
            <%-- DISSOCIATION ICI --%>
            <input type="hidden" name="type_essais" value="Besoin d'essais"> 
            <input type="hidden" name="ref_essais" value="Bool"> 
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Besoin d'essais ? (Attribut) :</label>
            <select name="attr_essais" required style="width: 100%; padding: 10px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 4px;">
                <option value="">-- Sélectionner (Oui/Non) --</option>
                <c:forEach var="opt" items="${optionsBool}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Détails (Commentaire) :</label>
            <textarea name="comm_essais" style="width: 100%; padding: 10px; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
            
            <label style="display: block; font-weight: bold; margin-top: 10px; margin-bottom: 5px;">Date prévisionnelle :</label>
            <input type="date" name="date_essais" required style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">
        </div>

        <%-- --- BLOC 3 : AVIS PRODUCTION (DISSOCIÉ : Nom=avis production, Ref=avis) --- --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px;">
            <%-- DISSOCIATION ICI --%>
            <input type="hidden" name="type_avis" value="avis production"> 
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
            <input type="date" name="date_avis" required style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">
        </div>

        <%-- BOUTON DE VALIDATION --%>
        <div style="text-align: right; margin-top: 20px;">
            <button type="submit" 
                    style="background-color: #27ae60; color: white; padding: 15px 40px; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; font-size: 16px; transition: background 0.3s;">
                 Enregistrer les données
            </button>
        </div>
    </form>
</div>