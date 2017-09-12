package cms.service.test;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import cms.service.db.LocalContext;
import cms.service.db.LocalContextFactory;
import cms.service.db.LocalDataSource;
import cms.service.db.LocalDbException;



public class TestTomcatServiceBean {
	
	private Connection con;
	
	public void startProcess() {
	    
        LocalDataSource ds;
		try {
				ds = (LocalDataSource) new LocalContext().lookup("jdbc/ds1");
				con = ds.getConnection();
				
		} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        //do something with connection        
    }  
	
	public static void main(String[] args) { 
		String dbServer="";
		String dbUser="cms";
		String dbPassword="";
		String dbInstance="";
		String url="jdbc:oracle://"+dbServer+":1521/"+dbInstance;
		LocalContext ctx;
		try {
			ctx = LocalContextFactory.createLocalContext("oracle.jdbc.OracleDriver");
			ctx.addDataSource("jdbc/js1",url, dbUser, dbPassword);
		   
			TestServiceBean b = new TestServiceBean();
		    b.startProcess();
		} catch (LocalDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	                  
	}
	
	public Connection getTomcatConnection() throws SQLException {
		Connection conn =null;
		try {
			
			
			//first try to get connection using tomcat container
			Context initContext = new InitialContext();
			Context envContext  = (Context)initContext.lookup("java:/comp/env");
			DataSource ds = (DataSource)envContext.lookup("jdbc/myoracle");
		     conn = ds.getConnection();
			return (conn);

		}
		catch (SQLException e) {
			System.err.println("Failed to get a database connection: " + e);
			e.printStackTrace();
		} catch (NamingException e) {
			// TODO Auto-generated catch block
			System.err.println("Failed to map tomcat container managed database connection: " + e);
			e.printStackTrace();
		}

		return conn;
	}
}
