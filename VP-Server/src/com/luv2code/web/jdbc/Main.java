package com.luv2code.web.jdbc;

import javax.persistence.Query;

import org.hibernate.Session;

import database.HibernateUtil;
import service.JobService;

public class Main {

	public static void main(String[] args) {
		Session session = HibernateUtil.getSessionFactory().openSession();
		Query queryIsAdmin = session.createNativeQuery("SELECT admin FROM map_User WHERE ID = :user");
		queryIsAdmin.setParameter("user", "DESLU001");
		boolean admin = (boolean) queryIsAdmin.getResultList().get(0);
		System.out.println(admin);
	}

}
