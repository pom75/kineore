package com.kinecab.demo.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "COLAB")
public class Colab {

    @Id
    private int id;

    @Column
    private int idCab;

    @Column
    private int idKineUser;

    @Column
    private String name;

    @Column
    private  boolean isSuperAdmin;

    public Colab() {
    }


    public boolean isSuperAdmin() {
        return isSuperAdmin;
    }

    public void setSuperAdmin(boolean superAdmin) {
        isSuperAdmin = superAdmin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCab() {
        return idCab;
    }

    public void setIdCab(int idCab) {
        this.idCab = idCab;
    }

    public int getIdKineUser() {
        return idKineUser;
    }

    public void setIdKineUser(int idKineUser) {
        this.idKineUser = idKineUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
