package cms.service.gen;


import java.io.*;
import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import cms.service.app.PartitionObject;
import cms.service.template.TemplateTable;
import cms.service.template.TemplateUtility;
import cms.service.util.FileUtility;


/**
 * Title:        Semantic Application
 * Description:  Semantic Main Infrastructure Project
 * Copyright:    Copyright (c) 2001
 * Company:      SemanticJava Soft
 * @author
 * @version 1.0
 */

public class SchemaGenerator {

    static final String adminuser="admin@biderp.com";
	static Log logger = LogFactory.getLog(SchemaGenerator.class);
	private SemanticTag tag= new SemanticTag();	
	private TemplateUtility tu= new TemplateUtility();
	private PartitionObject key = new PartitionObject();
	private FileUtility fo= new FileUtility();
	private String name="";   
	private int[] findcolllist={0,1,2,5};	
	private String tablename="";
	private String importtable="";
	private String dbtype="Oracle";	
	private String signature="";
	private String FieldKey="";
	private String PrevTag="";
	private String dbname="";       
	private String codeattribute="";
	private String codeattributeid="";

	// Id related variable
	private String appId="";
	private String tableId="";
	private String viewId="";
	private String attrId="";
	private String relationId="";
	private String indexId="";
	private String domainId="";
	private String cobjId="";
	private String ruleobjid="";
	private String ruleatrobjid="";
	private String listpropertyid="";
	private String objectruleid="";
	private String dbinstance="";
	private String SchemaMode="new";
	private String gendate="";
	private String compdate="";
	private String objectname="";
	public boolean islog=false;
	private String ruleobject="";
	private String ruleattribute="";
	public static final int APPLICATION= 0;
	public static final int SEMANTICOBJECT= 2;
	public static final int FIELDMAP= 4;
	public static final int SEMANTICDATA= 6;
	public static final int CONVERTOBJECT= 8;
	public static final int LISTPROPERTY= 10;
	public static final int IMPLEMENTOBJECT= 12;
	public static final int OBJECTRELATION= 14;
	public static final int VALIDATION= 16;
	public static final int INDEX= 18;
	public static final int ATTRIBUTE= 20;
	public static final int DOMAIN= 22;
	public static final int OBJECTRULE= 24;
	public static final int GENERICCODE= 26;
	public static final int ACTIONQUERY= 28;
	public static final int CONTROLMAP= 30;
	public static final int ATTRIBUTERELATION= 32;
	public static final int RECORD= 34;
	public static final int SEMANTICVIEW= 36;
	public static final int VIEWRELATION= 38;
	public static final int VIEWATTRIBUTE= 40;
	public static final int PROJECTCODE= 42;
	public static final int MAINCODE= 44;
	public static final int SUBCODE= 46;
	public static final int TASKCODE= 48;
	public static final int CALLERMAP= 50;
	public static final int VALUEMAP= 52;
	public static final int CHARTMAP= 54;

	public void SchemaGenerator(){
		SchemaMode="new";
		appId="";
		tableId="";
		attrId="";
		relationId="";
		indexId="";
		domainId="";
		cobjId="";
	}

	public void setIOUtility(FileUtility fo){
		this.fo=fo;
	}
	public FileUtility getIOUtility(){
		return(fo);
	}
	public void setName(String Name){
		this.name=Name;
	}
	public String getName(){
		return(name);
	}
	public void setDbName(String Name){
		this.dbname=Name;
	}
	public String getDbName(){
		return(dbname);
	}
	public void setDbInstance(String Name){
		this.dbinstance=Name;
	}
	public String getDbInstance(){
		return(dbinstance);
	}
	public void setObjectName(String Name){
		this.objectname=Name;
	}
	public String getObjectName(){
		return(objectname);
	}
	public void setImportTable(String Name){
		this.importtable=Name;
	}
	public String getImportTable(){
		return(importtable);
	}
	public void setTableName(String Name){
		this.tablename=Name;
	}
	public String getTableName(){
		return(tablename);
	}
	public void setDbType(String dbtype){
		this.dbtype=dbtype;
	}
	public String getDbType(){
		return(dbtype);
	}
	public void setSignature(String Signature){
		this.signature=Signature;
	}
	public String getSignature(){
		return(signature);
	}
	public void setFieldKey(String Key){
		this.FieldKey=Key;
	}
	public String getFieldKey(){
		return(FieldKey);
	}
	public void setPreviousTag(String tag){
		this.PrevTag=tag;
	}
	public String getPreviousTag(){
		return(PrevTag);
	}
	public int[] getVisibleColumn(){
		return(findcolllist);
	}
	public String getPrimaryKey(){
		return(key.getPrimaryKey());
	}


	/* public boolean importData(String FilePath){
          //Tag data
          Vector tagdata=new Vector();
          int tagindex=0;
          String retString="";
          //first cleanup object rule
          tu.executeQuery("delete from table_listproperty");
          tu.executeQuery("delete from table_attribute");
          tu.executeQuery("delete from table_Object");
          tu.executeQuery("update sml_dbattribute set HasCodeObject='no' where HasCodeObject='null'");
          tu.executeQuery("insert into table_object  select ObjId,ObjectName,'1','1',DbObject2Application"+
                         ",0,0,GenUser,GenDate,ModUser,ModDate from sml_dbobject");
          tu.executeQuery("insert into table_attribute  select objid,AttributeName,dbObjectName,HasProperty,HasCodeObject,FieldType,"+
                          "DbAttribute2DbObject,0,0,GenUser,GenDate,ModUser,ModDate from sml_dbattribute");

          try {
      		RandomAccessFile file = new RandomAccessFile(FilePath,"r");
                while (file.getFilePointer() < file.length()){
                    retString = file.readLine().trim();
                    int retindex=retString.indexOf("//");
                    if(retindex<0 &&!retString.equals("")&& retString!=null ){
                      if(islog)
                        getIOUtility().writeToFile(retString);
                      logger.info(retString);
                      if(tag.isTag(retString)&& tag.getTagType(retString).equalsIgnoreCase("BEGIN") &&tag.getTagIndex(getPreviousTag())!=SEMANTICOBJECT)
                        tagindex=tag.getTagIndex(retString);
                      if(tagindex!=-1 && tag.getTagType(retString).equalsIgnoreCase("BEGIN") &&tag.getTagIndex(getPreviousTag())!=SEMANTICOBJECT){
                          setPreviousTag(retString);
                          tagdata.removeAllElements();
                      }else if (tagindex!=-1 && tag.getTagType(retString).equalsIgnoreCase("END")&&!retString.equalsIgnoreCase("</SemanticObject>")||
                                tagindex!=-1 && !getPreviousTag().equalsIgnoreCase(retString)&&tag.getTagIndex(retString)>0 &&!retString.equalsIgnoreCase("</SemanticObject>"))
                              //Import Tag data here
                                switch(tagindex){
    				  case SEMANTICDATA :
                                      importAppData(tagdata);
                                      break;
                                  case LISTPROPERTY:
                                      importListProperty(tagdata);
                                      break;
                                }
                      else{
                          tagdata.addElement(retString);

                        }
                    }
                }
                file.close();
          }catch (Exception e) {
                if(islog)
                getIOUtility().writeToFile("\nError: " + e.toString());
      		logger.info("Error: " + e.toString());
                e.printStackTrace();
			return(false);
          }
          return(true);
        }
	 */
	public boolean importRuleData(String FilePath){
		//Tag data
		Vector tagdata=new Vector();
		int tagindex=0;
		String retString="";
		//first cleanup object rule

		tu.executeQuery("delete from table_objectrule");
		tu.executeQuery("delete from table_actionquery");

		tu.executeQuery("delete from table_genericcode");
		tu.executeQuery("delete from table_attribute");
		tu.executeQuery("delete from table_Object");
		tu.executeQuery("update sml_dbattribute set HasCodeObject='no' where HasCodeObject='null'");
		tu.executeQuery("insert into table_object  select ObjId,ObjectName,'1','1','',DbObject2Application"+
				",null,'0','0',GenUser,GenUser,GenDate,ModUser,ModDate from sml_dbobject");
		tu.executeQuery("insert into table_attribute  select objid,AttributeName,dbObjectName,HasProperty,HasCodeObject,"+
				"DbAttribute2DbObject,null,'0','0',GenUser,GenUser,GenDate,ModUser,ModDate from sml_dbattribute");

		try {
			RandomAccessFile file = new RandomAccessFile(FilePath,"r");
			while (file.getFilePointer() < file.length()){
				retString = file.readLine().trim();
				int retindex=retString.indexOf("//");
				if(retindex<0 &&!retString.equals("")&& retString!=null ){
					if(islog)
						getIOUtility().writeToFile(retString);
					logger.info(retString);
					if(tag.isTag(retString)&& tag.getTagType(retString).equalsIgnoreCase("BEGIN") &&tag.getTagIndex(getPreviousTag())!=LISTPROPERTY)
						tagindex=tag.getTagIndex(retString);
					if(tagindex!=-1 && tag.getTagType(retString).equalsIgnoreCase("BEGIN") &&tag.getTagIndex(getPreviousTag())!=LISTPROPERTY){
						setPreviousTag(retString);
						tagdata.removeAllElements();
					}else if (tagindex!=-1 && tag.getTagType(retString).equalsIgnoreCase("END")&&!retString.equalsIgnoreCase("</ListProperty>")||
							tagindex!=-1 && !getPreviousTag().equalsIgnoreCase(retString)&&tag.getTagIndex(retString)>0 &&!retString.equalsIgnoreCase("</ListProperty>")
							|| tagindex!=-1 && !getPreviousTag().equalsIgnoreCase("</ActionQuery>")&&tag.getTagIndex(retString)>0&& tag.getTagType(retString).equalsIgnoreCase("END") && retString.equalsIgnoreCase("</ListProperty>"))
						//Import Tag data here
						switch(tagindex){
						case LISTPROPERTY:
							tagindex=tag.getTagIndex(retString);
							importListProperty(tagdata);
							setPreviousTag(retString);
							tagdata.removeAllElements();
							break;
						case GENERICCODE:
							importGenericCode(tagdata);
							break;
						case OBJECTRULE:
							importObjectRule(tagdata);
							break;
						case ACTIONQUERY:
							importActionQuery(tagdata);
							setPreviousTag(retString);
							break;

						}
					else{
						tagdata.addElement(retString);

					}
				}
			}
			file.close();
		}catch (Exception e) {
			if(islog){
				getIOUtility().writeToFile("\nError: " + e.toString());
				logger.info("Error: " + e.toString());
			}
			e.printStackTrace();
			return(false);
		}
		return(true);
	}
	public boolean createPrivgroup(){

		//Insert all privilegegroup records based on the OfferCode
		String privsql="select g.* from table_genericcode g, table_codeattribute c "+
				" where c.name='ReasonCode' and c.objid=g.genericcode2codeattribute and not exists (select * from table_privilegegroup p where p.name=g.name)";
		TemplateTable privs=tu.getResultSet(privsql);
		for(int r=0;r<privs.getRowCount();r++){
			String privname=privs.getFieldValue("name",r);
			String codeval=privs.getFieldValue("codevalue",r);
			setTableName("Table_privilegegroup");
			setFieldKey("ObjId");
			setSignature(privname);
			String privid=  getPrimaryKey();
			String inspriv="insert into  table_PrivilegeGroup(ObjId,Name,Scope,Status,GroupUser,GenUser,Gendate)values("+privid+",'"+privname+"','"+codeval+"','1','"+adminuser+"','"+adminuser+"',sysdate)";
			tu.executeQuery(inspriv);
			
			if(!privname.equals("Registration")){
				String adm="insert into  table_AddModule(ObjId,Name,offercode,cost,currencycode,description,GroupUser,GenUser,Gendate)values("+privid+",'"+privname+"','"+codeval+"',0,1,'please specify','"+adminuser+"','"+adminuser+"',sysdate)";
				tu.executeQuery(adm);
			}
		}
		return(true);
	}

	public boolean createCalendarAndMonth(){

		//Insert all Calendar records based on the CalendarCode
		String calsql="select g.* from table_genericcode g, table_codeattribute c "+
				" where c.name='YearCode' and c.objid=g.genericcode2codeattribute and not exists (select * from table_Calendar cl where cl.name=g.name)";
		TemplateTable calv=tu.getResultSet(calsql);
		for(int r=0;r<calv.getRowCount();r++){
			String name=calv.getFieldValue("name",r);
			String year=calv.getFieldValue("codevalue",r);
			setTableName("Table_Calendar");
			setFieldKey("ObjId");
			setSignature(name);
			String yearid=  getPrimaryKey();
			String insyear="insert into table_Calendar(ObjId,Name,YearCode,GroupUser,GenUser)values("+yearid+",'"+name+"','"+year+"','"+adminuser+"','"+adminuser+"')";
			tu.executeQuery(insyear);
			if(islog){
				getIOUtility().writeToFile("\n" + insyear);
				logger.info("\n " + insyear);
			}
			if(!year.equals("")){
				String monthsql="select g.* from table_genericcode g, table_codeattribute c "+
						" where c.name='MonthCode' and c.objid=g.genericcode2codeattribute and not exists (select * from table_Month m where m.yearcode='"+year+"')";
				TemplateTable month=tu.getResultSet(monthsql);
				for( int i=0; i<month.getRowCount(); i++){
					String mname=month.getFieldValue("name",i);
					String mvalue=month.getFieldValue("codevalue",i);
					setTableName("Table_Month");
					setFieldKey("ObjId");
					setSignature(mname);
					String monthid=  getPrimaryKey();
					String sql="insert into table_Month(ObjId,Name,YearCode,MonthCode,Month2Calendar,GroupUser,GenUser)values("+monthid+",'"+mname+"','"+year+"','"+mvalue+"',"+yearid+",'"+adminuser+"','"+adminuser+"')";
					tu.executeQuery(sql);
					if(islog){
						getIOUtility().writeToFile("\n" + sql);
						logger.info("\n " + sql);
					}
				}
			}
		}
		return(true);
	}
	/**
	 * This method is use for importing table data based on some condition
	 * Paremeter:
	 *        FilePath : The Physical location of the file with name
	 *
	 */

	public boolean importJobCode(String FilePath){
		//Tag data
		Vector tagdata=new Vector();
		int tagindex=0;
		String retString="";
		//first cleanup object rule
		tu.executeQuery("delete from table_projectcode");
		tu.executeQuery("delete from table_maincode");
		tu.executeQuery("delete from table_subcode");
		tu.executeQuery("delete from table_taskcode");

		try {
			RandomAccessFile file = new RandomAccessFile(FilePath,"r");
			while (file.getFilePointer() < file.length()){
				retString = file.readLine().trim();
				int retindex=retString.indexOf("//");
				if(retindex<0 &&!retString.equals("")&& retString!=null ){
					if(islog)
						getIOUtility().writeToFile(retString);
					logger.info(retString);
					if(tag.isTag(retString)&& tag.getTagType(retString).equalsIgnoreCase("BEGIN") )
						tagindex=tag.getTagIndex(retString);
					if(tagindex!=-1 && tag.getTagType(retString).equalsIgnoreCase("BEGIN") ){
						setPreviousTag(retString);
						tagdata.removeAllElements();
					}else if (tagindex!=-1 && tag.getTagType(retString).equalsIgnoreCase("END")||
							tagindex!=-1 && !getPreviousTag().equalsIgnoreCase(retString)&&tag.getTagIndex(retString)>0 ){
						//Import Tag data here
						switch(tagindex){
						case PROJECTCODE:
							importProjectCode(tagdata);
							break;
						case MAINCODE:
							importMainCode(tagdata);
							break;
						case SUBCODE:
							importSubCode(tagdata);
							break;
						case TASKCODE:
							importTaskCode(tagdata);
							break;

						}
					}else{
						tagdata.addElement(retString);

					}
				}
			}
			file.close();
		}catch (Exception e) {
			if(islog){
				getIOUtility().writeToFile("\nError: " + e.toString());
				logger.error("Error: " + e.toString());
			}
			e.printStackTrace();
			return(false);
		}
		return(true);
	}

