package service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFDrawing;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import model.GlobalConstants;

public class Excel {
	
	public static final List<String> ISP_CURRENCIES = Arrays.asList("ARA", "AUD", "CAD", "CHF", "CNY", "GBP", "HKD", "JPY", "NZD", "SGD", "USD", "UYI", "ZAR");
	
	public static void createMarkitRequest(String table, String path) throws SQLException, IOException{
		Connection myConn = null;
		myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
	    Statement st = myConn.createStatement();
    	ResultSet isins = st.executeQuery(
	    		"SELECT ISIN "
	    		+ "FROM " + table 
	    		+ " WHERE [Type of price] = 'Percent'"
	    		+ " AND ([Fair Price EY] is null OR EY_Price_Origin_Position = 'Last')");
    	StringBuilder sb = new StringBuilder();
	    if((!isins.isBeforeFirst()))
    		return;
	    while (isins.next()){
	    	sb.append(isins.getString(1)).append("\n");
	    }  
	    String currentTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
	    Files.write(Paths.get(path + "\\EY_SOI_Bonds_" + currentTime +".csv"), sb.toString().getBytes());
	}
	
	public static void createIspRequest(String table, String path) throws SQLException, IOException {
		Connection myConn = null;
		myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
	    Statement st = myConn.createStatement();
	    ArrayList<String> currencies = new ArrayList<String>();
	    ResultSet currencyRS = st.executeQuery(
	    		"SELECT DISTINCT BB_CRNCY "
	    		+ "FROM " + table 
	    		+ " WHERE [Type of price] = 'Percent'"
	    		+ " AND ([Fair Price EY] is null OR EY_Price_Origin_Position = 'Last')");
	    while(currencyRS.next()) {
	    	String cur = currencyRS.getString(1);
	    	if(ISP_CURRENCIES.contains(cur))
	    		currencies.add(cur);
	    }
	    for(String currency : currencies) {
	    	ResultSet isins = st.executeQuery(
		    		"SELECT DISTINCT ISIN "
		    		+ "FROM " + table 
		    		+ " WHERE BB_CRNCY = '" + currency
		    		+ "' AND [Type of price] = 'Percent'"
		    		+ " AND ([Fair Price EY] is null OR EY_Price_Origin_Position = 'Last')");
	    	StringBuilder sb = new StringBuilder();
		    if((!isins.isBeforeFirst()))
	    		continue;
		    while (isins.next()){
		    	sb.append(isins.getString(1)).append("\r\n");
		    }  
		    Files.write(Paths.get(path + "\\ISP_" + currency +".csv"), sb.toString().getBytes());
	    }
	    //output ISINs without master data
    	ResultSet isins = st.executeQuery(
	    		"SELECT DISTINCT ISIN "
	    		+ "FROM " + table 
	    		+ " WHERE BB_CRNCY IS NULL"
	    		+ " AND ([Fair Price EY] is null OR EY_Price_Origin_Position = 'Last')");
    	StringBuilder sb = new StringBuilder();
	    if((isins.isBeforeFirst())) {
		    while (isins.next()){
		    	sb.append(isins.getString(1)).append("\r\n");
		    }  
		    Files.write(Paths.get(path + "\\ISP_Unknown.csv"), sb.toString().getBytes());
	    }
	    //output equities
    	isins = st.executeQuery(
	    		"SELECT DISTINCT ISIN "
	    		+ "FROM " + table 
	    		+ " WHERE ([Type of price] = 'Piece' OR BB_CRNCY = 'EUR')"
	    		+ " AND ([Fair Price EY] is null OR EY_Price_Origin_Position = 'Last')");
    	sb = new StringBuilder(); 
	    if((isins.isBeforeFirst())) {
		    while (isins.next()){
		    	sb.append(isins.getString(1)).append("\r\n");
		    }  
		    Files.write(Paths.get(path + "\\ISP_EUR.csv"), sb.toString().getBytes());
	    }
	}
	
