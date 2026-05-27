package service;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailSender {
    public static void send(String to, String subject, String text) throws Exception {
        Transport transport = null;
        try {
            Properties props = new Properties();

            // ON UTILISE LE SERVEUR GMAIL (Expéditeur)
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "465");
            props.put("mail.smtp.auth", "true");
            
            // Configuration SSL Direct (Port 465)
            props.put("mail.smtp.socketFactory.port", "465");
            props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
            props.put("mail.smtp.socketFactory.fallback", "false");

            props.put("mail.smtp.ssl.protocols", "TLSv1.2");
            props.put("mail.smtp.ssl.trust", "smtp.gmail.com");
           
            // Timeouts de sécurité
            props.put("mail.smtp.connectiontimeout", "3000");
            props.put("mail.smtp.timeout", "3000");
            props.put("mail.smtp.writetimeout", "3000");

            Session session = Session.getInstance(props, null);
           
            Message message = new MimeMessage(session);
            //  L'expéditeur réel de l'email
            message.setFrom(new InternetAddress("robin.colle38@gmail.com"));

            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(text);

            System.out.println("Connexion physique au SMTP de Gmail...");
            transport = session.getTransport("smtp");

            // Authentification sur les serveurs de Google avec ton token d'application
            transport.connect("smtp.gmail.com", "robin.colle38@gmail.com", "vggdnrsugcfzntvk");
            System.out.println(" CONNECTÉ AU SMTP DE GMAIL !");

            System.out.println(" ENVOI DU MAIL EN COURS...");
            transport.sendMessage(message, message.getAllRecipients());
            System.out.println(" Email envoyé avec succès !");
            
        } catch (Exception e) {
            System.out.println("L'ENVOI DE L'EMAIL A ÉCHOUÉ");
            throw e; 
        } finally {
            if (transport != null && transport.isConnected()) {
                try {
                    transport.close();
                    System.out.println(" Connexion SMTP fermée.");
                } catch (MessagingException e) {
                }
            }
        }
    }
}
