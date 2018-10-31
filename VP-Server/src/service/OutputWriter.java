package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFDrawing;
import org.apache.poi.xssf.streaming.SXSSFPicture;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sun.prism.paint.Color;

import database.HibernateUtil;
import model.GlobalConstants;
import model.Job;
import model.OutputMessage;
import model.User;
import utils.StringUtil;


public class OutputWriter {
	
	private static final String SCRIPT_PATH = "C:\\Users\\deslu001\\Documents\\SQL Server Management Studio\\outputScript4.sql";
	public static final String OUTPUT_PREFIX = "OUTPUT_";
	public static final String INPUT_PREFIX = "DTP_";
	public static final String INPUT_CL_PREFIX = "DTP_CL_";
	
	private OutputMessage message;
	private SXSSFWorkbook workbook;
	
	
	public OutputWriter(OutputMessage message) {
		this.message = message;
	    //Create Blank workbook
	    this.workbook = new SXSSFWorkbook(); 
	    workbook.setCompressTempFiles(true);
	}
	
	private void writeValuationResultsPreparer() throws SQLException {
		String outputTable = OUTPUT_PREFIX + message.getJob().getPreparer().getId();
		Connection myConn = null;
		myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
	    Statement st = myConn.createStatement();
	    String sheetName = "EY Valuation Results Preparer";
	    ResultSet fieldOrderRs = st.executeQuery("Select * from tbl_FieldSelection Where Preparer_Selection = 1 order by Sort");
	    StringBuilder sb = new StringBuilder();
	    sb.append("Select ");
	    boolean start = true;
	    while(fieldOrderRs.next()) {
	    	if(!start)
	    		sb.append(",");
	    	String fieldName = fieldOrderRs.getString(1);
	    	sb.append("[").append(fieldName).append("]");
	    	start = false;
	    }
	    sb.append(" from " + outputTable);
	    ResultSet rs = st.executeQuery(sb.toString());
	    populateSheetContent(sheetName, rs, -1);
	}
	
	private void writeValuationResultsClient() throws SQLException {
		String outputTable = OUTPUT_PREFIX + message.getJob().getPreparer().getId();
		String input_cl_table = INPUT_CL_PREFIX + message.getJob().getPreparer().getId();
		Connection myConn = null;
		myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
	    Statement st = myConn.createStatement();
	    String sheetName = "EY Valuation Results Client";
	    ResultSet fieldOrderRs = st.executeQuery("Select FieldName from tbl_FieldSelection Where Client_Selection = 1 order by Sort");
	    StringBuilder sb = new StringBuilder();
	    sb.append("Select ");
	    boolean start = true;
	    while(fieldOrderRs.next()) {
	    	if(!start)
	    		sb.append(",");
	    	String fieldName = fieldOrderRs.getString(1);
	    	sb.append("ot.[").append(fieldName).append("]");
	    	start = false;
	    }
	    //append additional client columns by join
	    sb.append(", it.* from " + outputTable).append(" ot INNER JOIN ").append(input_cl_table).append(" it ON ot.EY_No = it.[CL EY_No]");
	    System.out.println(sb.toString());
	    ResultSet rs = st.executeQuery(sb.toString());
	    
	    populateSheetContent(sheetName, rs, -1);
	}
	
	private void writeNotCoveredPositions() throws SQLException {	    
		String outputTable = OUTPUT_PREFIX + message.getJob().getPreparer().getId();
		Connection myConn = null;
		myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
	    Statement st = myConn.createStatement();
	    String sheetName = "Not Covered Positions";
	    ResultSet fieldOrderRs = st.executeQuery("Select * from tbl_FieldSelection Where Preparer_Selection = 1 order by Sort");
	    StringBuilder sb = new StringBuilder();
	    sb.append("Select ");
	    boolean start = true;
	    while(fieldOrderRs.next()) {
	    	if(!start)
	    		sb.append(",");
	    	String fieldName = fieldOrderRs.getString(1);
	    	sb.append("[").append(fieldName).append("]");
	    	start = false;
	    }
	    sb.append(" from " + outputTable + " WHERE [Fair Price EY] is null");
	    ResultSet rs = st.executeQuery(sb.toString());
	    populateSheetContent(sheetName, rs, -1);
	}
	
