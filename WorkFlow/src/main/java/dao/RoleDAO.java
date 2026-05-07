package dao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoleDAO {
    // Récupère tous les rôles et concatène les étapes (ex: "1, 2, 5")
    public List<Map<String, Object>> getRolesWithSteps() {
        List<Map<String, Object>> list = new ArrayList<>();
        String sql = "SELECT r.id, r.role, STRING_AGG(d.etape::text, ', ' ORDER BY d.etape) as etapes " +
                     "FROM role r LEFT JOIN droit d ON r.id = d.role GROUP BY r.id, r.role ORDER BY r.id";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", rs.getInt("id"));
                map.put("role", rs.getString("role"));
                map.put("etapes", rs.getString("etapes"));
                list.add(map);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<Integer> getEtapesByRole(int roleId) {
        List<Integer> etapes = new ArrayList<>();
        String sql = "SELECT etape FROM droit WHERE role = ? ORDER BY etape";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) etapes.add(rs.getInt("etape"));
        } catch (SQLException e) { e.printStackTrace(); }
        return etapes;
    }

    public void addDroit(int roleId, int etape) {
        String sql = "INSERT INTO droit (id, role, etape) VALUES ((SELECT COALESCE(MAX(id),0)+1 FROM droit), ?, ?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ps.setInt(2, etape);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteDroit(int roleId, int etape) {
        String sql = "DELETE FROM droit WHERE role = ? AND etape = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, roleId);
            ps.setInt(2, etape);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
}