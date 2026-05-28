package controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import model.Utilisateur;
import service.RoleService;

@WebServlet("/admin-roles")
public class AdminRoleController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Injection du service gérant les rôles
    private final RoleService roleService = new RoleService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        
        // Optionnel mais recommandé : Vérification de session globale
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("edit".equals(action)) {
                int id = Integer.parseInt(request.getParameter("id"));
                
                // Appels via la couche Service
                String name = roleService.getRoleName(id); 
                List<Integer> roleEtapes = roleService.getStepsForRole(id);
                Map<Integer, String> allRolesMap = roleService.getAllOtherRolesMap(id); 
                List<Integer> etapesDisponibles = roleService.calculateAvailableSteps(id, roleEtapes, user);

                // Assignation des attributs pour la JSP
                request.setAttribute("roleId", id);
                request.setAttribute("roleName", name);
                request.setAttribute("roleEtapes", roleEtapes);
                request.setAttribute("etapesDisponibles", etapesDisponibles);
                request.setAttribute("allRolesMap", allRolesMap); 
                
                request.getRequestDispatcher("/View/modifierRole.jsp").forward(request, response);
            
            } else {
                // Mode liste globale via le Service
                List<Map<String, Object>> roles = roleService.getAllRolesWithSteps();
                request.setAttribute("roles", roles);
                request.getRequestDispatcher("/View/gestionrole.jsp").forward(request, response);
            }
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID de rôle invalide");
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        String dbAction = request.getParameter("dbAction");
        String roleIdStr = request.getParameter("roleId");
        String etapeStr = request.getParameter("etape");
        String roleName = request.getParameter("roleName");

        try {
            if (roleIdStr != null && etapeStr != null) {
                int roleId = Integer.parseInt(roleIdStr);
                int etape = Integer.parseInt(etapeStr);

                // Traitement de l'action via le Service
                roleService.manageDroit(dbAction, roleId, etape);

                // Redirection post-action
                String encodedName = URLEncoder.encode(roleName != null ? roleName : "", StandardCharsets.UTF_8);
                response.sendRedirect("admin-roles?action=edit&id=" + roleId + "&name=" + encodedName);
            } else {
                response.sendRedirect("admin-roles"); 
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendRedirect("admin-roles");
        }
    }
}