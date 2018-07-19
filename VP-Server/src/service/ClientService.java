package service;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.Session;

import database.HibernateUtil;
import model.Job;
import model.User;
import model.VPClient;



public class ClientService {
	
	public static List<Job> getAllJobs(String clientId){
		Session session = HibernateUtil.getSessionFactory().openSession();	
		try {		
			session.beginTransaction();
			
			Query query = session.createNativeQuery("SELECT * FROM tbl_Job WHERE client = :client").addEntity(Job.class);
			query.setParameter("client", clientId);
			List<Job> res =  query.getResultList();
			return res;
			
			
		} finally {
			session.close();
		}	
	}
	
	public static List<VPClient> getAllClients(){
		Session session = HibernateUtil.getSessionFactory().openSession();	
		try {		
			session.beginTransaction();
			Query query = session.createNativeQuery("SELECT * FROM tbl_client ORDER BY client").addEntity(VPClient.class);
			//session.getTransaction().commit();	
			List<VPClient> res =  query.getResultList();
			return res;
		} finally {
			session.close();
		}	
	}
	
	public static List<String> getAllCountries(){
		Session session = HibernateUtil.getSessionFactory().openSession();	
		try {		
			session.beginTransaction();
			Query query = session.createNativeQuery("SELECT English FROM map_CountryISO ORDER BY English");
			//session.getTransaction().commit();	
			List<String> res =  query.getResultList();
			return res;
		} finally {
			session.close();
		}	
	}
	
	public static void addClient(VPClient client) {
		Session session = HibernateUtil.getSessionFactory().openSession();	
		try {		
			session.beginTransaction();
			session.saveOrUpdate(client);
			session.getTransaction().commit();			
		} finally {
			session.close();
		}
	}
	
	public static void deleteClient(int id) {
		Session session = HibernateUtil.getSessionFactory().openSession();	
		try {		
			session.beginTransaction();
			Query query = session.createNativeQuery("DELETE FROM tbl_client WHERE id = :id");
			query.setParameter("id", id);
			query.executeUpdate();
		} finally {
			session.close();
		}
	}

}
