
package com.kinecab.demo.controller;


import com.kinecab.demo.db.LoginDB;
import com.kinecab.demo.db.entity.Admin;
import com.kinecab.demo.db.entity.CabPerson;
import com.kinecab.demo.db.entity.Person;
import com.kinecab.demo.json.Message;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

import static com.kinecab.demo.db.CabDB.getCabByAdminID;
import static com.kinecab.demo.db.CabDB.saveCabPerson;
import static com.kinecab.demo.db.LoginDB.getAdminByToken;


@Controller
public class AdminService {

    @RequestMapping(value = "/admin/addpatient", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Message inscription(@RequestParam("nom") String nom,
                                             @RequestParam("prenom") String prenom,
                                             @RequestParam("tel") String tel,
                                             @RequestParam("tokenAdmin") String tokenAdmin) {
        List<Admin> adminByToken = getAdminByToken(tokenAdmin);
        if (adminByToken.isEmpty()) {
            return new Message("FAIL", "Token invalide");
        }
        if (nom.isEmpty() || prenom.isEmpty() || !tel.matches("[0][76][0-9]{8}$")) {
            return new Message("FAIL", "Un des champ est invalide");
        } else {
            try {
                Person person = new Person(nom, prenom, nom + prenom + tel, tel);
                LoginDB.savePerson(person);
                CabPerson cabPerson = new CabPerson(getCabByAdminID(String.valueOf(adminByToken.get(0).getId())).get(0).getId(),person.getId());
                saveCabPerson(cabPerson);
            } catch (Exception e) {
                return new Message("FAIL", "Mail déjà utilisé "+e);
            }
            return new Message("OK", "Nouveau patient ajouté.");

        }
    }
}
