
package com.kinecab.demo.controller;

import java.util.LinkedList;
import java.util.List;

import static com.kinecab.demo.db.KineUserDB.*;
import static com.kinecab.demo.db.CabDB.*;
import static com.kinecab.demo.db.LoginDB.*;
import static com.kinecab.demo.db.PatientDB.emailExist;
import static com.kinecab.demo.util.MailUtil.*;

import com.google.common.base.Strings;
import com.kinecab.demo.db.LoginDB;
import com.kinecab.demo.db.entity.*;
import com.kinecab.demo.json.*;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class KineUserService {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @PostMapping(value = "/kineuser/addpatient", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message ajoutPatient(@RequestParam("nom") String nom,
                                @RequestParam("prenom") String prenom,
                                @RequestParam("tel") String tel,
                                @RequestParam(value = "mail", required = false) String mail,
                                @RequestParam("tokenKineUser") String tokenKineUser) {
        try {
            Colab colabByToken = getColabByToken(tokenKineUser);
            if (colabByToken == null) {
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
                    CabPerson cabPerson = new CabPerson(getCabByColabID(String.valueOf(colabByToken.getId())).getId(), person.getId());
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


    @PostMapping(value = "/kineuser/getcolab", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message getAllColabs() {
        try {
            List<Colab> colabs = getColabs();
            return new GetColab("OK", "RAS", colabs);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant le chargement.");
        }
    }

    @PostMapping(value = "/kineuser/getpersonbyid", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message getPersonById(@RequestParam("tokenKineUser") String tokenKineUser,
                                              @RequestParam("idPerson") String idPerson) {
        try {
            Colab colabByToken = getColabByToken(tokenKineUser);
            if (colabByToken == null) {
                return new Message("FAIL", "Token invalide");
            }
            Person person = getPersonByIdCabIdPerson(colabByToken.getIdCab(), idPerson);
            if (person != null) {
                if (person.getPassword() == null) {
                    person.setCryptedPassword("null");
                }else {
                    person.setCryptedPassword("not null");
                }
                    return new GetPerson("OK", "RAS", person);
            } else {
                return new Message("FAIL", "Erreur pendant le chargement du patient.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant le chargement du patient.");
        }
    }

    @PostMapping(value = "/kineuser/updatepatient", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message updatePatient(@RequestParam("num") String num,
                                 @RequestParam("mail") String mail,
                                 @RequestParam("nom") String nom,
                                 @RequestParam("prenom") String prenom,
                                 @RequestParam("id") String id,
                                 @RequestParam("token") String token) {
        try {
            Colab colabByToken = getColabByToken(token);
            if (colabByToken == null) {
                return new Message("FAIL", "Token invalide");
            }
            Person person = getPersonByIdCabIdPerson(colabByToken.getIdCab(), id);
            if (person == null) {
                return new Message("FAIL", "Aucun Patient trouvé.");
            }
            if (num.isEmpty() || !num.matches("[0][12345679][0-9]{8}$")) {
                return new Message("FAIL", "Numéro de téléphone invalide.");
            }
            if (!mail.contentEquals(person.getEmail()) && getPersonByEmail(mail) != null) {
                return new Message("FAIL", "Mail déjà utilisé");
            }
            //TODO update nom prenom RDVs
            if (person.getPassword()== null) {
                person.setEmail(mail);
                person.setNom(nom);
                person.setPrenom(prenom);
                person.setTel(num);
                savePerson(person);
                return new Message("OK", "Modifications enregistrées.");
            } else {
                return new Message("FAIL", "Vous ne pouvez pas modifier le profil d'un patient inscrit.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Impossible de changer le profil.");
        }
    }

    @PostMapping(value = "/kineuser/sendmailinscription", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message sendEmailPatient(@RequestParam("id") String id,
                                    @RequestParam("token") String token) {
        try {
            Colab colabByToken = getColabByToken(token);
            if (colabByToken == null) {
                return new Message("FAIL", "Token invalide");
            }
            Person person = getPersonByIdCabIdPerson(colabByToken.getIdCab(), id);
            if (person == null) {
                return new Message("FAIL", "Aucun Patient trouvé.");
            }
            if (person.getPassword() == null) {
                String newPassword = newPasswordPerson(person);
                sendEmail(person.getEmail(), SEND_PERSON_TITLE, SEND_PERSON_CONTENT.replace("xxx", newPassword));
                return new Message("OK", "Email d'inscription envoyé à "+person.getEmail()+".");
            } else {
                return new Message("FAIL", "Vous ne pouvez pas envoyer un mail d'inscription a un patient déja inscrit.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Impossible de changer le profil.");
        }
    }

    @PostMapping(value = "/kineuser/getallcabcolabs", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message getallcabcolabs( @RequestParam("token") String token) {
        try {
            List<KineUser> allKineUsersCab = getAllCabKineUserByToken(token);
            if (!allKineUsersCab.isEmpty()) {
                List<ColabInfo> colabInfos = new LinkedList<>();
                allKineUsersCab.forEach(e -> {
                    Colab colab = getColabByIdKineUser(String.valueOf(e.getId()));
                    colabInfos.add(new ColabInfo(colab.getId(),colab.getName()));
                });
                return new GetKineUsers("OK","RAS",colabInfos);
            } else {
                return new Message("FAIL", "Erreur de Token.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Impossible de changer le profil.");
        }
    }


}