	private void writeLargestPriceDeviations() throws SQLException {
		String outputTable = OUTPUT_PREFIX + message.getJob().getPreparer().getId();
		Connection myConn = null;
		myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
	    Statement st = myConn.createStatement();
	    String sheetName = "Largest Price Deviations";
	    ResultSet rs = st.executeQuery("Select Count(*) from " + outputTable + " WHERE [Fair Price EY] is not null");
	    rs.next();
	    int numberPositions = rs.getInt(1);
	    int numberTopPercent = (int) (numberPositions * 0.05);
	    int numberToBeDisplayed = (numberTopPercent > 10) ? numberTopPercent : 10;
	    
	    ResultSet fieldOrderRs = st.executeQuery("Select * from tbl_FieldSelection Where Preparer_Selection = 1 order by Sort");
	    StringBuilder sb = new StringBuilder();
	    sb.append("Select TOP (" + numberToBeDisplayed + ") ");
	    boolean start = true;
	    while(fieldOrderRs.next()) {
	    	if(!start)
	    		sb.append(",");
	    	String fieldName = fieldOrderRs.getString(1);
	    	sb.append("[").append(fieldName).append("]");
	    	start = false;
	    }
	    sb.append(" from " + outputTable + " Order by [Price Deviation Percent] desc");
	    rs = st.executeQuery(sb.toString());
	    populateSheetContent(sheetName, rs, 7);
	}
	
	private void writeLargestMarketValueDeviations() throws SQLException {
		String outputTable = OUTPUT_PREFIX + message.getJob().getPreparer().getId();
		Connection myConn = null;
		myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
	    Statement st = myConn.createStatement();
	    String sheetName = "Largest Market Value Deviations";
	    ResultSet rs = st.executeQuery("Select Count(*) from " + outputTable + " WHERE [Fair Price EY] is not null");
	    rs.next();
	    int numberPositions = rs.getInt(1);
	    int numberTopPercent = (int) (numberPositions * 0.05);
	    int numberToBeDisplayed = (numberTopPercent > 10) ? numberTopPercent : 10;
	    
	    ResultSet fieldOrderRs = st.executeQuery("Select * from tbl_FieldSelection Where Preparer_Selection = 1 order by Sort");
	    StringBuilder sb = new StringBuilder();
	    sb.append("Select TOP (" + numberToBeDisplayed + ") ");
	    boolean start = true;
	    while(fieldOrderRs.next()) {
	    	if(!start)
	    		sb.append(",");
	    	String fieldName = fieldOrderRs.getString(1);
	    	sb.append("[").append(fieldName).append("]");
	    	start = false;
	    }
	    sb.append(" from " + outputTable + " Order by [Value Deviation EY] desc");
	    rs = st.executeQuery(sb.toString());
	    populateSheetContent(sheetName, rs, 10);
	}

