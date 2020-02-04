/**
 *  Copyright Murex S.A.S., 2003-2020. All Rights Reserved.
 *
 *  This software program is proprietary and confidential to Murex S.A.S and its affiliates ("Murex") and, without limiting the generality of the foregoing reservation of rights, shall not be accessed, used, reproduced or distributed without the
 *  express prior written consent of Murex and subject to the applicable Murex licensing terms. Any modification or removal of this copyright notice is expressly prohibited.
 */
package com.kinecab.demo.controller;

import java.util.List;

import static com.kinecab.demo.db.CabDB.getCabByAdminID;
import static com.kinecab.demo.db.CabDB.saveCabPerson;
import com.kinecab.demo.db.LoginDB;
import static com.kinecab.demo.db.LoginDB.getAdminByToken;
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


@Controller
public class AdminService {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @RequestMapping(value = "/admin/addpatient", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message inscription(@RequestParam("nom") String nom,
        @RequestParam("prenom") String prenom,
        @RequestParam("tel") String tel,
        @RequestParam("tokenAdmin") String tokenAdmin) {
        try {
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
}