	public static void createBbMasterDataRequest(String table, String path) throws SQLException, IOException{
		StringBuilder sb = new StringBuilder();
		sb.append("START-OF-FILE").append("\n");
		Connection myConn = null;
		myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
	    Statement st = myConn.createStatement();
	        
	    ResultSet headerOptions = st.executeQuery(
	    		"SELECT * "
	    		+ "FROM def_PP_Header "
	    		+ "WHERE RequestTypeID = 'DL_Stammdaten' "
	    		+ "AND CurrentRequest = 1 "
	    		+ "ORDER BY Sort " );
	    while (headerOptions.next()){
	    	sb.append(headerOptions.getString(4))
	    	.append(" = ")
	    	.append(headerOptions.getString(5))
	    	.append("\n");
	    }
	    sb.append("START-OF-FIELDS").append("\n");
	    ResultSet fields = st.executeQuery(
	    		"SELECT * "
	    		+ "FROM def_NRequestFields "
	    		+ "WHERE RequestTypeID = 2  "
	    		+ "AND [REQUIRED] = 1 "
	    		+ "ORDER BY SORT");
	    while (fields.next()){
	    	sb.append(fields.getString(4)).append("\n");
	    }   
	    sb.append("END-OF-FIELDS").append("\n");
	    sb.append("START-OF-DATA").append("\n");
	    ResultSet isins = st.executeQuery(
	    		"SELECT DISTINCT ISIN "
	    		+ "FROM " + table 
	    		+ " WHERE BB_CRNCY IS NULL");
	    if((!isins.isBeforeFirst()))
	    		return;
	    while (isins.next()){
	    	sb.append(isins.getString(1)).append("|ISIN|").append("\n");
	    }   
	    
	    sb.append("END-OF-DATA").append("\n");
	    sb.append("END-OF-FILE").append("\n");
	    System.out.println(sb.toString());
	    String currentTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
	    Files.write(Paths.get(path + "\\DL_M_" + currentTime + ".req"), sb.toString().getBytes());
	    
	    st.close();
	    myConn.close();
	}
	
	public static void createBbDLPricingRequest(String table, String path, String day) throws SQLException, IOException, ParseException{
		LocalDate pricingDay = LocalDate.parse(day);
		LocalDate pricingDayPlus1 = pricingDay.plusDays(1);
		LocalDate pricingDayMinus1 = pricingDay.minusDays(1);
		LocalDate pricingDayMinus2 = pricingDay.minusDays(2);
		StringBuilder sb = new StringBuilder();
		sb.append("START-OF-FILE").append("\n");
		Connection myConn = null;
		myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
	    Statement st = myConn.createStatement();
	    
	    ResultSet headerOptions = st.executeQuery(
	    		"SELECT * "
	    		+ "FROM def_PP_Header "
	    		+ "WHERE RequestTypeID = 'DL_Pricing' "
	    		+ "AND CurrentRequest = 1 "
	    		+ "ORDER BY Sort " );
	    while (headerOptions.next()){
	    	sb.append(headerOptions.getString(4))
	    	.append(" = ")
	    	.append(headerOptions.getString(5))
	    	.append("\n");
	    }
	    sb.append("START-OF-FIELDS").append("\n");
	    ResultSet fields = st.executeQuery(
	    		"SELECT * "
	    		+ "FROM def_NRequestFields "
	    		+ "WHERE RequestTypeID = 5  "
	    		+ "AND [REQUIRED] = 1 "
	    		+ "ORDER BY Sort");
	    while (fields.next()){
	    	if(fields.getString(13).equalsIgnoreCase("0"))
	    		sb.append(fields.getString(4)).append("\n");
	    	else {
	    		sb.append(fields.getString(4)).append(":").append(pricingDayPlus1.format(DateTimeFormatter.BASIC_ISO_DATE)).append(":P\n");
	    		sb.append(fields.getString(4)).append(":").append(pricingDay.format(DateTimeFormatter.BASIC_ISO_DATE)).append(":P\n");
	    		sb.append(fields.getString(4)).append(":").append(pricingDayMinus1.format(DateTimeFormatter.BASIC_ISO_DATE)).append(":P\n");
	    		sb.append(fields.getString(4)).append(":").append(pricingDayMinus2.format(DateTimeFormatter.BASIC_ISO_DATE)).append(":P\n");
	    	}
	    }   
	    sb.append("END-OF-FIELDS").append("\n");
	    sb.append("START-OF-DATA").append("\n");
	    ResultSet isins = st.executeQuery(
	    		"SELECT DISTINCT ISIN "
	    		+ "FROM " + table 
	    		+ " WHERE [Type of price] = 'Piece' "
	    		+ "AND ([Fair Price EY] is null OR EY_Price_Origin_Position = 'Last')");
	    if((!isins.isBeforeFirst()))
	    		return;
	    while (isins.next()){
	    	sb.append(isins.getString(1)).append("|ISIN|").append("\n");
	    }   
	    
	    sb.append("END-OF-DATA").append("\n");
	    sb.append("END-OF-FILE").append("\n");
	    System.out.println(sb.toString());
	    String currentTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
	    Files.write(Paths.get(path + "\\DL_P_" + currentTime +".req"), sb.toString().getBytes());
	    st.close();
	    myConn.close();
	}
	
