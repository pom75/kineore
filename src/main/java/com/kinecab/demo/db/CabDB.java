
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

    public static List<Cab> getCabByColabID(String id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from COLAB where COLAB.id = '" + id + "'");
            return getCabByID(((Colab)sqlQuery.addEntity(Colab.class).list().get(0)).getIdCab()+"");
        }
    }

    public static List<Cab> getCabByID(String id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from CAB where CAB.id = '" + id + "'");
            return sqlQuery.addEntity(Cab.class).list();
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


    public static List<Cab> getCabByUrl(String url) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from CAB where CAB.url = '" + url + "'");
            return sqlQuery.addEntity(Cab.class).list();
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
        Cab cab = getCabByColabID(idColab+"").get(0);
        CabPerson cabPerson = new CabPerson(cab.getId(),idPerson);
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * FROM CAB_PERSON WHERE  CAB_PERSON.idCab = '" + cab.getId() + "' AND CAB_PERSON.idPerson = '" + idPerson + "'");
            List cabPersons = sqlQuery.addEntity(CabPerson.class).list();
            if(cabPersons.isEmpty()){
                saveCabPerson(cabPerson);
            }
        }
    }

    public static boolean saveIfIsFree(Cab cab) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * FROM CAB WHERE  CAB.url = '" + cab.getUrl() + "'");
            List cabs =  sqlQuery.addEntity(Cab.class).list();
            if(cabs.isEmpty()){
                Transaction trx = session.beginTransaction();
                session.saveOrUpdate(cab);
                trx.commit();
                return true;
            }
        }
        return false;
    }
}
