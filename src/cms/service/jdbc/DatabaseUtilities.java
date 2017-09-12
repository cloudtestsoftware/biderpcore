package cms.service.jdbc;

import java.sql.*;
import java.io.*;
import java.math.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



import cms.service.app.ApplicationConstants;
import cms.service.db.JndiDataSource;
import cms.service.template.TemplateTable;
import cms.service.template.TemplateUtility;
import cms.service.util.PrintTime;


/** Three database utilities:<BR>
 *   1) getQueryResults. Connects to a database, executes
 *      a query, retrieves all the rows as arrays
 *      of strings, and puts them inside a TemplateTable
 *      object. Also places the database product name,
 *      database version, and the names of all the columns
 *      into the TemplateTable object. This has two versions:
 *      one that makes a new connection and another that
 *      uses an existing connection. <P>
 *   2) createTable. Given a table name, a string denoting
 *      the column formats, and an array of strings denoting
 *      the row values, this method connects to a database,
 *      removes any existing versions of the designated
 *      table, issues a CREATE TABLE command with the
 *      designated format, then sends a series of INSERT INTO
 *      commands for each of the rows. Again, there are
 *      two versions: one that makes a new connection and
 *      another that uses an existing connection. <P>
 *   3) printTable. Given a table name, this connects to
 *      the specified database, retrieves all the rows,
 *      and prints them on the standard output.
 *
 * @author S.K.jana
 * @version $Id: DatabaseUtilities.java,v 1.1 2010/06/19 05:53:59 cvs Exp $
 * @since JDK 1.2.1
 * @since JSDK 2.0
 */

public class DatabaseUtilities {
	static Log logger = LogFactory.getLog(DatabaseTransaction.class);
	private static DatabaseTransaction dt = new DatabaseTransaction();    
	//private static Connection connection =null;
	private static int counttable;
	private String appname ="";
	private static int startRow=0;
	private static int numRows=0;
	private static int totalRows=0;
	private static String dbcharset="";
	private static String dbtype="";
	private static TemplateUtility tu= new TemplateUtility();


	/** Connect to database, execute specified query,
	 *  and accumulate results into TemplateTable object.
	 *  If the database connection is left open (use the
	 *  close argument to specify), you can retrieve the
	 *  connection with TemplateTable.getConnection.
	 */

	public void setCountTable(int count){
		counttable=count;

	}
	public int getCountTable(){
		return(counttable);
	}
	public void setAppName(String appname){
		this.appname=appname;

	}
	public String getAppName(){
		return(this.appname);
	}
	public static void setTotalRow(int row){
		totalRows=row;
	}
	public int getTotalRow(){
		return(this.totalRows);
	}
	public void setStartRow(int row){
		this.startRow=row;
	}
	public static int getStartRow(){
		return(startRow);
	}
	public void setNumRows(int row){
		this.numRows=row;
	}
	public static int getNumRows(){
		return(numRows);
	}

	public static String getDbCharset(){
		return(dbcharset);
	}
	public void setDbCharset(String dbcharset){
		this.dbcharset=dbcharset;
	}
	public static String getDbType(){
		return(dbtype);
	}
	public void setDbType(String dbtype){
		this.dbtype=dbtype;
	}
	public  TemplateTable getQueryResults(String query,
			boolean close) throws SQLException  {
		Connection connection=null;
		try {

			 connection =dt.getConnection();

			return(getQueryResults(connection, query, close));

		}catch(SQLException sqle) {
			logger.error("Query: " + query);
			logger.error("Error connecting: " + sqle);
			sqle.getStackTrace();
			//logger.error("Error connecting with stacktrace: " + sqle.getStackTrace());
			connection =dt.getConnection();
			dt.setConnection(connection);
			return(getQueryResults(connection, query, close));
		}

	}

