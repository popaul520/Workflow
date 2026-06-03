<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="UTF-8">
<title>Sélection du Template</title>
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/css/style.css">
<style>
.grid-container {
	display: grid;
	grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
	gap: 25px;
	margin-top: 30px;
}

.template-card {
	background: white;
	border-radius: 10px;
	padding: 25px;
	box-shadow: 0 4px 15px rgba(0, 0, 0, 0.05);
	border: 1px solid #eef2f5;
	transition: transform 0.2s, box-shadow 0.2s;
	text-decoration: none;
	color: inherit;
	display: flex;
	flex-direction: column;
	justify-content: space-between;
}

.template-card:hover {
	transform: translateY(-5px);
	box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
	border-color: var(--accent, #3182ce);
}

.card-header {
	display: flex;
	justify-content: space-between;
	align-items: flex-start;
	margin-bottom: 15px;
}

.template-version {
	background: #edf2f7;
	color: #4a5568;
	font-size: 0.8em;
	font-weight: bold;
	padding: 4px 10px;
	border-radius: 20px;
}

.template-title {
	font-size: 1.3em;
	color: var(--primary, #2d3748);
	margin: 0 0 10px 0;
	font-weight: 700;
}

.template-desc {
	color: #718096;
	font-size: 0.9em;
	line-height: 1.5;
	margin-bottom: 20px;
	flex-grow: 1;
}

.card-footer {
	font-size: 0.85em;
	font-weight: bold;
	color: var(--accent, #3182ce);
	display: flex;
	align-items: center;
	gap: 5px;
}

.welcome-section {
	margin-bottom: 40px;
	border-bottom: 2px solid #edf2f7;
	padding-bottom: 20px;
	display: flex;
	justify-content: space-between;
	align-items: center;
}
</style>
</head>
<body>
	<div class="main-container"
		style="margin-left: 0; padding: 40px max(5%, 20px);">
		<div class="welcome-section">
			<div>
				<h1 style="font-size: 2.2em; color: var(--primary);">Bienvenue
					sur la plateforme Workflow</h1>
				<p style="color: #718096; margin-top: 5px;">Sélectionnez un
					modèle de procédure ci-dessous pour accéder à vos dossiers.</p>
			</div>
			<div class="user-controls">
				<span style="font-weight: bold; margin-right: 15px;"><c:out
						value="${sessionScope.user.nom}" /></span> <a href="logout"
					style="color: #e74c3c; text-decoration: none; font-weight: bold; font-size: 0.9em;">Déconnexion</a>
			</div>
		</div>

		<div class="grid-container">
			<c:forEach var="tpl" items="${templates}">
				<a href="homeport?template=${tpl.titre}" class="template-card">
					<div>
						<div class="card-header">
							<span class="template-version">v${tpl.version}</span>
						</div>
						<h3 class="template-title">
							<c:out value="${tpl.titre}" />
						</h3>
						<p class="template-desc">
							<c:out value="${tpl.commentaire}" />
						</p>
					</div>
					<div class="card-footer">Accéder aux dossiers -></div>
				</a>
			</c:forEach>
		</div>
	</div>
</body>
</html>