	private void populateSheetContent(String sheetName, ResultSet rs, int highlight) throws SQLException {
		XSSFColor eyGreen = new XSSFColor(new java.awt.Color(149, 203, 137));
		XSSFColor eyYellow = new XSSFColor(new java.awt.Color(255, 230, 0));
		
		ResultSetMetaData rsmd = rs.getMetaData();
	    int numColumns = rsmd.getColumnCount();
	    
	    SXSSFSheet currentSheet = workbook.createSheet(sheetName);
	    currentSheet.setTabColor(eyGreen);
	    currentSheet.setRandomAccessWindowSize(100);
	    
	    CellStyle wrapText = workbook.createCellStyle();
	    wrapText.setAlignment(HorizontalAlignment.CENTER);
	    wrapText.setVerticalAlignment(VerticalAlignment.CENTER);
	    wrapText.setWrapText(true);
	    wrapText.setFillForegroundColor(IndexedColors.WHITE1.getIndex());
	    wrapText.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    
	    CellStyle greyHeader = workbook.createCellStyle();
	    greyHeader.cloneStyleFrom(wrapText);
	    greyHeader.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
	    
	    XSSFCellStyle yellowHeader = (XSSFCellStyle) workbook.createCellStyle();
	    yellowHeader.cloneStyleFrom(wrapText);
	    yellowHeader.setFillForegroundColor(eyYellow);
	    
	    CellStyle redCellStyle = workbook.createCellStyle();
	    redCellStyle.setAlignment(HorizontalAlignment.CENTER);
	    redCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	    redCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
	    redCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    
	    CellStyle twoDecimalStyle = workbook.createCellStyle();
	    twoDecimalStyle.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
	    
	    CellStyle integerStyle = workbook.createCellStyle();
	    integerStyle.setDataFormat(workbook.createDataFormat().getFormat("#,###"));
	    
	    SXSSFRow rowHead = currentSheet.createRow(0);
	    for(int a = 0; a < numColumns; a++) {
	    	String colName = rsmd.getColumnName(a+1);
	    	SXSSFCell cell = rowHead.createCell(a);
	    	cell.setCellValue(colName);
	    	if(a == 5)
	    		cell.setCellStyle(yellowHeader);
	    	else if(a>2 && a<12)
	    		cell.setCellStyle(greyHeader);
	    	else
	    		cell.setCellStyle(wrapText);
	    }
	    int i = 1;
	    while (rs.next()){
	    	SXSSFRow row = currentSheet.createRow(i);
	    	for(int a = 0; a < numColumns; a++) {
	    		SXSSFCell cell = row.createCell(a);
	    		if(a == highlight)
	    			cell.setCellStyle(redCellStyle);
	    		String value = rs.getString(a+1);
	    		if(NumberUtils.isCreatable(value) && NumberUtils.isCreatable(value.substring(value.length() - 1))) {
	    			double number = Double.parseDouble(value);
	    			cell.setCellType(CellType.NUMERIC);
	    			cell.setCellValue(Double.parseDouble(value));
	    			if(number % 1 != 0)
	    				cell.setCellStyle(twoDecimalStyle);
	    			else
	    				cell.setCellStyle(integerStyle);
	    		}
	    		else
	    			cell.setCellValue(value);
	    	}
	        i++;
	        System.out.println(i);
	    }
	    
	    //WORKAROUND: set last row with headers because autosizecolumn doesnt work on first row
	    SXSSFRow dummyrow = currentSheet.createRow(i);
	    for(int a = 0; a < numColumns; a++) {
	    	String colName = rsmd.getColumnName(a+1);
	    	SXSSFCell cell = dummyrow.createCell(a);
	    	cell.setCellValue(colName);
	    	if(a == 5)
	    		cell.setCellStyle(yellowHeader);
	    	else if(a>1 && a<11)
	    		cell.setCellStyle(greyHeader);
	    	else
	    		cell.setCellStyle(wrapText);
	    }
	    currentSheet.trackAllColumnsForAutoSizing();
	    for(int a = 0; a < numColumns; a++) {
	    	currentSheet.autoSizeColumn(a+1);
	    }
	    
	    //delete the last row after autosizing
	    SXSSFRow lastRow = currentSheet.getRow(i);
	    for(int a = 0; a < numColumns; a++) {
	    	SXSSFCell cell = lastRow.getCell(a);
	    	cell.setCellValue("");
	    	cell.setCellStyle(null);
	    }
	    
	    currentSheet.createFreezePane(0, 1);
	}
	
	
	private void writeCoverPage() throws IOException {
		String pricingDay = StringUtil.convertDateFormat(message.getJob().getPricingDay());
		String currency = message.getJob().getCurrency();
		double fxRate = getFxRate(currency, message.getJob().getPricingDay());
	    //set EY colors
	    XSSFColor eyLightGrey = new XSSFColor(new java.awt.Color(204, 204, 204));
	    XSSFColor eyGreen = new XSSFColor(new java.awt.Color(149, 203, 137));
	    XSSFColor eyViolett = new XSSFColor(new java.awt.Color(200, 147, 199));
	    XSSFColor eyYellow = new XSSFColor(new java.awt.Color(255, 230, 0));
		
		SXSSFSheet coverPage = workbook.createSheet("Cover Page");
		coverPage.setTabColor(eyYellow);
		coverPage.setRandomAccessWindowSize(100);
		
	    //add EY logo
	    SXSSFDrawing eyLogo = coverPage.createDrawingPatriarch();
	    FileInputStream stream = new FileInputStream("L:\\10_Entwicklung\\00_Templates\\60_Logos\\English\\EY_Logo_Beam_Tag_Stacked_EN\\EY_Logo_Beam_Tag_Stacked_RGB_EN.png");
	    int pictureIndex = workbook.addPicture(IOUtils.toByteArray(stream), Workbook.PICTURE_TYPE_PNG);
	    ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
	    anchor.setCol1(1);
	    anchor.setRow1(2);
	    anchor.setCol2(4);
	    anchor.setRow2(11);
	    SXSSFPicture logo = eyLogo.createPicture(anchor, pictureIndex);
	    logo.resize(0.05);
	    
	    
	    //set colors 
	    CellStyle whiteBackground = workbook.createCellStyle();
	    whiteBackground.setFillForegroundColor(IndexedColors.WHITE.getIndex());
	    whiteBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    for(int i = 0; i < 62; i++) {
	    	SXSSFRow row = coverPage.createRow(i);
	    	for(int j = 0; j < 35; j++)
	    		row.createCell(j).setCellStyle(whiteBackground);
	    }
	    XSSFCellStyle yellowBackground = (XSSFCellStyle) workbook.createCellStyle();
	    yellowBackground.setFillForegroundColor(eyYellow);
	    yellowBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    for(int i = 2; i < 11; i++) {
	    	SXSSFRow row = coverPage.getRow(i);
	    	for(int j = 5; j < 35; j++)
	    		row.getCell(j).setCellStyle(yellowBackground);
	    }
	    XSSFCellStyle lightGreyBackground = (XSSFCellStyle) workbook.createCellStyle();
	    lightGreyBackground.setFillForegroundColor(eyLightGrey);
	    lightGreyBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    for(int i = 29; i < 34; i++) {
	    	SXSSFRow row = coverPage.getRow(i);
	    	for(int j = 5; j < 20; j++)
	    		row.getCell(j).setCellStyle(lightGreyBackground);
	    }
	    
	    XSSFCellStyle wrapText = (XSSFCellStyle) workbook.createCellStyle();
	    wrapText.setWrapText(true);
	    wrapText.setVerticalAlignment(VerticalAlignment.CENTER);
	    wrapText.setFillForegroundColor(eyLightGrey);
	    wrapText.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    Font whiteFont = workbook.createFont();
	    whiteFont.setColor(IndexedColors.WHITE.getIndex());
	    wrapText.setFont(whiteFont);
	    
	    CellStyle centerAlign = workbook.createCellStyle();
	    centerAlign.setAlignment(HorizontalAlignment.CENTER);
	    centerAlign.setVerticalAlignment(VerticalAlignment.CENTER);
	    centerAlign.setWrapText(true);
	    
	    //set labels
	    coverPage.getRow(2).getCell(5).setCellValue("Client:");
	    coverPage.getRow(4).getCell(5).setCellValue("Valuation Date:");
	    coverPage.getRow(6).getCell(5).setCellValue("Reporting Currency:");
	    coverPage.getRow(8).getCell(5).setCellValue("Prepared by:");
	    coverPage.getRow(10).getCell(5).setCellValue("Reviewed by:");
	    
	    coverPage.getRow(2).getCell(8).setCellValue(message.getJob().getClient().getClient());
	    coverPage.getRow(4).getCell(8).setCellValue(pricingDay);
	    coverPage.getRow(6).getCell(8).setCellValue(currency);
	    
	    User preparer = message.getJob().getPreparer();
	    if(preparer == null)
	    	coverPage.getRow(8).getCell(8).setCellValue("");
	    else
	    	coverPage.getRow(8).getCell(8).setCellValue(message.getJob().getPreparer().getFirstname() + " " + message.getJob().getPreparer().getLastname());
	    	
	    User reviewer = message.getJob().getReviewer();
	    if(reviewer == null)
	    	coverPage.getRow(10).getCell(8).setCellValue("");
	    else
	    	coverPage.getRow(10).getCell(8).setCellValue(message.getJob().getReviewer().getFirstname() + " " + message.getJob().getReviewer().getLastname());
	    
	    coverPage.addMergedRegion(new CellRangeAddress(15, 15, 0, 4));
	    coverPage.getRow(15).getCell(0).setCellValue("Visual Portfolio Output");
	    coverPage.getRow(15).getCell(0).setCellStyle(centerAlign);
	    coverPage.getRow(12).getCell(5).setCellValue("The client prices are compared to our EY fair price derived from the data of different market data providers including Bloomberg, ICE, Markit and Thomson Reuters.");
	    coverPage.getRow(13).getCell(5).setCellValue("We adhere to the pre-defined prefect hierarchy, which reflects the priority in which the data are classified to obtain a fair valuation.");
	    coverPage.getRow(15).getCell(13).setCellValue("Includes all priced and not priced positions in the analysis.");
	    coverPage.getRow(17).getCell(13).setCellValue("Short list including the top 10 percent of the largest price deviations found in the portfolio.");
	    coverPage.getRow(19).getCell(13).setCellValue("Short list including the top 10 percent of the largest market value deviations found in the portfolio.");
	    coverPage.getRow(21).getCell(13).setCellValue("Positions for which prices were not available from any of our market data providers.");
	    
	    coverPage.addMergedRegion(new CellRangeAddress(23, 27, 0, 4));
	    coverPage.getRow(23).getCell(0).setCellValue("Specialist Input \r\n Columns inserted by the EY Specialist are placed to the left of the client delivery and are coloured in EY Yellow");
	    coverPage.getRow(23).getCell(0).setCellStyle(centerAlign);
	    coverPage.getRow(23).getCell(13).setCellValue("Client data processed by an EY specialist.");
	    coverPage.getRow(25).getCell(13).setCellValue("Positions which were excluded from the analysis.");
	    coverPage.getRow(27).getCell(13).setCellValue("Positions delivered by the client.");
	    
	    coverPage.addMergedRegion(new CellRangeAddress(29, 30, 0, 4));
	    coverPage.getRow(29).getCell(0).setCellValue("Reconciliation of the Market Value given by the Client");
	    coverPage.getRow(29).getCell(0).setCellStyle(centerAlign);
	    coverPage.getRow(30).getCell(5).setCellValue("Market Value as delivered:");
	    coverPage.getRow(31).getCell(5).setCellValue("Market Value excluded as delivered:");
	    coverPage.getRow(32).getCell(5).setCellValue("Market Value in Scope of Analysis calculated:");
	    coverPage.getRow(33).getCell(5).setCellValue("Difference:");
	    
	    coverPage.addMergedRegion(new CellRangeAddress(12, 33, 23, 33));
	    coverPage.getRow(12).getCell(23).setCellValue("\r\n+++++++++++++++Disclaimer+++++++++++++++\r\n" + 
	    		"\r\n" + 
	    		"The analysis is based on the data in sheet EY Valuation Results of this Excel file. The reporting currency is " + currency +" and prices were primarily compared to end-of-day Bid prices for active positions and end-of-day Ask prices for passive positions.\r\n" + 
	    		"\r\n" + 
	    		"Please note that we have to convert the prices obtained from our data vendors from EUR into the respective currency. For this analysis we used the EUR/" + currency +" FX rate of " + fxRate + ".\r\n" + 
	    		"Column Fair Price EY holds the benchmark price used for the valuation of the price given by the client in column Fair Price Client. \r\n" + 
	    		"All this information is free to use for EY analysts. Only for specific positions the data is allowed to be forwarded to the client.\r\n" + 
	    		"\r\n" + 
	    		"Please note that the EY composite rating used in the analysis is based on credit ratings as of " + pricingDay + " (or older). As historical ratings are not provided by our data vendors slight changes with respect to the grouping in the EY composite rating based graphs can occur occasionally.\r\n" + 
	    		"\r\n" + 
	    		"The responsible audit team needs to evaluate and recognize the work performed according to EY GAM including necessary documentation in CANVAS as the members of the Visual Portfolio team are treated as internal specialists (ISA 620).\r\n" + 
	    		"\r\n" + 
	    		"+++++++++++++++++++++++++++++++++++++\r\n" + 
	    		"");
	    coverPage.getRow(12).getCell(23).setCellStyle(wrapText);
	    
	    XSSFCellStyle bold_style = (XSSFCellStyle) workbook.createCellStyle();
	    Font bold_font = workbook.createFont();
	    bold_font.setBold(true);
	    bold_style.setFont(bold_font);
	    bold_style.setFillForegroundColor(eyLightGrey);
	    bold_style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    coverPage.getRow(29).getCell(10).setCellValue("Assets");
	    coverPage.getRow(29).getCell(10).setCellStyle(bold_style);
	    coverPage.getRow(29).getCell(13).setCellValue("Liabilities");
	    coverPage.getRow(29).getCell(13).setCellStyle(bold_style);
	      
	    
	    //Hyperlink font 
        Font hlink_font = workbook.createFont();
        hlink_font.setUnderline(Font.U_SINGLE);
        hlink_font.setColor(IndexedColors.BLUE.getIndex());
	    
        //Green background
	    XSSFCellStyle greenBackground = (XSSFCellStyle) workbook.createCellStyle();
	    greenBackground.setFillForegroundColor(eyGreen);
	    greenBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    greenBackground.setFont(hlink_font);
	    greenBackground.setAlignment(HorizontalAlignment.CENTER);
	    
	    //set green links
	    CreationHelper createHelper = workbook.getCreationHelper();
	    
	    Hyperlink eyValuationResultsLink = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
	    eyValuationResultsLink.setAddress("'EY Valuation Results'!A1");
	    coverPage.addMergedRegion(new CellRangeAddress(15, 15, 5, 12));
	    coverPage.getRow(15).getCell(5).setCellStyle(greenBackground);
	    coverPage.getRow(15).getCell(5).setCellValue("EY Valuation Results");
	    coverPage.getRow(15).getCell(5).setHyperlink(eyValuationResultsLink);
	    
	    Hyperlink largestPriceDeviationsLink = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
	    largestPriceDeviationsLink.setAddress("'Largest Price Deviations'!A1");
	    coverPage.addMergedRegion(new CellRangeAddress(17, 17, 5, 12));
	    coverPage.getRow(17).getCell(5).setCellStyle(greenBackground);
	    coverPage.getRow(17).getCell(5).setCellValue("Largest Price Deviations");
	    coverPage.getRow(17).getCell(5).setHyperlink(largestPriceDeviationsLink);
	    
	    Hyperlink largestMarketValueDeviationsLink = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
	    largestMarketValueDeviationsLink.setAddress("'Largest Market Value Deviations'!A1");
	    coverPage.addMergedRegion(new CellRangeAddress(19, 19, 5, 12));
	    coverPage.getRow(19).getCell(5).setCellStyle(greenBackground);
	    coverPage.getRow(19).getCell(5).setCellValue("Largest Market Value Deviations");
	    coverPage.getRow(19).getCell(5).setHyperlink(largestMarketValueDeviationsLink);
	    
	    Hyperlink notCoveredPositionsLink = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
	    notCoveredPositionsLink.setAddress("'Not Covered Positions'!A1");
	    coverPage.addMergedRegion(new CellRangeAddress(21, 21, 5, 12));
	    coverPage.getRow(21).getCell(5).setCellStyle(greenBackground);
	    coverPage.getRow(21).getCell(5).setCellValue("Not Covered Positions");
	    coverPage.getRow(21).getCell(5).setHyperlink(notCoveredPositionsLink);

        //Aqua background
	    XSSFCellStyle aquaBackground = (XSSFCellStyle) workbook.createCellStyle();
	    aquaBackground.setFillForegroundColor(eyViolett);
	    aquaBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    aquaBackground.setFont(hlink_font);
	    aquaBackground.setAlignment(HorizontalAlignment.CENTER);
	    
	    //set aqua links
	    Hyperlink dataPreparationLink = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
	    dataPreparationLink.setAddress("'Data Preparation'!A1");
	    coverPage.addMergedRegion(new CellRangeAddress(23, 23, 5, 12));
	    coverPage.getRow(23).getCell(5).setCellStyle(aquaBackground);
	    coverPage.getRow(23).getCell(5).setCellValue("Data Preparation");
	    coverPage.getRow(23).getCell(5).setHyperlink(dataPreparationLink);
	    
	    Hyperlink excludedPositionsLink = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
	    excludedPositionsLink.setAddress("'Excluded Positions'!A1");
	    coverPage.addMergedRegion(new CellRangeAddress(25, 25, 5, 12));
	    coverPage.getRow(25).getCell(5).setCellStyle(aquaBackground);
	    coverPage.getRow(25).getCell(5).setCellValue("Excluded Positions");
	    coverPage.getRow(25).getCell(5).setHyperlink(excludedPositionsLink);
	    
        //Grey background
	    XSSFCellStyle greyBackground = (XSSFCellStyle) workbook.createCellStyle();
	    greyBackground.setFillForegroundColor(eyLightGrey);
	    greyBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    greyBackground.setFont(hlink_font);
	    greyBackground.setAlignment(HorizontalAlignment.CENTER);
	    
	    //set grey links
	    Hyperlink clientDeliveryLink = createHelper.createHyperlink(HyperlinkType.DOCUMENT);
	    clientDeliveryLink.setAddress("'Client Delivery'!A1");
	    coverPage.addMergedRegion(new CellRangeAddress(27, 27, 5, 12));
	    coverPage.getRow(27).getCell(5).setCellStyle(greyBackground);
	    coverPage.getRow(27).getCell(5).setCellValue("Client Delivery");
	    coverPage.getRow(27).getCell(5).setHyperlink(clientDeliveryLink);
	}
	
