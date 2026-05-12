package ldap;

import java.util.Map;
import javax.naming.directory.Attribute;

import dao.UtilisateurDAO;
import model.Utilisateur;

public class LdapUserMapper {

    public static Utilisateur toUtilisateur(Map<String, Object> adData) {

        String login = (String) adData.get("login");

        UtilisateurDAO dao = new UtilisateurDAO();

        // Recherche en base
        Utilisateur u = dao.findByLogin(login);

        if (u != null) {
            // UTILISATEUR DÉJÀ EN BASE
            System.out.println("Utilisateur trouvé en base : " + login);
            return u;
        }

        //  2. Sinon création depuis l'AD
        System.out.println("Création utilisateur depuis AD : " + login);

        u = new Utilisateur();
        u.setLogin(login);
        u.setNom((String) adData.get("nom"));
        u.setPrenom((String) adData.get("prenom"));
        u.setMail((String) adData.get("mail"));


        //  Rôle via groupes AD
        Attribute groups = (Attribute) adData.get("groups");
        //u.setRole(mapRoleFromGroups(groups)); ne sert à rien on ne connais le role

        // Insertion DB
        dao.insert(u);

        //  Recharge depuis DB pour avoir l'ID
        return dao.findByLogin(login);
    }

    private static String mapRoleFromGroups(Attribute groups) {

        if (groups == null) return null;

        try {
            for (int i = 0; i < groups.size(); i++) {
                String dn = groups.get(i).toString();

                if (dn.contains("APP_PATRON")) return "PATRON";
                if (dn.contains("APP_COMMERCE")) return "COMMERCE";
                if (dn.contains("APP_PRODUCTION")) return "PRODUCTION";
                if (dn.contains("APP_APPRO")) return "APPROVISIONNEMENT";
                if (dn.contains("APP_SCM")) return "SCM";
                if (dn.contains("APP_LOGISTIQUE")) return "LOGISTIQUE";
                if (dn.contains("APP_QHE")) return "QHE";
                if (dn.contains("APP_DOP")) return "DOP";
                if (dn.contains("APP_METHODES")) return "METHODES";
                if (dn.contains("APP_CDG")) return "CDG";
                if (dn.contains("APP_DCD")) return "DCD";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}