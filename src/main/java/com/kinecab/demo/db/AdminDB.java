
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

    public static List<Admin> checkPasswordByTokenAdmin(String token, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from TOKEN where  TOKEN.token = '" + token + "'");
            List<Token> list = sqlQuery.addEntity(Token.class).list();
            if (!list.isEmpty()) {
                sqlQuery = session.createSQLQuery("SELECT * from ADMIN where  ADMIN.password = '" + password + "' and ADMIN.id= '" + list.get(0).getId() + "'");
                return sqlQuery.addEntity(Admin.class).list();
            }
        }
        return Collections.EMPTY_LIST;
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
            List<Token> tokenList = sqlQuery.addEntity(Token.class).list();
            List<Colab> colabList = null;
            if (!tokenList.isEmpty()) {
                sqlQuery = session.createSQLQuery("SELECT * from COLAB where  COLAB.idAdmin = '" + tokenList.get(0).getAdmin() + "'");
                colabList = sqlQuery.addEntity(Colab.class).list();
            }
            if (!colabList.isEmpty()) {
                sqlQuery = session.createSQLQuery("SELECT * from Colab where  Colab.idCab = '" + colabList.get(0).getIdCab() + "'");
                colabList = sqlQuery.addEntity(Colab.class).list();
            }
            List<Admin> listAdmin = new LinkedList<>();
            if (!colabList.isEmpty()) {
                for (Colab currentColab : colabList) {
                    sqlQuery = session.createSQLQuery("SELECT * from Admin where  Admin.id = '" + currentColab.getIdAdmin() + "'");
                    Admin e = (Admin) sqlQuery.addEntity(Admin.class).uniqueResult();
                    e.setPassword("");
                    listAdmin.add(e);
                }
            }
            return listAdmin;
        }
    }


    public static List<Colab> getColabByToken(String token) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from TOKEN where  TOKEN.token = '" + token + "'");
            List<Token> list = sqlQuery.addEntity(Token.class).list();
            if (!list.isEmpty()) {
                sqlQuery = session.createSQLQuery("SELECT * from COLAB where  COLAB.idAdmin = '" + list.get(0).getId() + "'");
                return sqlQuery.addEntity(Colab.class).list();
            }
            return Collections.emptyList();
        }
    }

    public static Colab getColabByIdAdmin(String idAdmin) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from COLAB where  COLAB.idAdmin = '" + idAdmin+ "'");
            return (Colab) sqlQuery.addEntity(Colab.class).uniqueResult();
        }
    }


    public static List<Admin> getAdminByToken(String token) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from TOKEN where  TOKEN.token = '" + token + "'");
            List<Token> list = sqlQuery.addEntity(Token.class).list();
            if (!list.isEmpty()) {
                sqlQuery = session.createSQLQuery("SELECT * from ADMIN where  ADMIN.id = '" + list.get(0).getId() + "'");
                return sqlQuery.addEntity(Admin.class).list();
            }
            return Collections.emptyList();
        }
    }

    public static List<Colab> getColabs() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from COLAB ;");
            return sqlQuery.addEntity(Colab.class).list();
        }
    }

    public static List<Admin> checkPasswordByEmailAdmin(String email, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from ADMIN where ADMIN.password = '" + password + "' and  ADMIN.email = '" + email + "'");
            return sqlQuery.addEntity(Admin.class).list();
        }
    }

    public static List<Person> getPersonByIdCab(int idCab) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<Person> people = new ArrayList<>();
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * FROM CAB_PERSON WHERE  CAB_PERSON.idCab = '" + idCab + "'");
            List<CabPerson> cabPerson = (List<CabPerson>) sqlQuery.addEntity(CabPerson.class).list();
            try {
                cabPerson.stream().forEach(person -> {
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
                        Person singleResult = (Person) sqlQuery1.addEntity(Person.class).getSingleResult();
                        personResult[0] = singleResult;
                    }
                });
            } catch (NoResultException nre) {
                //Ignore this because as per your logic this is ok!
            }
            return personResult[0];
        }
    }

    public static List<Admin> getAdminByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from ADMIN where  ADMIN.email = '" + email + "'");
            return sqlQuery.addEntity(Admin.class).list();
        }
    }

}
