<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="java.util.List" %>
<%@ page import="dao.DonneeDAO" %>

<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

<div class="form-container" style="padding: 20px; background: white; border-radius: 8px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); font-family: 'Segoe UI', Arial, sans-serif;">
    <h2 style="color: #27ae60; border-bottom: 2px solid #27ae60; padding-bottom: 10px; margin-bottom: 20px;">
        Étape 9 : Actions C.d.G. (Contrôle de Gestion)
    </h2>

    <%-- ================= BLOC : DERNIER AVIS DE LA LISTE (ÉTAPE REÇUE DE LA DOP) ================= --%>
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

    <%-- ================= FORMULAIRE DE CALCUL DU C.R.I. ================= --%>
    <form action="${pageContext.request.contextPath}/etapeController" method="post">     
        <%-- Context ID --%>
        <input type="hidden" name="id_workflow" value="${param.id_workflow}">
        <input type="hidden" name="current_n" value="9">

        <%-- --- BLOC 1 : CALCUL DU C.R.I. --- --%>
        <div class="bloc-donnee" style="margin-bottom: 25px; padding: 20px; border: 2px solid #27ae60; border-radius: 5px; background-color: #f1f9f5;">
            <input type="hidden" name="type_cri" value="Calcul du C.R.I."> 
            
            <label style="display: block; font-weight: bold; margin-bottom: 8px; color: #27ae60; font-size: 1.1em;">Montant du C.R.I. (Coût de Revient Industriel) * :</label>
            <div style="position: relative;">
                <input type="text" name="attr_cri" placeholder="0.00" required
                       style="width: 100%; padding: 12px; border: 1px solid #27ae60; border-radius: 4px; font-size: 1.2em; font-weight: bold; box-sizing: border-box;">
                <span style="position: absolute; right: 15px; top: 12px; color: #27ae60; font-weight: bold;">€</span>
            </div>

            <label style="display: block; font-weight: bold; margin-top: 15px; margin-bottom: 5px;">Commentaire(s) C.d.G. / Détails du coût :</label>
            <textarea name="comm_cri" placeholder="Détaillez les coûts matières, main d'œuvre, frais fixes, ou marges spécifiques..." 
                      style="width: 100%; padding: 10px; height: 120px; border: 1px solid #ccc; border-radius: 4px; box-sizing: border-box; font-family: inherit; font-size: 14px;"></textarea>
        </div>

        <%-- --- BLOC 2 : VALIDATION CALCUL --- --%>
        <div class="bloc-donnee" style="margin-bottom: 20px; padding: 15px; border: 1px solid #eee; border-radius: 5px; background-color: #f9f9f9;">
            <input type="hidden" name="type_valid_cdg" value="Validation calcul C.d.G."> 
            <input type="hidden" name="ref_valid_cdg" value="Bool"> 
            <input type="hidden" name="date_avis" value="CURRENT_DATE">
            
            <label style="display: block; font-weight: bold; margin-bottom: 8px;">Calcul finalisé et validé par le contrôleur de gestion :</label>
            <select name="attr_valid_cdg" required style="width: 100%; padding: 10px; border: 1px solid #ccc; border-radius: 4px; background: white; font-size: 14px;">
                <option value="" disabled selected>-- Sélectionner la validation --</option>
                <c:forEach var="opt" items="${optionsBool}">
                    <option value="${opt}">${opt}</option>
                </c:forEach>
            </select>
        </div>
        <%-- BOUTON DE FINALISATION ÉTAPE 9 --%>
        <div style="text-align: right; margin-top: 20px;">
            <button type="submit" 
                    style="background-color: #27ae60; color: white; padding: 15px 45px; border: none; border-radius: 5px; cursor: pointer; font-weight: bold; font-size: 16px; transition: background 0.3s; box-shadow: 0 4px 6px rgba(39, 174, 96, 0.2);">
                Clôturer l'analyse C.d.G.
            </button>
        </div>
    </form>
</div>