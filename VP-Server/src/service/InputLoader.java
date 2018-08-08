package service;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.persistence.Query;

import org.apache.poi.ss.usermodel.DataFormatter;
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
	
	public InputLoader(InputMessage message, InputStream inputStream) throws Exception {
		try {
			this.inputMessage = message;
			tableName = OutputWriter.INPUT_PREFIX + inputMessage.getUserName();
			createTable();
			XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
			XSSFSheet sheet = workbook.getSheetAt(0);
			int numberOfRows = sheet.getPhysicalNumberOfRows();
	//		List<String> sheetNames = new ArrayList<String>();
			Session session = HibernateUtil.getSessionFactory().openSession();	
			Transaction tx = session.beginTransaction();
			System.out.println("total: " + numberOfRows);
			
			int numberOfColumns = sheet.getRow(0).getPhysicalNumberOfCells();
			List<String> originalColumnNames = new ArrayList<String>();
			for(int i = 0; i < numberOfColumns; i++)
				originalColumnNames.add(sheet.getRow(0).getCell(i).getStringCellValue());
			
			createClientDataTable(originalColumnNames);
			
			insertRows(sheet, numberOfRows, session);
			insertRowsCL(sheet, numberOfRows, session, originalColumnNames);
			session.flush();
			session.clear();
			tx.commit();
			session.close();
			mapSecId();
			defineNewSecID();
			mapSecId();
			updateActiveJob();
		} catch (Exception e) {
			throw e;
		}
	}

	private void insertRows(XSSFSheet sheet, int numberOfRows, Session session) {
		for (int i = 1; i < numberOfRows; i++) {
			System.out.println("Line " + i);
			XSSFRow row = sheet.getRow(i);
			//skip empty rows
			if(row.getCell(0) == null)
				continue;
			int eyNr = (int) row.getCell(0).getNumericCellValue();
			String isin = row.getCell(1).getStringCellValue();
			String description = ""; 
			if(row.getCell(2) != null) {
				description = row.getCell(2).getStringCellValue();
				description = description.replace("'", "");
			}
			String toP = row.getCell(4).getStringCellValue();
			double valuatioQuote = row.getCell(5).getNumericCellValue();
			double nominal = row.getCell(6).getNumericCellValue();
			String shortLong = row.getCell(7).getStringCellValue();
			String eyComment = "";
			if(row.getCell(10) != null)
				eyComment = row.getCell(10).getStringCellValue();
			String queryString = "INSERT INTO " +
					tableName + 
					" (EY_No, SecID, ISIN, [Client Security Description], [Valuation quote], Nominal, [Type of Price], ShortLongFlag, [EY_Comment]) VALUES (" +
					eyNr + ",NULL,'" + 
					isin + "','" +
					description + "'," +
					valuatioQuote + "," +
					nominal + ",'" + 
					toP + "','" + 
					shortLong + "','" +
					eyComment + "')";
			
			Query query = session.createNativeQuery(queryString);
			query.executeUpdate();
		}
	}
	
	private void insertRowsCL(XSSFSheet sheet, int numberOfRows, Session session, List<String> columnNames) {
		for (int i = 1; i < numberOfRows; i++) {
			XSSFRow row = sheet.getRow(i);
			//skip empty rows
			if(row.getCell(0) == null)
				continue;
			int numberOfColumns = row.getPhysicalNumberOfCells();
			StringBuilder sb = new StringBuilder();
			String tableName = OutputWriter.INPUT_PREFIX + "CL_" + inputMessage.getUserName();
			sb.append("INSERT INTO ").append(tableName).append(" ([CL EY_No],");
			String comma = "";
			for(int j = 9; j < numberOfColumns; j++) {
				sb.append(comma);
				comma = ",";
				sb.append("[CL ");
				sb.append(columnNames.get(j)).append("]");
			}
			comma = "";
			sb.append(") VALUES(");
			//EY_No as first col
			sb.append(row.getCell(0).getNumericCellValue()).append(",");
			for(int j = 9; j < numberOfColumns; j++) {
				sb.append(comma);
				comma = ",";
				DataFormatter formatter = new DataFormatter();
				String value = formatter.formatCellValue(row.getCell(j));
				value = value.replace("'", "");
				sb.append("'").append(value).append("'");
				
			}
			sb.append(")");
			System.out.println(sb.toString());
			Query query = session.createNativeQuery(sb.toString());
			query.executeUpdate();
		}
	}
	
	private void createClientDataTable(List<String> clientCols) {
		StringBuilder sb = new StringBuilder();
		String tableName = OutputWriter.INPUT_PREFIX + "CL_" + inputMessage.getUserName();
		sb.append("CREATE TABLE ").append(tableName).append(" (");	
		sb.append("[CL EY_No] int,");
		String comma = "";
		for(int i = 9; i < clientCols.size(); i++) {
			sb.append(comma);
			comma = ",";
			sb.append("[CL ");
			sb.append(clientCols.get(i)).append("]");
			sb.append(" nvarchar(255) ");
		}
		sb.append(")");
		
		String dropTableSqlString = "IF (EXISTS (SELECT * " + 
				"FROM INFORMATION_SCHEMA.TABLES " + 
				"WHERE TABLE_SCHEMA = 'dbo' " + 
				" AND TABLE_NAME = '"
				+ tableName
				+ "')) " + 
				"BEGIN " + 
				"DROP Table "
				+ tableName + 
				" END";
		
		Session session = HibernateUtil.getSessionFactory().openSession();	
		try {		
			session.beginTransaction();
			Query query = session.createNativeQuery(dropTableSqlString);
			query.executeUpdate();
			query = session.createNativeQuery(sb.toString());
			query.executeUpdate();
		} finally {
			session.close();
		}
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
				+ " (EY_No int, SecID int, ISIN varchar(12), [Client Security Description] nvarchar(255), [Valuation quote] float, Nominal float, [Type of Price] varchar(10), ShortLongFlag varchar(10), EY_Comment nvarchar(255)) " + 
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
	
	private void updateActiveJob() {
		String queryString = "IF NOT EXISTS (SELECT username FROM tbl_UserActiveJob WHERE username = '"
				+ inputMessage.getUserName() + "') "
				+ "INSERT INTO tbl_UserActiveJob (username, jobId) VALUES('"
				+ inputMessage.getUserName() + "', "
				+ inputMessage.getJob().getId() + ") "
				+ "ELSE UPDATE tbl_UserActiveJob SET jobId = " + inputMessage.getJob().getId()
				+ " WHERE username = '" + inputMessage.getUserName() + "'";
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
	
	public static String getActiveInput(String userName) {
		String queryString = "SELECT b.client FROM tbl_UserActiveJob u INNER JOIN tbl_job j ON u.jobId = j.id INNER JOIN tbl_client b ON j.client = b.id WHERE u.username = '" + userName + "'" ;
		Session session = HibernateUtil.getSessionFactory().openSession();	
		try {		
			session.beginTransaction();
			Query query = session.createNativeQuery(queryString);
			List<Object> resList = query.getResultList();
			if(resList.size() > 0)
				return (String) query.getResultList().get(0);
			else
				return null;
		} finally {
			session.close();
		}
	}
	

}
