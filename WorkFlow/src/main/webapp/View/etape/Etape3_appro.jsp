<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.DonneeDAO" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

<div class="form-container" style="padding: 20px; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); font-family: 'Segoe UI', sans-serif;">
    <h2 style="color: #2c3e50; border-bottom: 2px solid #e67e22; padding-bottom: 10px;">
        Étape 3 : Faisabilité Approvisionnement
    </h2>
        
    <form action="${pageContext.request.contextPath}/etapeController" method="post">     
        <input type="hidden" name="id_workflow" value="${id_workflow}">
        <input type="hidden" name="current_n" value="3">

        <%-- 1. CRÉATION INTRANTS --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px;">
            <input type="hidden" name="type_intrants" value="Création intrant(s)">
            <input type="hidden" name="ref_intrants" value="Bool">
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Création intrant(s) (Attribut) * :</label>
            <select name="attr_intrants" required style="width: 100%; padding: 8px; margin-bottom: 10px; border: 1px solid #ccc; border-radius: 4px;">
                <option value="" disabled selected>-- Sélectionner (Oui/Non) --</option>
                <c:forEach var="opt" items="${optionsBool}">
                	<option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire(s) :</label>
            <textarea name="comm_intrants" style="width: 100%; height: 50px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
        </div>

        <%-- 2. FOURNISSEURS RÉFÉRENCÉS --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px;">
            <input type="hidden" name="type_fournisseurs" value="Fournisseur(s) référencé(s)">
            <input type="hidden" name="ref_fournisseurs" value="Bool">
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Fournisseur(s) référencé(s) * :</label>
            <select name="attr_fournisseurs" required style="width: 100%; padding: 8px; margin-bottom: 10px; border: 1px solid #ccc; border-radius: 4px;">
                <option value="" disabled selected>-- Sélectionner (Oui/Non) --</option>
                <c:forEach var="opt" items="${optionsBool}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire(s) :</label>
            <textarea name="comm_fournisseurs" style="width: 100%; height: 50px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
        </div>

        <%-- 3. DÉLAI COMPATIBLE --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px; background-color: #f9f9f9;">
            <input type="hidden" name="type_delai" value="Délai compatible">
            <input type="hidden" name="ref_delai" value="Bool">
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Délai compatible (Attribut) * :</label>
            <select name="attr_delai" required style="width: 100%; padding: 8px; margin-bottom: 10px; border: 1px solid #ccc; border-radius: 4px;">
                <option value="" disabled selected>-- Sélectionner (Oui/Non) --</option>
                <c:forEach var="opt" items="${optionsBool}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Contrainte(s) fournisseur (Commentaire) :</label>
            <textarea name="comm_delai" style="width: 100%; height: 70px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;" placeholder="Détaillez les contraintes de temps ou de fournisseur ici..."></textarea>
        </div>

        <%-- 4. AJOUT : RISQUE APPROVISIONNEMENT --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px; background-color: #f9f9f9;">
            <input type="hidden" name="type_risques" value="Risque Approvisionnement">
            <input type="hidden" name="ref_risques" value="Bool">
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Présence de risques d'approvisionnement * :</label>
            <select name="attr_risques" required style="width: 100%; padding: 8px; margin-bottom: 10px; border: 1px solid #ccc; border-radius: 4px;">
                <option value="" disabled selected>-- Sélectionner (Oui/Non) --</option>
                <c:forEach var="opt" items="${optionsBool}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Détails des risques identifiés (Commentaire) :</label>
            <textarea name="comm_risques" style="width: 100%; height: 70px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;" placeholder="Ruptures potentielles, matières volatiles, mono-source..."></textarea>
        </div>

        <%-- 5. AVIS APPRO --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #e67e22; border-radius: 5px;">
            <input type="hidden" name="type_avis" value="Avis Appro.">
            <input type="hidden" name="ref_avis" value="avis">
            
            <label style="display: block; font-weight: bold; color: #d35400; margin-bottom: 5px;">Avis Appro. (Attribut) * :</label>
            <select name="attr_avis" required style="width: 100%; padding: 10px; margin-bottom: 10px; border: 1px solid #e67e22; border-radius: 4px;">
                <option value="" disabled selected>-- Sélectionner Un AVIS --</option>
                <c:forEach var="opt" items="${optionsAvis}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>

            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire(s) de l'avis :</label>
            <textarea name="comm_avis" style="width: 100%; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
            
            <input type="hidden" name="date_avis" value="CURRENT_DATE">
        </div>

        <div style="text-align: right;">
            <button type="submit" style="background-color: #e67e22; color: white; padding: 15px 40px; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; font-size: 16px; transition: background 0.3s;">
                Enregistrer Faisabilité Appro.
            </button>
        </div>
    </form>
</div>