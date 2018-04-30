package servlets;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Stream;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.tomcat.util.IntrospectionUtils;

import com.opencsv.CSVReader;


/**
 * Servlet implementation class UploadServlet
 */
@WebServlet("/UploadServlet")
@MultipartConfig
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	LinkedHashMap<String,String> mapping;
	List<String[]> myEntries; 
	String[] inputFields;
	LinkedHashMap<String,Integer> inputIndexMapping;
       
    /**
     * @throws IOException 
     * @throws FileNotFoundException 
     * @throws URISyntaxException 
     * @see HttpServlet#HttpServlet()
     */
    public UploadServlet() throws FileNotFoundException, IOException, URISyntaxException {
        super();
        mapping = new LinkedHashMap<>();
        try(BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\deslu001\\git\\VP-Server\\VP-Server\\WebContent\\WEB-INF\\target_fields"))) {
            for(String line; (line = br.readLine()) != null; ) {
            	mapping.put(line, "");
            }
        }
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String theCommand = request.getParameter("command");
		if(theCommand == null) {
			RequestDispatcher dispatcher = request.getRequestDispatcher("/home.jsp");
			dispatcher.forward(request, response);
			return;
		}
		switch (theCommand) {
		case "export":
			exportMappedCsv(request, response);
			break;
		default:
			RequestDispatcher dispatcher = request.getRequestDispatcher("/home.jsp");
			dispatcher.forward(request, response);
		}
		RequestDispatcher dispatcher = request.getRequestDispatcher("/home.jsp");
		dispatcher.forward(request, response);
	}

	private void exportMappedCsv(HttpServletRequest request, HttpServletResponse response) {
		for(String targetField : mapping.keySet()) {
			String inputField = request.getParameter(targetField);
			mapping.put(targetField, inputField);
		}
		
		for(String[] array : myEntries) {
			String output = "";
			for(String key : mapping.keySet()) {
				output += array[inputIndexMapping.get(mapping.get(key))] + ",";
			}
			System.out.println(output);
		}	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">
	    File uploads = new File("C:\\Users\\deslu001\\Documents\\NordLB\\upload");
	    inputIndexMapping = new LinkedHashMap<>();
	    try (InputStream input = filePart.getInputStream()) {
	        //Files.copy(input, uploads.toPath());
	    	CSVReader reader = new CSVReader(new InputStreamReader(input));
			myEntries = reader.readAll();
			inputFields = myEntries.get(0);
			for(int i = 0; i < inputFields.length; i++) {
				inputIndexMapping.put(inputFields[i], i);
			}
			request.setAttribute("INPUT_FIELDS", Arrays.asList(myEntries.get(0)));
			request.setAttribute("TARGET_FIELDS", mapping.keySet());
			reader.close();
	    }
		RequestDispatcher dispatcher = request.getRequestDispatcher("/list-students.jsp");
		dispatcher.forward(request, response);
	}

}
