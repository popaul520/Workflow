<%@ page contentType="text/html;charset=UTF-8" %>
<%@ page import="java.util.*, model.Workflow" %>
<!DOCTYPE html>
<html>
<head>
    <title>Résultats de recherche</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/inte.css" />
</head>
<body>
    <div class="container">
        <div class="main">
            <div class="header">
                <h1>Recherche de Workflows</h1>
                <form action="search" method="get" style="display: flex; gap: 10px;">
                    <input type="text" name="q" class="search" 
                           placeholder="Rechercher un titre..." 
                           value="${lastSearch != null ? lastSearch : ''}">
                    <button type="submit">OK</button>
                </form>
            </div>

            <h2>Résultats pour : "${lastSearch}"</h2>
            <div class="box">
    <table border="1">
        <thead>
            <tr>
                <th>ID</th>
                <th>Titre</th>
                <th>Date</th>
                <th>Action</th>
            </tr>
        </thead>
        
        <div class="box">
    <table border="1" style="width:100%; border-collapse: collapse;">
        <thead>
            <tr style="background-color: #f2f2f2;">
                <th>ID</th>
                <th>Titre</th>
                <th>Date</th>
                <th>Action</th>
            </tr>
        </thead>
        <tbody>
            <% 
            List<Workflow> list = (List<Workflow>) request.getAttribute("workflows");
            if(list != null && !list.isEmpty()) {
                for(Workflow wf : list) {
            %>
                <tr>
                    <td><%= wf.getId() %></td>
                    <td><%= wf.getTitre() %></td>
                    <td>Non renseignée</td> <td>
                        <a href="details?id=<%= wf.getId() %>">Voir</a>
                    </td>
                </tr>
            <%
                }
            } else { 
            %>
                <tr>
                    <td colspan="4" style="text-align:center; padding: 10px;">
                        Aucun workflow trouvé pour "${lastSearch}"
                    </td>
                </tr>
            <% } %>
        </tbody>
    </table>
</div>
       
    </table>
</div>
            
            <p><a href="home">Retour à l'accueil</a></p>
        </div>
    </div>
</body>
</html>