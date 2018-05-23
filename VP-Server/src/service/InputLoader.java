package service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.Transaction;

import database.HibernateUtil;
import model.InputMessage;

public class InputLoader {
	
	private InputMessage inputMessage;
	private String tableName;
	
	public InputLoader(InputMessage message) throws IOException {
		this.inputMessage = message;
		tableName = OutputWriter.INPUT_PREFIX + inputMessage.getUserName();
		createTable();
		XSSFWorkbook workbook = new XSSFWorkbook(inputMessage.getInputPath());
		XSSFSheet sheet = workbook.getSheetAt(0);
		int numberOfRows = sheet.getPhysicalNumberOfRows();
//		List<String> sheetNames = new ArrayList<String>();
		Session session = HibernateUtil.getSessionFactory().openSession();	
		Transaction tx = session.beginTransaction();
		for (int i = 1; i < numberOfRows; i++) {
			XSSFRow row = sheet.getRow(i);
			String isin = row.getCell(1).getStringCellValue();
			String toP = row.getCell(4).getStringCellValue();
			double valuatioQuote = row.getCell(5).getNumericCellValue();
			double nominal = row.getCell(6).getNumericCellValue();
			String shortLong = row.getCell(7).getStringCellValue();
			String queryString = "INSERT INTO "
					+ tableName + 
					" (SecID, ISIN, [Valuation quote], Nominal, [Type of Price], ShortLongFlag) VALUES (NULL,'" + 
					isin + "'," +
					valuatioQuote + "," +
					nominal + ",'" + 
					toP + "','" + 
					shortLong + "')";
			
			Query query = session.createNativeQuery(queryString);
			query.executeUpdate();
			session.flush();
			session.clear();
			
		}
		tx.commit();
		session.close();
		mapSecId();
		defineNewSecID();
		mapSecId();
	}
	
	private void createTable() {
		String queryString = "IF (NOT EXISTS (SELECT * " + 
				"FROM INFORMATION_SCHEMA.TABLES " + 
				"WHERE TABLE_SCHEMA = 'dbo' " + 
				" AND TABLE_NAME = '"
				+ tableName
				+ "')) " + 
				"BEGIN " + 
				"CREATE Table "
				+ tableName
				+ " (SecID int, ISIN varchar(12), [Valuation quote] float, Nominal float, [Type of Price] varchar(10), ShortLongFlag varchar(10)) " + 
				"END ELSE DELETE FROM " +
				tableName;
		Session session = HibernateUtil.getSessionFactory().openSession();	
		try {		
			session.beginTransaction();
			Query query = session.createNativeQuery(queryString);
			query.executeUpdate();
		} finally {
			session.close();
		}
	}
	
	private void mapSecId() {
		String queryString = "UPDATE a SET a.SecID = b.SecID FROM " + tableName + " a INNER JOIN map_secID b ON a.ISIN = b.ISIN";
		Session session = HibernateUtil.getSessionFactory().openSession();	
		try {		
			session.beginTransaction();
			Query query = session.createNativeQuery(queryString);
			query.executeUpdate();
		} finally {
			session.close();
		}
	}
	
	private void defineNewSecID() {
		String queryString = "SELECT MAX(SecID) FROM map_secID";
		String queryString2 = "SELECT ISIN FROM " + tableName + " WHERE SecID is null";
		Session session = HibernateUtil.getSessionFactory().openSession();	
		Transaction tx = session.beginTransaction();
		try {		
			Query query = session.createNativeQuery(queryString);
			List<Integer> lastSecID = query.getResultList();
			
			Query query2 = session.createNativeQuery(queryString2);
			List<String> isins = query2.getResultList();
			
			int newSecID = lastSecID.get(0) + 1;
			String timeStamp = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(Calendar.getInstance().getTime());
			for(String isin : isins) {
				String queryString3 = "INSERT INTO map_secID (SecID, ISIN, CUSIP, WKN, SEDOL, DatePrint, Inactive) VALUES (" 
										+ newSecID + ",'"
										+ isin + "',NULL,NULL,NULL,'" 
										+ timeStamp + "',0)";
				Query query3 = session.createNativeQuery(queryString3);
				query3.executeUpdate();
				newSecID++;
			}
		} finally {
			session.close();
		}
		
	}
	

}
