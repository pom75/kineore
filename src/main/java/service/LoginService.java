
package service;

import java.nio.charset.StandardCharsets;

import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.hash.Hashing;

import db.LoginDB;

import static db.LoginDB.*;

import static db.MailUtil.*;

import db.entity.Admin;
import db.entity.Person;
import db.entity.PersonTemp;
import db.entity.Token;

import json.CookieMessage;
import json.Message;


@Path("/login")
public class LoginService {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @POST
    @Produces("application/json")
    @Path("/inscription")
    public Response inscription(@FormParam("nom") String nom,
        @FormParam("prenom") String prenom,
        @FormParam("email") String email,
        @FormParam("tel") String tel,
        @FormParam("password") String password) {
        if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || !validateEmailStandard(email) || !tel.matches("[0][76][0-9]{8}$") || (password.length() < 6)) {
            return Response.status(Response.Status.OK).entity(new Message("FAIL", "Un des champ est invalide")).build();
        } else {
            if (!getPersonByEmail(email).isEmpty()) {
                return Response.status(Response.Status.OK).entity(new Message("FAIL", "Mail déjà utilisé")).build();
            }
            try {
                String token = passwordGenerator.generate(10);
                LoginDB.savePersonTemp(new PersonTemp(nom, prenom, email, tel, password, token));
                sendEmail(email, NEW_PERSON_TITLE, NEW_PERSON_CONTENT.replace("xxx", token));
            } catch (Exception e) {
                return Response.status(Response.Status.OK).entity(new Message("FAIL", "Mail déjà utilisé "+e)).build();
            }
             return Response.status(Response.Status.OK).entity(new Message("OK", "Inscription Réussite. Vous allez recevoir un lien d'activation de votre compte par mail.")).build();

        }
    }

    @POST
    @Produces("application/json")
    @Path("/connexion")
    public Response connexion(@FormParam("email") String email,
                              @FormParam("password") String password) {
        try {
            List<Person> person = LoginDB.checkPasswordByEmailPerson(email, Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString());
            if (!person.isEmpty()) {
                CookieMessage cookieMessage = new CookieMessage("OK", "Connexion réussite", getTokenPerson(person.get(0)), "0");
                return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(cookieMessage).build();
            } else {
                List<Admin> admin = LoginDB.checkPasswordByEmailAdmin(email, Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString());
                if (!admin.isEmpty()) {
                    CookieMessage message = new CookieMessage("OK", "Connexion réussite", LoginDB.getTokenAdmin(admin.get(0)), "1");
                    return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(message).build();
                } else {
                    return Response.status(Response.Status.OK).entity(new Message("FAIL", "Email ou mot de passe incorrecte")).build();
                }
            }
        } catch (Exception e) {
            return Response.status(Response.Status.OK).entity(new Message("FAIL", "Email ou mot de passe incorrecte "+e)).build();
        }
    }

    @POST
    @Produces("application/json")
    @Path("/motdepasseoublier")
    public Response motDePasseOublier(@FormParam("email") String email) throws Exception {
        List<Person> personByEmail = getPersonByEmail(email);
        if (!personByEmail.isEmpty()) {
            String newPassword = newPassword(personByEmail.get(0));
            if (!newPassword.isEmpty()) {
                sendEmail(email, CHANGE_PASSWORD_TITLE, CHANGE_PASSWORD_CONTENT.replace("xxx", newPassword));
                return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(new Message("OK", "Un nouveau mot de passe vas vous être envoyé d'ici 5 minutes")).build();
            }
            return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(new Message("FAIL", "Erreur pendant le changement de mot de passe")).build();
        } else {
            return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(new Message("FAIL", "Cet email n'existe pas")).build();
        }
    }

    @GET
    @Produces("application/json")
    @Path("/confirme")
    public Response confirme(@QueryParam("token") String token) {
        List<PersonTemp> personTemps = tempTokenExist(token);
        if (!personTemps.isEmpty()) {
            PersonTemp personTemp = personTemps.get(0);
            Person person = new Person(personTemp.getNom(), personTemp.getPrenom(), personTemp.getEmail(), personTemp.getTel(), "");
            person.setCryptedPassword(personTemp.getPassword());
            savePerson(person);
            deletePersonTemp(personTemp);
            return Response.status(Response.Status.OK).type(MediaType.APPLICATION_JSON).entity(new Message("OK", "Votre compte a été validé, vous pouvez maintenant vous connecter.")).build();
        }
        return Response.status(Response.Status.OK).entity( new Message("ERROR", "Token invalide.")).build();
    }

    @POST
    @Produces("application/json")
    @Path("/getprofil")
    public Person getProfil(@FormParam("token") String token) {
        List<Person> personByToken = getPersonByToken(token);
        if (!personByToken.isEmpty()) {
            Person person = personByToken.get(0);
            person.setPassword("");
            return person;
        } else {
            return null;
        }
    }

    @POST
    @Produces("application/json")
    @Path("/changeprofil")
    public Response changeProfil(@FormParam("num") String tel,
        @FormParam("mdp") String mdp,
        @FormParam("token") String token) {
        List<Person> personByToken = getPersonByToken(token);
        if (personByToken.isEmpty()) {
            return Response.status(Response.Status.OK).entity(new Message("FAIL", "Token invalide.")).build();
        }
        if (tel.isEmpty() || !tel.matches("[0][76][0-9]{8}$")) {
            return Response.status(Response.Status.OK).entity( new Message("FAIL", "Numéro de téléphone invalide.")).build();
        }
        if ((mdp.length() >= 1) && (mdp.length() < 6)) {
            return Response.status(Response.Status.OK).entity( new Message("FAIL", "Mot de passe trop court.")).build();
        }

        Person person = personByToken.get(0);
        if (mdp.length() >= 6) {
            person.setPassword(mdp);
        }
        person.setTel(tel);
        savePerson(person);
        return Response.status(Response.Status.OK).entity( new Message("OK", "Modifications enregistré.")).build();
    }

    @POST
    @Produces("application/json")
    @Path("/suppprofil")
    public Response deleteProfil(@FormParam("mdp") String mdp,
        @FormParam("token") String token) {
        List<Person> personByToken = getPersonByToken(token);
        if (personByToken.isEmpty()) {
            return Response.status(Response.Status.OK).entity( new Message("FAIL", "Token invalide.")).build();
        }
        List<Person> people = checkPasswordByTokenPerson(token, Hashing.sha256().hashString(mdp, StandardCharsets.UTF_8).toString());
        if (people.isEmpty()) {
            return Response.status(Response.Status.OK).entity( new Message("FAIL", "Mot de passe incorrecte.")).build();
        }
        removeToken(new Token(people.get(0).getId(), token, "0"));
        removePerson(people.get(0));
        return Response.status(Response.Status.OK).entity( new Message("OK", "Compte supprimé.")).build();
    }

    @POST
    @Produces("application/json")
    @Path("/getprofiladmin")
    public Response getProfilAdmin(@FormParam("token") String token) {
        List<Admin> adminByToken = getAdminByToken(token);
        if (!adminByToken.isEmpty()) {
            Admin admin = adminByToken.get(0);
            admin.setPassword("");
            return Response.ok().entity(admin).build();
        } else {
            return null;
        }
    }

    @POST
    @Produces("application/json")
    @Path("/changeprofiladmin")
    public Response changeProfilAdmin(@FormParam("num") String tel,
        @FormParam("mdp") String mdp,
        @FormParam("token") String token) {
        List<Admin> adminByToken = getAdminByToken(token);
        if (adminByToken.isEmpty()) {
            return Response.status(Response.Status.OK).entity( new Message("FAIL", "Token invalide.")).build();
        }
        if (tel.isEmpty() || !tel.matches("[0][76][0-9]{8}$")) {
            return Response.status(Response.Status.OK).entity( new Message("FAIL", "Numéro de téléphone invalide.")).build();
        }
        if ((mdp.length() >= 1) && (mdp.length() < 6)) {
            return Response.status(Response.Status.OK).entity( new Message("FAIL", "Mot de passe trop court.")).build();
        }

        Admin admin = adminByToken.get(0);
        if (mdp.length() >= 6) {
            admin.setPassword(mdp);
        }
        admin.setTel(tel);
        saveAdmin(admin);
        return Response.status(Response.Status.OK).entity( new Message("OK", "Modifications enregistré.")).build();
    }

    @POST
    @Produces("application/json")
    @Path("/suppprofiladmin")
    public Response deleteProfilAdmin(@FormParam("mdp") String mdp,
        @FormParam("token") String token) {
        List<Admin> adminByToken = getAdminByToken(token);
        if (adminByToken.isEmpty()) {
            return Response.status(Response.Status.OK).entity(new Message("FAIL", "Token invalide.")).build();
        }
        List<Admin> admins = checkPasswordByTokenAdmin(token, Hashing.sha256().hashString(mdp, StandardCharsets.UTF_8).toString());
        if (admins.isEmpty()) {
            return Response.status(Response.Status.OK).entity(new Message("FAIL", "Mot de passe incorrecte.")).build();
        }
        removeToken(new Token(admins.get(0).getId(), token, "1"));
        removeAdmin(admins.get(0));
        return Response.status(Response.Status.OK).entity(new Message("OK", "Compte supprimé.")).build();
    }

    private static boolean validateEmailStandard(String email) {
        try {
            InternetAddress testEmail = new InternetAddress(email);
            testEmail.validate();
            return true;
        } catch (AddressException e) {
            return false;
        }
    }
}