	public boolean importViewData(String FilePath){
		//Tag data
		Vector tagdata=new Vector();
		int tagindex=0;
		String retString="";
		tu.executeQuery("delete from sml_Viewobject where upper(objecttype)=Upper('View')");
		tu.executeQuery("delete from sml_viewattribute ");
		tu.executeQuery("delete from sml_viewrelation ");
		try {
			RandomAccessFile file = new RandomAccessFile(FilePath,"r");
			while (file.getFilePointer() < file.length()){
				retString = file.readLine().trim();
				int retindex=retString.indexOf("//");
				if(retindex<0 &&!retString.equals("")&& retString!=null ){
					if(islog)
						getIOUtility().writeToFile(retString);
					logger.info(retString);
					if(tag.isTag(retString)&& tag.getTagType(retString).equalsIgnoreCase("BEGIN") &&tag.getTagIndex(getPreviousTag())!=SEMANTICVIEW)
						tagindex=tag.getTagIndex(retString);
					if(tagindex!=-1 && tag.getTagType(retString).equalsIgnoreCase("BEGIN") &&tag.getTagIndex(getPreviousTag())!=SEMANTICVIEW){
						setPreviousTag(retString);
						tagdata.removeAllElements();
					}else if (tagindex!=-1 && tag.getTagType(retString).equalsIgnoreCase("END")&&!retString.equalsIgnoreCase("</SemanticView>")||
							tagindex!=-1 && !getPreviousTag().equalsIgnoreCase(retString)&&tag.getTagIndex(retString)>0 &&!retString.equalsIgnoreCase("</SemanticView>"))
						//Import Tag data here
						switch(tagindex){

						case SEMANTICVIEW:
							tagindex=tag.getTagIndex(retString);
							importSemanticView(tagdata);
							setPreviousTag(retString);
							tagdata.removeAllElements();
							break;
						case VIEWATTRIBUTE:
							importViewAttribute(tagdata);
							break;
						case VIEWRELATION:
							importViewRelation(tagdata);
							break;
						}
					else{
						tagdata.addElement(retString);

					}
				}
			}
			file.close();
		}catch (Exception e) {
			if(islog){
				getIOUtility().writeToFile("\nError: " + e.toString());
				logger.error("Error: " + e.toString());
			}
			e.printStackTrace();
			return(false);
		}
		return(true);
	}
	public boolean importSchemaData(String FilePath){
		//Tag data
		Vector tagdata=new Vector();
		int tagindex=0;
		String retString="";

		try {
			RandomAccessFile file = new RandomAccessFile(FilePath,"r");
			while (file.getFilePointer() < file.length()){
				retString = file.readLine().trim();
				int retindex=retString.indexOf("//");
				if(retindex<0 &&!retString.equals("")&& retString!=null ){
					if(islog){
						getIOUtility().writeToFile(retString);
						logger.info(retString);
					}
					if(tag.isTag(retString)&& tag.getTagType(retString).equalsIgnoreCase("BEGIN") &&tag.getTagIndex(getPreviousTag())!=SEMANTICOBJECT)
						tagindex=tag.getTagIndex(retString);
					if(tagindex!=-1 && tag.getTagType(retString).equalsIgnoreCase("BEGIN") &&tag.getTagIndex(getPreviousTag())!=SEMANTICOBJECT){
						setPreviousTag(retString);
						tagdata.removeAllElements();
					}else if (tagindex!=-1 && tag.getTagType(retString).equalsIgnoreCase("END")&&!retString.equalsIgnoreCase("</SemanticObject>")||
							tagindex!=-1 && !getPreviousTag().equalsIgnoreCase(retString)&&tag.getTagIndex(retString)>0 &&!retString.equalsIgnoreCase("</SemanticObject>")){
						if(getImportTable()!=null &&!getImportTable().equals("")){
							//Import Tag data here
							switch(tagindex){
							case APPLICATION:
								importApplication(tagdata);
								break;
							case SEMANTICOBJECT:
								tagindex=tag.getTagIndex(retString);
								if(tu.parseInputValue(tagdata,"ObjectName").equalsIgnoreCase(getImportTable()))
									importSemanticObject(tagdata);
								setObjectName(tu.parseInputValue(tagdata,"ObjectName"));
								setPreviousTag(retString);
								tagdata.removeAllElements();
								break;
							case ATTRIBUTE:
								if(getObjectName().equalsIgnoreCase(getImportTable()))
									importAttribute(tagdata);
								break;
							case INDEX:
								if(getObjectName().equalsIgnoreCase(getImportTable()))
									importIndex(tagdata);
								break;
							case ATTRIBUTERELATION:
								if(getObjectName().equalsIgnoreCase(getImportTable()))
									importRelation(tagdata);
								break;
							case FIELDMAP:
								if(getObjectName().equalsIgnoreCase(getImportTable()))
									importFieldMap(tagdata);
								break;
							case CALLERMAP:
								if(getObjectName().equalsIgnoreCase(getImportTable()))
									importCallerMap(tagdata);
								break;
							case VALUEMAP:
								if(getObjectName().equalsIgnoreCase(getImportTable()))
									importValueMap(tagdata);
								break;
							case CHARTMAP:
								if(getObjectName().equalsIgnoreCase(getImportTable()))
									importChartMap(tagdata);
								break;
							}
						}else if(getImportTable()==null||getImportTable().equals("")){

							//Import Tag data here
							switch(tagindex){
							case APPLICATION:
								importApplication(tagdata);
								break;
							case SEMANTICOBJECT:
								tagindex=tag.getTagIndex(retString);
								importSemanticObject(tagdata);
								setPreviousTag(retString);
								tagdata.removeAllElements();
								break;
							case ATTRIBUTE:
								importAttribute(tagdata);
								break;
							case DOMAIN:
								importDomain(tagdata);
								break;
							case FIELDMAP:
								importFieldMap(tagdata);
								break;
							case CALLERMAP:
								importCallerMap(tagdata);
								break;
							case VALUEMAP:
								importValueMap(tagdata);
								break;
							 case CHARTMAP:
								importChartMap(tagdata);
								break;
							case INDEX:
								importIndex(tagdata);
								break;
							case ATTRIBUTERELATION:
								importRelation(tagdata);
								break;
							}
						}
					}else{
						tagdata.addElement(retString);

					}
				}
			}
			file.close();
		}catch (Exception e) {
			if(islog){
				getIOUtility().writeToFile("\nError: " + e.toString());
				logger.error("Error: " + e.toString());
			}
			e.printStackTrace();
			return(false);
		}
		return(true);
	}
	private boolean importApplication(Vector tagdata){
		String fieldvalues="";
		String sql="";
		String CurrentStartTime="";
		String CurrentEndTime="";
		String LastStartTime="";
		String LastEndTime="";
		String Moddate="";
		String fieldvalue="";
		/* if(getDbType().equalsIgnoreCase("Oracle"))
              tu.executeQuery("ALTER SESSION SET NLS_DATE_FORMAT = 'yyyy-mm-dd hh24:mi:ss'");
		 */
		CurrentStartTime=tu.getConvertDateTime(getDbType(),"select","CurrentStartTime","yyyy-mm-dd hh24:mi:ss","CurrentStartTime");
		CurrentEndTime=tu.getConvertDateTime(getDbType(),"select","CurrentEndTime","yyyy-mm-dd hh24:mi:ss","CurrentEndTime");

		// Verify if the object record exist
		//If record exists  than update the record and set LastStartTime=CurrentStartTime
		//LastEndTime=CurrentEndTime
		setDbName(tu.parseInputValue(tagdata,"DbName"));
		String verifyapp="Select ObjId,Version,"+CurrentStartTime+","+CurrentEndTime+" from SML_Application where upper(AppName)=upper('"+tu.parseInputValue(tagdata,"AppName")+"')" +
				" and Version='" + tu.parseInputValue(tagdata,"Version")+"'";
		TemplateTable appresult=tu.getResultSet(verifyapp);
		if(appresult!=null &&appresult.getRowCount()>0&& !appresult.getFieldValue("ObjId",0).equals("")){
			appId="'"+appresult.getFieldValue("ObjId",0)+"'";
			SchemaMode="upgrade";
			if(appresult.getFieldValue("CurrentStartTime",0)!=null
					&&!appresult.getFieldValue("CurrentStartTime",0).equals("")){
				LastStartTime=tu.getConvertDate(getDbType(),"update","LastStartTime","yyyy-mm-dd hh24:mi:ss",appresult.getFieldValue("CurrentStartTime",0));
			}else{
				LastStartTime=(getDbType().equalsIgnoreCase("Oracle")==true?"sysdate":getDbType().equalsIgnoreCase("mysql")==true?"now()":"cast(getdate() as datetime)");
			}
			if(appresult.getFieldValue("CurrentEndTime",0)!=null &&
					!appresult.getFieldValue("CurrentEndTime",0).equals("") ){
				LastEndTime=tu.getConvertDate(getDbType(),"update","LastEndTime","yyyy-mm-dd hh24:mi:ss",appresult.getFieldValue("CurrentEndTime",0));
			}else{
				LastEndTime=(getDbType().equalsIgnoreCase("Oracle")==true?"sysdate":getDbType().equalsIgnoreCase("mysql")==true?"now()":"cast(getdate() as datetime)");
			}
			Moddate=(getDbType().equalsIgnoreCase("Oracle")==true? "sysdate":getDbType().equalsIgnoreCase("mysql")==true?"now()":"getdate()");

			sql="update SML_Application set Description='"+ tu.parseInputValue(tagdata,"Description") + "',Status='"+
					tu.parseInputValue(tagdata,"Status") + "',Version='"+tu.parseInputValue(tagdata,"Version")+ "',LastStartTime="+
					LastStartTime+",DbName='"+getDbInstance()+"',LastEndTime="+LastEndTime+",Moddate="+Moddate;
			//Find if the last application version is same as the current application version
			//If the version is same than delete the schema metadata from SML_Dbobject,SML_ConvertObject,SMl_Attribute,
			//SML_AttributeRelation,SMl_AttributeDomain,SML_DbIndex
			if(tu.parseInputValue(tagdata,"Version").equalsIgnoreCase(appresult.getFieldValue("Version",0))){
				gendate=(getDbType().equalsIgnoreCase("mysql")==true?tu.getVarchar2DateTime("mysql",true,"gendate","yyyy-mm-dd hh24:mi:ss",""):getDbType().equalsIgnoreCase("Oracle")==true? tu.getVarchar2DateTime("oracle",true,"gendate","yyyy-mm-dd hh24:mi:ss",""):tu.getVarchar2DateTime("mssql",true,"gendate","yyyy-mm-dd hh24:mi:ss",""));
				fieldvalue=appresult.getFieldValue("CurrentStartTime",0);
				if (fieldvalue!=null &&!fieldvalue.equals("")){
					compdate=(getDbType().equalsIgnoreCase("mysql")==true?tu.getVarchar2DateTime("mysql",true,"CurrentStartTime","yyyy-mm-dd hh24:mi:ss",fieldvalue):getDbType().equalsIgnoreCase("Oracle")==true? tu.getVarchar2DateTime("oracle",true,"CurrentStartTime","yyyy-mm-dd hh24:mi:ss",fieldvalue):tu.getVarchar2DateTime("mssql",true,"CurrentStartTime","yyyy-mm-dd hh24:mi:ss",fieldvalue));
				}else{
					compdate=gendate;
				}
				if(getImportTable()!=null &&!getImportTable().equals("")){
					String sml_AttributeRelation="delete from SML_AttributeRelation where "+ (getDbType().equalsIgnoreCase("Oracle")? "upper(relationname) like '":"upper(relationname) like '")+getImportTable().toUpperCase()+"2%'";
					tu.executeQuery(sml_AttributeRelation);
					String sml_DbIndex="delete from SML_DbIndex where "+
							(getDbType().equalsIgnoreCase("Oracle")? "upper(tablename)='":"upper(tablename)='")+getImportTable().toUpperCase()+"'";
					tu.executeQuery(sml_DbIndex);
					String sml_dbobject="delete from SML_DbObject where " +
							(getDbType().equalsIgnoreCase("Oracle")? "upper(objectname)='":"upper(objectname)='")+getImportTable().toUpperCase()+"'";
					tu.executeQuery(sml_dbobject);
					String sml_dbAttribute="delete from SML_DbAttribute where "+
							(getDbType().equalsIgnoreCase("Oracle")? "upper(dbobjectname)='":"upper(dbobjectname)='")+getImportTable().toUpperCase()+"'";
					tu.executeQuery(sml_dbAttribute);
					String sml_fldmap="delete from SML_FieldMap where "+
							(getDbType().equalsIgnoreCase("Oracle")? "upper(TargetObject)='":"upper(TargetObject)='")+getImportTable().toUpperCase()+"'";
					tu.executeQuery(sml_fldmap);
					String sml_calmap="delete from SML_CallerMap where "+
							(getDbType().equalsIgnoreCase("Oracle")? "upper(DbObjectName)='":"upper(DbObjectName)='")+getImportTable().toUpperCase()+"'";
					tu.executeQuery(sml_calmap);
					String sml_valuemap="delete from SML_ValueMap where "+
							(getDbType().equalsIgnoreCase("Oracle")? "upper(ObjectName)='":"upper(DbObjectName)='")+getImportTable().toUpperCase()+"'";
					tu.executeQuery(sml_valuemap);
					
					String sml_chartmap="delete from SML_ChartMap where "+
							(getDbType().equalsIgnoreCase("Oracle")? "upper(TableName)='":"upper(DbObjectName)='")+getImportTable().toUpperCase()+"'";
					tu.executeQuery(sml_chartmap);

				}else{

					String sml_dbobject="delete from SML_DbObject";
					tu.executeQuery(sml_dbobject);
					String sml_dbAttribute="delete from SML_DbAttribute";
					tu.executeQuery(sml_dbAttribute);
					String sml_AttributeDomain="delete from SML_AttributeDomain";
					tu.executeQuery(sml_AttributeDomain);
					String sml_AttributeRelation="delete from SML_AttributeRelation";
					tu.executeQuery(sml_AttributeRelation);
					String sml_DbIndex="delete from SML_DbIndex";
					tu.executeQuery(sml_DbIndex);
					String sml_fldmap="delete from SML_FieldMap";
					tu.executeQuery(sml_fldmap);
					String sml_calmap="delete from SML_CallerMap";
					tu.executeQuery(sml_calmap);
					String sml_valuemap="delete from SML_ValueMap";
					tu.executeQuery(sml_valuemap);
					String sml_chartmap="delete from SML_ChartMap";
					tu.executeQuery(sml_chartmap);
				}
			}

		}else{
			//Otherwise insert
			setTableName("SML_Application");
			setFieldKey("ObjId");
			setSignature(tu.parseInputValue(tagdata,"AppName"));
			appId=  getPrimaryKey();
			fieldvalues = appId+ ",'" + tu.parseInputValue(tagdata,"AppName") + "','" + tu.parseInputValue(tagdata,"Description") + "','"
					+ tu.parseInputValue(tagdata,"Status") + "','" + tu.parseInputValue(tagdata,"Version")+ "','"+
					tu.parseInputValue(tagdata,"DbName")+  "'";
			if (getDbType().equalsIgnoreCase("Oracle")){
				sql= "Insert into SML_Application( ObjId ,AppName ,Description,Status,Version,DbName,GenUser,GenDate" +
						" ) values(" + fieldvalues +",'"+adminuser+"',sysdate)";
			}else if (getDbType().equalsIgnoreCase("Mysql")){
				sql= "Insert into SML_Application( ObjId ,AppName ,Description,Status,Version,DbName,GenUser,GenDate" +
						" ) values(" +fieldvalues +",'"+adminuser+"',now())";
			}else if (getDbType().equalsIgnoreCase("Mssql")){
				sql= "Insert into SML_Application( ObjId ,AppName ,Description,Status,Version,DbName,GenUser,GenDate" +
						" ) values(" +fieldvalues +",'"+adminuser+"',getdate())";
			}

		}
		//After importing the whole schema file set CurrentStartTime=ModDate if the record is updated
		//Else set CurrentStartTime=GenDate if the record inserted
		//CurrentEndTime=sysdate when the import finished
		// Execute query
		String curgendate=(getDbType().equalsIgnoreCase("mysql")==true? tu.getVarchar2DateTime("mysql",false,"gendate","yyyy-mm-dd hh24:mi:ss",""):getDbType().equalsIgnoreCase("Oracle")==true? tu.getVarchar2DateTime("oracle",false,"gendate","yyyy-mm-dd hh24:mi:ss",""):tu.getVarchar2DateTime("mssql",false,"gendate","yyyy-mm-dd hh24:mi:ss",""));
		String sqlStartTime="Select ObjId,Version,"+CurrentStartTime+","+CurrentEndTime+","+curgendate+" from SML_Application where upper(AppName)=upper('"+tu.parseInputValue(tagdata,"AppName")+"')" +
				" and Version='" + tu.parseInputValue(tagdata,"Version")+"'";
		TemplateTable curappresult=tu.getResultSet(sqlStartTime);
		if(curappresult!=null&&curappresult.getRowCount()>0&&(fieldvalue==null||fieldvalue.equals(""))){
			String tmpdate=curappresult.getFieldValue("gendate",0);
			compdate=(getDbType().equalsIgnoreCase("mysql")==true?tu.getVarchar2DateTime("mysql",true,"CurrentStartTime","yyyy-mm-dd hh24:mi:ss",tmpdate):getDbType().equalsIgnoreCase("Oracle")==true? tu.getVarchar2DateTime("oracle",true,"CurrentStartTime","yyyy-mm-dd hh24:mi:ss",tmpdate):tu.getVarchar2DateTime("mssql",true,"CurrentStartTime","yyyy-mm-dd hh24:mi:ss",tmpdate));
		}else{
			compdate=(getDbType().equalsIgnoreCase("mysql")==true?"now()":getDbType().equalsIgnoreCase("Oracle")==true? "(sysdate-1)":"(getdate()-1)");
		}
		return(tu.executeQuery(sql));

	}
	private boolean importSemanticObject(Vector tagdata){
		String fieldvalues="";
		String sql="";
		setTableName("SML_DBObject");
		setFieldKey("ObjId");
		setSignature(tu.parseInputValue(tagdata,"ObjectName"));
		setObjectName(tu.parseInputValue(tagdata,"ObjectName"));
		tableId=  getPrimaryKey();

		fieldvalues = tableId+","+ appId + ",'" + tu.parseInputValue(tagdata,"ObjectName") + "','" + tu.parseInputValue(tagdata,"AliasName") + "','"
				+ tu.parseInputValue(tagdata,"ObjectType") + "','" +  tu.parseInputValue(tagdata,"ObjectFilter") + "','"
				+tu.parseInputValue(tagdata,"IsBaseline")+ "','"+
				tu.parseInputValue(tagdata,"Version")+ "','"+tu.parseInputValue(tagdata,"ObjectTypeNo")+ "','"+
				tu.parseInputValue(tagdata,"GroupAccess")+ "','"+tu.parseInputValue(tagdata,"AllowDelete")+ "','"+
				tu.parseInputValue(tagdata,"AllowNew")+ "','"+tu.parseInputValue(tagdata,"AllowSearch")+ "','"+
				tu.parseInputValue(tagdata,"Remark") + "'";
		if (getDbType().equalsIgnoreCase("Oracle")){
			sql= "Insert into SML_DbObject( ObjId ,DbObject2Application,ObjectName ,AliasName,ObjectType,ObjectFilter,IsBaseline,Version,ObjectTypeNo,GroupAccess,AllowDelete,AllowNew,AllowSearch,Remark,GenUser,GenDate" +
					" ) values(" + fieldvalues +",'"+adminuser+"',sysdate)";
		}else if (getDbType().equalsIgnoreCase("Mssql")){
			sql= "Insert into SML_DbObject( ObjId ,DbObject2Application,ObjectName ,AliasName,ObjectType,ObjectFilter,IsBaseline,Version,ObjectTypeNo,GroupAccess,AllowDelete,AllowNew,AllowSearch,Remark,GenUser,GenDate" +
					" ) values(" +fieldvalues +",'"+adminuser+"',getdate())";
		}else if (getDbType().equalsIgnoreCase("Mysql")){
			sql= "Insert into SML_DbObject( ObjId ,DbObject2Application,ObjectName ,AliasName,ObjectType,ObjectFilter,IsBaseline,Version,ObjectTypeNo,GroupAccess,AllowDelete,AllowNew,AllowSearch,Remark,GenUser,GenDate" +
					" ) values(" +fieldvalues +",'"+adminuser+"',now())";
		}
		//Log
		if(islog)
			getIOUtility().writeToFile(sql);
		// Execute query

		return(tu.executeQuery(sql));

	}

