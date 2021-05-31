
package com.kinecab.demo.db.entity;

import com.google.common.base.Objects;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.Timestamp;


@Entity
@Table(name = "Event")
public class Event {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Instance fields
    //~ ----------------------------------------------------------------------------------------------------------------

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private int idColab;

    @Column
    private Timestamp start;

    @Column
    private Timestamp end;

    @Enumerated(EnumType.STRING)
    @Column
    private Status status;

    @Column
    private int idPatient;

    @Column
    private String idMotif;

    @Column
    private String listIdMotif;

    @Column
    private int duration;

    @Column
    private String titrePostIt;

    @Column
    @Type(type="text")
    private String info;

    @Column
    private boolean pointe;

    @Column
    private boolean paye;

    @Column
    private String nomPrenom;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    public Event(int idColab, Timestamp start, Timestamp end, Status status, int idPatient, String idMotif, int duration, String titre, String info, boolean pointe, boolean paye, String nomPrenom, String listIdMotif) {
        this.idColab = idColab;
        this.start = start;
        this.end = end;
        this.status = status;
        this.idPatient = idPatient;
        this.idMotif = idMotif;
        this.duration = duration;
        this.info = info;
        this.pointe = pointe;
        this.paye = paye;
        this.nomPrenom = nomPrenom;
        this.listIdMotif=listIdMotif;
        this.titrePostIt = titre;
    }

    public Event() {

    }

    //Create same event but free
    public Event(Event other) {
        this.idColab = other.idColab;
        this.start = other.start;
        this.end = other.end;
        this.status = Status.FREE;
        this.idPatient = 0;
        this.idMotif = "0";
        this.listIdMotif = other.listIdMotif;
        this.duration = other.duration;
        this.info = "";
        this.pointe = false;
        this.paye = false;
        this.nomPrenom = "";
    }

//~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    public boolean isPointe() {
        return pointe;
    }

    public void setPointe(boolean pointe) {
        this.pointe = pointe;
    }

    public boolean isPaye() {
        return paye;
    }

    public void setPaye(boolean paye) {
        this.paye = paye;
    }

    public String getTitrePostIt() {
        return titrePostIt;
    }

    public void setTitrePostIt(String titrePostIt) {
        this.titrePostIt = titrePostIt;
    }

    public String getNomPrenom() {
        return nomPrenom;
    }

    public void setNomPrenom(String nomPrenom) {
        this.nomPrenom = nomPrenom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdColab() {
        return idColab;
    }

    public void setIdColab(int idColab) {
        this.idColab = idColab;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public int getIdPatient() {
        return idPatient;
    }

    public void setIdPatient(int idPatient) {
        this.idPatient = idPatient;
    }

    public String getIdMotif() {
        return idMotif;
    }

    public void setIdMotif(String idMotif) {
        this.idMotif = idMotif;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getListIdMotif() {
        return listIdMotif;
    }

    public void setListIdMotif(String listIdMotif) {
        this.listIdMotif = listIdMotif;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return getId() == event.getId() &&
                getIdColab() == event.getIdColab() &&
                getIdPatient() == event.getIdPatient() &&
                getIdMotif() == event.getIdMotif() &&
                getDuration() == event.getDuration() &&
                isPointe() == event.isPointe() &&
                isPaye() == event.isPaye() &&
                Objects.equal(getStart(), event.getStart()) &&
                Objects.equal(getEnd(), event.getEnd()) &&
                getStatus() == event.getStatus() &&
                Objects.equal(getInfo(), event.getInfo()) &&
                Objects.equal(getNomPrenom(), event.getNomPrenom());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId(), getIdColab(), getStart(), getEnd(), getStatus(), getIdPatient(), getIdMotif(), getDuration(), getInfo(), isPointe(), isPaye(), getNomPrenom());
    }
}
