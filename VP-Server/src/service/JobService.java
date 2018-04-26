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
	
	public static void addJob(Job job) {
		Session session = HibernateUtil.getSessionFactory().openSession();	
		try {		
			session.beginTransaction();
			session.saveOrUpdate(job);
			session.getTransaction().commit();			
		} finally {
			session.close();
		}
	}
	
	public static void deleteJob(int id) {
		Session session = HibernateUtil.getSessionFactory().openSession();	
		try {		
			session.beginTransaction();
			Query query = session.createNativeQuery("DELETE FROM tbl_Job WHERE id = :id");
			query.setParameter("id", id);
			query.executeUpdate();
		} finally {
			session.close();
		}
	}

}