	private boolean importAttribute(Vector tagdata){
		String fieldvalues="";
		String DbAttribute2AttributeDomain="select objid from SML_AttributeDomain where DomainName='"+tu.parseInputValue(tagdata,"DomainName")+"'";
		String sql="Insert into SML_DbAttribute( ObjId ,AttributeName,AttrIndex,AliasName,Behavior,DomainName,DbObjectName,DbAttribute2DbObject,"+
				"DbAttribute2AttributeDomain,IsNull,DefaultValue,HasProperty,HasCodeObject,FieldType,IsViewField,SelectMandatory,"+
				"IsSearchField,IsBaseline,Version,Remark,GenUser,GenDate ) values(";
		setTableName("SML_DbAttribute");
		setFieldKey("ObjId");
		setSignature(tu.parseInputValue(tagdata,"AttributeName"));
		attrId=  getPrimaryKey();
		fieldvalues = attrId+ ",'" + tu.parseInputValue(tagdata,"AttributeName") + "'," + tu.parseInputValue(tagdata,"AttrIndex") + ",'"
				+ tu.parseInputValue(tagdata,"AliasName") + "','" + tu.parseInputValue(tagdata,"Behavior")+ "','"
				+ tu.parseInputValue(tagdata,"DomainName")+ "'," +"'"+getObjectName()+"',"+ tableId+ ","
				+ tu.getObjId(DbAttribute2AttributeDomain)+ ",'" + tu.parseInputValue(tagdata,"IsNull")+ "','"
				+ tu.parseInputValue(tagdata,"DefaultValue")+ "','" + tu.parseInputValue(tagdata,"HasProperty")+ "','"
				+ tu.parseInputValue(tagdata,"HasCodeObject")+ "','" + tu.parseInputValue(tagdata,"FieldType")+ "','"
				+ tu.parseInputValue(tagdata,"IsViewField")+ "','" + tu.parseInputValue(tagdata,"SelectMandatory")+ "','"
				+ tu.parseInputValue(tagdata,"IsSearchField")+ "','" + tu.parseInputValue(tagdata,"IsBaseline")+ "','"
				+ tu.parseInputValue(tagdata,"Version")+ "','"+ tu.parseInputValue(tagdata,"Remark") + "'";
		if (getDbType().equalsIgnoreCase("Oracle")){
			sql+= fieldvalues +",'"+adminuser+"',sysdate)";
		}else if (getDbType().equalsIgnoreCase("Mssql")){
			sql+= fieldvalues +",'"+adminuser+"',getdate())";
		}else if (getDbType().equalsIgnoreCase("Mysql")){
			sql+= fieldvalues +",'"+adminuser+"',now())";
		}
		//Log
		if(islog)
			getIOUtility().writeToFile(sql);
		// Execute query

		return(tu.executeQuery(sql));
	}
	private boolean importDomain(Vector tagdata){
		String fieldvalues="";
		String sql="Insert into SML_AttributeDomain( ObjId ,DomainName,OracleDataType,MssqlDataType,MysqlDataType,JavaDataType,DataSize,"+
				"DecimalSize,AttributeDomain2Application,IsBaseline,Version,Remark,GenUser,GenDate ) values(";
		setTableName("SML_AttributeDomain");
		setFieldKey("ObjId");
		setSignature(tu.parseInputValue(tagdata,"DomainName"));
		domainId=  getPrimaryKey();
		fieldvalues = domainId+ ",'" + tu.parseInputValue(tagdata,"DomainName").trim() + "','" + tu.parseInputValue(tagdata,"OracleDataType").trim() + "','"
				+ tu.parseInputValue(tagdata,"MssqlDataType") + "','" + tu.parseInputValue(tagdata,"MysqlDataType") + "','" + tu.parseInputValue(tagdata,"JavaDataType")+ "',"
				+ tu.parseInputValue(tagdata,"DataSize")+ "," + tu.parseInputValue(tagdata,"DecimalSize")+ ","+ appId+ ",'"
				+ tu.parseInputValue(tagdata,"IsBaseline")+ "','"
				+ tu.parseInputValue(tagdata,"Version")+ "','"  + tu.parseInputValue(tagdata,"Remark")+ "'";
		if (getDbType().equalsIgnoreCase("Oracle")){
			sql+= fieldvalues +",'"+adminuser+"',sysdate)";
		}else if (getDbType().equalsIgnoreCase("Mssql")){
			sql+= fieldvalues +",'"+adminuser+"',getdate())";
		}else if (getDbType().equalsIgnoreCase("Mysql")){
			sql+= fieldvalues +",'"+adminuser+"',now())";
		}
		//Log
		if(islog)
			getIOUtility().writeToFile(sql);
		// Execute query

		return(tu.executeQuery(sql));

	}

	private boolean importIndex(Vector tagdata){
		String fieldvalues="";
		String sql="Insert into SML_DbIndex( ObjId ,TableName,IndexName,Fields,IndexType,DbIndex2DbObject,"+
				"GenUser,GenDate ) values(";
		setTableName("SML_DbIndex");
		setFieldKey("ObjId");
		setSignature(tu.parseInputValue(tagdata,"IndexName"));
		indexId=  getPrimaryKey();
		fieldvalues = indexId+ ",'"+getObjectName()+ "','" + tu.parseInputValue(tagdata,"IndexName") + "','" + tu.parseInputValue(tagdata,"Fields") + "','"
				+ tu.parseInputValue(tagdata,"IndexType")+ "',"+tableId ;

		if (getDbType().equalsIgnoreCase("Oracle")){
			sql+= fieldvalues +",'"+adminuser+"',sysdate)";
		}else if (getDbType().equalsIgnoreCase("Mssql")){
			sql+= fieldvalues +",'"+adminuser+"',getdate())";
		}else if (getDbType().equalsIgnoreCase("Mysql")){
			sql+= fieldvalues +",'"+adminuser+"',now())";
		}
		//Log
		if(islog){
			getIOUtility().writeToFile(sql);
		}
		// Execute query

		return(tu.executeQuery(sql));
	}
	private boolean importRelation(Vector tagdata){
		String fieldvalues="";
		String sql="Insert into SML_AttributeRelation( ObjId ,RelationName,ParentTable,AttributeRelation2DbObject,RelationType,CurrentState,TabIndex,DefaultFilter,IsMandatory,"+
				"Remark,GenUser,GenDate ) values(";
		setTableName("SML_AttributeRelation");
		setFieldKey("ObjId");
		setSignature(tu.parseInputValue(tagdata,"RelationName"));
		relationId=  getPrimaryKey();
		fieldvalues = relationId+ ",'" + tu.parseInputValue(tagdata,"RelationName") + "','" + tu.parseInputValue(tagdata,"ParentTable") + "',"
				+tableId + ",'" + tu.parseInputValue(tagdata,"RelationType")+ "','"
				+ tu.parseInputValue(tagdata,"CurrentState")+ "',"+ tu.parseInputValue(tagdata,"TabIndex")+ ",'"
				+tu.parseInputValue(tagdata,"DefaultFilter")+ "','" + tu.parseInputValue(tagdata,"IsMandatory")+ "','" + tu.parseInputValue(tagdata,"Remark")+ "'";
		if (getDbType().equalsIgnoreCase("Oracle")){
			sql+= fieldvalues +",'"+adminuser+"',sysdate)";
		}else if (getDbType().equalsIgnoreCase("Mssql")){
			sql+= fieldvalues +",'"+adminuser+"',getdate())";
		}else if (getDbType().equalsIgnoreCase("Mysql")){
			sql+= fieldvalues +",'"+adminuser+"',now())";
		}
		//Log
		if(islog){
			getIOUtility().writeToFile(sql);
		}
		// Execute query
		//sql=tu.replaceStringWithPlus(sql,"\\","\\\'");
		return(tu.executeQuery(sql));
	}
	private boolean importFieldMap(Vector tagdata){
		String fieldvalues="";
		String sql="Insert into SML_FieldMap( ObjId ,RelationName,SourceObject,TargetObject,FieldMap2DbObject,SourceField,TargetField,MapType,Remark"+
				",GenUser,GenDate ) values(";
		setTableName("SML_FieldMap");
		setFieldKey("ObjId");
		setSignature(tu.parseInputValue(tagdata,"RelationName"));
		String fieldId=  getPrimaryKey();
		fieldvalues = fieldId+ ",'" + tu.parseInputValue(tagdata,"RelationName")+ "','" + tu.parseInputValue(tagdata,"SourceObject") + "','"
				+ tu.parseInputValue(tagdata,"TargetObject") + "',"
				+tableId + ",'" + tu.parseInputValue(tagdata,"SourceField")+ "','"
				+ tu.parseInputValue(tagdata,"TargetField")+ "','"+ tu.parseInputValue(tagdata,"MapType")+ "','"+ tu.parseInputValue(tagdata,"Remark")+ "'";
		if (getDbType().equalsIgnoreCase("Oracle")){
			sql+= fieldvalues +",'"+adminuser+"',sysdate)";
		}else if (getDbType().equalsIgnoreCase("Mssql")){
			sql+= fieldvalues +",'"+adminuser+"',getdate())";
		}else if (getDbType().equalsIgnoreCase("Mysql")){
			sql+= fieldvalues +",'"+adminuser+"',now())";
		}
		//Log
		if(islog){
			getIOUtility().writeToFile(sql);
		}
		// Execute query
		sql=tu.replaceStringWithPlus(sql,"\\","\\\'");
		return(tu.executeQuery(sql));
	}


	private boolean importChartMap(Vector tagdata){
	 
		String fieldvalues="";
		String sql="Insert into SML_ChartMap( ObjId ,TableName,ChartName,ChartAlias,X_Axis,Y_Axis,ChartType,SelectColumn,FieldNames,Captions,ChartDataType,IsDefault,Remark,FieldMap2DbObject"+
				",GenUser,GenDate ) values(";
		setTableName("SML_ChartMap");
		setFieldKey("ObjId");
		setSignature(tu.parseInputValue(tagdata,"TableName"));
		String fieldId=  getPrimaryKey();
		fieldvalues = fieldId+ ",'" 
				+ tu.parseInputValue(tagdata,"TableName")+ "','" 
				+ tu.parseInputValue(tagdata,"ChartName") + "','"
				+ tu.parseInputValue(tagdata,"ChartAlias")+ "','"
				+ tu.parseInputValue(tagdata,"X_Axis")+ "','"
				+ tu.parseInputValue(tagdata,"Y_Axis")+ "','" 
				+ tu.parseInputValue(tagdata,"ChartType") + "','"
				+ tu.parseInputValue(tagdata,"SelectColumn")+ "','"
				+ tu.parseInputValue(tagdata,"FieldNames")+ "','"
				+ tu.parseInputValue(tagdata,"Captions") + "','"
				+ tu.parseInputValue(tagdata,"ChartDataType")+ "','"
				+ tu.parseInputValue(tagdata,"IsDefault")+ "','"
				+ tu.parseInputValue(tagdata,"Remark")
				+ "'," +tableId ;
		if (getDbType().equalsIgnoreCase("Oracle")){
			sql+= fieldvalues +",'"+adminuser+"',sysdate)";
		}else if (getDbType().equalsIgnoreCase("Mssql")){
			sql+= fieldvalues +",'"+adminuser+"',getdate())";
		}else if (getDbType().equalsIgnoreCase("Mysql")){
			sql+= fieldvalues +",'"+adminuser+"',now())";
		}
		//Log
		if(islog){
			getIOUtility().writeToFile(sql);
		}
		// Execute query
		sql=tu.replaceStringWithPlus(sql,"\\","\\\'");
		return(tu.executeQuery(sql));
	}

	private boolean importCallerMap(Vector tagdata){
		String fieldvalues="";
		String sql="Insert into SML_CallerMap( ObjId ,CallerName,DbObjectName,AllowSkip,ViewToSearch,CallerButton,DisplayField,RecordOptions,PreviousObject,CallerMap2DbObject,NextObject,Remark"+
				",GenUser,GenDate ) values(";
		setTableName("SML_CallerMap");
		setFieldKey("ObjId");
		setSignature(tu.parseInputValue(tagdata,"DbObjectName"));
		String fieldId=  getPrimaryKey();
		fieldvalues = fieldId+ ",'" + tu.parseInputValue(tagdata,"CallerName")
				+ "','" + tu.parseInputValue(tagdata,"DbObjectName")
				+ "','" + tu.parseInputValue(tagdata,"AllowSkip") 
				+ "','" + tu.parseInputValue(tagdata,"ViewToSearch") 
				+ "','" + tu.parseInputValue(tagdata,"CallerButton") 
				+ "','" + tu.parseInputValue(tagdata,"DisplayField") 
				+ "','" + tu.parseInputValue(tagdata,"RecordOptions") 
				+ "','" + tu.parseInputValue(tagdata,"PreviousObject") 
				+ "'," +tableId 
				+ ",'" + tu.parseInputValue(tagdata,"NextObject")
				+ "','" + tu.parseInputValue(tagdata,"Remark")+ "'";
		if (getDbType().equalsIgnoreCase("Oracle")){
			sql+= fieldvalues +",'"+adminuser+"',sysdate)";
		}else if (getDbType().equalsIgnoreCase("Mssql")){
			sql+= fieldvalues +",'"+adminuser+"',getdate())";
		}else if (getDbType().equalsIgnoreCase("Mysql")){
			sql+= fieldvalues +",'"+adminuser+"',now())";
		}
		//Log
		if(islog){
			getIOUtility().writeToFile(sql);
		}
		// Execute query
		sql=tu.replaceStringWithPlus(sql,"\\","\\\'");
		return(tu.executeQuery(sql));
	}

