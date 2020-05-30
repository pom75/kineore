
package com.kinecab.demo.db.entity;

import javax.persistence.*;


@Entity
@Table(name = "MOTIF_COLAB")
public class MotifColab {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    @Id
    private int id;

    @Column
    private int idColab;

    @Column
    private int idMotifCab;

    public MotifColab(int idAdmin, int idMotifCab) {
        this.idColab=idAdmin;
        this.idMotifCab = idMotifCab;
    }

    public MotifColab() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdAdmin() {
        return idColab;
    }

    public void setIdAdmin(int idColab) {
        this.idColab = idColab;
    }

    public int getIdMotifCab() {
        return idMotifCab;
    }

    public void setIdMotifCab(int idMotifCab) {
        this.idMotifCab = idMotifCab;
    }
}

