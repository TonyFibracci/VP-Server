package com.luv2code.web.jdbc;

import database.HibernateUtil;
import service.JobService;

public class Main {

	public static void main(String[] args) {
		HibernateUtil.getSessionFactory();
		System.out.println(JobService.getAllJobs());
	}

}