	public static void createBbBVALRequest(String table, String path, String day) throws SQLException, IOException, ParseException{
		LocalDate pricingDay = LocalDate.parse(day);
		StringBuilder sb = new StringBuilder();
		sb.append("START-OF-FILE").append("\n");
		Connection myConn = null;
		myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
	    Statement st = myConn.createStatement();
	    
	    ResultSet headerOptions = st.executeQuery(
	    		"SELECT * "
	    		+ "FROM def_PP_Header "
	    		+ "WHERE RequestTypeID = 'BVAL_Migration' "
	    		+ "AND CurrentRequest = 1 "
	    		+ "ORDER BY Sort " );
	    while (headerOptions.next()){
	    	String exchangeSuffix = "";
	    	if(headerOptions.getString(4).equalsIgnoreCase("PRICING_SOURCE"))
	    		exchangeSuffix = ":LO4PM";
	    	sb.append(headerOptions.getString(4))
	    	.append(" = ")
	    	.append(headerOptions.getString(5))
	    	.append(exchangeSuffix)
	    	.append("\n");
	    }
	    sb.append("START-OF-FIELDS").append("\n");
	    ResultSet fields = st.executeQuery(
	    		"SELECT * "
	    		+ "FROM def_NRequestFields "
	    		+ "WHERE RequestTypeID = 1  "
	    		+ "AND [REQUIRED] = 1 "
	    		+ "ORDER BY Sort");
	    while (fields.next()){
	    	if(fields.getString(13).equalsIgnoreCase("0"))
	    		sb.append(fields.getString(4)).append("\n");
	    	else {
	    		sb.append(fields.getString(4)).append(":").append(pricingDay.format(DateTimeFormatter.BASIC_ISO_DATE)).append("\n");
	    	}
	    }   
	    sb.append("END-OF-FIELDS").append("\n");
	    sb.append("START-OF-DATA").append("\n");
	    ResultSet isins = st.executeQuery(
	    		"SELECT DISTINCT ISIN "
	    		+ "FROM " + table 
	    		+ " WHERE [Type of price] = 'Percent' "
	    		+ "AND ([Fair Price EY] is null OR EY_Price_Origin_Position = 'Last')");
	    if((!isins.isBeforeFirst()))
    		return;
	    while (isins.next()){
	    	sb.append(isins.getString(1)).append("|ISIN|").append("\n");
	    }   
	    
	    sb.append("END-OF-DATA").append("\n");
	    sb.append("END-OF-FILE").append("\n");
	    System.out.println(sb.toString());
	    String currentTime = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
	    Files.write(Paths.get(path + "\\BV_LO_" + currentTime + ".req"), sb.toString().getBytes());
	    st.close();
	    myConn.close();
	}
	
