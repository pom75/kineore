
package com.kinecab.demo.util;


import com.kinecab.demo.db.entity.*;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


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
                configuration.addAnnotatedClass(KineUser.class);
                configuration.addAnnotatedClass(Cab.class);
                configuration.addAnnotatedClass(CabPerson.class);
                configuration.addAnnotatedClass(Event.class);
                configuration.addAnnotatedClass(Person.class);
                configuration.addAnnotatedClass(PersonTemp.class);
                configuration.addAnnotatedClass(Token.class);
                configuration.addAnnotatedClass(MotifCab.class);
                configuration.addAnnotatedClass(MotifColab.class);
                configuration.addAnnotatedClass(Colab.class);

//                Context context=new InitialContext();
//                Context environmentContext=(Context) context.lookup("java:comp/env");
                sessionFactory = configuration.configure().buildSessionFactory();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return sessionFactory;
    }
}
