
package com.kinecab.demo.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;


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
    private String color;

    @Column
    private int resource;

    @Column
    private int duree;

//~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public MotifCab() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MotifCab motifCab = (MotifCab) o;
        return id == motifCab.id && idCab == motifCab.idCab && resource == motifCab.resource && duree == motifCab.duree && motif.equals(motifCab.motif) && color.equals(motifCab.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, idCab, motif, color, resource, duree);
    }

    public MotifCab(int idCab, String motif) {
        this.idCab = idCab;
        this.motif = motif;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getResource() {
        return resource;
    }

    public void setResource(int resource) {
        this.resource = resource;
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

    public int getDuree() {
        return duree;
    }

    public void setDuree(int duree) {
        this.duree = duree;
    }
}
