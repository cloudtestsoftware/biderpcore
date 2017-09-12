package cms.service.template;

import java.sql.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cms.service.jdbc.DatabaseUtilities;
import cms.service.jdbc.ParseQuery;



/** Simple class that tells a JTable how to extract
 *  relevant data from a TemplateTable object (which is
 *  used to store the results from a database query).
 *
 * @author S.K.jana
 * @version $Id: TemplateProcedure.java,v 1.2 2010/06/19 06:00:28 cvs Exp $
 * @since JDK 1.2.1
 * @since JSDK 2.0
 */

public class TemplateProcedure extends ParseQuery{
  static Log logger = LogFactory.getLog(TemplateProcedure.class);
  private static TemplateTable[] OutputTable =new TemplateTable[10] ;
  private DatabaseUtilities du =new DatabaseUtilities();
 
  private String queryname ="";
  
  private String appname="";

       
  	public void setProc(String proc) {
    		super.setProc(proc);
  	}

	public String getProc() {
    		return(super.getProc());
  	}
	public void setAppName(String appname){
		this.appname=appname;
  	}
  	public String getAppName(){
		return(this.appname);
  	}

	public void setQueryName(String queryname) {
    		this.queryname=queryname;
  	}
	public String getQueryName() {
    		return(queryname);
  	}

	public void setInputTable(int[] index, String[] fieldname,
					String[] datatype,String[] value) {
		super.setNoOfInput(index.length);
		super.setArrayIndex(index);
		super.setArrayFieldname(fieldname);
		super.setArrayDatatype(datatype);
		super.setArrayFieldValue(value);
		try{
		if ((index.length == datatype.length) && (index.length ==value.length))
				mapInputTable();
		else
            	throw new Exception();
		}catch(Exception ie){
			logger.error("Error:");
			logger.error(" Array size for Index and value are not same!" +ie);
			logger.error(" index length=" + index.length + " datatype length=" + datatype.length + " value Length=" + value.length );

		}
  	}
	public void setInputTable(int[] index, String[] datatype,String[] value) {
		super.setArrayIndex(index);
		super.setArrayFieldname(null);
		super.setArrayDatatype(datatype);
		super.setArrayFieldValue(value);
		try{
		if ((index.length == datatype.length) && (index.length == value.length))
				mapInputTable();
		else
			throw new Exception();

		}catch(Exception ie){
			logger.error(" Array size for Index and value are not same!" +ie);
			logger.error(" index length=" + index.length + " datatype length=" + datatype.length + " value Length=" + value.length );
		}

  	}

	public void setOutputTable(int[] index, String[] fieldname,
					String[] datatype) {
		super.setNoOfOutput(index.length);
		super.setOutArrayIndex(index);
		super.setOutArrayFieldname(fieldname);
		super.setOutArrayDatatype(datatype);

		try{
		if ((index.length == datatype.length) )
				mapOutputTable();
		else
            	throw new Exception();
		}catch(Exception ie){
			logger.error("Error:");
			logger.error(" Array size for Index and value are not same!" +ie);
			logger.error(" index length=" + index.length + " datatype length=" + datatype.length );

		}
  	}
	public void setOutputTable(int[] index,String[] datatype) {
		super.setNoOfOutput(index.length);
		super.setOutArrayIndex(index);
		super.setOutArrayFieldname(null);
		super.setOutArrayDatatype(datatype);

		try{
		if ((index.length == datatype.length) )
				mapOutputTable();
		else
            	throw new Exception();
		}catch(Exception ie){
			logger.error("Error:");
			logger.error(" Array size for Index and value are not same!" +ie);
			logger.error(" index length=" + index.length + " datatype length=" + datatype.length );

		}
  	}
	public void setProcReturnType(String type){
		super.setProcReturnType(type);
	}
	public String getProcReturnType(){
		return(super.getProcReturnType());
	}


	public TemplateTable[] getProcResultset(){
		try{
			logger.info("Calling Stored Proc :"+getProc());
			OutputTable=du.getProcResults(getProc(),getInputTable(),getOutputTable(),true);
			return(OutputTable);
		} catch(SQLException sqle) {
      		logger.error("Error creating table: " + sqle);
      		return(null);
    		}

	}
	public int getOutParamCount() {
		return(OutputTable.length);
  	}
	public String getParamValue(int index) {
		if (OutputTable[0].getRowCount() >index)
			return(OutputTable[0].getRow(index)[2]);
		return(null);
  	}
	public TemplateTable getTableRecordSet(int index) {
		if (OutputTable.length > index)
			return(OutputTable[index]);
		return(null);
  	}
	public String[] getRow(int tabindex,int rowindex) {
		if (OutputTable.length > tabindex && OutputTable[tabindex].getRowCount() >rowindex)
			return(OutputTable[tabindex].getRow(rowindex));
		return(null);
  	}

  	public String[] getRow(TemplateTable tab,int index) {
    		return((String[])tab.getRow(index));
  	}

  	public String getValueAt(TemplateTable tab,int row, int column) {
    		return(tab.getRow(row)[column]);
  	}
	public String getValueAt(int tabindex,int row, int column) {
		if (OutputTable!=null && OutputTable[tabindex].getRowCount() > row)
    			return(OutputTable[tabindex].getRow(row)[column]);
		return(null);
  	}

	public String getValueAt(String[] row, int column) {
    		return(row[column]);
  	}

 	public Object[] getColumnArray(int column) {
	   Object[] colArray = new Object[outputTable.getRowCount()];
		for (int i=0; i<outputTable.getRowCount(); i++)
			colArray[i] = outputTable.getRow(i)[column];

    	return(colArray);
  	}


}
