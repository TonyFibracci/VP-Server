package service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.Query;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.Transaction;

import database.HibernateUtil;
import database.JDBCUtil;
import model.GlobalConstants;

public class ImportService {
	
	public static void importISP(InputStream inputStream) throws IOException {
		XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
		XSSFSheet sheet = workbook.getSheetAt(0);
		int numberOfRows = sheet.getPhysicalNumberOfRows();
		Session session = HibernateUtil.getSessionFactory().openSession();	
		Transaction tx = session.beginTransaction();
		int lastRowNr = numberOfRows - 2 + 5;
		for (int i = 5; i < lastRowNr; i++) {
			XSSFRow row = sheet.getRow(i);
			String category = row.getCell(0).getStringCellValue();
			String ISIN = row.getCell(1).getStringCellValue();
			String localCurrency = row.getCell(3).getStringCellValue();
			String targetCurrency = row.getCell(17).getStringCellValue();
			String pricingSource = row.getCell(5).getStringCellValue();
			String valuationDate = row.getCell(6).getStringCellValue();
			String idcBid = row.getCell(11).getStringCellValue();
			String idcClosing = row.getCell(12).getStringCellValue();
			String markitBid = row.getCell(13).getStringCellValue();
			String markitClosing = row.getCell(14).getStringCellValue();
			String trBid = row.getCell(15).getStringCellValue();
			String trClosing = row.getCell(16).getStringCellValue();
			
			if(!localCurrency.equalsIgnoreCase(targetCurrency) && (
					category.equalsIgnoreCase("Government Bonds") || 
					category.equalsIgnoreCase("Corporate Bonds") ||
					category.equalsIgnoreCase("Securitized Products") ||
					category.equalsIgnoreCase("Loans") ||
					category.equalsIgnoreCase("Municipal Bonds"))) {
				continue;	
			}
			
			if(!targetCurrency.equalsIgnoreCase("EUR") && (
					category.equalsIgnoreCase("Equities") ||
					category.equalsIgnoreCase("Exchange Traded Funds(ETFs)"))) {
				continue;
			}
			
			insertIspPrice(session, ISIN, pricingSource, valuationDate, idcBid, "IDC Bid");
			insertIspPrice(session, ISIN, pricingSource, valuationDate, idcClosing, "IDC Closing");
			insertIspPrice(session, ISIN, pricingSource, valuationDate, markitBid, "MARKIT Bid");
			insertIspPrice(session, ISIN, pricingSource, valuationDate, markitClosing, "MARKIT Closing");
			insertIspPrice(session, ISIN, pricingSource, valuationDate, trBid, "TR Bid");
			insertIspPrice(session, ISIN, pricingSource, valuationDate, trClosing, "TR Closing");
			
			session.flush();
			session.clear();
			
		}
		tx.commit();
		session.close();
	}

	private static void insertIspPrice(Session session, String ISIN, String pricingSource, String valuationDate,
			String value, String fieldName) {
		if(value.matches("-?\\d+(\\.\\d+)?")) {
			String queryString = " DECLARE @SecID INT DECLARE @FieldID INT " +
					"SET @SecID = (SELECT SecID FROM map_secID WHERE ISIN = '" + ISIN + "') " +
					"SET @FieldID = (SELECT ID FROM def_NISPFields WHERE Field = '" + fieldName + "') " +
					"IF NOT EXISTS (SELECT 1 FROM " + GlobalConstants.ISP_PRICING_TABLE + " WHERE FK_SecID = @SecID AND FK_FieldID = @FieldID AND PricingDay = '" + valuationDate + "' AND PricingSource = '" + pricingSource + "') " +
					"BEGIN INSERT INTO " +
					GlobalConstants.ISP_PRICING_TABLE + 
					" (FK_SecID, FK_FieldID, doubleValue, stringValue, PricingSource, PricingDay, LastUpdate) VALUES (@SecID, @FieldID," +
					value + ",NULL,'" + 
					pricingSource + "','" +
					valuationDate + "', GETDATE()) END";
			
			Query query = session.createNativeQuery(queryString);
			query.executeUpdate();
		}
	}