	public static void importInput(String path) throws IOException, InvalidFormatException {
	    //Create file system using specific name
	    //FileInputStream in = new FileInputStream(new File(path));
	    OPCPackage opcpackage = OPCPackage.open(path);
	    //Create Blank workbook
	    XSSFWorkbook workbook = new XSSFWorkbook(opcpackage); 
        XSSFSheet mySheet = workbook.getSheetAt(0);
        Iterator rowIter = mySheet.rowIterator();
//        while(rowIter.hasNext()){
            XSSFRow myRow = (XSSFRow) rowIter.next();
            Iterator cellIter = myRow.cellIterator();
            //Vector cellStoreVector=new Vector();
            List list = new ArrayList();
            while(cellIter.hasNext()){
                XSSFCell myCell = (XSSFCell) cellIter.next();
                list.add(myCell);
                System.out.println(myCell.toString());
            }
//        }
	    
	    workbook.close();
	    System.out.println("input imported successfully");
	}
	
	public static void export(String table, String path) throws SQLException, IOException {
		Connection myConn = null;
		myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
	    Statement st = myConn.createStatement();
	    ResultSet rs = st.executeQuery("Select * from " + table);
	    ResultSetMetaData rsmd = rs.getMetaData();
	    int numColumns = rsmd.getColumnCount();
	    ArrayList<String> colNames = new ArrayList<String>();
	  
	    //Create Blank workbook
	    SXSSFWorkbook workbook = new SXSSFWorkbook(); 
	    workbook.setCompressTempFiles(true);

	    //Create file system using specific name
	    FileOutputStream out = new FileOutputStream(new File(path));

	    SXSSFSheet spreadsheet = workbook.createSheet("Start");
	    spreadsheet.setRandomAccessWindowSize(100);
	    
	    CellStyle headerStyle = workbook.createCellStyle();
	    headerStyle.setAlignment(HorizontalAlignment.CENTER);
	    headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
	    headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    headerStyle.setWrapText(true);
	    
	    SXSSFRow rowHead = spreadsheet.createRow(0);
	    for(int a = 0; a < numColumns; a++) {
	    	String colName = rsmd.getColumnName(a+1);
	    	colNames.add(colName);
	    	SXSSFCell cell = rowHead.createCell(a);
	    	cell.setCellValue(colName);
	    	if(a>1 && a<8)
	    		cell.setCellStyle(headerStyle);
//	    	cell.setCellStyle(style);
	    }
//	    spreadsheet.trackAllColumnsForAutoSizing();
//	    for(int a = 0; a < numColumns; a++) {
//	    	spreadsheet.autoSizeColumn(a+1);
//	    }
	    int i = 1;
	    while (rs.next()){
	    	SXSSFRow row = spreadsheet.createRow(i);
	    	for(int a = 0; a < numColumns; a++) {
	    		SXSSFCell cell = row.createCell(a);
//	    		cell.setCellStyle(style);
	    		String value = rs.getString(a+1);
	    		if(NumberUtils.isCreatable(value)) {
	    			cell.setCellType(CellType.NUMERIC);
	    			cell.setCellValue(Double.parseDouble(value));
	    		}
	    		else
	    			cell.setCellValue(value);
	    	}
	        i++;
	        System.out.println(i);
	    }
	    spreadsheet.createFreezePane(0, 1);
//	    spreadsheet.trackAllColumnsForAutoSizing();
//	    for(int a = 0; a < numColumns; a++) {
//	    	spreadsheet.autoSizeColumn(a+1);
//	    }
	    
	    //create cover page

	    
	    //write operation workbook using file out object 
	    workbook.write(out);
	    out.close();
	    workbook.close();
	    System.out.println("output written successfully");
	}
	
