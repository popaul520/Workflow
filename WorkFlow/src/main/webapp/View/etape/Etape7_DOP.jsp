<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.DonneeDAO" %>



<div class="form-container" style="padding: 20px; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); font-family: 'Segoe UI', Arial, sans-serif;">
    <h2 style="color: #c0392b; border-bottom: 2px solid #c0392b; padding-bottom: 10px;">
        Étape 7 : Décision D.O.P.
    </h2>
        
    <form action="${pageContext.request.contextPath}/etapeController" method="post">     
        <%-- Context ID --%>
        <input type="hidden" name="id_workflow" value="${param.id_workflow}">
        <input type="hidden" name="current_n" value="7">

        <%-- --- BLOC 1 : AVIS D.O.P. (La date est gérée automatiquement côté serveur) --- --%>
        <div class="bloc-donnee" style="margin-bottom: 25px; padding: 20px; border: 2px solid #c0392b; border-radius: 5px; background-color: #fdf2f2;">
            <input type="hidden" name="type_avis" value="Avis D.O.P."> 
            <input type="hidden" name="ref_avis" value="avis"> 
                        <input type="hidden" name="date_avis" value="CURRENT_DATE">
            
            <label style="display: block; font-weight: bold; margin-bottom: 8px; color: #c0392b; font-size: 1.1em;">Décision Finale D.O.P. :</label>
            <select name="attr_avis" required style="width: 100%; padding: 12px; margin-bottom: 15px; border: 1px solid #c0392b; border-radius: 4px; font-weight: bold;">
                <option value="">-- Sélectionner la décision --</option>
                <c:forEach var="opt" items="${optionsAvis}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Commentaire(s) de la direction :</label>
            <textarea name="comm_avis" placeholder="Justification de la décision, arbitrages effectués..." 
                      style="width: 100%; padding: 10px; height: 100px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
        </div>

        <%-- --- BLOC 2 : ACTIONS À LEVER (Saisie Libre) --- --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px; background-color: #f9f9f9;">
            <input type="hidden" name="type_actions" value="Action(s) à lever">
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Action(s) à lever (Attribut) :</label>
            <input type="text" name="attr_actions" placeholder="Ex: Investissement moule, validation packaging..." 
                   style="width: 100%; padding: 10px; margin-bottom: 15px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;">	
            
            <label style="display: block; font-weight: bold; margin-bottom: 5px;">Détails des actions / Échéancier (Commentaire) :</label>
            <textarea name="comm_actions" placeholder="Détaillez les pré-requis avant lancement ou les points de vigilance..." 
                      style="width: 100%; padding: 10px; height: 80px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box;"></textarea>
        
        </div>

        <%-- BOUTON DE VALIDATION --%>
        <div style="text-align: right; margin-top: 20px;">
            <button type="submit" 
                    style="background-color: #c0392b; color: white; padding: 15px 45px; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; font-size: 16px; transition: background 0.3s;">
                 Enregistrer la décision finale
            </button>
        </div>
    </form>
</div>