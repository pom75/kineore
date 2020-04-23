
package com.kinecab.demo.db;

import java.util.LinkedList;
import java.util.List;

import com.kinecab.demo.db.entity.Admin;
import com.kinecab.demo.db.entity.Cab;
import com.kinecab.demo.db.entity.CabAdmin;
import com.kinecab.demo.db.entity.CabPerson;
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
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from CAB where CAB.adminid = '" + id + "' ;");
            return sqlQuery.addEntity(Cab.class).list();
        }
    }

    public static List<Cab> getCabByID(String id) {
        System.out.println(id);
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from CAB where CAB.id = '" + id + "' ;");
            return sqlQuery.addEntity(Cab.class).list();
        }
    }

    public static List<Admin> getAdminsByIdCab(String id) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from CAB_ADMIN where CAB_ADMIN.idCab = '" + id + "' ;");
            List<CabAdmin> list = sqlQuery.addEntity(CabAdmin.class).list();
            List<Admin> admins = new LinkedList();
            list.forEach(cab ->{
                NativeQuery sqlQuery2 = session.createSQLQuery("SELECT * from ADMIN where ADMIN.id = '" + cab.getIdAdmin() + "' ;");
                Admin admin = (Admin) sqlQuery2.addEntity(Admin.class).list().get(0);
                System.out.println(admin.getNom());
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
}
