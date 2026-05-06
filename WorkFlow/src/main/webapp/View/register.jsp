<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html lang="fr">
<head>
<meta charset="UTF-8">
<title>Inscription</title>

<style>
body {
    margin: 0;
    font-family: Arial;
    background-color: #f5f6f7;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
}
.box {
    background: white;
    padding: 30px;
    border: 1px solid #ccc;
    width: 350px;
}
input {
    width: 100%;
    padding: 8px;
    margin: 8px 0;
}
button {
    width: 100%;
    padding: 10px;
    background: #4a6fa5;
    color: white;
    border: none;
}
.error {
    color: red;
}
</style>

</head>
<body>

<%@ page contentType="text/html;charset=UTF-8" %>


<head>
<meta charset="UTF-8">
<title>Inscription</title>

<style>
body {
    margin: 0;
    font-family: Arial;
    background-color: #f5f6f7;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
}
.box {
    background: white;
    padding: 30px;
    border: 1px solid #ccc;
    width: 350px;
}
input {
    width: 100%;
    padding: 8px;
    margin: 8px 0;
}
button {
    width: 100%;
    padding: 10px;
    background: #4a6fa5;
    color: white;
    border: none;
}
.error {
    color: red;
}
</style>

</head>
<body>

<div class="box">
    <h2>Inscription</h2>

	<form action="${pageContext.request.contextPath}/register" method="post">        
		<input type="text" name="login" placeholder="Login" required>
        <input type="password" name="mdp" placeholder="Mot de passe" required>
        <input type="text" name="nom" placeholder="Nom" required>
        <input type="text" name="prenom" placeholder="Prénom">
        <input type="email" name="mail" placeholder="Email">
        <button type="submit">S'inscrire</button>
    </form>

    <div class="error">
        <%= request.getAttribute("error") != null ? request.getAttribute("error") : "" %>
    </div>

    <!-- LIEN SIMPLE (PAS UN FORM) -->
    <p><a href="login">Déjà un compte ? Connexion</a></p>

</div>

</body>
</html>
