package service;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.Session;

import database.HibernateUtil;
import model.User;

public class UserService {
	
	public static List<User> getAllUsers(){
		Session session = HibernateUtil.getSessionFactory().openSession();	
		try {		
			session.beginTransaction();
			Query query = session.createNativeQuery("SELECT * FROM map_User").addEntity(User.class);
			//session.getTransaction().commit();	
			List<User> res =  query.getResultList();
			return res;
		} finally {
			session.close();
		}	
	}

}
