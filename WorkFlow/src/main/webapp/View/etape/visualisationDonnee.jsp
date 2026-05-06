<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<%@ page import="model.Utilisateur, model.Workflow, java.util.List" %>

<%
    Workflow wf = (Workflow) request.getAttribute("wf");
    Utilisateur user = (Utilisateur) session.getAttribute("user");
    int nEtape = (Integer) request.getAttribute("numEtape");
    int derniereEtape = (request.getAttribute("derniereEtape") != null) ? (Integer) request.getAttribute("derniereEtape") : 0;

    boolean isClosed = (wf != null && wf.getDateFinalisation() != null);
    boolean isAdmin = (user != null && "PATRON".equalsIgnoreCase(user.getRole()));
    boolean userHasRole = (user != null && user.canAccessStep(nEtape));

    boolean canEdit = false;
    if (isAdmin) {
        canEdit = true;
    } else if (!isClosed) {
        canEdit = userHasRole;
    } else {
        if (nEtape == 7 && derniereEtape == 7 && userHasRole) {
            canEdit = true; 
        } else if (nEtape == 10 && userHasRole) {
            canEdit = true; 
        }
    }

    List<?> listeDonnees = (List<?>) request.getAttribute("donneesEtape");
    boolean hasData = (listeDonnees != null && !listeDonnees.isEmpty());
%>

<div class="visu-container" style="padding: 20px; font-family: 'Segoe UI', sans-serif;">

    <% if (isClosed) { %>
        <div class="status-banner" style="background-color: <%= isAdmin ? "#ebf8ff" : "#fff5f5" %>; border: 1px solid <%= isAdmin ? "#90cdf4" : "#feb2b2" %>; padding: 15px; border-radius: 8px; margin-bottom: 20px; display: flex; align-items: center;">
            <span style="font-size: 24px; margin-right: 15px;"><%= isAdmin ? "🔓" : "🔒" %></span>
            <div>
                <strong style="color: <%= isAdmin ? "#2c5282" : "#c53030" %>;">
                    <%= isAdmin ? "Mode Maintenance Administrateur" : "Dossier Clôturé" %>
                </strong><br>
                <small style="color: #4a5568;">Finalisé le : ${wf.dateFinalisation}</small>
            </div>
        </div>
    <% } %>

    <% if (!hasData && !canEdit) { %>
        <div class="info-box" style="text-align: center; padding: 40px; color: #718096;">
            <p>Cette étape n'a pas encore été renseignée</p>
        </div>
    <% } else { %>

        <form action="${pageContext.request.contextPath}/etapeController" method="post">
            <input type="hidden" name="id_workflow" value="${id_workflow}">
            <input type="hidden" name="current_n" value="<%= nEtape %>">

            <div class="step-header" style="display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; border-bottom: 2px solid #edf2f7; padding-bottom: 10px;">
                <h3 style="margin: 0;">Étape <%= nEtape %> : <%= model.Utilisateur.getRole(nEtape) %></h3>
                <div>
                    <% if (canEdit) { %>
                        <button type="button" id="btn-modifier" onclick="activerEdition()" class="btn" style="background: #3182ce; color: white; padding: 8px 20px; border: none; border-radius: 4px; cursor: pointer;">Modifier</button>
                        <button type="submit" id="btn-enregistrer" style="display: none; background: #38a169; color: white; padding: 8px 20px; border: none; border-radius: 4px; cursor: pointer;">Enregistrer</button>
                    <% } %>
                </div>
            </div>

            <fieldset id="fs-edition" <%= canEdit ? "" : "disabled" %> style="border:none; padding:0; margin:0;">
                <c:forEach var="d" items="${donneesEtape}" varStatus="status">
                    <div class="visu-row" style="display: flex; align-items: flex-start; padding: 12px; border-bottom: 1px solid #f7fafc;">
                        
                        <%-- Champs techniques --%>
                        <input type="hidden" name="idDonne_${status.index}" value="${d.idDonne}">
                        <input type="hidden" name="ref_${status.index}" value="${d.refTypeContraint}">
                        <input type="hidden" name="type_${status.index}" value="${d.type}">
                        
                        <%-- Libellé --%>
                        <div style="flex: 1; font-weight: 600; color: #4a5568;">${d.type}</div>

                        <%-- Attribut --%>
                        <div style="flex: 1;">
                            <span class="view-mode">${not empty d.attribut ? d.attribut : '(Vide)'}</span>
                            <div class="edit-mode" style="display: none;">
                                <c:choose>
                                    <c:when test="${d.refTypeContraint == 'avis'}">
                                        <select name="attr_${status.index}" style="width: 100%; padding: 5px;">
                                            <c:forEach var="opt" items="${optionsAvis}">
                                                <option value="${opt}" ${d.attribut == opt ? 'selected' : ''}>${opt}</option>
                                            </c:forEach>
                                        </select>
                                    </c:when>
                                    <c:when test="${d.refTypeContraint == 'Bool'}">
                                        <select name="attr_${status.index}" style="width: 100%; padding: 5px;">
                                            <option value="OUI" ${d.attribut == 'OUI' ? 'selected' : ''}>OUI</option>
                                            <option value="NON" ${d.attribut == 'NON' ? 'selected' : ''}>NON</option>
                                        </select>
                                    </c:when>
                                    <c:otherwise>
                                        <input type="text" name="attr_${status.index}" value="${d.attribut}" style="width: 100%; padding: 5px;">
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>

                        <%-- Meta (Commentaire + Date) conditionnels --%>
                        <div style="flex: 1; margin-left: 20px;">
                            <%-- Gestion Commentaire --%>
                            <c:if test="${not empty d.commentaire}">
                                <div class="view-mode" style="color: #a0aec0; font-size: 0.85em;">${d.commentaire}</div>
                                <div class="edit-mode" style="display: none;">
                                    <input type="text" name="comm_${status.index}" value="${d.commentaire}" style="width: 100%; padding: 5px;">
                                </div>
                            </c:if>

                            <%-- Gestion Date --%>
                            <c:if test="${not empty d.date}">
                                <div class="view-mode" style="font-size: 0.8em; color: #718096;">Date: ${d.date}</div>
                                <div class="edit-mode" style="display: none;">
                                    <input type="date" name="date_${status.index}" value="${d.date}" style="width: 100%; padding: 5px; margin-top: 5px;">
                                </div>
                            </c:if>
                        </div>
                    </div>
                </c:forEach>
            </fieldset>
        </form>
    <% } %>
</div>

<script>
function activerEdition() {
    document.querySelectorAll('.view-mode').forEach(el => el.style.display = 'none');
    document.querySelectorAll('.edit-mode').forEach(el => el.style.display = 'block');
    document.getElementById('btn-modifier').style.display = 'none';
    document.getElementById('btn-enregistrer').style.display = 'inline-block';
    const fs = document.getElementById('fs-edition');
    if(fs) fs.disabled = false;
}
</script>