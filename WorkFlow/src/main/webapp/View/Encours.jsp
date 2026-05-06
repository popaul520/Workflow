<!DOCTYPE html>
<html lang="fr">

<head>
	<meta charset="UTF-8">
	<%@ page contentType="text/html;charset=UTF-8" %>
	<%@ page import="java.util.*, model.Workflow" %>
			<title>Accueil Workflow</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/inte.css" />
</head>

<body>

	<div class="container">
		<!-- Sidebar -->
		<div class="sidebar">
			<h3>Workflow</h3>
			<ul>
				<li>Accueil</li>
				<li>En cours</li>
				<li>Terminé</li>
				<li>Annulé</li>
				<li>Créer Workflow</li>
				
			</ul>
		</div>

		<!-- Main -->
		<div class="main">
			<div class="header">
				<h1>Accueil WorkFlow</h1>
				<input type="text" class="search" placeholder="recherche">
				<div class="profile-box">Compte</div>
			</div>

			<h2>En cours de vous</h2>
			<p>Nombre de workflows reçus : ${workflows.size()}</p>
			<div class="box">
				<table>
					<thead>
						<tr>
							<th>Titre</th>
							<th>Date</th>
							<th>Demandeur</th>
							<th>EtapeNB/NB</th>
							<th>Attente_Rôle</th>
							<th>ID</th>
							<th>Cours</th>
						</tr>
					</thead>

					<tbody>
						<% 
					    List<Workflow> list = (List<Workflow>) request.getAttribute("workflows");
					    if(list != null && !list.isEmpty()) {
					        for(Workflow wf : list) {
						%>
					        <tr>
					            <td><%= wf.getTitre() %></td>
					            <td>-</td> <td>-</td> <td>-</td>
					            <td>-</td>
					            <td><%= wf.getId() %></td>
					            <td><a href="details?id=<%= wf.getId() %>">Voir</a></td>
					        </tr>
					<%
					        }
					    } else { 
					%>
					        <tr><td colspan="7">Aucun workflow en cours.</td></tr>
					<% } %>
					</tbody>
				</table>
			</div>

			<h2>En attente de vous</h2>
			<div class="box"></div>
		</div>
	</div>

</body>

</html>