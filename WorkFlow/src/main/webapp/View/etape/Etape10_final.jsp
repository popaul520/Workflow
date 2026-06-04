<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.DonneeDAO" %>

<div class="form-container" style="padding: 20px; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); font-family: 'Segoe UI', Arial, sans-serif;">
    <h2 style="color: #2980b9; border-bottom: 2px solid #2980b9; padding-bottom: 10px; margin-bottom: 20px;">
        Étape 10 : Décision Finale D.C.M. / D.C.D.
    </h2>

    <%-- ================= BLOC SYNTHÈSE ÉCONOMIQUE : CALCUL DU C.R.I ================= --%>
    <div class="cri-container" style="margin-bottom: 25px; padding: 20px; border: 1px solid #bdc3c7; border-radius: 6px; background-color: #fdfefe;">
        <h3 style="color: #2c3e50; margin-top: 0; border-bottom: 1px dashed #bdc3c7; padding-bottom: 8px;">
            Synthèse Économique : Calcul du C.R.I (Coût de Revient Industriel)
        </h3>
        
        <table style="width: 100%; border-collapse: collapse; margin-top: 10px; font-size: 14px;">
            <tr style="background-color: #f8f9fa;">
                <td style="padding: 10px; border-bottom: 1px solid #eceeef; font-weight: 600;">Coût Matières Premières & Emballages (Standard Matrice)</td>
                <td style="padding: 10px; border-bottom: 1px solid #eceeef; text-align: right; font-family: monospace;">${calculCri.coutStandard != null ? calculCri.coutStandard : "0.00"} € / kg</td>
            </tr>
            <tr>
                <td style="padding: 10px; border-bottom: 1px solid #eceeef; font-weight: 600;">+ Écarts de fabrication & Pertes process spécifiques</td>
                <td style="padding: 10px; border-bottom: 1px solid #eceeef; text-align: right; color: #e74c3c; font-family: monospace;">+ ${calculCri.ecartFabrication != null ? calculCri.ecartFabrication : "0.00"} € / kg</td>
            </tr>
            <tr style="background-color: #f8f9fa;">
                <td style="padding: 10px; border-bottom: 1px solid #eceeef; font-weight: 600;">+ Suppléments Main d'Œuvre / Spécificités Conditionnement</td>
                <td style="padding: 10px; border-bottom: 1px solid #eceeef; text-align: right; color: #e74c3c; font-family: monospace;">+ ${calculCri.supplementMo != null ? calculCri.supplementMo : "0.00"} € / kg</td>
            </tr>
            <tr>
                <td style="padding: 10px; border-bottom: 2px solid #bdc3c7; font-weight: 600;">+ Coûts Logistiques & Stockage Dédié</td>
                <td style="padding: 10px; border-bottom: 2px solid #bdc3c7; text-align: right; color: #e74c3c; font-family: monospace;">+ ${calculCri.coutLogistique != null ? calculCri.coutLogistique : "0.00"} € / kg</td>
            </tr>
            <tr style="background-color: #eaedd0;">
                <td style="padding: 12px; font-weight: bold; font-size: 1.1em; color: #27ae60;">C.R.I. TOTAL CALCULÉ</td>
                <td style="padding: 12px; text-align: right; font-weight: bold; font-size: 1.1em; color: #27ae60; font-family: monospace;">
                    ${calculCri.criTotal != null ? calculCri.criTotal : "0.00"} € / kg
                </td>
            </tr>
        </table>
    </div>

    <%-- ================= BLOC SYNTHÈSE OPÉRATIONNELLE : DERNIER AVIS DOP ================= --%>
    <div class="dop-container" style="margin-bottom: 25px; padding: 15px; border: 1px solid #fab1a0; border-radius: 6px; background-color: #fff9f8;">
        <h3 style="color: #d35400; margin-top: 0; font-size: 1em; text-transform: uppercase; letter-spacing: 0.5px;">
            Dernier Avis Récapitulatif : Direction des Opérations (DOP)
        </h3>
        <div style="margin-top: 10px; font-size: 14px;">
            <p style="margin: 5px 0;">
                <strong>Statut de l'Avis DOP :</strong> 
                <span style="padding: 3px 8px; border-radius: 4px; font-weight: bold; background-color: #ffeaa7; color: #d35400;">
                    ${dernierAvisDop.statut != null ? dernierAvisDop.statut : "En attente / Non communiqué"}
                </span>
            </p>
            <p style="margin: 10px 0 5px 0; line-height: 1.4; color: #555;">
                <strong>Commentaire de synthèse DOP :</strong><br>
                <span style="font-style: italic;">
                    "${dernierAvisDop.commentaire != null ? dernierAvisDop.commentaire : 'Aucun commentaire de synthèse associé.'}"
                </span>
            </p>
        </div>
    </div>
        
    <%-- ================= FORMULAIRE DE DÉCISION FINALE ================= --%>
    <form action="${pageContext.request.contextPath}/etapeController" method="post">     
        <%-- Context ID --%>
        <input type="hidden" name="id_workflow" value="${param.id_workflow}">
        <input type="hidden" name="current_n" value="10">

        <div class="bloc-donnee" style="margin-bottom: 25px; padding: 25px; border: 2px solid #2980b9; border-radius: 5px; background-color: #f0f7fc;">
            <input type="hidden" name="type_avis" value="Avis D.C.D."> 
            <input type="hidden" name="ref_avis" value="avis"> 
            
            <label style="display: block; font-weight: bold; margin-bottom: 10px; color: #2980b9; font-size: 1.2em;">Décision Finale Réseau / Direction * :</label>
            <select name="attr_avis" required style="width: 100%; padding: 12px; margin-bottom: 20px; border: 1px solid #2980b9; border-radius: 4px; font-weight: bold; font-size: 1.1em; background: white;">
                <option value="" disabled selected>-- Sélectionner l'avis final pour le Go / No-Go --</option>
                <c:forEach var="opt" items="${optionsAvis}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
            
            <label style="display: block; font-weight: bold; margin-bottom: 8px;">Commentaire(s) de clôture & Directives :</label>
            <textarea name="comm_avis" required placeholder="Saisissez ici la validation définitive, les conditions de mise en marché, ou les motifs du refus..." 
                      style="width: 100%; padding: 12px; height: 120px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; font-size: 14px; font-family: inherit;"></textarea>
        	<input type="hidden" name="date_avis" value="CURRENT_DATE">
        </div>

        <%-- BOUTON DE VALIDATION FINALE --%>
        <div style="text-align: right; margin-top: 20px;">
            <button type="submit" 
                    style="background-color: #2980b9; color: white; padding: 18px 50px; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; font-size: 16px; transition: background 0.3s; box-shadow: 0 4px 6px rgba(41, 128, 185, 0.3);">
                 🚀 VALIDER ET CLÔTURER LE WORKFLOW DEFINITIVEMENT
            </button>
        </div>
    </form>
</div>