	private boolean importValueMap(Vector tagdata){
		String fieldvalues="";
		String sql="Insert into SML_ValueMap( ObjId ,ObjectName,KeyFieldName,KeyFieldValue,TargetField,TargetValue,IsHidden,IsDisabled,valueMap2DbObject,Remark"+
				",GenUser,GenDate ) values(";
		setTableName("SML_ValueMap");
		setFieldKey("ObjId");
		setSignature(tu.parseInputValue(tagdata,"DbObjectName"));
		String fieldId=  getPrimaryKey();
		fieldvalues = fieldId+ ",'" + tu.parseInputValue(tagdata,"ObjectName")
				+ "','" + tu.parseInputValue(tagdata,"KeyFieldName")
				+ "','" + tu.parseInputValue(tagdata,"KeyFieldValue") 
				+ "','" + tu.parseInputValue(tagdata,"TargetField") 
				+ "','" + tu.parseInputValue(tagdata,"TargetValue") 
				+ "','" + tu.parseInputValue(tagdata,"IsHidden")              			
				+ "','" + tu.parseInputValue(tagdata,"IsDisabled") 
				+ "'," +tableId              			
				+ ",'" + tu.parseInputValue(tagdata,"Remark")+ "'";
		if (getDbType().equalsIgnoreCase("Oracle")){
			sql+= fieldvalues +",'"+adminuser+"',sysdate)";
		}else if (getDbType().equalsIgnoreCase("Mssql")){
			sql+= fieldvalues +",'"+adminuser+"',getdate())";
		}else if (getDbType().equalsIgnoreCase("Mysql")){
			sql+= fieldvalues +",'"+adminuser+"',now())";
		}         
		//Log
		if(islog){
			getIOUtility().writeToFile(sql);
		}
		// Execute query
		sql=tu.replaceStringWithPlus(sql,"\\","\\\'");
		return(tu.executeQuery(sql));
	}
	private boolean importConvertObject(Vector tagdata){
		String fieldvalues="";
		String ConvertObject2AttributeDomain="select objid from SML_AttributeDomain where DomainName='"+tu.parseInputValue(tagdata,"DomainName")+"'";
		String sql="Insert into SML_ConvertObject( ObjId ,Name,ConvertObject2AttributeDomain,ConvertFrom,ConvertTo,"+
				"QueryType,Format,Oracle,Mssql,Remark,GenUser,GenDate ) values(";
		setTableName("SML_AttributeRelation");
		setFieldKey("ObjId");
		setSignature(tu.parseInputValue(tagdata,"Name"));
		cobjId=  getPrimaryKey();
		fieldvalues = cobjId+ ",'" + tu.parseInputValue(tagdata,"Name") + "'," + tu.getObjId(ConvertObject2AttributeDomain) + ",'"
				+ tu.parseInputValue(tagdata,"ConvertFrom") + "','" + tu.parseInputValue(tagdata,"ConvertTo")+ "','"
				+ tu.parseInputValue(tagdata,"QueryType")+ "','" + tu.parseInputValue(tagdata,"Format")+ "','"
				+ tu.parseInputValue(tagdata,"Oracle")+ "','"
				+ tu.parseInputValue(tagdata,"Mssql")+ "','"  + tu.parseInputValue(tagdata,"Remark")+ "'";
		if (getDbType().equalsIgnoreCase("Oracle")){
			sql+= fieldvalues +",'"+adminuser+"',sysdate)";
		}else if (getDbType().equalsIgnoreCase("Mssql")){
			sql+= fieldvalues +",'"+adminuser+"',getdate())";
		}else if (getDbType().equalsIgnoreCase("Mysql")){
			sql+= fieldvalues +",'"+adminuser+"',now())";
		}
		//Log
		if(islog){
			getIOUtility().writeToFile(sql);
		}
		// Execute query

		return(tu.executeQuery(sql));
	}
	public boolean importListProperty(Vector tagdata){

		boolean retVal=false;
		String table=tu.parseInputValue(tagdata,"Object");
		String atrname=tu.parseInputValue(tagdata,"Attribute");
		// verify if the rule attribute  and rule object is same as previous value
		//if(!ruleobject.equals(table)&&!ruleattribute.equals(atrname)){
		ruleobject=table;
		ruleattribute=atrname;
		String objectsql="select do.ObjId ObjectId,do.objectname,da.ObjId AttributeId,da.attributename from  SML_DbObject do, SML_dbAttribute da where "+
				" do.ObjectName='"+table+"' and do.objid=da.dbAttribute2dbObject and da.Attributename='"+ atrname +"' ";
		TemplateTable result=tu.getResultSet(objectsql);
		if (getDbType().equalsIgnoreCase("Mysql")&& result!=null && result.getRowCount()>0){
			ruleobjid=result.getFieldValue(0,result.getRowCount()-1);
			ruleatrobjid=result.getFieldValue(2,result.getRowCount()-1);
		}else

			if(result!=null && result.getRowCount()>0){                	
				ruleobjid=result.getFieldValue("ObjectId",result.getRowCount()-1);
				ruleatrobjid=result.getFieldValue("AttributeId",result.getRowCount()-1);
			}else{
				ruleobjid="";
				ruleatrobjid="";
			}
		//}
		setTableName("Table_ListProperty");
		setFieldKey("ObjId");
		setSignature(tu.parseInputValue(tagdata,"Object"));
		listpropertyid=  getPrimaryKey();
		String sql="insert into Table_ListProperty(objid,name,TableName,PropertyString,"+
				" PropertyValue,Scope,PropIndex,ListProperty2Object,ListProperty2Attribute,GroupUser,GenUser,GenDate)" +
				"values("+listpropertyid+",'"+tu.parseInputValue(tagdata,"Attribute")+"','"+tu.parseInputValue(tagdata,"Object")+"','"+
				tu.parseInputValue(tagdata,"PropertyString")+"','"+tu.parseInputValue(tagdata,"PropertyValue")+
				"','"+tu.parseInputValue(tagdata,"Scope")+"',"+tu.parseInputValue(tagdata,"PropIndex")+",'"+
				ruleobjid+"','"+ruleatrobjid;

		if (getDbType().equalsIgnoreCase("Oracle")){
			sql+= "','"+adminuser+"','"+adminuser+"',sysdate)";
		}else if (getDbType().equalsIgnoreCase("Mssql")){
			sql+= ",'"+adminuser+"','"+adminuser+"',getdate())";
		}else if (getDbType().equalsIgnoreCase("Mysql")){
			sql+= ",'"+adminuser+"','"+adminuser+"',now())";
		}
		//Log
		if(islog){
			getIOUtility().writeToFile(sql);
		}
		// Execute query

		return(tu.executeQuery(sql));
	}

	public boolean importGenericCode(Vector tagdata){

		boolean retVal=false;
		String Name=tu.parseInputValue(tagdata,"Name");
		String AttributeName=tu.parseInputValue(tagdata,"AttributeName");
		String CodeValue=tu.parseInputValue(tagdata,"CodeValue");
		String Description=tu.parseInputValue(tagdata,"Description");
		String Status=tu.parseInputValue(tagdata,"Status");
		String CodeIndex=tu.parseInputValue(tagdata,"CodeIndex");

		// verify if the rule attribute  and rule object is same as previous value
		//if(!ruleobject.equals(table)&&!ruleattribute.equals(atrname)){

		String objectsql="select *from table_genericcode where "+(getDbType().equalsIgnoreCase("Oracle")? "upper(name)='":"upper(name)='" )+
				Name.toUpperCase()+"' and upper(CodeValue)='"+ CodeValue.toUpperCase()+"'";
		TemplateTable result=tu.getResultSet(objectsql);
		if(result!=null && result.getRowCount()>0)
			return(true);


		setTableName("Table_GenericCode");
		setFieldKey("ObjId");
		setSignature(Name);
		String objid=  getPrimaryKey();
		codeattributeid=codeattribute.equals(AttributeName)?codeattributeid: "0";
		if ( codeattributeid.equals("0")){

			TemplateTable codeattr=tu.getResultSet("select ObjId from table_codeattribute where upper(name)=upper('"+AttributeName+"')" );
			if (codeattr.getRowCount()>0){
				codeattribute=AttributeName;
				codeattributeid=codeattr.getFieldValue("ObjId",0);
			}
		}
		String sql="insert into Table_GenericCode(ObjId,Name,AttributeName,CodeValue,Description,"+
				"Status,CodeIndex,GenericCode2CodeAttribute,GroupUser,GenUser,GenDate)" +
				"values("+objid+",'"+Name+"','"+AttributeName+"','"+CodeValue+"','"+Description+"','"+
				Status+"','"+CodeIndex+"','"+codeattributeid;

		if (getDbType().equalsIgnoreCase("Oracle")){
			sql+= "','"+adminuser+"','"+adminuser+"',sysdate)";
		}else if (getDbType().equalsIgnoreCase("Mssql")){
			sql+= ",'"+adminuser+"','"+adminuser+"',getdate())";
		}else if (getDbType().equalsIgnoreCase("Mysql")){
			sql+= ",'"+adminuser+"','"+adminuser+"',now())";
		}
		//Log
		if(islog){
			getIOUtility().writeToFile(sql);
		}
		// Execute query

		return(!codeattributeid.equals("0")? tu.executeQuery(sql):false);
	}
	public boolean importObjectRule(Vector tagdata){

		boolean retVal=false;
		String sql="";
		String Name=tu.parseInputValue(tagdata,"Name");
		String TableName=tu.parseInputValue(tagdata,"TableName");
		String EffectedTable=tu.parseInputValue(tagdata,"EffectedTable");
		String Description=tu.parseInputValue(tagdata,"Description");
		String Reason=tu.parseInputValue(tagdata,"Reason");
		String ActionState=tu.parseInputValue(tagdata,"ActionState");
		String Condition=tu.parseInputValue(tagdata,"Condition");
		String RuleIndex=tu.parseInputValue(tagdata,"RuleIndex");
		String Status=tu.parseInputValue(tagdata,"Status");
		String tsql="select objid from table_object where upper(name)=upper('"+TableName+"')";
		TemplateTable tdata=tu.getResultSet(tsql);
		String tableid=tdata.getRowCount()>0? tdata.getFieldValue(0,tdata.getRowCount()-1):"0";
		//String tableid=tdata.getRowCount()>0? tdata.getFieldValue("objid",tdata.getRowCount()-1):"0";
		objectruleid="";
		if(!tableid.equals("")){
			setTableName("Table_ObjectRule");
			setFieldKey("ObjId");
			setSignature(tu.parseInputValue(tagdata,"Name"));
			objectruleid=  getPrimaryKey();
			sql="insert into Table_ObjectRule(ObjId,Name,TableName,EffectedTable,Description,Reason,ActionState,Condition,RuleIndex,Status,ObjectRule2Object,GroupUser,GenUser,GenDate)" +
					"values("+objectruleid+",'"+Name+"','"+TableName+"','"+EffectedTable+"','"+Description+"','"+Reason+"','"+ActionState+"','"+Condition+"','"+RuleIndex+"','"+Status + "','"+tableid;

			if (getDbType().equalsIgnoreCase("Oracle")){
				sql+= "','"+adminuser+"','"+adminuser+"',sysdate)";
			}else if (getDbType().equalsIgnoreCase("Mssql")){
				sql+= ",'"+adminuser+"','"+adminuser+"',getdate())";
			}else if (getDbType().equalsIgnoreCase("Mysql")){
				sql+= ",'"+adminuser+"','"+adminuser+"',now())";
			}
			//Log
			if(islog){
				getIOUtility().writeToFile(sql);
			}
			// Execute query
		}
		return(tableid.equals("")?false: tu.executeQuery(sql));
	}
	public boolean importActionQuery(Vector tagdata){

		boolean retVal=false;
		String sql="";
		if(!objectruleid.equals("")){
			String Name=tu.parseInputValue(tagdata,"Name");
			String TableName=tu.parseInputValue(tagdata,"TableName");
			String EffectedTable=tu.parseInputValue(tagdata,"EffectedTable");
			String Description=tu.parseInputValue(tagdata,"Description");
			String StepNo=tu.parseInputValue(tagdata,"StepNo");
			String Input=tu.parseInputValue(tagdata,"Input");
			String InputDataType=tu.parseInputValue(tagdata,"InputDataType");
			String Output=tu.parseInputValue(tagdata,"Output");
			String QueryType=tu.parseInputValue(tagdata,"QueryType");
			String HasRecordSet=tu.parseInputValue(tagdata,"HasRecordSet");
			String OracleQuery=tu.parseInputValue(tagdata,"OracleQuery");
			String MssqlQuery=tu.parseInputValue(tagdata,"MssqlQuery");
			String Status=tu.parseInputValue(tagdata,"Status");

			setTableName("Table_ActionQuery");
			setFieldKey("ObjId");
			setSignature(tu.parseInputValue(tagdata,"Name"));
			String objid=  getPrimaryKey();
			sql="insert into Table_ActionQuery(ObjId,Name,TableName,Description,StepNo,Input,InputDataType,Output,QueryType,HasRecordSet,OracleQuery,MssqlQuery,Status,ActionQuery2ObjectRule,GroupUser,GenUser,GenDate)" +
					"values("+objid+",'"+Name+"','"+TableName+"','"+Description+"','"+StepNo+"','"+Input+"','"+(InputDataType==null?" ":InputDataType)+"','"+Output+"','"+QueryType+"','"+HasRecordSet
					+"','"+OracleQuery+"','"+MssqlQuery+"','"+Status +"',"+objectruleid;

			if (getDbType().equalsIgnoreCase("Oracle")){
				sql+= ",'"+adminuser+"','"+adminuser+"',sysdate)";
			}else if (getDbType().equalsIgnoreCase("Mssql")){
				sql+= ",'"+adminuser+"','"+adminuser+"',getdate())";
			}else if (getDbType().equalsIgnoreCase("Mysql")){
				sql+= ",'"+adminuser+"','"+adminuser+"',now())";
			}
			//Log
			if(islog){
				getIOUtility().writeToFile(sql);
			}
		}
		// Execute query
		//format sql
		sql=tu.replaceStringWithPlus(sql,"\\","\\\'");

		return(!objectruleid.equals("")? tu.executeQuery(sql):false);
	}

