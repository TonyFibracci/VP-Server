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
import java.util.HashMap;
import java.util.HashSet;
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
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;

import org.apache.tomcat.util.IntrospectionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.opencsv.CSVReader;


/**
 * Servlet implementation class UploadServlet
 */
@WebServlet("/UploadServlet")
@MultipartConfig
public class UploadServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @throws IOException 
     * @throws FileNotFoundException 
     * @throws URISyntaxException 
     * @see HttpServlet#HttpServlet()
     */
    public UploadServlet() throws FileNotFoundException, IOException, URISyntaxException {
        super();
//        targetInputMapping = new LinkedHashMap<>();
//        fieldValueMapping = new HashMap<>();
//        HashMap<String,String> dummyMapping = new HashMap<>();
//        dummyMapping.put("HALLO", "");
//        dummyMapping.put("WORLD", "");
//        fieldValueMapping.put("trade currency", dummyMapping);
//        try(BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\deslu001\\git\\VP-Server\\VP-Server\\WebContent\\WEB-INF\\target_fields"))) {
//            for(String line; (line = br.readLine()) != null; ) {
//            	targetInputMapping.put(line, "");
//            }
//        }
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
			String theCommand = request.getParameter("command");
			if(theCommand == null) {
				RequestDispatcher dispatcher = request.getRequestDispatcher("/home.jsp");
				dispatcher.forward(request, response);
				return;
			}
			switch (theCommand) {
			case "EXPORT":
				exportMappedCsv(request, response);
				break;
			case "LOAD":
				loadValues(request, response);
				break;
			case "MAP":
				mapValues(request, response);
				break;
			case "CONTINUE":
				showFieldMapping(request, response);
				break;
			default:
				RequestDispatcher dispatcher = request.getRequestDispatcher("/home.jsp");
				dispatcher.forward(request, response);
			}
		} catch (Exception exc) {
			throw new ServletException(exc);
		}
	}

	private void showFieldMapping(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		LinkedHashMap<String,String> targetInputMapping = (LinkedHashMap<String, String>) request.getSession().getAttribute("targetInputMapping");
		for(String targetField : targetInputMapping.keySet()) {
			String inputField = request.getParameter(targetField);
			if(inputField != null)
				targetInputMapping.put(targetField, inputField);
		}
		request.setAttribute("TARGET_INPUT_MAP", targetInputMapping);
		RequestDispatcher dispatcher = request.getRequestDispatcher("/list-field-mapping.jsp");
		dispatcher.forward(request, response);	
	}

	private void mapValues(HttpServletRequest request, HttpServletResponse response) {
		String targetField = request.getParameter("targetField");
		if(fieldValueMapping.containsKey(targetField)) {
			HashMap<String,String> valueMapping = fieldValueMapping.get(targetField);
			for(String key : valueMapping.keySet()) {
				String inputValue = request.getParameter(key);
				valueMapping.put(key, inputValue);
				System.out.println(key + " : " + inputValue);
			}
		}
		
	}

	private void loadValues(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String targetField = request.getParameter("targetField");
		String inputField = request.getParameter("inputField");
		
		LinkedHashMap<String,Integer> inputIndexMapping = (LinkedHashMap<String, Integer>) request.getSession().getAttribute("inputIndexMapping");
		List<String[]> inputRecords = (List<String[]>) request.getSession().getAttribute("inputRecords");
		HashMap<String, HashMap<String,String>> fieldValueMapping = (HashMap<String, HashMap<String, String>>) request.getSession().getAttribute("fieldValueMapping");
		int inputIndex = inputIndexMapping.get(inputField);
		HashSet<String> distinctValues = new HashSet<>();
		int counter = 0;
		for(String[] array : inputRecords) {
			counter++;
			if(counter == 1) {
				continue;
			}
			else {
				distinctValues.add(array[inputIndex]);
			}
		}
		
		request.setAttribute("TARGET_FIELD", targetField);
		request.setAttribute("INPUT_FIELD", inputField);
		request.setAttribute("INPUT_VALUE_SET", distinctValues.toArray());
		request.setAttribute("TARGET_VALUE_SET", fieldValueMapping.get(targetField).keySet().toArray());
		RequestDispatcher dispatcher = request.getRequestDispatcher("/display-mapping.jsp");
		dispatcher.forward(request, response);	
	}

	private void exportMappedCsv(HttpServletRequest request, HttpServletResponse response) {
		for(String targetField : targetInputMapping.keySet()) {
			String inputField = request.getParameter(targetField);
			targetInputMapping.put(targetField, inputField);
		}
		
		for(String[] array : inputRecords) {
			String output = "";
			for(String key : targetInputMapping.keySet()) {
				output += array[inputIndexMapping.get(targetInputMapping.get(key))] + ",";
			}
			System.out.println(output);
		}	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    Part filePart = request.getPart("file"); // Retrieves <input type="file" name="file">
	    File uploads = new File("C:\\Users\\deslu001\\Documents\\NordLB\\upload.xlsx");
	    HttpSession session = request.getSession();
	    LinkedHashMap<String,Integer> inputIndexMapping = new LinkedHashMap<>();
	    try (InputStream input = filePart.getInputStream()) {
	        //Files.copy(input, uploads.toPath());
	    	CSVReader reader = new CSVReader(new InputStreamReader(input));
	    	List<String[]> inputRecords = reader.readAll();
	    	String[] inputFields = inputRecords.get(0);
	    	session.setAttribute("inputIndexMapping", inputIndexMapping);
	    	session.setAttribute("inputFields", inputFields);
	    	session.setAttribute("inputRecords", inputRecords);
			for(int i = 0; i < inputFields.length; i++) {
				inputIndexMapping.put(inputFields[i], i);
			}
			LinkedHashMap<String, String> targetInputMapping = new LinkedHashMap<>();
			HashMap<String, HashMap<String,String>> fieldValueMapping = new HashMap<>();;
			JSONArray targetArray = (JSONArray) request.getServletContext().getAttribute("template_numerics");
			for(int i = 0; i < targetArray.length(); i++) {
				JSONObject obj = targetArray.getJSONObject(i);
				targetInputMapping.put(obj.getString("ID"), "");
				if(obj.optJSONArray("Values") != null) {
					JSONArray values = obj.getJSONArray("Values");
					HashMap<String,String> dummyMapping = new HashMap<>();
					for(int j = 0; j < values.length(); j++) {
						dummyMapping.put(values.getString(j), "");
					}
					fieldValueMapping.put(obj.getString("ID"), dummyMapping);
				}
			}
//	        try(BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\deslu001\\git\\VP-Server\\VP-Server\\WebContent\\WEB-INF\\target_fields"))) {
//	        	for(String line; (line = br.readLine()) != null; ) {
//	        		targetInputMapping.put(line, "");
//	        	}
//	        }
			session.setAttribute("fieldValueMapping", fieldValueMapping);
	        session.setAttribute("targetInputMapping", targetInputMapping);
			request.setAttribute("INPUT_FIELDS", Arrays.asList(inputRecords.get(0)));
			request.setAttribute("TARGET_FIELDS", targetInputMapping.keySet());
			reader.close();
	    }
	    
		RequestDispatcher dispatcher = request.getRequestDispatcher("/list-students.jsp");
		dispatcher.forward(request, response);
	}

}
