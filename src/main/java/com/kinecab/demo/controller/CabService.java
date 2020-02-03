
package com.kinecab.demo.controller;

import com.kinecab.demo.db.entity.Admin;
import com.kinecab.demo.db.entity.Cab;
import com.kinecab.demo.json.Message;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.RedirectView;

import java.util.List;

import static com.kinecab.demo.db.CabDB.*;
import static com.kinecab.demo.db.LoginDB.getAdminByToken;

@Controller
public class CabService {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @RequestMapping(value = "/cab/updatecab", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    Message updateCab(@RequestParam("urlcab") String urlcab,
                      @RequestParam("nomcab") String nomcab,
                      @RequestParam("prescab") String prescab,
                      @RequestParam("numcab") String numcab,
                      @RequestParam("mailcab") String mailcab,
                      @RequestParam("token") String token,
                      @RequestParam("rue") String rue,
                      @RequestParam("codepostal") String codepostal,
                      @RequestParam("paiement") String paiement,
                      @RequestParam("vitale") String vitale,
                      @RequestParam("tarifs") String tarifs,
                      @RequestParam("convention") String convention) {
        List<Admin> adminByToken = getAdminByToken(token);
        if (adminByToken.isEmpty()) {
            return  new Message("FAIL", "Token invalide.");
        }
        List<Cab> cabByAdminID = getCabByAdminID(String.valueOf(adminByToken.get(0).getId()));
        if (cabByAdminID.isEmpty()) {
            return  new Message("FAIL", "Pas de Cabinet a ce nom.");
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
        return  new Message("OK", "Modifications enregistré.");
    }

    @RequestMapping(value = "/cab/getcabprofil", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Cab getCabProfil(@RequestParam("token") String token) {
        List<Admin> adminByToken = getAdminByToken(token);
        if (adminByToken.isEmpty()) {
            return null;
        }
        return getCabByAdminID(String.valueOf(adminByToken.get(0).getId())).get(0);
    }

    @RequestMapping(value = "/cab/getcabid", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Cab getCabId(@RequestParam("id") String id) {
        List<Cab> cabs = getCabByID(id);
        if (cabs.isEmpty()) {
            return null;
        }
        return cabs.get(0);
    }

    @RequestMapping(value = "/cab/{url}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody RedirectView redirect(@PathVariable("url") String url) {
        List<Cab> cabByUrl = getCabByUrl(url);
        RedirectView redirectView = new RedirectView();
        if (!cabByUrl.isEmpty()) {
            Cab cab = cabByUrl.get(0);
            redirectView.setUrl("/cabinet.html?id=" + cab.getId());
        } else {
            redirectView.setUrl("/notfound");
        }
        return redirectView;
    }

}
