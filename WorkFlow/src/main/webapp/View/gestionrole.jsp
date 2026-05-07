<div class="main-container">
    <h2>Gestion des Accès par Rôle</h2>
    <div class="box">
        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Rôle</th>
                    <th>Étapes autorisées</th>
                    <th>Action</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="r" items="${roles}">
                    <tr>
                        <td>${r.id}</td>
                        <td><strong>${r.role}</strong></td>
                        <td>${empty r.etapes ? '<span style="color:gray italic">Aucun accès</span>' : r.etapes}</td>
                        <td><a href="admin-roles?action=edit&id=${r.id}&name=${r.role}" class="btn-view">Modifier</a></td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>