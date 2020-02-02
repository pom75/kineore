
package com.kinecab.demo.db;

import com.google.common.hash.Hashing;
import com.kinecab.demo.db.entity.*;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;


public class LoginDB {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Static fields/initializers
    //~ ----------------------------------------------------------------------------------------------------------------

    public static PasswordGenerator passwordGenerator = new PasswordGenerator.PasswordGeneratorBuilder().useDigits(true).useLower(true).useUpper(true).build();

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    public static void savePersonTemp(PersonTemp person) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            session.saveOrUpdate(person);
            trx.commit();
        }
    }

    public static void deletePersonTemp(PersonTemp person) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            session.delete(person);
            trx.commit();
        }
    }

    public static void savePerson(Person person) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            session.saveOrUpdate(person);
            trx.commit();
        }
    }

    public static void saveAdmin(Admin admin) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            session.saveOrUpdate(admin);
            trx.commit();
        }
    }

    public static List<Person> checkPasswordByEmailPerson(String email, String password) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from PERSON where PERSON.password = '" + password + "' and  PERSON.email = '" + email + "';");
            return sqlQuery.addEntity(Person.class).list();
        }
    }

    public static List<Admin> checkPasswordByEmailAdmin(String email, String password) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from ADMIN where ADMIN.password = '" + password + "' and  ADMIN.email = '" + email + "';");
            return sqlQuery.addEntity(Admin.class).list();
        }
    }

    public static List<Person> checkPasswordByTokenPerson(String token, String password) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from TOKEN where  TOKEN.token = '" + token + "';");
            List<Token> list = sqlQuery.addEntity(Token.class).list();
            if (!list.isEmpty()) {
                sqlQuery = session.createSQLQuery("SELECT * from PERSON where  PERSON.password = '" + password + "' and PERSON.id= '" + list.get(0).getId() + "';");
                return sqlQuery.addEntity(Person.class).list();
            }
        }
        return Collections.EMPTY_LIST;
    }

    public static List<Admin> checkPasswordByTokenAdmin(String token, String password) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from TOKEN where  TOKEN.token = '" + token + "';");
            List<Token> list = sqlQuery.addEntity(Token.class).list();
            if (!list.isEmpty()) {
                sqlQuery = session.createSQLQuery("SELECT * from ADMIN where  ADMIN.password = '" + password + "' and ADMIN.id= '" + list.get(0).getId() + "';");
                return sqlQuery.addEntity(Admin.class).list();
            }
        }
        return Collections.EMPTY_LIST;
    }

    public static List<Person> getPersonByEmail(String email) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from PERSON where  PERSON.email = '" + email + "';");
            return sqlQuery.addEntity(Person.class).list();
        }
    }

    public static List<PersonTemp> tempTokenExist(String token) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from PERSON_TEMP where  PERSON_TEMP.token = '" + token + "';");
            return sqlQuery.addEntity(PersonTemp.class).list();
        }
    }

    public static void removePerson(Person person) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            session.remove(person);
            trx.commit();
        }
    }

    public static void removeAdmin(Admin admin) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            session.remove(admin);
            trx.commit();
        }
    }

    public static void removeToken(Token token) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            session.remove(token);
            trx.commit();
        }
    }

    public static String newPassword(Person person) {
        String password = passwordGenerator.generate(8);
        person.setPassword(password);
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction tx = session.beginTransaction();
            session.update(person);
            tx.commit();
        } catch (Exception e) {
            return "";
        }
        return password;
    }

    public static String getTokenPerson(Person person) {
        Token token = new Token(person.getId(), Hashing.sha256().hashString(person.getPassword() + person.getId(), StandardCharsets.UTF_8).toString(), "0");
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            session.saveOrUpdate(token);
            trx.commit();
        }
        return token.getToken();
    }

    public static String getTokenAdmin(Admin admin) {
        Token token = new Token(admin.getId(), Hashing.sha256().hashString(admin.getPassword() + admin.getId(), StandardCharsets.UTF_8).toString(), "1");
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            Transaction trx = session.beginTransaction();
            session.saveOrUpdate(token);
            trx.commit();
        }
        return token.getToken();
    }

    public static List<Person> getPersonByToken(String token) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from TOKEN where  TOKEN.token = '" + token + "';");
            List<Token> list = sqlQuery.addEntity(Token.class).list();
            if (!list.isEmpty()) {
                sqlQuery = session.createSQLQuery("SELECT * from PERSON where  PERSON.id = '" + list.get(0).getId() + "';");
                return sqlQuery.addEntity(Person.class).list();
            }
            return Collections.emptyList();
        }
    }

    public static List<Admin> getAdminByToken(String token) {
        try(Session session = HibernateUtil.getSessionFactory().openSession()) {
            NativeQuery sqlQuery = session.createSQLQuery("SELECT * from TOKEN where  TOKEN.token = '" + token + "';");
            List<Token> list = sqlQuery.addEntity(Token.class).list();
            if (!list.isEmpty()) {
                sqlQuery = session.createSQLQuery("SELECT * from ADMIN where  ADMIN.id = '" + list.get(0).getId() + "';");
                return sqlQuery.addEntity(Admin.class).list();
            }
            return Collections.emptyList();
        }
    }

}