	public TemplateTable getTableMetaData(String table) throws SQLException{
		Connection connection=null;
		DatabaseMetaData databaseMetaData;
		ResultSet rs;
		Statement stmt;
		TemplateTable  templateTable=null;
		try {
			//logger.error("#### Getting metedata for table=: " + table);
			connection =dt.getConnection();
			stmt = connection.createStatement();
			stmt.execute("select * from "+table +" where 1=0");
			rs=stmt.getResultSet();
			//databaseMetaData = connection.getMetaData();
			//rs=databaseMetaData.getColumns(null, null, table, "%");


		}catch(SQLException sqle) {

			logger.error("Error connecting: " + sqle);
			connection =dt.getConnection();
			dt.setConnection(connection);
			stmt = connection.createStatement();
			stmt.execute("select * from "+table +" where 1=0");
			rs=stmt.getResultSet();
			//databaseMetaData = connection.getMetaData();
			//rs=databaseMetaData.getColumns(null, null, table, "%");
		}

		if (rs != null) {

			// use metadata to get info about result set columns
			//logger.info(">>>Rs=NOT NULL");
			ResultSetMetaData resultsMetaData =rs.getMetaData();

			int columnCount = resultsMetaData.getColumnCount();
			String[] columnNames = new String[columnCount];
			String[] columnNullables = new String[columnCount];
			String[] columnDataTypes = new String[columnCount];
			String[] columnSize = new String[columnCount];
			

			// Column index starts at 1 (a la SQL) not 0 (a la Java).

			for(int i=1; i<columnCount+1; i++) {

				if (resultsMetaData.getColumnName(i)!=null  && !resultsMetaData.getColumnName(i).trim().equals("")){
					columnNames[i-1] = resultsMetaData.getColumnName(i).trim();
					columnNullables[i-1] = String.valueOf(resultsMetaData.isNullable(i));
					columnDataTypes[i-1] = resultsMetaData.getColumnTypeName(i);
					columnSize[i-1] = String.valueOf(resultsMetaData.getColumnDisplaySize(i));
				}else{
					columnNames[i-1] = "column" + Integer.toString(i-1);
					columnNullables[i-1] ="1";
					columnDataTypes[i-1] = "VARCHAR";
					columnSize[i-1] = String.valueOf("10");
				}

			}

			templateTable =new TemplateTable(connection, null, null, columnCount, columnNames,columnNullables,columnDataTypes,columnSize);

		}else{

			logger.error("#### Failed to retrieve metadata. RecordSet=null");

		}


		stmt.close();

		if (dt==null){
			connection.close();
		}else{
			dt.closeTransaction(connection);
		}

		return templateTable;

	}
	/** Connect to database, execute specified query,
	 *  and accumulate results into TemplateTable object.
	 *  If the database connection is left open (use the
	 *  close argument to specify), you can retrieve the
	 *  connection with TemplateTable.getConnection.
	 */

	public TemplateTable[] getProcResults(String proc,TemplateTable in,TemplateTable out,
			boolean close) throws SQLException  {
		Connection connection=null;
		try {

			connection = dt.getConnection();

			return(getProcResults(connection, proc,in,out, close));

		}catch(SQLException sqle) {
			logger.error("Procedure: " + proc);
			logger.error("Error connecting: " + sqle);
			dt.setConnection(connection);
			connection = dt.getConnection();
			return(getProcResults(connection, proc,in,out, close));

		}

	}


	/** Retrieves results as in previous method but uses
	 *  an existing connection instead of opening a new one.
	 *  If the user sets the start row no  and number of rows per
	 *  query this method will return only this number of records if available
	 *  otherwise this will return all rows by default as a query resultset
	 *  when getStartRow()=0 and getNumRows()=0
	 */

