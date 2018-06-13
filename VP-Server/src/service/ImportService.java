package service;

import java.io.IOException;
import java.io.InputStream;

import javax.persistence.Query;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.Transaction;

import database.HibernateUtil;
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
					"SET @FieldID = (SELECT ID FROM def_NISPFields WHERE Field = '" + fieldName + "')" +
					"INSERT INTO " +
					GlobalConstants.ISP_PRICING_TABLE + 
					" (FK_SecID, FK_FieldID, doubleValue, stringValue, PricingSource, PricingDay, LastUpdate) VALUES (@SecID, @FieldID," +
					value + ",NULL,'" + 
					pricingSource + "','" +
					valuationDate + "', GETDATE())";
			
			Query query = session.createNativeQuery(queryString);
			query.executeUpdate();
		}
	}

}
