package cms.service.jdbc;

import java.sql.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cms.service.db.JndiDataSource;





/*
 * @author S.K.jana
 * @version $Id: DatabaseTransaction.java,v 1.1 2010/06/19 05:53:59 cvs Exp $
 * @since JDK 1.2.1
 * @since JSDK 2.0
*/

public class DatabaseTransaction {

	static Log logger = LogFactory.getLog(DatabaseTransaction.class);
	
    
    private String appname="";
    private static String databaseProduct=null;
    
   
    public void setConnection(Connection mconn){

      boolean isConnOK=verifyConnection(mconn);
      if(!isConnOK){
        logger.info(">>>Current Connection is not Good, Connection closed");
        JndiDataSource.releaseConnectionPool();
        try{
            mconn.close();
            mconn=getConnection();
            logger.info(">>>Connection Reestablished!");
            isConnOK=verifyConnection(mconn);
            if(!isConnOK){
            	mconn.close();
            	JndiDataSource.releaseConnectionPool();
            	mconn=getConnection();
            	logger.info(">>>Connection Reestablished twice!");
            }
        }catch (SQLException e) {
            System.err.println("Failed to get a database connection: " + e);
            e.printStackTrace();
        }
      }
    }
    public String getAppName(){
		return(this.appname);
    }
    
    public void closeConnection(Connection conn){
    	try {
    		if(!conn.isClosed()){
    			commit(conn);
    		}
    	} catch (SQLException e1) {
    		// TODO Auto-generated catch block
    		logger.info("Connection is already closed! "+e1.getMessage());
    	}

    	try {
    		
    		conn.close();
    	} catch (SQLException e) {
    		// TODO Auto-generated catch block
    		logger.info("Connection is already closed! "+e.getMessage());
    	}
    	
    	JndiDataSource.closeConnection(conn);


    }
    public void closeTransaction(Connection conn){
    
    	 	try {
				if(!conn.isClosed()){
					commit(conn);
				}
				
				JndiDataSource.closeConnection(conn);
			} catch (SQLException e1) {
				logger.info("Connection is already closed! "+e1.getMessage());
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
    	
    }

    public static String getDbType(){
    	if(databaseProduct==null ||databaseProduct.isEmpty()){
    		try {
				databaseProduct=getConnection().getMetaData().getDatabaseProductName().toLowerCase();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.info("Failed to get Database Connection using JNDI Database! "+e.getMessage());
				
				//retry
				try {
					databaseProduct=getConnection().getMetaData().getDatabaseProductName().toLowerCase();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					logger.info("Retry failed to get Database Connection using JNDI Database! "+e.getMessage());
					
				}
			}			
    		//logger.info("Database Product Type="+databaseProduct);
    	}
    	return databaseProduct;
    }
	public static Connection getConnection() throws SQLException {
		Connection conn=null;
		try {
			
			//first try to get connection using tomcat container
		    conn=JndiDataSource.getPoolConnection() ;
			
			
			if(conn!=null){
				return(conn);
			}

		}
		catch (SQLException e) {
			logger.error("Failed to get a database connection Using JNDI: " + e);
			//logger.info("Connecting Database using database connection using ConnectionPool!");
			 throw e;
		}

		return conn;
	}
	
	

	public void release(Connection conn) {

		try {
			if (conn != null && !conn.isClosed())
				conn.close();
		}
		catch (SQLException e) {
			System.err.println("Failed to release the database conn: " + e);
			e.printStackTrace();
		}
	}

	public void abort(Connection conn) {
		try {
			conn.rollback();
		}
		catch (SQLException e) {
			System.err.println("Failed to abort a transaction: " + e);
			e.printStackTrace();
		}
	}

	public void commit(Connection conn) {
		try {
			conn.commit();
		}
		catch (SQLException e) {
			System.err.println("Failed to commit a transaction: " + e);
			e.printStackTrace();
		}
	}

        private boolean  verifyConnection(Connection conn) {

		Statement stmt = null;
		String sql = "select count(*) count from sml_dbobject";
              

		try {
			stmt = conn.createStatement();
            ResultSet rs=stmt.executeQuery(sql);
            int rows=stmt.getFetchSize();
			stmt.close();
            logger.info(">>Verifying Connection returns rows="+rows);
            if(rows>0)
              return(true);

		}
		catch (SQLException e) {
			logger.info(">>Connection Test failed! Failed to connect database: " + e);
			try {
				conn.close();
                return(false);
			}
			catch (SQLException e1) {}
		}
		finally {
			try {
				if (stmt != null)
					stmt.close();
			}
			catch (SQLException e2) {}
		}
                return(false);
	}

	private void changeDateFormat(Connection conn) {

		Statement stmt = null;
		String sql = "ALTER SESSION SET NLS_DATE_FORMAT = 'YYYY-MM-DD'";

		try {
			stmt = conn.createStatement();
			int rows = stmt.executeUpdate(sql);
			conn.commit();
		}
		catch (SQLException e) {
			logger.info("Failed to change date format: " + e);
			try {
				conn.rollback();
			}
			catch (SQLException e1) {}
		}
		finally {
			try {
				if (stmt != null)
					stmt.close();
			}
			catch (SQLException e2) {}
		}
	}

	


}
