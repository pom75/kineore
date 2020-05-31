
package com.kinecab.demo.controller;

import java.util.List;

import static com.kinecab.demo.db.AdminDB.*;
import static com.kinecab.demo.db.CabDB.*;
import static com.kinecab.demo.db.PatientDB.emailExist;

import com.google.common.base.Strings;
import com.kinecab.demo.db.LoginDB;
import com.kinecab.demo.db.entity.*;
import com.kinecab.demo.json.GetColab;
import com.kinecab.demo.json.Message;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class AdminService {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @PostMapping(value = "/admin/addpatient", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message ajoutPatient(@RequestParam("nom") String nom,
                                @RequestParam("prenom") String prenom,
                                @RequestParam("tel") String tel,
                                @RequestParam(value = "mail", required = false) String mail,
                                @RequestParam("tokenAdmin") String tokenAdmin) {
        try {
            List<Colab> colabByToken = getColabByToken(tokenAdmin);
            if (colabByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide");
            }
            if (nom.isEmpty()) {
                return new Message("FAIL", "Le nom du patient est vide.");
            }
            if (prenom.isEmpty()) {
                return new Message("FAIL", "Le prenom du patient est vide");
            }
            if (!tel.matches("[0][12345679][0-9]{8}$")) {
                return new Message("FAIL", "L'email du patient est invalide");
            } else {
                try {
                    Person person;
                    System.out.println(mail);
                    if (!Strings.isNullOrEmpty(mail) && !emailExist(mail)) {
                        person = new Person(nom, prenom, mail, tel);
                    } else {
                        person = new Person(nom, prenom, nom + prenom + tel, tel);
                    }
                    LoginDB.savePerson(person);
                    CabPerson cabPerson = new CabPerson(getCabByColabID(String.valueOf(colabByToken.get(0).getId())).get(0).getId(), person.getId());
                    saveCabPerson(cabPerson);
                } catch (Exception e) {
                    return new Message("FAIL", "Mail déjà utilisé " + e);
                }
                return new Message("OK", "Nouveau patient ajouté.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Impossible d'ajouter un patient.");
        }
    }


    @PostMapping(value = "/admin/getadmin", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message getPersonIdAdmin() {
        try {
            List<Colab> colabs = getColabs();
            return new GetColab("OK", "RAS", colabs);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant le chargement des admins.");
        }
    }

}
