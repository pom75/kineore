
package com.kinecab.demo.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "MOTIF_CAB")
public class MotifCab {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    @Id
    private int id;

    @Column
    private int idCab;

    @Column
    private String motif;

    @Column
    private int resource;


//~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public MotifCab() {
    }

    public MotifCab(int idCab, String motif) {
        this.idCab = idCab;
        this.motif = motif;
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------
    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
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

    public void setIdCab(int idAdmin) {
        this.idCab = idAdmin;
    }
}
