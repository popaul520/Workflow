package controller;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import  dao.RoleDAO;

@WebServlet("/admin-roles")
public class AdminRoleController extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	RoleDAO roleDao = new RoleDAO();
        String action = request.getParameter("action");
        
        if ("edit".equals(action)) {
            int id = Integer.parseInt(request.getParameter("id"));
            request.setAttribute("roleEtapes", roleDao.getEtapesByRole(id));
            request.setAttribute("roleId", id);
            request.setAttribute("roleName", request.getParameter("name"));
            request.getRequestDispatcher("/View/edit-role.jsp").forward(request, response);
        } else {
            request.setAttribute("roles", roleDao.getRolesWithSteps());
            request.getRequestDispatcher("/View/gestionrole.jsp").forward(request, response);
        }
    }

    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        RoleDAO roleDao = new RoleDAO();
        String action = request.getParameter("dbAction");
        int roleId = Integer.parseInt(request.getParameter("roleId"));

        if ("add".equals(action)) {
            int etape = Integer.parseInt(request.getParameter("etape"));
            roleDao.addDroit(roleId, etape);
        } else if ("delete".equals(action)) {
            int etape = Integer.parseInt(request.getParameter("etape"));
            roleDao.deleteDroit(roleId, etape);
        }
        response.sendRedirect("modifierRole?action=edit&id=" + roleId + "&name=" + request.getParameter("roleName"));
    }
}