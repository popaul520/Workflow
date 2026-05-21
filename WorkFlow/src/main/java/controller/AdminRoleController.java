package controller;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Utilisateur;
import dao.RoleDAO;

@WebServlet("/admin-roles")
public class AdminRoleController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	
        RoleDAO roleDao = new RoleDAO();
        String action = request.getParameter("action");
        HttpSession session = request.getSession();
        Utilisateur user = (Utilisateur) session.getAttribute("user");
        try {
        	if ("edit".equals(action)) {
        	    int id = Integer.parseInt(request.getParameter("id"));
        	    String name = roleDao.getRoleNameById(id); 

        	    List<Integer> roleEtapes = roleDao.getEtapesByRole(id);
        	    Map<Integer, String> allRolesMap = roleDao.getAllRoleNames(); 
        	    allRolesMap.remove(id); 

        	    List<Integer> etapesDisponibles = new ArrayList<>();
        	    for (int i = 1; i <= 12; i++) { //pour le déroulant
        	        if (i == id) {
        	            continue; 
        	        }
        	        // Conditions filtrage
        	        if (!roleEtapes.contains(i) || !(user.getRole() == i)) {
        	            etapesDisponibles.add(i);
        	        }
        	    }

        	    request.setAttribute("roleId", id);
        	    request.setAttribute("roleName", name);
        	    request.setAttribute("roleEtapes", roleEtapes);
        	    request.setAttribute("etapesDisponibles", etapesDisponibles);
        	    request.setAttribute("allRolesMap", allRolesMap); 
        	    
        	    request.getRequestDispatcher("/View/modifierRole.jsp").forward(request, response);
        	
        	}else {
                // Mode liste globale
                List<Map<String, Object>> roles = roleDao.getRolesWithSteps();
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

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Force l'encodage pour récupérer les accents (ex: PRODUCTION)
        request.setCharacterEncoding("UTF-8");
        
        RoleDAO roleDao = new RoleDAO();
        String dbAction = request.getParameter("dbAction");
        String roleIdStr = request.getParameter("roleId");
        String etapeStr = request.getParameter("etape");
        String roleName = request.getParameter("roleName");

        try {
            if (roleIdStr != null && etapeStr != null) {
                int roleId = Integer.parseInt(roleIdStr);
                int etape = Integer.parseInt(etapeStr);

                if ("add".equals(dbAction)) {
                    roleDao.addDroit(roleId, etape);
                } else if ("delete".equals(dbAction)) {
                    roleDao.deleteDroit(roleId, etape);
                }
                // Redirection propre pour éviter les erreurs 404 :=)
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