	private TemplateTable getQueryResults(Connection connection,
			String query,
			boolean close) {

		TemplateTable templateTable = new TemplateTable();
		Statement stmt = null;
		ResultSet rs=null;
		int startCount=0;
		int endCount=0;

		try {
			DatabaseMetaData dbMetaData = connection.getMetaData();
			String productName =dbMetaData.getDatabaseProductName();
			String productVersion = dbMetaData.getDatabaseProductVersion();
			stmt = connection.createStatement();
			String sql="";
			try {
				sql=(getDbCharset()!=null &&!getDbCharset().trim().equalsIgnoreCase("english")? tu.convertCharsetClient2db(query,getDbCharset()):query);
			} catch(IOException e) {
				sql=query;
			}

			stmt.execute(sql);

			int rowCount = stmt.getUpdateCount();
			if(getDbType().equalsIgnoreCase("Oracle")&&sql!=null
					&&(sql.indexOf("insert")>=0)||sql.indexOf("update")>0||sql.indexOf("delete")>0 &&rowCount==-1)
				rowCount=0;

			setTotalRow(rowCount);
			if (rowCount >= 0) {    //  this is an update count
				String[] row = new String[1];
				row[0]= Integer.toString(rowCount);
				templateTable.addRow(row);
				//logger.info("Rows changed = " + rowCount);

			}else{

				// if we have gotten this far, we have either a result set
				// or no more results
				rs = stmt.getResultSet();

			}

			if (rs != null) {

				// use metadata to get info about result set columns
				//logger.info(">>>Rs=NOT NULL");
				ResultSetMetaData resultsMetaData =rs.getMetaData();
				int columnCount = resultsMetaData.getColumnCount();
				String[] columnNames = new String[columnCount];
				String[] columnNullables = new String[columnCount];
				String[] columnDataTypes = new String[columnCount];
				String[] columnSize = new String[columnCount];
				if(ApplicationConstants.GENERATE_LOG){
					logger.info("Query="+sql);
				}

				// Column index starts at 1 (a la SQL) not 0 (a la Java).

				for(int i=1; i<columnCount+1; i++) {

					if (resultsMetaData.getColumnName(i)!=null  && !resultsMetaData.getColumnName(i).trim().equals("")){
						columnNames[i-1] = resultsMetaData.getColumnName(i).trim();
						columnNullables[i-1] = String.valueOf(resultsMetaData.isNullable(i));
						columnDataTypes[i-1] = resultsMetaData.getColumnTypeName(i);
						columnSize[i-1] = String.valueOf(resultsMetaData.getColumnDisplaySize(i));
					}else{
						columnNames[i-1] = "column" + Integer.toString(i-1);
						columnNullables[i-1] ="1";
						columnDataTypes[i-1] = "VARCHAR";
						columnSize[i-1] = String.valueOf("10");
					}

				}

				templateTable =new TemplateTable(connection, productName, productVersion, columnCount, columnNames,columnNullables,columnDataTypes,columnSize);


				while (rs.next())  {
					startCount++;
					if(startCount>=getStartRow()){
						endCount++;
						// process results
						String[] row = new String[columnCount];
						// Again, ResultSet index starts at 1, not 0.
						for(int i=1; i<columnCount+1; i++) {
							String entry="";
							try {
								//logger.info(resultsMetaData.getColumnName(i).trim() +"="+resultsMetaData.getColumnTypeName(i).toLowerCase());
								if(resultsMetaData.getColumnTypeName(i).toLowerCase().contains("date") &&rs.getDate(i)!=null){
									
									entry=PrintTime.getDateByDeafultFormat(rs.getDate(i));
									//logger.info(resultsMetaData.getColumnTypeName(i).toLowerCase()+ entry);
								}else{
									entry=(getDbCharset()!=null &&!getDbCharset().trim().equalsIgnoreCase("english")? tu.convertCharsetDb2client(rs.getString(i),getDbCharset()):rs.getString(i));
							    }
							} catch(IOException e) {
								entry=rs.getString(i);
							}
							if (entry != null) {
								entry = entry.trim().replace("\\", "");
							}
							row[i-1] = tu.decodeSpecialChar(entry);
						}
						templateTable.addRow(row);
						if(getNumRows()!=0 && endCount==getNumRows())
							break;
					}

				}

			}else if(ApplicationConstants.GENERATE_LOG){
				logger.info("#### Resultset=null for \n Query="+sql);
			}
				
		

			/*if (close) {

				stmt.close();

				if (dt==null){
					connection.close();
				}else{
					dt.closeTransaction(connection);
				}

			}
			*/
			
			return(templateTable);
        
		} catch(SQLException sqle) {
			logger.error("Query: " + query);
			logger.error("Error connecting: " + sqle);
			//dt.setConnection(connection);
			return(templateTable);
		}
		finally{
			
			if (close &&stmt!=null) {

				try {
					stmt.close();
					
					if (dt==null){
						connection.close();
					}else{
						dt.closeTransaction(connection);
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}


	/** Retrieves results as in previous method but uses
	 *  an existing connection instead of opening a new one.
	 */

	private TemplateTable[] getProcResults(Connection connection,
			String proc,
			TemplateTable in,
			TemplateTable out,
			boolean close) {
		try {
			//	logger.info("Get Proc Result" + proc );

			DatabaseMetaData dbMetaData = connection.getMetaData();
			String productName =dbMetaData.getDatabaseProductName();
			String productVersion =dbMetaData.getDatabaseProductVersion();

			CallableStatement cs = connection.prepareCall(proc);
			cs = PrepareProc(cs,in,out);

			TemplateTable[] table = new TemplateTable[counttable];
			//	logger.info("counttable" +Integer.toString(counttable));

			boolean hasResultSet1= cs.execute();
			//	logger.info("Get Proc Result" + proc +dt.getVendor() );

			int count=0;

			//if(dt.getVendor().equals("ORACLE")){

			TemplateTable temp =new TemplateTable();
			for (int i=0; i<out.getRowCount(); i++){
				String type=  out.getRow(i)[2];
				String index= out.getRow(i)[0];
				String paramname= out.getRow(i)[1];
				String[] row = new String[3];
				row[0]=index;
				row[1]=paramname;
				if (!type.equals(DataType.OTHER) ){
					Object val = cs.getObject(Integer.parseInt(index));
					//	logger.info("Object Value:" +val);
					row[2]=String.valueOf(val);
					count=1;
				}
				temp.addRow(row);
				//	logger.info("DatabaseUtility Temp table:" +temp.getRow(0)[2]);

				//	paramindex++;
				//}
				if (temp.getRowCount()>0)
					table[0]= temp;
			}

			while (hasResultSet1) {
				ResultSet rs = cs.getResultSet();
				ResultSetMetaData resultsMetaData =rs.getMetaData();
				int columnCount = resultsMetaData.getColumnCount();
				TemplateTable temp1 =new TemplateTable();
				//	logger.info("DatabaseUtility Temp table:" +Integer.toString(columnCount));

				while (rs.next())  {
					// process results
					String[] row = new String[columnCount];
					// Again, ResultSet index starts at 1, not 0.
					for(int i=1; i<columnCount+1; i++) {
						String entry="";
						try {
							entry=(getDbCharset()!=null &&!getDbCharset().trim().equalsIgnoreCase("english")? tu.convertCharsetDb2client(rs.getString(i),getDbCharset()):rs.getString(i));
						} catch(IOException e) {
							entry=rs.getString(i);
						}

						if (entry != null) {
							entry = entry.trim();
						}
						row[i-1] = tu.decodeSpecialChar(entry);
					}
					temp1.addRow(row);

				}

				rs.close();
				table[count]=temp1;
				count++;
				hasResultSet1 = cs.getMoreResults();
			}

			if (close) {
				if (dt==null)
					connection.close();
				else
					dt.closeTransaction(connection);
			}
			return(table);
		} catch(SQLException sqle) {
			logger.error("Proc: " + proc);
			logger.error("Error connecting: " + sqle);
			dt.setConnection(connection);
			return(null);
		}
	}



	/** Build a table with the specified format and rows. */

	public  Connection createTable( String tableName,
			String tableFormat,
			String[] tableRows,
			boolean close)  throws SQLException {
		Connection connection=null;
		try {
			if ( connection==null ||connection.isClosed())
				connection =  dt.getConnection();

			return(createTable(connection,tableName, tableFormat,
					tableRows, close));
			/*} catch(ClassNotFoundException cnfe) {
      logger.error("Error loading driver: " + cnfe);
      return(null); */
		} catch(SQLException sqle) {
			logger.error("Error connecting: " + sqle);
			dt.setConnection(connection);
			return(null);
		}
	}

	/** Like the previous method, but uses existing connection. */

	public Connection createTable(Connection connection,
			String tableName,
			String tableFormat,
			String[] tableRows,
			boolean close) throws SQLException {
		try {

			Statement statement = connection.createStatement();
			// Drop previous table if it exists, but don't get
			// error if it doesn't. Thus the separate try/catch here.
			try {
				statement.execute("DROP TABLE " + tableName);
			} catch(SQLException sqle) {}
			String createCommand =
					"CREATE TABLE " + tableName + " " + tableFormat;
			statement.execute(createCommand);
			String insertPrefix =
					"INSERT INTO " + tableName + " VALUES";
			for(int i=0; i<tableRows.length; i++) {
				statement.execute(insertPrefix + tableRows[i]);
			}
			if (close) {
				if (dt==null)
					connection.close();
				else
					dt.closeTransaction(connection);
				return(null);
			} else {
				return(connection);
			}
		} catch(SQLException sqle) {
			logger.error("Error creating table: " + sqle);
			dt.setConnection(connection);
			return(null);
		}
	}

	public void printTable( String tableName,
			int entryWidth,
			boolean close) {
		Connection connection=null;
		try{
			String query = "SELECT * FROM " + tableName;
			TemplateTable results =
					getQueryResults(query, close);
			printTableData(tableName, results, entryWidth, true);
		} catch(SQLException sqle) {
			logger.error("Error creating table: " + sqle);
			dt.setConnection(connection);

		}

	}

	/** Prints out all entries in a table. Each entry will
	 *  be printed in a column that is entryWidth characters
	 *  wide, so be sure to provide a value at least as big
	 *  as the widest result.
	 */

	public void printTable(Connection connection,
			String tableName,
			int entryWidth,
			boolean close) {
		String query = "SELECT * FROM " + tableName;
		TemplateTable results =
				getQueryResults(connection, query, close);
		printTableData(tableName, results, entryWidth, true);
	}

	public  void printTableData(String tableName,
			TemplateTable results,
			int entryWidth,
			boolean printMetaData) {
		if (results == null) {
			return;
		}
		if (printMetaData) {
			logger.info("Database: " + results.getProductName());
			logger.info("Version: " + results.getProductVersion());
			logger.info("  ");
		}
		logger.info(tableName + ":");
		String underline =
				padString("", tableName.length()+1, "=");
		logger.info(underline);
		int columnCount = results.getColumnCount();
		String separator =
				makeSeparator(entryWidth, columnCount);
		logger.info(separator);
		String row = makeRow(results.getColumnNames(), entryWidth);
		logger.info(row);
		logger.info(separator);
		int rowCount = results.getRowCount();
		for(int i=0; i<rowCount; i++) {
			row = makeRow(results.getRow(i), entryWidth);
			logger.info(row);
		}
		logger.info(separator);
	}

	// A String of the form "|  xxx |  xxx |  xxx |"

	private static String makeRow(String[] entries,
			int entryWidth) {
		String row = "|";
		for(int i=0; i<entries.length; i++) {
			row = row + padString(entries[i], entryWidth, " ");
			row = row + " |";
		}
		return(row);
	}

	// A String of the form "+------+------+------+"

	private static String makeSeparator(int entryWidth,
			int columnCount) {
		String entry = padString("", entryWidth+1, "-");
		String separator = "+";
		for(int i=0; i<columnCount; i++) {
			separator = separator + entry + "+";
		}
		return(separator);
	}

	private static String padString(String orig, int size,
			String padChar) {
		if (orig == null) {
			orig = "<null>";
		}
		// Use StringBuffer, not just repeated String concatenation
		// to avoid creating too many temporary Strings.
		StringBuffer buffer = new StringBuffer("");
		int extraChars = size - orig.length();
		for(int i=0; i<extraChars; i++) {
			buffer.append(padChar);
		}
		buffer.append(orig);
		return(buffer.toString());
	}
	private static CallableStatement PrepareProc(CallableStatement cs,
			TemplateTable in,
			TemplateTable out){
		counttable=1;

		if (in!=null && in.getRowCount()>0){
			for (int j=0; j<in.getRowCount(); j++){
				try {
					//	logger.info("Index=" +in.getRow(j)[0]+ "loop="+ Integer.toString(j) + "value=" +in.getRow(j)[3]);
					int index = Integer.parseInt(in.getRow(j)[0]);
					String oval = in.getRow(j)[3];
					if (in.getRow(j)[2].equals(DataType.BIT))
						cs.setBoolean(index,Boolean.valueOf(oval).booleanValue());
					else if(in.getRow(j)[2].equals(DataType.TINYINT))
						cs.setByte(index,Byte.valueOf(oval).byteValue());
					else if (in.getRow(j)[2].equals(DataType.SMALLINT))
						cs.setShort(index,Short.valueOf(oval).shortValue());
					else if (in.getRow(j)[2].equals(DataType.INTEGER))
						cs.setInt(index,Integer.valueOf(oval).intValue());
					else if (in.getRow(j)[2].equals(DataType.BIGINT))
						cs.setLong(index,Long.valueOf(oval).longValue());
					else if (in.getRow(j)[2].equals(DataType.FLOAT))
						cs.setFloat(index,Float.valueOf(oval).floatValue());
					else if (in.getRow(j)[2].equals(DataType.DOUBLE))
						cs.setDouble(index,Double.valueOf(oval).doubleValue());
					else if (in.getRow(j)[2].equals(DataType.DECIMAL) ||
							in.getRow(j)[2].equals(DataType.NUMERIC) ){
						BigDecimal bd =new BigDecimal(oval);
						cs.setBigDecimal(index,bd);
					}else if (in.getRow(j)[2].equals(DataType.CHAR) ||
							in.getRow(j)[2].equals(DataType.VARCHAR)||
							in.getRow(j)[2].equals(DataType.LONGVARCHAR)){

						try {
							oval=(getDbCharset()!=null &&!getDbCharset().trim().equalsIgnoreCase("english")? tu.convertCharsetClient2db(oval,getDbCharset()):oval);
						} catch(IOException e) {
							oval=oval;
						}
						cs.setString(index,oval);
					}else if (in.getRow(j)[2].equals(DataType.VARBINARY) ||
							in.getRow(j)[2].equals(DataType.LONGVARBINARY))
						cs.setBytes(index,oval.getBytes());
					else if (in.getRow(j)[2].equals(DataType.DATE)){
						Date d = new Date(Long.valueOf(oval).longValue());
						cs.setDate(index,d);
					}else if (in.getRow(j)[2].equals(DataType.TIME))
						cs.setTime(index,Time.valueOf(oval));
					else if (in.getRow(j)[2].equals(DataType.TIMESTAMP))
						cs.setTimestamp(index,Timestamp.valueOf(oval));
					else if (in.getRow(j)[2].equals(DataType.NULL)){
						cs.setNull(index,Integer.valueOf(oval).intValue());
					}else
						cs.setObject(index,oval);

				}catch(NumberFormatException ne){
					ne.printStackTrace();
				}catch(SQLException se){
					se.printStackTrace();

				}

			}
		}
		if (out!=null && out.getRowCount()>0){

			for (int i=0; i<out.getRowCount(); i++){
				String type= out.getRow(i)[2];
				if (type.equals(DataType.OTHER))
					counttable++;
				try {
					int idx = Integer.parseInt(out.getRow(i)[0]) ;
					//	logger.info("Index=" + Integer.toString(idx));
					cs.registerOutParameter(idx, Integer.parseInt(type));
				}catch(NumberFormatException ne){
					ne.printStackTrace();

				}catch(SQLException se){
					se.printStackTrace();

				}


			}

		}
		return(cs);
	}
	
	

}
