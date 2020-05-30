
package com.kinecab.demo.db;

import java.sql.Timestamp;

import java.util.*;

import com.kinecab.demo.db.entity.*;
import com.kinecab.demo.util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;

import org.json.JSONArray;
import org.json.JSONObject;


public class RDVDB {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    public static void saveRDVs(List<Event> rdvs) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            for (Event rdv : rdvs) {
                session.saveOrUpdate(rdv);
            }
            trx.commit();
        }
    }

    public static void saveRDV(Event rdv) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
                session.saveOrUpdate(rdv);
            trx.commit();
        }
    }


    public static List<Event> rdvJsonToRdvs(int adminId, JSONArray array) {
        final Iterator<Object> events = array.iterator();
        ArrayList<Event> rdvs = new ArrayList<>();
        while (events.hasNext()) {
            final JSONObject currentEvent = (JSONObject) events.next();
            int idPatient;
            final Object idPatObj = ((JSONObject) currentEvent.get("data")).get("idPatient");
            if (idPatObj instanceof Integer) {
                idPatient = (int) idPatObj;
            } else {
                idPatient = Integer.parseInt((String) idPatObj);
            }
            final Event event = new Event(adminId, Timestamp.valueOf((String) currentEvent.get("start")), Timestamp.valueOf((String) currentEvent.get("end")),
                Status.stringToStatus((String) ((JSONObject) currentEvent.get("data")).get("status")), idPatient, (String) ((JSONObject) currentEvent.get("data")).get("idMotif"),
                (Integer) ((JSONObject) currentEvent.get("data")).get("duration"), (String) ((JSONObject) currentEvent.get("data")).get("info"), (boolean) ((JSONObject) currentEvent.get("data")).get("pointe"),
                (boolean) ((JSONObject) currentEvent.get("data")).get("paye"), (String) ((JSONObject) currentEvent.get("data")).get("nomPatient"), (String) ((JSONObject) currentEvent.get("data")).get("listIdMotif"));
            if ((currentEvent.get("id") != null) && (Integer.parseInt(String.valueOf(currentEvent.get("id"))) != 0)) { //TODO DIRTY
                event.setId(Integer.parseInt(String.valueOf(currentEvent.get("id"))));
            }
            rdvs.add(event);
        }
        return rdvs;
    }

    public static List<Event> getRdvByTime(String start, String end, int id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * FROM Event WHERE  Event.idAdmin = '" + id + "' AND Event.start BETWEEN '" + start + "' AND '" + end + "';");
            return sqlQuery.addEntity(Event.class).list();
        }
    }

    public static List<Event> getRdvFreeByTime(String start, String end, int id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * FROM Event WHERE  Event.idAdmin = '" + id + "' AND Event.start BETWEEN '" + start + "' AND '" + end + "' AND Event.status = 'FREE';");
            return sqlQuery.addEntity(Event.class).list();
        }
    }

    public static void removeRdvByIds(JSONArray idEvents, int idAdmin) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery;
            Transaction trx = session.beginTransaction();
            for (Object currentId : idEvents) {
                sqlQuery = session.createSQLQuery("DELETE FROM Event WHERE Event.idAdmin = '" + idAdmin + "' AND  Event.id = '" + currentId + "';");
                sqlQuery.executeUpdate();
            }
            trx.commit();
        }
    }

    public static List<MotifCab> getMotifByIdColab(int id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * FROM MOTIF_COLAB WHERE  MOTIF_COLAB.idColab = '" + id + "';");
            List<MotifColab> list = sqlQuery.addEntity(MotifColab.class).list();
            List<MotifCab> motifCabs = new LinkedList<>();
            list.forEach(motifColab -> {
                NativeQuery sqlQuery2 = session.createSQLQuery("SELECT * FROM MOTIF_CAB WHERE  MOTIF_CAB.id = '" + motifColab.getIdMotifCab() + "';");
                motifCabs.add((MotifCab) sqlQuery2.addEntity(MotifCab.class).list().get(0));
            });
            return motifCabs;
        }
    }

    public static List<MotifCab> getMotif() {//TODO overkill
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * FROM MOTIF_CAB;");
            List<MotifCab> list = sqlQuery.addEntity(MotifCab.class).list();
            return list;
        }
    }

    public static List<Event> getRdvFreeById(String id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * FROM Event WHERE  Event.id = '" + id + "' AND Event.status = 'FREE';");
            return sqlQuery.addEntity(Event.class).list();
        }
    }

    public static List<Event> getRdvbyIdClient(int id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * FROM Event WHERE  Event.idPatient = '" + id + "';");
            return sqlQuery.addEntity(Event.class).list();
        }
    }

    public static Event getRdvbyId(int id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * FROM Event WHERE  Event.id = '" + id + "';");
            return (Event) sqlQuery.addEntity(Event.class).list().get(0);
        }
    }
}
