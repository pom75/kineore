
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


public class KineUserDB {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    public static void saveKineUser(KineUser kineUser) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            session.saveOrUpdate(kineUser);
            trx.commit();
        }
    }

    public static KineUser checkPasswordByTokenKineUser(String token, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from TOKEN where  TOKEN.token = '" + token + "'");
            Token tokenDb = (Token) sqlQuery.addEntity(Token.class).uniqueResult();
            if (tokenDb != null) {
                sqlQuery = session.createSQLQuery("SELECT * from KINE_USER where  KINE_USER.password = '" + password + "' and KINE_USER.id= '" + tokenDb.getId() + "'");
                return (KineUser) sqlQuery.addEntity(KineUser.class).uniqueResult();
            }
        }
        return null;
    }

    public static void removeKineUser(KineUser kineUser) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            session.remove(kineUser);
            trx.commit();
        }
    }

    public static String getTokenKineUser(KineUser kineUser) {
        Token token = new Token(kineUser.getId(), Hashing.sha256().hashString(kineUser.getPassword() + kineUser.getId(), StandardCharsets.UTF_8).toString(), "1");
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            session.saveOrUpdate(token);
            trx.commit();
        }
        return token.getToken();
    }

    public static List<KineUser> getAllCabKineUserByToken(String token) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from TOKEN where  TOKEN.token = '" + token + "'");
            Token tokenDb = (Token) sqlQuery.addEntity(Token.class).uniqueResult();
            List<Colab> colabList = Collections.emptyList();
            Colab colabDb = null;
            if (tokenDb != null) {
                sqlQuery = session.createSQLQuery("SELECT * from COLAB where  COLAB.idKineUser = '" + tokenDb.getAdmin() + "'");//TODO BUG HERE
                colabDb = (Colab) sqlQuery.addEntity(Colab.class).uniqueResult();
            }
            if (colabDb != null) {
                sqlQuery = session.createSQLQuery("SELECT * from COLAB where  COLAB.idCab = '" + colabDb.getIdCab() + "'");
                colabList = sqlQuery.addEntity(Colab.class).list();
            }
            List<KineUser> listKineUser = new LinkedList<>();
            if (!colabList.isEmpty()) {
                for (Colab currentColab : colabList) {
                    sqlQuery = session.createSQLQuery("SELECT * from KINE_USER where  KINE_USER.id = '" + currentColab.getIdKineUser() + "'");
                    KineUser e = (KineUser) sqlQuery.addEntity(KineUser.class).uniqueResult();
                    e.setPassword("");
                    listKineUser.add(e);
                }
            }
            return listKineUser;
        }
    }


    public static Colab getColabByToken(String token) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from TOKEN where  TOKEN.token = '" + token + "'");
            Token tokenDb = (Token) sqlQuery.addEntity(Token.class).uniqueResult();
            if (tokenDb != null) {
                sqlQuery = session.createSQLQuery("SELECT * from COLAB where  COLAB.idKineUser = '" + tokenDb.getId() + "'");
                return (Colab) sqlQuery.addEntity(Colab.class).uniqueResult();
            }
            return null;
        }
    }

    public static Colab getColabByIdKineUser(String idKineUser) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from COLAB where  COLAB.idKineUser = '" + idKineUser+ "'");
            return (Colab) sqlQuery.addEntity(Colab.class).uniqueResult();
        }
    }


    public static KineUser getKineUserByToken(String token) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from TOKEN where  TOKEN.token = '" + token + "'");
            Token tokenDb = (Token) sqlQuery.addEntity(Token.class).uniqueResult();
            if (tokenDb != null) {
                sqlQuery = session.createSQLQuery("SELECT * from KINE_USER where  KINE_USER.id = '" + tokenDb.getId() + "'");
                return (KineUser) sqlQuery.addEntity(KineUser.class).uniqueResult();
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

    public static Colab getColabById(String idColab) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from COLAB WHERE COLAB.id = '" + idColab + "'");
            return(Colab) sqlQuery.addEntity(Colab.class).uniqueResult();
        }
    }

    public static KineUser checkPasswordByEmailKineUser(String email, String password) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from KINE_USER where KINE_USER.password = '" + password + "' and  KINE_USER.email = '" + email + "'");
            return (KineUser) sqlQuery.addEntity(KineUser.class).uniqueResult();
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

    public static KineUser getKineUserByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from KINE_USER where  KINE_USER.email = '" + email + "'");
            return (KineUser) sqlQuery.addEntity(KineUser.class).uniqueResult();
        }
    }

}