	private void export() {
		try {
			System.out.println("start coverpage");
			writeCoverPage();
			System.out.println("coverpage done");
			writeValuationResultsPreparer();
			System.out.println("Client results");
			writeValuationResultsClient();
			System.out.println("valuation results done");
			writeLargestPriceDeviations();
			System.out.println("largest price devs done");
			writeLargestMarketValueDeviations();
			System.out.println("largest market devs done");

		    writeNotCoveredPositions();
		    System.out.println("not covered done");

		    SXSSFSheet dataPreparation = workbook.createSheet("Data Preparation");
		    SXSSFSheet excludedPositions = workbook.createSheet("Excluded Positions");
		    SXSSFSheet clientDelivery = workbook.createSheet("Client Delivery");

		    XSSFColor eyViolett = new XSSFColor(new java.awt.Color(200, 147, 199));
		    XSSFColor eyLightGrey = new XSSFColor(new java.awt.Color(204, 204, 204));
		    dataPreparation.setTabColor(eyViolett);
		    excludedPositions.setTabColor(eyViolett);
		    clientDelivery.setTabColor(eyLightGrey);
		    //Create file system using specific name
		    FileOutputStream out = new FileOutputStream(new File(message.getOutputPath()));
		    workbook.write(out);
		    out.close();
		    workbook.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static boolean hasFxRate(String currency, Date pricingDay){
		Session session = HibernateUtil.getSessionFactory().openSession();	
		try {		
			session.beginTransaction();
			Query query = session.createNativeQuery("SELECT * FROM tbl_FXrates WHERE FromCur = :currency AND fxDate = :pricingDay");
			query.setParameter("currency", currency);
			query.setParameter("pricingDay", StringUtil.convertDateFormat(pricingDay));
			if(query.getResultList().size() > 0) {
				return true;
			}
			return false;
			
			
		} finally {
			session.close();
		}	
	}
	
	private static double getFxRate(String currency, Date pricingDay){
		if(currency.equals("EUR"))
			return 1;
		Session session = HibernateUtil.getSessionFactory().openSession();	
		try {		
			session.beginTransaction();
			Query query = session.createNativeQuery("SELECT Rate FROM tbl_FXrates WHERE FromCur = :currency AND fxDate = :pricingDay");
			query.setParameter("currency", currency);
			query.setParameter("pricingDay", StringUtil.convertDateFormat(pricingDay));
			if(query.getResultList().size() > 0) {
				@SuppressWarnings("unchecked")
				List<Object> res = query.getResultList();
				return (double) res.get(0);
			}
			return -1;
			
			
		} finally {
			session.close();
		}	
	}
	
	private static List<String> getMissingIdxPositions(String outputTable){
		Session session = HibernateUtil.getSessionFactory().openSession();	
		try {		
			session.beginTransaction();
			Query query = session.createNativeQuery("SELECT ISIN FROM " + outputTable + " WHERE BB_INFLATION_LINKED_INDICATOR = 'Y' AND BB_IDX_RATIO is null");
			List<String> res = query.getResultList();
			return res;		
		} finally {
			session.close();
		}	
	}
	
	
	public String run() {
		try {
			if(message.getStatus() == OutputMessage.STATUS_PREPARE) {
				String currency = message.getJob().getCurrency();
				if(!currency.equals("EUR") && !hasFxRate(currency, message.getJob().getPricingDay())) {
					return "FX rate is missing";
				}
				System.out.println("start calc...");
				System.out.println(GlobalConstants.SERVER);
				ProcessBuilder pb = new ProcessBuilder(
						"sqlcmd", 
						"-E", 
						"-d",
						GlobalConstants.DATABASE,
						"-S",
						GlobalConstants.SERVER,
						"-v",
						"pricingDay=" + StringUtil.convertDateFormat(message.getJob().getPricingDay()),
						"inputTable=" + INPUT_PREFIX + message.getJob().getPreparer().getId(),
						"inputTableCL=" + INPUT_PREFIX + "CL_" + message.getJob().getPreparer().getId(),
						"outputTable=" + OUTPUT_PREFIX + message.getJob().getPreparer().getId(),
						"currency=" + message.getJob().getCurrency(),
						"priceCategory=" + message.getPriceCategory(),
						"-i",
						SCRIPT_PATH);
				pb.redirectErrorStream(true);
				Process evaluation = pb.start();
				Thread.sleep(10000);
				List<String> missingIdxRatioPositions = getMissingIdxPositions(OUTPUT_PREFIX + message.getJob().getPreparer().getId());
				if(missingIdxRatioPositions.size() > 0) {
					StringBuilder sb = new StringBuilder();
					for(String isin : missingIdxRatioPositions) {
						sb.append(isin).append("\n");
					}
					return sb.toString();
				}
				//evaluation.waitFor();
			} else if(message.getStatus() == OutputMessage.STATUS_REQUEST) {
				System.out.println("Excel start...");
				String outputTable = OUTPUT_PREFIX + message.getJob().getPreparer().getId();
				String outputFolderPath = message.getOutputPath();
				if(message.isBloomberg()) {
					Excel.createBbMasterDataRequest(outputTable, outputFolderPath);
					Excel.createBbDLPricingRequest(outputTable, outputFolderPath, StringUtil.convertDateFormat(message.getJob().getPricingDay()));
					Excel.createBbBVALRequest(outputTable, outputFolderPath, StringUtil.convertDateFormat(message.getJob().getPricingDay()));
				}
				if(message.isIsp()) {
					Excel.createIspRequest(outputTable, outputFolderPath);
				}
				if(message.isMarkit()) {
					Excel.createMarkitRequest(outputTable, outputFolderPath);
				}
				if(message.isIdx()) {
					Excel.createBbIdxRequest(outputTable, outputFolderPath, StringUtil.convertDateFormat(message.getJob().getPricingDay()));		
				}
				if(message.isNav()) {
					Excel.createBbNavRequest(outputTable,outputFolderPath, StringUtil.convertDateFormat(message.getJob().getPricingDay()));
				}
			} else if(message.getStatus() == OutputMessage.STATUS_OUTPUT) {
				System.out.println("Excel start...");
				export();
			}
		} catch (IOException e) {
			e.printStackTrace();
			return "IOException";
		} catch (InterruptedException e) {
			e.printStackTrace();
			return "IOException";
		} catch (SQLException e) {
			e.printStackTrace();
			return "SQLException";
		} catch (ParseException e) {
			e.printStackTrace();
			return "ParseException";
		}
		
		return "OK";
	}

}
