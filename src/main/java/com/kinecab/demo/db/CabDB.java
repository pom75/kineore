
package com.kinecab.demo.db;

import java.util.LinkedList;
import java.util.List;

import com.kinecab.demo.db.entity.*;
import com.kinecab.demo.util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;


public class CabDB {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Constructors
    //~ ----------------------------------------------------------------------------------------------------------------

    private CabDB() {
    }

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    public static List<Cab> getCabByAdminID(String id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from CAB_ADMIN where CAB_ADMIN.idAdmin = '" + id + "' ;");
            return getCabByID(((CabAdmin)sqlQuery.addEntity(CabAdmin.class).list().get(0)).getIdCab());
        }
    }

    public static List<Cab> getCabByID(String id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from CAB where CAB.id = '" + id + "' ;");
            return sqlQuery.addEntity(Cab.class).list();
        }
    }

    public static List<Admin> getAdminsByIdCab(String id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from CAB_ADMIN where CAB_ADMIN.idCab = '" + id + "' ;");
            List<CabAdmin> list = sqlQuery.addEntity(CabAdmin.class).list();
            List<Admin> admins = new LinkedList<>();
            list.forEach(cab ->{
                NativeQuery sqlQuery2 = session.createSQLQuery("SELECT * from ADMIN where ADMIN.id = '" + cab.getIdAdmin() + "' ;");
                Admin admin = (Admin) sqlQuery2.addEntity(Admin.class).list().get(0);
                admin.setPassword("");
                admin.setEmail("");
                admins.add(admin);
            });
            return admins;
        }
    }

    public static void saveCabPerson(CabPerson cabPerson) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            session.save(cabPerson);
            trx.commit();
        }
    }

    public static void saveCabAdmin(CabAdmin person) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            session.saveOrUpdate(person);
            trx.commit();
        }
    }

    public static List<Cab> getCabByUrl(String url) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from CAB where CAB.url = '" + url + "' ;");
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

    public static void addCabPersonIfNotPresent(int idPerson,int idAdmin) {
        Cab cab = getCabByAdminID(idAdmin+"").get(0);
        CabPerson cabPerson = new CabPerson(cab.getId(),idPerson);
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * FROM CAB_PERSON WHERE  CAB_PERSON.idCab = '" + cab.getId() + "' AND CAB_PERSON.idPerson = '" + idPerson + "';");
            List<CabPerson> cabPersons = (List<CabPerson>) sqlQuery.addEntity(CabPerson.class).list();
            if(cabPersons.isEmpty()){
                saveCabPerson(cabPerson);
            }
        }
    }

    public static List<CabAdmin> getCab() {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from CAB_ADMIN ;");
            return sqlQuery.addEntity(CabAdmin.class).list();
        }
    }

}
