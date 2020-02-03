
package com.kinecab.demo.db.entity;

import com.google.common.hash.Hashing;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.nio.charset.StandardCharsets;

@Entity
public class Person {

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

    @Column()
    private String password;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors 
    //~ ----------------------------------------------------------------------------------------------------------------

    public Person() {
    }

    public Person(String nom, String prenom, String email, String tel, String password) {
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.tel = tel;
        this.password = Hashing.sha256().hashString(password, StandardCharsets.UTF_8).toString();
    }

    public Person(String nom, String prenom, String email, String tel) {
        this.email = email;
        this.nom = nom;
        this.prenom = prenom;
        this.tel = tel;
        this.password = null;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods 
    //~ ----------------------------------------------------------------------------------------------------------------

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
