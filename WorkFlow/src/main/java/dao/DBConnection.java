package dao;
//permet la connection à la base de donnée
import java.sql.Connection;
import java.sql.DriverManager;
//setup /Host:postgres User Name: postgres Password:1234 port:5432 
public class DBConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/bd_workflow";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root"; 
    public static Connection getConnection() {
        try {
            Class.forName("org.postgresql.Driver");
            //System.out.println("Connexion OK !");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}