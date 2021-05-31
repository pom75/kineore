
package com.kinecab.demo.db.entity;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import javax.persistence.*;

import com.google.common.hash.Hashing;
import com.kinecab.demo.util.Utils;


@Entity
@Table(name = "KINE_USER")
public class KineUser implements Serializable {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    public KineUser() {
    }

    public KineUser(String nom, String prenom, String email) {
        this.email = email;
        this.nom = Utils.trimUp(nom);
        this.prenom = Utils.trimUp(prenom);
    }

    public KineUser(String nom, String prenom, String email, String tel, String password) {
        this.email = email;
        this.nom = Utils.trimUp(nom);
        this.prenom = Utils.trimUp(prenom);
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
