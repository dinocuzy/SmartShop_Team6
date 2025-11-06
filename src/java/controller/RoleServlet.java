package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Role;
import roleservice.IRoleService;
import roleservice.RoleService;

import java.io.IOException;
import java.util.List;

/**
 * Servlet xử lý các request CRUD cho Role
 * URL mapping: /admin/roles
 * Actions: list, add, edit, delete, save
 * 
 * NOTE: Roles được quản lý thông qua Users (JOIN với Roles table)
 * Servlet này được disable để gộp quản lý vào Users
 */
// @WebServlet("/admin/roles") // Disabled - Roles được quản lý qua Users
public class RoleServlet extends HttpServlet {
    
    private IRoleService roleService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        roleService = new RoleService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null || action.isEmpty()) {
            action = "list";
        }
        
        try {
            switch (action) {
                case "add":
                    showAddForm(request, response);
                    break;
                case "edit":
                    showEditForm(request, response);
                    break;
                case "delete":
                    deleteRole(request, response);
                    break;
                case "list":
                default:
                    listRoles(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            listRoles(request, response);
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        
        if (action == null || action.isEmpty()) {
            action = "list";
        }
        
        try {
            switch (action) {
                case "save":
                    saveRole(request, response);
                    break;
                default:
                    listRoles(request, response);
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "An error occurred: " + e.getMessage());
            
            String roleID = request.getParameter("roleID");
            if (roleID != null && !roleID.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
        }
    }
    
    private void listRoles(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        List<Role> roles = roleService.getAllRoles();
        request.setAttribute("roles", roles);
        request.getRequestDispatcher("/views/admin/roleList.jsp").forward(request, response);
    }
    
    private void showAddForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setAttribute("action", "add");
        request.setAttribute("role", new Role());
        request.getRequestDispatcher("/views/admin/roleList.jsp").forward(request, response);
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String roleIDParam = request.getParameter("roleID");
        
        if (roleIDParam == null || roleIDParam.isEmpty()) {
            request.setAttribute("errorMessage", "Role ID is required");
            listRoles(request, response);
            return;
        }
        
        try {
            int roleID = Integer.parseInt(roleIDParam);
            Role role = roleService.getRoleById(roleID);
            
            if (role == null) {
                request.setAttribute("errorMessage", "Role not found with ID: " + roleID);
                listRoles(request, response);
                return;
            }
            
            request.setAttribute("action", "edit");
            request.setAttribute("role", role);
            request.getRequestDispatcher("/views/admin/roleList.jsp").forward(request, response);
            
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid Role ID format");
            listRoles(request, response);
        }
    }
    
    private void deleteRole(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String roleIDParam = request.getParameter("roleID");
        
        if (roleIDParam == null || roleIDParam.isEmpty()) {
            request.setAttribute("errorMessage", "Role ID is required");
            listRoles(request, response);
            return;
        }
        
        try {
            int roleID = Integer.parseInt(roleIDParam);
            roleService.deleteRole(roleID);
            request.setAttribute("successMessage", "Role deleted successfully");
        } catch (NumberFormatException e) {
            request.setAttribute("errorMessage", "Invalid Role ID format");
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
        }
        
        listRoles(request, response);
    }
    
    private void saveRole(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String roleIDParam = request.getParameter("roleID");
        String roleName = request.getParameter("roleName");
        String description = request.getParameter("description");
        
        if (roleName == null || roleName.trim().isEmpty()) {
            request.setAttribute("errorMessage", "Role name is required");
            if (roleIDParam != null && !roleIDParam.isEmpty()) {
                showEditForm(request, response);
            } else {
                showAddForm(request, response);
            }
            return;
        }
        
        Role role = new Role();
        
        if (roleIDParam != null && !roleIDParam.isEmpty()) {
            try {
                int roleID = Integer.parseInt(roleIDParam);
                role.setRoleID(roleID);
            } catch (NumberFormatException e) {
                request.setAttribute("errorMessage", "Invalid Role ID format");
                showEditForm(request, response);
                return;
            }
        }
        
        role.setRoleName(roleName.trim());
        role.setDescription(description != null ? description.trim() : null);
        
        try {
            if (role.getRoleID() > 0) {
                roleService.updateRole(role);
                request.setAttribute("successMessage", "Role updated successfully");
            } else {
                roleService.addRole(role);
                request.setAttribute("successMessage", "Role added successfully");
            }
        } catch (IllegalArgumentException e) {
            request.setAttribute("errorMessage", e.getMessage());
            if (role.getRoleID() > 0) {
                request.setAttribute("role", role);
                request.setAttribute("action", "edit");
                request.getRequestDispatcher("/views/admin/roleList.jsp").forward(request, response);
                return;
            } else {
                request.setAttribute("role", role);
                request.setAttribute("action", "add");
                request.getRequestDispatcher("/views/admin/roleList.jsp").forward(request, response);
                return;
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/roles?action=list");
    }
}
