<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<div class="form-container" style="padding: 20px; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); font-family: 'Segoe UI', sans-serif;">
    <h2 style="color: #2c3e50; border-bottom: 2px solid #9b59b6; padding-bottom: 10px;">
        Étape 4 : Faisabilité S.C.M. (Supply Chain)
    </h2>
        
    <form action="${pageContext.request.contextPath}/etapeController" method="post">     
        <input type="hidden" name="id_workflow" value="${id_workflow}">
        <input type="hidden" name="current_n" value="4">

        <%-- 1. COHÉRENCE S.D.I. --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px;">
            <input type="hidden" name="type_sdi" value="Cohérence S.D.I.">
            <input type="hidden" name="ref_sdi" value="Bool">
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Cohérence S.D.I. (Attribut) :</label>
            <select name="attr_sdi" style="width: 100%; padding: 10px; margin-bottom: 10px; border: 1px solid #ccc; border-radius: 4px;">
                <c:forEach var="opt" items="${optionsBool}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire(s) :</label>
            <textarea name="comm_sdi" style="width: 100%; padding: 10px; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
        </div>

        <%-- 2. GESTION FLUX/STOCKS --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px; background-color: #fcfaff;">
            <input type="hidden" name="type_flux" value="Gestion flux/stocks">
            <input type="hidden" name="ref_flux" value="flux/stock">
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Gestion flux/stocks (Attribut) :</label>
            <select name="attr_flux" style="width: 100%; padding: 10px; margin-bottom: 10px; border: 1px solid #ccc; border-radius: 4px;">
                <c:forEach var="opt" items="${optionsFlux}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire(s) :</label>
            <textarea name="comm_flux" style="width: 100%; padding: 10px; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
        </div>

        <%-- 3. COMPATIBILITÉ PLANIFICATION --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px;">
            <input type="hidden" name="type_planif" value="Compatibilité planification">
            <input type="hidden" name="ref_planif" value="Bool">
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Compatibilité planification (Attribut) :</label>
            <select name="attr_planif" style="width: 100%; padding: 10px; margin-bottom: 10px; border: 1px solid #ccc; border-radius: 4px;">
                <c:forEach var="opt" items="${optionsBool}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire(s) :</label>
            <textarea name="comm_planif" style="width: 100%; padding: 10px; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
        </div>

        <%-- 4. AVIS S.C.M. --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #9b59b6; border-radius: 5px; background-color: #fdfbff;">
            <input type="hidden" name="type_avis" value="Avis S.C.M.">
            <input type="hidden" name="ref_avis" value="avis">
            
            <label style="display: block; font-weight: bold; color: #8e44ad; margin-bottom: 5px;">Avis S.C.M. (Attribut) :</label>
            <select name="attr_avis" required style="width: 100%; padding: 10px; margin-bottom: 10px; border: 1px solid #9b59b6; border-radius: 4px;">
                <option value="">-- Choisir un avis --</option>
                <c:forEach var="opt" items="${optionsAvis}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire(s) :</label>
            <textarea name="comm_avis" style="width: 100%; padding: 10px; height: 60px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
            
            <label style="display: block; font-weight: bold; margin-top: 10px; margin-bottom: 5px;">Date de validation :</label>
            <input type="date" name="date_avis" required style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">
        </div>

        <div style="text-align: right; margin-top: 20px;">
            <button type="submit" 
                    style="background-color: #9b59b6; color: white; padding: 15px 40px; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; font-size: 16px;">
                 Valider l'étape S.C.M.
            </button>
        </div>
    </form>
</div>