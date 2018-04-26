package service;

import org.hibernate.Session;

import database.HibernateUtil;
import model.Upload;


public class UploadService {

	public static void addUpload(Upload upload) {
		Session session = HibernateUtil.getSessionFactory().openSession();	
		try {		
			session.beginTransaction();
			session.saveOrUpdate(upload);
			session.getTransaction().commit();			
		} finally {
			session.close();
		}
	}
}
