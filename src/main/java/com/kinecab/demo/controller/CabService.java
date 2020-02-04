
package com.kinecab.demo.controller;

import java.util.List;

import static com.kinecab.demo.db.AdminDB.getAdminByToken;
import static com.kinecab.demo.db.CabDB.*;
import com.kinecab.demo.db.entity.Admin;
import com.kinecab.demo.db.entity.Cab;
import com.kinecab.demo.json.Message;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.result.view.RedirectView;


@Controller
public class CabService {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    @PostMapping(value = "/cab/updatecab", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message updateCab(@RequestParam("urlcab") String urlcab,
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
        try {
            List<Admin> adminByToken = getAdminByToken(token);
            if (adminByToken.isEmpty()) {
                return new Message("FAIL", "Token invalide.");
            }
            List<Cab> cabByAdminID = getCabByAdminID(String.valueOf(adminByToken.get(0).getId()));
            if (cabByAdminID.isEmpty()) {
                return new Message("FAIL", "Pas de Cabinet a ce nom.");
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
            return new Message("OK", "Modifications enregistré.");
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("OK", "Impossible de changer le Cabinet");
        }
    }

    @PostMapping(value = "/cab/getcabprofil", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Cab getCabProfil(@RequestParam("token") String token) {
        List<Admin> adminByToken = getAdminByToken(token);
        if (adminByToken.isEmpty()) {
            return null;
        }
        return getCabByAdminID(String.valueOf(adminByToken.get(0).getId())).get(0);
    }

    @PostMapping(value = "/cab/getcabid", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Cab getCabId(@RequestParam("id") String id) {
        try {
            List<Cab> cabs = getCabByID(id);
            if (cabs.isEmpty()) {
                return null;
            }
            return cabs.get(0);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @GetMapping(value = "/cab/{url}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RedirectView redirect(@PathVariable("url") String url) {
        try {
            List<Cab> cabByUrl = getCabByUrl(url);
            RedirectView redirectView = new RedirectView();
            if (!cabByUrl.isEmpty()) {
                Cab cab = cabByUrl.get(0);
                redirectView.setUrl("/cabinet.html?id=" + cab.getId());
            } else {
                redirectView.setUrl("/notfound");
            }
            return redirectView;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
