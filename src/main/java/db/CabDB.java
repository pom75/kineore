
package db;

import java.util.List;

import db.entity.Cab;

import db.entity.Person;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;


public class CabDB {

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
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from CAB where CAB.id = '" + id + "' ;");
            return sqlQuery.addEntity(Cab.class).list();
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
