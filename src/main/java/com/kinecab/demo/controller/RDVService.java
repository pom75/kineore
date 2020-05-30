
package com.kinecab.demo.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.kinecab.demo.db.CabDB;
import com.kinecab.demo.db.PatientDB;
import com.kinecab.demo.db.RDVDB;
import com.kinecab.demo.db.entity.*;
import com.kinecab.demo.json.*;

import com.kinecab.demo.util.EmailException;
import org.json.JSONArray;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import static com.kinecab.demo.db.AdminDB.*;
import static com.kinecab.demo.db.PatientDB.getPatientById;
import static com.kinecab.demo.util.MailUtil.*;


@Controller
public class RDVService {
    public static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @PostMapping(value = "/rdv/addevent", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message addEvent(@RequestParam("events") String events,
                            @RequestParam("tokenAdmin") String tokenAdmin) {
        try {
            List<Colab> colabByToken = getColabByToken(tokenAdmin);
            if (colabByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide");
            }
            final List<Event> rdvs = RDVDB.rdvJsonToRdvs(colabByToken.get(0).getId(), new JSONArray(events));
            RDVDB.saveRDVs(rdvs);
            final Set<Integer> collect = rdvs.stream().map(Event::getId).collect(Collectors.toSet());
            return new BookRdv("OK", "RAS", collect);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant la création des rendez-vous.");
        }
    }

    @PostMapping(value = "/rdv/changeoneevent", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message changeOneEvent(@RequestParam("events") String events,
                                  @RequestParam("tokenAdmin") String tokenAdmin) {
        try {
            List<Colab> colabByToken = getColabByToken(tokenAdmin);
            if (colabByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide");
            }
            Event rdv = RDVDB.rdvJsonToRdvs(colabByToken.get(0).getId(), new JSONArray(events)).get(0);
            Event rdvbyId = RDVDB.getRdvbyId(rdv.getId());
            if (rdvbyId.getIdAdmin() == rdv.getIdAdmin()) {
                RDVDB.saveRDV(rdv);
                return new Message("OK", "RAS");
            } else {
                return new Message("FAIL", "Erreur pendant la création des rendez-vous.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant la création des rendez-vous.");
        }
    }

    @PostMapping(value = "/rdv/changestatusevent", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message changeStatusRDV(@RequestParam("events") String events,
                                   @RequestParam("tokenAdmin") String tokenAdmin,
                                   @RequestParam String status,
                                   @RequestParam String idPat) {
        try {
            List<Colab> ColabByToken = getColabByToken(tokenAdmin);
            if (ColabByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide");
            }
            Event rdv = RDVDB.rdvJsonToRdvs(ColabByToken.get(0).getId(), new JSONArray(events)).get(0);
            Event rdvbyId = RDVDB.getRdvbyId(rdv.getId());
            if (rdvbyId.getIdAdmin() == rdv.getIdAdmin()) {
                RDVDB.saveRDV(rdv);
                try {
                    switch (status) {//Todo crete thread for mails
                        case "BOOKED"://TODO ADD from Admin warninig fake email
                            break;
                        case "ACCEPTE":
                            sendEmail(getPatientById(idPat).getEmail(), ACCEPTE_TITLE, ACCEPTE_CONTENT.replace("xxx", new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(rdv.getStart())));
                            break;
                        case "REFUSE":
                            sendEmail(getPatientById(idPat).getEmail(), REFUSE_TITLE, REFUSE_CONTENT.replace("xxx", new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(rdv.getStart())));
                            break;
                        case "CANCEL":
                            sendEmail(getPatientById(idPat).getEmail(), CANCELED_TITLE, CANCELED_CONTENT.replace("xxx", new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(rdv.getStart())));
                            break;
                    }
                }catch (EmailException exception){
                    //TODO HACK temp patient no email
                    exception.printStackTrace();
                    return new Message("OK", "RAS");
                }
                return new Message("OK", "RAS");
            } else {
                return new Message("FAIL", "Erreur pendant la création des rendez-vous.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant la création des rendez-vous.");
        }
    }

    @PostMapping(value = "/rdv/getrdv", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message getRDV(@RequestParam("start") String start,
                          @RequestParam("end") String end,
                          @RequestParam("tokenAdmin") String tokenAdmin) {
        try {
            List<Colab> colabByToken = getColabByToken(tokenAdmin);
            if (colabByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide");
            }
            final List<Event> rdvs = RDVDB.getRdvByTime(start, end, colabByToken.get(0).getId());
            return new GetRDV("OK", "RAS", rdvs);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant le chargement des rendez-vous.");
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
            return new Message("FAIL", "Erreur pendant le chargement des rendez-vous.");
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
            List<Colab> colabByToken = getColabByToken(tokenAdmin);
            if (colabByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide");
            }
            RDVDB.removeRdvByIds(new JSONArray(idEvents), colabByToken.get(0).getId());
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
            List<Colab> colabByToken = getColabByToken(tokenAdmin);
            if (colabByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide");
            }
            final List<MotifCab> motifByIdAdmin = RDVDB.getMotifByIdColab(colabByToken.get(0).getId());
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
            final List<MotifCab> motifByIdAdmin = RDVDB.getMotifByIdColab(Integer.parseInt(idAdmin.replace("#", "")));
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
            List<Colab> colabByToken = getColabByToken(tokenAdmin);
            if (colabByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide");
            }
            final List<Person> people = getPersonByIdCab(colabByToken.get(0).getIdCab());
            return new GetPerson("OK", "RAS", people);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant le chargement des patients.");
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
            if (!idMotifIsPresentInEvent(idMotif, curentEvent)) {
                return new Message("FAIL", "Ce rendez-vous n'est plus dispoblible.");
            }
            curentEvent.setIdMotif(idMotif);
            curentEvent.setStatus(Status.WAITING);
            curentEvent.setIdPatient(person.getId());
            curentEvent.setNomPrenom(person.getNom() + " " + person.getPrenom());
            CabDB.addCabPersonIfNotPresent(person.getId(), curentEvent.getIdAdmin());
            RDVDB.saveRDVs(Collections.singletonList(curentEvent));
            sendEmail(person.getEmail(), TOOK_TITLE, TOOK_CONTENT.replace("xxx", new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(curentEvent.getStart())));
            //TODO controle now + 1 month
            return new Message("OK", "RAS");
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant la création des rendez-vous.");
        }
    }

    @PostMapping(value = "/rdv/mesrdv", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message getMesRDV(
            @RequestParam("tokenPat") String tokenPat) {
        try {
            Person person = PatientDB.getPatientByToken(tokenPat);
            final List<Event> rdvs = RDVDB.getRdvbyIdClient(person.getId());
            return new GetRDV("OK", "RAS", rdvs);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant le chargement des rendez-vous.");
        }
    }

    @PostMapping(value = "/rdv/cancelRDV", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message cancelRDV(
            @RequestParam("tokenPat") String tokenPat, @RequestParam("idEvent") String idEvent) {
        try {
            Person person = PatientDB.getPatientByToken(tokenPat);
            Event rdv = RDVDB.getRdvbyId(Integer.parseInt(idEvent));
            if (checkRdv(person, rdv)) {
                rdv.setStatus(Status.CANCELED);
                RDVDB.saveRDVs(Collections.singletonList(rdv));
                RDVDB.saveRDVs(Collections.singletonList(new Event(rdv)));
                sendEmail(person.getEmail(), CANCEL_TITLE, CANCEL_CONTENT.replace("xxx", new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(rdv.getStart())));
                return new Message("OK", "RAS");
            } else {
                return new Message("FAIL", "Impossible d'annuler le rendez-vous. Veuillez contacter votre praticien");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant le chargement des rendez-vous.");
        }
    }

    private boolean checkRdv(Person person, Event rdv) {
        return rdv.getIdPatient() == person.getId() && rdv.getStatus() != Status.FREE && rdv.getStatus() != Status.CANCELED &&
                !rdv.getStart().before(Timestamp.valueOf(LocalDateTime.now().plusDays(1)));
    }


    private boolean idMotifIsPresentInEvent(String idMotif, Event curentEvent) {
        String[] split = curentEvent.getListIdMotif().split(",");
        for (String id : split) {
            if (id.contentEquals(idMotif)) {
                return true;
            }
        }
        return false;
    }

}
