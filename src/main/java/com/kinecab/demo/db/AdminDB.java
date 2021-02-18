
package com.kinecab.demo.db;

import java.nio.charset.StandardCharsets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.google.common.hash.Hashing;

import com.kinecab.demo.db.entity.*;
import com.kinecab.demo.util.HibernateUtil;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;

import javax.persistence.NoResultException;


public class AdminDB {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    public static void saveAdmin(Admin admin) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            session.saveOrUpdate(admin);
            trx.commit();
        }
    }

    public static Admin checkPasswordByTokenAdmin(String token, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from TOKEN where  TOKEN.token = '" + token + "'");
            Token tokenDb = (Token) sqlQuery.addEntity(Token.class).uniqueResult();
            if (tokenDb != null) {
                sqlQuery = session.createSQLQuery("SELECT * from ADMIN where  ADMIN.password = '" + password + "' and ADMIN.id= '" + tokenDb.getId() + "'");
                return (Admin) sqlQuery.addEntity(Admin.class).uniqueResult();
            }
        }
        return null;
    }

    public static void removeAdmin(Admin admin) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            session.remove(admin);
            trx.commit();
        }
    }

    public static String getTokenAdmin(Admin admin) {
        Token token = new Token(admin.getId(), Hashing.sha256().hashString(admin.getPassword() + admin.getId(), StandardCharsets.UTF_8).toString(), "1");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            session.saveOrUpdate(token);
            trx.commit();
        }
        return token.getToken();
    }

    public static List<Admin> getAllCabAdminByToken(String token) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from TOKEN where  TOKEN.token = '" + token + "'");
            Token tokenDb = (Token) sqlQuery.addEntity(Token.class).uniqueResult();
            List<Colab> colabList = Collections.emptyList();
            Colab colabDb = null;
            if (tokenDb != null) {
                sqlQuery = session.createSQLQuery("SELECT * from COLAB where  COLAB.idAdmin = '" + tokenDb.getAdmin() + "'");
                colabDb = (Colab) sqlQuery.addEntity(Colab.class).uniqueResult();
            }
            if (colabDb != null) {
                sqlQuery = session.createSQLQuery("SELECT * from COLAB where  COLAB.idCab = '" + colabDb.getIdCab() + "'");
                colabList = sqlQuery.addEntity(Colab.class).list();
            }
            List<Admin> listAdmin = new LinkedList<>();
            if (!colabList.isEmpty()) {
                for (Colab currentColab : colabList) {
                    sqlQuery = session.createSQLQuery("SELECT * from ADMIN where  ADMIN.id = '" + currentColab.getIdAdmin() + "'");
                    Admin e = (Admin) sqlQuery.addEntity(Admin.class).uniqueResult();
                    e.setPassword("");
                    listAdmin.add(e);
                }
            }
            return listAdmin;
        }
    }


    public static Colab getColabByToken(String token) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from TOKEN where  TOKEN.token = '" + token + "'");
            Token tokenDb = (Token) sqlQuery.addEntity(Token.class).uniqueResult();
            if (tokenDb != null) {
                sqlQuery = session.createSQLQuery("SELECT * from COLAB where  COLAB.idAdmin = '" + tokenDb.getId() + "'");
                return (Colab) sqlQuery.addEntity(Colab.class).uniqueResult();
            }
            return null;
        }
    }

    public static Colab getColabByIdAdmin(String idAdmin) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from COLAB where  COLAB.idAdmin = '" + idAdmin+ "'");
            return (Colab) sqlQuery.addEntity(Colab.class).uniqueResult();
        }
    }


    public static Admin getAdminByToken(String token) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from TOKEN where  TOKEN.token = '" + token + "'");
            Token tokenDb = (Token) sqlQuery.addEntity(Token.class).uniqueResult();
            if (tokenDb != null) {
                sqlQuery = session.createSQLQuery("SELECT * from ADMIN where  ADMIN.id = '" + tokenDb.getId() + "'");
                return (Admin) sqlQuery.addEntity(Admin.class).uniqueResult();
            }
            return null;
        }
    }

    public static List<Colab> getColabs() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from COLAB ;");
            return sqlQuery.addEntity(Colab.class).list();
        }
    }

    public static Admin checkPasswordByEmailAdmin(String email, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from ADMIN where ADMIN.password = '" + password + "' and  ADMIN.email = '" + email + "'");
            return (Admin) sqlQuery.addEntity(Admin.class).uniqueResult();
        }
    }

    public static List<Person> getPersonByIdCab(int idCab) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Person> people = new ArrayList<>();
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * FROM CAB_PERSON WHERE  CAB_PERSON.idCab = '" + idCab + "'");
            List<CabPerson> cabPerson = (List<CabPerson>) sqlQuery.addEntity(CabPerson.class).list();
            try {
                cabPerson.forEach(person -> {
                    final NativeQuery sqlQuery1 = session.createSQLQuery("SELECT * FROM PERSON WHERE  PERSON.id = '" + person.getIdPerson() + "'");
                    final Person singleResult = (Person) sqlQuery1.addEntity(Person.class).getSingleResult();
                    singleResult.setPassword("");
                    people.add(singleResult);
                });
            } catch (NoResultException nre) {
                //Ignore this because as per your logic this is ok!
            }
            return people;
        }
    }

    public static Person getPersonByIdCabIdPerson(int idCab, String idPerson) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            final Person[] personResult = {null};
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * FROM CAB_PERSON WHERE  CAB_PERSON.idCab = '" + idCab + "'");
            List<CabPerson> cabPerson = (List<CabPerson>) sqlQuery.addEntity(CabPerson.class).list();
            try {
                cabPerson.stream().forEach(person -> {
                    if (idPerson.contentEquals(person.getIdPerson() + "")) {
                        final NativeQuery sqlQuery1 = session.createSQLQuery("SELECT * FROM PERSON WHERE  PERSON.id = '" + person.getIdPerson() + "'");
                        Person singleResult = (Person) sqlQuery1.addEntity(Person.class).uniqueResult();
                        personResult[0] = singleResult;
                    }
                });
            } catch (NoResultException nre) {
                //Ignore this because as per your logic this is ok!
            }
            return personResult[0];
        }
    }

    public static Admin getAdminByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from ADMIN where  ADMIN.email = '" + email + "'");
            return (Admin) sqlQuery.addEntity(Admin.class).uniqueResult();
        }
    }

}
