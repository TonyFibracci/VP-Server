package database;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class HibernateUtil {
	
	private static SessionFactory sessionFactory;
	
	private HibernateUtil() {
		
	}
	
	public static synchronized SessionFactory getSessionFactory() {
		if(sessionFactory == null) {
			try {
				sessionFactory = new Configuration()
						.configure("hibernate.cfg.xml")
						.buildSessionFactory();
			} catch (HibernateException e) {
				e.printStackTrace();
			}
		}
		return sessionFactory;
	}
	
	public static void closeSessionFactory() {
		if(sessionFactory != null)
			sessionFactory.close();
	}

}
