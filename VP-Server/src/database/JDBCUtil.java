package database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import com.microsoft.sqlserver.jdbc.SQLServerDataTable;
import com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement;

import model.GlobalConstants;



public class JDBCUtil {
	

	private String userName;
	
	public JDBCUtil(String userName) {
		this.userName = userName;
	}
	
	public void createBVALTable(List<String> fields) throws Exception {
		Connection myConn = null;
		String tableName = userName + GlobalConstants.IMPORT_SUFFIX;
		try {
			myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
			SQLServerDataTable tvp = new SQLServerDataTable();
			tvp.addColumnMetadata("field_name", Types.NVARCHAR);
			for(String field : fields) {
				tvp.addRow(field);
			}
			String execDropIfExist = "EXEC spDropIfExist ?";
			SQLServerPreparedStatement pst = (SQLServerPreparedStatement) myConn.prepareStatement(execDropIfExist);
			pst.setString(1, tableName);
			pst.execute();
			String execStoredProcedure = "EXEC spCreateBVALTable ?, ?";
			pst = (SQLServerPreparedStatement) myConn.prepareStatement(execStoredProcedure);
			pst.setStructured(1, "dbo.FieldTableType", tvp);
			pst.setString(2, tableName);
			pst.execute();
		}
		catch(Exception e) {
			throw e;
		}
		finally {
			try {
				myConn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void createMarkitTable(List<String> fields) throws Exception {
		Connection myConn = null;
		String tableName = userName + GlobalConstants.IMPORT_SUFFIX;
		try {
			myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
			SQLServerDataTable tvp = new SQLServerDataTable();
			tvp.addColumnMetadata("field_name", Types.NVARCHAR);
			for(String field : fields) {
				tvp.addRow(field);
			}
			String execDropIfExist = "EXEC spDropIfExist ?";
			SQLServerPreparedStatement pst = (SQLServerPreparedStatement) myConn.prepareStatement(execDropIfExist);
			pst.setString(1, tableName);
			pst.execute();
			String execStoredProcedure = "EXEC spCreateMarkitTable ?, ?";
			pst = (SQLServerPreparedStatement) myConn.prepareStatement(execStoredProcedure);
			pst.setStructured(1, "dbo.FieldTableType", tvp);
			pst.setString(2, tableName);
			pst.execute();
			Thread.sleep(1000);
		}
		catch(Exception e) {
			throw e;
		}
		finally {
			try {
				myConn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void createTestTable(List<String> fields) throws Exception {
		Connection myConn = null;
		try {
			myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
			SQLServerDataTable tvp = new SQLServerDataTable();
			tvp.addColumnMetadata("field_name", Types.NVARCHAR);
			for(String field : fields) {
				tvp.addRow(field);
			}
			String execStoredProcedure = "EXEC spCreateTestTable ?";
			SQLServerPreparedStatement pst = (SQLServerPreparedStatement) myConn.prepareStatement(execStoredProcedure);
			pst = (SQLServerPreparedStatement) myConn.prepareStatement(execStoredProcedure);
			pst.setStructured(1, "dbo.FieldTableType", tvp);
			pst.execute();
		}
		catch(Exception e) {
			throw e;
		}
		finally {
			try {
				myConn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void createHistoricalTable(List<String> fields) throws Exception {
		Connection myConn = null;
		String errorMessage = null;
		String tableName = userName + GlobalConstants.IMPORT_SUFFIX;
		try {
			myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
			SQLServerDataTable tvp = new SQLServerDataTable();
			tvp.addColumnMetadata("field_name", Types.NVARCHAR);
			for(String field : fields) {
				tvp.addRow(field);
			}
			String execDropIfExist = "EXEC spDropIfExist ?";
			SQLServerPreparedStatement pst = (SQLServerPreparedStatement) myConn.prepareStatement(execDropIfExist);
			pst.setString(1, tableName);
			pst.execute();
			String execStoredProcedure = "EXEC spCreateHistoricalTable ?, ?";
			pst = (SQLServerPreparedStatement) myConn.prepareStatement(execStoredProcedure);
			pst.setStructured(1, "dbo.FieldTableType", tvp);
			pst.setString(2, tableName);
			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(rs != null) {
		        rs.next(); 
	            errorMessage = rs.getString("ErrorMessage");            
			}
		}
		catch(Exception e) {
			if (!errorMessage.equalsIgnoreCase(""))
				throw new Exception(errorMessage);
			else
				throw e;
		}
		finally {
			try {
				myConn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void createMarkitTable2(List<String> fields) throws Exception {
		Connection myConn = null;
		String tableName = userName + GlobalConstants.IMPORT_SUFFIX;
		try {
			myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
			SQLServerDataTable tvp = new SQLServerDataTable();
			tvp.addColumnMetadata("field_name", Types.NVARCHAR);
			for(String field : fields) {
				tvp.addRow(field);
			}
			String execDropIfExist = "EXEC spDropIfExist ?";
			SQLServerPreparedStatement pst = (SQLServerPreparedStatement) myConn.prepareStatement(execDropIfExist);
			pst.setString(1, tableName);
			pst.execute();
			String execStoredProcedure = "EXEC spCreateMarkitTable2 ?, ?";
			pst = (SQLServerPreparedStatement) myConn.prepareStatement(execStoredProcedure);
			pst.setStructured(1, "dbo.FieldTableType", tvp);
			pst.setString(2, tableName);
			pst.execute();
		}
		catch(Exception e) {
			throw e;
		}
		finally {
			try {
				myConn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public  void createDLMasterTable(List<String> fields) throws Exception {
		Connection myConn = null;
		String tableName = userName + GlobalConstants.IMPORT_SUFFIX;
		try {
			myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
			SQLServerDataTable tvp = new SQLServerDataTable();
			tvp.addColumnMetadata("field_name", Types.NVARCHAR);
			for(String field : fields) {
				tvp.addRow(field);
			}
			String execDropIfExist = "EXEC spDropIfExist ?";
			SQLServerPreparedStatement pst = (SQLServerPreparedStatement) myConn.prepareStatement(execDropIfExist);
			pst.setString(1, tableName);
			pst.execute();
			String execStoredProcedure = "EXEC spCreateDLMasterTable ?, ?";
			pst = (SQLServerPreparedStatement) myConn.prepareStatement(execStoredProcedure);
			pst.setStructured(1, "dbo.FieldTableType", tvp);
			pst.setString(2, tableName);
			pst.execute();
		}
		catch(Exception e) {
			throw e;
		}
		finally {
			try {
				myConn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public  void createDLPricingTable(List<String> fieldNamesWithDate, List<String> fieldNamesWithoutDate) throws Exception {
		Connection myConn = null;
		String tableName = userName + GlobalConstants.IMPORT_SUFFIX;
		try {
			myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
			SQLServerDataTable tvpWithDate = new SQLServerDataTable();
			tvpWithDate.addColumnMetadata("field_name", Types.NVARCHAR);
			for(String field : fieldNamesWithDate) {
				tvpWithDate.addRow(field);
			}
			SQLServerDataTable tvpWithoutDate = new SQLServerDataTable();
			tvpWithoutDate.addColumnMetadata("field_name", Types.NVARCHAR);
			for(String field : fieldNamesWithoutDate) {
				tvpWithoutDate.addRow(field);
			}
			
			String execDropIfExist = "EXEC spDropIfExist ?";
			SQLServerPreparedStatement pst = (SQLServerPreparedStatement) myConn.prepareStatement(execDropIfExist);
			pst.setString(1, tableName);
			pst.execute();
			
			String execStoredProcedure = "EXEC spCreateDLPricingTable ?, ?, ?";
			pst = (SQLServerPreparedStatement) myConn.prepareStatement(execStoredProcedure);
			pst.setStructured(1, "dbo.FieldTableType", tvpWithDate);
			pst.setStructured(2, "dbo.FieldTableType", tvpWithoutDate);
			pst.setString(3, tableName);
			pst.execute();
		}
		catch(Exception e) {
			throw e;
		}
		finally {
			try {
				myConn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public  String importCsvBcp(String path) throws Exception {
		StringBuffer output = new StringBuffer();
		Process p = null;
		String tableName = (userName + GlobalConstants.IMPORT_SUFFIX);
		try {
			System.out.println("start import");
			p = Runtime.getRuntime().exec("bcp MarketDB.dbo."
					+ tableName
					+ " in \""
					+ path
					+ "\" -S\"" + GlobalConstants.SERVER 
					+ "\" -T -t ; -r \\n -c -F 2");
			System.out.println("wait import");
			BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line = "";
			while ((line = reader.readLine())!= null) {
				output.append(line + "\n");
				System.out.println(line);
			}
			p.waitFor();
		} catch (Exception e) {
			throw e;
		}	
		String outputString = output.toString();
		if(outputString.contains("Error") || outputString.contains("\n0 rows copied")) {
			throw new Exception(outputString);
		}
		return output.toString();
	}
	
	
	public  void importBVALTable(String pricingDay, String targetTable) throws Exception {
		Connection myConn = null;
		String sourceTable = userName + GlobalConstants.IMPORT_SUFFIX;
		String errorMessage = "";
		try {
			myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
			String execStoredProcedure = "EXEC spImportBVALTable2 ?, ?, ?";
			SQLServerPreparedStatement pst = (SQLServerPreparedStatement) myConn.prepareStatement(execStoredProcedure);
			pst.setString(1, pricingDay);
			pst.setString(2, targetTable);
			pst.setString(3, sourceTable);
			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(rs != null) {
		        rs.next(); 
	            errorMessage = rs.getString("ErrorMessage");            
			}
		}
		catch(Exception e) {
			if (!errorMessage.equalsIgnoreCase(""))
				throw new Exception(errorMessage);
			else
				throw e;
		}
		finally {
			try {
				myConn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public  void importHistoricalTable(String targetTable) throws Exception {
		Connection myConn = null;
		String sourceTable = userName + GlobalConstants.IMPORT_SUFFIX;
		String errorMessage = "";
		try {
			myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
			String execStoredProcedure = "EXEC spImportHistoricalTable ?, ?";
			SQLServerPreparedStatement pst = (SQLServerPreparedStatement) myConn.prepareStatement(execStoredProcedure);
			pst.setString(1, targetTable);
			pst.setString(2, sourceTable);
			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(rs != null) {
		        rs.next(); 
	            errorMessage = rs.getString("ErrorMessage");            
			}
		}
		catch(Exception e) {
			if (!errorMessage.equalsIgnoreCase(""))
				throw new Exception(errorMessage);
			else
				throw e;
		}
		finally {
			try {
				myConn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public  void importMarkitTable(String targetTable) throws Exception {
		Connection myConn = null;
		String sourceTable = userName + GlobalConstants.IMPORT_SUFFIX;
		String errorMessage = "";
		try {
			myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
			String execStoredProcedure = "EXEC spImportMarkitTable ?, ?";
			SQLServerPreparedStatement pst = (SQLServerPreparedStatement) myConn.prepareStatement(execStoredProcedure);
			pst.setString(1, targetTable);
			pst.setString(2, sourceTable);
			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(rs != null) {
		        rs.next(); 
	            errorMessage = rs.getString("ErrorMessage");            
			}
		}
		catch(Exception e) {
			if (!errorMessage.equalsIgnoreCase(""))
				throw new Exception(errorMessage);
			else
				throw e;
		}
		finally {
			try {
				myConn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	public void importDLMasterTable(String targetTable) throws Exception {
		Connection myConn = null;
		String sourceTable = userName + GlobalConstants.IMPORT_SUFFIX;
		String errorMessage = "";
		try {
			myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
			String execStoredProcedure = "EXEC spImportDLMasterTable2 ?, ?";
			SQLServerPreparedStatement pst = (SQLServerPreparedStatement) myConn.prepareStatement(execStoredProcedure);
			pst.setString(1, targetTable);
			pst.setString(2, sourceTable);
			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(rs != null) {
		        rs.next(); 
	            errorMessage = rs.getString("ErrorMessage");            
			}
		}
		catch(Exception e) {
			if (!errorMessage.equalsIgnoreCase(""))
				throw new Exception(errorMessage);
			else
				throw e;
		}
		finally {
			try {
				myConn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void updateMapSecID() throws Exception {
		Connection myConn = null;
		String sourceTable = userName + GlobalConstants.IMPORT_SUFFIX;
		String errorMessage = "";
		try {
			myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
			String execStoredProcedure = 
					"UPDATE map "
					+ "SET map.CUSIP = source.ID_CUSIP, map.SEDOL = source.ID_SEDOL1, map.WKN = source.ID_WERTPAPIER "
					+ "FROM map_secID map "
					+ "INNER JOIN " + sourceTable + " source "
					+ "ON map.ISIN = source.SECURITIES "
					+ "WHERE source.ID_CUSIP IS NOT NULL";
			SQLServerPreparedStatement pst = (SQLServerPreparedStatement) myConn.prepareStatement(execStoredProcedure);
			pst.execute();
			ResultSet rs = pst.getResultSet();
			if(rs != null) {
		        rs.next(); 
	            errorMessage = rs.getString("ErrorMessage");            
			}
		}
		catch(Exception e) {
			if (!errorMessage.equalsIgnoreCase(""))
				throw new Exception(errorMessage);
			else
				throw e;
		}
		finally {
			try {
				myConn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public  void importDLPricingTable(String targetTable, List<String> fieldNamesWithoutDate) throws Exception {
		Connection myConn = null;
		String sourceTable = userName + GlobalConstants.IMPORT_SUFFIX;
		try {
			myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
			
			SQLServerDataTable tvpWithoutDate = new SQLServerDataTable();
			tvpWithoutDate.addColumnMetadata("field_name", Types.NVARCHAR);
			for(String field : fieldNamesWithoutDate) {
				tvpWithoutDate.addRow(field);
			}
			
			String execStoredProcedure = "EXEC spImportDLPricingTable2 ?, ?, ?";
			SQLServerPreparedStatement pst = (SQLServerPreparedStatement) myConn.prepareStatement(execStoredProcedure);
			pst.setString(1, targetTable);
			pst.setString(2, sourceTable);
			pst.setStructured(3, "dbo.FieldTableType", tvpWithoutDate);
			pst.execute();
		}
		catch(Exception e) {
			throw e;
		}
		finally {
			try {
				myConn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	public static void main(String[] args) {
//		userName = "DESLU001";
//		try {
//			BloombergRequest req = new BloombergRequest();
//			
//			List<String> fields = new ArrayList<String>();
//			fields.add("PX_LAST");
//			fields.add("PX_VOLUME");
//			fields.add("PX_ASK");
//			fields.add("PX_BID");
//			fields.add("PX_FIXING");
//			fields.add("PX_MID");
//			fields.add("FUND_NET_ASSET_VAL");
//			createHistoricalTable(fields);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		try {
//			String a = importCsvBcp("L:\\60_Maschine\\Back-Up\\develop_SQL-anbindung\\pop\\BV_LO20171024_15_22.csv");
//			System.out.println(a);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
//		importCsv("C:\\Users\\deslu001\\Desktop\\Test Client VP\\BV_LO20171024_15_22_sql.csv", "wowididit");
//		List<String> stringList = new ArrayList<String>();
//		stringList.add("BVAL_CMO_PRICING_STRUCTURE");
//		stringList.add("BVAL_ASSET_CLASS");
//		stringList.add("BVAL_WORKOUT_PX_BID");
//		stringList.add("BVAL_EFFECTIVE_NUMBER");
//		stringList.add("BVAL_STANDARD_DEVIATION");
//		stringList.add("BVAL_TOTAL_EXECUTABLE_BIDS");
//		stringList.add("BVAL_TOTAL_EXECUTABLE_ASKS");
//		stringList.add("BVAL_TOTAL_CONTRIBUTOR_BIDS");
//		stringList.add("BVAL_TOTAL_CONTRIBUTOR_ASKS");
//		stringList.add("BVAL_POOL_SEASONING_ADJUSTMENT");
//		stringList.add("BVAL_POOL_ODD_LOT_ADJUSTMENT");
//		stringList.add("BVAL_POOL_LOAN_CHAR_ADJUSTMENT");
//		stringList.add("BVAL_CMO_GNMA_ADJUSTMENT");
//		stringList.add("BVAL_PRICING_DOMAIN");
//		stringList.add("BVAL_SNAPSHOT");
//		stringList.add("BVAL_CMO_COUPON_ADJUSTMENT");
//		stringList.add("BVAL_CMO_OBSERVATION_COUNT");
//		stringList.add("BVAL_RELEASED_STATE");
//		stringList.add("BVAL_BREAKEVEN_PRICE");
//		stringList.add("BVAL_PERCENT_BREAKEVEN_PRICE");
//		stringList.add("BVAL_WEIGHTED_AVERAGE_BCM_VPR");
//		stringList.add("BVAL_WEIGHTED_AVERAGE_BCM_CDR");
//		stringList.add("BVAL_WEIGHTED_AVERAGE_BCM_LSV");
//		stringList.add("BVAL_BID_PRICE");
//		stringList.add("BVAL_MID_PRICE");
//		stringList.add("BVAL_ASK_PRICE");
//		stringList.add("BVAL_BID_SCORE");
//		stringList.add("BVAL_MID_SCORE");
//		stringList.add("BVAL_ASK_SCORE");
//		stringList.add("BVAL_BENCHMARK_BID");
//		stringList.add("BVAL_POOL_TBA_EXECUTABLE_BID_PX");
//		stringList.add("BVAL_SPRD_BENCHMARK_ID");
//		stringList.add("BVAL_SPRD_BENCHMARK_BID");
//		stringList.add("BVAL_OAS_CURVE_ID");
//		stringList.add("BVAL_CMO_BASE_I_SPREAD");
//		stringList.add("BVAL_BID_YIELD");
//		stringList.add("BVAL_MID_YIELD");
//		stringList.add("BVAL_ASK_YIELD");
//		stringList.add("BVAL_YLD_AAA_BENCHMARK");
//		passTVP(stringList);
		
//		String GlobalConstants.JDBC_URL = "jdbc_sqlserver_//DE2236240W1\\SQLEXPRESS2014;databaseName=MarketDB;integratedSecurity=true";
	
		try {
			Connection myConn = DriverManager.getConnection(GlobalConstants.JDBC_URL);
			String query = "CREATE TABLE KOOL (nr int)";
			myConn.createStatement().executeUpdate(query);
//			PreparedStatement ps = myConn.prepareStatement(
//					"WITH temp (ISIN) as "
//					+ "(VALUES");
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
//		List<Upload> list = StoredProcedure.getUnimportedUploadsByUser("DESLU001");
//		for(Upload u _ list) {
//			System.out.println(u.getEngagementCode());
//		}
//		
//		SessionFactory factory = new Configuration()
//				.configure("hibernate.cfg.xml")
//				.addAnnotatedClass(Upload.class)
//				.buildSessionFactory();
//		Session session = factory.getCurrentSession();
//		
//		try {
//			
//			Upload upload = new Upload(null, null, null, null, null, null, null, false, null);
//			upload.setEngagementCode("3434");
//			upload.setClient("Test Client 3");
//			
//			session.beginTransaction();
//			session.save(upload);
//			session.getTransaction().commit();
//			
//		} finally {
//			factory.close();
//		}
		
//		Upload upload = new Upload();
//		upload.setEngagementCode("11112222");
//		upload.setClient("Test Client");	
//		
//		EntityManagerFactory emf = Persistence.createEntityManagerFactory("PersistenceUnit");
//		EntityManager em = emf.createEntityManager();
//		em.getTransaction().begin();
//		em.persist(upload);
//		em.getTransaction().commit();
//		em.close();
	}
	

}
