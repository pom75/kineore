
package com.kinecab.demo.db;

import com.kinecab.demo.db.entity.Cab;
import com.kinecab.demo.db.entity.CabPerson;
import com.kinecab.demo.db.entity.Colab;
import com.kinecab.demo.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;

import java.util.List;


public class CabDB {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    private CabDB() {
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    public static Cab getCabByColabID(String id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from COLAB where COLAB.id = '" + id + "'");
            return getCabByID(((Colab)sqlQuery.addEntity(Colab.class).uniqueResult()).getIdCab()+"");
        }
    }

    public static Cab getCabByID(String id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from CAB where CAB.id = '" + id + "'");
            return (Cab) sqlQuery.addEntity(Cab.class).uniqueResult();
        }
    }

    public static List<Colab> getColabsByIdCab(String id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from COLAB where COLAB.idCab = '" + id + "'");
            return sqlQuery.addEntity(Colab.class).list();
        }
    }

    public static void saveCabPerson(CabPerson cabPerson) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            session.save(cabPerson);
            trx.commit();
        }
    }


    public static Cab getCabByUrl(String url) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from CAB where CAB.url = '" + url + "'");
            return (Cab) sqlQuery.addEntity(Cab.class).uniqueResult();
        }
    }

    public static void saveCab(Cab cab) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            session.saveOrUpdate(cab);
            trx.commit();
        }
    }

    public static void addCabPersonIfNotPresent(int idPerson,int idColab) {
        Cab cab = getCabByColabID(idColab+"");
        CabPerson cabPerson = new CabPerson(cab.getId(),idPerson);
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * FROM CAB_PERSON WHERE  CAB_PERSON.idCab = '" + cab.getId() + "' AND CAB_PERSON.idPerson = '" + idPerson + "'");
            CabPerson cabPersons = (CabPerson) sqlQuery.addEntity(CabPerson.class).uniqueResult();
            if(cabPersons == null){
                saveCabPerson(cabPerson);
            }
        }
    }

    public static boolean saveIfIsFree(Cab cab) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * FROM CAB WHERE  CAB.url = '" + cab.getUrl() + "'");
            Cab cabs = (Cab) sqlQuery.addEntity(Cab.class).uniqueResult();
            if(cabs == null){
                Transaction trx = session.beginTransaction();
                session.saveOrUpdate(cab);
                trx.commit();
                return true;
            }
        }
        return false;
    }
}
