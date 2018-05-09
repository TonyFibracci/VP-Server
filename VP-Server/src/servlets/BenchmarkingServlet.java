package servlets;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Servlet implementation class BenmarkingServlet
 */
@WebServlet("/BenchmarkingServlet")
@MultipartConfig
public class BenchmarkingServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public BenchmarkingServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String theCommand = request.getParameter("command");
			if(theCommand == null) {
				RequestDispatcher dispatcher = request.getRequestDispatcher("/home.jsp");
				dispatcher.forward(request, response);
				return;
			}
			switch (theCommand) {
			case "MAP":
				mapColumns(request, response);
				break;
			default:
				RequestDispatcher dispatcher = request.getRequestDispatcher("/home.jsp");
				dispatcher.forward(request, response);
			}
		} catch (Exception exc) {
			throw new ServletException(exc);
		}
	}

	private void mapColumns(HttpServletRequest request, HttpServletResponse response) throws ServletException {
		try {
			String index = request.getParameter("sheet");
			String headerIndex = request.getParameter("headerRowIndex");
			XSSFWorkbook workbook = (XSSFWorkbook) request.getSession().getAttribute("WORKBOOK");
			XSSFSheet sheet = workbook.getSheetAt(Integer.parseInt(index));
			XSSFRow headerRow = sheet.getRow(Integer.parseInt(headerIndex));
			int lastCellIndex = headerRow.getPhysicalNumberOfCells();
			HttpSession session = request.getSession();
			LinkedHashMap<String,String> columnMapping = new LinkedHashMap<>();
			for(int i = 0; i < lastCellIndex; i++) {
				String colName = headerRow.getCell(i).getStringCellValue();
				columnMapping.put(colName, "");
				System.out.println(colName);
			}
			session.setAttribute("COLUMN_MAPPING", columnMapping);
			RequestDispatcher dispatcher = request.getRequestDispatcher("/map-columns.jsp");
			dispatcher.forward(request, response);
		} catch (Exception exc) {
			throw new ServletException(exc);
		}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Part filePart = request.getPart("file");
		//File uploads = new File("C:\\Users\\deslu001\\Documents\\NordLB\\upload.xlsx");
		try (InputStream input = filePart.getInputStream()) {
//			Files.copy(input, uploads.toPath());
			XSSFWorkbook  workbook = new XSSFWorkbook(input);
			List<String> sheetNames = new ArrayList<String>();
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				XSSFSheet sheet = workbook.getSheetAt(i);
				sheetNames.add(workbook.getSheetAt(i).getSheetName());
				//System.out.println(sheet.getSheetName());
			}
			HttpSession session = request.getSession();
			session.setAttribute("SHEETS", sheetNames);
			session.setAttribute("WORKBOOK", workbook);
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher("/select-sheet.jsp");
		dispatcher.forward(request, response);
	}

}
