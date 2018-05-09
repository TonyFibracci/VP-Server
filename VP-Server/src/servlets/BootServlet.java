package servlets;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.json.JSONArray;

import database.HibernateUtil;
import jdk.nashorn.internal.parser.JSONParser;

public class BootServlet implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		HibernateUtil.getSessionFactory();	
		String templateContent;
		try {
			String path = arg0.getServletContext().getRealPath("/WEB-INF/numerics_fields.json");
			templateContent = new String ( Files.readAllBytes( Paths.get(path) ) );
			JSONArray templateNumerics = new JSONArray(templateContent);
			arg0.getServletContext().setAttribute("template_numerics", templateNumerics);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		List<String> vpColumns = new ArrayList<String>();
		vpColumns.add("ISIN");
		vpColumns.add("Valuation quote");
		vpColumns.add("ShortLongFlag");
		vpColumns.add("TypeOfPrice");
		vpColumns.add("Nominal");
		arg0.getServletContext().setAttribute("VP_COLUMNS", vpColumns);
	}

}
