<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.DonneeDAO" %>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

<div class="form-container" style="padding: 20px; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); font-family: 'Segoe UI', Arial, sans-serif;">
    <h2 style="color: #2980b9; border-bottom: 2px solid #2980b9; padding-bottom: 10px; margin-bottom: 20px;">
        Étape 10 : Décision Finale D.C.M. / D.C.D.
    </h2>

    <%-- ================= BLOC SYNTHÈSE ÉCONOMIQUE : C.R.I. RÉCUPÉRÉ DE L'ÉTAPE 9 ================= --%>
    <div class="cri-container" style="margin-bottom: 25px; padding: 20px; border: 1px solid #27ae60; border-radius: 6px; background-color: #f1f9f5;">
        <h3 style="color: #27ae60; margin-top: 0; border-bottom: 1px dashed #27ae60; padding-bottom: 8px;">
            💰 Synthèse Économique : Analyse du C.d.G (Étape 9)
        </h3>
        
        <div style="margin-top: 15px; font-size: 14px;">
            <p style="margin: 5px 0; font-size: 1.2em;">
                <strong>Montant du C.R.I. validé :</strong> 
                <span style="font-family: monospace; font-weight: bold; color: #27ae60; background: #e8f8f5; padding: 4px 10px; border-radius: 4px;">
                    ${donneeCri.attribut != null ? donneeCri.attribut : "0.00"} €
                </span>
            </p>
            <p style="margin: 15px 0 5px 0; line-height: 1.4; color: #555;">
                <strong>Détails et commentaires du C.d.G. :</strong><br>
                <span style="font-style: italic; color: #666;">
                    "${donneeCri.commentaire != null && !donneeCri.commentaire.trim().isEmpty() ? donneeCri.commentaire : 'Aucun détail de coût renseigné par le C.d.G.'}"
                </span>
            </p>
        </div>
    </div>

    <%-- ================= BLOC : DERNIER AVIS DE LA LISTE ================= --%>
    <div class="dernier-avis-container" style="margin-bottom: 25px; padding: 15px; border: 1px solid #fab1a0; border-radius: 6px; background-color: #fff9f8;">
        <h3 style="color: #d35400; margin-top: 0; font-size: 1em; text-transform: uppercase; letter-spacing: 0.5px;">
            📢 Dernier Avis Décisionnel Enregistré (DOP / Précédent)
        </h3>
        <div style="margin-top: 10px; font-size: 14px;">
            <c:choose>
                <c:when test="${not empty dernierAvis}">
                    <p style="margin: 5px 0;">
                        <strong>Décision (Étape ${dernierAvis.nbEtape}) :</strong> 
                        <span style="padding: 3px 8px; border-radius: 4px; font-weight: bold; 
                                     background-color: ${dernierAvis.attribut == 'Favorable' ? '#d4edda' : '#ffeaa7'}; 
                                     color: ${dernierAvis.attribut == 'Favorable' ? '#155724' : '#d35400'};">
                            ${dernierAvis.attribut}
                        </span>
                    </p>
                    <p style="margin: 10px 0 5px 0; line-height: 1.4; color: #555;">
                        <strong>Commentaire associé :</strong><br>
                        <span style="font-style: italic;">
                            "${dernierAvis.commentaire != null && !dernierAvis.commentaire.trim().isEmpty() ? dernierAvis.commentaire : 'Aucun commentaire renseigné.'}"
                        </span>
                    </p>
                </c:when>
                <c:otherwise>
                    <p style="margin: 5px 0; font-style: italic; color: #7f8c8d;">
                        Aucun avis préalable trouvé dans la base de données pour ce workflow.
                    </p>
                </c:otherwise>
            </c:choose>
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