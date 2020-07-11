
package com.kinecab.demo.controller;

import java.util.List;

import static com.kinecab.demo.db.AdminDB.*;
import static com.kinecab.demo.db.CabDB.*;
import static com.kinecab.demo.db.LoginDB.getPersonByEmail;
import static com.kinecab.demo.db.LoginDB.savePerson;
import static com.kinecab.demo.db.PatientDB.emailExist;

import com.google.common.base.Strings;
import com.kinecab.demo.db.LoginDB;
import com.kinecab.demo.db.entity.*;
import com.kinecab.demo.json.GetColab;
import com.kinecab.demo.json.GetPerson;
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
            if (!tel.matches("[0][12345679][0-9]{8}$") || tel.isEmpty()) {
                return new Message("FAIL", "Le numéro du patient est invalide");
            } else {
                try {
                    Person person;
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

    @PostMapping(value = "/admin/getpersonidadminidperson", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message getPersonIdAdminByIdPerson(@RequestParam("tokenAdmin") String tokenAdmin,
                                              @RequestParam("idPerson") String idPerson) {
        try {
            List<Colab> colabByToken = getColabByToken(tokenAdmin);
            if (colabByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide");
            }
            Person person = getPersonByIdCabIdPerson(colabByToken.get(0).getIdCab(), idPerson);
            if(person != null){
                return new GetPerson("OK", "RAS", person);
            }else {
                return new Message("FAIL", "Erreur pendant le chargement du patient.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant le chargement du patient.");
        }
    }

    @PostMapping(value = "/admin/updatepatient", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message updatePatient(@RequestParam("num") String num,
                                @RequestParam("mail") String mail,
                                @RequestParam("nom") String nom,
                                @RequestParam("prenom") String prenom,
                                @RequestParam("id") String id,
                                @RequestParam("token") String token) {
        try {
            List<Colab> colabByToken = getColabByToken(token);
            if (colabByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide");
            }
            Person person = getPersonByIdCabIdPerson(colabByToken.get(0).getIdCab(), id);
            if (num.isEmpty() || !num.matches("[0][12345679][0-9]{8}$")) {
                return new Message("FAIL", "Numéro de téléphone invalide.");
            }
            if (!getPersonByEmail(mail).isEmpty()) {
                return new Message("FAIL", "Mail déjà utilisé");
            }
            if(person.getPassword().contentEquals("e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")){
                person.setEmail(mail);
                person.setNom(nom);
                person.setPrenom(prenom);
                person.setTel(num);
                savePerson(person);
                return new Message("OK", "Modifications enregistrées.");
            }else {
                return new Message("FAIL", "Vosu ne pouvez pas modifier le profil d'un patient inscrit.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Impossible de changer le profil.");
        }
    }

}
