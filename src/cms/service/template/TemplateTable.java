package cms.service.template;

import java.sql.*;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** Class to store completed results of a JDBC Query.
 *  Differs from a ResultSet in several ways:
 *  <UL>
 *    <LI>ResultSet doesn't necessarily have all the data;
 *        reconnection to database occurs as you ask for
 *        later rows.
 *    <LI>This class stores results as strings, in arrays.
 *    <LI>This class includes DatabaseMetaData (database product
 *        name and version) and ResultSetMetaData
 *        (the column names).
 *    <LI>This class has a toHTMLTable method that turns
 *        the results into a long string corresponding to
 *        an HTML table.
 *  </UL>
 *
 * @author S.K.jana
 * @version $Id: TemplateTable.java,v 1.1 2010/06/19 05:53:59 cvs Exp $
 * @since JDK 1.2.1
 * @since JSDK 2.0
 */

public class TemplateTable {
	static Log logger = LogFactory.getLog(TemplateTable.class);
	private String tablename;
	private Connection connection;
	private String productName;
	private String productVersion;
	private int columnCount;
	private String[] columnNames;
	private String[] columnNullables;
	private String[] columnDataTypes;
	private String[] columnSize;
	private Vector queryResults = new Vector();

	String[] rowData;

	public TemplateTable(){
		this.queryResults = new Vector();
		this.rowData=null;
	};

	public TemplateTable(Connection connection,
			String productName,
			String productVersion,
			int columnCount,
			String[] columnNames,
			String[] columnNullables,
			String[] columnDataTypes,
			String[] columnSize) {
		this.connection = connection;
		this.productName = productName;
		this.productVersion = productVersion;
		this.columnCount = columnCount;
		this.columnNames = columnNames;
		this.columnNullables=columnNullables;
		this.columnDataTypes=columnDataTypes;
		this.columnSize=columnSize;
		this.queryResults =new Vector();
		rowData = new String[columnCount];

	}


	public String[] getColumnSizes(){
		return(this.columnSize);
	}

	public String[] getColumnNullables(){
		return(this.columnNullables);
	}

	public String[] getColumnDataTypes(){
		return(this.columnDataTypes);
	}
	public void setTableName(String name){
		this.tablename=name;
	}
	public String getTableName(){
		return(this.tablename);
	}
	public Connection getConnection() {
		return(connection);
	}

	public String getProductName() {
		return(productName);
	}

	public String getProductVersion() {
		return(productVersion);
	}

	public int getColumnCount() {
		if (columnCount==0 )
			columnCount= getRow(0).length;
		return(columnCount);
	}

	public String[] getColumnNames() {
		return(columnNames);
	}
	public Object[] getColumnValueArray(int column) {
		Object[] colArray = new Object[getRowCount()];
		for (int i=0; i<getRowCount(); i++)
			colArray[i] = this.getRow(i)[column];

		return(colArray);
	}
	public int getColumnIndex(String ColumnName){
		for(int i=0; i<getColumnNames().length; i++)
			if(getColumnNames()[i].equalsIgnoreCase(ColumnName))
				return(i);
		return(-1);
	}
	public String getFieldValue(int FieldIdx, int row){

		String value=getRow(row)[FieldIdx];
		return((value==null||value.equalsIgnoreCase("null"))==true?"":removeSpecialChar(value));
	}

	public int getRowIndexByValue(String FieldName, String value){
		for (int row=0;row<this.getRowCount();row++){
			String val=(getColumnIndex(FieldName)==-1? null:getRow(row)[getColumnIndex(FieldName)]);
			if(val!=null &&val.equals(value)){
				return row;
			}
		}
		return(-1);
	}
	public String getFieldValue(String FieldName, int row){

		String value=(getColumnIndex(FieldName)==-1? null:getRow(row)[getColumnIndex(FieldName)]);
		
		return((value==null||value.equalsIgnoreCase("null"))==true?"":removeSpecialChar(value));
	}
	
	private String removeSpecialChar(String val){
		String ret=val;
		
		if(val.contains("!!@")){
			
			if(ret.contains("!!@amp;")){
				ret=ret.replaceAll("!!@amp;", "&");
			}
			if(ret.contains("!!@quot;")){
				ret=ret.replaceAll("!!@quot;", "\"");
			}
			if(ret.contains("!!@apos;")){
				ret=ret.replaceAll("!!@apos;", "\'");
			}
			if(ret.contains("!!@lt;")){
				ret=ret.replaceAll("!!@lt;", "<");
			}
			if(ret.contains("!!@gt;")){
				ret=ret.replaceAll("!!@gt;", ">");
			}
		}
		return ret;
	}
	private void setColumnNames(String[] ColumnNames ) {
		columnNames=ColumnNames;
	}

	public void addColumn(String ColumnName ) {
		if(!(getRowCount()>0)){
			String[] col = new String[1];
			col[0]=ColumnName;
			addRow(col);
			setColumnNames(col);
		}else{
			String[] row =new String[getColumnCount() +1];
			row=getRow(0);
			row[getColumnCount() +1]= ColumnName;
			//	queryResults.elementAt(0)=row;
			queryResults= new Vector();
			addRow(row);
			setColumnNames(row);
		}
	}

	public void addColumns(String[] ColumnNames ) {
		if(!(getRowCount()>0)){
			addRow(ColumnNames);
		}else{
			String[] row =new String[getColumnCount() +ColumnNames.length];
			row=getRow(0);
			for(int i=0; i< ColumnNames.length; i++)
				row[getColumnCount() +i]= ColumnNames[i];
			queryResults = new Vector();
			addRow(row);
		}
		setColumnNames(ColumnNames);
	}

	public void deleteColumn(int Index ) {
		int i=0;
		Vector myqueryResults = new Vector();

		while (i< getRowCount()){
			String[] row =new String[getColumnCount() ];
			row=getRow(i);
			row[Index]= "";
			myqueryResults.addElement(row);
			i++;
		}
		queryResults= myqueryResults;
	}

	public void modifyRow(String[] Rowdata, int Index ) {

		int i=0;
		Vector myqueryResults = new Vector();

		while (i< getRowCount()){
			String[] row =new String[getColumnCount() ];
			row=getRow(i);
			if (i== Index)
				row= Rowdata;
			myqueryResults.addElement(row);
			i++;
		}
		queryResults= myqueryResults;
	}

	public void deleteRow( int Index ) {

		int i=0;
		Vector myqueryResults = new Vector();

		while (i< getRowCount()){
			String[] row =new String[getColumnCount() ];
			row=getRow(i);
			if (i== Index)
				row= null;
			myqueryResults.addElement(row);
			i++;
		}
		queryResults= myqueryResults;
	}

	public int getRowCount() {
		return(queryResults.size());
	}

	public String[] getRow(int index) {
		return((String[]) (queryResults!=null && queryResults.elementAt(index)!=null ?queryResults.elementAt(index):null));
	}

	public void addRow(String[] row) {
		queryResults.addElement(row);
	}

	/** Output the results as an HTML table, with
	 *  the column names as headings and the rest of
	 *  the results filling regular data cells.
	 */


