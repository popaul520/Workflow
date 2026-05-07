<div class="main-container">
    <h2>Détails des accès : ${roleName}</h2>
    <a href="admin-roles" style="margin-bottom: 10px; display: inline-block;">← Retour à la liste</a>
    
    <div class="box">
        <h3>Étapes configurées</h3>
        <table>
            <c:forEach var="etape" items="${roleEtapes}">
                <tr>
                    <td>Étape ${etape}</td>
                    <td style="text-align: right;">
                        <form action="admin-roles" method="post" style="display:inline;">
                            <input type="hidden" name="dbAction" value="delete">
                            <input type="hidden" name="roleId" value="${roleId}">
                            <input type="hidden" name="roleName" value="${roleName}">
                            <input type="hidden" name="etape" value="${etape}">
                            <button type="submit" style="background:#e74c3c; color:white; border:none; padding:5px 10px; border-radius:4px; cursor:pointer;">Supprimer</button>
                        </form>
                    </td>
                </tr>
            </c:forEach>
        </table>

        <hr style="margin: 20px 0; border: 0; border-top: 1px solid #eee;">
        
        <form action="admin-roles" method="post">
            <input type="hidden" name="dbAction" value="add">
            <input type="hidden" name="roleId" value="${roleId}">
            <input type="hidden" name="roleName" value="${roleName}">
            <label>Ajouter l'accès à l'étape :</label>
            <input type="number" name="etape" min="1" max="10" required style="padding:5px; margin:0 10px;">
            <button type="submit" class="profile-btn">Ajouter</button>
        </form>
    </div>
</div>