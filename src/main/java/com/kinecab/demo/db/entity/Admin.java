
package com.kinecab.demo.db.entity;

import com.google.common.hash.Hashing;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlRootElement;
import java.nio.charset.StandardCharsets;

@XmlRootElement
@Entity
public class Admin {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    @Id
    private int id;

    @Column
    private String email;

    @Column
    private String nom;

    @Column
    private String prenom;

    @Column
    private String tel;

    @Column
    private String adeli;

    @Column
    private String rpps;

    @Column
    private String password;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public Admin() {
    }

    public Admin(String nom, String prenom, String email, String tel, String password) {
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.tel = tel;
        this.password = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    public String getAdeli() {
        return adeli;
    }

    public void setAdeli(String adeli) {
        this.adeli = adeli;
    }

    public String getRpps() {
        return rpps;
    }

    public void setRpps(String rpps) {
        this.rpps = rpps;
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

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
    }

    public void setCryptedPassword(String password) {
        this.password = password;
    }
}
