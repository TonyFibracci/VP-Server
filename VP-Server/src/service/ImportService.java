package service;

import java.io.IOException;
import java.io.InputStream;

import javax.persistence.Query;

import org.apache.poi.ss.usermodel.CellType;
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
		double idcBid = -1;
		double idcClosing = -1;
		double markitBid = -1;
		double markitClosing = -1;
		double trBid = -1;
		double trClosing = -1;
		for (int i = 5; i < numberOfRows; i++) {
			XSSFRow row = sheet.getRow(i);
			String category = row.getCell(0).getStringCellValue();
			String ISIN = row.getCell(1).getStringCellValue();
			String localCurrency = row.getCell(3).getStringCellValue();
			String targetCurrency = row.getCell(17).getStringCellValue();
			String pricingSource = row.getCell(5).getStringCellValue();
			String valuationDate = row.getCell(6).getStringCellValue();
			System.out.println(row.getCell(15).getStringCellValue());
			System.out.println(row.getCell(16).getStringCellValue());
			if(row.getCell(11).getCellTypeEnum() == CellType.NUMERIC)
				idcBid = row.getCell(11).getNumericCellValue();
			if(row.getCell(12).getCellTypeEnum() == CellType.NUMERIC)
				idcClosing = row.getCell(12).getNumericCellValue();
			if(row.getCell(13).getCellTypeEnum() == CellType.NUMERIC)
				markitBid = row.getCell(13).getNumericCellValue();
			if(row.getCell(14).getCellTypeEnum() == CellType.NUMERIC)
				markitClosing = row.getCell(14).getNumericCellValue();
			if(row.getCell(15).getCellTypeEnum() == CellType.NUMERIC)
				trBid = row.getCell(15).getNumericCellValue();
			if(row.getCell(16).getCellTypeEnum() == CellType.NUMERIC)
				trClosing = row.getCell(16).getNumericCellValue();
			
			if(!localCurrency.equalsIgnoreCase(targetCurrency) && (
					category.equalsIgnoreCase("Government Bonds") || 
					category.equalsIgnoreCase("Corporate Bonds") ||
					category.equalsIgnoreCase("Securitized Products") ||
					category.equalsIgnoreCase("Loans") ||
					category.equalsIgnoreCase("Municipal Bonds"))) {
				continue;	
			}
			
			if(targetCurrency.equalsIgnoreCase("EUR") && (
					category.equalsIgnoreCase("Equities") ||
					category.equalsIgnoreCase("Exchange Traded Funds(ETFs)"))) {
				continue;
			}
			
			insertIspPrice(session, ISIN, pricingSource, valuationDate, idcBid);
			insertIspPrice(session, ISIN, pricingSource, valuationDate, idcClosing);
			insertIspPrice(session, ISIN, pricingSource, valuationDate, markitBid);
			insertIspPrice(session, ISIN, pricingSource, valuationDate, markitClosing);
			insertIspPrice(session, ISIN, pricingSource, valuationDate, trBid);
			insertIspPrice(session, ISIN, pricingSource, valuationDate, trClosing);
			
			session.flush();
			session.clear();
			
		}
		tx.commit();
		session.close();
	}

	private static void insertIspPrice(Session session, String ISIN, String pricingSource, String valuationDate,
			double idcBid) {
		if(idcBid >= 0) {
			String queryString = " DECLARE @SecID INT DECLARE @FieldID INT " +
					"SET @SecID = (SELECT SecID FROM map_secID WHERE ISIN = '" + ISIN + "') " +
					"SET @FieldID = (SELECT ID FROM def_NISPFields WHERE Field = 'IDC Bid') " +
					"INSERT INTO " +
					GlobalConstants.ISP_PRICING_TABLE + 
					" (FK_SecID, FK_FieldID, doubleValue, stringValue, PricingSource, PricingDay, LastUpdate) VALUES (@SecID, @FieldID," +
					idcBid + ",NULL,'" + 
					pricingSource + "','" +
					valuationDate + "', GETDATE())";
			
			Query query = session.createNativeQuery(queryString);
			query.executeUpdate();
		}
	}

}
