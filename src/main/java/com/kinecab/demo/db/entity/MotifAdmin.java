
package com.kinecab.demo.db.entity;

import javax.persistence.*;


@Entity
@Table(name = "MOTIF_ADMIN")
public class MotifAdmin {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    @Id
    private int id;

    @Column
    private int idAdmin;

    @Column
    private int idMotifCab;

    public MotifAdmin(int idAdmin,int idMotifCab) {
        this.idAdmin=idAdmin;
        this.idMotifCab = idMotifCab;
    }

    public MotifAdmin() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdAdmin() {
        return idAdmin;
    }

    public void setIdAdmin(int idAdmin) {
        this.idAdmin = idAdmin;
    }

    public int getIdMotifCab() {
        return idMotifCab;
    }

    public void setIdMotifCab(int idMotifCab) {
        this.idMotifCab = idMotifCab;
    }
}

