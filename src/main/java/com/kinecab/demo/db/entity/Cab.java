
package com.kinecab.demo.db.entity;

import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
public class Cab {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    @Id
    private int id;

    @Column
    private String email;

    @Column
    private String url;

    @Column
    private String name;

    @Column
    @Type(type = "text")
    private String pres;

    @Column
    private String phone;

    @Column
    private String nomRue;

    @Column
    private String codePostal;

    @Column
    private String paiment;

    @Column
    private String ville;

    @Column
    private String cartevital;

    @Column
    private String tarif;

    @Column
    private String convention;

    @Column
    private String adminID;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public Cab() {
    }

    public Cab(String email, String url, String name, String pres, String phone, String nomRue, String ville, String codePostal, String paiment, String cartevital, String tarif, String convention, String adminID) {
        this.email = email;
        this.url = url;
        this.name = name;
        this.pres = pres;
        this.phone = phone;
        this.nomRue = nomRue;
        this.codePostal = codePostal;
        this.paiment = paiment;
        this.cartevital = cartevital;
        this.tarif = tarif;
        this.convention = convention;
        this.adminID = adminID;
        this.ville = ville;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPres() {
        return pres;
    }

    public void setPres(String pres) {
        this.pres = pres;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getNomRue() {
        return nomRue;
    }

    public void setNomRue(String nomRue) {
        this.nomRue = nomRue;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public void setCodePostal(String codePostal) {
        this.codePostal = codePostal;
    }

    public String getPaiment() {
        return paiment;
    }

    public void setPaiment(String paiment) {
        this.paiment = paiment;
    }

    public String getCartevital() {
        return cartevital;
    }

    public void setCartevital(String cartevital) {
        this.cartevital = cartevital;
    }

    public String getTarif() {
        return tarif;
    }

    public void setTarif(String tarif) {
        this.tarif = tarif;
    }

    public String getConvention() {
        return convention;
    }

    public void setConvention(String convention) {
        this.convention = convention;
    }
}