	public static void main(String[] args) throws IOException {
		SXSSFWorkbook workbook = new SXSSFWorkbook(); 
	    SXSSFSheet coverPage = workbook.createSheet("Cover Page");
	    SXSSFSheet eyValuationResults = workbook.createSheet("EY Valuation Results");
	    SXSSFSheet largestPriceDeviations = workbook.createSheet("Largest Price Deviations");
	    SXSSFSheet largestMarketValueDeviations = workbook.createSheet("Largest Market Value Deviations");
	    SXSSFSheet notCoveredPositions = workbook.createSheet("Not Covered Positions");
	    SXSSFSheet dataPreparation = workbook.createSheet("Data Preparation");
	    SXSSFSheet excludedPositions = workbook.createSheet("Excluded Positions");
	    SXSSFSheet clientDelivery = workbook.createSheet("Client Delivery");
	    coverPage.setTabColor(IndexedColors.YELLOW1.getIndex());
	    eyValuationResults.setTabColor(IndexedColors.LIGHT_GREEN.getIndex());
	    largestPriceDeviations.setTabColor(IndexedColors.LIGHT_GREEN.getIndex());
	    largestMarketValueDeviations.setTabColor(IndexedColors.LIGHT_GREEN.getIndex());
	    notCoveredPositions.setTabColor(IndexedColors.LIGHT_GREEN.getIndex());
	    dataPreparation.setTabColor(IndexedColors.AQUA.getIndex());
	    excludedPositions.setTabColor(IndexedColors.AQUA.getIndex());
	    clientDelivery.setTabColor(IndexedColors.GREY_50_PERCENT.getIndex());
	    
	    //add EY logo
	    SXSSFDrawing eyLogo = coverPage.createDrawingPatriarch();
	    FileInputStream stream = new FileInputStream("L:\\10_Entwicklung\\00_Templates\\60_Logos\\English\\EY_Logo_Beam_Tag_Stacked_EN\\EY_Logo_Beam_Tag_Stacked_RGB_EN.png");
	    int pictureIndex = workbook.addPicture(IOUtils.toByteArray(stream), Workbook.PICTURE_TYPE_PNG);
	    ClientAnchor anchor = workbook.getCreationHelper().createClientAnchor();
	    anchor.setCol1(1);
	    anchor.setRow1(2);
	    anchor.setCol2(4);
	    anchor.setRow2(11);
	    eyLogo.createPicture(anchor, pictureIndex);
	    
	    //set colors 
	    CellStyle whiteBackground = workbook.createCellStyle();
	    whiteBackground.setFillForegroundColor(IndexedColors.WHITE.getIndex());
	    whiteBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    for(int i = 0; i < 62; i++) {
	    	SXSSFRow row = coverPage.createRow(i);
	    	for(int j = 0; j < 30; j++)
	    		row.createCell(j).setCellStyle(whiteBackground);
	    }
	    CellStyle yellowBackground = workbook.createCellStyle();
	    yellowBackground.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	    yellowBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    for(int i = 2; i < 11; i++) {
	    	SXSSFRow row = coverPage.getRow(i);
	    	for(int j = 5; j < 30; j++)
	    		row.getCell(j).setCellStyle(yellowBackground);
	    }
	    CellStyle lightGreyBackground = workbook.createCellStyle();
	    lightGreyBackground.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
	    lightGreyBackground.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    for(int i = 29; i < 34; i++) {
	    	SXSSFRow row = coverPage.getRow(i);
	    	for(int j = 5; j < 20; j++)
	    		row.getCell(j).setCellStyle(lightGreyBackground);
	    }
	    
	    //set labels
	    coverPage.getRow(2).getCell(5).setCellValue("Client:");
	    coverPage.getRow(4).getCell(5).setCellValue("Valuation Date:");
	    coverPage.getRow(6).getCell(5).setCellValue("Reporting Currency:");
	    coverPage.getRow(8).getCell(5).setCellValue("Prepared by:");
	    coverPage.getRow(10).getCell(5).setCellValue("Reviewed by:");
	    
	    CellStyle centerAlign = workbook.createCellStyle();
	    coverPage.addMergedRegion(new CellRangeAddress(15, 15, 0, 4));
	    coverPage.getRow(15).getCell(0).setCellValue("Visual Portfolio Output");
	    coverPage.getRow(15).getCell(0).setCellStyle(centerAlign);
	    coverPage.getRow(12).getCell(5).setCellValue("The client prices are compared to our EY fair price derived from the data of different market data providers including Bloomberg, ICE, Markit and Thomson Reuters.");
	    coverPage.getRow(13).getCell(5).setCellValue("We adhere to the pre-defined prefect hierarchy, which reflects the priority in which the data are classified to obtain a fair valuation.");
	    coverPage.getRow(15).getCell(13).setCellValue("Includes all priced and not priced positions in the analysis.");
	    coverPage.getRow(17).getCell(13).setCellValue("Short list including the top 10 of the largest relative deviations found in the portfolio. Please note that this list is already included in the database tab.");
	    coverPage.getRow(19).getCell(13).setCellValue("Short list including the top 10 of the largest absolute deviations found in the portfolio. Please note that this list is already included in the database tab.");
	    coverPage.getRow(21).getCell(13).setCellValue("Positions for which prices were not available from any of our market data providers.");
	    
	    coverPage.addMergedRegion(new CellRangeAddress(23, 23, 0, 4));
	    coverPage.getRow(23).getCell(0).setCellValue("Specialist Input");
	    centerAlign.setAlignment(HorizontalAlignment.CENTER);
	    coverPage.getRow(23).getCell(0).setCellStyle(centerAlign);
	    coverPage.getRow(23).getCell(13).setCellValue("Client data processed by an EY specialist.");
	    coverPage.getRow(25).getCell(13).setCellValue("Positions which were excluded from the analysis.");
	    coverPage.getRow(27).getCell(13).setCellValue("Positions delivered by the client.");
	    
	    coverPage.addMergedRegion(new CellRangeAddress(29, 29, 0, 4));
	    coverPage.getRow(29).getCell(0).setCellValue("Reconciliation of the MV given by the Client");
	    coverPage.getRow(29).getCell(0).setCellStyle(centerAlign);
	    coverPage.getRow(30).getCell(5).setCellValue("Market Value as delivered:");
	    coverPage.getRow(31).getCell(5).setCellValue("Market Value excluded as delivered:");
	    coverPage.getRow(32).getCell(5).setCellValue("Market Value in Scope of Analysis calculated:");
	    coverPage.getRow(33).getCell(5).setCellValue("Difference:");
	    
	    CellStyle bold_style = workbook.createCellStyle();
	    Font bold_font = workbook.createFont();
	    bold_font.setBold(true);
	    bold_style.setFont(bold_font);
	    bold_style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
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
	    CellStyle greenBackground = workbook.createCellStyle();
	    greenBackground.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
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
	    CellStyle aquaBackground = workbook.createCellStyle();
	    aquaBackground.setFillForegroundColor(IndexedColors.AQUA.getIndex());
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
	    CellStyle greyBackground = workbook.createCellStyle();
	    greyBackground.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
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
	    
	    try {
			FileOutputStream out = new FileOutputStream(new File("coverPage.xlsx"));
		    workbook.write(out);
		    out.close();
		    workbook.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
//		try {
//			importInput("L:\\10_Entwicklung\\10_Customer_Data\\28_Phoenix\\10_Input\\2017-12-29\\20171229_phoenix_v11.xlsx");
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (InvalidFormatException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

}
