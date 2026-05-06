package ldap;

import javax.naming.*;
import javax.naming.directory.*;
import java.util.*;

public class LdapService {

    private static final String LDAP_URL = "ldap://10.15.4.10:389";
    private static final String BASE_DN  = "dc=raffin,dc=local";
    private static final String DOMAIN   = "raffin.local";

    /**
     * Authentifie un utilisateur Active Directory
     */
    public static Map<String, Object> authenticate(String login, String password)
            throws NamingException {

        Hashtable<String, String> env = new Hashtable<>();

        env.put(Context.INITIAL_CONTEXT_FACTORY,
                "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, LDAP_URL);
        env.put(Context.SECURITY_AUTHENTICATION, "simple");
        env.put(Context.SECURITY_PRINCIPAL,
                login + "@" + DOMAIN);
        env.put(Context.SECURITY_CREDENTIALS,
                password);

        // 🔐 Tentative de connexion AD
        DirContext ctx = new InitialDirContext(env);

        // 🔍 Recherche de l'utilisateur
        SearchControls sc = new SearchControls();
        sc.setSearchScope(SearchControls.SUBTREE_SCOPE);

        NamingEnumeration<SearchResult> results =
                ctx.search(BASE_DN,
                        "(sAMAccountName=" + login + ")",
                        sc);

        if (!results.hasMore()) {
            throw new NamingException("Utilisateur non trouvé dans l’AD");
        }
        
        NamingEnumeration<SearchResult> result = results;
        
        /*
        while (result.hasMore()) {

            SearchResult sr = result.next();

            System.out.println("====================================");
            System.out.println("DN utilisateur : " + sr.getNameInNamespace());
            System.out.println("====================================");

            Attributes attrs = sr.getAttributes();
            NamingEnumeration<? extends Attribute> allAttrs = attrs.getAll();

            while (allAttrs.hasMore()) {
                Attribute attr = allAttrs.next();

                System.out.println("Attribut : " + attr.getID());

                NamingEnumeration<?> values = attr.getAll();
                while (values.hasMore()) {
                    Object value = values.next();
                    System.out.println("   → Valeur : " + value);
                }
            }
        }*/

        SearchResult sr = results.next();
        Attributes attrs = sr.getAttributes();

        Map<String, Object> user = new HashMap<>();
        user.put("login", login);
        user.put("nom", attrs.get("cn") != null ? attrs.get("cn").get() : "");
        user.put("mail", attrs.get("mail") != null ? attrs.get("mail").get() : "");
        user.put("groups", attrs.get("memberOf"));
        user.put("role", attrs.get("department"));
        ctx.close();
        return user;
    }
}