
package db;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;


public class MailUtil {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Static fields/initializers
    //~ ----------------------------------------------------------------------------------------------------------------

    private static final Session session;
    public static final String CHANGE_PASSWORD_TITLE = "Changement de votre mot de passe";
    public static final String CHANGE_PASSWORD_CONTENT = "Bonjour,\n\n Suite à une demande de changement de votre mot de passe,\n voici votre nouveau mot de passe : xxx\n\n Cordialement,\n Votre Service Client KineCab";
    public static final String NEW_PERSON_TITLE = "Bienvenue sur KineCab !";
    public static final String NEW_PERSON_CONTENT =
        "Bonjour,\n Pour activer votre compte KineCab veuillez cicker sur le lient qui suit : http://51.178.55.94:8080/kineore_war/rest/login/confirme?token=xxx \n\n Cordialement,\n Votre Service Client KineCab ";

    static {
        final String username = "noreply.kinecab@gmail.com";
        final String password = "Azerty1234!";

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", "465");
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.socketFactory.port", "465");
        prop.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");

        session = Session.getInstance(prop, new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                });
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    public static void sendEmail(String mail, String title, String content) throws Exception {
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("noreply.kinecab@gmail.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail));
        message.setSubject(title);
        message.setText(content);

        Transport.send(message);

    }
}