	public static void importNAV(InputStream fileInputStream, String userName) throws Exception {
	    File targetFile = new File("targetFile_" + userName + ".csv");
	    String result = new BufferedReader(new InputStreamReader(fileInputStream))
	    		  .lines().collect(Collectors.joining("\n")); 
	    String content = createSqlServerCompatibleFile(result);
	    Files.write(targetFile.toPath(), content.getBytes());
	    
	    List<String> fields = new ArrayList<String>();
	    fields.add("PX_LAST");
	    fields.add("PX_VOLUME");
	    fields.add("PX_ASK");
	    fields.add("PX_BID");
	    fields.add("PX_FIXING");
	    fields.add("PX_MID");
	    fields.add("FUND_NET_ASSET_VAL");
	    
	    JDBCUtil dbUtil = new JDBCUtil(userName);
	    dbUtil.createHistoricalTable(fields);
	    dbUtil.importCsvBcp(targetFile.getAbsolutePath());
	    dbUtil.importHistoricalTable(GlobalConstants.BLOOMBERG_PRICING_TABLE); 
		
	}
	
	private static String createSqlServerCompatibleFile(String input) {
		String content = input;
		Pattern p = Pattern.compile("\"([^\"]*)\"");
		Matcher m = p.matcher(content);
		StringBuffer sb = new StringBuffer();
		while (m.find()) {
		  String text = m.group(1);
		  text = text.replaceAll(";", "|");
		  m.appendReplacement(sb, Matcher.quoteReplacement(text));
		}
		m.appendTail(sb);
		content = sb.toString();
		content = content.replace("\"", "");
		content = content.replace("N.A.", "");
		content = content.replace("N.S.", "");
		content = content.replace("N.D.", "");
		//add carriage return to all line breaks
		content = content.replaceAll(";\\s*$", "").replaceAll(";\\s*\n", "\r\n");	
		return content;
	}

	public static void importDLPricing(InputStream fileInputStream, String userName) throws Exception{
	    File targetFile = new File("targetFile_" + userName + ".csv");
	    String result = new BufferedReader(new InputStreamReader(fileInputStream))
	    		  .lines().collect(Collectors.joining("\n")); 
	    String content = createSqlServerCompatibleFile(result);
	    Files.write(targetFile.toPath(), content.getBytes());
	    
	    List<String> fields = new ArrayList<String>();
	    List<String> fieldsAndDates = new ArrayList<String>();
	    
	    String headers = content.split("\n")[0];
	    String[] headersSplitted = headers.split(";");
	    for(int i = 3; i < headersSplitted.length; i++) {
	    	String[] subSplits = headersSplitted[i].split(":");
	    	fields.add(subSplits[0]);
	    	if(subSplits.length > 1)
	    		fieldsAndDates.add(subSplits[0]+subSplits[1]);
	    	else
	    		fieldsAndDates.add(subSplits[0]);
	    }
	    
	    
	    JDBCUtil dbUtil = new JDBCUtil(userName);
	    dbUtil.createDLPricingTable(fieldsAndDates, fields);
	    dbUtil.importCsvBcp(targetFile.getAbsolutePath());
	    dbUtil.importDLPricingTable(GlobalConstants.BLOOMBERG_PRICING_TABLE, fields);
		
	}

