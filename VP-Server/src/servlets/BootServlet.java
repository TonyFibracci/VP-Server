package servlets;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import database.HibernateUtil;

public class BootServlet implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		HibernateUtil.getSessionFactory();		
	}

}
