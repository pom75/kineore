
package service;

import java.net.URI;
import java.net.URISyntaxException;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static db.CabDB.*;

import static db.LoginDB.getAdminByToken;

import db.entity.Admin;
import db.entity.Cab;

import json.Message;


@Path("/cab")
public class CabService {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @POST
    @Produces("application/json")
    @Path("/updatecab")
    public Response updateCab(@FormParam("urlcab") String urlcab,
        @FormParam("nomcab") String nomcab,
        @FormParam("prescab") String prescab,
        @FormParam("numcab") String numcab,
        @FormParam("mailcab") String mailcab,
        @FormParam("token") String token,
        @FormParam("rue") String rue,
        @FormParam("codepostal") String codepostal,
        @FormParam("paiement") String paiement,
        @FormParam("vitale") String vitale,
        @FormParam("tarifs") String tarifs,
        @FormParam("convention") String convention) {
        List<Admin> adminByToken = getAdminByToken(token);
        if (adminByToken.isEmpty()) {
            return Response.status(Response.Status.OK).entity( new Message("FAIL", "Token invalide.")).build();
        }
        List<Cab> cabByAdminID = getCabByAdminID(String.valueOf(adminByToken.get(0).getId()));
        if (cabByAdminID.isEmpty()) {
            return Response.status(Response.Status.OK).entity( new Message("FAIL", "Pas de Cabinet a ce nom.")).build();
        }
        Cab cab = cabByAdminID.get(0);
        cab.setUrl(urlcab);
        cab.setName(nomcab);
        cab.setPres(prescab);
        cab.setPhone(numcab);
        cab.setEmail(mailcab);
        cab.setNomRue(rue);
        cab.setCodePostal(codepostal);
        cab.setPaiment(paiement);
        cab.setCartevital(vitale);
        cab.setTarif(tarifs);
        cab.setConvention(convention);

        saveCab(cab);
        return Response.status(Response.Status.OK).entity( new Message("OK", "Modifications enregistré.")).build();
    }

    @POST
    @Produces("application/json")
    @Path("/getcabprofil")
    public Response getCabProfil(@FormParam("token") String token) {
        List<Admin> adminByToken = getAdminByToken(token);
        if (adminByToken.isEmpty()) {
            return null;
        }
        return Response.ok().entity(getCabByAdminID(String.valueOf(adminByToken.get(0).getId())).get(0)).build();
    }

    @POST
    @Produces("application/json")
    @Path("/getcabid")
    public Response getCabId(@FormParam("id") String id) {
        List<Cab> cabs = getCabByID(id);
        if (cabs.isEmpty()) {
            return null;
        }
        return Response.ok().entity(cabs.get(0)).build();
    }

    @GET
    @Produces({ MediaType.TEXT_HTML })
    @Path("{url}")
    public Response redirect(@PathParam("url") String url) throws URISyntaxException {
        List<Cab> cabByUrl = getCabByUrl(url);
        if (!cabByUrl.isEmpty()) {
            Cab cab = cabByUrl.get(0);
            return Response.temporaryRedirect(new URI("http://localhost/cab.html?id=" + cab.getId())).build();
        } else {
            return Response.temporaryRedirect(new URI("http://localhost/notfound")).build();
        }
    }

}
