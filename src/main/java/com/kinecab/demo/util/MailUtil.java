
package com.kinecab.demo.util;

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
        "Bonjour,\n Pour activer votre compte KineCab veuillez cicker sur le lient qui suit : http://kinecab.com/login/confirme?token=xxx \n\n Cordialement,\n Votre Service Client KineCab ";
    public static final String CANCEL_TITLE = "KineCab - Confirmation d'annulation";
    public static final String CANCEL_CONTENT =
            "Bonjour,\n Votre rendez-vous du xxx à bien été annulé.\n\n Cordialement,\n Votre Service Client KineCab ";
    public static final String TOOK_TITLE = "KineCab - Prise de rendez-vous confirmé";
    public static final String TOOK_CONTENT =
            "Bonjour,\n Votre rendez-vous du xxx à bien été prit. Une confirmation va vous etres envoyé d'ici peu.\n\n Cordialement,\n Votre Service Client KineCab ";
    public static final String ACCEPTE_TITLE = "KineCab - Rendez-vous accepté";
    public static final String ACCEPTE_CONTENT =
            "Bonjour,\n Votre rendez-vous du xxx vient d'etre confirmé par le pratitien.\n\n Cordialement,\n Votre Service Client KineCab ";
    public static final String REFUSE_TITLE = "KineCab - Rendez-vous refusé";
    public static final String REFUSE_CONTENT =
            "Bonjour,\n Votre rendez-vous du xxx vient d'etre refusé par le pratitien.\n\n Cordialement,\n Votre Service Client KineCab ";
    public static final String CANCELED_TITLE = "KineCab - Rendez-vous annulé";
    public static final String CANCELED_CONTENT =
            "Bonjour,\n Votre rendez-vous du xxx vient d'etre annulé par le pratitien.\n\n Cordialement,\n Votre Service Client KineCab ";


    static {
        final String username = "no-reply@kinecab.com";
        final String password = "Kinecab5!";

        Properties prop = new Properties();
        prop.put("mail.smtp.host", "ssl0.ovh.net");
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
        message.setFrom(new InternetAddress("no-reply@kinecab.com"));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(mail));
        message.setSubject(title);
        message.setText(content);

        Transport.send(message);

    }
}
