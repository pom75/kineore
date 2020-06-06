
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
    public static final SimpleDateFormat FORMAT_RDV = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final SimpleDateFormat FORMAT_MAIL = new SimpleDateFormat("EEEE dd MMMM yyyy à HH:mm", Locale.FRANCE);

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


    @PostMapping(value = "/rdv/safebookoneevent", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message safeBookOneEvent(@RequestParam("events") String events,
                                    @RequestParam("tokenAdmin") String tokenAdmin) {
        try {
            List<Colab> colabByToken = getColabByToken(tokenAdmin);
            if (colabByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide");
            }
            Event rdv = RDVDB.rdvJsonToRdvs(colabByToken.get(0).getId(), new JSONArray(events)).get(0);
            Event rdvbyId = RDVDB.getRdvbyId(rdv.getId());
            if (rdvbyId.getIdAdmin() == rdv.getIdAdmin()) {
                if (RDVDB.safeUpdateRDV(rdv, Status.FREE)) {
                    return new Message("OK", "RAS");
                } else {
                    return new Message("FAIL", "Erreur, le rendez-vous que vous voulez modifier vient d'etre pris par un patient.");
                }
            } else {
                return new Message("FAIL", "Erreur pendant la création des rendez-vous.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant la création des rendez-vous.");
        }
    }

    @PostMapping(value = "/rdv/moveevent", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message moveEvent(@RequestParam("events") String events,
                             @RequestParam("tokenAdmin") String tokenAdmin) {
        try {
            List<Colab> colabByToken = getColabByToken(tokenAdmin);
            if (colabByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide");
            }
            Event rdv = RDVDB.rdvJsonToRdvs(colabByToken.get(0).getId(), new JSONArray(events)).get(0);
            Event rdvbyId = RDVDB.getRdvbyId(rdv.getId());
            if (rdvbyId.getIdAdmin() == rdv.getIdAdmin()) {
                if (RDVDB.safeUpdateRDV(rdv, rdv.getStatus())) {
                    return new Message("OK", "RAS");
                } else {
                    return new Message("FAIL", "Erreur, le rendez-vous que vous voulez modifier vient d'etre modifier par le patient.");
                }
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
                boolean sucess;
                if (status.equalsIgnoreCase("CANCEL")) {
                    sucess = RDVDB.safeUpdateRDV(rdv, Status.BOOKED);
                } else {
                    sucess = RDVDB.safeUpdateRDV(rdv, Status.WAITING);
                }
                if (!sucess) {
                    return new Message("FAIL", "Le rendez-vous que vous voulez modifier vient d'etre mis à jour par le patient.");
                }
                switch (status) {//Todo crete thread for mails
                    case "BOOKED":
                        break;
                    case "ACCEPTE":
                        sendEmail(getPatientById(idPat).getEmail(), ACCEPTE_TITLE, ACCEPTE_CONTENT.replace("xxx", FORMAT_MAIL.format(rdv.getStart())));
                        break;
                    case "REFUSE":
                        sendEmail(getPatientById(idPat).getEmail(), REFUSE_TITLE, REFUSE_CONTENT.replace("xxx", FORMAT_MAIL.format(rdv.getStart())));
                        break;
                    case "CANCEL":
                        sendEmail(getPatientById(idPat).getEmail(), CANCELED_TITLE, CANCELED_CONTENT.replace("xxx", FORMAT_MAIL.format(rdv.getStart())));
                        break;
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
        return FORMAT_RDV.format(cal.getTime());
    }

    private String getStart() {
        return FORMAT_RDV.format(new Date());
    }

    @PostMapping(value = "/rdv/removerdv", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message removeRDV(@RequestParam("listEvents") String listEvents,
                             @RequestParam("tokenAdmin") String tokenAdmin) {
        try {
            List<Colab> colabByToken = getColabByToken(tokenAdmin);
            if (colabByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide");
            }
            final List<Event> rdvs = RDVDB.rdvJsonToRdvs(colabByToken.get(0).getId(), new JSONArray(listEvents));
            boolean success = RDVDB.removeRdvByEvent(rdvs, colabByToken.get(0).getId());
            if (success) {
                //sendEmail(getPatientById(idPat).getEmail(), ACCEPTE_TITLE, ACCEPTE_CONTENT.replace("xxx", FORMAT_MAIL.format(rdv.getStart())));
                return new Message("OK", "Evenement supprimé");
            } else {
                return new Message("FAIL", "Erreur dans la suppression du rendez-vous. Le rendez-vous a été modifié par un patient.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur dans la suppression du rendez-vous. Le rendez-vous a été modifié par un patient.");

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
                                  @RequestParam("idMotif") String idMotif,
                                  @RequestParam("start") String start) {
        try {
            Person person = PatientDB.getPatientByToken(tokenPat);
            List<Event> rdvFreeById = RDVDB.getRdvFreeById(idEvent);
            if (rdvFreeById.isEmpty()) {
                return new Message("FAIL", "Ce rendez-vous n'est plus disponible.");
            }
            Event curentEvent = rdvFreeById.get(0);
            if (!idMotifIsPresentInEvent(idMotif, curentEvent)) {
                return new Message("FAIL", "Ce rendez-vous n'est plus disponible.");
            }
            Timestamp timestamp = Timestamp.valueOf(start);
            if (curentEvent.getStart().compareTo(timestamp) != 0) {
                return new Message("FAIL", "Ce rendez-vous n'est plus disponible.");
            }
            curentEvent.setIdMotif(idMotif);
            curentEvent.setStatus(Status.WAITING);
            curentEvent.setIdPatient(person.getId());
            curentEvent.setNomPrenom(person.getNom() + " " + person.getPrenom());
            CabDB.addCabPersonIfNotPresent(person.getId(), curentEvent.getIdAdmin());
            RDVDB.saveRDVs(Collections.singletonList(curentEvent));
            sendEmail(person.getEmail(), TOOK_TITLE, TOOK_CONTENT.replace("xxx", FORMAT_MAIL.format(curentEvent.getStart())));
            //TODO controle now + 1 month
            return new Message("OK", "RAS");
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant la création du rendez-vous.");
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
                sendEmail(person.getEmail(), CANCEL_TITLE, CANCEL_CONTENT.replace("xxx", FORMAT_MAIL.format(rdv.getStart())));
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
