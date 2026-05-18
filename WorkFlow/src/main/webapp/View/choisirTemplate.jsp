<div class="form-card">
    <h2>Démarrer un nouveau projet</h2>
    <form action="lancer-workflow" method="post">
        <div class="form-group">
            <label>Nom du projet / Article</label>
            <input type="text" name="titre" required placeholder="Ex: Nouveau Produit 2026">
        </div>

        <div class="form-group">
            <label>Modèle de Workflow à suivre</label>
            <select name="id_template" required>
                <c:forEach var="t" items="${templates}">
                    <option value="${t.id}">${t.nom}</option>
                </c:forEach>
               
            </select>
        </div>
        <button type="submit" class="btn btn-submit">Initialiser et passer à l'étape 1 ➔</button>
    </form>
</div>