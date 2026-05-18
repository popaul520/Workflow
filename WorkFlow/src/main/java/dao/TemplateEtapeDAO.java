package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import model.templateWorkflow;
import model.template_etape;

public class TemplateEtapeDAO {

    public List<template_etape> getEtapesByWorkflow(int workflowId) {
        List<template_etape> list = new ArrayList<>();
        String sql = "SELECT * FROM template_etape WHERE id_template_workflow = ? ORDER BY place ASC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, workflowId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
            	template_etape e = new template_etape();
                e.setId(rs.getInt("id"));
                e.setIdTemplateWorkflow(rs.getInt("id_template_workflow"));
                e.setRoleAssocie(rs.getInt("role_associe"));
                e.setPlace(rs.getInt("place"));
                e.setAttentePlace(rs.getInt("attente_place"));
                e.setNomEtape(rs.getString("nom_etape"));
                e.setEstFinale(rs.getBoolean("est_finale"));
                list.add(e);
            }
        } catch (SQLException ex) { ex.printStackTrace(); }
        return list;
    }

    public void saveOrUpdate(template_etape e) {
        String sql;
        if (e.getId() > 0) {
            sql = "UPDATE template_etape SET role_associe=?, place=?, attente_place=?, nom_etape=?, est_finale=? WHERE id=?";
        } else {
            sql = "INSERT INTO template_etape (role_associe, place, attente_place, nom_etape, est_finale, id_template_workflow) VALUES (?,?,?,?,?,?)";
        }
        
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, e.getRoleAssocie());
            ps.setInt(2, e.getPlace());
            ps.setInt(3, e.getAttentePlace());
            ps.setString(4, e.getNomEtape());
            ps.setBoolean(5, e.isEstFinale());
            ps.setInt(6, e.getId() > 0 ? e.getId() : e.getIdTemplateWorkflow());
            ps.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }
    
    public void delete(int id) {
        String sql = "DELETE FROM template_etape WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (SQLException ex) { ex.printStackTrace(); }
    }
    
    public template_etape getEtapeById(int id) {
        String sql = "SELECT * FROM template_etape WHERE id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTemplate(rs);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private template_etape mapResultSetToTemplate(ResultSet rs) throws SQLException {
    	template_etape t  = new template_etape();
        t.setId(rs.getInt("id"));
        t.setIdTemplateWorkflow(rs.getInt("id_template_workflow"));
        t.setRoleAssocie(rs.getInt("role_associe"));
        t.setPlace(rs.getInt("place"));
        t.setAttentePlace(rs.getInt("attente_place"));
        t.setNomEtape(rs.getString("nom_etape"));
        t.setEstFinale(rs.getBoolean("est_finale"));
        return t;
    }


}