	public static void importDLMaster(InputStream fileInputStream, String userName) throws Exception {
	    File targetFile = new File("targetFile_" + userName + ".csv");
	    String result = new BufferedReader(new InputStreamReader(fileInputStream))
	    		  .lines().collect(Collectors.joining("\n")); 
	    String content = createSqlServerCompatibleFile(result);
	    Files.write(targetFile.toPath(), content.getBytes());
	    
	    List<String> fields = new ArrayList<String>();
	    
	    String headers = content.split("\n")[0];
	    String[] headersSplitted = headers.split(";");
	    for(int i = 3; i < headersSplitted.length; i++) {
	    	fields.add(headersSplitted[i]);
	    }
	    
	    
	    JDBCUtil dbUtil = new JDBCUtil(userName);
	    dbUtil.createDLMasterTable(fields);
	    dbUtil.importCsvBcp(targetFile.getAbsolutePath());
	    dbUtil.importDLMasterTable(GlobalConstants.BLOOMBERG_MASTER_TABLE);	
	    dbUtil.updateMapSecID();
	}

	public static void importIDX(InputStream fileInputStream, String userName) throws Exception {
	    File targetFile = new File("targetFile_" + userName + ".csv");
	    String result = new BufferedReader(new InputStreamReader(fileInputStream))
	    		  .lines().collect(Collectors.joining("\n")); 
	    String content = createSqlServerCompatibleFile(result);
	    Files.write(targetFile.toPath(), content.getBytes());
	    
	    List<String> fields = new ArrayList<String>();
	    fields.add("IDX_RATIO");

	    
	    JDBCUtil dbUtil = new JDBCUtil(userName);
	    dbUtil.createHistoricalTable(fields);
	    dbUtil.importCsvBcp(targetFile.getAbsolutePath());
	    dbUtil.importHistoricalTable(GlobalConstants.BLOOMBERG_PRICING_TABLE); 
		
	}

	public static void importMarkit(InputStream fileInputStream, String userName) throws Exception{
	    File targetFile = new File("targetFile_" + userName + ".csv");
	    String result = new BufferedReader(new InputStreamReader(fileInputStream))
	    		  .lines().collect(Collectors.joining("\n")); 
	    String content = createSqlServerCompatibleFile(result);
	    Files.write(targetFile.toPath(), content.getBytes());
	    
	    List<String> fields = new ArrayList<String>();
	    
	    String headers = content.split("\n")[0];
	    String[] headersSplitted = headers.split(",");
	    for(int i = 0; i < headersSplitted.length; i++) {
	    	fields.add(headersSplitted[i]);
	    }
	    
	    
	    JDBCUtil dbUtil = new JDBCUtil(userName);
	    dbUtil.createMarkitTable(fields);
	    dbUtil.importCsvBcp(targetFile.getAbsolutePath());
	    dbUtil.importMarkitTable(GlobalConstants.MARKIT_TABLE);	
		
	}

	public static void importBVAL(InputStream fileInputStream, String userName) throws Exception {
	    File targetFile = new File("targetFile_" + userName + ".csv");
	    String result = new BufferedReader(new InputStreamReader(fileInputStream))
	    		  .lines().collect(Collectors.joining("\n")); 
	    String content = createSqlServerCompatibleFile(result);
	    Files.write(targetFile.toPath(), content.getBytes());
	    
	    List<String> fields = new ArrayList<String>();
	    String pricingDay = "";
	    
	    String headers = content.split("\n")[0];
	    String[] headersSplitted = headers.split(";");
	    for(int i = 3; i < headersSplitted.length; i++) {
	    	String[] subSplitted = headersSplitted[i].split(":");
	    	fields.add(subSplitted[0]);
	    	if(subSplitted.length > 1) {
	    		if(subSplitted[1].length() == 8) {
	    			pricingDay = subSplitted[1].substring(0, 4) + "-" + subSplitted[1].substring(4, 6) + "-" + subSplitted[1].substring(6);
	    		}
	    	}
	    }
	    
	    
	    JDBCUtil dbUtil = new JDBCUtil(userName);
	    dbUtil.createDLMasterTable(fields);
	    dbUtil.importCsvBcp(targetFile.getAbsolutePath());
	    dbUtil.importBVALTable(pricingDay, GlobalConstants.BLOOMBERG_MASTER_TABLE);	
		
	}

}
