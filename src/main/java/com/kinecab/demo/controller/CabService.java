
package com.kinecab.demo.controller;

import java.util.List;

import static com.kinecab.demo.db.AdminDB.getColabByToken;
import static com.kinecab.demo.db.CabDB.*;
import static com.kinecab.demo.db.RDVDB.getMotif;

import com.kinecab.demo.db.CabDB;
import com.kinecab.demo.db.entity.Cab;
import com.kinecab.demo.db.entity.Colab;
import com.kinecab.demo.db.entity.MotifCab;
import com.kinecab.demo.json.GetColab;
import com.kinecab.demo.json.GetMotif;
import com.kinecab.demo.json.Message;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;


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
            Colab colabByToken = getColabByToken(token);
            if (colabByToken == null) {
                return new Message("FAIL", "Token invalide.");
            }
            Cab cabByAdminID = getCabByColabID(String.valueOf(colabByToken.getId()));
            if (cabByAdminID == null) {
                return new Message("FAIL", "Pas de Cabinet a ce nom.");
            }
            Cab cab = cabByAdminID;
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
            if(urlcab.contentEquals(cab.getUrl())){
                saveCab(cab);
            }else {
                cab.setUrl(urlcab);
                if(!CabDB.saveIfIsFree(cab)){
                    return new Message("FAIL", "Impossible d'utiliser "+urlcab+ ", cet url est déjà pris.");
                }
            }
            return new Message("OK", "Modifications enregistrées.");
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Impossible de changer le Cabinet");
        }
    }

    //TODO : to remove
    @PostMapping(value = "/cab/getcabprofil", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Cab getCabProfil(@RequestParam("token") String token) {
        Colab colabByToken = getColabByToken(token);
        if (colabByToken == null) {
            return null;
        }
        return getCabByColabID(String.valueOf(colabByToken.getId()));
    }

    @PostMapping(value = "/cab/getcabid", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Cab getCabId(@RequestParam("id") String id) {
        try {
            Cab cabs = getCabByID(id);
            if (cabs == null) {
                return null;
            }
            return cabs;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @PostMapping(value = "/cab/getAdminsCab", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message getAdminsCab(@RequestParam("id") String id) {
        try {
            List<Colab> colabs = getColabsByIdCab(id.replace("#",""));
            if (colabs.isEmpty()) {
                return null;
            }
            return new GetColab("OK", "RAS", colabs);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Une erreur s'est produite.");
        }
    }

    @PostMapping(value = "/cab/motifs", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public Message getMotifs() {
        try {
            List<MotifCab> motif = getMotif();
            return new GetMotif("OK", "RAS", motif);
        } catch (Exception e) {
            e.printStackTrace();
            return new Message("FAIL", "Une erreur s'est produite.");
        }
    }

    @GetMapping(value = "/cab/{url}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public void redirect(@PathVariable("url") String url, HttpServletResponse httpServletResponse) {
        try {
            Cab cabByUrl = getCabByUrl(url);
            if (cabByUrl != null) {
                Cab cab = cabByUrl;
                httpServletResponse.setHeader("Location", "/cabinet.html?id=" + cab.getId());
                httpServletResponse.setStatus(302);
            } else {
                httpServletResponse.setHeader("Location", "/index.html");
                httpServletResponse.setStatus(302);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
