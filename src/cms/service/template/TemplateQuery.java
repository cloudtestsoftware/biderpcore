package cms.service.template;

import java.text.*;
import java.util.*;
import java.sql.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cms.service.app.ApplicationConstants;
import cms.service.app.PartitionObject;

import cms.service.jdbc.DataType;
import cms.service.jdbc.DatabaseTransaction;
import cms.service.jdbc.DatabaseUtilities;
import cms.service.jdbc.ParseQuery;






public class TemplateQuery extends ParseQuery{
	static Log logger = LogFactory.getLog(TemplateQuery.class);
	private DatabaseUtilities du =new DatabaseUtilities();
	private String queryname ="";
	private PartitionObject key = new PartitionObject();
	private static ApplicationConstants ACONST = new ApplicationConstants();
	private TemplateUtility tu=new TemplateUtility();
	private TemplateXml tx=new TemplateXml();
	private TemplateTable data;

	private String appname="";
	private String objid="";
	private String uname="";
	private String sfield="";
	private String header="";
	private String[] uobjid;
	private int numRows=0;
	private int startRow=0;
	private int totalRows=0;
	private static HashMap priv = new HashMap();

	public void setQuery(String query) {
		super.setQuery(query);
	}
	public String getQuery() {
		return(super.getQuery());
	}
	public void setAppName(String appname){
		du.setAppName(appname);
		this.appname=appname;
	}

	public String getAppName(){
		return(this.appname);
	}
	public void setObjId(String objid) {
		this.objid=objid;
	}
	public String getObjId() {
		return(objid);
	}
	public void setTableData(TemplateTable data) {
		this.data=data;
	}
	public TemplateTable getTableData() {
		return(data);
	}
	public void setSummaryField(String sfield) {
		this.sfield=sfield;
	}
	public String getSummaryField() {
		return(sfield);
	}
	public void setUserName(String uname) {
		this.uname=uname;
	}
	public String getUserName() {
		return(uname);
	}
	public void setReportHeader(String header) {
		this.header=header;
	}
	public String getReportHeader() {
		return(header);
	}
	public void setUnchangedObjId(String[] objid) {
		this.uobjid=objid;
	}
	public String[] getUnchangedObjId() {
		return(uobjid);
	}
	public void setQueryName(String queryname) {
		this.queryname=queryname;
	}
	public String getQueryName() {
		return(queryname);
	}
	public int getTotalRow(){
		return(this.totalRows);
	}
	public void setTotalRow(int row){
		totalRows=row;
	}
	public void setStartRow(int row){
		this.startRow=row;
	}
	public  int getStartRow(){
		return(startRow);
	}
	public void setNumRows(int row){
		this.numRows=row;
	}
	public int getNumRows(){
		return(numRows);
	}
	
