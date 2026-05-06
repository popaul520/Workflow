<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<div style="padding: 15px; border: 1px solid #ddd; border-radius: 8px; background: #fff;">
    <c:choose>
        <c:when test="${not empty donneesEtape}">
            <table style="width:100%; border-collapse: collapse;">
                <c:forEach var="d" items="${donneesEtape}">
                    <tr style="border-bottom: 1px solid #eee;">
                        <td style="padding: 10px; font-weight: bold;">${d.type}</td>
                        <td style="padding: 10px;">${d.attribut}</td>
                        <td style="padding: 10px; color: #666; font-style: italic;">${d.commentaire}</td>
                    </tr>
                </c:forEach>
            </table>
        </c:when>
        <c:otherwise>
            <p>Aucune donnée saisie pour cette étape.</p>
        </c:otherwise>
    </c:choose>
    
    <div style="margin-top: 15px;">
        <a href="etapeController?n=${numEtape}&id_workflow=${param.id_workflow}" class="btn-etape" style="background: #3498db; color: white; padding: 5px 10px; text-decoration: none; border-radius: 4px; font-size: 0.8em;">
            Modifier / Saisir
        </a>
    </div>
</div>