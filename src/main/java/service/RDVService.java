
package service;

import db.RDVDB;
import db.entity.Admin;
import db.entity.Event;
import db.entity.Motif;
import db.entity.Person;
import json.Message;
import org.json.JSONArray;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static db.LoginDB.getAdminByToken;


@Path("/rdv")
public class RDVService {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @POST
    @Produces("application/json")
    @Path("/bookrdv")
    public Response bookRDV(
        @FormParam("events") String events,
        @FormParam("tokenAdmin") String tokenAdmin) {
        try {
            List<Admin> adminByToken = getAdminByToken(tokenAdmin);
            if (adminByToken.isEmpty()) {
                return Response.ok().status(0).entity("Token invalide").build();
            }
            final List<Event> rdvs = RDVDB.rdvJsonToRdvs(adminByToken.get(0).getId(), new JSONArray(events));
            RDVDB.saveRDV(rdvs);
            final Set<Integer> collect = rdvs.stream().map(e -> e.getId()).collect(Collectors.toSet());
            return Response.ok().entity(collect).build();
        }catch (Exception e){
            System.out.println(e);
            return Response.ok().status(0).entity("Erreur pendant la création des rendez-vous.").build();
        }
    }

    @POST
    @Produces("application/json")
    @Path("/getrdv")
    public Response getRDV(
            @FormParam("start") String start,
            @FormParam("end") String end,
            @FormParam("tokenAdmin") String tokenAdmin) {
        try {
            List<Admin> adminByToken = getAdminByToken(tokenAdmin);
            if (adminByToken.isEmpty()) {
                return Response.ok().status(0).entity("Token invalide").build();
            }
            final List<Event> rdvs = RDVDB.getRdvByTime(start,end,adminByToken.get(0).getId());
            return Response.ok().entity(rdvs).build();
        }catch (Exception e){
            System.out.println(e);
            return Response.ok().status(0).entity("Erreur pendant le chargement des rendez-vous.").build();
        }
    }

    @POST
    @Produces("application/json")
    @Path("/removerdv")
    public Response removeRDV(
            @FormParam("idEvents") String idEvents,
            @FormParam("tokenAdmin") String tokenAdmin) {
        try {
            List<Admin> adminByToken = getAdminByToken(tokenAdmin);
            if (adminByToken.isEmpty()) {
                return Response.ok().entity( new Message("FAIL", "Token invalide")).build();
            }
            RDVDB.removeRdvByIds(new JSONArray(idEvents),adminByToken.get(0).getId());
            return Response.ok().entity(new Message("OK", "Evenement supprimé")).build();
        }catch (Exception e){
            System.out.println(e);
            return Response.ok().entity( new Message("FAIL", "Erreur interne serveur "+e)).build();

        }
    }

    @POST
    @Produces("application/json")
    @Path("/getmotif")
    public Response getMotif(
            @FormParam("tokenAdmin") String tokenAdmin) {
        try {
            List<Admin> adminByToken = getAdminByToken(tokenAdmin);
            if (adminByToken.isEmpty()) {
                return Response.ok().status(0).entity("Token invalide").build();
            }
            final List<Motif> motifByIdAdmin = RDVDB.getMotifByIdAdmin(adminByToken.get(0).getId());
            return Response.ok().entity(motifByIdAdmin).build();
        }catch (Exception e){
            System.out.println(e);
            return Response.ok().status(0).entity("Erreur pendant le chargement des rendez-vous.").build();
        }
    }

    @POST
    @Produces("application/json")
    @Path("/getpersonidadmin")
    public Response getPersonIdAdmin(
            @FormParam("tokenAdmin") String tokenAdmin) {
        try {
            List<Admin> adminByToken = getAdminByToken(tokenAdmin);
            if (adminByToken.isEmpty()) {
                return Response.ok().status(0).entity("Token invalide").build();
            }
            final List<Person> people = RDVDB.getPersonByIdAdmin(adminByToken.get(0).getId());
            return Response.ok().entity(people).build();
        }catch (Exception e){
            System.out.println(e);
            return Response.ok().status(0).entity("Erreur pendant le chargement des rendez-vous.").build();
        }
    }
}
