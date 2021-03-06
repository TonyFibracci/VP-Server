package service;

import java.util.List;

import javax.persistence.Query;

import org.hibernate.Session;

import database.HibernateUtil;
import model.UploadJsonObject;



public class UploadService {

	public static void addUpload(UploadJsonObject upload) {
		Session session = HibernateUtil.getSessionFactory().openSession();	
		try {		
			session.beginTransaction();
			session.saveOrUpdate(upload);
			session.getTransaction().commit();			
		} finally {
			session.close();
		}
	}
	
	public static List<UploadJsonObject> getUploadsByJobId(int jobId){
		Session session = HibernateUtil.getSessionFactory().openSession();	
		try {		
			session.beginTransaction();
			Query query = session.createNativeQuery("SELECT * FROM tbl_upload WHERE jobId = :jobId").addEntity(UploadJsonObject.class);
			query.setParameter("jobId", jobId);
			@SuppressWarnings("unchecked")
			List<UploadJsonObject> res =  query.getResultList();
			return res;
		} finally {
			session.close();
		}	
	}
}
