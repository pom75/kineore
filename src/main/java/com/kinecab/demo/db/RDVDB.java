
package com.kinecab.demo.db;

import com.kinecab.demo.db.entity.Event;
import com.kinecab.demo.db.entity.MotifCab;
import com.kinecab.demo.db.entity.MotifColab;
import com.kinecab.demo.db.entity.Status;
import com.kinecab.demo.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


public class RDVDB {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    public static void saveRDVs(List<Event> rdvs) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            for (Event rdv : rdvs) {
                session.saveOrUpdate(rdv);
            }
            trx.commit();
        }
    }

    public static boolean safeUpdateRDV(Event rdv, Status status) throws Exception {
        boolean succes = true;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            Event rdvbyId = RDVDB.getRdvbyId(rdv.getId());
            if (rdvbyId.getStatus() == status) {
                session.update(rdv);
            } else {
                succes = false;
            }
            trx.commit();
        }
        return succes;
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
                    (Integer) ((JSONObject) currentEvent.get("data")).get("duration"), (String) currentEvent.get("title"), (String) ((JSONObject) currentEvent.get("data")).get("info"), (boolean) ((JSONObject) currentEvent.get("data")).get("pointe"),
                    (boolean) ((JSONObject) currentEvent.get("data")).get("paye"), (String) ((JSONObject) currentEvent.get("data")).get("nomPatient"), (String) ((JSONObject) currentEvent.get("data")).get("listIdMotif"));
            if ((currentEvent.get("id") != null) && (Integer.parseInt(String.valueOf(currentEvent.get("id"))) != 0)) { //TODO DIRTY
                event.setId(Integer.parseInt(String.valueOf(currentEvent.get("id"))));
            }
            rdvs.add(event);
        }
        return rdvs;
    }

    public static List<Event> getRdvByTime(String start, String end, int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * FROM Event WHERE  Event.idAdmin = '" + id + "' AND Event.start BETWEEN '" + start + "' AND '" + end + "'");
            return sqlQuery.addEntity(Event.class).list();
        }
    }

    public static List<Event> getRdvFreeByTime(String start, String end, int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * FROM Event WHERE  Event.idAdmin = '" + id + "' AND Event.start BETWEEN '" + start + "' AND '" + end + "' AND Event.status = 'FREE' ORDER BY Event.start");
            return sqlQuery.addEntity(Event.class).list();
        }
    }

    public static boolean removeRdvByEvent(List<Event> events, int idAdmin) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery;
            Transaction trx = session.beginTransaction();
            for (Event event : events) {
                Event dbEvent = RDVDB.getRdvbyId(event.getId());
                if (dbEvent.getStatus() != event.getStatus()) {
                    trx.commit();
                    return false;
                } else {
                    sqlQuery = session.createSQLQuery("DELETE FROM Event WHERE Event.idAdmin = '" + idAdmin + "' AND  Event.id = '" + event.getId() + "'");
                    sqlQuery.executeUpdate();
                }
            }
            trx.commit();
        }
        return true;
    }

    public static List<MotifCab> getMotifByIdColab(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * FROM MOTIF_COLAB WHERE  MOTIF_COLAB.idColab = '" + id + "'");
            List<MotifColab> list = sqlQuery.addEntity(MotifColab.class).list();
            List<MotifCab> motifCabs = new LinkedList<>();
            list.forEach(motifColab -> {
                NativeQuery sqlQuery2 = session.createSQLQuery("SELECT * FROM MOTIF_CAB WHERE  MOTIF_CAB.id = '" + motifColab.getIdMotifCab() + "' and MOTIF_CAB.archived=0");
                List l = sqlQuery2.addEntity(MotifCab.class).list();
                if(!l.isEmpty()){
                    motifCabs.add((MotifCab) l.get(0));
                }
            });
            return motifCabs;
        }
    }

    public static List<MotifCab> getMotifCabByIdCab(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<MotifCab> motifCabs = new LinkedList<>();
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * FROM MOTIF_CAB WHERE  MOTIF_CAB.idCab = '" + id + "' and MOTIF_CAB.archived=0");
            motifCabs.addAll(sqlQuery.addEntity(MotifCab.class).list());
            return motifCabs;
        }
    }
    public static List<MotifCab> getArchivedMotifCabByIdCab(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<MotifCab> motifCabs = new LinkedList<>();
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * FROM MOTIF_CAB WHERE  MOTIF_CAB.idCab = '" + id + "' and MOTIF_CAB.archived=1");
            motifCabs.addAll(sqlQuery.addEntity(MotifCab.class).list());
            return motifCabs;
        }
    }

    public static List<MotifCab> getMotif() {//TODO overkill
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * FROM MOTIF_CAB");
            List<MotifCab> list = sqlQuery.addEntity(MotifCab.class).list();
            return list;
        }
    }

    public static void addMotif(int idCab,String motif,String color, String duree) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            NativeQuery sqlQuery = session.createSQLQuery("insert into MOTIF_CAB (idCab, motif, resource, color, duree) values(" + idCab + ", '" + motif + "', 0, '" + color + "', " + duree+ ")");
            sqlQuery.executeUpdate();
            trx.commit();
        }
    }
    public static void archiveMotif(int idMotif) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            NativeQuery sqlQuery = session.createSQLQuery("update MOTIF_CAB set archived=1 where id= " + idMotif);
            sqlQuery.executeUpdate();
            trx.commit();
        }
    }
    public static void modifyMotif(String id,String motif,String color) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            NativeQuery sqlQuery = session.createSQLQuery("update MOTIF_CAB set motif='" + motif + "', color='" + color + "' where id="+id);
            sqlQuery.executeUpdate();
            trx.commit();
        }
    }

    public static Event getRdvFreeById(String id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * FROM Event WHERE  Event.id = '" + id + "' AND Event.status = 'FREE'");
            return (Event) sqlQuery.addEntity(Event.class).uniqueResult();
        }
    }

    public static List<Event> getRdvbyIdClient(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * FROM Event WHERE  Event.idPatient = '" + id + "'");
            return sqlQuery.addEntity(Event.class).list();
        }
    }

    public static Event getRdvbyId(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * FROM Event WHERE  Event.id = '" + id + "'");
            return (Event) sqlQuery.addEntity(Event.class).uniqueResult();
        }
    }

    public static void removeMotifByIdColab(int id, int motifId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();

            NativeQuery sqlQuery = session.createSQLQuery("DELETE FROM MOTIF_COLAB WHERE  MOTIF_COLAB.idColab = '" + id + "' AND MOTIF_COLAB.idMotifCab = '" + motifId + "';");
            sqlQuery.executeUpdate();

            trx.commit();
        }
    }

    public static void addMotifsForCollab(int id, List<Integer> motifIds) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            for (Integer motifId : motifIds) {
                NativeQuery sqlQuery = session.createSQLQuery("INSERT INTO MOTIF_COLAB (idColab, idMotifCab) VALUES (" + id + ", " + motifId + ")");
                sqlQuery.executeUpdate();
            }
            trx.commit();
        }
    }
    public static void restoreMotifs(int id, List<Integer> motifIds) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            for (Integer motifId : motifIds) {
                NativeQuery sqlQuery = session.createSQLQuery("update MOTIF_CAB set archived=0 where id= " + motifId);
                sqlQuery.executeUpdate();
            }
            trx.commit();
        }
    }

}
