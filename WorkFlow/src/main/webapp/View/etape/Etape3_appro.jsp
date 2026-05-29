<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.DonneeDAO" %>
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
            
            <label style="display: block; font-weight: bold;">Création intrant(s) (Attribut) :</label>
            <select name="attr_intrants" style="width: 100%; padding: 8px; margin-bottom: 10px;">
                <c:forEach var="opt" items="${optionsBool}">
                	<option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            
            <label style="display: block; font-weight: bold;">Commentaire(s) :</label>
            <textarea name="comm_intrants" style="width: 100%; height: 50px;"></textarea>
        </div>

        <%-- 2. FOURNISSEURS RÉFÉRENCÉS --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px;">
            <input type="hidden" name="type_fournisseurs" value="Fournisseur(s) référencé(s)">
            <input type="hidden" name="ref_fournisseurs" value="Bool">
            
            <label style="display: block; font-weight: bold;">Fournisseur(s) référencé(s) :</label>
            <select name="attr_fournisseurs" style="width: 100%; padding: 8px; margin-bottom: 10px;">
                <c:forEach var="opt" items="${optionsBool}"><option value="${opt}">${opt}</option></c:forEach>
            </select>
            
            <label style="display: block; font-weight: bold;">Commentaire(s) :</label>
            <textarea name="comm_fournisseurs" style="width: 100%; height: 50px;"></textarea>
        </div>

        <%-- 3. DÉLAI / RISQUES / CONTRAINTES --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px; background-color: #f9f9f9;">
            <input type="hidden" name="type_delai" value="Délai compatible">
            <input type="hidden" name="ref_delai" value="avis">
            
            <label style="display: block; font-weight: bold;">Délai compatible (Attribut) :</label>
            <select name="attr_delai" style="width: 100%; padding: 8px; margin-bottom: 10px;">
                <c:forEach var="opt" items="${optionsBool}"><option value="${opt}">${opt}</option></c:forEach>
            </select>
            
            <label style="display: block; font-weight: bold;">Risque(s) appro. / Contrainte(s) fournisseur (Commentaire) :</label>
            <textarea name="comm_delai" style="width: 100%; height: 70px;" placeholder="Détaillez les risques ou contraintes ici..."></textarea>
        </div>

        <%-- 4. AVIS APPRO --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #e67e22; border-radius: 5px;">
            <input type="hidden" name="type_avis" value="Avis Appro.">
            <input type="hidden" name="ref_avis" value="avis">
            
            <label style="display: block; font-weight: bold; color: #d35400;">Avis Appro. (Attribut) :</label>
            <select name="attr_avis" required style="width: 100%; padding: 10px; margin-bottom: 10px; border: 1px solid #e67e22;">
                <c:forEach var="opt" items="${optionsAvis}"><option value="${opt}">${opt}</option></c:forEach>
            </select>

            <label style="display: block; font-weight: bold;">Commentaire(s) :</label>
            <textarea name="comm_avis" style="width: 100%; height: 60px;"></textarea>
            
            <label style="display: block; font-weight: bold; margin-top: 10px;">Date de validation :</label>
            <input type="hidden" name="date_avis" value="CURRENT_DATE">
        </div>

        <div style="text-align: right;">
            <button type="submit" style="background-color: #e67e22; color: white; padding: 15px 40px; border: none; border-radius: 5px; cursor: pointer; font-weight: bold;">
                Enregistrer Faisabilité Appro.
            </button>
        </div>
    </form>
</div>