	public boolean createGenericCodeRuleObject(){

		boolean retVal=false;

		// verify if the rule attribute  and rule object is same as previous value
		//if(!ruleobject.equals(table)&&!ruleattribute.equals(atrname)){
		//Delete code attribute
		tu.executeQuery("delete from table_listproperty");
		tu.executeQuery("delete from table_codeattribute");

		String objectsql="select do.ObjId ObjectId,do.objectname,da.ObjId AttributeId,da.attributename from  SML_DbObject do, SML_dbAttribute da where "+
				" do.ObjectName='GenericCode' and do.objid=da.dbAttribute2dbObject and da.Attributename='AttributeName' ";
		TemplateTable result=tu.getResultSet(objectsql);
		if(result!=null && result.getRowCount()>0){
			//ruleobjid=result.getFieldValue("ObjectId",0);
			//ruleatrobjid=result.getFieldValue("AttributeId",0);
			ruleobjid=result.getFieldValue(0,0);
			ruleatrobjid=result.getFieldValue(2,0);
		}else{
			ruleobjid="";
			ruleatrobjid="";
		}
		String asql="select distinct attributename from sml_dbAttribute where upper(HasCodeObject)='YES' and DomainName='Code_t' and "+
				" attributename not in ('ProjectCode','MainCode','MainJobCode','SubCode','SubJobCode')"; //,'TaskCode','TaskJobCode'
		TemplateTable attrname = tu.getResultSet(asql);
		setTableName("Table_ListProperty");
		setFieldKey("ObjId");
		if(attrname!=null && attrname.getRowCount()>0){
			retVal=true;
			for( int i=0; i<attrname.getRowCount(); i++){
				setSignature(attrname.getFieldValue("AttributeName",i));
				String objid=  getPrimaryKey();
				String codeattrsql="insert into table_codeattribute(objid,name,purpose,groupuser,genuser,gendate) values("+
						objid+",'"+attrname.getFieldValue("AttributeName",i)+"','Please specify'";
				String sql="insert into Table_ListProperty(objid,name,PropertyString,"+
						" PropertyValue,Scope,PropIndex,ListProperty2Object,ListProperty2Attribute,GroupUser,GenUser,GenDate)" +
						"values("+objid+",'GenericCode','"+
						attrname.getFieldValue("AttributeName",i)+"','"+attrname.getFieldValue("AttributeName",i)+
						"','global',"+i+",'"+
						ruleobjid+"','"+ruleatrobjid;

				if (getDbType().equalsIgnoreCase("Oracle")){
					sql+= "','"+adminuser+"','"+adminuser+"',sysdate)";
					codeattrsql+=",'"+adminuser+"','"+adminuser+"',sysdate)";
				}else if (getDbType().equalsIgnoreCase("Mssql")){
					sql+= ",'"+adminuser+"','"+adminuser+"',getdate())";
					codeattrsql+= ",'"+adminuser+"','"+adminuser+"',getdate())";
				}else if (getDbType().equalsIgnoreCase("Mysql")){
					sql+= ",'"+adminuser+"','"+adminuser+"',now())";
					codeattrsql+= ",'"+adminuser+"','"+adminuser+"',now())";
				}
				tu.executeQuery(sql);
				tu.executeQuery(codeattrsql);

				//Log
				if(islog){
					getIOUtility().writeToFile(sql);
				}
			}
		}

		// Execute query

		return(retVal);
	}
	public boolean importAppData(Vector tagdata){

		boolean retVal=false;
		String objectsql="select ObjId,name from the SML_DbObject";

		String indexsql="select ObjId, name,type,fields from SML_DbIndex where DbIndex2DbObject=";
		//Get the list of the object to be compiled

		//Get the list of indexes for the object

		//Create the object or table

		//Create the indexes
		return(retVal);
	}
	public boolean createallSequences(){
		TemplateTable tables=tu.getResultSet("Select * from sml_dbobject where upper(objecttype)='TABLE'");
		//TemplateTable tables=tu.getResultSet("Select * from sml_dbobject");

		for (int row=0;row<tables.getRowCount(); row++){
			String objectname=tables.getFieldValue("ObjectName", row);
			String maxid="1";
			TemplateTable maxval=tu.getResultSet("Select nvl(max(objid),0)+1 as maxobjid from table_"+objectname);
			if(maxval.getRowCount()>0){
				maxid=maxval.getFieldValue("maxobjid", maxval.getRowCount()-1);
			}
			//Create a sequence no for table
			if(!objectname.equals("")){
				//drop sequence if exists for this table
				String cmd="drop sequence "+objectname+"_seq";
				String create="create sequence "+objectname+"_seq start with "+maxid+" increment by 1 nocache";
				getIOUtility().writeToFile(cmd+";");        			
				getIOUtility().writeToFile(create+";");
				tu.executeQuery("drop sequence "+objectname+"_seq");
				tu.executeQuery("create sequence "+objectname+"_seq start with "+maxid+" increment by 1 nocache");
			}
		}
		return true;
	}
	public boolean updateApplication(){
		//After importing the whole schema file set CurrentStartTime=ModDate if the record is updated
		//Else set CurrentStartTime=GenDate if the record inserted
		//CurrentEndTime=sysdate when the import finished
		String CurrentStartTime="";
		String sql="";
		String GenDate=tu.getConvertDate(getDbType(),"select","GenDate","yyyy-mm-dd hh24:mi:ss","GenDate");
		String ModDate=tu.getConvertDate(getDbType(),"select","ModDate","yyyy-mm-dd hh24:mi:ss","ModDate");
		// Verify if the object record exist
		//If record exists  than update the record and set LastStartTime=CurrentStartTime
		//LastEndTime=CurrentEndTime
		String verifyapp="Select ObjId,"+GenDate+","+ModDate+" from SML_Application where ObjId="+appId;
		TemplateTable appresult=tu.getResultSet(verifyapp);
		if(appresult.getRowCount()>0 &&!appresult.getFieldValue("ObjId",0).equals("")){
			if(SchemaMode.equalsIgnoreCase("upgrade"))
				CurrentStartTime=tu.getConvertDate(getDbType(),"update","ModDate","yyyy-mm-dd hh24:mi:ss",appresult.getFieldValue("ModDate",0));
			if(SchemaMode.equalsIgnoreCase("new"))
				CurrentStartTime=tu.getConvertDate(getDbType(),"update","GenDate","yyyy-mm-dd hh24:mi:ss",appresult.getFieldValue("GenDate",0));
			sql="update SML_Application set CurrentStartTime="+CurrentStartTime +",DbName='"+getDbInstance()+ "',CurrentEndTime="+
					(getDbType().equalsIgnoreCase("mysql")==true?"now()":getDbType().equalsIgnoreCase("Oracle")==true? "sysdate" : "getdate()" ) +" where ObjId="+appId;

		}
		//Log
		if(islog){
			getIOUtility().writeToFile(sql);
		}
		// Execute query

		return(tu.executeQuery(sql));
	}
	public boolean compileSchema(){

		boolean retVal=false;
		TemplateTable objectlist;
		TemplateTable attrtable;
		TemplateTable relation;
		TemplateTable index;
		String objectsql;
		//if oarcle database change the nls_date
		/* if(getDbType().equalsIgnoreCase("Oracle"))
            tu.executeQuery("ALTER SESSION SET NLS_DATE_FORMAT = 'yyyy-mm-dd hh24:mi:ss'");
		 */
		if(getImportTable()!=null &&!getImportTable().equals("")){
			objectsql="select ObjId,ObjectName from SML_DbObject where upper(ObjectType)=upper('Table') and upper(ObjectName)=upper('"+getImportTable()+"')";
		}else{
			objectsql="select ObjId,ObjectName from SML_DbObject where upper(ObjectType)=upper('Table') order by "+(getDbType().equalsIgnoreCase("mysql")==true?"now()":getDbType().equalsIgnoreCase("Oracle")==true? "sysdate" : "getdate()" );
		}
			gendate=(getDbType().equalsIgnoreCase("mysql")==true? tu.getVarchar2DateTime("mysql",true,"da.gendate","yyyy-mm-dd hh24:mi:ss",""):getDbType().equalsIgnoreCase("Oracle")==true? tu.getVarchar2DateTime("oracle",true,"da.gendate","yyyy-mm-dd hh24:mi:ss",""):tu.getVarchar2DateTime("mssql",true,"da.gendate","yyyy-mm-dd hh24:mi:ss",""));
		
		String attributesql="select da.ObjId,da.DbAttribute2DbObject,da.AttributeName,da.AttrIndex,da.Behavior,da.DomainName, " +
				" da.IsNull,da.DefaultValue,da.IsViewField,da.IsBaseline,da.Version, " +
				" dd.OracleDataType,dd.MssqlDataType,dd.MysqlDataType,dd.DataSize,dd.DecimalSize " +
				" from SML_DBAttribute da ,SML_AttributeDomain dd where "+ gendate+ (getDbType().equalsIgnoreCase("mysql")==true? "<":">")+ (tu.isEmptyValue(compdate)?"="+gendate:compdate) +" and da.DbAttribute2AttributeDomain= dd.ObjId " +
				" and upper(da.IsViewField)='NO' and da.DbAttribute2DbObject=" ;
		gendate=(getDbType().equalsIgnoreCase("mysql")==true? tu.getVarchar2DateTime("mysql",true,"ar.gendate","yyyy-mm-dd hh24:mi:ss",""):getDbType().equalsIgnoreCase("Oracle")==true? tu.getVarchar2DateTime("oracle",true,"ar.gendate","yyyy-mm-dd hh24:mi:ss",""):tu.getVarchar2DateTime("mssql",true,"ar.gendate","yyyy-mm-dd hh24:mi:ss",""));
		String relationsql="select ar.ObjId,ar.RelationName,ar.ParentTable,ar.RelationType,ar.CurrentState,ar.TabIndex,ar.DefaultFilter,ar.IsMandatory,do.ObjectName "+
				" from SML_AttributeRelation ar,SML_DbObject do where " + gendate+(getDbType().equalsIgnoreCase("mysql")==true? "<":">")+(tu.isEmptyValue(compdate)?"="+gendate:compdate) +" and  ar.AttributeRelation2DbObject=do.ObjId and do.ObjId=";
		gendate=(getDbType().equalsIgnoreCase("mysql")==true? tu.getVarchar2DateTime("mysql",true,"di.gendate","yyyy-mm-dd hh24:mi:ss",""):getDbType().equalsIgnoreCase("Oracle")==true? tu.getVarchar2DateTime("oracle",true,"di.gendate","yyyy-mm-dd hh24:mi:ss",""):tu.getVarchar2DateTime("mssql",true,"di.gendate","yyyy-mm-dd hh24:mi:ss",""));
		String indexsql="select di.ObjId, di.IndexName,di.Fields,di.IndexType,do.ObjectName from "+
				"SML_DbIndex di,SML_DbObject do where " + gendate+(getDbType().equalsIgnoreCase("mysql")==true? "<":">")+(tu.isEmptyValue(compdate)?"="+gendate:compdate) +" and di.DbIndex2DbObject=do.ObjId and do.ObjId=";
		//Get the list of the object to be compiled
		objectlist=tu.getResultSet(objectsql);
		if(objectlist!=null){
			for (int trow=0; trow<objectlist.getRowCount(); trow++){
				StringBuffer buffer =	new StringBuffer();
				//First drop the object if exist
				try{
					String dropsql="drop table Table_"+objectlist.getFieldValue("ObjectName",trow);

					tu.executeQuery(dropsql);

					//drop sequence if exists for this table
					//tu.executeQuery("drop sequence "+objectlist.getFieldValue("ObjectName",trow)+"_seq");
				}catch (Exception e){
				}
				//Get attribute list

				relation=tu.getResultSet(relationsql+"'"+objectlist.getFieldValue("ObjId",trow)+"'");
				attrtable= tu.getResultSet(attributesql+"'"+objectlist.getFieldValue("ObjId",trow)+ "' Order By da.AttrIndex");
				String objectname=objectlist.getFieldValue("ObjectName",trow);
				buffer.append("Create Table Table_" +objectname + "( \n ObjId  "+(getDbType().equalsIgnoreCase("Oracle")==true? "raw(16) NOT NULL,":"int NOT NULL auto_increment,"));
				//Add Attributes here
				for(int atrrow=0; atrrow<attrtable.getRowCount(); atrrow++) {

					//verify the field behavior "Semantic" or "custom"
					//If the field is a custom field will be appended by "x_"
					String ColumnName=attrtable.getFieldValue("IsBaseline",atrrow).equalsIgnoreCase("Yes")==true ? attrtable.getFieldValue("AttributeName",atrrow):("x_"+attrtable.getFieldValue("AttributeName",atrrow));

					buffer.append("\n " + ColumnName + "   " +
							(getDbType().equalsIgnoreCase("Mysql")==true ?attrtable.getFieldValue("MysqlDataType",atrrow):getDbType().equalsIgnoreCase("Oracle")==true ?attrtable.getFieldValue("OracleDataType",atrrow) : attrtable.getFieldValue("MssqlDataType",atrrow)));
					buffer.append( attrtable.getFieldValue("IsNull",atrrow).equalsIgnoreCase("Yes")==true ? " NULL," : " NOT NULL," );

				}
				//Add relation fields here
				for(int relrow=0; relrow<relation.getRowCount(); relrow++) {
					//verify the field behavior "Semantic" or "custom"
					//If the field is a custom field will be appended by "x_"

					//Verify if the relation is MTM then create seperate table else add a column to the current table
					if(relation.getFieldValue("RelationType",relrow).equalsIgnoreCase("MTM")){
						String tname=relation.getFieldValue("RelationName",relrow);
						int attrlen=tname.indexOf("2");
						tu.executeQuery("drop table "+tname);
						String mtmcreate="create table "+tname +"(\n" + tname.substring(0,attrlen)+"Id "+(getDbType().equalsIgnoreCase("Oracle")?" raw(16)":"int")+" NOT NULL,\n"
								+tname.substring(attrlen+1)+"Id "+(getDbType().equalsIgnoreCase("Oracle")?" raw(16)":"int")+" NOT NULL)";
						tu.executeQuery(mtmcreate);
						String createindx="create unique index "+tname +" on "+ tname+"("+ tname.substring(0,attrlen)+"Id,"+ tname.substring(attrlen+1)+"Id)";
						tu.executeQuery(createindx);
						tu.executeQuery("insert into "+tname+" values(0,0)");
						//Beangen should disable AddPopUpTextBox(..) related to this relationfiled such that user can not change this field value
						String ColumnName=relation.getFieldValue("RelationName",relrow);
						buffer.append("\n " + ColumnName + (relation.getFieldValue("IsMandatory",relrow).equalsIgnoreCase("yes")?  (getDbType().equalsIgnoreCase("Oracle")==true? " raw(16)":" int")+"  NOT NULL," :(getDbType().equalsIgnoreCase("Oracle")==true? " raw(16)":" int")+ "   NULL," ));

					}else{
						String ColumnName=relation.getFieldValue("RelationName",relrow);
						buffer.append("\n " + ColumnName + (relation.getFieldValue("IsMandatory",relrow).equalsIgnoreCase("yes")?  (getDbType().equalsIgnoreCase("Oracle")==true? " raw(16)":" int")+"  NOT NULL," :(getDbType().equalsIgnoreCase("Oracle")==true? " raw(16)":" int")+ "   NULL," ));
					}
				}

				//Find all relation related to this Object
				if (getDbType().equalsIgnoreCase("Oracle"))
					buffer.append(
							"\n BQN Varchar2 (20) NULL ,"+
							"\n OriginId raw(16) NULL ,"+
							"\n DestinitionId raw(16) NULL ,"+
							"\n GroupUser Varchar2 (100) NULL ,"+
							"\n GenUser Varchar2 (100) NULL ,"+
							"\n GenDate DATE NULL ,"+
							"\n ModUser Varchar2 (100) NULL ,"+
							"\n ModDate DATE NULL )");

				else if (getDbType().equalsIgnoreCase("mysql"))
					buffer.append(
							"\n BQN Varchar2 (20) NULL ,"+
							"\n OriginId int NULL ,"+
							"\n DestinitionId int NULL ,"+
							"\n GroupUser Varchar (100) NULL ,"+
							"\n GenUser Varchar (50) NULL ,"+
							"\n GenDate Datetime NULL ,"+
							"\n ModUser Varchar (50) NULL ,"+
							"\n ModDate Datetime NULL," +
							"\n PRIMARY KEY  (`objid`) )");
				else
					buffer.append(
							"\n BQN Varchar2 (20) NULL ,"+
							"\n OriginId int NULL ,"+
							"\n DestinitionId int NULL ,"+
							"\n GroupUser Varchar (100) NULL ,"+
							"\n GenUser Varchar (50) NULL ,"+
							"\n GenDate Datetime NULL ,"+
							"\n ModUser Varchar (50) NULL ,"+
							"\n ModDate Datetime NULL )");

				// tu.getResultSet(buffer.toString());
				try{
					tu.getResultSet(buffer.toString());
				}catch (Exception e){
					if(islog){
						getIOUtility().writeToFile(e.getMessage());
						getIOUtility().writeToFile(buffer.toString());
					}
				}
				//Create a sequence no for table
				//tu.executeQuery("create sequence "+objectname+"_seq start with 1 increment by 1 nocache");

				// create groupuser index
				String gsql="create Index "+ objectname +"_GrpUsr on Table_"+objectname+"(GroupUser,BQN)";
				tu.getResultSet(gsql);
				//Log
				if(islog){
					getIOUtility().writeToFile(buffer.toString());
				}
				//Get the list of indexes for the object
				index=tu.getResultSet(indexsql+"'"+objectlist.getFieldValue("ObjId",trow)+"'");
				//Add Index for the current database Object here
				for(int idrow=0; idrow<index.getRowCount(); idrow++) {

					String IndexName=index.getFieldValue("IndexName",idrow);
					String IndexType=(index.getFieldValue("IndexType",idrow).equalsIgnoreCase("Unique")==true? "unique":" ");
					String Fields=index.getFieldValue("Fields",idrow);
					String ObjectName=index.getFieldValue("ObjectName",idrow);
					/*try{
                    String dropindx="drop index "+ IndexName;
                    tu.executeQuery(dropindx);
                  }catch (Exception e){
                  }
					 */
					String idsql="";
					if(getDbType().equalsIgnoreCase("mysql") && !Fields.equalsIgnoreCase("objid") ){
						idsql="create "+IndexType+ " Index "+ IndexName +" on Table_"+ObjectName+"("+Fields+")";
						tu.getResultSet(idsql);
					}else if(!getDbType().equalsIgnoreCase("mysql")){
						idsql="create "+IndexType+ " Index "+ IndexName +" on Table_"+ObjectName+"("+Fields+")";
						tu.getResultSet(idsql);
					}

					//Log
					if(islog){
						getIOUtility().writeToFile(idsql);
					}
					if (getDbType().equalsIgnoreCase("Oracle")){

					}
				}
				//Create the object or table

				//Create the indexes

			}
		}
		//Add admin user
		TemplateTable priv=tu.getResultSet("select *from table_PrivilegeGroup");
		if(priv.getRowCount()==0){
			String privid=this.getPrimaryKey();
			String strpriv="insert into table_PrivilegeGroup(ObjId,Name,Scope,Status,GenUser)values("+privid+",'administrator','0','1','"+adminuser+"')" ;
			tu.executeQuery(strpriv);
		
		
		String stradminuser= "insert into table_user(ObjId,Name,LastName,LoginName,Password,VerifyPassword,email,UserType,User2PrivilegeGroup,User2Company,User2Supplier,Status,GroupUser,GenUser,GenDate)values("+
				this.getPrimaryKey()+",'Administrator','Admin','"+adminuser+"','sa$784','sa$784','admin@biderp.com','1',"+privid+",'1','1','1','"+adminuser+"','"+adminuser+"',sysdate)";
		tu.executeQuery(stradminuser);
		
		String registration= "insert into table_user(ObjId,Name,LastName,LoginName,Password,VerifyPassword,email,UserType,User2PrivilegeGroup,User2Company,User2Supplier,Status,GroupUser,GenUser,Gendate)values("+
				this.getPrimaryKey()+",'registration','registration','registration','reg$56*123','reg$56*123','registration@biderp.com','1',"+privid+",'1','1','1','"+adminuser+"','"+adminuser+"',sysdate)";
		tu.executeQuery(registration);

			//Log
			if(islog){
				getIOUtility().writeToFile(strpriv);
				getIOUtility().writeToFile(stradminuser);
				getIOUtility().writeToFile(registration);
			}
		}
		return(retVal);
	}
	public boolean compileView(){

		boolean retVal=false;
		TemplateTable objectlist;
		TemplateTable attrtable;
		TemplateTable relation;
		TemplateTable index;
		String objectsql;

		//if oarcle database change the nls_date
		/* if(getDbType().equalsIgnoreCase("Oracle"))
            tu.executeQuery("ALTER SESSION SET NLS_DATE_FORMAT = 'yyyy-mm-dd hh24:mi:ss'");
		 */
		if(getImportTable()!=null &&!getImportTable().equals(""))
			objectsql="select ObjId,ObjectName from SML_DbObject where upper(ObjectType)=upper('View') and upper(ObjectName)=upper('"+getImportTable()+"')";
		else
			objectsql="select ObjId,ObjectName from SML_DbObject where upper(ObjectType)=upper('View')  order by ObjectTypeNo ";
		gendate=(getDbType().equalsIgnoreCase("mysql")==true? tu.getVarchar2DateTime("mysql",true,"da.gendate","yyyy-mm-dd hh24:mi:ss",""):getDbType().equalsIgnoreCase("Oracle")==true? tu.getVarchar2DateTime("oracle",true,"da.gendate","yyyy-mm-dd hh24:mi:ss",""):tu.getVarchar2DateTime("mssql",true,"da.gendate","yyyy-mm-dd hh24:mi:ss",""));
		String attributesql="select da.ObjId,da.DbAttribute2DbObject,da.AttributeName,da.AttrIndex,da.Behavior,da.DomainName, " +
				" da.IsNull,da.DefaultValue,da.FieldType,da.IsViewField,da.IsBaseline,da.Version, " +
				" dd.OracleDataType,dd.MssqlDataType,dd.DataSize,dd.DecimalSize " +
				" from SML_DBAttribute da ,SML_AttributeDomain dd where "+ gendate+(getDbType().equalsIgnoreCase("mysql")==true? "<":">")+(tu.isEmptyValue(compdate)?"="+gendate:compdate) +" and da.DbAttribute2AttributeDomain= dd.ObjId " +
				" and da.DbAttribute2DbObject=" ;
		gendate=(getDbType().equalsIgnoreCase("mysql")==true? tu.getVarchar2DateTime("mysql",true,"ar.gendate","yyyy-mm-dd hh24:mi:ss",""):getDbType().equalsIgnoreCase("Oracle")==true? tu.getVarchar2DateTime("oracle",true,"ar.gendate","yyyy-mm-dd hh24:mi:ss",""):tu.getVarchar2DateTime("mssql",true,"ar.gendate","yyyy-mm-dd hh24:mi:ss",""));
		String relationsql="select ar.ObjId,ar.RelationName,ar.ParentTable,ar.RelationType,ar.CurrentState,ar.TabIndex,ar.DefaultFilter,ar.IsMandatory,ar.Remark,do.ObjectName "+
				" from SML_AttributeRelation ar,SML_DbObject do where " + gendate+(getDbType().equalsIgnoreCase("mysql")==true? "<":">")+(tu.isEmptyValue(compdate)?"="+gendate:compdate) +" and  ar.AttributeRelation2DbObject=do.ObjId and do.ObjId=";
		gendate=(getDbType().equalsIgnoreCase("mysql")==true? tu.getVarchar2DateTime("mysql",true,"di.gendate","yyyy-mm-dd hh24:mi:ss",""):getDbType().equalsIgnoreCase("Oracle")==true? tu.getVarchar2DateTime("oracle",true,"di.gendate","yyyy-mm-dd hh24:mi:ss",""):tu.getVarchar2DateTime("mssql",true,"di.gendate","yyyy-mm-dd hh24:mi:ss",""));

		//Get the list of the object to be compiled
		objectlist=tu.getResultSet(objectsql);
		if(objectlist!=null){
			for (int trow=0; trow<objectlist.getRowCount(); trow++){
				StringBuffer buffer =	new StringBuffer();
				String fromtable="";
				String whereclause="";
				//First drop the object if exist
				try{
					String dropsql="drop view Table_"+objectlist.getFieldValue("ObjectName",trow);

					tu.executeQuery(dropsql);
				}catch (Exception e){
					e.getMessage();
				}
				//Get attribute list
				String view_relsql=relationsql+"'"+objectlist.getFieldValue("ObjId",trow)+"'";
				relation=tu.getResultSet(view_relsql);
				logger.info(view_relsql);
				String view_sql=attributesql+"'"+objectlist.getFieldValue("ObjId",trow)+ "' Order By da.AttrIndex";
				attrtable= tu.getResultSet(view_sql);
				logger.info(view_sql);
				String objectname=objectlist.getFieldValue("ObjectName",trow);
				buffer.append("Create or Replace View Table_" +objectname + " as select distinct ");
				//Add Attributes here
				for(int atrrow=0; atrrow<attrtable.getRowCount(); atrrow++) {

					//verify the field behavior "Semantic" or "custom"
					//If the field is a custom field will be appended by "x_"
					String ColumnName=((atrrow==0||atrrow==attrtable.getRowCount())?attrtable.getFieldValue("FieldType",atrrow)+" " + attrtable.getFieldValue("AttributeName",atrrow):","+attrtable.getFieldValue("FieldType",atrrow)+" " + attrtable.getFieldValue("AttributeName",atrrow));

					buffer.append( ColumnName);

				}
				//Add relation fields here
				for(int relrow=0; relrow<relation.getRowCount(); relrow++) {
					//The remark column will hold the relation field and aliasname
					//The defualtvalue should have atleast from table with alias and "@".
					// If there is no where clause it should be defaultvalue=table_<object>@none

					String ColumnName=relation.getFieldValue("Remark",relrow);
					buffer.append(ColumnName.equals("")?"":"," + ColumnName );
					int tmplen=relation.getFieldValue("DefaultFilter",relrow).indexOf("@");
					if(tmplen>0){
						logger.info("rel@length="+tmplen);
						String tmpfrom=tmplen>0?relation.getFieldValue("DefaultFilter",relrow).substring(0,tmplen):"";
						String tmpwhere=(tmplen>0&&relation.getFieldValue("DefaultFilter",relrow).substring(tmplen+1).equalsIgnoreCase("none")?"":relation.getFieldValue("DefaultFilter",relrow).substring(tmplen+1));
						fromtable=fromtable.equals("")?tmpfrom:","+tmpfrom;
						whereclause=whereclause.equals("")?tmpwhere:" and "+tmpwhere;
						logger.info("fromtable="+fromtable+"\n whereclause="+whereclause );
					}else{
						fromtable=relation.getFieldValue("DefaultFilter",relrow);
						whereclause="";
					}
				}

				//Find all relation related to this Object

				buffer.append(" from "+fromtable +(whereclause.equals("")?"" :" where " +whereclause) );
				fromtable="";
				whereclause="";
				String vsql=buffer.toString().replaceAll("\\^", "'");  
				//String vsql=tu.replaceStringWithPlus(buffer.toString(),"^","'");                
				tu.getResultSet(vsql );
				//Log
				if(islog){
					getIOUtility().writeToFile(vsql+(getDbType().equalsIgnoreCase("Oracle")==true?";":""));
				}
			}
		}

		return(retVal);
	}
	public boolean upgradeSchema(){

		boolean retVal=false;
		String objectsql="select ObjId,name from the SML_DbObject";

		String indexsql="select di.ObjId, di.IndexName,di.Fields,di.IndexType,do.ObjectName from SML_DbIndex di,SML_DbObject do where di.DbIndex2DbObject=do.ObjId and do.ObjId=";
		//Create two view for <SML_..> tables based on
		//**1)) where <SML_..>.Gendate between SML_Application.LastStartTime and SML_Application.LastEndTime
		//**2)) where <SML_..>.Gendate between SML_Application.CurrentStartTime and SML_Application.CurrentEndTime
		//Compare these two views to find out what are the new baseline objects which needs to be created
		//Find out what are new customs objects which needs to be created
		//Find out those objects which needs to be modified ,so you need to alter the tables
		//List out the attributes which are new
		//List out those attributes whose domains are changed
		//List out those attributes which are custom fields needs to be prefix x_<attributeName>
		//Get the list of the object to be compiled

		//Get the list of indexes for the object

		//Create the object or table

		//Create the indexes
		return(retVal);
	}
	public boolean upgradeSchemaWithDrop(){

		boolean retVal=false;
		String objectsql="select ObjId,name from the SML_DbObject";
		String attributesql="select da.ObjId,da.DbAttribute2DbObject,da.Name,da.Behavior,da.DomainName, " +
				" da.IsNull,da.Validation,da.AttrRule,da.RelationObject,da.ConvertObject, " +
				" da.ImplementObject,da.IsViewField,da.SelectMandatory,da.SearchField, " +
				" da.Version ,dd.OracleDataType,dd.MssqlDataType,dd.DataSize,dd.DecimalSize " +
				" from table_DB_Attribute da ,table_Attribute_domain dd where da.DomainName= dd.DomainName " +
				" and da.DbObjId=" ;
		String indexsql="select ObjId, name,type,fields from SML_DbIndex where DbIndex2DbObject=";
		//Create two view for <SML_..> tables based on
		//**1)) where <SML_..>.Gendate between SML_Application.LastStartTime and SML_Application.LastEndTime
		//**2)) where <SML_..>.Gendate between SML_Application.CurrentStartTime and SML_Application.CurrentEndTime
		//Compare these two views to find out what are the new baseline objects which needs to be created
		//Find out what are new customs objects which needs to be created
		//Find out those objects which needs to be modified ,so you need to alter the tables
		//List out those baseline attributes which are to be droped and not available in the current schema
		//Create a temporary table from the previous original table by sql query like
		// "Create Table_tmp_<ObjectName> as select <field1>,<field2>... from Table_<ObjectName> ..
		//without selecting the droped fields
		//List out the attributes which are new and need to be added to the current selected temporary table
		//Alter the Temporary table and add these fields
		//List out those attributes whose domains are changed
		//Aleter the Temporary table and apply the new domains
		//List out those attributes which are custom fields needs to be prefix x_<attributeName>
		//Apply the custom fields also
		//Atlast drop the Original table and rename the Temporary table to it's original name

		//Get the list of indexes for the object

		//Create the indexes
		//Continue with object list
		return(retVal);
	}

