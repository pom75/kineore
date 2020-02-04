/**
 *  Copyright Murex S.A.S., 2003-2020. All Rights Reserved.
 *
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package com.kinecab.demo.controller;

import java.nio.charset.StandardCharsets;

import java.util.List;

import com.google.common.hash.Hashing;

import static com.kinecab.demo.db.AdminDB.*;
import com.kinecab.demo.db.LoginDB;
import static com.kinecab.demo.db.LoginDB.*;
import com.kinecab.demo.db.entity.Admin;
import com.kinecab.demo.db.entity.Person;
import com.kinecab.demo.db.entity.PersonTemp;
import com.kinecab.demo.db.entity.Token;
import com.kinecab.demo.json.CookieMessage;
import com.kinecab.demo.json.Message;
import static com.kinecab.demo.util.MailUtil.*;

import org.apache.commons.validator.routines.EmailValidator;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class LoginService {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @PostMapping(value = "/login/connexion", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message connexion(@RequestParam("email") String email,
        @RequestParam("password") String password) {
        try {
            List<Person> person = LoginDB.checkPasswordByEmailPerson(email, Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString());
            if (!person.isEmpty()) {
                return new CookieMessage("OK", "Connexion réussite", getTokenPerson(person.get(0)), "0");
            } else {
                List<Admin> admin = checkPasswordByEmailAdmin(email, Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString());
                if (!admin.isEmpty()) {
                    return new CookieMessage("OK", "Connexion réussite", getTokenAdmin(admin.get(0)), "1");
                } else {
                    return new Message("FAIL", "Email ou mot de passe incorrecte");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Email ou mot de passe incorrecte ");
        }
    }

    @PostMapping(value = "/login/inscription", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message inscription(@RequestParam("nom") String nom,
        @RequestParam("prenom") String prenom,
        @RequestParam("email") String email,
        @RequestParam("tel") String tel,
        @RequestParam("password") String password) {
        try {
            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || !validateEmailStandard(email) || !tel.matches("[0][76][0-9]{8}$") || (password.length() < 6)) {
                return new Message("FAIL", "Un des champ est invalide");
            } else {
                if (!getPersonByEmail(email).isEmpty()) {
                    return new Message("FAIL", "Mail déjà utilisé");
                }
                try {
                    String token = passwordGenerator.generate(10);
                    LoginDB.savePersonTemp(new PersonTemp(nom, prenom, email, tel, password, token));
                    sendEmail(email, NEW_PERSON_TITLE, NEW_PERSON_CONTENT.replace("xxx", token));
                } catch (Exception e) {
                    e.printStackTrace();
                    return new Message("FAIL", "Mail déjà utilisé " + e);
                }
                return new Message("OK", "Inscription Réussite. Vous allez recevoir un lien d'activation de votre compte par mail. Pensez a regarder dans vos spams.");

            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Inscription Impossible.");
        }
    }

    @PostMapping(value = "/login/motdepasseoublier", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message motDePasseOublier(@RequestParam("email") String email) throws Exception {
        List<Person> personByEmail = getPersonByEmail(email);
        try {
            if (!personByEmail.isEmpty()) {
                String newPassword = newPassword(personByEmail.get(0));
                if (!newPassword.isEmpty()) {
                    sendEmail(email, CHANGE_PASSWORD_TITLE, CHANGE_PASSWORD_CONTENT.replace("xxx", newPassword));
                    return new Message("OK", "Un nouveau mot de passe vas vous être envoyé d'ici 5 minutes. Pensez à regarder dans vos spams.");
                }
                return new Message("FAIL", "Erreur pendant le changement de mot de passe");
            } else {
                return new Message("FAIL", "Cet email n'existe pas");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Impossible de générer un nouveau mot de passe.");
        }
    }

    @GetMapping(value = "/login/confirme", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message confirme(@RequestParam("token") String token) {
        try {
            List<PersonTemp> personTemps = tempTokenExist(token);
            if (!personTemps.isEmpty()) {
                PersonTemp personTemp = personTemps.get(0);
                Person person = new Person(personTemp.getNom(), personTemp.getPrenom(), personTemp.getEmail(), personTemp.getTel(), "");
                person.setCryptedPassword(personTemp.getPassword());
                savePerson(person);
                deletePersonTemp(personTemp);
                return new Message("OK", "Votre compte a été validé, vous pouvez maintenant vous connecter.");
            }
            return new Message("ERROR", "Token invalide.");
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Impossible d'activer le compte");
        }
    }

    @PostMapping(value = "/login/getprofil", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Person getProfil(@RequestParam("token") String token) {
        try {
            List<Person> personByToken = getPersonByToken(token);
            if (!personByToken.isEmpty()) {
                Person person = personByToken.get(0);
                person.setPassword("");
                return person;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(value = "/login/changeprofil", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message changeProfil(@RequestParam("num") String tel,
        @RequestParam("mdp") String mdp,
        @RequestParam("token") String token) {
        try {
            List<Person> personByToken = getPersonByToken(token);
            if (personByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide.");
            }
            if (tel.isEmpty() || !tel.matches("[0][76][0-9]{8}$")) {
                return new Message("FAIL", "Numéro de téléphone invalide.");
            }
            if ((mdp.length() >= 1) && (mdp.length() < 6)) {
                return new Message("FAIL", "Mot de passe trop court.");
            }

            Person person = personByToken.get(0);
            if (mdp.length() >= 6) {
                person.setPassword(mdp);
            }
            person.setTel(tel);
            savePerson(person);
            return new Message("OK", "Modifications enregistré.");
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Impossible de changer le profil.");
        }
    }

    @PostMapping(value = "/login/suppprofil", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message deleteProfil(@RequestParam("mdp") String mdp,
        @RequestParam("token") String token) {
        try {
            List<Person> personByToken = getPersonByToken(token);
            if (personByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide.");
            }
            List<Person> people = checkPasswordByTokenPerson(token, Hashing.sha256().hashString(mdp, StandardCharsets.UTF_8).toString());
            if (people.isEmpty()) {
                return new Message("FAIL", "Mot de passe incorrecte.");
            }
            removeToken(new Token(people.get(0).getId(), token, "0"));
            removePerson(people.get(0));
            return new Message("OK", "Compte supprimé.");
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Impossible de supprimer le profil");
        }
    }

    @PostMapping(value = "/login/getprofiladmin", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Admin getProfilAdmin(@RequestParam("token") String token) {
        try {
            List<Admin> adminByToken = getAdminByToken(token);
            if (!adminByToken.isEmpty()) {
                Admin admin = adminByToken.get(0);
                admin.setPassword("");
                return admin;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(value = "/login/changeprofiladmin", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message changeProfilAdmin(@RequestParam("num") String tel,
        @RequestParam("mdp") String mdp,
        @RequestParam("token") String token) {
        try {
            List<Admin> adminByToken = getAdminByToken(token);
            if (adminByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide.");
            }
            if (tel.isEmpty() || !tel.matches("[0][76][0-9]{8}$")) {
                return new Message("FAIL", "Numéro de téléphone invalide.");
            }
            if ((mdp.length() >= 1) && (mdp.length() < 6)) {
                return new Message("FAIL", "Mot de passe trop court.");
            }

            Admin admin = adminByToken.get(0);
            if (mdp.length() >= 6) {
                admin.setPassword(mdp);
            }
            admin.setTel(tel);
            saveAdmin(admin);
            return new Message("OK", "Modifications enregistré.");
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Impossible de changer le profil.");
        }
    }

    @PostMapping(value = "/login/suppprofiladmin", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message deleteProfilAdmin(@RequestParam("mdp") String mdp,
        @RequestParam("token") String token) {
        try {

            List<Admin> adminByToken = getAdminByToken(token);
            if (adminByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide.");
            }
            List<Admin> admins = checkPasswordByTokenAdmin(token, Hashing.sha256().hashString(mdp, StandardCharsets.UTF_8).toString());
            if (admins.isEmpty()) {
                return new Message("FAIL", "Mot de passe incorrecte.");
            }
            removeToken(new Token(admins.get(0).getId(), token, "1"));
            removeAdmin(admins.get(0));
            return new Message("OK", "Compte supprimé.");
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Impossible de supprimer le profil.");
        }
    }

    static boolean validateEmailStandard(String email) {
        return EmailValidator.getInstance(true).isValid(email);
    }
}
