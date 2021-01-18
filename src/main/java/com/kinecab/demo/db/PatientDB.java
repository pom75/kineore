package com.kinecab.demo.db;

import com.kinecab.demo.db.entity.Person;
import com.kinecab.demo.db.entity.Token;
import com.kinecab.demo.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;

import java.util.List;

public class PatientDB {
    public static Person getPatientByToken(String token) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from TOKEN where  TOKEN.token = '" + token + "'");
            Token tokenDb = (Token) sqlQuery.addEntity(Token.class).uniqueResult();
            if (tokenDb != null) {
                sqlQuery = session.createSQLQuery("SELECT * from PERSON where  PERSON.id = '" + tokenDb.getId() + "'");
                return (Person) sqlQuery.addEntity(Person.class).uniqueResult();
            }
            return null;
        }
    }

    public static Person getPatientById(String id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from PERSON where  PERSON.id = '" + id + "'");
            return (Person) sqlQuery.addEntity(Person.class).uniqueResult();
        }
    }

    public static boolean emailExist(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from PERSON where  PERSON.email = '" + email + "'");
            return  sqlQuery.addEntity(Person.class).uniqueResult() != null;
        }
    }

}
