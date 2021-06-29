package com.kinecab.demo.controller;
import com.kinecab.demo.db.CabDB;
import com.kinecab.demo.db.PatientDB;
import com.kinecab.demo.db.RDVDB;
import com.kinecab.demo.db.entity.*;
import com.kinecab.demo.json.*;
import com.kinecab.demo.util.MailUtil;
import org.json.JSONArray;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.kinecab.demo.db.KineUserDB.*;
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
                            @RequestParam("tokenKineUser") String tokenKineUser) {
        try {
            Colab colabByToken = getColabByToken(tokenKineUser);
            if (colabByToken == null) {
                return new Message("FAIL", "Token invalide");
            }
            final List<Event> rdvs = RDVDB.rdvJsonToRdvs(colabByToken.getId(), new JSONArray(events));
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
                                    @RequestParam("tokenKineUser") String tokenKineUser) {
        try {
            Colab colabByToken = getColabByToken(tokenKineUser);
            if (colabByToken == null) {
                return new Message("FAIL", "Token invalide");
            }
            Event rdv = RDVDB.rdvJsonToRdvs(colabByToken.getId(), new JSONArray(events)).get(0);
            Event rdvbyId = RDVDB.getRdvbyId(rdv.getId());
            if (rdvbyId.getIdColab() == rdv.getIdColab()) {
                if (RDVDB.safeUpdateRDV(rdv, Status.FREE)) {
                    MailUtil.sendEmail(getPatientById(rdv.getIdPatient() + "").getEmail(), ACCEPTE_TITLE, ACCEPTE_CONTENT.replace("xxx", FORMAT_MAIL.format(rdv.getStart())));
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
    @PostMapping(value = "/rdv/savepostit", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message savePostIt(@RequestParam("events") String events,
                              @RequestParam("tokenKineUser") String tokenKineUser) {
        try {
            Colab colabByToken = getColabByToken(tokenKineUser);
            if (colabByToken == null) {
                return new Message("FAIL", "Token invalide");
            }
            List<Event> rdvs = RDVDB.rdvJsonToRdvs(colabByToken.getId(), new JSONArray(events));
            Event rdv = rdvs.get(0);
            Event rdvbyId = RDVDB.getRdvbyId(rdv.getId());
            if (rdvbyId.getIdColab() == rdv.getIdColab()) {
                RDVDB.saveRDVs(rdvs);
                return new Message("OK", "RAS");
            } else {
                return new Message("FAIL", "Erreur, le rendez-vous que vous voulez modifier vient d'etre pris par un patient.");
            }
        } catch (
                Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant la création du Post it.");
        }
    }
    @PostMapping(value = "/rdv/moveevent", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message moveEvent(@RequestParam("events") String events,
                             @RequestParam("tokenKineUser") String tokenKineUser) {
        try {
            Colab colabByToken = getColabByToken(tokenKineUser);
            if (colabByToken == null) {
                return new Message("FAIL", "Token invalide");
            }
            Event rdv = RDVDB.rdvJsonToRdvs(colabByToken.getId(), new JSONArray(events)).get(0);
            Event rdvbyId = RDVDB.getRdvbyId(rdv.getId());
            if (rdvbyId.getIdColab() == rdv.getIdColab()) {
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
                                   @RequestParam("tokenKineUser") String tokenKineUser,
                                   @RequestParam String status,
                                   @RequestParam String idPat) {
        try {
            Colab colabByToken = getColabByToken(tokenKineUser);
            if (colabByToken == null) {
                return new Message("FAIL", "Token invalide");
            }
            Event rdv = RDVDB.rdvJsonToRdvs(colabByToken.getId(), new JSONArray(events)).get(0);
            Event rdvbyId = RDVDB.getRdvbyId(rdv.getId());
            if (rdvbyId.getIdColab() == rdv.getIdColab()) {
                boolean sucess;
                if (status.equalsIgnoreCase("CANCEL")) {
                    sucess = RDVDB.safeUpdateRDV(rdv, Status.BOOKED);
                } else {
                    sucess = RDVDB.safeUpdateRDV(rdv, Status.WAITING);
                }
                if (!sucess) {
                    return new Message("FAIL", "Le rendez-vous que vous voulez modifier vient d'etre mis à jour par le patient.");
                }
                switch (status) {
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
                          @RequestParam("tokenKineUser") String tokenKineUser) {
        try {
            Colab colabByToken = getColabByToken(tokenKineUser);
            if (colabByToken == null) {
                return new Message("FAIL", "Token invalide");
            }
            final List<Event> rdvs = RDVDB.getRdvByTime(start, end, colabByToken.getId());
            return new GetRDV("OK", "RAS", rdvs);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant le chargement des rendez-vous.");
        }
    }

    @PostMapping(value = "/rdv/getrdvbyidcolab", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message getRDVByIdColab(@RequestParam("start") String start,
                                   @RequestParam("end") String end,
                                   @RequestParam("tokenKineUser") String tokenKineUser,
                                   @RequestParam("idKineUser") String idKineUser) {
        try {
            Colab colab = getColabByToken(tokenKineUser);
            if (colab == null) {
                return new Message("FAIL", "Token invalide");
            }
            if (colab.getId() == Integer.parseInt(idKineUser)) {//TODO wrong comparaison compar idCOlab to idKineuser
                final List<Event> rdvs = RDVDB.getRdvByTime(start, end, colab.getId());
                return new GetRDV("OK", "RAS", rdvs);
            } else {
                List<KineUser> kineUserList = getAllCabKineUserByToken(tokenKineUser);
                if (kineUserList.isEmpty()) {
                    return new Message("FAIL", "Token invalide");
                }
                boolean containKineUser = false;
                for (KineUser kineUser : kineUserList) {
                    if (kineUser.getId() == Integer.parseInt(idKineUser)) {
                        containKineUser = true;
                    }
                }
                if (!containKineUser) {
                    return new Message("FAIL", "Token invalide");
                }

                final List<Event> rdvs = RDVDB.getRdvByTime(start, end, getColabByIdKineUser(idKineUser).getId());
                return new GetRDV("OK", "RAS", rdvs);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant le chargement des rendez-vous.");
        }
    }
    @PostMapping(value = "/rdv/getrdvfree", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message getRDVFree(
            @RequestParam("idKineUser") String idKineUser) {
        try {
            String start = getStart();
            String end = getEnd();
            final List<Event> rdvs = RDVDB.getRdvFreeByTime(start, end, Integer.parseInt(idKineUser.replace("#", "")));
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
                             @RequestParam("tokenKineUser") String tokenKineUser) {
        try {
            Colab colabByToken = getColabByToken(tokenKineUser);
            if (colabByToken == null) {
                return new Message("FAIL", "Token invalide");
            }
            final List<Event> rdvs = RDVDB.rdvJsonToRdvs(colabByToken.getId(), new JSONArray(listEvents));
            boolean success = RDVDB.removeRdvByEvent(rdvs, colabByToken.getId());
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

    @PostMapping(value = "/rdv/getmotifcolabbytoken", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message getMotifByToken(@RequestParam("tokenKineUser") String tokenKineUser) {
        try {
            Colab colabByToken = getColabByToken(tokenKineUser);
            if (colabByToken == null) {
                return new Message("FAIL", "Token invalide");
            }
            final List<MotifCab> motifCabList = RDVDB.getMotifByIdColab(colabByToken.getId());
            return new GetMotif("OK", "RAS", motifCabList);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant le chargement des Motifs.");
        }
    }

    //Mdr  to refactor this shit
    @PostMapping(value = "/rdv/getmotifcolabbyidkineuser", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message getMotifById(@RequestParam("tokenKineUser") String tokenKineUser, @RequestParam("idKineUser") String idKineUser) {
        try {
            Colab colab = getColabByToken(tokenKineUser);
            if (colab == null) {
                return new Message("FAIL", "Token invalide");
            }
            if (colab.getIdKineUser() == Integer.parseInt(idKineUser)) {
                final List<MotifCab> motifCabList = RDVDB.getMotifByIdColab(colab.getId());
                return new GetMotif("OK", "RAS", motifCabList);
            } else {

                List<KineUser> kineUserList = getAllCabKineUserByToken(tokenKineUser);
                if (kineUserList.isEmpty()) {
                    return new Message("FAIL", "Token invalide");
                }
                boolean containKineUser = false;
                for (KineUser kineUser : kineUserList) {
                    if (kineUser.getId() == Integer.parseInt(idKineUser)) {
                        containKineUser = true;
                    }
                }
                if (!containKineUser) {
                    return new Message("FAIL", "Token invalide");
                }

                final List<MotifCab> motifCabs = RDVDB.getMotifByIdColab(getColabByIdKineUser(idKineUser).getId());
                return new GetMotif("OK", "RAS", motifCabs);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant le chargement des Motifs.");
        }
    }

    @PostMapping(value = "/rdv/getmotiftokenKineUser", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message getMotifIdBytokenKineUser(@RequestParam("tokenKineUser")  String tokenKineUser) {
        try {
            int idCab = getColabByToken(tokenKineUser).getIdCab();
            final List<MotifCab> motifCabList = RDVDB.getMotifCabByIdCab(idCab);
            return new GetMotif("OK", "RAS", motifCabList);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant le chargement des Motifs.");
        }
    }
    @PostMapping(value = "/rdv/getArchivedMotiftokenKineUser", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message getArchivedMotifIdBytokenKineUser(@RequestParam("tokenKineUser")  String tokenKineUser) {
        try {
            int idCab = getColabByToken(tokenKineUser).getIdCab();
            final List<MotifCab> motifCabList = RDVDB.getArchivedMotifCabByIdCab(idCab);
            return new GetMotif("OK", "RAS", motifCabList);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant le chargement des Motifs.");
        }
    }
    @PostMapping(value = "/rdv/getmotifcabid", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message getMotifId(@RequestParam("idCab") String idCab) {
        try {
            final List<MotifCab> motifCabList = RDVDB.getMotifCabByIdCab(Integer.parseInt(idCab.replace("#", "")));
            return new GetMotif("OK", "RAS", motifCabList);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant le chargement des Motifs.");
        }
    }

    @PostMapping(value = "/rdv/getpersonidkineuser", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message getPersonByIdKineUser(@RequestParam("tokenKineUser") String tokenKineUser) {
        try {
            Colab colabByToken = getColabByToken(tokenKineUser);
            if (colabByToken == null) {
                return new Message("FAIL", "Token invalide");
            }
            final List<Person> people = getPersonByIdCab(colabByToken.getIdCab());
            return new GetPeople("OK", "RAS", people);
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
            Event rdvFreeById = RDVDB.getRdvFreeById(idEvent);
            if (rdvFreeById == null) {
                //Event not free in the DB
                return new Message("FAIL", "Ce rendez-vous n'est plus disponible.");
            }
            Event curentEvent = rdvFreeById;
            if (!idMotifIsPresentInEvent(idMotif, curentEvent)) {
                //Wrong Motif
                return new Message("FAIL", "Ce rendez-vous n'est plus disponible.");
            }
            curentEvent.setIdMotif(idMotif);
            curentEvent.setStatus(Status.WAITING);
            curentEvent.setIdPatient(person.getId());
            curentEvent.setNomPrenom(person.getNom() + " " + person.getPrenom());
            CabDB.addCabPersonIfNotPresent(person.getId(), curentEvent.getIdColab());
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
    @PostMapping(value = "/rdv/getrdvpatient", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message getRdvPatient(@RequestParam("id") String id,
                                 @RequestParam("tokenKineUser") String token) {
        try {
            Colab colabByToken = getColabByToken(token);
            if (colabByToken == null) {
                return new Message("FAIL", "Token invalide");
            }
            Person person = getPersonByIdCabIdPerson(colabByToken.getIdCab(), id);
            if (person == null) {
                return new Message("FAIL", "Aucun Patient trouvé.");
            }
            final List<Event> rdvs = RDVDB.getRdvbyIdClient(person.getId()).stream().filter(event -> event.getIdColab() == colabByToken.getId()).collect(Collectors.toList());
            return new GetRDV("OK", "RAS", rdvs);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Impossible recuperer les rendez-vous");
        }
    }

    @PostMapping(value = "/rdv/getmotifCabNotUsedByColab", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message getMotifCabNotUsedByColab(@RequestParam("tokenKineUser") String tokenKineUser) {
        try {
            Colab colabByToken = getColabByToken(tokenKineUser);
            if (colabByToken == null) {
                return new Message("FAIL", "Token invalide");
            }

            final List<MotifCab> motifByIdCab = RDVDB.getMotifCabByIdCab(colabByToken.getIdCab());
            final List<MotifCab> motifCabList = RDVDB.getMotifByIdColab(colabByToken.getId());
            List<MotifCab> result = new ArrayList<>(motifByIdCab);
            result.removeAll(motifCabList);
            return new GetMotif("OK", "RAS", result);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant le chargement des Motifs.");
        }
    }

    @PostMapping(value = "/rdv/remCollabMotif", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message removeCollaboratorMotif(@RequestParam("tokenKineUser") String tokenKineUser, @RequestParam("motifId") String motifId) {
        try {
            Colab colabByToken = getColabByToken(tokenKineUser);
            if (colabByToken == null) {
                return new Message("FAIL", "Token invalide");
            }
            RDVDB.removeMotifByIdColab(colabByToken.getId(), Integer.parseInt(motifId));
            return new Message("OK", "Evenement supprimé");
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant le chargement des Motifs.");
        }
    }

    @PostMapping(value = "/rdv/addMotifsForCollab", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message addMotifsForCollab(@RequestParam("tokenKineUser") String tokenKineUser, @RequestParam("motifIds") String[] motifIds) {
        try {
            Colab colabByToken = getColabByToken(tokenKineUser);
            if (colabByToken == null) {
                return new Message("FAIL", "Token invalide");
            }
            ArrayList<Integer> motifs = new ArrayList<Integer>();
            for (String motif : motifIds
            ) {
                motifs.add(Integer.parseInt(motif));
            }
            RDVDB.addMotifsForCollab(colabByToken.getId(), motifs);
            return new Message("OK", "Evenement supprimé");
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant le chargement des Motifs.");
        }
    }
    @PostMapping(value = "/rdv/restoreMotifs", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message restoreMotifs(@RequestParam("tokenKineUser") String tokenKineUser, @RequestParam("motifIds") String[] motifIds) {
        try {
            Colab colabByToken = getColabByToken(tokenKineUser);
            if (colabByToken == null) {
                return new Message("FAIL", "Token invalide");
            }
            ArrayList<Integer> motifs = new ArrayList<Integer>();
            for (String motif : motifIds
            ) {
                motifs.add(Integer.parseInt(motif));
            }
            RDVDB.restoreMotifs(colabByToken.getId(), motifs);
            return new Message("OK", "Evenement supprimé");
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant le chargement des Motifs.");
        }
    }

    @PostMapping(value = "/rdv/addMotifForCabinet", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message addMotifForCabinet(@RequestParam("tokenKineUser") String tokenKineUser, @RequestParam("motif") String motif,@RequestParam("color") String color,@RequestParam("duree") String duree) {
        try {
            //TODO : add table "superadmin" cab and add check
            Colab colabByToken = getColabByToken(tokenKineUser);
            if (colabByToken == null) {
                return new Message("FAIL", "Token invalide");
            }
            //TODO add check on input param
            RDVDB.addMotif(colabByToken.getIdCab(),motif,color,duree);
            return new Message("OK", "Motif ajoute");
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant l'addition du Motif.");
        }
    }

    @PostMapping(value = "/rdv/modifyMotif", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message modifyMotif(@RequestParam("tokenKineUser") String tokenKineUser,@RequestParam("id") String id,@RequestParam("motif") String motif,@RequestParam("color") String color) {
        try {
            //TODO : add table "superadmin" cab and add check
            Colab colabByToken = getColabByToken(tokenKineUser);
            if (colabByToken == null) {
                return new Message("FAIL", "Token invalide");
            }
            RDVDB.modifyMotif(id,motif,color);
            return new Message("OK", "Motif ajoute");
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant l'addition du Motif.");
        }
    }

    @PostMapping(value = "/rdv/archiveMotif", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message archiveMotif(@RequestParam("tokenKineUser") String tokenKineUser, @RequestParam("motifId") String motifId) {
        try {
            Colab colabByToken = getColabByToken(tokenKineUser);
            if (colabByToken == null) {
                return new Message("FAIL", "Token invalide");
            }
            RDVDB.archiveMotif(Integer.parseInt(motifId));
            return new Message("OK", "Motif archivé");
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Erreur pendant le chargement des Motifs.");
        }
    }
}