	public String getFormData(String form, String[] columns) {
		StringBuffer buffer =
				new StringBuffer("\n <Input hidden name="+form+"data>\n\t"+
						"<SCRIPT language=JavaScript> \n \t " +
						"function get"+form+"GridData(){ \n\t\t"+
						"var x=String.fromCharCode(1); \n\t\t" +
						"var y=String.fromCharCode(2); \n\t\t" );

		String elem="var "+form+"_elem= new Array(";
		String elem_type="var "+form+"_elem_types= new Array(";
		String elem_required="var "+form+"_elem_required= new Array(";
		String table_labels="var "+form+"_table_labels= new Array(";
		String fieldvalue="";
		boolean verifycol=false;
		//logger.info("Before Verifying col column jsp length="+columnNames.length+" Column query length="+columns.length);
		if(columnNames.length>=columns.length){
			//logger.info("Verifying col");
			verifycol=true;
			for(int v=0; v<columns.length;v++){
				//logger.info("column jsp="+columns[v]+" Column query="+columnNames[v]);
				if( getColumnIndex(columns[v])==-1){
					verifycol=false;
					break;
				}
			}

		}
		for(int col=0; col<getColumnCount(); col++) {
			if(col==0){
				elem+= "'"+columns[col]+"'";
				elem_type+= "'text'";
				elem_required+= true;
				table_labels+= "'"+columns[col]+"'";


			}else{
				if(col==1){
					elem+= ",'"+form+columns[col]+"'";
					table_labels+= ",'"+form+columns[col]+"'";
				}else{
					elem+= ",'"+columns[col]+"'";
					table_labels+= ",'"+columns[col]+"'";

				}

				elem_type+= ",'text'";
				elem_required+= ","+true;
			}

		}
		int count=0;
		for(int row=0; row<getRowCount()&&verifycol; row++) {
			count++;
			String[] rowData = getRow(row);
			for(int col=0; col<getColumnCount(); col++) {
				if (col==0)
					fieldvalue+="\""+((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+"\"";
				else
					fieldvalue+= "+x+\""+ ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+"\"";
			}
			if (row<getRowCount()-1)
				fieldvalue+= "+y+";
		}
		count++;
		if(verifycol){
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['next"+form+ "idx'].value="+count+";");
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['lineindex'].value=0;");
			buffer.append("\n\t\t document.forms['"+form+"'].elements['"+form+ "data'].value="+ fieldvalue);
		}
		buffer.append("\n\t\t "+elem+");\n\t\t"+elem_type+");\n\t\t"+elem_required+");\n\t\t"+table_labels+");\n\t\t");
		buffer.append("\n\t\t"+form+"_grid= new Grid('"+form+"',"+form+"_elem,"+form+"_elem_types,"+form+"_elem_required," +
				"document.forms['"+form+"_form'],document.forms['"+form+"_form'],document.all."+form+"_splits,"+form+"_table_labels,true); \n");
		buffer.append("\n\t\t\t" + form+"_grid.barchartelement="+form+"_barchartelement;");
		buffer.append("\n\t\t\t" + form+"_grid.buildtable(); \n \t}");

		buffer.append("\n</SCRIPT>");
		return(buffer.toString());
	}

	public String getFormData(String form, String[] columns,String[]datatype) {
		StringBuffer buffer =
				new StringBuffer("\n <Input hidden name="+form+"data>\n\t"+
						"<SCRIPT language=JavaScript> \n \t " +
						"function get"+form+"GridData(){ \n\t\t"+
						"var x=String.fromCharCode(1); \n\t\t" +
						"var y=String.fromCharCode(2); \n\t\t" );

		String elem="var "+form+"_elem= new Array(";
		String elem_type="var "+form+"_elem_types= new Array(";
		String tmp_elem_type="var "+form+"_elem_types= new Array(";
		String elem_required="var "+form+"_elem_required= new Array(";
		String table_labels="var "+form+"_table_labels= new Array(";
		String fieldvalue="";
		boolean verifycol=false;
		//logger.info("Before Verifying col column jsp length="+columnNames.length+" Column query length="+columns.length);
		if(getColumnNames().length>=columns.length){
			//logger.info("Verifying col");
			verifycol=true;
			for(int v=0; v<columns.length;v++){
				//logger.info("column jsp="+columns[v]+" Column query="+columnNames[v]);
				if( getColumnIndex(columns[v])==-1){
					verifycol=false;
					break;
				}
			}

		}

		for(int col=0; col<getColumnCount(); col++) {
			if(col==0){
				elem+= "'"+columns[col]+"'";
				elem_type+= "'text'";
				elem_required+= true;
				table_labels+= "'"+columns[col]+"'";


			}else{
				if(col==1){
					elem+= ",'"+form+columns[col]+"'";
					table_labels+= ",'"+form+columns[col]+"'";
				}else{
					elem+= ",'"+columns[col]+"'";
					table_labels+= ",'"+columns[col]+"'";

				}

				elem_type+= ",'text'";
				elem_required+= ","+true;

			}

		}
		if(datatype.length==getColumnCount()){
			for(int k=0;k<getColumnCount();k++){
				if(k==0)
					tmp_elem_type+= "'text'";
				else
					tmp_elem_type+= ",'text'";

			}
			elem_type=tmp_elem_type;
		}
		int count=0;
		for(int row=0; row<getRowCount()&&verifycol; row++) {
			count++;
			String[] rowData = getRow(row);
			for(int col=0; col<getColumnCount(); col++) {
				if (col==0)
					fieldvalue+="\""+((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+"\"";
				else
					fieldvalue+= "+x+\""+ ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+"\"";
			}
			if (row<getRowCount()-1)
				fieldvalue+= "+y+";

		}
		count++;
		if(verifycol){
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['next"+form+ "idx'].value="+ count+";");
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['lineindex'].value=0;");
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['"+form+ "data'].value="+ fieldvalue);
		}
		buffer.append("\n\t\t "+elem+");\n\t\t"+elem_type+");\n\t\t"+elem_required+");\n\t\t"+table_labels+");\n\t\t");
		buffer.append("\n\t\t"+form+"_grid= new Grid('"+form+"',"+form+"_elem,"+form+"_elem_types,"+form+"_elem_required," +
				"document.forms['"+form+"_form'],document.forms['"+form+"_form'],document.all."+form+"_splits,"+form+"_table_labels,true);");
		buffer.append("\n\t\t\t" + form+"_grid.barchartelement="+form+"_barchartelement;");
		buffer.append("\n\t\t"+ form+"_grid.buildtable(); \n \t}");

		buffer.append("\n</SCRIPT>");
		return(buffer.toString());
	}
	public String getFormData(String form, String []columns,int[] visibleCol,String[]datatype,String reason) {
		StringBuffer buffer =new StringBuffer();
		if(!reason.equalsIgnoreCase("sort"))
			buffer.append("<SCRIPT language=JavaScript> \n \t " +
					"function get"+form+"GridData(){ \n\t\t" +
					"var x=String.fromCharCode(1); \n\t\t" +
					"var y=String.fromCharCode(2); \n\t\t");

		String elem="var "+form+"_elem= new Array(";
		String elem_type="var "+form+"_elem_types= new Array(";
		String tmp_elem_type="var "+form+"_elem_types= new Array(";
		String elem_required="var "+form+"_elem_required= new Array(";
		String table_labels="var "+form+"_table_labels= new Array(";
		String fieldvalue="";
		boolean verifycol=false;
		//logger.info("Before Verifying col column jsp length="+columnNames.length+" Column query length="+columns.length);
		if(getColumnNames().length>=columns.length){
			//logger.info("Verifying col");
			verifycol=true;
			for(int v=0; v<columns.length;v++){
				//logger.info("column jsp="+columns[v]+" Column query="+columnNames[v]);
				if( getColumnIndex(columns[v])==-1){
					verifycol=false;
					break;
				}
			}

		}

		if( visibleCol.length >0){

			for(int col=0; col<visibleCol.length ; col++) {
				int colIndex=visibleCol[col];

				if(col==0){
					if(columns[colIndex].equalsIgnoreCase("Name")){
						elem+= "'"+columns[colIndex]+"'";
						table_labels+= "'"+columns[colIndex]+"'";
					}else{
						elem+= "'"+columns[colIndex]+"'";
						table_labels+= "'"+columns[colIndex]+"'";

					}

					elem_type+= "'text'";
					elem_required+=true;
				}else{
					if(columns[colIndex].equalsIgnoreCase("Name")){
						elem+= ",'"+columns[colIndex]+"'";
						table_labels+= ",'"+columns[colIndex]+"'";
					}else{
						elem+= ",'"+columns[colIndex]+"'";
						table_labels+= ",'"+columns[colIndex]+"'";

					}

					elem_type+= ",'text'";
					elem_required+= ","+true;
				}

			}
			if(datatype.length==visibleCol.length){
				for(int k=0; k<visibleCol.length; k++){
					if(k==0)
						tmp_elem_type+= "'"+datatype[k]+"'";
					else
						tmp_elem_type+= ",'"+datatype[k]+"'";
				}
				elem_type=tmp_elem_type;
			}


		}else{
			for(int col=0; col<getColumnCount(); col++) {
				if(col==0){
					elem+= "'"+columns[col]+"'";
					elem_type+= "'text'";
					elem_required+= true;
					table_labels+= "'"+columns[col]+"'";


				}else{
					if(columns[col].equalsIgnoreCase("Name")){
						elem+= ",'"+columns[col]+"'";
						table_labels+= ",'"+columns[col]+"'";
					}else{
						elem+= ",'"+columns[col]+"'";
						table_labels+= ",'"+columns[col]+"'";

					}

					elem_type+= ",'text'";
					elem_required+= ","+true;

				}

			}
			if(datatype.length==getColumnCount()){
				for(int k=0;k<getColumnCount();k++){
					if(k==0)
						tmp_elem_type+= "'text'";
					else
						tmp_elem_type+= ",'text'";
				}
				elem_type=tmp_elem_type;
			}


		}
		int count=0;
		if( !reason.equalsIgnoreCase("sort")){
			for(int row=0; row<getRowCount()&& verifycol; row++) {
				count++;
				String[] rowData = getRow(row);
				if( visibleCol.length >0){
					for(int col=0; col<visibleCol.length ; col++) {
						int colIndex=visibleCol[col];
						if (col==0)
							fieldvalue+="\""+((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex])+"\"";
						else
							fieldvalue+= "+x+\""+ ((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex])+"\"";
					}
					if (row<getRowCount()-1)
						fieldvalue+= "+y+";
				}else{
					for(int col=0; col<getColumnCount(); col++) {
						if (col==0)
							fieldvalue+="\""+((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+"\"";
						else

							fieldvalue+= "+x+\""+ ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+"\"";
					}
					if (row<getRowCount()-1)
						fieldvalue+= "+y+";

				}
			}
			count++;
		}
		if(verifycol && !reason.equalsIgnoreCase("sort")){
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['next"+form+ "idx'].value="+count+";");
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['lineindex'].value=0;");
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['"+form+ "data'].value="+ fieldvalue+";");
		}
		buffer.append("\n\t\t "+elem+");\n\t\t"+elem_type+");\n\t\t"+elem_required+");\n\t\t"+table_labels+");\n\t\t");
		buffer.append("\n\t\t"+form+"_grid= new Grid('"+form+"',"+form+"_elem,"+form+"_elem_types,"+form+"_elem_required," +
				"document.forms['"+form+"_form'],document.forms['"+form+"_form'],document.all."+form+"_splits,"+form+"_table_labels,true);");
		buffer.append("\n\t\t\t" + form+"_grid.barchartelement="+form+"_barchartelement;");
		if(reason.equalsIgnoreCase("sort"))
			buffer.append("\n\t\t"+form+"_grid.sorttable(label); \n \t");
		else{
			buffer.append("\n\t\t"+form+"_grid.buildtable(); \n \t}");
			buffer.append("\n</SCRIPT>");
		}
		return(buffer.toString());
	}

	public String getFormData(String form, String []columns,String []caption,int[] visibleCol,String[]datatype,String reason) {
		StringBuffer buffer =new StringBuffer();

		if(!reason.equalsIgnoreCase("sort"))
			buffer.append("<SCRIPT language=JavaScript> \n \t " +
					"function get"+form+"GridData(){ \n\t\t" +
					"var x=String.fromCharCode(1); \n\t\t" +
					"var y=String.fromCharCode(2); \n\t\t");

		String elem="var "+form+"_elem= new Array(";
		String elem_type="var "+form+"_elem_types= new Array(";
		String tmp_elem_type="var "+form+"_elem_types= new Array(";
		String elem_required="var "+form+"_elem_required= new Array(";
		String table_labels="var "+form+"_table_labels= new Array(";
		String fieldvalue="";
		boolean verifycol=false;
		//logger.info("Before Verifying col column jsp length="+columnNames.length+" Column query length="+columns.length);
		if(getRowCount()>0&&getColumnNames().length>=columns.length){
			//logger.info("Verifying col");
			verifycol=true;
			for(int v=0; v<columns.length;v++){
				//logger.info("column jsp="+columns[v]+" Column query="+columnNames[v]);
				if(getColumnIndex(columns[v])==-1){
					verifycol=false;
					break;
				}
			}

		}

		if( visibleCol.length >0 && caption.length>0 && columns.length>0
				&&  caption.length>=visibleCol.length && columns.length>=visibleCol.length &&
				datatype.length>0 &&datatype.length>=visibleCol.length){

			for(int col=0; col<visibleCol.length ; col++) {
				int colIndex=visibleCol[col];

				if(col==0){
					if(columns[colIndex].equalsIgnoreCase("ObjId")){
						elem+= "'"+columns[colIndex]+"'";
						table_labels+= "'"+caption[colIndex]+"'";
						elem_type+= "'number'";
						elem_required+=false;
					}else{
						elem+= "'"+columns[colIndex]+"'";
						table_labels+= "'"+caption[colIndex]+"'";
						elem_type+= "'"+datatype[col]+"'";
						elem_required+=true;

					}


				}else{
					elem+= ",'"+columns[colIndex]+"'";
					table_labels+= ",'"+caption[colIndex]+"'";
					elem_type+= ",'"+datatype[col]+"'";
					elem_required+= ","+true;
				}

			}
			if(datatype.length==visibleCol.length){
				for(int k=0; k<visibleCol.length; k++){
					if(k==0)
						tmp_elem_type+= "'"+datatype[k]+"'";
					else
						tmp_elem_type+= ",'"+datatype[k]+"'";
				}
				elem_type=tmp_elem_type;
			}


		}else{
			for(int col=0; col<getColumnCount(); col++) {
				if(col==0){
					elem+= "'"+columns[col]+"'";
					elem_type+= "'text'";
					elem_required+= false;
					table_labels+= "'"+columns[col]+"'";


				}else{
					if(columns[col].equalsIgnoreCase("Name")){
						elem+= ",'"+columns[col]+"'";
						table_labels+= ",'"+columns[col]+"'";
					}else{
						elem+= ",'"+columns[col]+"'";
						table_labels+= ",'"+columns[col]+"'";

					}

					elem_type+= ",'text'";
					elem_required+= ","+true;

				}

			}
			if(datatype.length==getColumnCount()){
				for(int k=0;k<getColumnCount();k++){
					if(k==0)
						tmp_elem_type+= "'text'";
					else
						tmp_elem_type+= ",'text'";
				}
				elem_type=tmp_elem_type;
			}


		}
		int count=0;
		if( !reason.equalsIgnoreCase("sort")){

			for(int row=0; row<getRowCount()&& verifycol; row++) {
				count++;
				String[] rowData = getRow(row);
				if( visibleCol.length >0){
					for(int col=0; col<visibleCol.length ; col++) {
						int colIndex=visibleCol[col];
						if (col==0)
							fieldvalue+="\""+((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex])+"\"";
						else
							fieldvalue+= "+x+\""+ ((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex])+"\"";
					}
					if (row<getRowCount()-1)
						fieldvalue+= "+y+";
				}else{
					for(int col=0; col<getColumnCount(); col++) {
						if (col==0)
							fieldvalue+="\""+((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+"\"";
						else

							fieldvalue+= "+x+\""+ ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+"\"";
					}
					if (row<getRowCount()-1)
						fieldvalue+= "+y+";

				}
			}
			count++;
		}
		if(verifycol && !reason.equalsIgnoreCase("sort")){
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['next"+form+ "idx'].value="+count+";");
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['lineindex'].value=0;");
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['"+form+ "data'].value="+ fieldvalue+";");
		}
		buffer.append("\n\t\t "+elem+");\n\t\t"+elem_type+");\n\t\t"+elem_required+");\n\t\t"+table_labels+");\n\t\t");
		buffer.append("\n\t\t"+form+"_grid= new Grid('"+form+"',"+form+"_elem,"+form+"_elem_types,"+form+"_elem_required," +
				"document.forms['"+form+"_form'],document.forms['"+form+"_form'],document.all."+form+"_splits,"+form+"_table_labels,true);");
		buffer.append("\n\t\t\t" + form+"_grid.barchartelement="+form+"_barchartelement;");
		//buffer.append("\n\t\t"+form+"_grid.buildtable(); \n \t}");
		if(reason.equalsIgnoreCase("sort"))
			buffer.append("\n\t\t"+form+"_grid.sorttable(label); \n \t");
		else{
			buffer.append("\n\t\t"+form+"_grid.buildtable(); \n \t}");
			buffer.append("\n</SCRIPT>");
		}
		return(buffer.toString());
	}
	//this function validate the schema null and not null fields
	public String getFormData(String form, String []columns,String []caption,int[] visibleCol,String[]datatype,String reason,boolean [] required) {
		StringBuffer buffer =new StringBuffer();

		if(!reason.equalsIgnoreCase("sort"))
			buffer.append("<SCRIPT language=JavaScript> \n \t " +
					"function get"+form+"GridData(){ \n\t\t" +
					"var x=String.fromCharCode(1); \n\t\t" +
					"var y=String.fromCharCode(2); \n\t\t");

		String elem="var "+form+"_elem= new Array(";
		String elem_type="var "+form+"_elem_types= new Array(";
		String tmp_elem_type="var "+form+"_elem_types= new Array(";
		String elem_required="var "+form+"_elem_required= new Array(";
		String table_labels="var "+form+"_table_labels= new Array(";
		String fieldvalue="";
		boolean verifycol=false;
		boolean isurl=false;
		String urlval="";
		//logger.info("Before Verifying col column jsp length="+columnNames.length+" Column query length="+columns.length);
		//logger.info("Row Count="+getRowCount());
		if(getRowCount()>0&&getColumnNames().length>=columns.length){
			//logger.info("Verifying col");
			verifycol=true;
			for(int v=0; v<columns.length;v++){
				//logger.info("column jsp="+columns[v]+" Column query="+columnNames[v]);
				if(getColumnIndex(columns[v])==-1){
					verifycol=false;
					break;
				}
			}

		}

		if( visibleCol.length >0 && caption.length>0 && columns.length>0
				&&  caption.length>=visibleCol.length && columns.length>=visibleCol.length &&
				datatype.length>0 &&datatype.length>=visibleCol.length){

			for(int col=0; col<visibleCol.length ; col++) {
				int colIndex=visibleCol[col];

				if(col==0){
					if(columns[colIndex].equalsIgnoreCase("ObjId")){
						elem+= "'"+columns[colIndex]+"'";
						table_labels+= "'"+caption[colIndex]+"'";
						elem_type+= "'number'";
						elem_required+=false;
					}else{
						elem+= "'"+columns[colIndex]+"'";
						table_labels+= "'"+caption[colIndex]+"'";
						elem_type+= "'"+datatype[col]+"'";
						elem_required+=required[colIndex];

					}


				}else{
					elem+= ",'"+columns[colIndex]+"'";
					table_labels+= ",'"+caption[colIndex]+"'";
					elem_type+= ",'"+datatype[col]+"'";
					elem_required+= ","+required[colIndex];
				}

			}
			if(datatype.length==visibleCol.length){
				for(int k=0; k<visibleCol.length; k++){
					if(k==0)
						tmp_elem_type+= "'"+datatype[k]+"'";
					else
						tmp_elem_type+= ",'"+datatype[k]+"'";
				}
				elem_type=tmp_elem_type;
			}


		}else{
			for(int col=0; col<getColumnCount(); col++) {
				if(col==0){
					elem+= "'"+columns[col]+"'";
					elem_type+= "'text'";
					elem_required+= false;
					table_labels+= "'"+columns[col]+"'";


				}else{
					if(columns[col].equalsIgnoreCase("Name")){
						elem+= ",'"+columns[col]+"'";
						table_labels+= ",'"+columns[col]+"'";
					}else{
						elem+= ",'"+columns[col]+"'";
						table_labels+= ",'"+columns[col]+"'";

					}

					elem_type+= ",'text'";
					elem_required+= ","+required[col];

				}

			}
			if(datatype.length==getColumnCount()){
				for(int k=0;k<getColumnCount();k++){
					if(k==0)
						tmp_elem_type+= "'text'";
					else
						tmp_elem_type+= ",'text'";
				}
				elem_type=tmp_elem_type;
			}


		}
		int count=0;
		if( !reason.equalsIgnoreCase("sort")){

			for(int row=0; row<getRowCount()&& verifycol; row++) {
				count++;
				String[] rowData = getRow(row);
				if( visibleCol.length >0){
					for(int col=0; col<visibleCol.length ; col++) {
						int colIndex=visibleCol[col];
						isurl=(getColumnNames()[colIndex]!=null &&getColumnNames()[colIndex].equalsIgnoreCase("url")||rowData[colIndex]!=null&&rowData[colIndex].indexOf("<a")>=0)==true?true:false;
						//logger.info("Column Name="+getColumnNames()[colIndex] + "  Isurl="+isurl);
						if(isurl)
							urlval=rowData[colIndex]!=null&&rowData[colIndex].indexOf("<a")>=0?rowData[colIndex]:
								(rowData[colIndex].indexOf("http://")>=0||rowData[colIndex].indexOf("https://")>=0||rowData[colIndex].indexOf("www.")>=0)?
										"<a href="+rowData[colIndex]+" target=_blank>See Detail Attachment</a>":"<a href=/service?url="+rowData[colIndex]+" target=_blank>See Detail Attachment</a>";
							if (col==0)
								fieldvalue+="\""+((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex])+"\"";
							else if(isurl)
								fieldvalue+="+x+\""+urlval+"\"";
							else
								fieldvalue+= "+x+\""+ ((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex])+"\"";
					}
					if (row<getRowCount()-1)
						fieldvalue+= "+y+";
				}else{
					for(int col=0; col<getColumnCount(); col++) {
						isurl=(getColumnNames()[col]!=null &&getColumnNames()[col].equalsIgnoreCase("url")||rowData[col]!=null&&rowData[col].indexOf("<a")>=0)==true?true:false;
						//logger.info("Column Name="+getColumnNames()[col] + "  Isurl="+isurl);
						if(isurl)
							urlval=rowData[col]!=null&&rowData[col].indexOf("<a")>=0?rowData[col]:
								(rowData[col].indexOf("http://")>=0||rowData[col].indexOf("https://")>=0||rowData[col].indexOf("www.")>=0)?
										"<a href="+rowData[col]+" target=_blank>See Detail Attachment</a>":"<a href=/service?url="+rowData[col]+" target=_blank>See Detail Attachment</a>";

							if (col==0)
								fieldvalue+="\""+((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+"\"";
							else

								fieldvalue+= "+x+\""+ ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+"\"";
					}
					if (row<getRowCount()-1)
						fieldvalue+= "+y+";

				}
			}
			count++;
		}
		if(verifycol && !reason.equalsIgnoreCase("sort")){
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['next"+form+ "idx'].value="+count+";");
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['lineindex'].value=0;");
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['"+form+ "data'].value="+ fieldvalue+";");
		}
		buffer.append("\n\t\t "+elem+");\n\t\t"+elem_type+");\n\t\t"+elem_required+");\n\t\t"+table_labels+");\n\t\t");
		buffer.append("\n\t\t"+form+"_grid= new Grid('"+form+"',"+form+"_elem,"+form+"_elem_types,"+form+"_elem_required," +
				"document.forms['"+form+"_form'],document.forms['"+form+"_form'],document.all."+form+"_splits,"+form+"_table_labels,true);");
		buffer.append("\n\t\t\t" + form+"_grid.barchartelement="+form+"_barchartelement;");
		//buffer.append("\n\t\t"+form+"_grid.buildtable(); \n \t}");
		if(reason.equalsIgnoreCase("sort"))
			buffer.append("\n\t\t"+form+"_grid.sorttable(label); \n \t");
		else{
			buffer.append("\n\t\t"+form+"_grid.buildtable(); \n \t}");
			buffer.append("\n</SCRIPT>");
		}
		return(buffer.toString());
	}

	public String getSearchFormData(String form, String []columns,String []caption,int[] visibleCol,String[]datatype,String url) {

		StringBuffer buffer =
				new StringBuffer("<SCRIPT language=JavaScript> \n \t " +
						"function get"+form+"GridData(){ \n\t\t"+
						"var x=String.fromCharCode(1); \n\t\t" +
						"var y=String.fromCharCode(2); \n\t\t" );

		String elem="var "+form+"_elem= new Array(";
		String elem_type="var "+form+"_elem_types= new Array(";
		String tmp_elem_type="var "+form+"_elem_types= new Array(";
		String elem_required="var "+form+"_elem_required= new Array(";
		String table_labels="var "+form+"_table_labels= new Array(";
		String fieldvalue="";
		String idlist="";
		String objid="";
		int objidindex=0;
		boolean verifycol=false;
		boolean isurl=false;
		String urlval="";
		//logger.info("Before Verifying col column jsp length="+columnNames.length+" Column query length="+columns.length);
		if(getColumnNames().length>=columns.length){
			//logger.info("Verifying col");
			verifycol=true;
			for(int v=0; v<columns.length;v++){
				//logger.info("column jsp="+columns[v]+" Column query="+columnNames[v]);
				int colidx=getColumnIndex(columns[v]);
				if( colidx==-1){
					//logger.info("Not Found Column="+columns[v]);
					verifycol=false;
					break;
				}else{
					//visibleCol[v]=colidx;
					// logger.info("Actual Column Index="+colidx);
				}
			}

		}

		if( visibleCol.length >0 && caption.length>0 && columns.length>0
				&&  caption.length>=visibleCol.length && columns.length>=visibleCol.length &&
				datatype.length>0 &&datatype.length>=visibleCol.length){

			for(int col=0; col<visibleCol.length ; col++) {
				int colIndex=visibleCol[col];

				if(col==0){

					if(columns[colIndex].equalsIgnoreCase("ObjId")){
						elem+= "'"+columns[colIndex]+"'";
						table_labels+= "'"+caption[colIndex]+"'";
						elem_type+= "'number'";
						elem_required+=false;
					}else{
						elem+= "'"+columns[colIndex]+"'";
						table_labels+= "'"+caption[colIndex]+"'";
						elem_type+= "'"+datatype[colIndex]+"'";
						elem_required+=true;

					}


				}else{
					elem+= ",'"+columns[colIndex]+"'";
					table_labels+= ",'"+caption[colIndex]+"'";
					elem_type+= ",'"+datatype[colIndex]+"'";
					elem_required+= ","+true;
				}

			}

			if(datatype.length==visibleCol.length){
				for(int k=0; k<visibleCol.length; k++){
					if(k==0)
						tmp_elem_type+= "'"+datatype[k]+"'";
					else
						tmp_elem_type+= ",'"+datatype[k]+"'";
				}
				elem_type=tmp_elem_type;
			}


		}else{
			for(int col=0; col<getColumnCount(); col++) {
				if(col==0){
					elem+= "'"+columns[col]+"'";
					elem_type+= "'text'";
					elem_required+= false;
					table_labels+= "'"+columns[col]+"'";


				}else{
					if(columns[col].equalsIgnoreCase("Name")){
						elem+= ",'"+columns[col]+"'";
						table_labels+= ",'"+columns[col]+"'";
					}else{
						elem+= ",'"+columns[col]+"'";
						table_labels+= ",'"+columns[col]+"'";

					}

					elem_type+= ",'text'";
					elem_required+= ","+true;

				}

			}
			if(datatype.length==getColumnCount()){
				for(int k=0;k<getColumnCount();k++){
					if(k==0)
						tmp_elem_type+= "'text'";
					else
						tmp_elem_type+= ",'text'";
				}
				elem_type=tmp_elem_type;
			}


		}
		//first find all id list
		for(int row=0; row<getRowCount()&& verifycol; row++) {
			if(row==getRowCount()-1)
				idlist+=getFieldValue("ObjId",row);
			else
				idlist+=getFieldValue("ObjId",row)+",";
		}
		int count=0;
		for(int row=0; row<getRowCount()&& verifycol; row++) {
			count++;
			String[] rowData = getRow(row);

			if( visibleCol.length >0){
				for(int col=0; col<visibleCol.length ; col++) {
					int colIndex=visibleCol[col];
					int linkcolindex=(columns[colIndex].equalsIgnoreCase("name")?colIndex:-1);
					objidindex=columns[colIndex].equalsIgnoreCase("Objid")?colIndex:objidindex;
					//String link= linkcolindex>0?"<a href='javascript:void(0)' onClick='openSearchLink(&quot;"+url+"&mode=console&key=" +((rowData[objidindex]==null||rowData[objidindex].equalsIgnoreCase("null"))==true?"":rowData[objidindex]) + "&row=0&idlist="+rowData[objidindex]+"&quot;)'>" + ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+ "</a>":"";

					String link= linkcolindex>0?"<a href=" + url + "&key=" +((rowData[objidindex]==null||rowData[objidindex].equalsIgnoreCase("null"))==true?"":rowData[objidindex]) + (url.indexOf("row=0")>0?"":"&row=" + row) + "&idlist="+idlist+">" + ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+ "</a>":"";
					isurl=(columns[colIndex]!=null &&columns[colIndex].equalsIgnoreCase("url")||rowData[colIndex]!=null&&rowData[colIndex].indexOf("<a")>=0)==true?true:false;
					//logger.info("Column Name="+columns[colIndex] + "  Isurl="+isurl);
					if(isurl)
						urlval=rowData[colIndex]!=null&&rowData[colIndex].indexOf("<a")>=0?rowData[colIndex]:
							(rowData[colIndex].indexOf("http://")>=0||rowData[colIndex].indexOf("https://")>=0||rowData[colIndex].indexOf("www.")>=0)?
									"<a href="+rowData[colIndex]+" target=_blank>See Detail Attachment</a>":"<a href=/service?url="+rowData[colIndex]+" target=_blank>See Detail Attachment</a>";
						if (col==0){
							if(col==linkcolindex)
								fieldvalue+="\""+link+"\"";
							else if(isurl)
								fieldvalue+=urlval;
							else
								fieldvalue+="\""+((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex])+"\"";

						}else{
							if(col==linkcolindex)
								fieldvalue+="+x+\""+link+"\"";
							else if(isurl)
								fieldvalue+="+x+\""+urlval+"\"";
							else
								fieldvalue+= "+x+\""+((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex])+"\"";
						}
				}
				if (row<getRowCount()-1)
					fieldvalue+= "+y+";

			}else{
				for(int col=0; col<getColumnCount(); col++) {
					int colIndex=visibleCol[col];
					int linkcolindex=(columns[colIndex].equalsIgnoreCase("name")?colIndex:-1);
					objidindex=columns[colIndex].equalsIgnoreCase("Objid")?colIndex:objidindex;
					//String link= linkcolindex>0?"<a href='javascript:void(0)' onClick='openSearchLink(&quot;"+url+"&key=" +((rowData[objidindex]==null||rowData[objidindex].equalsIgnoreCase("null"))==true?"":rowData[objidindex]) + "&row=0&idlist="+rowData[objidindex]+"&quot;)'>" + ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+ "</a>":"";

					String link= linkcolindex>0?"<a href=" + url + "&key=" +((rowData[objidindex]==null||rowData[objidindex].equalsIgnoreCase("null"))==true?"":rowData[objidindex]) + (url.indexOf("row=0")>0?"":"&row=" + row) + ">" + ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+ "</a>":"";
					isurl=(columns[colIndex]!=null &&columns[colIndex].equalsIgnoreCase("url")||rowData[colIndex]!=null&&rowData[colIndex].indexOf("<a")>=0)==true?true:false;
					//logger.info("Column Name="+columns[colIndex] + "  Isurl="+isurl);
					if(isurl)
						urlval=rowData[colIndex]!=null&&rowData[colIndex].indexOf("<a")>=0?rowData[colIndex]:
							(rowData[colIndex].indexOf("http://")>=0||rowData[colIndex].indexOf("https://")>=0||rowData[colIndex].indexOf("www.")>=0)?
									"<a href="+rowData[colIndex]+" target=_blank>See Detail Attachment</a>":"<a href=/service?url="+rowData[colIndex]+" target=_blank>See Detail Attachment</a>";
						if (col==0){
							if(col==linkcolindex)
								fieldvalue+="\""+link+"\"";
							else if(isurl)
								fieldvalue+=urlval;
							else
								fieldvalue+="\""+((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex])+"\"";

						}else{
							if(col==linkcolindex)
								fieldvalue+="+x+\""+link+"\"";
							else if(isurl)
								fieldvalue+="+x+\""+urlval+"\"";
							else
								fieldvalue+= "+x+\""+ ((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex])+"\"";
						}
				}
				if (row<getRowCount()-1)
					fieldvalue+= "+y+";

			}
		}
		count++;
		if(verifycol){
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['next"+form+ "idx'].value="+count+";");
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['lineindex'].value=0;");
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['"+form+ "data'].value="+ fieldvalue+";");
		}
		buffer.append("\n\t\t "+elem+");\n\t\t"+elem_type+");\n\t\t"+elem_required+");\n\t\t"+table_labels+");\n\t\t");
		buffer.append("\n\t\t"+form+"_grid= new Grid('"+form+"',"+form+"_elem,"+form+"_elem_types,"+form+"_elem_required," +
				"document.forms['"+form+"_form'],document.forms['"+form+"_form'],document.all."+form+"_splits,"+form+"_table_labels,true);");
		buffer.append("\n\t\t\t" + form+"_grid.barchartelement="+form+"_barchartelement;");
		buffer.append("\n\t\t"+form+"_grid.buildtable(); \n \t}");

		buffer.append("\n</SCRIPT>");
		//logger.info(buffer.toString());
		return(buffer.toString());
	}

	public String getReportData(String form, String []columns,String []caption,int[] visibleCol,String[]datatype) {

		StringBuffer buffer =
				new StringBuffer("<SCRIPT language=JavaScript> \n \t " +
						"function get"+form+"GridData(){ \n\t\t"+
						"var x=String.fromCharCode(1); \n\t\t" +
						"var y=String.fromCharCode(2); \n\t\t" );

		String elem="var "+form+"_elem= new Array(";
		String elem_type="var "+form+"_elem_types= new Array(";
		String tmp_elem_type="var "+form+"_elem_types= new Array(";
		String elem_required="var "+form+"_elem_required= new Array(";
		String table_labels="var "+form+"_table_labels= new Array(";
		String fieldvalue="";
		String idlist="";
		String objid="";
		int objidindex=0;
		boolean verifycol=false;
		boolean isurl=false;
		String urlval="";
		//logger.info("Before Verifying col column jsp length="+columnNames.length+" Column query length="+columns.length);
		if(getColumnNames().length>=columns.length){
			//logger.info("Verifying col");
			verifycol=true;
			for(int v=0; v<columns.length;v++){
				//logger.info("column jsp="+columns[v]+" Column query="+columnNames[v]);
				int colidx=getColumnIndex(columns[v]);
				if( colidx==-1){
					//logger.info("Not Found Column="+columns[v]);
					verifycol=false;
					break;
				}else{
					//visibleCol[v]=colidx;
					// logger.info("Actual Column Index="+colidx);
				}
			}

		}

		if( visibleCol.length >0 && caption.length>0 && columns.length>0
				&&  caption.length>=visibleCol.length && columns.length>=visibleCol.length &&
				datatype.length>0 &&datatype.length>=visibleCol.length){

			for(int col=0; col<visibleCol.length ; col++) {
				int colIndex=visibleCol[col];

				if(col==0){

					if(columns[colIndex].equalsIgnoreCase("ObjId")){
						elem+= "'"+columns[colIndex]+"'";
						table_labels+= "'"+caption[colIndex]+"'";
						elem_type+= "'number'";
						elem_required+=false;
					}else{
						elem+= "'"+columns[colIndex]+"'";
						table_labels+= "'"+caption[colIndex]+"'";
						elem_type+= "'"+datatype[colIndex]+"'";
						elem_required+=true;

					}


				}else{
					elem+= ",'"+columns[colIndex]+"'";
					table_labels+= ",'"+caption[colIndex]+"'";
					elem_type+= ",'"+datatype[colIndex]+"'";
					elem_required+= ","+true;
				}

			}

			if(datatype.length==visibleCol.length){
				for(int k=0; k<visibleCol.length; k++){
					if(k==0)
						tmp_elem_type+= "'"+datatype[k]+"'";
					else
						tmp_elem_type+= ",'"+datatype[k]+"'";
				}
				elem_type=tmp_elem_type;
			}


		}else{
			for(int col=0; col<getColumnCount(); col++) {
				if(col==0){
					elem+= "'"+columns[col]+"'";
					elem_type+= "'text'";
					elem_required+= false;
					table_labels+= "'"+columns[col]+"'";


				}else{
					if(columns[col].equalsIgnoreCase("Name")){
						elem+= ",'"+columns[col]+"'";
						table_labels+= ",'"+columns[col]+"'";
					}else{
						elem+= ",'"+columns[col]+"'";
						table_labels+= ",'"+columns[col]+"'";

					}

					elem_type+= ",'text'";
					elem_required+= ","+true;

				}

			}
			if(datatype.length==getColumnCount()){
				for(int k=0;k<getColumnCount();k++){
					if(k==0)
						tmp_elem_type+= "'text'";
					else
						tmp_elem_type+= ",'text'";
				}
				elem_type=tmp_elem_type;
			}


		}
		//first find all id list
		for(int row=0; row<getRowCount()&& verifycol; row++) {
			if(row==getRowCount()-1)
				idlist+=getFieldValue("ObjId",row);
			else
				idlist+=getFieldValue("ObjId",row)+",";
		}
		int count=0;
		for(int row=0; row<getRowCount()&& verifycol; row++) {
			count++;
			String[] rowData = getRow(row);

			if( visibleCol.length >0){
				for(int col=0; col<visibleCol.length ; col++) {
					int colIndex=visibleCol[col];                      
					objidindex=columns[colIndex].equalsIgnoreCase("Objid")?colIndex:objidindex;

					if (col==0){         
						fieldvalue+="\""+((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex])+"\"";
					}else{
						fieldvalue+= "+x+\""+((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex])+"\"";
					}
				}
				if (row<getRowCount()-1)
					fieldvalue+= "+y+";

			}else{
				for(int col=0; col<getColumnCount(); col++) {
					int colIndex=visibleCol[col];

					objidindex=columns[colIndex].equalsIgnoreCase("Objid")?colIndex:objidindex;
					if (col==0){                          
						fieldvalue+="\""+((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex])+"\"";
					}else{
						fieldvalue+= "+x+\""+ ((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex])+"\"";
					}
				}
				if (row<getRowCount()-1)
					fieldvalue+= "+y+";

			}
		}
		count++;
		if(verifycol){
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['next"+form+ "idx'].value="+count+";");
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['lineindex'].value=0;");
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['"+form+ "data'].value="+ fieldvalue+";");
		}
		buffer.append("\n\t\t "+elem+");\n\t\t"+elem_type+");\n\t\t"+elem_required+");\n\t\t"+table_labels+");\n\t\t");
		buffer.append("\n\t\t"+form+"_grid= new Grid('"+form+"',"+form+"_elem,"+form+"_elem_types,"+form+"_elem_required," +
				"document.forms['"+form+"_form'],document.forms['"+form+"_form'],document.all."+form+"_splits,"+form+"_table_labels,true);");
		buffer.append("\n\t\t\t" + form+"_grid.barchartelement="+form+"_barchartelement;");
		buffer.append("\n\t\t"+form+"_grid.buildtable(); \n \t}");

		buffer.append("\n</SCRIPT>");
		//logger.info(buffer.toString());
		return(buffer.toString());
	}
	public String getSearchFormEditData(String form, String []columns,String []caption,int[] visibleCol,String[]datatype,String url) {

		StringBuffer buffer =
				new StringBuffer("<SCRIPT language=JavaScript> \n \t " +
						"function get"+form+"GridData(){ \n\t\t"+
						"var x=String.fromCharCode(1); \n\t\t" +
						"var y=String.fromCharCode(2); \n\t\t" );

		String elem="var "+form+"_elem= new Array(";
		String elem_type="var "+form+"_elem_types= new Array(";
		String tmp_elem_type="var "+form+"_elem_types= new Array(";
		String elem_required="var "+form+"_elem_required= new Array(";
		String table_labels="var "+form+"_table_labels= new Array(";
		String fieldvalue="";
		String idlist="";
		String objid="";
		int objidindex=0;
		boolean verifycol=false;
		boolean isurl=false;
		String urlval="";
		//logger.info("Before Verifying col column jsp length="+columnNames.length+" Column query length="+columns.length);
		if(getColumnNames().length>=columns.length){
			//logger.info("Verifying col");
			verifycol=true;
			for(int v=0; v<columns.length;v++){
				//logger.info("column jsp="+columns[v]+" Column query="+columnNames[v]);
				int colidx=getColumnIndex(columns[v]);
				if( colidx==-1){
					//logger.info("Not Found Column="+columns[v]);
					verifycol=false;
					break;
				}else{
					//visibleCol[v]=colidx;
					// logger.info("Actual Column Index="+colidx);
				}
			}

		}

		if( visibleCol.length >0 && caption.length>0 && columns.length>0
				&&  caption.length>=visibleCol.length && columns.length>=visibleCol.length &&
				datatype.length>0 &&datatype.length>=visibleCol.length){

			for(int col=0; col<visibleCol.length ; col++) {
				int colIndex=visibleCol[col];

				if(col==0){

					if(columns[colIndex].equalsIgnoreCase("ObjId")){
						elem+= "'"+columns[colIndex]+"'";
						table_labels+= "'"+caption[colIndex]+"'";
						elem_type+= "'number'";
						elem_required+=false;
					}else{
						elem+= "'"+columns[colIndex]+"'";
						table_labels+= "'"+caption[colIndex]+"'";
						elem_type+= "'"+datatype[colIndex]+"'";
						elem_required+=true;

					}


				}else{
					elem+= ",'"+columns[colIndex]+"'";
					table_labels+= ",'"+caption[colIndex]+"'";
					elem_type+= ",'"+datatype[colIndex]+"'";
					elem_required+= ","+true;
				}

			}

			if(datatype.length==visibleCol.length){
				for(int k=0; k<visibleCol.length; k++){
					if(k==0)
						tmp_elem_type+= "'"+datatype[k]+"'";
					else
						tmp_elem_type+= ",'"+datatype[k]+"'";
				}
				elem_type=tmp_elem_type;
			}


		}else{
			for(int col=0; col<getColumnCount(); col++) {
				if(col==0){
					elem+= "'"+columns[col]+"'";
					elem_type+= "'text'";
					elem_required+= false;
					table_labels+= "'"+columns[col]+"'";


				}else{
					if(columns[col].equalsIgnoreCase("Name")){
						elem+= ",'"+columns[col]+"'";
						table_labels+= ",'"+columns[col]+"'";
					}else{
						elem+= ",'"+columns[col]+"'";
						table_labels+= ",'"+columns[col]+"'";

					}

					elem_type+= ",'text'";
					elem_required+= ","+true;

				}

			}
			if(datatype.length==getColumnCount()){
				for(int k=0;k<getColumnCount();k++){
					if(k==0)
						tmp_elem_type+= "'text'";
					else
						tmp_elem_type+= ",'text'";
				}
				elem_type=tmp_elem_type;
			}


		}
		//first find all id list
		for(int row=0; row<getRowCount()&& verifycol; row++) {
			if(row==getRowCount()-1)
				idlist+=getFieldValue("ObjId",row);
			else
				idlist+=getFieldValue("ObjId",row)+",";
		}
		int count=0;
		for(int row=0; row<getRowCount()&& verifycol; row++) {
			count++;
			String[] rowData = getRow(row);

			if( visibleCol.length >0){
				for(int col=0; col<visibleCol.length ; col++) {
					int colIndex=visibleCol[col];
					int linkcolindex=(columns[colIndex].equalsIgnoreCase("name")?colIndex:-1);
					objidindex=columns[colIndex].equalsIgnoreCase("Objid")?colIndex:objidindex;
					//String link= linkcolindex>0?"<a href='javascript:void(0)' onClick='openSearchLink(&quot;"+url+"&mode=console&key=" +((rowData[objidindex]==null||rowData[objidindex].equalsIgnoreCase("null"))==true?"":rowData[objidindex]) + "&row=0&idlist="+rowData[objidindex]+"&quot;)'>" + ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+ "</a>":"";

					String link= linkcolindex>0?"<a href=" + url + "&key=" +((rowData[objidindex]==null||rowData[objidindex].equalsIgnoreCase("null"))==true?"":rowData[objidindex]) + (url.indexOf("row=0")>0?"":"&row=" + row) + "&idlist="+idlist+">" + ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+ "</a>":"";
					isurl=(columns[colIndex]!=null &&columns[colIndex].equalsIgnoreCase("url")||rowData[colIndex]!=null&&rowData[colIndex].indexOf("<a")>=0)==true?true:false;
					//logger.info("Column Name="+columns[colIndex] + "  Isurl="+isurl);
					if(isurl)
						urlval=rowData[colIndex]!=null&&rowData[colIndex].indexOf("<a")>=0?rowData[colIndex]:
							(rowData[colIndex].indexOf("http://")>=0||rowData[colIndex].indexOf("https://")>=0||rowData[colIndex].indexOf("www.")>=0)?
									"<a href="+rowData[colIndex]+" target=_blank>See Detail Attachment</a>":"<a href=/service?url="+rowData[colIndex]+" target=_blank>See Detail Attachment</a>";
						if (col==0){
							if(col==linkcolindex)
								fieldvalue+="\""+link+"\"";
							else if(isurl)
								fieldvalue+=urlval;
							else
								fieldvalue+="\""+((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex])+"\"";

						}else{
							if(col==linkcolindex)
								fieldvalue+="+x+\""+link+"\"";
							else if(isurl)
								fieldvalue+="+x+\""+urlval+"\"";
							else
								fieldvalue+= "+x+\""+((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex])+"\"";
						}
				}
				if (row<getRowCount()-1)
					fieldvalue+= "+y+";

			}else{
				for(int col=0; col<getColumnCount(); col++) {
					int colIndex=visibleCol[col];
					int linkcolindex=(columns[colIndex].equalsIgnoreCase("name")?colIndex:-1);
					objidindex=columns[colIndex].equalsIgnoreCase("Objid")?colIndex:objidindex;
					//String link= linkcolindex>0?"<a href='javascript:void(0)' onClick='openSearchLink(&quot;"+url+"&key=" +((rowData[objidindex]==null||rowData[objidindex].equalsIgnoreCase("null"))==true?"":rowData[objidindex]) + "&row=0&idlist="+rowData[objidindex]+"&quot;)'>" + ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+ "</a>":"";

					String link= linkcolindex>0?"<a href=" + url + "&key=" +((rowData[objidindex]==null||rowData[objidindex].equalsIgnoreCase("null"))==true?"":rowData[objidindex]) + (url.indexOf("row=0")>0?"":"&row=" + row) + ">" + ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+ "</a>":"";
					isurl=(columns[colIndex]!=null &&columns[colIndex].equalsIgnoreCase("url")||rowData[colIndex]!=null&&rowData[colIndex].indexOf("<a")>=0)==true?true:false;
					//logger.info("Column Name="+columns[colIndex] + "  Isurl="+isurl);
					if(isurl)
						urlval=rowData[colIndex]!=null&&rowData[colIndex].indexOf("<a")>=0?rowData[colIndex]:
							(rowData[colIndex].indexOf("http://")>=0||rowData[colIndex].indexOf("https://")>=0||rowData[colIndex].indexOf("www.")>=0)?
									"<a href="+rowData[colIndex]+" target=_blank>See Detail Attachment</a>":"<a href=/service?url="+rowData[colIndex]+" target=_blank>See Detail Attachment</a>";
						if (col==0){
							if(col==linkcolindex)
								fieldvalue+="\""+link+"\"";
							else if(isurl)
								fieldvalue+=urlval;
							else
								fieldvalue+="\""+((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex])+"\"";

						}else{
							if(col==linkcolindex)
								fieldvalue+="+x+\""+link+"\"";
							else if(isurl)
								fieldvalue+="+x+\""+urlval+"\"";
							else
								fieldvalue+= "+x+\""+ ((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex])+"\"";
						}
				}
				if (row<getRowCount()-1)
					fieldvalue+= "+y+";

			}
		}
		count++;
		if(verifycol){
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['next"+form+ "idx'].value="+count+";");
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['lineindex'].value=0;");
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['"+form+ "data'].value="+ fieldvalue+";");
		}
		buffer.append("\n\t\t "+elem+");\n\t\t"+elem_type+");\n\t\t"+elem_required+");\n\t\t"+table_labels+");\n\t\t");
		buffer.append("\n\t\t"+form+"_grid= new Grid('"+form+"',"+form+"_elem,"+form+"_elem_types,"+form+"_elem_required," +
				"document.forms['"+form+"_form'],document.forms['"+form+"_form'],document.all."+form+"_splits,"+form+"_table_labels,true);");
		buffer.append("\n\t\t\t" + form+"_grid.barchartelement="+form+"_barchartelement;");
		buffer.append("\n\t\t"+form+"_grid.edittable(); \n \t}");

		buffer.append("\n</SCRIPT>");
		//logger.info(buffer.toString());
		return(buffer.toString());
	}
	public String getJSONTable(String [] columns, String[] captions) {
		StringBuffer buffer =new StringBuffer("");
		if(columns.length!=captions.length){
			return "";
		}

		for(int row=0; row<getRowCount(); row++) {
			buffer.append("[");

			for(int col=0; col<columns.length; col++) {
				buffer.append("\n['" +captions[col] +"'," + this.getFieldValue(columns[col], row)+"]"
						+ (col<columns.length-1?",\n":"\n"));
			}

			buffer.append((row<getRowCount()-1?"],\n":"]"));
		}
		return(buffer.toString());
	}
	public String getHTMLTable() {
		StringBuffer buffer =
				new StringBuffer("<TABLE BORDER=1>\n");

		buffer.append("  <TR>\n    ");

		for(int col=0; col<getColumnCount(); col++) {
			buffer.append("<TH class=texthead>" + columnNames[col]);
		}
		for(int row=0; row<getRowCount(); row++) {
			buffer.append("\n  <TR>\n    ");
			String[] rowData = getRow(row);
			for(int col=0; col<getColumnCount(); col++) {
				buffer.append("<TD class=texttable>" + ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col]));
			}
		}
		buffer.append("\n</TBODY></TABLE>");
		return(buffer.toString());
	}

	public String getHTMLTable(int linkcol, String URL) {
		StringBuffer buffer =
				new StringBuffer("<TABLE  cellSpacing=1 cellPadding=0 width=\"100%\" border=0>\n<TBODY>\n");

		buffer.append("  <TR>\n    ");

		for(int col=0; col<getColumnCount(); col++) {
			buffer.append("<TH class=texthead>" + columnNames[col]);
		}
		for(int row=0; row<getRowCount(); row++) {
			buffer.append("\n  <TR>\n    ");
			String[] rowData = getRow(row);
			for(int col=0; col<getColumnCount(); col++) {
				if (col==linkcol)
					buffer.append("<TD class=texttable>" + "<a href=" + URL + "&key=" +
							((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col]) + "&row=" + row + ">" + ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+ "</a>" );
				else
					buffer.append("<TD class=texttable>" + ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col]));
			}
		}
		buffer.append("\n<TBODY></TABLE>");
		return(buffer.toString());
	}

	public String getHTMLTable(int linkcol,int[] visibleCol, String URL) {
		StringBuffer buffer =
				new StringBuffer("<TABLE  cellSpacing=1 cellPadding=0 width=\"100%\" border=0>\n<TBODY>\n");

		buffer.append("  <TR>\n    ");

		if( visibleCol.length >0){

			for(int col=0; col<visibleCol.length ; col++) {
				int colIndex=visibleCol[col];
				buffer.append("\n <TH class=texthead>" + columnNames[colIndex] + "</TH>\n");
			}
		}else{
			for(int col=0; col<getColumnCount(); col++) {
				buffer.append("<TH class=texthead>" + columnNames[col]);
			}

		}

		for(int row=0; row<getRowCount(); row++) {

			String[] rowData = getRow(row);
			if( visibleCol.length >0){

				buffer.append("\n </TR><TR>\n    ");
				for(int col=0; col<visibleCol.length ; col++) {
					int colIndex=visibleCol[col];
					if (colIndex==linkcol)
						buffer.append("\n <TD class=texttable>" + "<a href=" + URL + "&key=" +
								((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex]) + "&row=" + row + ">" + ((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex])+ "</a></TD>\n" );
					else
						buffer.append("\n <TD class=texttable>" + ((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex]) + "</TD>\n" );
				}
				buffer.append("</TR>\n" );


			}else{
				for(int col=0; col<getColumnCount(); col++) {
					if (col==linkcol)
						buffer.append("<TD class=texttable>" + "<a href=" + URL + "&key=" +
								((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col]) + "&row=" + row + ">" + ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+ "</a>" );
					else
						buffer.append("<TD class=texttable>" + ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col]));
				}
			}
		}
		buffer.append("\n<TBODY></TABLE>");
		return(buffer.toString());
	}


	public String getHTMLTable(String headingColor) {
		StringBuffer buffer =
				new StringBuffer("<TABLE BORDER=1 width=\"100%\">\n");
		if (headingColor != null) {
			buffer.append("  <TR BGCOLOR=\"" + headingColor +
					"\">\n    ");
		} else {
			buffer.append("  <TR>\n    ");
		}
		for(int col=0; col<getColumnCount(); col++) {
			buffer.append("<TH>" + columnNames[col]);
		}
		for(int row=0; row<getRowCount(); row++) {
			buffer.append("\n  <TR>\n    ");
			String[] rowData = getRow(row);
			for(int col=0; col<getColumnCount(); col++) {
				buffer.append("<TD>" + ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col]));
			}
		}
		buffer.append("\n</TABLE>");
		return(buffer.toString());
	}

	public String getHTMLTable(String headingColor,int linkcol, String URL) {
		StringBuffer buffer =
				new StringBuffer("<TABLE bordercolor=#993366 bgcolor=#CCFFFF BORDER=1 width=\"100%\">\n");
		if (headingColor != null) {
			buffer.append("  <TR BGCOLOR=\"" + headingColor +
					"\">\n    ");
		} else {
			buffer.append("  <TR>\n    ");
		}
		for(int col=0; col<getColumnCount(); col++) {
			buffer.append("<TH>" + columnNames[col]);
		}
		for(int row=0; row<getRowCount(); row++) {
			buffer.append("\n  <TR>\n    ");
			String[] rowData = getRow(row);
			for(int col=0; col<getColumnCount(); col++) {
				if (col==linkcol)
					buffer.append("<TD>" + "<a href=" + URL + "&key=" +
							((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col]) + "&row=" + row + ">" + ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+ "</a>" );
				else
					buffer.append("<TD>" + ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col]));
			}
		}
		buffer.append("\n</TABLE>");
		return(buffer.toString());
	}

