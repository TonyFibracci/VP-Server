package service;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.Session;

import database.HibernateUtil;
import model.Job;
import model.User;



public class JobService {
	
	public static List<Job> getAllJobs(){
		Session session = HibernateUtil.getSessionFactory().openSession();	
		try {		
			session.beginTransaction();
			Query query = session.createNativeQuery("SELECT * FROM tbl_Job").addEntity(Job.class);
			//session.getTransaction().commit();	
			List<Job> res =  query.getResultList();
			return res;
		} finally {
			session.close();
		}	
	}
	
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
