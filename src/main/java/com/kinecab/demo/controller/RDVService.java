
package com.kinecab.demo.controller;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import static com.kinecab.demo.db.AdminDB.getAdminByToken;
import static com.kinecab.demo.db.AdminDB.getPersonByIdAdmin;

import com.kinecab.demo.db.CabDB;
import com.kinecab.demo.db.PatientDB;
import com.kinecab.demo.db.RDVDB;
import com.kinecab.demo.db.entity.*;
import com.kinecab.demo.json.*;

import org.json.JSONArray;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller
public class RDVService {
    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @PostMapping(value = "/rdv/bookrdv", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message bookRDV(@RequestParam("events") String events,
                           @RequestParam("tokenAdmin") String tokenAdmin) {
        try {
            List<Admin> adminByToken = getAdminByToken(tokenAdmin);
            if (adminByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide");
            }
            final List<Event> rdvs = RDVDB.rdvJsonToRdvs(adminByToken.get(0).getId(), new JSONArray(events));
            RDVDB.saveRDV(rdvs);
            final Set<Integer> collect = rdvs.stream().map(Event::getId).collect(Collectors.toSet());
            return new BookRdv("OK", "RAS", collect);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("OK", "Erreur pendant la création des rendez-vous.");
        }
    }

    @PostMapping(value = "/rdv/getrdv", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message getRDV(@RequestParam("start") String start,
                          @RequestParam("end") String end,
                          @RequestParam("tokenAdmin") String tokenAdmin) {
        try {
            List<Admin> adminByToken = getAdminByToken(tokenAdmin);
            if (adminByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide");
            }
            final List<Event> rdvs = RDVDB.getRdvByTime(start, end, adminByToken.get(0).getId());
            return new GetRDV("OK", "RAS", rdvs);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("OK", "Erreur pendant le chargement des rendez-vous.");
        }
    }

    @PostMapping(value = "/rdv/getrdvfree", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message getRDVFree(
            @RequestParam("idAdmin") String idAdmin) {
        try {
            String start = getStart();
            String end = getEnd();
            final List<Event> rdvs = RDVDB.getRdvFreeByTime(start, end, Integer.parseInt(idAdmin.replace("#", "")));
            return new GetRDV("OK", "RAS", rdvs);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("OK", "Erreur pendant le chargement des rendez-vous.");
        }
    }

    private String getEnd() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        return FORMAT.format(cal.getTime());
    }

    private String getStart() {
        return FORMAT.format(new Date());
    }

    @PostMapping(value = "/rdv/removerdv", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message removeRDV(@RequestParam("idEvents") String idEvents,
                             @RequestParam("tokenAdmin") String tokenAdmin) {
        try {
            List<Admin> adminByToken = getAdminByToken(tokenAdmin);
            if (adminByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide");
            }
            RDVDB.removeRdvByIds(new JSONArray(idEvents), adminByToken.get(0).getId());
            return new Message("OK", "Evenement supprimé");
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur interne serveur ");

        }
    }

    @PostMapping(value = "/rdv/getmotif", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message getMotif(@RequestParam("tokenAdmin") String tokenAdmin) {
        try {
            List<Admin> adminByToken = getAdminByToken(tokenAdmin);
            if (adminByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide");
            }
            final List<MotifCab> motifByIdAdmin = RDVDB.getMotifByIdAdmin(adminByToken.get(0).getId());
            return new GetMotif("OK", "RAS", motifByIdAdmin);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant le chargement des Motifs.");
        }
    }

    @PostMapping(value = "/rdv/getmotifid", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message getMotifId(@RequestParam("idAdmin") String idAdmin) {
        try {
            final List<MotifCab> motifByIdAdmin = RDVDB.getMotifByIdAdmin(Integer.parseInt(idAdmin.replace("#", "")));
            return new GetMotif("OK", "RAS", motifByIdAdmin);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant le chargement des Motifs.");
        }
    }

    @PostMapping(value = "/rdv/getpersonidadmin", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message getPersonIdAdmin(@RequestParam("tokenAdmin") String tokenAdmin) {
        try {
            List<Admin> adminByToken = getAdminByToken(tokenAdmin);
            if (adminByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide");
            }
            final List<Person> people = getPersonByIdAdmin(adminByToken.get(0).getId());
            return new GetPerson("OK", "RAS", people);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("OK", "Erreur pendant le chargement des patients.");
        }
    }

    @PostMapping(value = "/rdv/bookrdvfrompat", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message bookRDVFromPat(@RequestParam("idEvent") String idEvent,
                                  @RequestParam("tokenPat") String tokenPat,
                                  @RequestParam("idMotif") String idMotif) {
        try {
            Person person = PatientDB.getPatientByToken(tokenPat);
            List<Event> rdvFreeById = RDVDB.getRdvFreeById(idEvent);
            if (rdvFreeById.isEmpty()) {
                return new Message("FAIL", "Ce rendez-vous n'est plus dispoblible.");
            }
            Event curentEvent = rdvFreeById.get(0);
            if(!idMotifIsPresentInEvent(idMotif, curentEvent)){
                return new Message("FAIL", "Ce rendez-vous n'est plus dispoblible.");
            }
            curentEvent.setIdMotif(idMotif);
            curentEvent.setStatus(Status.WAITING);
            curentEvent.setIdPatient(person.getId());
            curentEvent.setNomPrenom(person.getNom()+" "+person.getPrenom());
            RDVDB.saveRDV(Collections.singletonList(curentEvent));
            CabDB.addCabPersonIfNotPresent(person.getId(),curentEvent.getIdAdmin());
            //TODO controle now + 1 month
            return new Message("OK", "RAS");
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("OK", "Erreur pendant la création des rendez-vous.");
        }
    }

    private boolean idMotifIsPresentInEvent(String idMotif, Event curentEvent) {
        String[] split = curentEvent.getListIdMotif().split(",");
        for (String id : split) {
            if(id.contentEquals(idMotif)){
                return true;
            }
        }
        return false;
    }

}
