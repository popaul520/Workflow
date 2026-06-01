<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Gestion des Catalogues Dynamiques</title>
    <style>
        :root {
            --bg-color: #f1f5f9;
            --card-bg: #ffffff;
            --text-main: #334155;
            --primary: #4f46e5;
            --primary-hover: #4338ca;
            --danger: #ef4444;
            --danger-hover: #dc2626;
            --border-color: #e2e8f0;
            --sidebar-width: 290px;
            --nav-btn-bg: #f8fafc;
            --nav-btn-hover: #e2e8f0;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: var(--bg-color);
            color: var(--text-main);
            margin: 0;
            padding: 0;
        }

        .app-layout {
            display: flex;
            height: 100vh;
            overflow: hidden;
        }

        /* Barre latérale gauche */
        .sidebar {
            width: var(--sidebar-width);
            background: #ffffff;
            border-right: 1px solid var(--border-color);
            display: flex;
            flex-direction: column;
            padding: 20px 15px;
            box-sizing: border-box;
        }

        /* Bloc de Navigation */
        .navigation-block {
            display: flex;
            flex-direction: column;
            gap: 8px;
            margin-bottom: 25px;
            padding-bottom: 15px;
            border-bottom: 2px dashed var(--border-color);
        }

        .nav-link-btn {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 10px 14px;
            background-color: var(--nav-btn-bg);
            color: #334155;
            text-decoration: none;
            font-size: 14px;
            font-weight: 600;
            border-radius: 6px;
            border: 1px solid var(--border-color);
            transition: all 0.2s;
        }

        .nav-link-btn:hover {
            background-color: var(--nav-btn-hover);
            color: var(--primary);
            transform: translateX(2px);
        }

        .sidebar h2 {
            font-size: 13px;
            text-transform: uppercase;
            letter-spacing: 0.05em;
            color: #64748b;
            margin-top: 0;
            margin-bottom: 12px;
        }

        .catalog-search {
            margin-bottom: 15px;
        }

        .catalog-list {
            flex: 1;
            overflow-y: auto;
            list-style: none;
            padding: 0;
            margin: 0;
        }

        .catalog-item {
            padding: 10px 12px;
            border-radius: 6px;
            margin-bottom: 4px;
            cursor: pointer;
            font-weight: 500;
            font-size: 14px;
            color: #475569;
            display: flex;
            justify-content: space-between;
            align-items: center;
            transition: all 0.2s;
        }

        .catalog-item:hover {
            background-color: #f8fafc;
            color: var(--primary);
        }

        .catalog-item.active {
            background-color: #eef2ff;
            color: var(--primary);
            font-weight: 600;
        }

        .catalog-item .count-badge {
            background: #e2e8f0;
            color: #64748b;
            font-size: 11px;
            padding: 2px 6px;
            border-radius: 10px;
        }

        .catalog-item.active .count-badge {
            background: var(--primary);
            color: white;
        }

        /* Contenu principal */
        .main-content {
            flex: 1;
            display: flex;
            flex-direction: column;
            overflow: hidden;
        }

        header {
            background: #ffffff;
            padding: 15px 30px;
            border-bottom: 1px solid var(--border-color);
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        header h1 {
            margin: 0;
            font-size: 22px;
            color: #0f172a;
        }

        .workspace {
            flex: 1;
            display: grid;
            grid-template-columns: 1fr 320px;
            gap: 20px;
            padding: 25px;
            overflow: hidden;
        }

        .card {
            background: var(--card-bg);
            border-radius: 8px;
            box-shadow: 0 1px 3px rgba(0,0,0,0.05);
            border: 1px solid var(--border-color);
            display: flex;
            flex-direction: column;
            overflow: hidden;
        }

        .card-header {
            padding: 15px 20px;
            border-bottom: 1px solid var(--border-color);
            font-weight: 600;
            font-size: 15px;
            background: #fafafa;
        }

        /* Barre d'outils supérieure */
        .search-action-bar {
            padding: 15px 20px;
            background: #f8fafc;
            border-bottom: 1px solid var(--border-color);
            display: flex;
            gap: 10px;
            align-items: center;
        }

        .form-control {
            padding: 8px 12px;
            border: 1px solid var(--border-color);
            border-radius: 6px;
            font-size: 14px;
            box-sizing: border-box;
        }

        .input-flex {
            flex: 1;
        }

        .btn {
            padding: 8px 16px;
            border-radius: 6px;
            font-size: 14px;
            font-weight: 500;
            cursor: pointer;
            border: none;
            display: inline-flex;
            align-items: center;
            justify-content: center;
            gap: 5px;
            white-space: nowrap;
        }

        .btn-primary { background-color: var(--primary); color: white; }
        .btn-primary:hover { background-color: var(--primary-hover); }
        .btn-secondary { background-color: #64748b; color: white; }
        .btn-secondary:hover { background-color: #475569; }
        .btn-danger { background-color: var(--danger); color: white; padding: 5px 10px; font-size: 12px; }
        .btn-danger:hover { background-color: var(--danger-hover); }
        
        .btn-outline { 
            background-color: transparent; 
            border: 1px solid var(--border-color); 
            color: #475569;
        }
        .btn-outline:hover { background-color: #f1f5f9; }

        .table-container {
            flex: 1;
            overflow-y: auto;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            text-align: left;
        }

        th {
            background: #f1f5f9;
            padding: 12px 16px;
            font-weight: 600;
            font-size: 13px;
            color: #475569;
            position: sticky;
            top: 0;
            z-index: 5;
            border-bottom: 1px solid var(--border-color);
        }

        td {
            padding: 12px 16px;
            border-bottom: 1px solid var(--border-color);
            font-size: 14px;
        }

        tr:hover { background-color: #f8fafc; }

        .catalog-inline-badge {
            display: inline-block;
            background-color: #e2e8f0;
            color: #334155;
            font-size: 11px;
            font-weight: 600;
            padding: 3px 8px;
            border-radius: 4px;
            text-transform: uppercase;
        }

        .form-panel { padding: 20px; }
        .form-group { margin-bottom: 15px; }
        .form-group label { display: block; font-weight: 600; font-size: 13px; margin-bottom: 5px; }
        .w-full { width: 100%; }

        .alert-error {
            background-color: #fee2e2;
            color: #991b1b;
            padding: 10px 20px;
            margin: 0;
            border-bottom: 1px solid #fca5a5;
        }
        
        .counter-text {
            font-size: 12px;
            color: #64748b;
            padding: 10px 20px;
            background: #fafafa;
            border-top: 1px solid var(--border-color);
            text-align: right;
        }
    </style>
</head>
<body>

<div class="app-layout">
    
    <aside class="sidebar">
        
        <div class="navigation-block">
            <a href="${pageContext.request.contextPath}/home" class="nav-link-btn">
                🏠 Accueil Principal
            </a>
            <a href="${pageContext.request.contextPath}/list-template" class="nav-link-btn">
                📋 Liste des Templates
            </a>
        </div>

        <h2>Catalogues associés</h2>
        <div class="catalog-search">
            <input type="text" id="sidebarSearch" class="form-control w-full" placeholder="Filtrer les catalogues...">
        </div>
        
        <ul class="catalog-list" id="catalogGroupList">
            <li class="catalog-item active" data-target="ALL">
                <span>TOUS LES CATALOGUES </span>
                <span class="count-badge" id="totalCount">0</span>
            </li>
        </ul>
    </aside>

    <main class="main-content">
        <header>
            <h1>Dictionnaire des Contraintes</h1>
            <span style="color: #64748b; font-size: 14px;">Architecture V2 Découplée</span>
        </header>

        <c:if test="${not empty error}">
            <div class="alert-error">${error}</div>
        </c:if>

        <div class="workspace">
            
            <div class="card">
                <div class="card-header" id="currentCatalogTitle">Toutes les valeurs</div>
                
                <div class="search-action-bar">
                    <input type="text" id="mainSearchInput" class="form-control input-flex" placeholder="Saisissez un mot-clé...">
                    <button type="button" id="btnSearch" class="btn btn-primary">🔍 Rechercher</button>
                    <button type="button" id="btnReset" class="btn btn-secondary">Réinitialiser</button>
                    
                    <button type="button" id="btnToggleOrder" class="btn btn-outline" title="Inverser l'ordre alphabétique">
                        <span id="sortIcon">⬇️</span> Ordre : <strong id="sortLabel">A-Z</strong>
                    </button>
                </div>

                <div class="table-container">
                    <table id="dataTable">
                        <thead>
                            <tr>
                                <th style="width: 80px;">ID</th>
                                <th style="width: 160px;">Catalogue associé</th>
                                <th>Valeur Libellé</th>
                                <th style="text-align: right; width: 100px;">Actions</th>
                            </tr>
                        </thead>
                        <tbody id="tableBody">
                            <c:forEach var="c" items="${listeContraintes}">
                                <tr data-catalog="${c.type}">
                                    <td><span style="color: #94a3b8;">#${c.id}</span></td>
                                    <td><span class="catalog-inline-badge">${c.type}</span></td>
                                    <td class="cell-value"><strong>${c.valeur}</strong></td>
                                    <td style="text-align: right;">
                                        <form action="${pageContext.request.contextPath}/View/type_contraint" method="post" style="display:inline;" onsubmit="return confirm('Supprimer définitivement la valeur \'${c.valeur}\' ?');">
                                            <input type="hidden" name="action" value="delete">
                                            <input type="hidden" name="id" value="${c.id}">
                                            <button type="submit" class="btn btn-danger">Supprimer</button>
                                        </form>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
                <div class="counter-text" id="totalVisibleCounter">0 élément(s) affiché(s)</div>
            </div>

            <div class="card">
                <div class="card-header">Nouvelle Entrée</div>
                <div class="form-panel">
                    <form action="${pageContext.request.contextPath}/View/type_contraint" method="post">
                        <input type="hidden" name="action" value="add">
                        
                        <div class="form-group">
                            <label for="type">Nom du Catalogue</label>
                            <input type="text" id="type" name="type" class="form-control w-full" placeholder="ex: client, pays" required>
                        </div>

                        <div class="form-group">
                            <label for="valeur">Valeur textuelle</label>
                            <input type="text" id="valeur" name="valeur" class="form-control w-full" placeholder="ex: des pâtes" required>
                        </div>
                        <button type="submit" class="btn btn-primary w-full" style="margin-top: 10px;"> Enregistrer</button>
                    </form>
                </div>
            </div>
        </div>
    </main>
</div>

<script>
document.addEventListener("DOMContentLoaded", function() {
    const tableBody = document.getElementById("tableBody");
    const tableRows = Array.from(document.querySelectorAll("#dataTable tbody tr"));
    const catalogGroupList = document.getElementById("catalogGroupList");
    const mainSearchInput = document.getElementById("mainSearchInput");
    const btnSearch = document.getElementById("btnSearch");
    const btnReset = document.getElementById("btnReset");
    const currentCatalogTitle = document.getElementById("currentCatalogTitle");
    const sidebarSearch = document.getElementById("sidebarSearch");
    
    const btnToggleOrder = document.getElementById("btnToggleOrder");
    const sortIcon = document.getElementById("sortIcon");
    const sortLabel = document.getElementById("sortLabel");
    const totalVisibleCounter = document.getElementById("totalVisibleCounter");

    let activeCatalog = "ALL";
    let isDescending = false;

    // 1. Extraction et statistiques
    const stats = {};
    tableRows.forEach(row => {
        const cat = row.getAttribute("data-catalog");
        if(cat) {
            stats[cat] = (stats[cat] || 0) + 1;
        }
    });
    document.getElementById("totalCount").textContent = tableRows.length;

    // 2. Remplissage de la liste avec le nom dynamique du catalogue associé explicite
    Object.keys(stats).sort().forEach(cat => {
        const li = document.createElement("li");
        li.className = "catalog-item";
        li.setAttribute("data-target", cat);
        // Ajout visuel : Icône de dossier + Nom du catalogue mis en avant
        li.innerHTML = `<span> ${stats[cat]}</span><span class="count-badge">${stats[cat]}</span>`;
        catalogGroupList.appendChild(li);
    });

    // 3. Filtrage et Tri
    function applyGlobalFilterAndSort() {
        const textQuery = mainSearchInput.value.toLowerCase().trim();
        let visibleCount = 0;
        
        tableRows.forEach(row => {
            const rowCat = row.getAttribute("data-catalog");
            const rowVal = row.querySelector(".cell-value").textContent.toLowerCase();
            
            const matchCatalog = (activeCatalog === "ALL" || rowCat === activeCatalog);
            const matchText = (textQuery === "" || rowVal.includes(textQuery));

            if(matchCatalog && matchText) {
                row.style.display = "";
                visibleCount++;
            } else {
                row.style.display = "none";
            }
        });

        const sortedRows = tableRows.slice().sort((rowA, rowB) => {
            const valA = rowA.querySelector(".cell-value").textContent.toLowerCase().trim();
            const valB = rowB.querySelector(".cell-value").textContent.toLowerCase().trim();
            return isDescending ? valB.localeCompare(valA) : valA.localeCompare(valB);
        });

        sortedRows.forEach(row => tableBody.appendChild(row));
        totalVisibleCounter.textContent = visibleCount + " élément(s) affiché(s) sur " + tableRows.length;
    }

    // 4. Inversion de l'ordre
    btnToggleOrder.addEventListener("click", function() {
        isDescending = !isDescending;
        if(isDescending) {
            sortIcon.textContent = "⬆️";
            sortLabel.textContent = "Z-A";
        } else {
            sortIcon.textContent = "⬇️";
            sortLabel.textContent = "A-Z";
        }
        applyGlobalFilterAndSort();
    });

    // 5. Sélection d'un catalogue
    // Ajout d'un écouteur global délégué pour prendre en compte les éléments injectés en JS
    catalogGroupList.addEventListener("click", function(e) {
        const item = e.target.closest(".catalog-item");
        if (!item) return;

        document.querySelectorAll(".catalog-item").forEach(i => i.classList.remove("active"));
        item.classList.add("active");
        
        activeCatalog = item.getAttribute("data-target");
        currentCatalogTitle.textContent = activeCatalog === "ALL" ? "Toutes les valeurs" : "Catalogue : " + activeCatalog.toUpperCase();
        
        if(activeCatalog !== "ALL") {
            document.getElementById("type").value = activeCatalog;
        }
        applyGlobalFilterAndSort();
    });

    // 6. Recherches
    btnSearch.addEventListener("click", applyGlobalFilterAndSort);
    mainSearchInput.addEventListener("keypress", function(e) {
        if (e.key === "Enter") applyGlobalFilterAndSort();
    });

    btnReset.addEventListener("click", function() {
        mainSearchInput.value = "";
        applyGlobalFilterAndSort();
    });

    sidebarSearch.addEventListener("input", function() {
        const query = this.value.toLowerCase().trim();
        document.querySelectorAll(".catalog-item").forEach(item => {
            const target = item.getAttribute("data-target");
            if(target === "ALL") return;
            
            if(target.toLowerCase().includes(query)) item.style.display = "";
            else item.style.display = "none";
        });
    });
    applyGlobalFilterAndSort();
});
</script>
</body>
</html>