	private boolean importSemanticView(Vector tagdata){
		String fieldvalues="";
		String sql="";
		setTableName("SML_ViewObject");
		setFieldKey("ObjId");
		setSignature(tu.parseInputValue(tagdata,"ObjectName"));
		viewId=  getPrimaryKey();
		appId=(appId.equals("")||appId==null)?tu.getResultSet("select objid from sml_application where upper(appname)=upper('"+tu.parseInputValue(tagdata,"ObjectTypeNo")+"')").getFieldValue("ObjId",0):appId;
		fieldvalues = viewId+",'"+ appId + "','" + tu.parseInputValue(tagdata,"ObjectName") + "','" + tu.parseInputValue(tagdata,"AliasName") + "','"
				+ tu.parseInputValue(tagdata,"ObjectType") + "','" +  tu.parseInputValue(tagdata,"HasGroup") + "','"
				+tu.parseInputValue(tagdata,"IsBaseline")+ "','"+
				tu.parseInputValue(tagdata,"Version")+ "','"+tu.parseInputValue(tagdata,"ObjectTypeNo")+ "','"+
				tu.parseInputValue(tagdata,"Remark") + "'";
		if (getDbType().equalsIgnoreCase("Oracle")){
			sql= "Insert into SML_ViewObject( ObjId ,ViewObject2Application,ObjectName ,AliasName,ObjectType,HasGroup,IsBaseline,Version,ObjectTypeNo,Remark,GenUser,GenDate" +
					" ) values(" + fieldvalues +",'"+adminuser+"',sysdate)";
		}else if (getDbType().equalsIgnoreCase("Mssql")){
			sql= "Insert into SML_ViewObject( ObjId ,ViewObject2Application,ObjectName ,AliasName,ObjectType,HasGroup,IsBaseline,Version,ObjectTypeNo,Remark,GenUser,GenDate" +
					" ) values(" +fieldvalues +",'"+adminuser+"',getdate())";
		}else if (getDbType().equalsIgnoreCase("Mysql")){
			sql= "Insert into SML_ViewObject( ObjId ,ViewObject2Application,ObjectName ,AliasName,ObjectType,HasGroup,IsBaseline,Version,ObjectTypeNo,Remark,GenUser,GenDate" +
					" ) values(" +fieldvalues +",'"+adminuser+"',now())";
		}
		//Log
		if(islog){
			getIOUtility().writeToFile(sql);
		}
		// Execute query

		return(tu.executeQuery(sql));

	}
	private boolean importViewAttribute(Vector tagdata){
		String fieldvalues="";
		String sql="";
		String ViewAttribute2DbObject="' '";
		String ViewAttribute2AttributeDomain="' '";
		String ViewAttribute2DbAttribute="' '";
		setTableName("SML_ViewAttribute");
		setFieldKey("ObjId");
		setSignature(tu.parseInputValue(tagdata,"Attribute"));
		attrId=  getPrimaryKey();
		int comaIndex=tu.parseInputValue(tagdata,"Attribute").indexOf(",");
		int index=tu.parseInputValue(tagdata,"Attribute").indexOf(".");
		String attrname=(comaIndex>0?tu.parseInputValue(tagdata,"Attribute").substring(index+1,comaIndex):tu.parseInputValue(tagdata,"Attribute").substring(index+1));
		String attrsql="select sa.objid,sa.AttributeName,sa.DbAttribute2DbObject,sa.DbAttribute2AttributeDomain "+
				" from sml_dbobject so,sml_dbattribute sa where sa.DbAttribute2DbObject=so.objid and so.ObjectName='"+
				tu.parseInputValue(tagdata,"dbObject")+"' and sa.AttributeName='"+attrname+"'";

		TemplateTable result=tu.getResultSet(attrsql);
		if(result.getRowCount()>0){
			ViewAttribute2DbObject="'"+tu.getResultSet(attrsql).getFieldValue("DbAttribute2DbObject",0)+"'";
			ViewAttribute2AttributeDomain="'"+tu.getResultSet(attrsql).getFieldValue("DbAttribute2AttributeDomain",0)+"'";
			ViewAttribute2DbAttribute="'"+tu.getResultSet(attrsql).getFieldValue("objid",0)+"'";
		}
		fieldvalues = attrId+ ",'" + tu.parseInputValue(tagdata,"Attribute") + "','" + tu.parseInputValue(tagdata,"AliasName") + "','"
				+ tu.parseInputValue(tagdata,"MssqlConverion") + "','" +  tu.parseInputValue(tagdata,"OracleConversion") + "',"
				+tu.parseInputValue(tagdata,"AttributeIndex")+ ","+tu.parseInputValue(tagdata,"OrderIndex")+ ",'"+
				tu.parseInputValue(tagdata,"IsSummary")+ "','"+tu.parseInputValue(tagdata,"HasConversion")+ "','"+
				tu.parseInputValue(tagdata,"IsFilter") + "','"+tu.parseInputValue(tagdata,"IsBaseline") + "','"+
				tu.parseInputValue(tagdata,"Version")+ "','"+tu.parseInputValue(tagdata,"Remark") + "',"+
				ViewAttribute2DbObject+","+viewId+","+ViewAttribute2AttributeDomain+","+ViewAttribute2DbAttribute;


		if (getDbType().equalsIgnoreCase("Oracle")){
			sql= "Insert into SML_ViewAttribute( ObjId ,Attribute,AliasName ,MsSqlConversion,OracleConversion,AttributeIndex,OrderIndex,IsSummary,"+
					"HasConversion,IsFilter,IsBaseline,Version,Remark,ViewAttribute2DbObject,ViewAttribute2View,"+
					"ViewAttribute2AttributeDomain,ViewAttribute2DbAttribute,GenUser,GenDate" +
					" ) values(" + fieldvalues +",'"+adminuser+"',sysdate)";
		}else if (getDbType().equalsIgnoreCase("Mssql")){
			sql= "Insert into SML_ViewAttribute( ObjId ,Attribute,AliasName ,MsSqlConversion,OracleConversion,AttributeIndex,OrderIndex,IsSummary,"+
					"HasConversion,IsFilter,IsBaseline,Version,Remark,ViewAttribute2DbObject,ViewAttribute2View,"+
					"ViewAttribute2AttributeDomain,ViewAttribute2DbAttribute,GenUser,GenDate" +
					" ) values(" +fieldvalues +",'"+adminuser+"',getdate())";
		}else if (getDbType().equalsIgnoreCase("Mysql")){
			sql= "Insert into SML_ViewAttribute( ObjId ,Attribute,AliasName ,MsSqlConversion,OracleConversion,AttributeIndex,OrderIndex,IsSummary,"+
					"HasConversion,IsFilter,IsBaseline,Version,Remark,ViewAttribute2DbObject,ViewAttribute2View,"+
					"ViewAttribute2AttributeDomain,ViewAttribute2DbAttribute,GenUser,GenDate" +
					" ) values(" +fieldvalues +",'"+adminuser+"',now())";
		}
		//Log
		if(islog){
			getIOUtility().writeToFile(sql);
		}
		// Execute query

		return(tu.executeQuery(sql));

	}
	private boolean importViewRelation(Vector tagdata){
		String fieldvalues="";
		String sql="";
		setTableName("SML_ViewRelation");
		setFieldKey("ObjId");
		setSignature(tu.parseInputValue(tagdata,"ParentObject"));
		relationId=  getPrimaryKey();

		fieldvalues = relationId+",'"+tu.parseInputValue(tagdata,"ParentObject") + "','" + tu.parseInputValue(tagdata,"ChildObject") + "','"
				+ tu.parseInputValue(tagdata,"OracleRelation") + "','" +  tu.parseInputValue(tagdata,"MsSqlRelation") + "','"
				+tu.parseInputValue(tagdata,"JoinType")+ "',"+viewId;
		if (getDbType().equalsIgnoreCase("Oracle")){
			sql= "Insert into SML_ViewRelation( ObjId ,ParentObject,ChildObject ,OracleRelation,MsSqlRelation,JoinType,ViewRelation2View,GenUser,GenDate" +
					" ) values(" + fieldvalues +",'"+adminuser+"',sysdate)";
		}else if (getDbType().equalsIgnoreCase("Mssql")){
			sql="Insert into SML_ViewRelation( ObjId ,ParentObject,ChildObject ,OracleRelation,MsSqlRelation,JoinType,ViewRelation2View,GenUser,GenDate" +
					" ) values(" +fieldvalues +",'"+adminuser+"',getdate())";
		}else if (getDbType().equalsIgnoreCase("Mysql")){
			sql="Insert into SML_ViewRelation( ObjId ,ParentObject,ChildObject ,OracleRelation,MsSqlRelation,JoinType,ViewRelation2View,GenUser,GenDate" +
					" ) values(" +fieldvalues +",'"+adminuser+"',now())";
		}
		//Log
		if(islog){
			getIOUtility().writeToFile(sql);
		}
		// Execute query

		return(tu.executeQuery(sql));

	}
	/**
	 * This method will export all schema data inculding application, domain, table, attributes, index and relations
	 *
	 * */
	public boolean syncSchemaData(){
		//Tag data
		Vector tagdata=new Vector();
		int tagindex=0;
		String retString="";
		//first cleanup object rule
		tu.executeQuery("delete from table_listproperty");
		tu.executeQuery("delete from table_attribute");
		tu.executeQuery("delete from table_Object");
		tu.executeQuery("update sml_dbattribute set HasCodeObject='no' where HasCodeObject='null'");
		tu.executeQuery("insert into table_object select ObjId,ObjectName,'1','1',null,DbObject2Application,null"+
				",'0','0',GenUser,GenUser,GenDate,ModUser,ModDate from sml_dbobject");
		tu.executeQuery("insert into table_attribute select objid,AttributeName,dbObjectName,HasProperty,HasCodeObject,"+
				"DbAttribute2DbObject,null,'0','0',GenUser,GenUser,GenDate,ModUser,ModDate from sml_dbattribute");
		
		try {

		}catch (Exception e) {
			if(islog){
				getIOUtility().writeToFile("\nError: " + e.toString());
				logger.error("Error: " + e.toString());
			}
			e.printStackTrace();
			return(false);
		}
		return(true);
	}
	/**
	 * This method will export all rule object, generic code object rule action and action query related to any rule
	 * */
	public boolean exportJobCode(){

		String header="";
		String projectcode="select *from table_projectcode order by projectcode";
		String maincode="select *from table_maincode order by projectcode, mainjobcode";
		String subcode="select *from table_subcode order by projectcode, mainjobcode, subjobcode";
		String taskcode="select *from table_taskcode order by projectcode, mainjobcode, subjobcode,taskjobcode";
		//String taskcode="select ObjId,Name,ProjectCode,substring(TaskJobCode,0,3)'MainJobCode',substring(TaskJobCode,0,4)'SubJobCode',TaskJobCode,UmCode,Description,Status,Successor,HasDependency,ApprovedBy from table_taskcode order by projectcode, mainjobcode, subjobcode,taskjobcode";
		try{
			//import project code
			TemplateTable pcode=tu.getResultSet(projectcode);
			if(pcode!=null && pcode.getRowCount()>0){
				for(int i=0; i<pcode.getRowCount(); i++){
					getIOUtility().writeToFile("\n//ProjectCode: "+pcode.getFieldValue("projectcode",i)+"-"+pcode.getFieldValue("Name",i));
					getIOUtility().writeToFile("\n<ProjectCode>");
					getIOUtility().writeToFile("\n\t Name="+pcode.getFieldValue("Name",i));
					getIOUtility().writeToFile("\n\t ProjectCode="+pcode.getFieldValue("ProjectCode",i));
					getIOUtility().writeToFile("\n\t Description="+((pcode.getFieldValue("Description",i)==null||pcode.getFieldValue("Description",i).equals(""))?"No Description Available":pcode.getFieldValue("Description",i)));
					getIOUtility().writeToFile("\n\t Status="+pcode.getFieldValue("Status",i));
					getIOUtility().writeToFile("\n\t GroupUser="+pcode.getFieldValue("GroupUser",i));
					getIOUtility().writeToFile("\n\t ApprovedBy="+((pcode.getFieldValue("ApprovedBy",i)==null||pcode.getFieldValue("ApprovedBy",i).equals(""))?"None":pcode.getFieldValue("ApprovedBy",i)));
					getIOUtility().writeToFile("\n</ProjectCode>");
				}
			}
			//import main code
			TemplateTable mcode=tu.getResultSet(maincode);
			if(mcode!=null && mcode.getRowCount()>0){
				for(int i=0; i<mcode.getRowCount(); i++){

					getIOUtility().writeToFile("\n//MainCode: "+mcode.getFieldValue("projectcode",i)+":"+mcode.getFieldValue("mainjobcode",i));
					getIOUtility().writeToFile("\n<MainCode>");
					getIOUtility().writeToFile("\n\t Name="+mcode.getFieldValue("Name",i));
					getIOUtility().writeToFile("\n\t ProjectCode="+mcode.getFieldValue("ProjectCode",i));
					getIOUtility().writeToFile("\n\t MainJobCode="+mcode.getFieldValue("MainJobCode",i));
					getIOUtility().writeToFile("\n\t UmCode="+mcode.getFieldValue("UmCode",i));
					getIOUtility().writeToFile("\n\t DeptCode="+mcode.getFieldValue("DeptCode",i));
					//getIOUtility().writeToFile("\n\t Successor=0");
					//getIOUtility().writeToFile("\n\t Predecessor=0");
					getIOUtility().writeToFile("\n\t Description="+((mcode.getFieldValue("Description",i)==null||mcode.getFieldValue("Description",i).equals(""))?"No Description Available":mcode.getFieldValue("Description",i)));
					getIOUtility().writeToFile("\n\t Status="+mcode.getFieldValue("Status",i));
					getIOUtility().writeToFile("\n\t GroupUser="+mcode.getFieldValue("GroupUser",i));
					getIOUtility().writeToFile("\n\t ApprovedBy="+((mcode.getFieldValue("ApprovedBy",i)==null||mcode.getFieldValue("ApprovedBy",i).equals(""))?"None":mcode.getFieldValue("ApprovedBy",i)));
					getIOUtility().writeToFile("\n</MainCode>");
				}
			}
			//import Sub code
			TemplateTable scode=tu.getResultSet(subcode);
			if(scode!=null && scode.getRowCount()>0){
				for(int i=0; i<scode.getRowCount(); i++){

					getIOUtility().writeToFile("\n//SubCode: "+scode.getFieldValue("projectcode",i)+":"+scode.getFieldValue("mainjobcode",i)+":"+scode.getFieldValue("SubjobCode",i));
					getIOUtility().writeToFile("\n<SubCode>");
					getIOUtility().writeToFile("\n\t Name="+scode.getFieldValue("Name",i));
					getIOUtility().writeToFile("\n\t ProjectCode="+scode.getFieldValue("ProjectCode",i));
					getIOUtility().writeToFile("\n\t MainJobCode="+scode.getFieldValue("MainJobCode",i));
					getIOUtility().writeToFile("\n\t SubJobCode="+scode.getFieldValue("SubJobCode",i));
					getIOUtility().writeToFile("\n\t Umcode="+scode.getFieldValue("Umcode",i));
					getIOUtility().writeToFile("\n\t UnitRate="+scode.getFieldValue("UnitRate",i));
					//getIOUtility().writeToFile("\n\t Successor=0");
					//getIOUtility().writeToFile("\n\t Predecessor=0");
					getIOUtility().writeToFile("\n\t Description="+((scode.getFieldValue("Description",i)==null||scode.getFieldValue("Description",i).equals(""))?"No Description Available":scode.getFieldValue("Description",i)));
					getIOUtility().writeToFile("\n\t Status="+scode.getFieldValue("Status",i));
					getIOUtility().writeToFile("\n\t ApprovedBy="+((scode.getFieldValue("ApprovedBy",i)==null||scode.getFieldValue("ApprovedBy",i).equals(""))?"None":scode.getFieldValue("ApprovedBy",i)));
					getIOUtility().writeToFile("\n\t GroupUser="+scode.getFieldValue("GroupUser",i));
					getIOUtility().writeToFile("\n</SubCode>");
				}
			}
			//import Task code
			TemplateTable tcode=tu.getResultSet(taskcode);
			if(tcode!=null && tcode.getRowCount()>0){
				for(int i=0; i<tcode.getRowCount(); i++){
					getIOUtility().writeToFile("\n//TaskCode: "+tcode.getFieldValue("projectcode",i)+":"+tcode.getFieldValue("mainjobcode",i)
							+":"+tcode.getFieldValue("SubJobCode",i)+":"+tcode.getFieldValue("TaskJobCode",i));
					getIOUtility().writeToFile("\n<TaskCode>");
					getIOUtility().writeToFile("\n\t Name="+tcode.getFieldValue("Name",i));
					getIOUtility().writeToFile("\n\t ProjectCode="+tcode.getFieldValue("ProjectCode",i));
					getIOUtility().writeToFile("\n\t MainJobCode="+tcode.getFieldValue("MainJobCode",i));
					getIOUtility().writeToFile("\n\t SubJobCode="+tcode.getFieldValue("SubJobCode",i));
					getIOUtility().writeToFile("\n\t TaskJobCode="+tcode.getFieldValue("TaskJobCode",i));
					getIOUtility().writeToFile("\n\t Umcode="+tcode.getFieldValue("Umcode",i));
					getIOUtility().writeToFile("\n\t Description="+((tcode.getFieldValue("Description",i)==null||tcode.getFieldValue("Description",i).equals(""))?"No Description Available":tcode.getFieldValue("Description",i)));
					getIOUtility().writeToFile("\n\t Status="+tcode.getFieldValue("Status",i));
					//getIOUtility().writeToFile("\n\t Successor=0");
					//getIOUtility().writeToFile("\n\t Precedessor=0");
					getIOUtility().writeToFile("\n\t GroupCode="+tcode.getFieldValue("GroupCode",i));
					getIOUtility().writeToFile("\n\t GroupUser="+tcode.getFieldValue("GroupUser",i));
					getIOUtility().writeToFile("\n\t ApprovedBy="+((tcode.getFieldValue("ApprovedBy",i)==null||tcode.getFieldValue("ApprovedBy",i).equals(""))?"None":tcode.getFieldValue("ApprovedBy",i)));
					getIOUtility().writeToFile("\n</TaskCode>");

				}
			}
		}catch (Exception e) {
			getIOUtility().closeFile();
			logger.info("Error: " + e.toString());
			e.printStackTrace();
			return(false);
		}

		return(true);
	}
	/**
	 * This method will export all rule object, generic code object rule action and action query related to any rule
	 * */
	public boolean exportRuleData(){
		//Tag data
		Vector tagdata=new Vector();
		int tagindex=0;
		String retString="";
		String queryobject="";
		String queryattribute="";
		String ruleobject="";
		String ruleattribute="";
		String actionobject="";
		String actionattribute="";
		String codeattribute="";
		String header="";
		String rulesql="select *from table_listproperty where tablename is not null order by tablename,name,PropIndex";
		String gencodesql="select *from table_genericcode order by attributename,CodeValue";

		//first cleanup list property
		TemplateTable rule=tu.getResultSet(rulesql);
		try{
			//Add List Property
			if(rule!=null && rule.getRowCount()>0){
				for(int k=0; k<rule.getRowCount(); k++){
					if(ruleobject!=null&&ruleobject.equals("") ||!ruleobject.equals(rule.getFieldValue("tablename",k))){
						ruleobject=rule.getFieldValue("tablename",k);
						header="\n//*****************************************************************//"+
								"\n//ListProperty:"+rule.getFieldValue("tablename",k)+
								"\n//**************************************************************//";
						getIOUtility().writeToFile(header+"\n");
					}
					if(ruleattribute!=null&&ruleattribute.equals("") ||!ruleattribute.equals(rule.getFieldValue("name",k))){
						ruleattribute=rule.getFieldValue("name",k);
						getIOUtility().writeToFile("\n//Attribute:"+ruleattribute);
					}
					getIOUtility().writeToFile("\n<ListProperty>");
					getIOUtility().writeToFile("\n\tObject="+rule.getFieldValue("tablename",k));
					getIOUtility().writeToFile("\n\tAttribute="+rule.getFieldValue("name",k));
					getIOUtility().writeToFile("\n\tPropertyString="+rule.getFieldValue("PropertyString",k));
					getIOUtility().writeToFile("\n\tPropertyValue="+rule.getFieldValue("PropertyValue",k));
					getIOUtility().writeToFile("\n\tScope="+rule.getFieldValue("Scope",k));
					getIOUtility().writeToFile("\n\tPropIndex="+rule.getFieldValue("PropIndex",k));
					getIOUtility().writeToFile("\n</ListProperty>");
				}
			}
			//String objectrulesql="select *from table_objectrule where TableName='"+rule.getFieldValue("tablename",k)+"' order by tablename,name,RuleIndex";
			String objectrulesql="select *from table_objectrule order by tablename,name,RuleIndex";

			TemplateTable action=tu.getResultSet(objectrulesql);

			//Add Object Rule
			if(action!=null && action.getRowCount()>0){
				for(int i=0; i<action.getRowCount(); i++){
					if(actionobject!=null&&actionobject.equals("") ||!actionobject.equals(action.getFieldValue("tablename",i))){
						actionobject=action.getFieldValue("tablename",i);
						header="\n//*****************************************************************//"+
								"\n//Adding Object Rule"+
								"\n//Table Name:"+action.getFieldValue("tablename",i)+
								"\n//Attribute Name:"+action.getFieldValue("name",i)+
								"\n//**************************************************************//";
						getIOUtility().writeToFile(header+"\n");
					}

					getIOUtility().writeToFile("\n\t<ObjectRule>");
					getIOUtility().writeToFile("\n\t\t Name="+action.getFieldValue("Name",i));
					getIOUtility().writeToFile("\n\t\t TableName="+action.getFieldValue("TableName",i));
					getIOUtility().writeToFile("\n\t\t EffectedTable="+action.getFieldValue("EffectedTable",i));
					getIOUtility().writeToFile("\n\t\t Description="+((action.getFieldValue("Description",i)==null||action.getFieldValue("Description",i).equals(""))?"None":action.getFieldValue("Description",i)));
					getIOUtility().writeToFile("\n\t\t Reason="+action.getFieldValue("Reason",i));
					getIOUtility().writeToFile("\n\t\t ActionState="+action.getFieldValue("ActionState",i));
					getIOUtility().writeToFile("\n\t\t Condition="+((action.getFieldValue("Condition",i)==null||action.getFieldValue("Condition",i).equals(""))?"Null":action.getFieldValue("Condition",i)));
					getIOUtility().writeToFile("\n\t\t RuleIndex="+action.getFieldValue("RuleIndex",i));
					getIOUtility().writeToFile("\n\t\t Status="+action.getFieldValue("Status",i));
					getIOUtility().writeToFile("\n\t</ObjectRule>");


					//Add Action query
					String objectrulequery="select *from table_actionquery where actionquery2objectrule='"+action.getFieldValue("objid",i)+"' order by stepno";
					TemplateTable actionquery=tu.getResultSet(objectrulequery);
					if(actionquery!=null && actionquery.getRowCount()>0){
						for(int j=0; j<actionquery.getRowCount(); j++){
							if(queryobject!=null&&queryobject.equals("") ||!queryobject.equals(actionquery.getFieldValue("tablename",j))){
								queryobject=actionquery.getFieldValue("tablename",j);
								header="\n//*****************************************************************//"+
										"\n//Adding Action Query"+
										"\n//Table Name:"+actionquery.getFieldValue("tablename",j)+
										"\n//Name:"+actionquery.getFieldValue("name",j)+
										"\n//Step No:"+actionquery.getFieldValue("stepno",j)+
										"\n//**************************************************************//";
								getIOUtility().writeToFile(header+"\n");
							}
							getIOUtility().writeToFile("\n\t<ActionQuery>");
							getIOUtility().writeToFile("\n\t\t Name="+actionquery.getFieldValue("Name",j));
							getIOUtility().writeToFile("\n\t\t TableName="+actionquery.getFieldValue("TableName",j));
							getIOUtility().writeToFile("\n\t\t Description="+((actionquery.getFieldValue("Description",j)==null||actionquery.getFieldValue("Description",j).equals(""))?"No Description Available":actionquery.getFieldValue("Description",j)));
							getIOUtility().writeToFile("\n\t\t StepNo="+actionquery.getFieldValue("StepNo",j));
							getIOUtility().writeToFile("\n\t\t Input="+actionquery.getFieldValue("Input",j));
							getIOUtility().writeToFile("\n\t\t InputDataType="+actionquery.getFieldValue("InputDataType",j));
							getIOUtility().writeToFile("\n\t\t Output="+actionquery.getFieldValue("Output",j));
							getIOUtility().writeToFile("\n\t\t QueryType="+actionquery.getFieldValue("QueryType",j));
							getIOUtility().writeToFile("\n\t\t HasRecordSet="+actionquery.getFieldValue("HasRecordSet",j));
							getIOUtility().writeToFile("\n\t\t OracleQuery="+((actionquery.getFieldValue("OracleQuery",j)==null||actionquery.getFieldValue("OracleQuery",j).equals(""))?"None":actionquery.getFieldValue("OracleQuery",j)));
							getIOUtility().writeToFile("\n\t\t MssqlQuery="+((actionquery.getFieldValue("MssqlQuery",j)==null||actionquery.getFieldValue("MssqlQuery",j).equals(""))?"None":actionquery.getFieldValue("MssqlQuery",j)));
							getIOUtility().writeToFile("\n\t\t Status="+actionquery.getFieldValue("Status",j));
							getIOUtility().writeToFile("\n\t</ActionQuery>");

						}
						queryobject="";

					}
				}
				actionobject="";
				//}
				//getIOUtility().writeToFile("\n</ListProperty>");
				//}
				ruleobject="";
				ruleattribute="";
			}
			//Add generic code
			TemplateTable gencode=tu.getResultSet(gencodesql);
			if(gencode!=null && gencode.getRowCount()>0){
				for(int i=0; i<gencode.getRowCount(); i++){
					if(codeattribute!=null&&codeattribute.equals("") ||!codeattribute.equals(gencode.getFieldValue("attributename",i))){
						codeattribute=gencode.getFieldValue("attributename",i);
						getIOUtility().writeToFile("\n//Adding Generic Code for Attribute:"+codeattribute);
					}
					getIOUtility().writeToFile("\n<GenericCode>");
					getIOUtility().writeToFile("\n\t Name="+gencode.getFieldValue("Name",i));
					getIOUtility().writeToFile("\n\t AttributeName="+gencode.getFieldValue("AttributeName",i));
					getIOUtility().writeToFile("\n\t CodeValue="+gencode.getFieldValue("CodeValue",i));
					getIOUtility().writeToFile("\n\t Description="+((gencode.getFieldValue("Description",i)==null||gencode.getFieldValue("Description",i).equals(""))?"No Description Available":gencode.getFieldValue("Description",i)));
					getIOUtility().writeToFile("\n\t Status="+gencode.getFieldValue("Status",i));
					getIOUtility().writeToFile("\n\t CodeIndex="+gencode.getFieldValue("CodeIndex",i));
					getIOUtility().writeToFile("\n</GenericCode>");

				}
				codeattribute="";
			}
		}catch (Exception e) {
			getIOUtility().closeFile();
			logger.info("Error: " + e.toString());
			e.printStackTrace();
			return(false);
		}
		return(true);
	}
	/**
	 * This method will export all rule object, generic code object rule action and action query related to any rule
	 * */
	public boolean exportTableData(String tablename){
		//Tag data
		Vector tagdata=new Vector();
		int tagindex=0;
		String header="";
		String tablesql="select *from "+(tablename.indexOf("sml_")>0?tablename:"table_"+tablename) +" order by objid";

		//first cleanup list property
		TemplateTable tdata=tu.getResultSet(tablesql);
		try{
			//Add List Property
			if(tdata!=null && tdata.getRowCount()>0){
				for(int k=0; k<tdata.getRowCount(); k++){
					if(tablename!=null&&tablename.equals("") ){

						header="\n//*****************************************************************//"+
								"\n//Table:"+tablename+
								"\n//**************************************************************//";
						getIOUtility().writeToFile(header+"\n");
					}
					for(int r=0;r<tdata.getRowCount();r++){
						getIOUtility().writeToFile("\n<"+tablename+">");
						for(int col=0;col<tdata.getColumnCount();col++){
							getIOUtility().writeToFile("\n\t"+tdata.getColumnNames()[col] +"="+tdata.getFieldValue(tdata.getColumnNames()[col],k));
						}
						getIOUtility().writeToFile("\n</"+tablename+">");
					}
					getIOUtility().writeToFile("\n</"+tablename+">");
				}
			}
		}catch (Exception e) {
			getIOUtility().closeFile();
			logger.info("Error: " + e.toString());
			e.printStackTrace();
			return(false);
		}
		return(true);
	}
	private boolean importProjectCode(Vector tagdata){
		String fieldvalues="";
		String sql="insert into table_projectcode(ObjId,Name,ProjectCode,Description,Status,ApprovedBy,GroupUser,GenUser,GenDate) values(";
		setTableName("Table_ProjectCode");
		setFieldKey("ObjId");
		setSignature(tu.parseInputValue(tagdata,"Name"));
		fieldvalues = getPrimaryKey()+ ",'"
				+ tu.parseInputValue(tagdata,"Name") + "','"
				+ tu.parseInputValue(tagdata,"ProjectCode") + "','"
				+ tu.parseInputValue(tagdata,"Description")+ "','"
				+ tu.parseInputValue(tagdata,"Status")+ "','"
				+ tu.parseInputValue(tagdata,"ApprovedBy")+ "','"
				+ tu.parseInputValue(tagdata,"GroupUser")+ "'";
		if (getDbType().equalsIgnoreCase("Oracle")){
			sql+= fieldvalues +",'"+adminuser+"',sysdate)";
		}else if (getDbType().equalsIgnoreCase("Mssql")){
			sql+= fieldvalues +",'"+adminuser+"',getdate())";
		}else if (getDbType().equalsIgnoreCase("Mysql")){
			sql+= fieldvalues +",'"+adminuser+"',now())";
		}
		//Log
		if(islog)
			getIOUtility().writeToFile(sql);
		// Execute query

		return(tu.executeQuery(sql));
	}