	public String getHTMLTable(String headingColor,int linkcol,int[] visibleCol, String URL) {
		StringBuffer buffer =
				new StringBuffer("<TABLE bordercolor=#993366 bgcolor=#CCFFFF BORDER=1 width=\"100%\">\n");
		if (headingColor != null) {
			buffer.append("  <TR BGCOLOR=\"" + headingColor +
					"\">\n    ");
		} else {
			buffer.append("  <TR>\n    ");
		}
		if( visibleCol.length >0){

			for(int col=0; col<visibleCol.length ; col++) {
				int colIndex=visibleCol[col];
				buffer.append("\n <TH>" + columnNames[colIndex] + "</TH>\n");
			}
		}else{
			for(int col=0; col<getColumnCount(); col++) {
				buffer.append("<TH>" + columnNames[col]);
			}

		}

		for(int row=0; row<getRowCount(); row++) {

			String[] rowData = getRow(row);
			if( visibleCol.length >0){

				buffer.append("\n </TR><TR>\n    ");
				for(int col=0; col<visibleCol.length ; col++) {
					int colIndex=visibleCol[col];
					if (colIndex==linkcol)
						buffer.append("\n <TD>" + "<a href=" + URL + "&key=" +
								((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex]) + "&row=" + row + ">" + ((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex])+ "</a></TD>\n" );
					else
						buffer.append("\n <TD>" + ((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex]) + "</TD>\n" );
				}
				buffer.append("</TR>\n" );


			}else{
				for(int col=0; col<getColumnCount(); col++) {
					if (col==linkcol)
						buffer.append("<TD>" + "<a href=" + URL + "&key=" +
								((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col]) + "&row=" + row + ">" + ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+ "</a>" );
					else
						buffer.append("<TD>" + ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col]));
				}
			}
		}
		buffer.append("\n</TABLE>");
		return(buffer.toString());
	}

	public String getHTMLList(String caption,String name, String defaultvalue,String size, boolean multiple) {
		StringBuffer buffer =
				new StringBuffer("");
		if ( size==null)
			size="10";

		if (caption != null && !multiple) {
			buffer.append ("<TR><TD><B>" +caption + "</B>:</TD><TD><select name=\"" + name +
					"\" size= \"" + size + "\" >\n " );
		}else if ("<TR><TD><B>" + caption != null && multiple) {
			buffer.append (caption + "</B>:</TD><TD><select name=\"" + name +
					"\" size= \"" + size + "\" multiple >\n    ");
		}
		for(int row=0; row<getRowCount(); row++) {
			String[] rowData = getRow(row);
			if (defaultvalue!= null && rowData[0].equals(defaultvalue))
				buffer.append("\n  <option selected value=\"" + rowData[0]  + "\" > " );
			else
				buffer.append("\n  <option  value=\"" + rowData[0]  + "\" > " );

			for(int col=0; col<getColumnCount(); col++) {
				buffer.append(((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col]) + "    " );
			}
		}
		if (caption==null)
			buffer.append("\n <option value=\"" + "\"  ></option></select>");
		else
			buffer.append("\n <option value=\"" + "\"  ></option></select></TD></TR>");


		return(buffer.toString());
	}

	public String getHTMLComboList(String caption,String name, String defaultvalue, boolean multiple) {
		StringBuffer buffer =
				new StringBuffer("");

		if (caption != null && !multiple) {
			buffer.append ("<TR><TD><B>" +caption + "</B>:</TD><TD><select name=\"" + name +
					"\" >\n " );
		}else if ("<TR><TD><B>" + caption != null && multiple) {
			buffer.append (caption + "</B>:</TD><TD><select name=\"" + name +
					"\"  multiple >\n    ");
		}
		for(int row=0; row<getRowCount(); row++) {
			String[] rowData = getRow(row);
			if (defaultvalue!= null && rowData[0].equals(defaultvalue))
				buffer.append("\n  <option selected value=\"" + rowData[0]  + "\" > " );
			else
				buffer.append("\n  <option  value=\"" + rowData[0]  + "\" > " );

			for(int col=0; col<getColumnCount(); col++) {
				buffer.append(((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col]) + "    " );
			}
		}
		if (caption==null)
			buffer.append("\n <option value=\"" + "\"   ></option></select>");
		else
			buffer.append("\n <option value=\"" + "\"   ></option></select></TD></TR>");


		return(buffer.toString());
	}
	/*this method is done by brinda
        it will forward to corresponding page from
        console depending on name of colsole table
	 */
	public String getSearchConsoleData(String form, String []columns,String []caption,
			int[] visibleCol,String[]datatype,String root,String usertoken) {
		//logger.info("the form value fromtemplateaTable="+ form);

		StringBuffer buffer =
				new StringBuffer("<SCRIPT language=JavaScript> \n \t " +
						"function get"+form+"GridData(){ \n\t\t"+
						"var x=String.fromCharCode(1); \n\t\t" +
						"var y=String.fromCharCode(2); \n\t\t" );

		String elem="var "+form+"_elem= new Array(";
		String elem_type="var "+form+"_elem_types= new Array(";
		String tmp_elem_type="var "+form+"_elem_types= new Array(";
		String elem_required="var "+form+"_elem_required= new Array(";
		String table_labels="var "+form+"_table_labels= new Array(";
		String fieldvalue="";
		String idlist="";
		String objid="";
		String url="";
		int objidindex=0;
		boolean verifycol=false;
		boolean isurl=false;
		String urlval="";
		//first find out the objidindex for field KeyObjId
		for(int i=0;i<columns.length; i++){
			if (columns[i].equalsIgnoreCase("KeyObjId")){
				objidindex=i;
				break;
			}
		}

		if(getColumnNames().length>=columns.length){

			verifycol=true;
			for(int v=0; v<columns.length;v++){

				int colidx=getColumnIndex(columns[v]);
				if( colidx==-1){

					verifycol=false;
					break;
				}else{
					//visibleCol[v]=colidx;
					//logger.info("Actual Column Index="+colidx);
				}
			}

		}

		if( visibleCol.length >0 && caption.length>0 && columns.length>0
				&&  caption.length>=visibleCol.length && columns.length>=visibleCol.length &&
				datatype.length>0 &&datatype.length>=visibleCol.length){

			//logger.info("Verifying col ********"+visibleCol.length );
			for(int col=0; col<visibleCol.length ; col++) {
				int colIndex=visibleCol[col];

				if(col==0){

					if(columns[colIndex].equalsIgnoreCase("ObjId")){
						elem+= "'"+columns[colIndex]+"'";
						table_labels+= "'"+caption[colIndex]+"'";
						elem_type+= "'number'";
						elem_required+=false;
					}else{
						elem+= "'"+columns[colIndex]+"'";
						table_labels+= "'"+caption[colIndex]+"'";
						elem_type+= "'"+datatype[col]+"'";
						elem_required+=true;

					}


				}else{
					elem+= ",'"+columns[colIndex]+"'";
					table_labels+= ",'"+caption[colIndex]+"'";
					elem_type+= ",'"+datatype[col]+"'";
					elem_required+= ","+true;
				}

			}

			if(datatype.length==visibleCol.length){
				for(int k=0; k<visibleCol.length; k++){
					if(k==0)
						tmp_elem_type+= "'"+datatype[k]+"'";
					else
						tmp_elem_type+= ",'"+datatype[k]+"'";
				}
				elem_type=tmp_elem_type;
			}


		}else{
			for(int col=0; col<getColumnCount(); col++) {
				if(col==0){
					elem+= "'"+columns[col]+"'";
					elem_type+= "'text'";
					elem_required+= false;
					table_labels+= "'"+columns[col]+"'";


				}else{

					if(columns[col].equalsIgnoreCase("Name")){
						elem+= ",'"+columns[col]+"'";
						table_labels+= ",'"+columns[col]+"'";
					}else{
						elem+= ",'"+columns[col]+"'";
						table_labels+= ",'"+columns[col]+"'";

					}

					elem_type+= ",'text'";
					elem_required+= ","+true;

				}

			}
			if(datatype.length==getColumnCount()){
				for(int k=0;k<getColumnCount();k++){
					if(k==0)
						tmp_elem_type+= "'text'";
					else
						tmp_elem_type+= ",'text'";
				}
				elem_type=tmp_elem_type;
			}


		}
		//first find all id list
		//logger.info("rowcount="+getRowCount());
		for(int row=0; row<getRowCount()&& verifycol; row++) {
			if(row==getRowCount()-1)
				idlist+=getFieldValue("ObjId",row);
			else
				idlist+=getFieldValue("ObjId",row)+",";
		}
		int count=0;
		//logger.info("rowcount="+getRowCount()+ "verifycol="+verifycol);
		for(int row=0; row<getRowCount()&& verifycol; row++) {
			count++;
			String[] rowData = getRow(row);

			if( visibleCol.length >0){
				for(int col=0; col<visibleCol.length ; col++) {
					int colIndex=visibleCol[col];
					int linkcolindex=(columns[colIndex].equalsIgnoreCase("name")?colIndex:-1);
					String tabname="";
					String type="";
					if(columns[colIndex].equalsIgnoreCase("name")){
						tabname=rowData[linkcolindex].toLowerCase() ;                                        
						tabname=tabname+"/"+tabname+"peer.jsp";                                         
						url=root+tabname+usertoken;
					}

					objidindex=columns[colIndex].equalsIgnoreCase("KeyObjid")?colIndex:objidindex;
					String link= linkcolindex>0?"<a href=" + url + "&key=" +((rowData[objidindex]==null||rowData[objidindex].equalsIgnoreCase("null"))==true?"":rowData[objidindex]) + "&row=0&idlist="+rowData[objidindex]+"&mode=null>" + ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+ "</a>":"";
					//String link= linkcolindex>0?"<a href='javascript:void(0)' onClick='openConsoleLink(&quot;"+url+"&mode=console&key=" +((rowData[objidindex]==null||rowData[objidindex].equalsIgnoreCase("null"))==true?"":rowData[objidindex]) + "&row=0&idlist="+rowData[objidindex]+"&quot;)'>" + ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+ "</a>":"";
					isurl=(columns[colIndex]!=null &&columns[colIndex].equalsIgnoreCase("url")||rowData[colIndex]!=null&&rowData[colIndex].indexOf("<a")>=0)==true?true:false;
					//System.out.print("objid="+objidindex);
					if(isurl)
						urlval=rowData[colIndex]!=null&&rowData[colIndex].indexOf("<a")>=0?rowData[colIndex]:
							(rowData[colIndex].indexOf("http://")>=0||rowData[colIndex].indexOf("https://")>=0||rowData[colIndex].indexOf("www.")>=0)?
									"<a href="+rowData[colIndex]+" target=_blank>See Detail Attachment</a>":"<a href=/service?url="+rowData[colIndex]+" target=_blank>See Detail Attachment</a>";
						if (col==0){
							if(col==linkcolindex)
								fieldvalue+="\""+link+"\"";
							else if(isurl)
								fieldvalue+=urlval;
							else
								fieldvalue+="\""+((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex])+"\"";

						}else{
							if(col==linkcolindex)
								fieldvalue+="+x+\""+link+"\"";
							else if(isurl)
								fieldvalue+="+x+\""+urlval+"\"";
							else
								fieldvalue+= "+x+\""+((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex])+"\"";
						}
				}
				if (row<getRowCount()-1)
					fieldvalue+= "+y+";

			}else{
				for(int col=0; col<getColumnCount(); col++) {
					int colIndex=visibleCol[col];
					int linkcolindex=(columns[colIndex].equalsIgnoreCase("name")?colIndex:-1);
					objidindex=columns[colIndex].equalsIgnoreCase("KeyObjid")?colIndex:objidindex;
					String tabname="";
					String type="";
					if(columns[colIndex].equalsIgnoreCase("name")){
						tabname=rowData[linkcolindex].toLowerCase() ;

						tabname=tabname+"/"+tabname+"peer.jsp";
						url=root+tabname+usertoken;

						//logger.info("url="+url );
					}
					//String link= linkcolindex>0?"<a href='javascript:void(0)' onClick='openConsoleLink(&quot;"+url+"&mode=console&key=" +((rowData[objidindex]==null||rowData[objidindex].equalsIgnoreCase("null"))==true?"":rowData[objidindex]) + "&row=0&idlist="+rowData[objidindex]+"&quot;)'>" + ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+ "</a>":"";

					String link= linkcolindex>0?"<a href=" + url + "&key=" +((rowData[objidindex]==null||rowData[objidindex].equalsIgnoreCase("null"))==true?"":rowData[objidindex]) + "&row=" + row + "&mode=null>" + ((rowData[col]==null||rowData[col].equalsIgnoreCase("null"))==true?"":rowData[col])+ "</a>":"";
					isurl=(columns[colIndex]!=null &&columns[colIndex].equalsIgnoreCase("url")||rowData[colIndex]!=null&&rowData[colIndex].indexOf("<a")>=0)==true?true:false;

					if(isurl)
						urlval=rowData[colIndex]!=null&&rowData[colIndex].indexOf("<a")>=0?rowData[colIndex]:
							(rowData[colIndex].indexOf("http://")>=0||rowData[colIndex].indexOf("https://")>=0||rowData[colIndex].indexOf("www.")>=0)?
									"<a href="+rowData[colIndex]+" target=_blank>See Detail Attachment</a>":"<a href=/service?url="+rowData[colIndex]+" target=_blank>See Detail Attachment</a>";
						if (col==0){
							if(col==linkcolindex)
								fieldvalue+="\""+link+"\"";
							else if(isurl)
								fieldvalue+=urlval;
							else
								fieldvalue+="\""+((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex])+"\"";

						}else{
							if(col==linkcolindex)
								fieldvalue+="+x+\""+link+"\"";
							else if(isurl)
								fieldvalue+="+x+\""+urlval+"\"";
							else
								fieldvalue+= "+x+\""+ ((rowData[colIndex]==null||rowData[colIndex].equalsIgnoreCase("null"))==true?"":rowData[colIndex])+"\"";
						}
				}
				if (row<getRowCount()-1)
					fieldvalue+= "+y+";

			}
		}
		count++;
		if(verifycol){
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['next"+form+ "idx'].value="+count+";");
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['lineindex'].value=0;");
			buffer.append("\n\t\t document.forms['"+form+"_form'].elements['"+form+ "data'].value="+ fieldvalue+";");
		}
		buffer.append("\n\t\t "+elem+");\n\t\t"+elem_type+");\n\t\t"+elem_required+");\n\t\t"+table_labels+");\n\t\t");
		buffer.append("\n\t\t"+form+"_grid= new Grid('"+form+"',"+form+"_elem,"+form+"_elem_types,"+form+"_elem_required," +
				"document.forms['"+form+"_form'],document.forms['"+form+"_form'],document.all."+form+"_splits,"+form+"_table_labels,true);");
		buffer.append("\n\t\t\t" + form+"_grid.barchartelement="+form+"_barchartelement;");
		buffer.append("\n\t\t"+form+"_grid.buildtable(); \n \t}");

		buffer.append("\n</SCRIPT>");
		return(buffer.toString());
	}



}


