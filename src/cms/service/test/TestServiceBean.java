package cms.service.test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.NamingException;

import cms.service.db.LocalContext;
import cms.service.db.LocalContextFactory;
import cms.service.db.LocalDataSource;
import cms.service.db.LocalDbException;


public class TestServiceBean {
	
	private Connection con;
	
	
	public void startProcess() {
		LocalContext ctx;
        LocalDataSource ds;
        String dbServer="";
		String dbUser="cms";
		String dbPassword="";
		String dbInstance="";
		String url="jdbc:oracle://"+dbServer+":1521/"+dbInstance;
		try {
				
				ctx = LocalContextFactory.createLocalContext("oracle.jdbc.OracleDriver");
				ctx.addDataSource("jdbc/ds1",url, dbUser, dbPassword);
				ds = (LocalDataSource) ctx.lookup("jdbc/ds1");
				con = ds.getConnection();
				Statement st =con.createStatement();
				st.execute("select * from table_user");
				ResultSet rs=st.getResultSet();
				while(rs.next()){
					String x=rs.getString("name");
					System.out.println("x="+x);
				}
				
		} catch (LocalDbException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
						
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
		
		
		try {
			remove$$("Road !!@amp; construction");
			//TestServiceBean b = new TestServiceBean();
		    //b.startProcess();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	                  
	}
	
	private static String remove$$(String val){
		String ret=val;
		
		if(val.contains("!")){
			System.out.println("Val contains $$="+ret);
			if(ret.contains("!!@amp;")){
				ret=ret.replaceAll("!!@amp;", "&");
				System.out.println(" ret1 Val contains $$="+ret);
				
			}
			if(ret.contains("$$!quot;")){
				ret=ret.replaceAll("$$!quot;", "\"");
			}
			if(ret.contains("$$!apos;")){
				ret=ret.replaceAll("$$!apos;", "'");
			}
			if(ret.contains("$$!lt;")){
				ret=ret.replaceAll("$$!lt;", "<");
			}
			if(ret.contains("$$!gt;")){
				ret=ret.replaceAll("$$!gt;", ">");
			}
		}
		System.out.println(" ret2 Val contains $$="+ret);
		return ret;
	}
}