	private boolean importMainCode(Vector tagdata){
		String fieldvalues="";
		String psql="select objid from table_projectcode where projectcode='"+tu.parseInputValue(tagdata,"ProjectCode")+"'";
		TemplateTable result=tu.getResultSet(psql);
		String MainCode2ProjectCode=(result.getRowCount()>0? result.getFieldValue(0,result.getRowCount()-1):"0");
		//String MainCode2ProjectCode=(result.getRowCount()>0? result.getFieldValue("objid",result.getRowCount()-1):"0");
		String sql="insert into table_MainCode(ObjId,Name,ProjectCode,MainJobCode,UmCode,DeptCode,Description,Status,ApprovedBy,MainCode2ProjectCode,GroupUser,GenUser,GenDate) values(";
		setTableName("Table_MainCode");
		setFieldKey("ObjId");
		setSignature(tu.parseInputValue(tagdata,"Name"));
		fieldvalues = getPrimaryKey()+ ",'"
				+ tu.parseInputValue(tagdata,"Name") + "','"
				+ tu.parseInputValue(tagdata,"ProjectCode") + "','"
				+ tu.parseInputValue(tagdata,"MainJobCode") + "','"
				+ tu.parseInputValue(tagdata,"UmCode") + " ','"
				+ tu.parseInputValue(tagdata,"DeptCode") + "','"
				//+ (tu.parseInputValue(tagdata,"Successor").equals("")?"0":tu.parseInputValue(tagdata,"Successor")) + ",'"
				//+ (tu.parseInputValue(tagdata,"Predecessor")!=null&&tu.parseInputValue(tagdata,"Predecessor").equals("")?"0":tu.parseInputValue(tagdata,"Predecessor")) + "','"
				+ tu.parseInputValue(tagdata,"Description")+ "','"
				+ tu.parseInputValue(tagdata,"Status")+ "','"
				+ tu.parseInputValue(tagdata,"ApprovedBy")+ "','"
				+MainCode2ProjectCode+"','"
				+ tu.parseInputValue(tagdata,"GroupUser")+ "'";
		//+ tu.parseInputValue(tagdata,"Status")+ "','";
		if (getDbType().equalsIgnoreCase("Oracle")){
			sql+= fieldvalues +",'"+adminuser+"',sysdate)";
		}else if (getDbType().equalsIgnoreCase("Mssql")){
			sql+= fieldvalues +",'"+adminuser+"',getdate())";
		}else if (getDbType().equalsIgnoreCase("Mysql")){
			sql+= fieldvalues +",'"+adminuser+"',now())";
		}
		//Log
		if(islog)
			getIOUtility().writeToFile(sql);
		// Execute query

		return(tu.executeQuery(sql));
	}