	public String getPrimaryKey(){
		return(key.getPrimaryKey());
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
				super.mapInputTable();
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
			if ((index.length == datatype.length) && (index.length == value.length)){
				super.mapInputTable();
			}else{
				throw new Exception();
			}

		}catch(Exception ie){
			logger.error(" Array size for Index and value are not same!" +ie);
			logger.error(" index length=" + index.length + " datatype length=" + datatype.length + " value Length=" + value.length );
		}

	}

	public TemplateTable getTableMetaData(String table){
		try{
			outputTable=du.getTableMetaData(table);

			return(outputTable);
		} catch(SQLException sqle) {
			logger.error("Error executing SQL: " + sqle);
			return(null);
		}

	}

	public TemplateTable getTableResultset(){
		try{
			if(getNumRows()>0||getStartRow()>0){
				du.setStartRow(startRow);
				du.setNumRows(numRows);
			}else{
				du.setStartRow(0);
				du.setNumRows(0);
			}

			outputTable=du.getQueryResults(super.getQuery(),true);
			//System.out.print("TemplateQuery.getTableResultset().rowCount="+outputTable.getRowCount());

			return(outputTable);
		} catch(SQLException sqle) {
			logger.error("Error executing SQL: " + sqle);
			return(null);
		}

	}

	public int getColumnCount() {
		return(outputTable.getColumnCount());
	}

	public String getColumnName(int column) {
		return(outputTable.getColumnNames()[column]);
	}

	public String[] getRow(int index) {
		return((String[])outputTable.getRow(index));
	}

	public Object getValueAt(int row, int column) {
		return(outputTable.getRow(row)[column]);
	}

	public Object[] getColumnArray(int column) {
		Object[] colArray = new Object[outputTable.getRowCount()];
		for (int i=0; i<outputTable.getRowCount(); i++)
			colArray[i] = outputTable.getRow(i)[column];

		return(colArray);
	}
	public String[] convertDataType(String [] strDatatype){
		String[] tmpar =new String[strDatatype.length];
		for (int i=0;i<strDatatype.length;i++){
			if(strDatatype[i].equalsIgnoreCase("VARCHAR"))
				tmpar[i]=DataType.VARCHAR;
			if(strDatatype[i].equalsIgnoreCase("RAW"))
				tmpar[i]=DataType.RAW;
			if(strDatatype[i].equalsIgnoreCase("INTEGER"))
				tmpar[i]=DataType.INTEGER;
			if(strDatatype[i].equalsIgnoreCase("NUMBER"))
				tmpar[i]=DataType.NUMBER;
			if(strDatatype[i].equalsIgnoreCase("DATE"))
				tmpar[i]=DataType.DATE;
			if(strDatatype[i].equalsIgnoreCase("FLOAT"))
				tmpar[i]=DataType.FLOAT;
			if(strDatatype[i].equalsIgnoreCase("DOUBLE"))
				tmpar[i]=DataType.DOUBLE;
			if(strDatatype[i].equalsIgnoreCase("DECIMAL"))
				tmpar[i]=DataType.DECIMAL;
			if(strDatatype[i].equalsIgnoreCase("CHAR"))
				tmpar[i]=DataType.CHAR;
			if(strDatatype[i].equalsIgnoreCase("BIGINT"))
				tmpar[i]=DataType.BIGINT;
			if(strDatatype[i].equalsIgnoreCase("BINARY"))
				tmpar[i]=DataType.BINARY;
			if(strDatatype[i].equalsIgnoreCase("LONGVARBINARY"))
				tmpar[i]=DataType.LONGVARBINARY;
			if(strDatatype[i].equalsIgnoreCase("LONGVARCHAR"))
				tmpar[i]=DataType.LONGVARCHAR;
			if(strDatatype[i].equalsIgnoreCase("LONG"))
				tmpar[i]=DataType.LONGVARCHAR;
			if(strDatatype[i].equalsIgnoreCase("REAL"))
				tmpar[i]=DataType.REAL;
			if(strDatatype[i].equalsIgnoreCase("TIME"))
				tmpar[i]=DataType.TIME;
			if(strDatatype[i].equalsIgnoreCase("TIMESTAMP"))
				tmpar[i]=DataType.TIMESTAMP;
			if(strDatatype[i].equalsIgnoreCase("TINYINT"))
				tmpar[i]=DataType.TINYINT;
			if(strDatatype[i].equalsIgnoreCase("SMALLINT"))
				tmpar[i]=DataType.SMALLINT;
			if(strDatatype[i].equalsIgnoreCase("NULL"))
				tmpar[i]=DataType.NULL;
			if(strDatatype[i].equalsIgnoreCase("VARBINARY"))
				tmpar[i]=DataType.VARBINARY;
		}
		return(tmpar);
	}
	public String[] getCustomInsertArray(String[] baseline, String[] custom) {
		int length = baseline.length + custom.length;
		String [] mycustom = new String[length];
		for (int i=0 ; i<baseline.length; i++)
			mycustom[i]=baseline[i];
		for (int j=baseline.length; j<length ; j++)
			mycustom[j]=custom[j-baseline.length];

		return(mycustom);
	}

	public String[] getCustomUpdateArray(String[] baseline, String[] custom) {
		int length = baseline.length + custom.length;
		String [] mycustom = new String[length];
		for (int i=0 ; i< baseline.length -1; i++)
			mycustom[i]=baseline[i];
		for (int j=baseline.length -1; j<length -1 ; j++)
			mycustom[j]=custom[j-baseline.length];

		mycustom[length]=mycustom[baseline.length];

		return(mycustom);
	}

	public int[] getCustomIndexArray(int[] baseline, int[] custom) {
		int length = baseline.length + custom.length;
		int [] mycustom = new int[length];
		for (int i=0 ; i<length; i++)
			mycustom[i]=i ;

		return(mycustom);
	}
	public int[] getCustomIndexArray(int[] baseline, int custlength) {
		int length = baseline.length +custlength;
		int [] mycustom = new int[length];
		for (int i=0 ; i<length; i++)
			mycustom[i]=i ;

		return(mycustom);
	}
	public int[] getCustomIndexArray(String[] baseline, int custlength) {
		int length = baseline.length +custlength;
		int [] mycustom = new int[length];
		for (int i=0 ; i<length; i++)
			mycustom[i]=i ;

		return(mycustom);
	}

	public String[] getCustomInsertValue(String vendor,String[] baseline, String[] customvalue, String[] customdatatype) {
		int length = baseline.length + customvalue.length;
		String [] mycustom = new String[length];
		for (int i=0 ; i<baseline.length; i++)
			mycustom[i]=baseline[i];
		for (int j=baseline.length; j<length ; j++){
			if(customdatatype[j-baseline.length].equalsIgnoreCase(DataType.DATE)){
				if(vendor.equalsIgnoreCase("Oracle"))
					customvalue[j-baseline.length]="\"to_date('"+ customvalue[j-baseline.length] + "' ,'dd/mm/yyyy')\"";
				else if(vendor.equalsIgnoreCase("Mssql"))
					customvalue[j-baseline.length]="\"cast('"+ customvalue[j-baseline.length] + "' as datetime)\"";

			}else
				mycustom[j]=customvalue[j-baseline.length];
		}
		return(mycustom);
	}
	public String[] getCustomUpdateValue(String vendor,String[] baseline, String[] customvalue,String[] customdatatype) {
		int length = baseline.length + customvalue.length;
		String [] mycustom = new String[length];
		for (int i=0 ; i< baseline.length -1; i++)
			mycustom[i]=baseline[i];
		for (int j=baseline.length -1; j<length -1 ; j++){
			if(customdatatype[j-baseline.length].equalsIgnoreCase(DataType.DATE)){
				if(vendor.equalsIgnoreCase("Oracle"))
					customvalue[j-baseline.length]="\"to_date('"+ customvalue[j-baseline.length] + "' ,'dd/mm/yyyy')\"";
				else if(vendor.equalsIgnoreCase("Mssql"))
					customvalue[j-baseline.length]="\"cast('"+ customvalue[j-baseline.length] + "' as datetime)\"";

			}else{
				mycustom[j]=customvalue[j];
			}
		}
		mycustom[length]=mycustom[baseline.length];

		return(mycustom);
	}

	public String getCustomSqlSelect(String table,String[] basefield,String[] basealias, String[] customfield,String[] customalias) {
		int length = basefield.length + customfield.length;
		String  sql = "select ";
		if (basefield.length>0 ){
			if (basefield.length==basealias.length ){

				for (int i=0 ; i<basefield.length; i++){
					if (i==0)
						sql+=basefield[i] + " \"" + basealias[i] +"\" ";
					else
						sql+="," + basefield[i] + " \"" + basealias[i] +"\" ";
				}
			}else{

				for (int i=0 ; i<basefield.length; i++){
					if (i==0)
						sql+=basefield[i] + " \"" + basefield[i] +"\" ";
					else
						sql+="," + basefield[i] + " \"" + basefield[i] +"\" ";
				}


			}
			if (customfield.length >0 ){
				if(customfield.length==customalias.length){
					for (int i=0 ; i<customfield.length; i++)
						sql+="," + customfield[i] + " \"" + customalias[i] +"\" ";
				}else{
					for (int i=0 ; i<customfield.length; i++)
						sql+="," + customfield[i] + " \"" + customfield[i] +"\" ";

				}

			}
		}
		sql+= "  from " +table + " where ";

		return(sql);
	}

	public String getCustomSqlInsert(String table,String vendor,int primarykey,String[] basefield,String[] customfield ) {
		int length = basefield.length + customfield.length;
		String  sql = "Insert into " + table + "(";
		String values=" )values(";

		if (basefield.length>0 ){

			for (int i=0 ; i<basefield.length; i++){
				if (primarykey==ACONST.NO_PRIMARY_KEY){
					if(i==1){
						sql+=basefield[i] ;
						if(vendor.equalsIgnoreCase("Oracle") && basefield[i].equalsIgnoreCase("GenDate"))
							values+="sysdate";
						else if (vendor.equalsIgnoreCase("Mssql") && basefield[i].equalsIgnoreCase("GenDate"))
							values+="getdate()";
						else
							values+="?";

					}else if (i >1){
						sql+="," + basefield[i] ;
						if(vendor.equalsIgnoreCase("Oracle") && basefield[i].equalsIgnoreCase("GenDate"))
							values+=",sysdate";
						else if (vendor.equalsIgnoreCase("Mssql") && basefield[i].equalsIgnoreCase("GenDate"))
							values+=",getdate()";
						else
							values+= ",?";
					}
				}
				if (primarykey>ACONST.NO_PRIMARY_KEY){
					if(i==0){
						sql+=basefield[i] ;
						if(vendor.equalsIgnoreCase("Oracle") && basefield[i].equalsIgnoreCase("GenDate"))
							values+="sysdate";
						else if (vendor.equalsIgnoreCase("Mssql") && basefield[i].equalsIgnoreCase("GenDate"))
							values+="getdate()";
						else
							values+="?";
					}else{
						sql+="," + basefield[i] ;
						if(vendor.equalsIgnoreCase("Oracle") && basefield[i].equalsIgnoreCase("GenDate"))
							values+=",sysdate";
						else if (vendor.equalsIgnoreCase("Mssql") && basefield[i].equalsIgnoreCase("GenDate"))
							values+=",getdate()";
						else
							values+= ",?";
					}
				}


			}

			if (customfield.length >0 ){
				for (int i=0 ; i<customfield.length; i++){
					sql+="," + basefield[i] ;
					values+= ",?";
				}
			}
		}


		return(sql+values+")");
	}
	public String getCustomSqlUpdate(String table,String vendor,int primarykey,String[] basefield,String[] customfield ) {

		String  sql = "update " + table + "set ";
		String[] fieldarray=getCustomUpdateArray(basefield, customfield);
		if (fieldarray.length>0 ){

			for (int i=0 ; i< fieldarray.length ; i++){
				if(i==fieldarray.length-2)
					sql+= fieldarray[i] + "=? where";
				else if(i==fieldarray.length-1)
					sql+= fieldarray[i] + "=?";
				else
					sql+= fieldarray[i] + "=?,";

			}
		}
		return(sql);

	}
	//This method does not convert the date datatype
	public void makeTableSelect(String table,String criteria , String operator ,String value)

	{

		String search= "";
		String sql="select *from table_"+table +" where ";
		int state =0;

		if (criteria.equalsIgnoreCase("ObjId") && !operator.equalsIgnoreCase("All"))
			state=1;
		else 	if (operator.equalsIgnoreCase("All"))
			state=2;

		switch(state) {

		case 1:

			if (operator.equalsIgnoreCase("Like"))
				search =criteria + " Like " +"'" + value + "%" + "'" ;

			else if(operator.equalsIgnoreCase("slike"))

				search =criteria + " Like " + "'%" + value + "%" + "'" ;

			else
				search = criteria+ operator +"'"+ value +"'";

			break;

		case 2:
			search = criteria + " Like '%'" ;

			break;

		case 0:

			if (operator.equalsIgnoreCase("Like"))
				search = "upper(" + criteria +")" + " Like upper('" +  value + "%" + "')" ;

			else if(operator.equalsIgnoreCase("slike"))

				search = "upper(" + criteria +")" + " Like upper('%" +  value + "%" + "')" ;

			else
				search = "upper(" + criteria +")" +"= upper('" + value + "')" ;

			break;
		}
		setQuery(sql+search + " order by ObjId");
	}
	//This method converts the data datatype
	public void makeTableSelect(String table,String criteria , String operator ,String value,String [] column,String[] datatype)

	{
		String dbtype=DatabaseTransaction.getDbType();
		String search= "";
		String colstr="";
		for (int i=0;i<column.length;i++){
			colstr+=(i>0?",":"")+(datatype[i].equalsIgnoreCase(DataType.DATE)?tu.getConvertDate(dbtype,"select",column[i],ACONST.DEFAULT_DATE_FORMAT,""):column[i]);
		}
		String sql="select " +colstr+" from table_"+table +" where ";
		int state =0;

		if (criteria.equalsIgnoreCase("ObjId") && !operator.equalsIgnoreCase("All"))
			state=1;
		else 	if (operator.equalsIgnoreCase("All"))
			state=2;

		switch(state) {

		case 1:

			if (operator.equalsIgnoreCase("Like"))
				search =criteria + " Like " +"'" + value + "%" + "'" ;

			else if(operator.equalsIgnoreCase("slike"))

				search =criteria + " Like " + "'%" + value.trim() + "%" + "'" ;

			else
				search = criteria+ operator +"'"+ value.trim()+"'" ;

			break;

		case 2:
			search = criteria + " Like '%'" ;

			break;

		case 0:

			if (operator.equalsIgnoreCase("Like"))
				search = "upper(" + criteria +")" + " Like upper('" +  value.trim() + "%" + "')" ;

			else if(operator.equalsIgnoreCase("slike"))

				search = "upper(" + criteria +")" + " Like upper('%" +  value + "%" + "')" ;
			else
				search = "upper(" + criteria +")" +"= upper('" + value.trim() + "')" ;

			break;
		}
		//setQuery(tu.replaceStringWithPlus(sql+search + " order by name","^","'"));
		setQuery(sql+search + " order by name");
	}
	public void makeSqlSelect(String sql,String criteria , String operator ,String value,String orderfields)

	{

		String search= "";

		int state =0;

		if (criteria.equalsIgnoreCase("ObjId") && !operator.equalsIgnoreCase("All"))
			state=1;
		else 	if (operator.equalsIgnoreCase("All"))
			state=2;

		switch(state) {

		case 1:

			if (operator.equalsIgnoreCase("Like"))
				search =criteria + " Like " + "'" + value + "%" + "'" ;

			else if(operator.equalsIgnoreCase("slike"))

				search =criteria + " Like " + "'%" + value + "%" + "'" ;

			else
				search = criteria+ operator +"'"+ value+"'" ;

			break;

		case 2:
			search = criteria + " Like '%'" ;

			break;

		case 0:

			if (operator.equalsIgnoreCase("Like"))
				search = "upper(" + criteria +")" + " Like upper('" +  value + "%" + "')" ;

			else if(operator.equalsIgnoreCase("slike"))

				search = "upper(" + criteria +")" + " Like upper('%" +  value + "%" + "')" ;

			else
				search = "upper(" + criteria +")" +"= upper('" + value + "')" ;

			break;
		}
		setQuery(sql+search + " order by " +orderfields);
		//setQuery(tu.replaceStringWithPlus(sql+search + " order by " +orderfields,"^","'"));

	}
	public String[] getArrayData(String mline){
		String line="dummy"+mline;
		String testfield="";
		String tempstring="";
		String[] returnarray= new String[4000] ;
		String[] valuearray;
		BreakIterator boundary = BreakIterator.getWordInstance();
		boundary.setText(line);
		int start = boundary.first();
		int index =0;
		int count =0;

		int fieldcount=0;
		//logger.info(line);
		for (int end = boundary.next();
				end != BreakIterator.DONE;
				start = end,
						end = boundary.next()) {

			testfield=line.substring(start,end);
			if (testfield.hashCode()!=1 && testfield.hashCode()!=2 && testfield !=null )
				tempstring+=testfield;

			if (testfield.hashCode()==1 || testfield.hashCode()==2 ){
				returnarray[index]= tempstring;
				//logger.info(index + " value= " + tempstring);
				tempstring="";
				index++;

			}else{
				fieldcount++;
			}
			//logger.info(count + " value= " + testfield+ "Hashcode=" + testfield.hashCode());
			count++;
		}
		returnarray[index++]= tempstring;
		valuearray= new String[index];
		for(int j=0; j<index; j++){

			if(j==0){
				valuearray[j]=(returnarray[j].substring(5).equalsIgnoreCase("null")? " " :returnarray[j].substring(5));;
			}else{
				valuearray[j]=(returnarray[j].equalsIgnoreCase("null")? " " :returnarray[j]);
			}
		}
		//logger.info("arrayIndex=" + 	valuearray.length);

		return(valuearray);
	}
	//This method create the sql for child tables
	public  String  makeChildSql(String parent,String childname,String relfield,String pid,String[]column,String[]datatype){
		String dbtype=DatabaseTransaction.getDbType();
		String colstr="";
		String mtmtable="";
		String mtmfilter="";
		int mtmlen=relfield.indexOf("@");
		mtmtable=mtmlen>0?","+relfield.substring(0,mtmlen)+" m ":"";
		mtmfilter=mtmlen>0?"o.objid=m."+parent+"id and m."+childname+"id=c.objid":"c."+relfield+"= o.ObjId";
		for (int i=0;i<column.length;i++){
			colstr+=(datatype[i].equalsIgnoreCase(DataType.DATE)?(i>0?",":"")+tu.getConvertDate(dbtype,"select","c."+column[i],ACONST.DEFAULT_DATE_FORMAT,""):(i>0?",c.":"c.")+column[i]);
		}
		//colstr+=",o.ObjId pObjId from";
		colstr+=" from";
		String sql= "select "+colstr+" table_" + childname + " c,table_"+parent+" o "+mtmtable+" where "+mtmfilter+" and o.ObjId='"+pid+"'" ;
		return(sql);
	}
	public String getReportSummarySQL(String reportname){
		String sql="select vo.ObjectName,vo.AliasName \"ReportName\",va.Attribute,"+
				"va.AliasName,va.MsSqlConversion,va.OracleConversion,va.AttributeIndex,"+
				"va.OrderIndex,va.IsSummary,va.HasConversion,va.IsFilter "+
				" from sml_viewobject vo,sml_viewattribute va where "+
				" vo.objid=va.ViewAttribute2View and  "+
				" va.ViewAttribute2AttributeDomain =0 and "+
				" va.isSummary='yes' and upper(vo.ObjectName)=upper('"+reportname+"')";
		return(sql);
	}
	/*********************************************************************************
	 * This method generates the SQL String for reports
	 *
	 ************************************************************************************/
	public String makeGenericReportSQL(String reportname , String[] filter ){
		String colstr="";
		String orderby="";
		String groupby="";
		String joinstr="";
		String tablestr="";
		String dbtype=DatabaseTransaction.getDbType();

		String attrsql="select vo.ObjectName,vo.AliasName \"ReportName\",vo.HasGroup,va.Attribute,"+
				"va.AliasName,va.MsSqlConversion,va.OracleConversion,va.AttributeIndex,"+
				"va.OrderIndex,va.IsSummary,va.HasConversion,va.IsFilter,ad.OracleDataType "+
				"from sml_viewobject vo,sml_viewattribute va,sml_attributedomain ad "+
				"where vo.objid=va.ViewAttribute2View and ad.objid=va.ViewAttribute2AttributeDomain and "+
				"upper(vo.ObjectName)=upper('"+reportname+"')";
		TemplateTable vattr=tu.getResultSet(attrsql);
		if(vattr.getRowCount()>0){
			for(int i=0; i<vattr.getRowCount(); i++){
				String column=vattr.getFieldValue("OracleDataType",i).indexOf("Date")>0?tu.getConvertDate(dbtype,"select",vattr.getFieldValue("Attribute",i),"mm/dd/yyyy",vattr.getFieldValue("Attribute",i))
						:(vattr.getFieldValue("HasConversion",i).equalsIgnoreCase("yes")?(dbtype.equalsIgnoreCase("mssql")?vattr.getFieldValue("MsSqlConversion",i)
								:vattr.getFieldValue("OracleConversion",i)):vattr.getFieldValue("Attribute",i));
				colstr+=colstr.equals("")?(tu.replaceStringWith(column,"sq;","'")+" \""+vattr.getFieldValue("AliasName",i)+"\""):(tu.replaceStringWith(column,"sq;","'")+" \""+vattr.getFieldValue("AliasName",i)+"\",");
			}

		}

		String joinsql="select vo.ObjectName,vo.AliasName \"ReportName\",vr.ParentObject,"+
				"vr.ChildObject,vr.OracleRelation,vr.MsSqlRelation,vr.JoinType "+
				"from sml_viewrelation vr,sml_viewobject vo "+
				"where vr.ViewRelation2View=vo.Objid and "+
				"upper(vo.ObjectName)=upper('"+reportname+"')";

		TemplateTable join=tu.getResultSet(joinsql);
		if(join.getRowCount()>0){
			for(int i=0; i<join.getRowCount(); i++){
				String jstr=(dbtype.equalsIgnoreCase("mssql")?join.getFieldValue("MsSqlRelation",i):join.getFieldValue("OracleRelation",i));
				joinstr+=joinstr.equals("")?tu.replaceStringWith(jstr,"sq;","'"):"and "+tu.replaceStringWith(jstr,"sq;","'");
			}

		}
		String tablesql="select distinct do.ObjectName from sml_dbobject do,sml_viewattribute va,sml_viewobject vo "+
				" where do.objid=va.ViewAttribute2DbObject and va.ViewAttribute2View=vo.objid and "+
				"upper(vo.ObjectName)=upper('"+reportname+"') and Upper(va.IsSummary)=upper('no')";
		TemplateTable table=tu.getResultSet(joinsql);
		if(table.getRowCount()>0){
			for(int i=0; i<table.getRowCount(); i++){
				String tab="Table_"+table.getFieldValue("ObjectName",i);
				tablestr+=tablestr.equals("")?tab:","+tab;
			}

		}

		String rsql="select " +colstr+" from "+tablestr +" where "+joinstr +" ";
		return(rsql);
	}
	/*********************************************************************************
	 * This method generates the SQL String for reports
	 *
	 ************************************************************************************/
	public void makeReportSQL(String reportname ,String  dbtype, String[] filter ){
		String colstr="";
		String orderby="";
		String groupby="";
		String joinstr="";
		String tablestr="";

		String attrsql="select vo.ObjectName,vo.AliasName \"ReportName\",vo.HasGroup,va.Attribute,"+
				"va.AliasName,va.MsSqlConversion,va.OracleConversion,va.AttributeIndex,"+
				"va.OrderIndex,va.IsSummary,va.HasConversion,va.IsFilter,ad.OracleDataType "+
				"from sml_viewobject vo,sml_viewattribute va,sml_attributedomain ad "+
				"where vo.objid=va.ViewAttribute2View and ad.objid=va.ViewAttribute2AttributeDomain and "+
				"upper(vo.ObjectName)=upper('"+reportname+"') order by va.OrderIndex ";
		TemplateTable vattr=tu.getResultSet(attrsql);
		if(vattr.getRowCount()>0){
			setReportHeader(vattr.getFieldValue("ReportName",0));
			for(int i=0; i<vattr.getRowCount(); i++){
				String column=vattr.getFieldValue("OracleDataType",i).indexOf("Date")>0?tu.getConvertDate(dbtype,"select",vattr.getFieldValue("Attribute",i),"mm/dd/yyyy",vattr.getFieldValue("Attribute",i))
						:(vattr.getFieldValue("HasConversion",i).equalsIgnoreCase("yes")?(dbtype.equalsIgnoreCase("mssql")?vattr.getFieldValue("MsSqlConversion",i)
								:vattr.getFieldValue("OracleConversion",i)):vattr.getFieldValue("Attribute",i));
				colstr+=colstr.equals("")?(tu.replaceStringWith(column,"sq;","'")+" \""+vattr.getFieldValue("AliasName",i)+"\""):(tu.replaceStringWith(column,"sq;","'")+" \""+vattr.getFieldValue("AliasName",i)+"\",");
			}

		}

		String joinsql="select vo.ObjectName,vo.AliasName \"ReportName\",vr.ParentObject,"+
				"vr.ChildObject,vr.OracleRelation,vr.MsSqlRelation,vr.JoinType "+
				"from sml_viewrelation vr,sml_viewobject vo "+
				"where vr.ViewRelation2View=vo.Objid and "+
				"upper(vo.ObjectName)=upper('"+reportname+"')";

		TemplateTable join=tu.getResultSet(joinsql);
		if(join.getRowCount()>0){
			for(int i=0; i<join.getRowCount(); i++){
				String jstr=(dbtype.equalsIgnoreCase("mssql")?join.getFieldValue("MsSqlRelation",i):join.getFieldValue("OracleRelation",i));
				joinstr+=joinstr.equals("")?tu.replaceStringWith(jstr,"sq;","'"):"and "+tu.replaceStringWith(jstr,"sq;","'");
			}

		}
		String tablesql="select distinct do.ObjectName from sml_dbobject do,sml_viewattribute va,sml_viewobject vo "+
				" where do.objid=va.ViewAttribute2DbObject and va.ViewAttribute2View=vo.objid and "+
				"upper(vo.ObjectName)=upper('"+reportname+"') and Upper(va.IsSummary)=upper('no')";
		TemplateTable table=tu.getResultSet(joinsql);
		if(table.getRowCount()>0){
			for(int i=0; i<table.getRowCount(); i++){
				String tab="Table_"+table.getFieldValue("ObjectName",i);
				tablestr+=tablestr.equals("")?tab:","+tab;
			}

		}
		String ordersql="select va1.attribute,va1.AliasName,va2.OrderIndex,va2.AliasName,va2.MssqlConversion,va2.OracleConversion "+
				" from sml_viewattribute va1,sml_viewattribute va2,sml_viewobject vo "+
				" where va1.orderindex=va2.orderindex and va2.ViewAttribute2AttributeDomain=0 and va1.ViewAttribute2View=va2.ViewAttribute2View and "+
				" va2.ViewAttribute2View=vo.objid and vo.objectname='CashReport' and va1.issummary='no' order by va2.OrderIndex ";
		TemplateTable order=tu.getResultSet(ordersql);
		if(order.getRowCount()>0){
			for(int i=0; i<order.getRowCount(); i++){
				String ord=order.getFieldValue("attribute",i);
				orderby+=tablestr.equals("")?"order by "+ ord:","+ord;
				setSummaryField(dbtype.equalsIgnoreCase("mssql")?order.getFieldValue("MssqlConversion",i):order.getFieldValue("OracleConversion",i));
			}

		}

		String sql="select " +colstr+" from "+tablestr +" where "+joinstr +" ";
		//String sql= "select * from table_" + table +" where " ;
		String criteria="";
		String operator="";
		String value="";
		String search= "";
		int state ;
		int k=filter.length-2;
		for ( int i=0; i<k; ){
			state=0;
			criteria=filter[i];
			operator=filter[i+1];
			if (!search.equals(""))
				search+=" and ";

			boolean verifydate=filter[i].toUpperCase().indexOf("DATE")>0&&tu.isDate(filter[i+2]);
			if(verifydate){
				criteria=tu.getVarchar2DateTime(dbtype,true,filter[i],"mm/dd/yyyy",null);
				value=tu.getVarchar2DateTime(dbtype,true,filter[i],"mm/dd/yyyy",filter[i+2]);
				state=3;
			}else{

				try {
					int j= Integer.parseInt(filter[i+2]) ;
					value= filter[i+2];
					if (!operator.equalsIgnoreCase("All"))
						state=1;


				}catch(NumberFormatException ne){
					if (filter[i+2].equalsIgnoreCase("null") )
						value= "null";
					else if( !filter[i+2].equals(""))
						value= filter[i+2];
					else
						value="' '";
				}

			}
			i+=3 ;
			if (operator.equalsIgnoreCase("All"))
				state=2;
			//logger.info("state=" +state);
			switch(state) {

			case 1:

				if (operator.equalsIgnoreCase("Like"))
					search +=criteria + " Like " +"'" + value + "%" + "'" ;
				else
					search += criteria+ operator + value ;

				break;

			case 2:
				search += criteria + " Like '%'" ;

				break;

			case 3:

				if (operator.equalsIgnoreCase("Like")||operator.equalsIgnoreCase("sound Like")||operator.equalsIgnoreCase("all"))
					search =  criteria + "=" +  value ;
				else
					search += criteria+ operator + value ;

				break;


			case 0:

				if (operator.equalsIgnoreCase("Like"))
					search += "upper(" + criteria +")" + " Like upper('" +  value + "%" + "')" ;
				else
					search += "upper(" + criteria +")" +"= upper('" + value + "')" ;

				break;
			}
		}
		setQuery(sql+search+orderby);

	}
	// This method initialize all privilege group objects
	// Add additional relation fileds if more groups are needed


	public TemplateTable getPrivilegeFilter(String dbobject){
		TemplateTable result=new TemplateTable();
		try{
			result=(TemplateTable)priv.get(dbobject);
		}catch (Exception e){

		} 
		if(result==null){
			String sql="select op.name TableName ,op.Value,op.Type,op.IsRecursive,pg.name groupname,"+
					" pg.objid groupid,tu.loginname,tu.user2supplier,tu.user2company,tu.user2privilegegroup from table_user tu, table_privilegegroup pg,table_objectprivilege op "+
					" where tu.User2PrivilegeGroup=pg.objid and pg.objid=op.Objectprivilege2privilegegroup "+
					" and tu.loginname='"+getUserName()+"' and upper(op.name)=upper('"+dbobject+"')";
			result=tu.getResultSet(sql);
			priv.put(dbobject, result);
		}
		return(result);
	}
	//this method converts date to mm/dd/yyyy format
	//This method verify the object privilege based on different group
	//Additional group can be added later like team, manager etc based on the above steps
	public void makeSQL(String table , String[] filter ,String [] column, String[] datatype){
		String dbtype=DatabaseTransaction.getDbType();
		String colstr="";
		String prefix=table.toLowerCase();
		for (int i=0;i<column.length;i++){
			colstr+=(i>0?",":"")+(datatype[i].equalsIgnoreCase(DataType.DATE)?tu.getConvertDate(dbtype,"select",prefix+"."+column[i],ACONST.DEFAULT_DATE_FORMAT,""):prefix+"."+column[i]);
		}
		String sql="select " +colstr+" from table_"+table +" "+prefix;
	
		//String sql= "select * from table_" + table +" where " ;
		String criteria="";
		String operator="";
		String value="";
		String search= "";
		int state ;
		int k=filter.length-2;
		for ( int i=0; i<k; ){
			state=0;
			criteria=filter[i].indexOf(".")>0?filter[i]: prefix+"."+filter[i];
			operator=filter[i+1];
			if (!search.equals(""))
				search+=" and ";
			boolean verifydate=filter[i].toUpperCase().indexOf("DATE")>0&&tu.isDate(filter[i+2]);
			if(verifydate){
				criteria=tu.getVarchar2DateTime(dbtype,true,criteria,"mm/dd/yyyy",null);
				value=tu.getVarchar2DateTime(dbtype,true,criteria,"mm/dd/yyyy",filter[i+2]);
				state=3;
			}else{

				try {
					int j= Integer.parseInt(filter[i+2]) ;
					value= filter[i+2].trim();
					if (!operator.equalsIgnoreCase("All"))
						state=1;


				}catch(NumberFormatException ne){
					if (filter[i+2].equalsIgnoreCase("null") )
						value= "null";
					else if( !filter[i+2].equals(""))
						value= filter[i+2].trim();
					else
						value="' '";
				}

			}
			i+=3 ;
			if (operator.equalsIgnoreCase("All"))
				state=2;
			//logger.info("state=" +state);
			switch(state) {

			case 1:

				if (operator.equalsIgnoreCase("Like"))
					search +=criteria + " Like " +"'" + value + "%" + "'" ;

				else if (operator.equalsIgnoreCase("sLike"))

					search +=criteria + " Like " +"'%" + value + "%" + "'" ;
				else
					search += criteria+ operator + value ;

				break;

			case 2:
				search += criteria + " Like '%'" ;

				break;

			case 3:

				if (operator.equalsIgnoreCase("Like")||operator.equalsIgnoreCase("sound Like")||operator.equalsIgnoreCase("all"))
					search =  criteria + "=" +  value ;
				else
					search += criteria+ operator + value ;

				break;


			case 0:

				if (operator.equalsIgnoreCase("Like"))
					search += "upper(" + criteria +")" + " Like upper('" +  value + "%" + "')" ;

				else if (operator.equalsIgnoreCase("sLike"))
					search += "upper(" + criteria +")" + " Like upper('%" +  value + "%" + "')" ;

				else
					search += "upper(" + criteria +")" +"= upper('" + value + "')" ;

				break;
			}
		}
		TemplateTable result=getPrivilegeFilter(table);
		String relationtable="";
		String privfilter="";
		if(result!=null &&result.getRowCount()>0)
			for(int i=0; i<result.getRowCount();i++){
				//if priviligetype=record i.e=2
				if(result.getFieldValue("type",i).equalsIgnoreCase("2")){
					privfilter=prefix+".genuser='"+getUserName()+"' or "+prefix+".moduser='"+getUserName()+"' and ";
					break;
					//If Privilege Group=Privilege Group
				}else if(result.getFieldValue("type",i).equalsIgnoreCase("3")){
					privfilter=prefix+".genuser in (select loginname from table_user where user2privilegegroup="+
							result.getFieldValue("groupid",i)+") or "+prefix+
							".moduser in (select loginname from table_user where user2privilegegroup="+
							result.getFieldValue("groupid",i)+") and ";
					break;
					//If Privilege Group=Vendor Group
				}else if(result.getFieldValue("type",i).equalsIgnoreCase("4")){

					privfilter=prefix+".genuser in (select loginname from table_user where user2vendor="+
							result.getFieldValue("user2vendor",i)+") or "+prefix+
							".moduser in (select loginname from table_user where user2vendor="+
							result.getFieldValue("user2vendor",i)+") and ";
					break;
					//If Privilege Group=Entity Group
				}else if(result.getFieldValue("type",i).equalsIgnoreCase("5")){

					privfilter=prefix+".genuser in (select loginname from table_user where user2company="+
							result.getFieldValue("user2company",i)+") or "+prefix+
							".moduser in (select loginname from table_user where user2company="+
							result.getFieldValue("user2company",i)+") and ";
					break;
				}
				//Additional group can be added later like team, manager etc based on the above steps
			}
		setQuery(sql+" where "+privfilter+ search+" order by "+prefix+".objid");
		//setQuery(tu.replaceStringWithPlus(sql+" where "+privfilter+ search+" order by "+prefix+".objid","^","'")); 

	}

	//This method create the sql for child tables
	public  String  makeChildObjectFilterSql(String parent,String childname,String relfield,String pid,String[]column,String[]datatype,String objectfilter){
		String dbtype=DatabaseTransaction.getDbType();
		String colstr="";
		String mtmtable="";
		String mtmfilter="";
		String parentobject=parent.toLowerCase();
		String childobject=childname.toLowerCase();
		String filterstr[]=tu.getString2TokenArray(objectfilter,"@");
		String tablelist[]=tu.getString2TokenArray(filterstr[1],",");
		String viewfldlist[]=tu.getString2TokenArray(filterstr[0],",");          
		String viewfldstr="";
		if(viewfldlist!=null &&viewfldlist.length>0)
			for(int k=0;k<viewfldlist.length;k++){
				for(int m=0;m<column.length;m++){
					if (viewfldlist[k].indexOf(column[m])>=0){

						break;
					}
				}
			}
		//logger.info("Calling makeChildObjectFilterSql-2" );
		String filtertable="";
		boolean ischild=false;
		boolean isparent=false;
		if (tablelist.length>0){
			for(int j=0;j<tablelist.length;j++){
				if(!ischild &&tablelist[j].toLowerCase().equalsIgnoreCase(childname))
					ischild=true;
				if(!isparent &&tablelist[j].toLowerCase().equalsIgnoreCase(parent))
					isparent=true;
				filtertable+=j>0?",table_"+tablelist[j].toLowerCase()+" "+tablelist[j].toLowerCase():"table_"+tablelist[j].toLowerCase()+" "+tablelist[j].toLowerCase();
				//logger.info("filtertable="+filtertable);
			}
		}
		//filtertable=filtertable.equals("")?"table_"+parent.toLowerCase():(filtertable+",table_"+parent.toLowerCase()+" " +parent.toLowerCase());
		filtertable=!filtertable.equals("")&&isparent?filtertable:(filtertable+",table_"+parent.toLowerCase()+" " +parent.toLowerCase());
		//logger.info("filtertable 11="+filtertable);
		filtertable=!filtertable.equals("")&&ischild?filtertable:(filtertable+",table_"+childname.toLowerCase()+" "+childname.toLowerCase());

		//logger.info("filtertable 21="+filtertable);
		int mtmlen=relfield.indexOf("@");
		mtmtable=mtmlen>0?","+relfield.substring(0,mtmlen)+" m ":"";
		mtmfilter=mtmlen>0?parentobject+".objid=m."+parentobject+"id and m."+childobject+"id="+childobject+".objid":childobject+"."+relfield+"="+parentobject+".ObjId";
		for (int i=0;i<column.length;i++){
			String field=childobject.toLowerCase()+"."+column[i];
			if(viewfldlist!=null &&viewfldlist.length>0)
				for(int k=0;k<viewfldlist.length;k++){

					if (tu.getString2TokenArray(viewfldlist[k].toLowerCase()," ")[1].equals(column[i].toLowerCase())){
						field=viewfldlist[k];
						break;
					}
				}
			colstr+=(datatype[i].equalsIgnoreCase(DataType.DATE)?(i>0?",":"")+tu.getConvertDate(dbtype,"select",field,ACONST.DEFAULT_DATE_FORMAT,""):(i>0?","+field:field));
		}

		String sql= "select distinct "+colstr+" from "+filtertable+mtmtable+" where "+mtmfilter+" and "+parent.toLowerCase()+".ObjId='"+pid+"' and "+ filterstr[2] ;
		//logger.info(" makeChildObjectFilterSql="+sql);
		//return(tu.replaceStringWithPlus(sql,"^","'"));
		return(sql);
	}
	//This method converts the data datatype
	public void makeTableSelectObjectFilter(String table,String criteria , String operator ,String value,String [] column,String[] datatype,String objectfilter)

	{
		//logger.info("Calling makeTableSelectObjectFilter" );
		String dbtype=DatabaseTransaction.getDbType();
		String search= "";
		String colstr="";
		String filterstr[]=tu.getString2TokenArray(objectfilter,"@");
		String tablelist[]=tu.getString2TokenArray(filterstr[1],",");
		String viewfldlist[]=tu.getString2TokenArray(filterstr[0],",");
		String viewfldstr="";
		String filtertable="";
		boolean istable=false;
		if (tablelist.length>0){
			for(int j=0;j<tablelist.length;j++){
				if(!istable &&tablelist[j].toLowerCase().equalsIgnoreCase(table))
					istable=true;
				filtertable+=j>0?",table_"+tablelist[j].toLowerCase()+" "+tablelist[j].toLowerCase():"table_"+tablelist[j].toLowerCase()+" "+tablelist[j].toLowerCase();
			}
		}
		for (int i=0;i<column.length;i++){
			String field=table.toLowerCase()+"."+column[i];
			if(viewfldlist!=null &&viewfldlist.length>0)
				for(int k=0;k<viewfldlist.length;k++){
					if (tu.getString2TokenArray(viewfldlist[k].toLowerCase()," ")[1].equals(column[i].toLowerCase())){
						field=viewfldlist[k];
						break;
					}
				}
			colstr+=(datatype[i].equalsIgnoreCase(DataType.DATE)?(i>0?",":"")+tu.getConvertDate(dbtype,"select",field,ACONST.DEFAULT_DATE_FORMAT,""):(i>0?","+field:field));
		}

		filtertable=(!filtertable.equals("")&&istable?filtertable:filtertable+", table_"+table.toLowerCase()+" "+ table.toLowerCase());

		String sql="select distinct " +colstr+" from "+filtertable +" where ";

		criteria=criteria.indexOf(".")>0?criteria: table.toLowerCase()+"."+criteria;
		//criteria=table.toLowerCase()+"."+criteria;

		int state =0;

		if (criteria.equalsIgnoreCase("ObjId") && !operator.equalsIgnoreCase("All"))
			state=1;
		else 	if (operator.equalsIgnoreCase("All"))
			state=2;

		switch(state) {

		case 1:

			if (operator.equalsIgnoreCase("Like"))
				search =criteria + " Like " +"'" + value.trim() + "%" + "'" ;

			else if(operator.equalsIgnoreCase("slike"))

				search =criteria + " Like " + "'%" + value.trim() + "%" + "'" ;

			else
				search = criteria+ operator + value.trim() ;

			break;

		case 2:
			search = criteria + " Like '%'" ;

			break;

		case 0:

			if (operator.equalsIgnoreCase("Like"))
				search = "upper(" + criteria +")" + " Like upper('" +  value.trim() + "%" + "')" ;

			else if(operator.equalsIgnoreCase("slike"))

				search = "upper(" + criteria +")" + " Like upper('%" +  value + "%" + "')" ;
			else
				search = "upper(" + criteria +")" +"= upper('" + value.trim() + "')" ;

			break;
		}
		//setQuery(tu.replaceStringWithPlus(sql+search +" and "+filterstr[2],"^","'"));
		setQuery(sql+search +" and "+filterstr[2] );

	}
	//this method converts date to mm/dd/yyyy format
	//This method verify the object privilege based on different group
	//Additional group can be added later like team, manager etc based on the above steps
	public void makeObjectFilterSQL(String table , String[] filter ,String [] column, String[] datatype,String objectfilter){
		String dbtype=DatabaseTransaction.getDbType();
		String colstr="";
		String prefix=table.toLowerCase();
		String filterstr[]=tu.getString2TokenArray(objectfilter,"@");
		String tablelist[]=tu.getString2TokenArray(filterstr[1],",");
		String filtertable="";
		String viewfldlist[]=tu.getString2TokenArray(filterstr[0],",");
		String viewfldstr="";
		//logger.info("Calling makeObjectFilterSQL" );
		boolean istable=false;
		if (tablelist.length>0){
			for(int j=0;j<tablelist.length;j++){
				//logger.info("Table="+tablelist[j] );
				if(!istable &&tablelist[j].toLowerCase().equalsIgnoreCase(table))
					istable=true;
				filtertable+=j>0?",table_"+tablelist[j].toLowerCase()+" "+tablelist[j].toLowerCase():"table_"+tablelist[j].toLowerCase()+" "+tablelist[j].toLowerCase();
			}
		}

		//for (int i=0;i<column.length;i++){
		//  colstr+=filterstr[0].indexOf(column[i])>=0?"":table.toLowerCase()+"."+(i>0?",":"")+(datatype[i].equalsIgnoreCase(DataType.DATE)?tu.getConvertDate(dbtype,"select",column[i],ACONST.DEFAULT_DATE_FORMAT,""):column[i]);
		//}
		for (int i=0;i<column.length;i++){
			String field=table.toLowerCase()+"."+column[i];
			if(viewfldlist!=null &&viewfldlist.length>0)
				for(int k=0;k<viewfldlist.length;k++){

					//logger.info(tu.getString2TokenArray(viewfldlist[k].toLowerCase()," ")[1]+" compare="+column[i].toLowerCase());

					if (tu.getString2TokenArray(viewfldlist[k].toLowerCase()," ")[1].equals(column[i].toLowerCase())){
						field=viewfldlist[k];
						break;
					}
				}
			colstr+=(datatype[i].equalsIgnoreCase(DataType.DATE)?(i>0?",":"")+tu.getConvertDate(dbtype,"select",field,ACONST.DEFAULT_DATE_FORMAT,""):(i>0?","+field:field));
		}
		filtertable=(!filtertable.equals("")&&istable?filtertable:filtertable+", table_"+table.toLowerCase()+" "+ table.toLowerCase());

		String sql="select distinct " +colstr+" from "+filtertable +" where ";



		//String sql= "select * from table_" + table +" where " ;
		String criteria="";
		String operator="";
		String value="";
		String search= "";
		int state ;
		int k=filter.length-2;
		for ( int i=0; i<k; ){
			state=0;
			criteria=filter[i].indexOf(".")>0?filter[i]: prefix+"."+filter[i];
			operator=filter[i+1];
			if (!search.equals(""))
				search+=" and ";
			boolean verifydate=filter[i].toUpperCase().indexOf("DATE")>0&&tu.isDate(filter[i+2]);
			if(verifydate){
				criteria=tu.getVarchar2DateTime(dbtype,true,criteria,"mm/dd/yyyy",null);
				value=tu.getVarchar2DateTime(dbtype,true,criteria,"mm/dd/yyyy",filter[i+2]);
				state=3;
			}else{

				try {
					int j= Integer.parseInt(filter[i+2]) ;
					value= filter[i+2].trim();
					if (!operator.equalsIgnoreCase("All"))
						state=1;


				}catch(NumberFormatException ne){
					if (filter[i+2].equalsIgnoreCase("null") )
						value= "null";
					else if( !filter[i+2].equals(""))
						value= filter[i+2].trim();
					else
						value="' '";
				}

			}
			i+=3 ;
			if (operator.equalsIgnoreCase("All"))
				state=2;
			//logger.info("state=" +state);
			switch(state) {

			case 1:

				if (operator.equalsIgnoreCase("Like"))
					search +=criteria + " Like " +"'" + value + "%" + "'" ;

				if (operator.equalsIgnoreCase("sLike"))

					search +=criteria + " Like " +"'%" + value + "%" + "'" ;
				else
					search += criteria+ operator + value ;

				break;

			case 2:
				search += criteria + " Like '%'" ;

				break;

			case 3:

				if (operator.equalsIgnoreCase("Like")||operator.equalsIgnoreCase("sound Like")||operator.equalsIgnoreCase("all"))
					search =  criteria + "=" +  value ;
				else
					search += criteria+ operator + value ;

				break;


			case 0:

				if (operator.equalsIgnoreCase("Like"))
					search += "upper(" + criteria +")" + " Like upper('" +  value + "%" + "')" ;

				else if (operator.equalsIgnoreCase("sLike"))
					search += "upper(" + criteria +")" + " Like upper('%" +  value + "%" + "')" ;

				else
					search += "upper(" + criteria +")" +"= upper('" + value + "')" ;

				break;
			}
		}
		TemplateTable result=getPrivilegeFilter(table);
		String relationtable="";
		String privfilter="";
		if(result!=null &&result.getRowCount()>0)
			for(int i=0; i<result.getRowCount();i++){
				//if priviligetype=record i.e=2
				if(result.getFieldValue("type",i).equalsIgnoreCase("2")){
					privfilter=prefix+".genuser='"+getUserName()+"' or "+prefix+".moduser='"+getUserName()+"' and ";
					break;
					//If Privilege Group=Privilege Group
				}else if(result.getFieldValue("type",i).equalsIgnoreCase("3")){
					privfilter=prefix+".genuser in (select loginname from table_user where user2privilegegroup="+
							result.getFieldValue("groupid",i)+") or "+prefix+
							".moduser in (select loginname from table_user where user2privilegegroup="+
							result.getFieldValue("groupid",i)+") and ";
					break;
					//If Privilege Group=Vendor Group
				}else if(result.getFieldValue("type",i).equalsIgnoreCase("4")){

					privfilter=prefix+".genuser in (select loginname from table_user where user2vendor="+
							result.getFieldValue("user2vendor",i)+") or "+prefix+
							".moduser in (select loginname from table_user where user2vendor="+
							result.getFieldValue("user2vendor",i)+") and ";
					break;
					//If Privilege Group=Entity Group
				}else if(result.getFieldValue("type",i).equalsIgnoreCase("5")){

					privfilter=prefix+".genuser in (select loginname from table_user where user2company="+
							result.getFieldValue("user2company",i)+") or "+prefix+
							".moduser in (select loginname from table_user where user2company="+
							result.getFieldValue("user2company",i)+") and ";
					break;
				}
				//Additional group can be added later like team, manager etc based on the above steps
			}
		setQuery(sql+privfilter+ search+" and "+filterstr[2]);
		//setQuery(tu.replaceStringWithPlus(sql+privfilter+ search+" and "+filterstr[2],"^","'"));

	}
	// Obsolate, this method does not convert data
	public void makeSQL(String table ,String[] filter ){
		String dbtype=DatabaseTransaction.getDbType();
		String sql= "select * from table_" + table +" where " ;
		String criteria="";
		String operator="";
		String value="";
		String search= "";
		int state ;
		int k=filter.length-2;
		for ( int i=0; i<k; ){
			state=0;
			criteria=filter[i];
			operator=filter[i+1];
			if (!search.equals(""))
				search+=" and ";
			boolean verifydate=filter[i].toUpperCase().indexOf("DATE")>0&& tu.isDate(filter[i+2]);
			if(verifydate){
				criteria=tu.getVarchar2DateTime(dbtype,true,filter[i],"mm/dd/yyyy",null);
				value=tu.getVarchar2DateTime(dbtype,true,filter[i],"mm/dd/yyyy",filter[i+2]);
				state=3;
			}else{

				try {
					int j= Integer.parseInt(filter[i+2]) ;
					value= filter[i+2];
					if (!operator.equalsIgnoreCase("All"))
						state=1;


				}catch(NumberFormatException ne){
					if (filter[i+2].equalsIgnoreCase("null") )
						value= "null";
					else if( !filter[i+2].equals(""))
						value= filter[i+2];
					else
						value="' '";
				}

			}
			i+=3 ;
			if (operator.equalsIgnoreCase("All"))
				state=2;
			//logger.info("state=" +state);
			switch(state) {

			case 1:

				if (operator.equalsIgnoreCase("Like"))
					search +=criteria + " Like " +"'" + value + "%" + "'" ;

				else if (operator.equalsIgnoreCase("sLike"))

					search +=criteria + " Like " +"'%" + value + "%" + "'" ;

				else
					search += criteria+ operator + value ;

				break;

			case 2:
				search += criteria + " Like '%'" ;

				break;

			case 3:

				if (operator.equalsIgnoreCase("Like")||operator.equalsIgnoreCase("sound Like")||operator.equalsIgnoreCase("all"))
					search =  criteria + "=" +  value ;
				else
					search += criteria+ operator + value ;

				break;


			case 0:

				if (operator.equalsIgnoreCase("Like"))
					search += "upper(" + criteria +")" + " Like upper('" +  value + "%" + "')" ;

				else if(operator.equalsIgnoreCase("slike"))

					search = "upper(" + criteria +")" + " Like upper('%" +  value + "%" + "')" ;
				else
					search += "upper(" + criteria +")" +"= upper('" + value + "')" ;

				break;
			}
		}
		//setQuery(tu.replaceStringWithPlus(sql+search,"^","'"));     
		setQuery(sql+search);

	}
	/**
	 * This methods returns the array of data which are only modified or new recordset
	 */
	public String[] getNewModifyData(String[]alldata,String[]dirtyidlist,String[]fields){
		int objpos=tu.getArrayFieldIndex("objid",fields);
		int count=0;
		int idcount=0;
		int rowcount=0;
		int row=alldata.length/fields.length;
		String [][]tmpdata=new String[row][fields.length];
		String [] marraydata;
		String [] resultarray;
		String [] unchangedid=new String[row];
		//logger.info("alldata length="+alldata.length +" field length="+fields.length );
		for(int i=0;i<row*fields.length;i=fields.length*rowcount){
			rowcount++;
			marraydata=new String[fields.length];
			for(int j=0;j<fields.length;j++){
				marraydata[j]=alldata[i+j];
			}
			//logger.info("objid="+(marraydata[objpos]+"dummy").equals("dummy")?"null":marraydata[objpos]+"dummy" +" index="+i);

			if(tu.getArrayFieldIndex(marraydata[objpos],dirtyidlist)>=0||marraydata[objpos]==null||marraydata[objpos].equals("")||row==1){
				//logger.info("row count="+count);
				tmpdata[count]=marraydata;
				count++;
			}
			if(marraydata[objpos]!=null&&tu.getArrayFieldIndex(marraydata[objpos],dirtyidlist)==-1){
				//logger.info("verifying unchanged  id");
				//logger.info("unchanged id="+marraydata[objpos]);
				unchangedid[idcount]=marraydata[objpos];
				idcount++;
			}

		}
		//logger.info("row count="+count);
		setUnchangedObjId(unchangedid);
		resultarray=new String[count*fields.length];
		//logger.info("tmpdata data2="+resultarray.length+" count="+count);
		int index=0;
		//for(int k=0;k<((count-1)>0?(count-1):(count-1)==0?1:0);k++){
		for(int k=0;k<count;k++){

			String [] eachrow=tmpdata[k];
			//logger.info("Result Row="+k+" field length="+fields.length);
			for(int col=0;col<fields.length;col++){
				resultarray[index]=eachrow[col];
				index++;
				//logger.info("Column data="+eachrow[col]);
			}
		}
		return(resultarray);
	}
	public  String makeBulkRemoveSQL(String tablename, String relfield,String removeid,String username){

		String []removeidlist;
		String sql="";
		
		if(removeid!=null &&!removeid.equals("")){
			logger.info(">>>delete id="+removeid);
			removeidlist=tu.getString2TokenArray(removeid,",") ;

			if(removeidlist!=null&&removeidlist.length>0)
				for (int i=0; i<removeidlist.length; i++)
					sql+=(i>0?",":"")+removeidlist[i];
			if(!sql.equals(""))
				sql="\n\t\t\t begin \n\t\t\t\t update table_"+tablename+ " set "+ relfield+"='' ,ModUser='"+username+"' where objid in(" +sql+"); \n\t\t\t end;";
		}
		return(sql);
	}
	public  String makeBulkSQL(boolean isparent,String xml,
			String relfield,String username,String groupuser){
		String dbtype=DatabaseTransaction.getDbType();
		String sql=tx.makeBulkSQL(dbtype,isparent, xml, relfield, username,groupuser);
		setObjId(tx.getParentObjId());
		setTableData(tx.getTableData());
		return(sql);
	}
	/* User should supply the follwing values for each form
	 ** dbtype= "Oracle" or "Mssql"
	 ** table = object name without Table_
	 ** isparent= whether the table is parent table in current screen object? true or false, if false relation value=null
	 ** fields = list of all database fields name same as table fields
	 ** datatype = database specific datatype for each fields
	 ** data = all the data for each form
	 ** querymode = insert,update,delete,modify
	 ** in modify mode the data will be updated which has objid!=null and inserted for the records which has objid=null
	 ** in modify mode the records which are deleted from the grid will be updated for their relation =null
	 ** relfield = relation field of the child table i.e. foreign key
	 ** relvalue = relation value of the child table i.e. foreign key value
	 ** mtmtable= mtm table for relation will have atleast 4 fields ObjId2Parent, ParentObject, ObjId2Child,ChildObject
	 ** Name and relation field is always should be composite key and unique in current design
	 **
	 **To Fix::::
	 **
	 ** Fix the problem while updating querymode=update/modify you are loosing extra objid for parent object
	 */

	public String makeBulkSQL(String table,boolean isparent,String fields ,String datatypestr ,
			String datastr,String querymode, String relfield,String mtmtable,String dirtyid){

		String []field = getArrayData(fields) ;
		String []dirtyidlist = getArrayData(dirtyid) ;
		String []datatype = new String[getArrayData(datatypestr).length];
		String []alldata=getArrayData(datastr) ;
		String []data;
		String dbtype=DatabaseTransaction.getDbType();
		String parentfilter="";
		//Fix the problem while updating you are loosing extra objid for parent object
		//if(querymode.equalsIgnoreCase("Insert")||isparent&&(querymode.equalsIgnoreCase("update")||querymode.equalsIgnoreCase("Modify")))
		if(querymode.equalsIgnoreCase("Insert"))
			data=alldata;
		else
			data = getNewModifyData(alldata,dirtyidlist,field) ;
		//TemplateUtility tu=new TemplateUtility();
		String updateFilter="";
		String relvalue="";
		int pindex=tu.getArrayFieldIndex("name",field);
		int relindex=tu.getArrayFieldIndex(relfield,field);
		//First Verify if any data exists atleast for one field in the data string
		boolean dat=false;
		for(int ip=0;ip<data.length;ip++){
			//logger.info("data ip="+data[ip]);
			if(ip>0&&data[ip]!=null&&!data[ip].equals("")){
				dat=true;
				break;
			}
		}
		if(!dat && isparent){
			logger.warn("\n WARNING: No Data for Parent Object "+table);
			return(null);
		}else if(!dat && !isparent){
			logger.warn("\n WARNING: No Data for Child Object "+table);
			return("");
		}
		// verify if the parent record exists, if true return null for bulk sql
		if (isparent ){
			relvalue=(relindex!=-1 && data.length>=field.length)? data[relindex] : "";
			String pname=(pindex!=-1 && data.length>=field.length)? data[pindex] : "";
			updateFilter=((relfield!=null && !relfield.equals("")&& relvalue!=null && !relvalue.equals("")) ? (relfield+"="+relvalue +" and ") : "" );
			if(querymode.equalsIgnoreCase("insert")){
				String psql= "select count(objid) from Table_"+table + " where  " +
						updateFilter + " upper(ltrim(rtrim(name))) = upper(ltrim(rtrim('"+tu.replaceSingleQouteForDatabase(pname) +"')))" ;
				TemplateTable presult=tu.getResultSet(psql);
				//logger.info("\n Row count="+presult.getRowCount());
				if(presult.getRowCount()>0 && !presult.getRow(0)[0].equals("0") && querymode.equalsIgnoreCase("insert"))
					return null;
			}
		}
		//verify if ObjId exists in the field list position 0

		int objpos=tu.getArrayFieldIndex("objid",field);;

		if(objpos!=0 &&objpos!=-1){
			logger.error("\n ERROR: ObjId should be always the first field in the fieldlist position,");
			logger.info("\n or it may not be at all in fields list, Please verify your list!");
			logger.info("\n Query can not be processed!");
			return null;
		}
		int datalength = data.length/field.length;
		//logger.info("\n Number Of Record for object"+table+"=" +datalength + "field length=" + field.length);
		String parentid="";
		String parentname="";
		String mycount="";
		if(dbtype.equalsIgnoreCase("Oracle")){
			parentid="parentid";
			parentname="parentname";
			mycount="mycount";
		}else if(dbtype.equalsIgnoreCase("Mssql")){
			parentid="@parentid";
			parentname="@parentname";
			mycount="@mycount";
		}

		String [][] dinsertarray = new String[datalength][field.length+1];
		String [][] dupdatearray = new String[datalength][field.length];
		String [] idarray= new String[datalength];
		String tableidupdate="\n\t\t\t update Table_"+ table+ " set " + relfield +" = "+ parentid + " where ObjId in (";
		String mtmidinsert="\n\t\t\t insert into Table_"+ table+ "(ObjId2parent,ParentObject,Objid2Child,ChildObject)values("+ parentid +","+ parentname ;
		String insertstr="Insert into table_" + table + "(ObjId,"+((relfield!=null&&!relfield.equals("")&&relindex==-1)?relfield+",":"");
		String insertval=",GenDate)values(?,"+((relfield!=null&&!relfield.equals("")&&relindex==-1)?parentid+",":"");
		String updatestr="update Table_"+table + " set "+((relfield!=null&&!relfield.equals("")&&relindex==-1)?relfield+"="+parentid+",":"");
		String selectparent="\n \t\t\t\t" + "select " + (dbtype.equalsIgnoreCase("Oracle")==true ?  " ObjId  into "+ parentid :parentid+"= ObjId")
				+ "  from Table_"+ table +" where " + ((updateFilter.equals("")&& !isparent)==true ? (relfield +"="+parentid + " and "): updateFilter )+" upper(ltrim(rtrim(name))) = upper(ltrim(rtrim('" ;
		String selectstr="select " + (dbtype.equalsIgnoreCase("Oracle")==true ?  " count(ObjId)  into "+ mycount :mycount+"= count(ObjId)") + "  from Table_"+ table +
				" where " +((updateFilter.equals("")&& !isparent)==true ? (relfield +"="+parentid + " and "): updateFilter )+ "upper(ltrim(rtrim(name))) = upper(ltrim(rtrim('" ;
		String deletestr="delete from Table_" + table +" where upper(ltrim(rtrim(name))) = upper(ltrim(rtrim(" ;
		String bulksql= "";
		String modifysql="\n \t\t Begin \n \t\t\t update Table_"+table+" set "+relfield+"=null where "+relfield+"="+parentid;
		String modifyidsql="\n \t\t update Table_"+table+" set " + relfield+"="+parentid + " where ObjId in(";
		String bulkinsert="\n \t\t Begin \n \t\t\t";
		String bulkupdate="\n \t\t Begin \n \t\t\t";
		String verifybulkupdate= "";
		String mtmbulkinsert="\n\n \t\t\t Begin \n";

		/*for ( int m=0; m<getArrayData(datatypestr).length; m++)
			datatype[m]="DataType." +getArrayData(datatypestr)[m];*/
		datatype=convertDataType(getArrayData(datatypestr));

		if (datalength*field.length!=data.length){
			logger.error("ERROR: All fields not having proper data!");
			return("");
		}else{

			int index=1;
			int k=0;
			for( int n=0; n<data.length; n++){

				if (index!=field.length+1){
					if(index>0 && datatype[index-1].equalsIgnoreCase(DataType.DATE) && dbtype.equalsIgnoreCase("Oracle")){
						//dinsertarray[k][index]="to_date('"+ data[n]+ "','mm/dd/yy')";
						//dupdatearray[k][index-1]="to_date('"+ data[n]+ "','mm/dd/yy')";
					}else if(index>0 && datatype[index-1].equalsIgnoreCase(DataType.DATE) && dbtype.equalsIgnoreCase("Mssql")){
						//dinsertarray[k][index]="cast('"+ data[n]+  "' as datetime)";
						//dupdatearray[k][index-1]="cast('"+ data[n]+  "' as datetime)";

					}else{
						//logger.info("Hello Index=" +index);

						dinsertarray[k][index]=data[n+((field[0].equalsIgnoreCase("ObjId") && n<data.length-1) ? 1:0)];
						dupdatearray[k][index-1]=data[n+((field[0].equalsIgnoreCase("ObjId") && n<data.length-1) ? 1:0)];

					}

					if (index==1){
						if(field[0].equalsIgnoreCase("ObjId")&&data[n]!=null&&!data[n].equals("")){
							dinsertarray[k][0]=data[n];
							parentfilter=" objid="+dinsertarray[k][0] +" and ";
							//dupdatearray[k][0]=data[n];
						}else{
							dinsertarray[k][0]=getPrimaryKey();
							//dinsertarray[k][0]=getPrimaryKey("Table_"+table,"ObjId", (field[0].equalsIgnoreCase("ObjId")==true? data[n+1] :data[n]));
							//dupdatearray[k][0]=dinsertarray[k][0];
						}
						idarray[k]=dinsertarray[k][0];
						if (k==0)
							tableidupdate+=dinsertarray[k][0];
						mtmbulkinsert+=mtmidinsert+"," +dinsertarray[k][0]+"," + "'" + table+ "');";

						if (k>0)
							tableidupdate+="," + dinsertarray[k][0];

					}
					//logger.info("Hello k="+ k +"n=" + n );

					index++;
				}else{
					k++;
					n--;
					index=1;
				}
			}
		}
		mtmbulkinsert+= " \n \t\t\t end; \n\t\t end;";
		tableidupdate +=  "); \n\t\t\t";
		//logger.info("Hello 3!");
		for ( int i=(field[0].equalsIgnoreCase("ObjId")? 1:0); i<field.length; i++){
			//logger.info("Hello 4!");
			if (i==(field[0].equalsIgnoreCase("ObjId")? 1:0)){
				insertstr+=field[i];
				insertval+="?";
				updatestr+=field[i] + "=?";
			}else{
				insertstr+="," +field[i];
				insertval+=",?";
				updatestr+="," + field[i] + "=?";
			}
		}
		insertstr +=insertval+"," + (dbtype.equalsIgnoreCase("Oracle")==true ? "sysdate" : "getdate()") + " )";

		for ( int row=0; row<datalength; row++){
			String []iddatatype={DataType.VARCHAR};
			String newupdatestr="";
			//logger.info("Hello 5!");
			String singleupdatestr="";
			String singleinsertstr="";
			setInputTable(getCustomIndexArray(field,1),getCustomInsertArray(iddatatype,datatype),dinsertarray[row]);
			setQuery(insertstr);
			singleinsertstr="\n\t\t\t\t"+ getQuery() +";";
			bulkinsert+= "\n \t\t\t\t" + getQuery() +";";
			newupdatestr=updatestr+ " where " +(parentfilter.equals("")&&!isparent?((updateFilter.equals(""))==true ? (relfield +"="+parentid + " and "): updateFilter ):parentfilter)
					+ " upper(ltrim(rtrim(name))) = upper(ltrim(rtrim('" +tu.replaceSingleQouteForDatabase(dupdatearray[row][(!querymode.equalsIgnoreCase("insert")? pindex-1:pindex+1)])+"')))" ;
			setInputTable(getCustomIndexArray(field,0),datatype,dupdatearray[row]);
			setQuery(newupdatestr);
			singleupdatestr="\n\t\t\t\t"+ getQuery() +";";
			selectparent+= tu.replaceSingleQouteForDatabase( dupdatearray[row][(!querymode.equalsIgnoreCase("insert")? pindex-1:pindex+1) ])+"')));" ;
			bulkupdate+= "\n \t\t\t\t" + getQuery() +";";
			verifybulkupdate+= "\n\n\t\t\t Begin \n \t\t\t\t\t" + selectstr + tu.replaceSingleQouteForDatabase(dupdatearray[row][(!querymode.equalsIgnoreCase("insert")? pindex-1:pindex+1)])+"')));"  +
					"\n\t\t\t if( "+ mycount +">0)"+ (dbtype.equalsIgnoreCase("Oracle")==true ?  " then " :"" )+
					( (relfield==null ||relfield.equals("")) ?  "\n\t\t\t Begin \n"+ selectparent :"" )+
					singleupdatestr+ ( (relfield==null ||relfield.equals("")) ?   "\n \t\t\t end;" :"" )+
					( isparent &&querymode.equalsIgnoreCase("modify")?(dbtype.equalsIgnoreCase("Oracle")==true ?"\n\t\t\t end if;":"")+" \n\t\t end; ":"\n\t\t\t else \n \t\t\t " +
							singleinsertstr +(dbtype.equalsIgnoreCase("Oracle")==true ?  "\n \t\t\t end if;" :"" )   +
							"\n\t\t\t" + (dbtype.equalsIgnoreCase("Oracle")==true ? ("\t exception \n\t\t\t\t when no_data_found then \n \t\t\t\t "+ mycount + ":=0; \n \t\t\t end;" ): ("end;"))) ;

		}

		bulkupdate+= "\n \t\t end;";
		bulkinsert+= "\n \t\t end;";
		//logger.info("Hello 6!");

		//Identify the parent table to declare the stored proc variable seting parent objid
		if(isparent){
			setObjId(dinsertarray[0][0]);
			if(dbtype.equalsIgnoreCase("Oracle")){
				bulksql="\n \t \t declare \n \t\t\t "+ mycount +" integer :=0; "+
						"\n \t\t\t "+parentid +" integer;" + "\n \t\t\t "+ parentname+ " varchar(50);" + "\n \t\t Begin \n " +
						"\n\t\t\t "+ parentid +" := " + dinsertarray[0][0]+";" + "\n\t\t\t "+parentname +" := '" +table+"';" ;

			}else if(dbtype.equalsIgnoreCase("Mssql")){
				bulksql="\n \t\t\t declare  "+ mycount +" integer; "+
						"\n \t\t\t declare  "+parentid +" integer;" + "\n \t\t\t declare  "+ parentname+ " varchar;" + "\n \t\t Begin \n " +
						"\n\t\t\t select  "+ parentid +" = " + dinsertarray[0][0]+";" + "\n\t\t\t select "+parentname +" = '" + table+"';" ;


			}
			// logger.info("Hello 7 bulksql="+bulksql);
		}
		//find out unchanged objid
		if(getUnchangedObjId()!=null && getUnchangedObjId().length>0){
			if(idarray.length>0){
				for(int i=0;i<getUnchangedObjId().length &&getUnchangedObjId()[i]!=null&& !getUnchangedObjId()[i].equals("");i++)
					modifyidsql+=getUnchangedObjId()[i]+",";
			}else{
				modifyidsql+=getUnchangedObjId()[0];
				for(int i=1;i<getUnchangedObjId().length &&getUnchangedObjId()[i]!=null && !getUnchangedObjId()[i].equals("");i++)
					modifyidsql+=","+getUnchangedObjId()[i];
				modifyidsql+="); \n\t\t end; \n";
			}
		}
		//make the modify objid list for setting new relation
		for(int idcount=0;idcount<idarray.length-1;idcount++)
			modifyidsql+=idarray[idcount]+",";
		modifyidsql+=idarray[idarray.length-1]+"); \n\t\t end; \n";

		/*if( field.length==data.length && querymode.equalsIgnoreCase("modify")){
                    bulksql+= verifybulkupdate;
           	}else */
		if( field.length==data.length && querymode.equalsIgnoreCase("Insert")){
			//bulksql+= verifybulkupdate;
			bulksql+= bulkinsert;

		}else if( field.length==data.length && querymode.equalsIgnoreCase("update")){
			bulksql+= verifybulkupdate;

		}else if( field.length!=data.length && querymode.equalsIgnoreCase("Insert")){
			//	logger.info("Hello insert"+ data.length);
			bulksql+=  bulkinsert + (mtmtable!=null && !mtmtable.equals("")==true ? mtmbulkinsert:(" " ));

		}else if( field.length!=data.length && querymode.equalsIgnoreCase("update")){
			bulksql+= verifybulkupdate + (mtmtable!=null && !mtmtable.equals("")==true ? mtmbulkinsert:(" \n \t\t\t  " + (relindex==-1?tableidupdate:"")));

		}else if( field.length<=data.length && querymode.equalsIgnoreCase("modify")){
			bulksql+= verifybulkupdate +((relindex==-1)?(modifysql +"\n \t \t\t"+ modifyidsql):"");
			//logger.info("Hello 8 bulksql="+bulksql);

		}
		//logger.info("\n Bulksql for Table_"+table+bulksql);
		return(bulksql);
	}

	/***
	 * This method will delete the records physically or relationally based on 4 modes
	 * Mode 1=deletes parent record and relation for next level of child objects, does not delete child records physically
	 * Mode 2=deletes parent and next level of child records physically from database
	 * Mode=3=deletes parent and 2 level (child and grandchild) of child relations in heirarchy, does not delete child records physically
	 * Mode=4=deletes parent and 2 level  (child and grandchild) of child records physically from database
	 * dbType= Database Type Mssql or Oracle
	 * parent= Parent Table name
	 * Child= Child table name
	 * By default always use mode 1
	 ***/
	public String makeBulkDelete(String parent, String parentObjId , int mode){
		String bulksql="";
		String parentid="";
		String parentname="";
		String mycount="";
		String dbtype=DatabaseTransaction.getDbType();

		String query ="select do.ObjId,do.ObjectName childobject,do.AliasName,do.SchemaName,"+
				"da.parenttable,da.relationname,da.relationType from "+
				"sml_DbObject do,sml_attributerelation da where "+
				"do.objid=da.AttributeRelation2DbObject and " +
				"da.CurrentState='1' and ltrim(rtrim(da.ParentTable))=(ltrim(rtrim('";
		if(dbtype.equalsIgnoreCase("Oracle")){
			parentid="parentid";
			//logger.info("It is Brinda  " + parentid );
			parentname="parentname";
			mycount="mycount";
			bulksql="\n \t \t declare \n \t\t\t "+ mycount +" integer :=0; "+
					"\n \t\t\t "+parentid +" raw(16);" + "\n \t\t\t "+ parentname+ " varchar(50);" + "\n \t\t Begin \n " +
					"\n\t\t\t "+ parentid +" := '" +parentObjId+"';" + "\n\t\t\t "+parentname +" := '" +parent+"';" ;

		}else if(dbtype.equalsIgnoreCase("Mssql")){
			parentid="@parentid";
			parentname="@parentname";
			mycount="@mycount";
			bulksql="\n \t\t\t declare  "+ mycount +" integer; "+
					"\n \t\t\t declare  "+parentid +" varchar;" + "\n \t\t\t declare  "+ parentname+ " varchar;" + "\n \t\t Begin \n " +
					"\n\t\t\t select  "+ parentid +" = " + parentObjId+";" + "\n\t\t\t select "+parentname +" = '" + parent+"';" ;
		}

		//First find out the direct child object for the parent
		this.setQuery(query+parent+"')))");
		TemplateTable result=this.getTableResultset();

		if(mode==ACONST.BULK_DELETE_MODE_REMOVE_RELATION) {
			String tmpsql="\n\t\t\t Begin ";
			for (int i=0; i<result.getRowCount(); i++){
				String relation=result.getFieldValue("relationname",i);
				tmpsql+="\n\t\t\t\t update table_"+result.getFieldValue("childobject",i) + " set "+
						relation+"=0,destinationid="+parentid+" where "+relation+"='"+parentid+"';";
			}
			if(result.getRowCount()>0)
				tmpsql+="\n\t\t\t End;";
			else
				tmpsql="";

			tmpsql+="\n\t\t\t delete from table_"+parent+" where objid="+parentObjId+";";
			tmpsql+="\n\t\t End;";
			bulksql+=tmpsql;
		}else if(mode==ACONST.BULK_DELETE_MODE_DELETE_RECORD) {
			String tmpsql="\n\t\t\t Begin ";
			for (int i=0; i<result.getRowCount(); i++){
				tmpsql+="\n\t\t\t\t delete from table_"+result.getFieldValue("childobject",i) +
						" where "+result.getFieldValue("relationname",i)+"='"+parentid+"';";
			}
			if(result.getRowCount()>0)
				tmpsql+="\n\t\t\t End;";
			else
				tmpsql="";

			tmpsql+="\n\t\t\t delete from table_"+parent+" where objid='"+parentObjId+"';";
			tmpsql+="\n\t\t End;";
			bulksql+=tmpsql;
		}

		return(bulksql);
	}
	public String removeSql(String parent, String parentObjId ,String[] childs, String groupuser){
	    
	    for(String child:childs){
	    	if(!tu.isEmptyValue(child) &&!child.equalsIgnoreCase(parent)){
	    		String sql="select objid from table_"+child + " where "+child+"2"+parent+"='"+parentObjId +"' and groupuser='"+groupuser+"' and rownum=1";
	    		TemplateTable result=tu.getResultSet(sql);
	    		if(result.getRowCount()>0){
	    			return null;
	    		}
	    	
	    	}
	    	
	    }
	    return ("delete from table_"+parent+ " where objid='"+parentObjId+"' and groupuser='"+groupuser+"'");
	}
	public String removeSql(String parent, String parentObjId ,String[] childs){
	    
	    for(String child:childs){
	    	if(!tu.isEmptyValue(child) &&!child.equalsIgnoreCase(parent)){
	    		String sql="select objid from table_"+child + " where "+child+"2"+parent+"='"+parentObjId +"' and rownum=1";
	    		TemplateTable result=tu.getResultSet(sql);
	    		if(result.getRowCount()>0){
	    			return null;
	    		}
	    	
	    	}
	    	
	    }
	    return ("delete from table_"+parent+ " where objid='"+parentObjId+"'");
	}

}
