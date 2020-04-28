
package com.kinecab.demo.util;

import java.util.Properties;

import com.kinecab.demo.db.entity.*;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;


public class HibernateUtil {

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Static fields/initializers
    //~ ----------------------------------------------------------------------------------------------------------------

    private static SessionFactory sessionFactory;

    //~ ----------------------------------------------------------------------------------------------------------------
    //~ Methods
    //~ ----------------------------------------------------------------------------------------------------------------

    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            try {
                Configuration configuration = new Configuration();
                // Hibernate settings equivalent to hibernate.cfg.xml's properties
                Properties settings = new Properties();
                settings.put(Environment.DRIVER, "com.mysql.jdbc.Driver");
                settings.put(Environment.URL, "jdbc:mysql://localhost:3306/kinecab");
                settings.put(Environment.USER, "administrator");
                settings.put(Environment.PASS, "Kinecab5!");
                settings.put(Environment.DIALECT, "org.hibernate.dialect.MySQL5Dialect");
                settings.put(Environment.CURRENT_SESSION_CONTEXT_CLASS, "thread");
                settings.put(Environment.HBM2DDL_AUTO, "validate");
                settings.put(Environment.AUTOCOMMIT, "true");
                settings.put(Environment.C3P0_MIN_SIZE, 5); //TODO add utf8 unicode
                settings.put(Environment.C3P0_MAX_SIZE, 51);
                settings.put("hibernate.c3p0.testConnectionOnCheckOut", "true");
                settings.put(Environment.C3P0_ACQUIRE_INCREMENT, 20);
                settings.put(Environment.C3P0_TIMEOUT, 1800);
                settings.put(Environment.C3P0_MAX_STATEMENTS, 50);
                settings.put(Environment.C3P0_IDLE_TEST_PERIOD, 3000);
                configuration.setProperties(settings);
                configuration.addAnnotatedClass(Admin.class);
                configuration.addAnnotatedClass(Cab.class);
                configuration.addAnnotatedClass(CabAdmin.class);
                configuration.addAnnotatedClass(CabPerson.class);
                configuration.addAnnotatedClass(Event.class);
                configuration.addAnnotatedClass(Person.class);
                configuration.addAnnotatedClass(PersonTemp.class);
                configuration.addAnnotatedClass(Token.class);
                configuration.addAnnotatedClass(MotifCab.class);
                configuration.addAnnotatedClass(MotifAdmin.class);
                ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }
}