	private boolean importSubCode(Vector tagdata){
		String fieldvalues="";
		String psql="select objid from table_maincode where projectcode='"
				+tu.parseInputValue(tagdata,"ProjectCode")+"' and mainjobcode='"+tu.parseInputValue(tagdata,"MainjobCode")+"'";
		TemplateTable result=tu.getResultSet(psql);
		//String SubCode2MainCode=(result.getRowCount()>0? result.getFieldValue("objid",result.getRowCount()-1):"0");
		String SubCode2MainCode=(result.getRowCount()>0? result.getFieldValue(0,result.getRowCount()-1):"0");
		String sql="insert into table_SubCode(ObjId,Name,ProjectCode,MainJobCode,SubJobCode,UmCode,UnitRate,Description,ApprovedBy,Status,SubCode2MainCode,GroupUser,GenUser,GenDate) values(";
		setTableName("Table_SubCode");
		setFieldKey("ObjId");
		setSignature(tu.parseInputValue(tagdata,"Name"));
		fieldvalues = getPrimaryKey()+ ",'"
				+ tu.parseInputValue(tagdata,"Name") + "','"
				+ tu.parseInputValue(tagdata,"ProjectCode") + "','"
				+ tu.parseInputValue(tagdata,"MainJobCode") + "','"
				+ tu.parseInputValue(tagdata,"SubJobCode") + "','"
				+ tu.parseInputValue(tagdata,"UmCode") + " ',"                    
				+ (tu.parseInputValue(tagdata,"UnitRate").equals("")?"0":tu.parseInputValue(tagdata,"UnitRate")) + ",'"
				+ tu.parseInputValue(tagdata,"Description")+ "','"
				//+ (tu.parseInputValue(tagdata,"Successor").equals("")?"0":tu.parseInputValue(tagdata,"Successor")) + ",'"
				//+ (tu.parseInputValue(tagdata,"Predecessor")!=null&&tu.parseInputValue(tagdata,"Predecessor").equals("")?"0":tu.parseInputValue(tagdata,"Predecessor")) + "','"
				+ tu.parseInputValue(tagdata,"ApprovedBy")+ "','"
				+ tu.parseInputValue(tagdata,"Status")+ "','"
				+SubCode2MainCode+"','"
				+ tu.parseInputValue(tagdata,"GroupUser")+ "'";;
				if (getDbType().equalsIgnoreCase("Oracle")){
					sql+= fieldvalues +",'"+adminuser+"',sysdate)";
				}else if (getDbType().equalsIgnoreCase("Mssql")){
					sql+= fieldvalues +",'"+adminuser+"',getdate())";
				}else if (getDbType().equalsIgnoreCase("Mysql")){
					sql+= fieldvalues +",'"+adminuser+"',now())";
				}
				//Log
				if(islog)
					getIOUtility().writeToFile(sql);
				// Execute query

				return(tu.executeQuery(sql));
	}

	private boolean importTaskCode(Vector tagdata){
		String fieldvalues="";
		String psql="select objid from table_subcode where projectcode='"
				+tu.parseInputValue(tagdata,"ProjectCode")+"' and mainjobcode='"
				+tu.parseInputValue(tagdata,"MainjobCode")+"' and subjobcode='"
				+tu.parseInputValue(tagdata,"SubJobCode")+"'";
		TemplateTable result=tu.getResultSet(psql);
		//String TaskCode2SubCode=(result.getRowCount()>0? result.getFieldValue("objid",result.getRowCount()-1):"0");
		String TaskCode2SubCode=(result.getRowCount()>0? result.getFieldValue(0,result.getRowCount()-1):"0");
		String sql="insert into table_TaskCode(ObjId,Name,ProjectCode,MainJobCode,SubJobCode,TaskJobCode,UmCode,Description,Status,GroupCode,ApprovedBy,TaskCode2SubCode,GroupUser,GenUser,GenDate) values(";
		setTableName("Table_TaskCode");
		setFieldKey("ObjId");
		setSignature(tu.parseInputValue(tagdata,"Name"));
		fieldvalues = getPrimaryKey()+ ",'"
				+ tu.parseInputValue(tagdata,"Name") + "','"
				+ tu.parseInputValue(tagdata,"ProjectCode") + "','"
				+ tu.parseInputValue(tagdata,"MainJobCode") + "','"
				+ tu.parseInputValue(tagdata,"SubJobCode") + "','"
				+ tu.parseInputValue(tagdata,"TaskJobCode") + "','"
				+ tu.parseInputValue(tagdata,"UmCode") + " ','"
				+ tu.parseInputValue(tagdata,"Description")+ "','"
				+ tu.parseInputValue(tagdata,"Status")+ "','"
				//+ tu.parseInputValue(tagdata,"Successor")+ "','"
				//+ tu.parseInputValue(tagdata,"Predecessor")+ " ','"
				+ tu.parseInputValue(tagdata,"GroupCode")+ " ','"
				+ tu.parseInputValue(tagdata,"ApprovedBy")+ " ','"
				+TaskCode2SubCode;
		if (getDbType().equalsIgnoreCase("Oracle")){
			sql+= fieldvalues +"','"+adminuser+"','"+adminuser+"',sysdate)";
		}else if (getDbType().equalsIgnoreCase("Mssql")){
			sql+= fieldvalues +",'"+adminuser+"','"+adminuser+"',getdate())";
		}else if (getDbType().equalsIgnoreCase("Mysql")){
			sql+= fieldvalues +",'"+adminuser+"','"+adminuser+"',now())";
		}
		//Log
		if(islog)
			getIOUtility().writeToFile(sql);
		// Execute query

		return(tu.executeQuery(sql));
	}
	/**
	 * This method create WeekMap records for 1 years
	 */

	public boolean createWeekMap(){
		tu.executeQuery("delete from table_weekmap");
		String sql="";
		Calendar mycal = new GregorianCalendar();
		int today=mycal.get(java.util.Calendar.DAY_OF_YEAR);
		int year=mycal.get(Calendar.YEAR);
		int week=mycal.get(Calendar.WEEK_OF_YEAR);

		for(int i=1; i<53; i++){
			setTableName("Table_WeekMap");
			setFieldKey("ObjId");
			setSignature("Week");
			int startday=(i-1)*7-today+1;
			int endday=i*7-today;
			String gendate=(!getDbType().equalsIgnoreCase("Oracle")?"getdate()":"sysdate");
			sql="insert into table_weekmap(ObjId,Name,WeekNo,Year,StartDate,EndDate,HolidayCount,NoOfWorkingDays,WEEKMAP2CALENDAR,GroupUser,GenUser,GenDate,ModUser,ModDate)values("+getPrimaryKey()+",'Week "+
					i+"',"+i+","+year+","+gendate+(startday>=0?"+":"")+startday+","+gendate+(endday>=0?"+":"")+endday+",null,null,2005,'"+adminuser+"','"+adminuser+"',"+gendate+",'"+adminuser+"',"+gendate+")";

			tu.executeQuery(sql);
		}
		//Log
		if(islog)
			getIOUtility().writeToFile(sql);
		// Execute query

		return(true);
	}
}
