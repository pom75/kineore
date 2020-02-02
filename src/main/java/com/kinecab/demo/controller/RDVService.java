
package com.kinecab.demo.controller;

import com.kinecab.demo.db.RDVDB;
import com.kinecab.demo.db.entity.*;
import com.kinecab.demo.json.*;
import org.json.JSONArray;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.kinecab.demo.db.LoginDB.getAdminByToken;


@Controller
public class RDVService {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @RequestMapping(value = "/rdv/bookrdv", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Message bookRDV(
        @RequestParam("events") String events,
        @RequestParam("tokenAdmin") String tokenAdmin) {
        try {
            List<Admin> adminByToken = getAdminByToken(tokenAdmin);
            if (adminByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide");
            }
            final List<Event> rdvs = RDVDB.rdvJsonToRdvs(adminByToken.get(0).getId(), new JSONArray(events));
            RDVDB.saveRDV(rdvs);
            final Set<Integer> collect = rdvs.stream().map(Event::getId).collect(Collectors.toSet());
            return new BookRdv("OK", "RAS",collect);
        }catch (Exception e){
            return new Message("OK", "Erreur pendant la création des rendez-vous."+e);
        }
    }

    @RequestMapping(value = "/rdv/getrdv", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Message getRDV(
            @RequestParam("start") String start,
            @RequestParam("end") String end,
            @RequestParam("tokenAdmin") String tokenAdmin) {
        try {
            List<Admin> adminByToken = getAdminByToken(tokenAdmin);
            if (adminByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide");
            }
            final List<Event> rdvs = RDVDB.getRdvByTime(start,end,adminByToken.get(0).getId());
            return new GetRDV("OK","RAS",rdvs);
        }catch (Exception e){
            return new Message("OK", "Erreur pendant le chargement des rendez-vous."+e);
        }
    }

    @RequestMapping(value = "/rdv/removerdv", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Message removeRDV(
            @RequestParam("idEvents") String idEvents,
            @RequestParam("tokenAdmin") String tokenAdmin) {
        try {
            List<Admin> adminByToken = getAdminByToken(tokenAdmin);
            if (adminByToken.isEmpty()) {
                return  new Message("FAIL", "Token invalide");
            }
            RDVDB.removeRdvByIds(new JSONArray(idEvents),adminByToken.get(0).getId());
            return new Message("OK", "Evenement supprimé");
        }catch (Exception e){
            return  new Message("FAIL", "Erreur interne serveur "+e);

        }
    }

    @RequestMapping(value = "/rdv/getmotif", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Message getMotif(
            @RequestParam("tokenAdmin") String tokenAdmin) {
        try {
            List<Admin> adminByToken = getAdminByToken(tokenAdmin);
            if (adminByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide");
            }
            final List<Motif> motifByIdAdmin = RDVDB.getMotifByIdAdmin(adminByToken.get(0).getId());
            return new GetMotif("OK", "RAS",motifByIdAdmin);
        }catch (Exception e){
            System.out.println(e);
            return new Message("FAIL", "Erreur pendant le chargement des Motifs."+e);
        }
    }

    @RequestMapping(value = "/rdv/getpersonidadmin", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Message getPersonIdAdmin(
            @RequestParam("tokenAdmin") String tokenAdmin) {
        try {
            List<Admin> adminByToken = getAdminByToken(tokenAdmin);
            if (adminByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide");
            }
            final List<Person> people = RDVDB.getPersonByIdAdmin(adminByToken.get(0).getId());
            return new GetPerson("OK", "RAS",people);
        }catch (Exception e){
            System.out.println(e);
            return new Message("OK", "Erreur pendant le chargement des patients."+e);
        }
    }
}
