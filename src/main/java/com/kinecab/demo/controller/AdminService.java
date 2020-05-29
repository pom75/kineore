
package com.kinecab.demo.controller;

import java.util.List;

import static com.kinecab.demo.controller.LoginService.validateEmailStandard;
import static com.kinecab.demo.db.AdminDB.*;
import static com.kinecab.demo.db.CabDB.*;
import com.kinecab.demo.db.LoginDB;
import com.kinecab.demo.db.entity.*;
import com.kinecab.demo.json.GetAdmin;
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
        @RequestParam("tokenAdmin") String tokenAdmin) {
        try {
            List<Admin> adminByToken = getAdminByToken(tokenAdmin);
            if (adminByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide");
            }
            if (nom.isEmpty() || prenom.isEmpty() || !tel.matches("[0][7691][0-9]{8}$")) {
                return new Message("FAIL", "Un des champ est invalide");
            } else {
                try {
                    Person person = new Person(nom, prenom, nom + prenom + tel, tel);
                    LoginDB.savePerson(person);
                    CabPerson cabPerson = new CabPerson(getCabByAdminID(String.valueOf(adminByToken.get(0).getId())).get(0).getId(), person.getId());
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

    @PostMapping(value = "/admin/addadmin", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message ajoutAdmin(@RequestParam("nom") String nom,
        @RequestParam("prenom") String prenom,
        @RequestParam("email") String email,
        @RequestParam("tokenAdmin") String tokenAdmin) {
        try {
            List<Admin> adminByToken = getAdminByToken(tokenAdmin);
            if (adminByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide");
            }
            if (nom.isEmpty() || prenom.isEmpty() || email.isEmpty() || !validateEmailStandard(email)) {
                return new Message("FAIL", "Un des champ est invalide");
            } else {
                Admin admin = new Admin(nom, prenom, email);
                saveAdmin(admin);
                final List<Cab> cabByAdminID = getCabByAdminID(String.valueOf(adminByToken.get(0).getId()));
                CabAdmin cabAdmin = new CabAdmin(String.valueOf(cabByAdminID.get(0).getId()), String.valueOf(admin.getId()));
                saveCabAdmin(cabAdmin);
                return new Message("OK", "Nouveau admin ajouté.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Impossible d'ajouter un admin.");
        }
    }

    @PostMapping(value = "/admin/getadmin", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message getPersonIdAdmin() {
        try {
            List<Admin> admins= getAdmins();
            admins.forEach(admin -> {admin.setPassword("");admin.setEmail("");});
            GetAdmin getAdmin = new GetAdmin("OK", "RAS", admins);
            getCab().forEach(cabAdmin -> getAdmin.addMapIdAdminIdCab(cabAdmin.getIdAdmin(),cabAdmin.getIdCab()));
            return getAdmin;
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant le chargement des admins.");
        }
    }

}
