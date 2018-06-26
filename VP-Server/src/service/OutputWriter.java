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
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import model.GlobalConstants;
import model.OutputMessage;
import model.User;
import utils.StringUtil;


public class OutputWriter {
	
	private static final String SCRIPT_PATH = "C:\\Users\\DESLU001\\Documents\\SQL Server Management Studio\\outputScript2.sql";
	public static final String OUTPUT_PREFIX = "OUTPUT_";
	public static final String INPUT_PREFIX = "DTP_";
	
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
	    ResultSet rs = st.executeQuery("Select * from " + outputTable);
	    populateSheetContent(sheetName, rs);
	}
	
	private void writeValuationResults() throws SQLException {
		String outputTable = OUTPUT_PREFIX + message.getJob().getPreparer().getId();
		Connection myConn = null;
		myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
	    Statement st = myConn.createStatement();
	    String sheetName = "EY Valuation Results";
	    ResultSet rs = st.executeQuery("Select * from " + outputTable);
	    populateSheetContent(sheetName, rs);
	}
	
	private void writeNotCoveredPositions() throws SQLException {
		String outputTable = OUTPUT_PREFIX + message.getJob().getPreparer().getId();
		Connection myConn = null;
		myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
	    Statement st = myConn.createStatement();
	    String sheetName = "Not Covered Positions";
	    ResultSet rs = st.executeQuery("Select * from " + outputTable + " WHERE [Fair Price EY] is null");
	    populateSheetContent(sheetName, rs);
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
	    rs = st.executeQuery("Select TOP (" + numberToBeDisplayed +") * from " + outputTable + " Order by [Price Deviation Percent] desc");
	    populateSheetContent(sheetName, rs);
	    SXSSFSheet sheet = workbook.getSheet(sheetName);
	    CellStyle redCellStyle = workbook.createCellStyle();
	    redCellStyle.setAlignment(HorizontalAlignment.CENTER);
	    redCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	    redCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
	    redCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    for(int i = 0; i < numberToBeDisplayed + 1; i++) {
	    	if(sheet.getRow(i) == null)
	    		continue;
	    	sheet.getRow(i).getCell(7).setCellStyle(redCellStyle);
	    }
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
	    rs = st.executeQuery("Select TOP (" + numberToBeDisplayed +") * from " + outputTable + " Order by [Value Deviation EY] desc");
	    populateSheetContent(sheetName, rs);
	    SXSSFSheet sheet = workbook.getSheet(sheetName);
	    CellStyle redCellStyle = workbook.createCellStyle();
	    redCellStyle.setAlignment(HorizontalAlignment.CENTER);
	    redCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	    redCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
	    redCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    for(int i = 0; i < numberToBeDisplayed + 1; i++) {
	    	if(sheet.getRow(i) == null)
	    		continue;
	    	sheet.getRow(i).getCell(10).setCellStyle(redCellStyle);
	    }
	}

	private void populateSheetContent(String sheetName, ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
	    int numColumns = rsmd.getColumnCount();
	    

	    SXSSFSheet currentSheet = workbook.createSheet(sheetName);
	    currentSheet.setTabColor(IndexedColors.LIGHT_GREEN.getIndex());
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
	    
	    CellStyle yellowHeader = workbook.createCellStyle();
	    yellowHeader.cloneStyleFrom(wrapText);
	    yellowHeader.setFillForegroundColor(IndexedColors.YELLOW.getIndex());
	    
	    CellStyle redCellStyle = workbook.createCellStyle();
	    redCellStyle.setAlignment(HorizontalAlignment.CENTER);
	    redCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
	    redCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
	    redCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
	    
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
		SXSSFSheet coverPage = workbook.createSheet("Cover Page");
		coverPage.setTabColor(IndexedColors.YELLOW1.getIndex());
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
	    
	    coverPage.getRow(2).getCell(8).setCellValue(message.getJob().getCustomer());
	    coverPage.getRow(4).getCell(8).setCellValue(StringUtil.convertDateFormat(message.getJob().getPricingDay()));
	    coverPage.getRow(6).getCell(8).setCellValue(message.getJob().getCurrency());
	    
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
	}
	
	private void export() {
		try {
			System.out.println("start coverpage");
			writeCoverPage();
			System.out.println("coverpage done");
			writeValuationResultsPreparer();
			System.out.println("valuation results done");
			writeLargestPriceDeviations();
			System.out.println("largest price devs done");
			writeLargestMarketValueDeviations();
			System.out.println("largest market devs done");
		    //SXSSFSheet largestPriceDeviations = workbook.createSheet("Largest Price Deviations");
		    //SXSSFSheet largestMarketValueDeviations = workbook.createSheet("Largest Market Value Deviations");
		    writeNotCoveredPositions();
		    System.out.println("not covered done");
		    //SXSSFSheet notCoveredPositions = workbook.createSheet("Not Covered Positions");
		    SXSSFSheet dataPreparation = workbook.createSheet("Data Preparation");
		    SXSSFSheet excludedPositions = workbook.createSheet("Excluded Positions");
		    SXSSFSheet clientDelivery = workbook.createSheet("Client Delivery");
		    //largestPriceDeviations.setTabColor(IndexedColors.LIGHT_GREEN.getIndex());
		    //largestMarketValueDeviations.setTabColor(IndexedColors.LIGHT_GREEN.getIndex());
		    //notCoveredPositions.setTabColor(IndexedColors.LIGHT_GREEN.getIndex());
		    dataPreparation.setTabColor(IndexedColors.AQUA.getIndex());
		    excludedPositions.setTabColor(IndexedColors.AQUA.getIndex());
		    clientDelivery.setTabColor(IndexedColors.GREY_50_PERCENT.getIndex());
		    //Create file system using specific name
		    FileOutputStream out = new FileOutputStream(new File(message.getOutputPath()));
		    workbook.write(out);
		    out.close();
		    workbook.close();
//		    XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(message.getOutputPath()));
//		    XSSFSheet sheet = wb.getSheet("");
//		    CellStyle redCellStyle = workbook.createCellStyle();
//		    redCellStyle.setAlignment(HorizontalAlignment.CENTER);
//		    redCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
//		    redCellStyle.setFillForegroundColor(IndexedColors.RED.getIndex());
//		    redCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void run() {
		try {
			if(message.getStatus() == OutputMessage.STATUS_PREPARE) {
				System.out.println("start calc...");
				Process evaluation = new ProcessBuilder(
						"sqlcmd", 
						"-E", 
						"-d",
						GlobalConstants.DATABASE,
						"-S",
						GlobalConstants.SERVER,
						"-v",
						"pricingDay=" + StringUtil.convertDateFormat(message.getJob().getPricingDay()),
						"inputTable=" + INPUT_PREFIX + message.getJob().getPreparer().getId(),
						"outputTable=" + OUTPUT_PREFIX + message.getJob().getPreparer().getId(),
						"currency=" + message.getJob().getCurrency(),
						"priceCategory=" + message.getPriceCategory(),
						"-i",
						SCRIPT_PATH).start();
				evaluation.waitFor();
			} else if(message.getStatus() == OutputMessage.STATUS_REQUEST) {
				System.out.println("Excel start...");
				String outputTable = OUTPUT_PREFIX + message.getJob().getPreparer().getId();
				String outputFolderPath = new File(message.getOutputPath()).getParentFile().getAbsolutePath();
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
			} else if(message.getStatus() == OutputMessage.STATUS_OUTPUT) {
				System.out.println("Excel start...");
				export();
			}
//			Excel.export(outputTable, message.getOutputPath());
//			export();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
