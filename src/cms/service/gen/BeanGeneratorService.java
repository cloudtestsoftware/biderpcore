package cms.service.gen;

import cms.service.template.TemplateTable;
import cms.service.template.TemplateUtility;
import cms.service.util.FileUtility;




/**
 * Title:        BidERP Application
 * Description:  BidERP Main Infrastructure Project
 * Copyright:    Copyright (c) 2001
 * Company:      comJava Soft
 * @author
 * @version 1.0
 */

public class BeanGeneratorService extends BeanGenService{

	private  TemplateUtility tu= new TemplateUtility();
	private static FileUtility iu = new FileUtility();
	private TemplateTable childrelation;
	private TemplateTable parentrelation;
	private TemplateTable parentproperty;
	private String appname="";
	private String parent="";
	private String objectfilter="";
	private String objectfilterfield="";
	private String childtabs="";
	private String childtabalias="";
	private String deletetabs="";
	
  
	public BeanGeneratorService() {
	}
	public void init(){
		parent="";
		objectfilter="";
		objectfilterfield="";
		childtabs="";
		childtabalias="";
		deletetabs="";
		childrelation=null;
		parentrelation=null;
		parentproperty=null;
	}
	public void setAppName(String name){
		this.appname=name;
	}
	public String getAppName(){
		return(appname.toLowerCase());
	}

	public void setParent(String parent){
		this.parent=parent;
	}
	public String getParent(){
		return(parent);
	}

	public void createOracleSpInsert(String parent,String path){
		setParent(parent);
		String tmpparent=parent.substring(0,1).toUpperCase()+parent.substring(1).toLowerCase();
		TemplateTable result=getChildRelationObject( parent);
		String [] child =new String[result.getRowCount()];
		if (result.getRowCount()>0){

			for(int i=0;i<result.getRowCount();i++)
				child[i]=result.getFieldValue("childname",i);
		}
		if(child!=null&&child.length>0){
			iu.openNewFile(path+"/com/"+getAppName()+"/sql/oracle/"+tmpparent+"/","Insert_"+tmpparent+".sql",false);
			iu.writeToFile(makeOracleStoredProcInsert(tmpparent,child));
			iu.closeFile();
		}
	}
	public void createOracleSpUpdate(String parent,String path){
		setParent(parent);
		String tmpparent=parent.substring(0,1).toUpperCase()+parent.substring(1).toLowerCase();
		TemplateTable result=getChildRelationObject( parent);
		String [] child =new String[result.getRowCount()];
		if (result.getRowCount()>0){

			for(int i=0;i<result.getRowCount();i++)
				child[i]=result.getFieldValue("childname",i);
		}
		if(child!=null&&child.length>0){
			iu.openNewFile(path+"/com/"+getAppName()+"/sql/oracle/"+tmpparent+"/","Update_"+tmpparent+".sql",false);
			iu.writeToFile(makeOracleStoredProcUpdate(tmpparent,child));
			iu.closeFile();
		}
	}
	public void createObjectRule(String parent,String path){
		setParent(parent);
		String tmpparent=parent.substring(0,1).toUpperCase()+parent.substring(1).toLowerCase();

		TemplateTable result=getChildRelationObject( parent);
		String [] child =new String[result.getRowCount()];
		if (result.getRowCount()>0){

			for(int i=0;i<result.getRowCount();i++)
				child[i]=result.getFieldValue("childname",i);
		}
		if(child!=null&&child.length>0){
			iu.openNewFile(path+"/com/"+getAppName()+"/sql/oracle/"+tmpparent+"/","ObjectRule_"+tmpparent+".txt",false);
			iu.writeToFile(makeObjectRule(tmpparent,child));
			iu.closeFile();
		}
	}
	public void createServiceBean(String parent,String path){
		setParent(parent);
		String tmpparent=parent.substring(0,1).toUpperCase()+parent.substring(1).toLowerCase();
		iu.openNewFile(path+"/com/"+getAppName()+"/service/",tmpparent+"Service.java",false);
		TemplateTable result=getChildRelationObject( parent);
		String [] child =new String[result.getRowCount()];
		if (result.getRowCount()>0){

			for(int i=0;i<result.getRowCount();i++)
				child[i]=result.getFieldValue("childname",i);
		}
		iu.writeToFile(makeServiceBean(tmpparent,child));
		iu.closeFile();
	}
	public void createDaoBean(String parent,String path){
		System.out.println("*******Started Calling createDaoBean()..");
		setParent(parent);
		String tmpparent=parent.substring(0,1).toUpperCase()+parent.substring(1).toLowerCase();
		iu.openNewFile(path+"/com/"+getAppName()+"/dao/",tmpparent+"Dao.java",false);
		TemplateTable result=getChildRelationObject( parent);
		this.deletetabs+="\"};";
		this.childtabs+="\"};";
		this.childtabalias+="\"};";
		String [] child =new String[result.getRowCount()];
		if (result.getRowCount()>0){

			for(int i=0;i<result.getRowCount();i++)
				child[i]=result.getFieldValue("childname",i);
		}
		iu.writeToFile(makeDaoBean(tmpparent,child));
		iu.closeFile();
		System.out.println("*******End Calling createDaoBean()..");
		System.out.println("*******Generated File="+path+"/com/"+getAppName()+"/dao/"+tmpparent+"Dao.java");
	}

	public void createImplBean(String parent,String path){
		System.out.println("*******Started Calling createImplBean()..");
		setParent(parent);
		String parentrelation="";
		//First get parent relation
		String sql="select do.objectname,do.aliasname,do.objecttype, ar.relationname,ar.ismandatory,ar.currentstate from sml_AttributeRelation ar ,sml_DbObject do where "+
				" do.objid=ar.attributerelation2dbobject and ltrim(rtrim(do.objectname))='"+parent+"'";
		TemplateTable parentrel=tu.getResultSet(sql);
		if(parentrel.getRowCount()>0){
			for(int i=0;i<parentrel.getRowCount(); i++){
				if(parentrel.getFieldValue("ismandatory",i).equalsIgnoreCase("yes")&&parentrel.getFieldValue("currentstate",i).equalsIgnoreCase("1")){
					parentrelation=parentrel.getFieldValue("relationname",i);
					break;
				}
			}
		}

		String tmpparent=parent.substring(0,1).toUpperCase()+parent.substring(1).toLowerCase();
		iu.openNewFile(path+"/com/"+getAppName()+"/bean/",tmpparent+"Impl.java",false);
		TemplateTable result=getChildRelationObject( parent);
		String [] child =new String[result.getRowCount()];
		String [] childrel =new String[result.getRowCount()];
		String  childobjfilter ="";
		objectfilter="\n\n\t\t public String "+tmpparent+"Filter=\""+getObjectViewField(parent)+getParentObjectFilter(parent)+"\";";
		if (result.getRowCount()>0)
			for(int i=0;i<result.getRowCount();i++){

				child[i]=result.getFieldValue("childname",i);
				childrel[i]=result.getFieldValue("relationname",i);
				childobjfilter=result.getFieldValue("objectfilter",i);
				childobjfilter=(childobjfilter!=null&&childobjfilter.equals("null"))?"":childobjfilter;
				String tmpchild=child[i].substring(0,1).toUpperCase()+child[i].substring(1).toLowerCase();
				String filterval=childobjfilter.replaceAll("\\^", "'");
				objectfilter+="\n\t\t public String "+tmpchild+"Filter=\""+getObjectViewField(child[i])+filterval+"\";";

			}
		//System.out.println(objectfilter);
		iu.writeToFile(makeImplBean(tmpparent,parentrelation,child,childrel));
		iu.closeFile();
		System.out.println("*******End Calling createImplBean()..");
	}
	public TemplateTable getParentRelationObject(String parent){

		if(this.parentrelation==null){

			String sql="select ar.parenttable,ar.relationname,ar.relationtype,ar.tabindex,ar.defaultfilter,ar.ismandatory,do.objectname childname,do.aliasname childtitle,do.objecttype,do.objectfilter "+
					" from sml_AttributeRelation ar,sml_DbObject do where do.objid=ar.attributerelation2dbobject "+
					//" and ltrim(rtrim(ar.CurrentState))='1'"+
					" and upper(do.objecttype) <> upper('View')"+
					" and upper(ar.relationname) like upper('"+parent+"2%') order by ar.tabindex,do.objectname" ;
			this.parentrelation=tu.getResultSet(sql);
		}
		return(parentrelation);
	}
	
	public TemplateTable getParentProperty(String parent){
        if(parentproperty==null){
        	String sql="select do.* "+
				" from sml_DbObject do where upper(ltrim(rtrim(do.objectname)))=upper('"+parent+"') and upper(do.objecttype)=upper('Table')" ;
        	parentproperty=tu.getResultSet(sql);
        }
		return(parentproperty);
	}
	public String getParentObjectFilter(String parent){
		String filter="";
		
		String sql="select do.* "+
				" from sml_DbObject do where upper(ltrim(rtrim(do.objectname)))=upper('"+parent+"')" ;
		TemplateTable tab=tu.getResultSet(sql);
		
		if (tab.getRowCount()>0){
			
			filter=tab.getFieldValue("objectfilter",tab.getRowCount()-1);
			filter=(filter!=null&&filter.equals("null"))?"":filter;
		}
		String val=filter.replaceAll("\\^","'");
		
		return(val);
	}

	public String getObjectViewField(String object){
		System.out.println("########Calling getObjectViewField() for "+object);

		String field="";
		objectfilterfield="";
		String sql="select at.* "+
				" from sml_DbObject do,sml_dbattribute at where do.objid=at.dbattribute2dbobject and upper(at.isViewField)='YES' and upper(ltrim(rtrim(do.objectname)))=upper('"+object+"')" ;
		TemplateTable result=tu.getResultSet(sql);
		int count=result.getRowCount();
		if (count>0){
			for(int i=0;i<count;i++){
				field=result.getFieldValue("fieldtype",i)+" "+result.getFieldValue("attributename",i);
				objectfilterfield+=(objectfilterfield.equals("")?field:","+field);
			}
		}
		
		String filter=objectfilterfield.equals("")?"":objectfilterfield+"@";
		String val=filter.replaceAll("\\^","'");
		
		return(val);
		
	}

	public TemplateTable getChildRelationObject(String parent){

		if( this.childrelation==null){

			String sql="select ar.parenttable,ar.relationname,ar.relationtype,ar.tabindex,do.objectname childname,do.aliasname childtitle,do.ALLOWDELETE,do.objecttype,do.objectfilter "+
					" from sml_AttributeRelation ar,sml_DbObject do where do.objid=ar.attributerelation2dbobject "+
					" and ltrim(rtrim(ar.CurrentState))='1'"+
					" and upper(ltrim(rtrim(ar.parenttable)))=upper('"+parent+"') order by ar.tabindex,do.objectname" ;

			this.childrelation=tu.getResultSet(sql);
			this.childtabs+=this.getTabAsString(this.childrelation, "childname").toLowerCase();
			this.deletetabs+=this.getDeleteTabAsString(this.childrelation).toLowerCase();
			this.childtabalias+=this.getTabAsString(this.childrelation, "childtitle");
		}

		return(this.childrelation);
	}

	public TemplateTable getChildMandatoryRelationObject(String child, String parent){

		String sql="select ar.parenttable,ar.relationname,ar.relationtype,ar.tabindex,do.objectname childname,do.aliasname childtitle,do.objecttype,do.objectfilter "+
				" from sml_AttributeRelation ar,sml_DbObject do where do.objid=ar.attributerelation2dbobject "+
				" and upper(ar.relationname) like upper('"+child+"2%') "+
				" and  upper(ar.relationname) not like upper('%2"+parent+"') "+
				" and upper(ar.ismandatory)=upper('yes') order by ar.tabindex,do.objectname";

		return(tu.getResultSet(sql));
	}


	public TemplateTable getTableFields(String table){
		String sql="select ap.AppName,do.ObjectName,do.objecttype,at.ObjId Attributeid,at.AttributeName,at.AttrIndex,at.AliasName,at.Behavior,"+
				"at.DomainName,at.IsNull,at.DefaultValue,at.HasProperty,at.HasCodeObject,at.FieldType,at.IsViewField,at.SelectMandatory"+
				",at.IsSearchField,at.IsBaseline,at.Version,at.Remark,ad.OracleDataType,ad.MssqlDataType,ad.JavaDataType "+
				",ad.DataSize,ad.DecimalSize,ad.Remark DomainRemark  "+
				"  from sml_application ap, sml_dbObject do,sml_dbattribute at,sml_AttributeDomain ad  "+
				"where ap.objid=do.dbobject2application and do.objid=at.dbattribute2dbobject  "+

               " and at.dbattribute2attributedomain=ad.objid "+
               "and upper(do.objectname)=upper('"+table+"') and upper(at.isViewField)=upper('no') order by at.AttrIndex";
		return tu.getResultSet(sql);

	}


	public TemplateTable getChildTableFields(String table){
		String sql="select  decode(at.DefaultValue, 'null',1,2) as idx , at.AttrIndex,ap.AppName,do.ObjectName,do.objecttype,at.ObjId Attributeid,at.AttributeName,at.AliasName,at.Behavior,"+
				"at.DomainName,at.IsNull,at.DefaultValue,at.HasProperty,at.HasCodeObject,at.FieldType,at.IsViewField,at.SelectMandatory"+
				",at.IsSearchField,at.IsBaseline,at.Version,at.Remark,ad.OracleDataType,ad.MssqlDataType,ad.JavaDataType "+
				",ad.DataSize,ad.DecimalSize,ad.Remark DomainRemark "+
				"  from sml_application ap, sml_dbObject do,sml_dbattribute at,sml_AttributeDomain ad  "+
				"where ap.objid=do.dbobject2application and do.objid=at.dbattribute2dbobject  "+
				" and at.dbattribute2attributedomain=ad.objid "+
				" and upper(do.objectname)=upper('"+table+"') and  upper(at.isNull)=upper('no') "+
				" and upper(at.DefaultValue) not like 'DISABLE%' "+
				 
				" union "+
				
				"select  decode(at.DefaultValue, 'null',1,2) as idx , at.AttrIndex,ap.AppName,do.ObjectName,do.objecttype,at.ObjId Attributeid,at.AttributeName,at.AliasName,at.Behavior,"+
				"at.DomainName,at.IsNull,at.DefaultValue,at.HasProperty,at.HasCodeObject,at.FieldType,at.IsViewField,at.SelectMandatory"+
				",at.IsSearchField,at.IsBaseline,at.Version,at.Remark,ad.OracleDataType,ad.MssqlDataType,ad.JavaDataType "+
				",ad.DataSize,ad.DecimalSize,ad.Remark DomainRemark "+
				"  from sml_application ap, sml_dbObject do,sml_dbattribute at,sml_AttributeDomain ad  "+
				"where ap.objid=do.dbobject2application and do.objid=at.dbattribute2dbobject  "+
				" and at.dbattribute2attributedomain=ad.objid "+
				" and upper(do.objectname)=upper('"+table+"') and (upper(at.isNull)=upper('no')" +
				" and upper(at.DefaultValue) like 'DISABLE%'  or upper(at.SelectMandatory)=upper('yes')) order by 2,1"
				;
		return tu.getResultSet(sql);

	}
	public TemplateTable getTableSummaryFields(String table){
		String sql="select ap.AppName,do.ObjectName,do.objecttype,at.ObjId Attributeid,at.AttributeName,at.AttrIndex,at.AliasName,at.Behavior,"+
				"at.DomainName,at.IsNull,at.DefaultValue,at.HasProperty,at.HasCodeObject,at.FieldType,at.IsViewField,at.SelectMandatory"+
				",at.IsSearchField,at.IsBaseline,at.Version,at.Remark,ad.OracleDataType,ad.MssqlDataType,ad.JavaDataType "+
				",ad.DataSize,ad.DecimalSize,ad.Remark DomainRemark  "+
				"  from sml_application ap, sml_dbObject do,sml_dbattribute at,sml_AttributeDomain ad  "+
				"where ap.objid=do.dbobject2application and do.objid=at.dbattribute2dbobject  "+
				" and at.dbattribute2attributedomain=ad.objid "+
				"and upper(do.objectname)=upper('"+table+"') and upper(at.isViewField)=upper('yes') order by at.AttrIndex";
		return tu.getResultSet(sql);

	}
	public TemplateTable getTableReportFields(String table){
		String sql="select ap.AppName,do.ObjectName,do.objecttype,at.ObjId Attributeid,at.AttributeName,at.AttrIndex,at.AliasName,at.Behavior,"+
				"at.DomainName,at.IsNull,at.DefaultValue,at.HasProperty,at.HasCodeObject,at.FieldType,at.IsViewField,at.SelectMandatory"+
				",at.IsSearchField,at.IsBaseline,at.Version,at.Remark,ad.OracleDataType,ad.MssqlDataType,ad.JavaDataType "+
				",ad.DataSize,ad.DecimalSize,ad.Remark DomainRemark  "+
				"  from sml_application ap, sml_dbObject do,sml_dbattribute at,sml_AttributeDomain ad  "+
				"where ap.objid=do.dbobject2application and do.objid=at.dbattribute2dbobject  "+
				" and at.dbattribute2attributedomain=ad.objid "+
				"and upper(do.objectname)=upper('"+table+"') and upper(at.SelectMandatory)=upper('yes')";
		return tu.getResultSet(sql);

	}
	public TemplateTable getTableSearchFields(String table){
		String sql="select ap.AppName,do.ObjectName,do.objecttype,at.ObjId Attributeid,at.AttributeName,at.AttrIndex,at.AliasName,at.Behavior,"+
				"at.DomainName,at.IsNull,at.DefaultValue,at.HasProperty,at.HasCodeObject,at.FieldType,at.IsViewField,at.SelectMandatory"+
				",at.IsSearchField,at.IsBaseline,at.Version,at.Remark,ad.OracleDataType,ad.MssqlDataType,ad.JavaDataType "+
				",ad.DataSize,ad.DecimalSize,ad.Remark DomainRemark  "+
				"  from sml_application ap, sml_dbObject do,sml_dbattribute at,sml_AttributeDomain ad  "+
				"where ap.objid=do.dbobject2application and do.objid=at.dbattribute2dbobject  "+
				" and at.dbattribute2attributedomain=ad.objid "+
				"and upper(do.objectname)=upper('"+table+"') and upper(at.IsSearchField)=upper('yes')";
		return tu.getResultSet(sql);

	}

	String getTabAsString(TemplateTable result,String field){
		String tmp="";
		for(int i=0;i<result.getRowCount();i++){

			tmp+=(tmp.isEmpty()?"": ",")+result.getFieldValue(field,i);

		}
		return tmp;
	}
	String getDeleteTabAsString(TemplateTable result){
		String tmp="";
		for(int i=0;i<result.getRowCount();i++){
			if(result.getFieldValue("objecttype",i).equalsIgnoreCase("table")){
				tmp+=(tmp.isEmpty()?"": ",")+result.getFieldValue("childname",i);
			}

		}
		return tmp;
	}
	private String getFieldAsString(TemplateTable tab,String field){
		String tmp="";
		for(int i=0;i<tab.getRowCount();i++){
			if(!tab.getFieldValue(field,i).isEmpty()){
				tmp+=",\""+ tab.getFieldValue(field,i)+"\"";
			}
		}
		return tmp;

	}
	private String getRelationFilterAsString(TemplateTable tab,String field){
		String tmp="";
		for(int i=0;i<tab.getRowCount();i++){
			if(!tab.getFieldValue(field,i).isEmpty()){
				tmp+=",#select_filter";
			}
		}
		return tmp;

	}

	private String getFieldDataTypeAsString(TemplateTable result){
		String tmp="";
		for(int i=0;i<result.getRowCount();i++){

			tmp+=","+((result.getFieldValue("OracleDataType",i).toLowerCase().indexOf("varchar")>=0)? "DataType.VARCHAR":
				(result.getFieldValue("OracleDataType",i).toUpperCase().trim().indexOf("LONG")>=0)?"DataType.VARCHAR":
				(result.getFieldValue("OracleDataType",i).toUpperCase().trim().indexOf("RAW")>=0)?"DataType.VARCHAR":
				(result.getFieldValue("OracleDataType",i).toUpperCase().trim().indexOf("NUMBER")>=0)?"DataType.NUMBER":"DataType."+result.getFieldValue("OracleDataType",i).toUpperCase());

		}
		return tmp;
	}
	
	private String getFilterAsString(TemplateTable result){
		String tmp="";
		for(int i=0;i<result.getRowCount();i++){
            if(result.getFieldValue("IsSearchField",i).toLowerCase().equalsIgnoreCase("yes")){
            	tmp+=","+((result.getFieldValue("Behavior",i).toLowerCase().indexOf("select")>=0)? "#select_filter":
				(result.getFieldValue("Behavior",i).toUpperCase().trim().indexOf("text")>=0)?"#text_filter":
					(result.getFieldValue("OracleDataType",i).toUpperCase().trim().indexOf("NUMBER")>=0)?"#numeric_filter":
						"#text_filter");
            }else{
            	tmp+=",";
            }

		}
		return tmp;
	}

	private String getRelationDataTypeAsString(int relationCount){
		String reldatatype="";
		for(int i=0;i<relationCount;i++){
			reldatatype+=",DataType.INTEGER";
		}
		return reldatatype;
	}
	private String getMandatoryRelationDisableAsString(int relationCount){
		String reldatatype="";
		for(int i=0;i<relationCount;i++){
			reldatatype+=",no";
		}
		return reldatatype;
	}
	private String getRelationDomainAsString(int relationCount){
		String reldomain="";
		for(int i=0;i<relationCount;i++){
			reldomain+=",\"Int_t\"";
		}
		return reldomain;
	}

	private String getDisabaledFieldAsString(TemplateTable result){
		String tmp="";
		for(int i=0;i<result.getRowCount();i++){

			tmp+=","+(result.getFieldValue("DefaultValue",i).toLowerCase().contains("disable")? "yes":"no");

		}
		return tmp;
	}
	private String getMandatoryRelationAsString(TemplateTable result){
		String tmp="";
		for(int i=0;i<result.getRowCount();i++){

			tmp+=(tmp.isEmpty()?"\"": ",\"")+result.getFieldValue("parenttable",i).toLowerCase()+":"+ 
			result.getFieldValue("RelationName",i).toLowerCase()+":list"+ 
			//(result.getFieldValue("ismandatory",i).equalsIgnoreCase("yes")?"list":"hiddin")+ 
			":"+ result.getFieldValue("defaultfilter",i).toLowerCase()+ "\"";

		}
		return tmp;
	}
	/*
	private String getMandatoryRelationAsString(TemplateTable result){
		String tmp="";
		for(int i=0;i<result.getRowCount();i++){

			tmp+=(tmp.isEmpty()?"\"": ",\"")+result.getFieldValue("RelationName",i).toLowerCase()+"\"";

		}
		return tmp;
	}
	*/
	private String getPropFieldAsString(TemplateTable result){
		String tmp="";
		for(int i=0;i<result.getRowCount();i++){
			if(result.getFieldValue("HasProperty",i).toLowerCase().equalsIgnoreCase("yes") &&
					result.getFieldValue("HasCodeObject",i).toLowerCase().equalsIgnoreCase("no")){
				tmp+=(tmp.isEmpty()?"\"": ",\"")+ result.getFieldValue("AttributeName",i).toLowerCase()+"\"";

			}
		}
		return tmp;
	}

	private String getCodeFieldAsString(TemplateTable result){
		String tmp="";
		for(int i=0;i<result.getRowCount();i++){
			if(result.getFieldValue("HasProperty",i).toLowerCase().equalsIgnoreCase("yes") &&
					result.getFieldValue("HasCodeObject",i).toLowerCase().equalsIgnoreCase("yes")){
				tmp+=(tmp.isEmpty()?"\"": ",\"")+ result.getFieldValue("AttributeName",i).toLowerCase()+"\"";

			}
		}
		return tmp;
	}

	public String makeDaoBean(String parent, String[]child){

		System.out.println("###### Started Executing makeDaoBean() ...");
		StringBuffer buffer =new StringBuffer();

		//parent data
		
		TemplateTable parentrelation=this.getParentRelationObject(parent);
		TemplateTable tablefld=this.getTableFields(parent);
		TemplateTable summaryfld=this.getTableSummaryFields(parent);
		TemplateTable reportfld=this.getTableReportFields(parent);
		TemplateTable searchfld=this.getTableSearchFields(parent);
		String dtabs="protected String []deletetabs={\"";
		String ctabs="protected String []childtabs={\"";
		String ctabalias="protected String []childtabnames={\"";
		if(summaryfld.getRowCount()>0){
			ctabs+=parent.toLowerCase()+",";
			ctabalias+=parent+" Facts,";
		}
		if(this.getParentProperty(parent)!=null &&parentproperty.getRowCount()>0 &&parentproperty.getFieldValue("objecttype", parentproperty.getRowCount()-1).equalsIgnoreCase("table")){
			dtabs+=parent.toLowerCase()+",";
		}
		this.deletetabs=dtabs+this.deletetabs;
		this.childtabs=ctabs+this.childtabs;
		this.childtabalias=ctabalias+this.childtabalias;
		
		String maintype="protected String [] maintype={\""+tablefld.getFieldValue("objecttype", tablefld.getRowCount()-1).toLowerCase()+"\"};";
		String maincol="protected String [] maincol={\"objid\""+getFieldAsString(parentrelation,"RelationName").toLowerCase()+getFieldAsString(tablefld,"AttributeName").toLowerCase()+"};";
		String maincolcaption="protected String [] maincolcaption={\"Id\""+getFieldAsString(parentrelation,"ParentTable")+getFieldAsString(tablefld,"AliasName")+"};";
		String mainSqlDatatype="protected String [] mainsqldatatype={DataType.VARCHAR"+getRelationDataTypeAsString(parentrelation.getRowCount())+getFieldDataTypeAsString(tablefld)+"};";
		String mainDataDomain="protected String [] maindatadomain={\"Int_t\""+getRelationDomainAsString(parentrelation.getRowCount())+getFieldAsString(tablefld,"DomainName")+"};";
		String maincolsearch="protected String [] maincolsearch={\"#text_filter"+getRelationFilterAsString(parentrelation,"RelationName").toLowerCase()+getFilterAsString(tablefld).toLowerCase()+"\"};";
		String maincoldisable="\n\t\tprotected String [] maincoldisable={\"yes"+getMandatoryRelationDisableAsString(parentrelation.getRowCount())+getDisabaledFieldAsString(tablefld)+"\"};";
		
		String summarycol="protected String [] summarycol={\"name\""+getFieldAsString(summaryfld,"AttributeName").toLowerCase()+"};";
		String summarycolcaption="protected String [] summarycolcaption={\"Name\""+getFieldAsString(summaryfld,"AliasName")+"};";
		String summarySqlDatatype="protected String [] summarysqldatatype={DataType.VARCHAR"+getFieldDataTypeAsString(summaryfld)+"};";
		String summaryDataDomain="protected String [] summarydatadomain={\"Name_t\""+getFieldAsString(summaryfld,"DomainName")+"};";

		String reportcol="protected String [] reportcol={\"objid\""+getFieldAsString(reportfld,"AttributeName").toLowerCase()+"};";
		String reportcolcaption="protected String [] reportcolcaption={\"Id\""+getFieldAsString(reportfld,"AliasName")+"};";
		String reportSqlDatatype="protected String [] reportsqldatatype={DataType.VARCHAR"+getFieldDataTypeAsString(reportfld)+"};";
		String reportDataDomain="protected String [] reportdatadomain={\"Id_t\""+getFieldAsString(reportfld,"DomainName")+"};";

		String searchcol="protected String [] searchcol={\"objid\""+getFieldAsString(searchfld,"AttributeName").toLowerCase()+"};";
		String searchcolcaption="protected String [] searchcolcaption={\"Id\""+getFieldAsString(searchfld,"AliasName")+"};";
		String searchColType="protected String [] searchcoltype={\"integer\""+getFieldAsString(searchfld,"Behavior")+"};";
		String searchDataDomain="protected String [] searchdatadomain={\"Id_t\""+getFieldAsString(searchfld,"DomainName")+"};";

		buffer.append("\n \t package com."+getAppName()+".dao; \n" +
				"\n \t import java.util.Map; \n " +
				"\t import java.util.ArrayList; \n"+
				"\t import java.util.Arrays; \n"+
				"\t import javax.ws.rs.core.Cookie;\n " +
				"\t import javax.ws.rs.core.HttpHeaders; \n" +
                 "\t import javax.ws.rs.core.UriInfo; \n" +
                 "\t import cms.service.app.ServiceManager;\n"+
                 "\t import cms.service.dhtmlx.*;\n" +
                 "\t import cms.service.exceptions.DaoException; \n" +
                 "\t import cms.service.exceptions.AuthenticationException;\n"+
                 "\t import cms.service.jdbc.DataType; \n" + 
                 "\t import cms.service.template.TemplateTable; \n" +
                 "\t import org.json.JSONException;\n"+
                 "\t import org.json.JSONObject;\n"+
                 "\t import com."+getAppName()+".bean.*; \n \n "+
                 "\t /** A simple bean that has a single String property \n" +
                 "\t *  called message. \n " +
                 "\t *  \n" +
                 "\t * @author S.K Jana Version 1.0 \n " +
                 "\t * @Copyright : This code belongs to BidERP.com. All right reserved! \n " +
                 "\t * @since 2005-2017 \n " +
				"\t */ \n");


		String tmptxt="\n\tpublic class " +parent +"Dao extends " +parent +"Impl {" +
				"\n\t\tMap<String, Cookie> cookies; "+
				"\n\t\tMap<String,String> userdata;"+
				"\n\t\t"+this.deletetabs+
				"\n\t\t"+this.childtabs+
				"\n\t\t"+this.childtabalias+
				"\n\t\t"+
				"\n\t\t"+maintype +
				"\n\t\t"+maincol +
				"\n\t\t"+maincolcaption +
				"\n\t\t"+mainSqlDatatype +
				"\n\t\t"+mainDataDomain +
				"\n\t\t"+maincolsearch+
				"\n\t\t"+maincoldisable+
				"\n\t\t"+
				"\n\t\t"+summarycol +
				"\n\t\t"+summarycolcaption +
				"\n\t\t"+summarySqlDatatype +
				"\n\t\t"+summaryDataDomain +
				"\n\t\t"+
				"\n\t\t"+reportcol +
				"\n\t\t"+reportcolcaption +
				"\n\t\t"+reportSqlDatatype +
				"\n\t\t"+reportDataDomain +
				"\n\t\t"+
				"\n\t\t"+searchcol +
				"\n\t\t"+searchcolcaption +
				"\n\t\t"+searchColType +
				"\n\t\t"+searchDataDomain ;

		tmptxt+="\n\n\tprotected String [] prop"+parent+"list={"+getPropFieldAsString(tablefld)+"};"+
				"\n\t\tprotected String [] code"+parent+"list={"+getCodeFieldAsString(tablefld)+"};"+
				"\n\t\tprotected String [] relation"+parent+"list={"+getMandatoryRelationAsString(parentrelation)+"};";
				

		String childcol="";
		String childmethods="";
		for (String table: child){
			System.out.println("###### Started Executing makeDaoBean() ... for "+table);
			TemplateTable childdata=this.getChildTableFields(table);
			TemplateTable mandatoryrel=this.getChildMandatoryRelationObject(table, parent);
			String childname=table.substring(0, 1).toUpperCase()+table.substring(1,table.length()).toLowerCase();
			String childtype="protected String [] " +table.toLowerCase()+"type={\""+childdata.getFieldValue("objecttype", childdata.getRowCount()-1).toLowerCase()+"\"};";
			String childfld="protected String [] "+table.toLowerCase()+"col={\"objid\""+getFieldAsString(mandatoryrel,"RelationName").toLowerCase()+getFieldAsString(childdata,"AttributeName").toLowerCase()+"};";
			String childcolcaption="protected String [] " +table.toLowerCase()+"colcaption={\"Id\""+getFieldAsString(mandatoryrel,"ParentTable")+getFieldAsString(childdata,"AliasName")+"};";
			String childDomain="protected String [] " +table.toLowerCase()+"datadomain={\"Int_t\""+getRelationDomainAsString(mandatoryrel.getRowCount())+getFieldAsString(childdata,"DomainName")+"};";
			String childSqlDatatype="protected String [] " +table.toLowerCase()+"sqldatatype={DataType.VARCHAR"+getRelationDataTypeAsString(mandatoryrel.getRowCount())+getFieldDataTypeAsString(childdata)+"};";
			String childDataDomain="protected String [] " +table.toLowerCase()+"disable={\"yes"+getMandatoryRelationDisableAsString(mandatoryrel.getRowCount())+getDisabaledFieldAsString(childdata)+"\"};";
			String childcolSearch="protected String [] "+table.toLowerCase()+"colsearch={\"#text_filter"+getRelationFilterAsString(mandatoryrel,"RelationName").toLowerCase()+getFilterAsString(childdata).toLowerCase()+"\"};";
			
			childcol+="\n\t\t"+
			        "\n\t\t"+childtype+
					"\n\t\t"+childfld +
					"\n\t\t"+childcolcaption +
					"\n\t\t"+childSqlDatatype +
					"\n\t\t"+childDomain +
					"\n\t\t"+childDataDomain+
					"\n\t\t"+childcolSearch;

			childmethods+="\n\n\t\tpublic Rows get"+childname+"Rows(){"+

			"\n\t\t\tTemplateTable tab=this.doSelectChild(\""+table.toLowerCase()+"\", \""+table.toLowerCase()+"2"+parent.toLowerCase()+"\",this.getParentobjid(),"
			+table.toLowerCase()+"col,"+table.toLowerCase()+"sqldatatype,this."+childname+"Filter);"+
			"\n\t\t\tString [] prop"+childname +"list={"+getPropFieldAsString(childdata)+"};"+
			"\n\t\t\tString [] code"+childname +"list={"+getCodeFieldAsString(childdata)+"};"+
			"\n\t\t\tString [] relation"+childname +"list={"+getMandatoryRelationAsString(mandatoryrel)+"};"+
			"\n\t\t\tRows rows=tu.getXMLRows(tab, \""+table.toLowerCase()+"\",code"+childname+"list,prop"+childname+"list,relation"+childname+"list,"+table.toLowerCase()+"colcaption,"+table.toLowerCase()+"datadomain,this.getGroupuser(),bqn);"+
			"\n\t\t\tArrayList<Userdata> userdata=rows.getUserdata();"+
			"\n\t\t\tUserdata data1= new Userdata(\"filters\",Arrays.asList("+childname.toLowerCase()+"colsearch));"+
			"\n\t\t\tUserdata data2= new Userdata(\"disablecols\",Arrays.asList("+childname.toLowerCase()+"disable));"+
			"\n\t\t\tUserdata data3= new Userdata(\"tabletype\",Arrays.asList("+childname.toLowerCase()+"type));"+
			
			"\n\t\t\tuserdata.add(data1);"+
			"\n\t\t\tuserdata.add(data2);"+
			"\n\t\t\tuserdata.add(data3);"+
			"\n\t\t\trows.setUserdata(userdata);"+
			"\n\t\t\treturn rows;"+
			"\n\t\t}\n\n";
			System.out.println("###### End Executing makeDaoBean() ... for "+table);

		}

		buffer.append(tmptxt +childcol);
		

		String daoconstructor=

				"\n\n\t\tpublic "+parent+"Dao(UriInfo uriInfo, HttpHeaders header) throws AuthenticationException{"+
						
						"\n\t\t\tthis.setObject(\""+parent+"\");"+
						"\n\t\t\tif(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst(\"generate_log\"))){"+
						"\n\t\t\t\t	ACONST.GENERATE_LOG=true;"+
						"\n\t\t\t}"+
						
						"\n\t\t\tif(!tu.isEmptyValue(uriInfo.getPathParameters().getFirst(\"id\"))){"+
						"\n\t\t\t\tthis.setParentobjid(uriInfo.getPathParameters().getFirst(\"id\").replace(\"id-\", \"\"));"+
						"\n\t\t\t}else if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst(\"searchfilter\"))){"+
						"\n\t\t\t\tthis.setSearchdata(uriInfo.getQueryParameters().getFirst(\"searchfilter\")+\"\"+(char)2);"+
						"\n\t\t\t}else{"+
						"\n\t\t\t\tthis.setSearchdata(\"ObjId\"+(char)1+\"All\"+(char)1+\"\"+(char)2);"+
						"\n\t\t\t}"+
						/*
						"\n\t\t\tif(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst(\"loginuser\"))){"+
						"\n\t\t\t\tthis.setUsername(uriInfo.getQueryParameters().getFirst(\"loginuser\"));"+
						"\n\t\t\t}else if(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst(\"username\"))){"+
						"\n\t\t\t\tthis.setUsername(uriInfo.getQueryParameters().getFirst(\"username\"));"+
						"\n\t\t\t}"+
						"\n\t\t\tif(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst(\"groupuser\"))){"+
						"\n\t\t\t\tthis.setGroupuser(uriInfo.getQueryParameters().getFirst(\"groupuser\"));"+
						"\n\t\t\t\tif(tu.isEmptyValue(this.username)){"+
						"\n\t\t\t\t\tthis.setUsername(this.groupuser);"+
						"\n\t\t\t\t}"+
						"\n\t\t\t\tthis.setSearchdata(this.getSearchdata()+\"groupuser\"+(char)1+\"=\"+(char)1+getGroupuser());"+
						"\n\t\t\t}"+
						*/
						"\n\t\t\tif(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst(\"token\"))){"+
						"\n\t\t\t\tthis.setToken(uriInfo.getQueryParameters().getFirst(\"token\"));"+
						"\n\t\t\t\tthis.userdata=ServiceManager.verifyUserToken(this.getToken());"+
						"\n\t\t\t}"+
						"\n\t\t\tif(this.userdata!=null &&!this.userdata.isEmpty()){"+
						"\n\t\t\t\tthis.groupuser=userdata.get(\"groupuser\");"+
						"\n\t\t\t\tthis.username=userdata.get(\"username\");"+
						"\n\t\t\t\tthis.setSearchdata(this.getSearchdata()+\"groupuser\"+(char)1+\"=\"+(char)1+getGroupuser());"+
						"\n\t\t\t}else{"+
						"\n\t\t\t\tthrow new AuthenticationException(\"Authentication Failed for user=\"+username+\" Token =\"+ this.getToken());"+
						"\n\t\t\t}"+
						"\n\t\t\tif(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst(\"pagesize\"))){"+
						"\n\t\t\t\tthis.setPagesize(Integer.parseInt(uriInfo.getQueryParameters().getFirst(\"pagesize\")));"+
						"\n\t\t\t}"+
						"\n\t\t\tif(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst(\"bqn\"))){"+
						"\n\t\t\t\tthis.setBqn(uriInfo.getQueryParameters().getFirst(\"bqn\"));"+
						"\n\t\t\t}"+
						"\n\t\t\tif(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst(\"page\"))){"+
						"\n\t\t\t\tthis.setPage(Integer.parseInt(uriInfo.getQueryParameters().getFirst(\"page\")));"+
						"\n\t\t\t}"+
						"\n\t\t\tif(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst(\"X-Forwarded-For\"))){"+
						"\n\t\t\t\tthis.setClientip(uriInfo.getQueryParameters().getFirst(\"X-Forwarded-For\"));"+
						"\n\t\t\t}"+
						"\n\t\t\tif(!tu.isEmptyValue(uriInfo.getQueryParameters().getFirst(\"filters\"))){"+
						"\n\t\t\t\tthis.setFilters(uriInfo.getQueryParameters().getFirst(\"filters\"));"+
						"\n\t\t\t}"+
						"\n\t\t\tif(ACONST.GENERATE_LOG){"+
						"\n\t\t\t\tlogger.info(\"getPathParameters=\"+uriInfo.getPathParameters().values());"+
						"\n\t\t\t\tlogger.info(\"getQueryParameters=\"+uriInfo.getQueryParameters().values());"+
						"\n\t\t\t\tlogger.info(\"User Data=\"+this.userdata.toString());"+
						"\n\t\t\t}"+
						"\n\t\t\tthis.cookies=header.getCookies();"+
						"\n\t\t}";

		String setxml=

				"\n\n\t\tpublic void setPostXml(String xml) throws DaoException{"+
						"\n\t\t\tif(tu.isEmptyValue(xml)) throw new DaoException(\"ERROR: Post XML Is null or empty\");"+
						"\n\t\t\tif(!xml.contains(\"<?xml\")) throw new DaoException(\"ERROR: Please provide xml document header at the begining of each entity in the POST XML body.\");";

		//"\n\t\t\t\"\n Example: \n<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n<project>\n\t <record id=\"0\">\n\t <ObjId isRequired=\"false\" type=\"VARCHAR\">1</ObjId>\n\t...\n</record>\n</project>\n\"+"+
		//"\n\t\t\t\"\n<?xml version=\"1.0\" encoding=\"UTF-8\"?> \n<jobmaster>\n\t <record id=\"0\">\n\t <ObjId isRequired=\"false\" type=\"VARCHAR\">1</ObjId>\n\t...\n</record>\n\n\t <record id=\"1\">\"+"+
		//"\n\t\t\t\"\n\t <ObjId isRequired=\"false\" type=\"VARCHAR\">2</ObjId>\n\t...\n</record>\n</jobmaster>\n You Provided XML:\n\"+xml);" ;

		setxml+=
				"\n\t\t\tString [] entitys=xml.split(\"<?xml\");"+

        				"\n\t\t\tfor(String entity:entitys){"+
        				"\n\t\t\t\tString tmp=\"\";"+
        				"\n\t\t\t\tif(entity.toLowerCase().contains(\"<"+parent.toLowerCase()+">\")){"+
        				"\n\t\t\t\t\ttmp=entity.replace(\"<?\", \"\");"+
        				"\n\t\t\t\t\tthis.setMainxml(\"<?xml\"+tmp);"+
        				"\n\t\t\t\t\tif(ACONST.GENERATE_LOG){"+
        				"\n\t\t\t\t\t\tlogger.info(\"Setting Main XML=\"+this.getMainxml());"+
        				"\n\t\t\t\t\t}";

		for (String table:child){

			String tmpname=table.substring(0, 1).toUpperCase()+table.substring(1,table.length()).toLowerCase();
			setxml+=	"\n\t\t\t\t}else if(entity.toLowerCase().contains(\"<"+table.toLowerCase()+">\")){"+
					"\n\t\t\t\t\tthis.set"+tmpname+"xml(\"<?xml\"+entity);"+
					"\n\t\t\t\t\tif(ACONST.GENERATE_LOG){"+
					"\n\t\t\t\t\t\tlogger.info(\"Setting Child XML=\"+this.get"+tmpname+"xml());"+
					"\n\t\t\t\t\t}";

		}
		setxml+="\n\t\t\t\t}"+
				"\n\t\t\t}"+
				"\n\t\t}";

		//String parentmethods="";
		
		String parentmethods="\n\n\t\tpublic Rows get"+parent+"SummaryRows(){"+
		         "\n\t\t\tTemplateTable tab=this.doSelect(summarycol,summarysqldatatype,this."+parent+"Filter);"+
		         "\n\t\t\tArrayList<String> chartcols=tu.getChartSelectColumns(\""+parent+"\");"+
		         "\n\t\t\tArrayList<String> moneycols=tu.getSummaryMoneyColsIndex(summarydatadomain);"+
		         "\n\t\t\tRows rows=tu.getXMLSummaryRows(tab,summarycolcaption);"+
		         "\n\t\t\tArrayList<Userdata> userdata=rows.getUserdata();"+
        		  "\n\t\t\tUserdata data1= new Userdata(\"charts\",chartcols);"+
        		  "\n\t\t\tuserdata.add(data1);"+
        		  "\n\t\t\tfor(String chartcol:chartcols){"+
        		  "\n\t\t\t\tArrayList<String> datas= tu.getChartPropertyJSON(\""+parent+"\", tab, chartcol);"+
        		  "\n\t\t\t\tArrayList<String> data2= new ArrayList<String>();"+
        		  "\n\t\t\t\tdata2.add(datas.get(0));"+
        		  "\n\t\t\t\tUserdata chart= new Userdata(chartcol+\".chart\",data2);"+
        		  "\n\t\t\t\tuserdata.add(chart);"+
        		  "\n\t\t\t\tArrayList<String> data3= new ArrayList<String>();"+
        		  "\n\t\t\t\tdata3.add(datas.get(1));"+
        		  "\n\t\t\t\tUserdata griddata= new Userdata(chartcol+\".data\",data3);"+
        		  "\n\t\t\t\tuserdata.add(griddata);"+
        		  "\n\t\t\t}"+
        		  "\n\t\t\tUserdata data4= new Userdata(\"grid.moneycols\",moneycols);"+
        		  "\n\t\t\tuserdata.add(data4);"+
        		  "\n\t\t\trows.setUserdata(userdata);"+
        		  "\n\t\t\treturn rows;"+
        		  "\n\t\t}"+

        		  "\n\n\t\tpublic Rows get"+parent+"Rows(){"+

        		  "\n\t\t\tTemplateTable tab=this.doSelect(maincol,mainsqldatatype,null);"+

        		  "\n\t\t\tRows rows=tu.getXMLRows(tab, \""+parent+"\",code"+parent+"list,prop"+parent+"list,relation"+parent+"list,maincolcaption,maindatadomain,this.getGroupuser(),bqn);"+
        		  "\n\t\t\tArrayList<Userdata> userdata=rows.getUserdata();"+
        		  "\n\t\t\tUserdata data1= new Userdata(\"tabs\",Arrays.asList(childtabs));"+
        		  "\n\t\t\tUserdata data2= new Userdata(\"tabnames\",Arrays.asList(childtabnames));"+
        		  "\n\t\t\tUserdata data3= new Userdata(\"filters\",Arrays.asList(maincolsearch));"+
        		  "\n\t\t\tUserdata data4= new Userdata(\"deletetabs\",Arrays.asList(deletetabs));"+
        		  "\n\t\t\tUserdata data5= new Userdata(\"disablecols\",Arrays.asList(maincoldisable));"+
        		  "\n\t\t\tUserdata data6= new Userdata(\"tabletype\",Arrays.asList(maintype));"+
        		  "\n\t\t\tuserdata.add(data1);"+
        		  "\n\t\t\tuserdata.add(data2);"+
        		  "\n\t\t\tuserdata.add(data3);"+
        		  "\n\t\t\tuserdata.add(data4);"+
        		  "\n\t\t\tuserdata.add(data5);"+
        		  "\n\t\t\tuserdata.add(data6);"+
        		  "\n\t\t\trows.setUserdata(userdata);"+
        		  "\n\t\t\treturn rows;"+
        		  "\n\t\t}"+

        		  "\n\n\t\tpublic Rows get"+parent+"RowModified(){"+

        		  "\n\t\t\tRows rows=tu.getXMLRows(maindata, \""+parent+"\",code"+parent+"list,prop"+parent+"list,relation"+parent+"list,maincolcaption,maindatadomain,this.getGroupuser(),bqn);"+
        		  "\n\t\t\treturn rows;"+
        		  "\n\t\t}"+
        		  "\n\n\t\tpublic Rows get"+parent+"RowDeleted(){"+
        		  "\n\t\t\tRows rows;"+
        		  "\n\t\t\tif(this.doDelete(childtabs)){"+
        		  "\n\t\t\t\trows=tu.getDeletedRows(this.getParentobjid());"+
        		  "\n\t\t\t}else{"+
        		  "\n\t\t\t\trows=tu.getDeletedRows(\"-1\");"+
        		  "\n\t\t\t}"+
        		  "\n\t\t\treturn rows;"+
        		  "\n\t\t}"+

        		  "\n\n\t\tpublic void post"+parent+"Container() throws DaoException{"+
		 
        		  "\n\t\t\tif(!tu.isEmptyValue(this.getMainxml())){"+
        		  "\n\t\t\t\tthis.doInsert();"+
        		  "\n\t\t\t}else{"+
        		  "\n\t\t\t\tthrow new DaoException(\"ERROR: Post unsuccessful! Probably your XML is missing parent entity or having error!\", this.getClass().getName());"+
        		  "\n\t\t\t}"+

        		  "\n\t\t}"+

		 "\n\n\t\tpublic Rows get"+parent+"ByFilter(){"+
		 "\n\t\t\tString newfilter=\" groupuser='\"+this.getGroupuser()+\"'\";"+
		"\n\t\t\tif(!tu.isEmptyValue(this.getFilters()) &&this.getFilters().toLowerCase().indexOf(\""
		+parent.toLowerCase()+"2\")<0){"+
		"\n\t\t\t\tnewfilter+=\" and "+parent.toLowerCase()+"2\"+this.getFilters();"+
		"\n\t\t\t}else{"+
		"\n\t\t\t\tnewfilter+=\" and \"+this.getFilters();"+
		"\n\t\t\t}"+
		 "\n\t\t\tString sql= \"select * from table_"+parent+" where \"+ newfilter;"+
		 "\n\t\t\tTemplateTable tab=tu.getResultSet(sql);"+
		 "\n\t\t\tRows rows=tu.getXMLFilterRows(tab, \""+parent+"\",code"+parent+"list,prop"+parent+"list,relation"+parent+"list,maincol,maincolcaption,maindatadomain,(QueryImpl)this);"+
		 "\n\t\t\tArrayList<Userdata> userdata=rows.getUserdata();"+
		 "\n\t\t\tUserdata data1= new Userdata(\"tabs\",Arrays.asList(childtabs));"+
		 "\n\t\t\tUserdata data2= new Userdata(\"tabnames\",Arrays.asList(childtabnames));"+
		 "\n\t\t\tUserdata data3= new Userdata(\"filters\",Arrays.asList(maincolsearch));"+
		 "\n\t\t\tUserdata data4= new Userdata(\"deletetabs\",Arrays.asList(deletetabs));"+
		 "\n\t\t\tUserdata data5= new Userdata(\"disablecols\",Arrays.asList(maincoldisable));"+
		 "\n\t\t\tUserdata data6= new Userdata(\"tabletype\",Arrays.asList(maintype));"+ 	
		 "\n\t\t\tuserdata.add(data1);"+
		 "\n\t\t\tuserdata.add(data2);"+
		 "\n\t\t\tuserdata.add(data3);"+
		 "\n\t\t\tuserdata.add(data4);"+
		 "\n\t\t\tuserdata.add(data5);"+
		 "\n\t\t\tuserdata.add(data6);"+
		 "\n\t\t\trows.setUserdata(userdata);"+
		 "\n\t\t\treturn rows;"+
		 "\n\t\t}\n"+
		 "\n\t\t public JSONObject get"+parent+"RecordCount() throws JSONException{"+
		 "\n\t\t\tString newfilter=\" groupuser='\"+this.getGroupuser()+\"'\";"+
		 "\n\t\t\tif(!tu.isEmptyValue(this.getFilters()) &&this.getFilters().toLowerCase().indexOf(\""
		+parent.toLowerCase()+"2\")<0){"+
		"\n\t\t\t\tnewfilter+=\" and "+parent.toLowerCase()+"2\"+this.getFilters();"+
		"\n\t\t\t}else{"+
		"\n\t\t\t\tnewfilter+=\" and \"+this.getFilters();"+
		"\n\t\t\t}"+
		 "\n\t\t\tString sql= \"select * from table_"+parent+" where \"+ newfilter;"+
		 "\n\t\t\tTemplateTable tab=tu.getResultSet(sql);"+
		 "\n\t\t\tJSONObject data=new JSONObject();"+
		 "\n\t\t\tdata.put(\"table\",this.getObject());"+
		 "\n\t\t\tdata.put(\"rowcount\",tab.getRowCount());"+
		 "\n\t\t\treturn data;"+
		 "\n\t\t\t}";


		buffer.append(daoconstructor+setxml+childmethods+parentmethods);

		buffer.append("\n\t}");
		System.out.println("###### End Executing makeDaoBean() ...");
		return(buffer.toString());
	}

	public String makeImplBean(String parent,String parentrelation, String[]child,String[] childrelation){

		System.out.println("###### Started Executing makeImplBean() ...");

		StringBuffer buffer =new StringBuffer();
		
		
		buffer.append("\n \t package com."+getAppName()+".bean; \n" +


                 "\n \t import org.apache.commons.logging.Log; \n " +
                 "\t import org.apache.commons.logging.LogFactory; \n " +
                 "\t import cms.service.app.ApplicationConstants; \n" +
                 "\t import cms.service.event.EventListener;\n" + 
                 "\t import cms.service.event.QueryEvent;\n" + 
                 "\t import cms.service.template.*; \n" +

                 "\t /** A simple bean that has a single String property \n" +
                 "\t *  called message. \n " +
                 "\t *  \n" +
                 "\t * @author S.K Jana Version 1.0 \n " +
                 "\t * @Copyright : This code belongs to BidERP.com. All right reserved! \n " +
                 "\t * @since 2005-2017 \n " +
                 "\t */ \n"+
                 "\n\t public class "+parent+"Impl implements QueryEvent {");

		String protectedvar="";
		String childsettergetter="";
		String childtemplatetable="";
		for(String obj:child){
			protectedvar+="\n\t\t protected String "+obj.toLowerCase()+"xml,"+obj.toLowerCase()+"deleteid;";
			childsettergetter +="\n\t\t public String get"+obj.substring(0,1).toUpperCase()+obj.substring(1,obj.length()).toLowerCase()+"xml() {"+
					"\n\t\t\t return "+ obj.toLowerCase()+"xml;"+
					"\n\t\t }"+
					"\n\t\t public void set"+obj.substring(0,1).toUpperCase()+obj.substring(1,obj.length()).toLowerCase()+"xml(String "+ obj.toLowerCase()+"xml) {"+
					"\n\t\t\t this."+obj.toLowerCase()+"xml="+obj.toLowerCase()+"xml;"+
					"\n\t\t }"+
					"\n\t\t public String get"+obj.substring(0,1).toUpperCase()+obj.substring(1,obj.length()).toLowerCase()+"deleteid() {"+
					"\n\t\t\t return "+ obj.toLowerCase()+"deleteid;"+
					"\n\t\t }"+
					"\n\t\t public void set"+obj.substring(0,1).toUpperCase()+obj.substring(1,obj.length()).toLowerCase()+"deleteid(String "+ obj.toLowerCase()+"deleteid) {"+
					"\n\t\t\t this."+obj.toLowerCase()+"deleteid="+obj.toLowerCase()+"deleteid;"+
					"\n\t\t }";
			childtemplatetable+="\n\n\t\t protected TemplateTable "+obj.toLowerCase()+"data=new TemplateTable();";
		}



		String declare="\n\t\t protected static Log logger = LogFactory.getLog("+parent+"Impl.class); \n " +
				"\t\t protected static ApplicationConstants ACONST =new ApplicationConstants(); \n " +
				"\t\t protected static TemplateUtility tu=new TemplateUtility();\n"+
				"\t\t protected TemplateTable maindata=new TemplateTable();\n"+
				"\t\t protected String searchdata,object,parentobjid,filters, bqn;\n" +
				"\t\t protected String username, groupuser,token,clientip; \n"+
				"\t\t protected int pagesize=30; \n " +
				"\t\t protected int page;\n " +
				"\t\t protected String mainxml;\n " ;



		buffer.append(declare+ protectedvar+childtemplatetable +objectfilter);

		String settergetter="\n\n\t\t public void setObject(String object){"+
				"\n\t\t\t this.object=object;"+
				"\n\t\t }"+
				"\n\t\t public String getObject(){"+
				"\n\t\t\t return(this.object);"+
				"\n\t\t }"+
				"\n\t\t public void setFilters(String filters){"+
				"\n\t\t\t this.filters=filters;"+
				"\n\t\t }"+
				"\n\t\t public String getFilters(){"+
				"\n\t\t\t return(filters);"+
				"\n\t\t }"+
				"\n\t\t public void setBqn(String bqn){"+
				"\n\t\t\t this.bqn=bqn;"+
				"\n\t\t }"+
				"\n\t\t public String getBqn(){"+
				"\n\t\t\t return(bqn);"+
				"\n\t\t }"+
				"\n\t\t public void setClientip(String clientip){"+
				"\n\t\t\t this.clientip=clientip;"+
				"\n\t\t }"+
				"\n\t\t public String getClientip(){"+
				"\n\t\t\t return(clientip);"+
				"\n\t\t }"+
				"\n\t\t public void setPagesize(int pagesize){"+
				"\n\t\t\t this.pagesize=pagesize;"+
				"\n\t\t }"+
				"\n\t\t public  int getPagesize(){"+
				"\n\t\t\t return(pagesize);"+
				"\n\t\t }"+
				"\n\t\t public void setPage(int page){"+
				"\n\t\t\t this.page=page;"+
				"\n\t\t }"+
				"\n\t\t public int getPage(){"+
				"\n\t\t\t return(page);"+
				"\n\t\t }"+
				"\n\t\t public void setToken(String token){"+
				"\n\t\t\t this.token=token;"+
				"\n\t\t }"+
				"\n\t\t public String getToken(){"+
				"\n\t\t\t return(token);"+
				"\n\t\t }"+
				"\n\t\t public void setUsername(String username){"+
				"\n\t\t\t this.username=username;"+
				"\n\t\t }"+
				"\n\t\t public String getUsername(){"+
				"\n\t\t\t return(username);"+
				"\n\t\t }"+
				"\n\t\t public void setMainxml(String mainxml){"+
				"\n\t\t\t this.mainxml=mainxml;"+
				"\n\t\t }"+
				"\n\t\t public String getMainxml(){"+
				"\n\t\t\t return(mainxml);"+
				"\n\t\t }"+
				"\n\t\t public void setGroupuser(String groupuser){"+
				"\n\t\t\t this.groupuser=groupuser;"+
				"\n\t\t }"+
				"\n\t\t public String getGroupuser(){"+
				"\n\t\t\t return(groupuser);"+
				"\n\t\t }"+
				"\n\t\t public void setSearchdata(String searchdata){"+
				"\n\t\t\t this.searchdata=searchdata;"+
				"\n\t\t }"+
				"\n\t\t public String getSearchdata(){"+
				"\n\t\t\t return(searchdata);"+
				"\n\t\t }"+
				"\n\t\t public void setParentobjid(String parentobjid){"+
				"\n\t\t\t this.parentobjid=parentobjid;"+
				"\n\t\t }"+
				"\n\t\t public String getParentobjid(){"+
				"\n\t\t\t return(parentobjid);"+
				"\n\t\t }";


		buffer.append(settergetter+childsettergetter);
		String childtabledata="";
		TemplateTable pdata=tu.getResultSet("select *from sml_attributerelation where upper(relationname) like upper('"+parent+"2MessageQueue')");
		String applyafterrule=(pdata.getRowCount()>0?
				"\n\t\t\t\t tu.applyConsoleObject(\""+parent+"\",maindata,this.getUsername(),groupuser,true);":"");

		applyafterrule+="\n\t\t\t\t tu.applyObjectRule(\""+parent+"\",ACONST.EVENT_REASON_INSERT, ACONST.EVENT_STATE_AFTER,maindata);"+
				"\n\t\t\t\t tu.applyMTMRelation(\""+parent+"\",\""+parent+"\",getParentobjid());"+
				"\n\t\t\t\t EventListener.registerPostInsertEvent(\""+parent+"\",maindata);";

		for (int i=0;i<(child==null?0:child.length);i++){
			childtabledata+="\n\t\t\t TemplateTable "+child[i].toLowerCase()+"data=new TemplateTable();";
			String tmptab=child[i].substring(0,1).toUpperCase()+child[i].substring(1).toLowerCase();
			applyafterrule+="\n\t\t\t\t tu.applyObjectRule(\""+tmptab+"\",ACONST.EVENT_REASON_INSERT, ACONST.EVENT_STATE_AFTER,"+child[i].toLowerCase()+"data);"+
					"\n\t\t\t\t tu.applyMTMRelation(\""+child[i].toLowerCase()+"\",\""+parent+"\",getParentobjid());"+
					"\n\t\t\t\t EventListener.registerPostInsertEvent(\""+child[i]+"\","+child[i].toLowerCase()+"data);";
			TemplateTable cdata=tu.getResultSet("select *from sml_attributerelation where upper(relationname) like upper( '"+child[i]+"2MessageQueue')");
			applyafterrule+=(cdata.getRowCount()>0? "\n\t\t\t\t tu.applyConsoleObject(\""+child[i].toLowerCase()+"\","+child[i].toLowerCase()+"data,this.getUsername(),groupuser,false);":"");


		}
		String doSelect="\n\n\n\t\t  public  TemplateTable  doSelect(String[] column,String[] datatype,String parentfilter){"+
				"\n\t\t\t String sql=\"\";"+
				"\n\t\t\t TemplateQuery query =new TemplateQuery();"+
				"\n\t\t\t//do some custom pre query operation if any "+               
				"\n\t\t\t EventListener.registerPreQueryParent(\""+parent+"\",column,datatype);"+
				"\n\t\t\t query.setUserName(this.getUsername());"+
				"\n\t\t\t if (tu.isEmptyValue(parentfilter)){"+
				"\n\t\t\t\t if(!tu.isEmptyValue(this.getParentobjid()))"+
				"\n\t\t\t\t\tquery.makeTableSelect(this.getObject(),\"ObjId\",\"=\",this.getParentobjid(),column,datatype);"+
				"\n\t\t\t\t else"+
				"\n\t\t\t\t\tquery.makeSQL(this.getObject(),query.getArrayData(this.getSearchdata()),column,datatype);"+
				"\n\t\t\t }else{"+
				"\n\t\t\t\t if(!tu.isEmptyValue(this.getParentobjid()))"+
				"\n\t\t\t\t\t query.makeTableSelectObjectFilter(this.getObject(),\"ObjId\",\"=\",this.getParentobjid(),column,datatype,parentfilter);"+
				"\n\t\t\t\t else"+
				"\n\t\t\t\t\t query.makeObjectFilterSQL(this.getObject(),query.getArrayData(this.getSearchdata()),column,datatype,parentfilter);"+
				"\n\t\t\t }"+
				"\n\t\t\t if(ACONST.GENERATE_LOG)"+
				"\n\t\t\t\t logger.info(query.getQuery());"+
				"\n\t\t\t if(this.getPage()>0){"+
				"\n\t\t\t\t int startrow=(this.getPage()-1)*getPagesize();"+
				"\n\t\t\t\t query.setStartRow(startrow);"+
				"\n\t\t\t\t query.setNumRows(getPagesize());"+
				"\n\t\t\t}"+
				"\n\t\t\t maindata=query.getTableResultset();"+
				"\n\t\t\t\t\t// do any post query operation for custom implementation"+
				"\n\t\t\t\t\tEventListener.registerPostQueryParent(\""+parent+"\",column,datatype);"+
				"\n\t\t\t return(maindata);"+
				"\n\t\t }"+
				"\n\n\n\t\t public  TemplateTable  doSelectChild(String childname,String relfield,String pid,String[]column,String[]datatype,String childfilter){"+
				"\n\t\t\tString sql=\"\"; "+
				"\n\t\t\tTemplateTable data =new TemplateTable();"+
				"\n\t\t\tTemplateQuery query =new TemplateQuery();"+
				"\n\t\t\t// Do some pre query child operation for custom implementation"+
				"\n\t\t\tEventListener.registerPreQueryChild(\""+parent+"\",childname,pid,relfield,column,datatype);"+
				"\n\t\t\tif (tu.isEmptyValue(childfilter)){"+
				"\n\t\t\t\tsql=query.makeChildSql(this.getObject(),childname,relfield,pid,column,datatype);"+
				"\n\t\t\t}else{"+
				"\n\t\t\t\t	sql=query.makeChildObjectFilterSql(this.getObject(),childname,relfield,pid,column,datatype,childfilter);"+
				"\n\t\t\t}"+
				"\n\t\t\tquery.setQuery(sql);"+
				"\n\t\t\tdata=query.getTableResultset();"+
				"\n\t\t\tif(ACONST.GENERATE_LOG)"+
				"\n\t\t\t\tlogger.info(query.getQuery());"+
				"\n\t\t\tif (data.getRowCount()>0){"+
				"\n\t\t\t//Do some post query operation for child"+
				"\n\t\t\t\t	EventListener.registerPostQueryChild(\""+parent+"\",childname,pid,relfield,column,datatype);"+

			"\n\t\t\t}"+
			"\n\t\t\treturn(data);"+
			"\n\t\t}"+
			"\n\t\tpublic  boolean   doDelete(String[] childtabs){"+
			"\n\t\t\tTemplateQuery query =new TemplateQuery();"+
			"\n\t\t\t// Do some pre delete operation "+
			"\n\t\t\tString sql=query.removeSql(\""+parent+"\",this.getParentobjid(),childtabs,groupuser);"+
			"\n\t\t\tif(!tu.isEmptyValue(sql)){"+
			"\n\t\t\t\ttu.applyObjectRuleForDelete(\""+parent+"\",ACONST.EVENT_REASON_DELETE, ACONST.EVENT_STATE_BEFORE,this.getParentobjid());"+
			"\n\t\t\t\tEventListener.registerPreDeleteEvent(\""+parent+"\",this.getParentobjid());"+
			"\n\t\t\t\tquery.setQuery(sql);"+
			"\n\t\t\t\tif(ACONST.GENERATE_LOG)"+
			"\n\t\t\t\t\tlogger.info(query.getQuery());"+
			"\n\t\t\t\tif (query.getTableResultset().getRowCount()>0){"+
			"\n\t\t\t\t	// Do some post delete operation"+
			"\n\t\t\t\t	tu.applyObjectRuleForDelete(\""+parent+"\",ACONST.EVENT_REASON_DELETE, ACONST.EVENT_STATE_AFTER,this.getParentobjid());"+
			"\n\t\t\t\t	EventListener.registerPostDeleteEvent(\""+parent+"\",this.getParentobjid());"+
			"\n\t\t\t\t	return(true);"+
			"\n\t\t\t\t}"+
			"\n\t\t\t}"+
			"\n\t\t\treturn(false);"+
			"\n\t\t}"+

			"\n\n\t\tpublic  boolean  doInsert(){"+
			"\n\t\t\tString sql=null;"+
			"\n\t\t\tString usql=\"\"; "+
			"\n\t\t\tTemplateQuery query =new TemplateQuery();"+
			"\n\t\t\tif(!tu.isEmptyValue(this.getMainxml())){"+
			"\n\t\t\t\t	sql=query.makeBulkSQL(true,this.getMainxml(),\""+parentrelation+"\",this.getUsername(),this.getGroupuser());"+
			"\n\t\t\t\t	maindata=query.getTableData();"+
			"\n\t\t\t\t	tu.applyObjectRule(\""+parent+"\",ACONST.EVENT_REASON_INSERT, ACONST.EVENT_STATE_BEFORE,maindata);"+
			"\n\t\t\t\t	EventListener.registerPreInsertEvent(\""+parent+"\",maindata);"+
			"\n\t\t\t}"+
			"\n\t\t\tif(sql==null){"+
			"\n\t\t\t\t	if(ACONST.GENERATE_LOG)"+
			"\n\t\t\t\t		logger.info(\" WARNING: Parent record exists! Duplicate record\");"+
			"\n\t\t\t	return(false);"+
			"\n\t\t\t}else{"+
			"\n\t\t\t\t if(ACONST.GENERATE_LOG)"+
			"\n\t\t\t\t logger.info(\"parent ObjId=\"+query.getObjId());"+
			"\n\t\t\t	setParentobjid(query.getObjId()); ";
		buffer.append(doSelect);
		String tmptxt="";


		for (int i=0;i<(child==null?0:child.length);i++){
			String usql="";
			String tmptab=child[i].substring(0,1).toUpperCase()+child[i].substring(1).toLowerCase();
			tmptxt+="\n\t\t\t\t if(!tu.isEmptyValue(this.get"+tmptab+"xml()) ){"+
					"\n\t\t\t\t\t sql+=query.makeBulkSQL(false,get"+tmptab+"xml(),\""+childrelation[i]+"\",this.getUsername(),this.getGroupuser());"+
					//"\n\t\t\t\t\t sql+=query.makeBulkRemoveSQL(\""+child[i]+"\",\""+ childrelation[i]+"\",this.get"+tmptab+"deleteid(),this.getUsername());"+
					"\n\t\t\t\t\t "+child[i].toLowerCase()+"data=query.getTableData();"+
					"\n\t\t\t\t\t tu.applyObjectRule(\""+tmptab+"\",ACONST.EVENT_REASON_INSERT, ACONST.EVENT_STATE_BEFORE,"+child[i].toLowerCase()+"data);"+
					"\n\t\t\t\t\t EventListener.registerPreInsertEvent(\""+child[i]+"\","+child[i].toLowerCase()+"data);";
					//"\n\t\t\t\t}";
			
			
			String autofield="";
			//find the attribute which are having fieldtype=auto for current table
			String asql="select ap.AppName,do.ObjectName,da.AttributeName from sml_application ap,"+
					" sml_dbobject do, sml_dbattribute da where ap.objid=do.dbobject2application and do.objid=da.dbattribute2dbobject " +
					" and ap.appname='"+getAppName()+"' and do.objectname='"+child[i]+"' and da.FieldType='auto' and IsNull='yes'";
			TemplateTable auto=tu.getResultSet(asql);

			if(auto!=null && auto.getRowCount()>0){
				for(int k=0; k<auto.getRowCount(); k++)
					autofield+=(autofield.equals("")?("\n\t\t\t\t String [] autofield"+child[i]+"list={\""):(",\""))+auto.getFieldValue("AttributeName",k).toLowerCase()+"\"";
			}
			if(!autofield.equals("")){
				usql+="\n\t\t\t\t"+autofield+"};"+
				"\n\t\t\t\t usql+=\"\\n\\t\\t\"+tu.copyParent2Child(maindata,\""+child[i]+"\",autofield"+child[i]+"list,\""+childrelation[i]+"\",getParentobjid());";
			
			}
			usql+="\n\t\t\t\t usql+=\"\\n\\t\\t\"+tu.copyParentBQN2Child(this.getObject(),\""+child[i]+"\",\""+childrelation[i]+"\",getParentobjid());";
			
			tmptxt+=usql+"\n\t\t\t\t}";
		}
		tmptxt+="\n\t\t\t }"+
				"\n\t\t\t sql+=\"\\t\\t end;\";"+

			  "\n\t\t\t query.setQuery(sql);"+
			  "\n\t\t\tif(ACONST.GENERATE_LOG)"+
			  "\n\t\t\t logger.info(query.getQuery());"+

			  "\n\t\t\t if (query.getTableResultset().getRowCount()>0){"+

                        "\n\t\t\t\t"+
                        "\n\t\t\t\t usql=(usql.equals(\"\")?\"\":\"\\n\\t begin\"+usql +\"\\n\\t end;\");"+
                        "\n\t\t\t\t if(!usql.equals(\"\"))"+
                        "\n\t\t\t\t\t tu.executeQuery(usql);"+
                        "\n\t\t\t\tif(ACONST.GENERATE_LOG)"+
                        "\n\t\t\t\t\tlogger.info(usql);"+
                        applyafterrule+
                        //"\n\t\t\t\t tu.applyConsoleObject(\""+parent+"\",maindata,manage.getDbType(),manage.getUserName());"+
                        // "\n\t\t\t\t if(manage.getMainstatus()!=null &&!manage.getMainstatus().equals(\"\"))"+
                        //"\n\t\t\t\t\t tu.applyRuleAction(\""+parent+"\",\"0\",\"0\",manage.getMainstatus(),getParentobjid(),manage.getDbType());"+
                        "\n\t\t\t\t return(true);"+
                        "\n\t\t\t}"+
                        "\n\t\t\t return(false);"+
                        "\n\t\t}"+
                        "\n\t}";
		buffer.append(tmptxt);

		return(buffer.toString());
	}



	public String makeOracleStoredProcInsert(String parent,String[] child){

		System.out.println("###### Started Executing makeOracleStoredProcInsert() ...");
		String attrSql=null;
		String relSql=null;
		String sp="CREATE OR REPLACE PROCEDURE Insert_"+parent+"(pnObjid NUMBER) IS \n\n--Constants for status"+
				"\nSTATUS_OPEN\tvarchar2(20):='1';"+
				"\nSTATUS_ACCEPTED\tvarchar2(20):='2';\n\n";
		String opencur="";
		String field="";
		String value="";
		String cursor="";
		String variable="\n--variables\nid\tNUMBER:=0;\n";
		if(child.length>0){
			for(int i=0;i<child.length;i++){
				field="";
				value="";
				cursor="\n\n--Please Modify the cursor as you need\n\nCURSOR m_"+child[i]+"_cur IS Select\n\t\t";
				variable+="i_"+child[i]+"_cur\tm_"+ child[i]+"_cur%rowtype;\n";
				opencur+="\n--opening cursor m_"+child[i]+"_cur\n\tid:=0;\nBegin\n\tOPEN m_"+child[i]+"_cur;"+
						"\n\tLOOP\n\tid := id-1;"+
						"\n\tFETCH m_"+child[i]+"_cur INTO i_"+child[i]+"_cur;"+
						"\n\tEXIT WHEN m_"+child[i]+"_cur%NOTFOUND;"+
						"\n\n--Insert records in "+child[i]+
						"\n\n\tINSERT INTO table_"+child[i]+"(\n\t\tObjid\n\t\t,";
				attrSql="select at.attributename from sml_dbattribute at, sml_dbobject do "+
						" where do.objid=at.dbattribute2dbobject and upper(at.isViewField)='NO' and do.objectname='"+child[i]+"' order by at.attrindex";
				TemplateTable attr=tu.getResultSet(attrSql);
				if(attr.getRowCount()>0){
					for(int c=0;c<attr.getRowCount();c++){
						//get all field list
						field+=attr.getFieldValue("attributename",c)+",\n\t\t";
						value+="i_"+child[i]+"_cur."+attr.getFieldValue("attributename",c)+",\n\t\t";
					}
				}
				sp+=cursor+field+"moduser\n\t\tfrom table_"+child[i]+"\n\t where objid=pnObjid \n\t and not exists (select *from table_"+child[i]+" where "+child[i]+"2"+parent+"=pnObjid);\n\n";
				opencur+=field+"ORIGINID,\n\t\t"+
						"DESTINITIONID,\n\t\t"+
						"GROUPUSER,\n\t\t"+
						"GENUSER,\n\t\t"+
						"GENDATE,\n\t\t"+
						"MODUSER,\n\t\t"+
						"MODDATE\n\t)values(\n\t\tid,\n\t\t"+value+"id,\n\t\t"+
						"pnObjid,\n\t\t"+
						"i_"+child[i]+"_cur.groupuser,\n\t\t"+
						"i_"+child[i]+"_cur.moduser,\n\t\t"+
						"sysdate,\n\t\t"+
						"null,\n\t\t"+
						"null\n\t\t"+
						"\n\t);\n\tEND LOOP;\n\tclose m_"+child[i]+"_cur;\n End;";
			}
			sp+=variable;
			sp+="\nBEGIN"+opencur+"\n\tEXCEPTION\n\t\tWHEN NO_DATA_FOUND THEN\n\t\tnull;\n\nEND;";
			//System.out.print(sp);
		}

		return(sp);
	}
	public String makeOracleStoredProcUpdate(String parent,String[] child){
		System.out.println("###### Started Executing makeOracleStoredProcUpdate() ...");
		String attrSql=null;

		String sp="CREATE OR REPLACE PROCEDURE Update_"+parent+"(pnObjid NUMBER) IS \n\n--Constants for status"+
				"\nSTATUS_OPEN\tvarchar2(20):='1';"+
				"\nSTATUS_ACCEPTED\tvarchar2(20):='2';\n\n";

		String field="";
		String variable="\n--variables\nid\tNUMBER:=0;\n";
		//update any fields for parent
		field="\nBEGIN\n\n\tBegin\n\t\tUpdate Table_"+parent+" set ";

		attrSql="select at.attributename from sml_dbattribute at, sml_dbobject do "+
				" where do.objid=at.dbattribute2dbobject and do.objectname='"+parent+"' order by at.attrindex";
		TemplateTable attr=tu.getResultSet(attrSql);
		if(attr.getRowCount()>0){
			for(int c=0;c<attr.getRowCount();c++){
				//get all field list
				field+=attr.getFieldValue("attributename",c)+"="+attr.getFieldValue("attributename",c)+",\n\t\t";
			}
		}
		field+="moduser=moduser \n\t\twhere objid=pnObjid;"+
				"\n\n\tEXCEPTION\n\t\tWHEN NO_DATA_FOUND THEN\n\t\tnull;\n\n\tEnd;";
		field+="\n\n--Set all relation path for child if needed "+
				"\n--Modify child record here";
		if(child.length>0){
			for(int i=0;i<child.length;i++){
				field+="\n\n--Modifying Child record for "+child[i]+"\n--We ADDED a DUMMY code below, Modify it\n\n\tBegin"+
						"\n\t\tupdate table_"+child[i]+" set "+child[i]+"2"+parent+"=pnObjid where "+child[i]+"2"+parent+"=pnObjid;"+
						"\n\n\tEXCEPTION\n\t\tWHEN NO_DATA_FOUND THEN\n\t\tnull;\n\n\tEnd;";
			}
			sp+=field+"\n\n--Final Exception\n\tEXCEPTION\n\t\tWHEN NO_DATA_FOUND THEN\n\t\tnull;\n\nEND;";

			//System.out.print(sp);
		}

		return(sp);
	}
	public String makeObjectRule(String parent,String[] child){
		System.out.println("###### Started Executing makeObjectRule() ...");
		String attrSql=null;
		int count=2;
		int count2=0;
		String sp="//*****************************************************************//\n"+
				"//Adding Object Rule\n"+
				"//Table Name:"+parent+"\n"+
				"//Attribute Name:"+parent+" Rule\n"+
				"//********************************************************************//\n"+
				"\n\t<ObjectRule>"+
				"\n\t\tName="+parent+" Rule"+
				"\n\t\tTableName="+parent+
				"\n\t\tEffectedTable="+(child.length>0?child[0]:parent)+
				"\n\t\tDescription=Calling Upadte and Insert Rule for object "+parent+
				"\n\t\tReason=1"+
				"\n\t\tActionState=1"+
				"\n\t\tCondition=objid$gt$0"+
				"\n\t\tRuleIndex=1"+
				"\n\t\tStatus=1"+
				"\n\t</ObjectRule>"+
				"\n\n//*****************************************************************//"+
				"\n//Adding Action Query"+
				"\n//Table Name:"+parent+
				"\n//Name:Call Insert_"+parent+" procedure"+
				"\n//Step No:1"+
				"\n//**************************************************************//"+
				"\n\n\t<ActionQuery>"+
				"\n\t\tName=Call Insert_"+parent+" procedure"+
				"\n\t\tTableName="+parent+
				"\n\t\tDescription=Call Insert_"+parent+" procedure"+
				"\n\t\tStepNo=1"+
				"\n\t\tInput=@Objid"+
				"\n\t\tInputDataType=@Integer"+
				"\n\t\tOutput=$activityId"+
				"\n\t\tQueryType=0"+
				"\n\t\tHasRecordSet=0"+
				"\n\t\tOracleQuery=exec Insert_"+parent+"(@objid)"+
				"\n\t\tMssqlQuery=exec Insert_"+parent+" @objid"+
				"\n\t\tStatus=0"+
				"\n\t</ActionQuery>";

		if(child!=null&&child.length>0){
			for(int i=0;i<child.length;i++){
				count2=count+1;
				sp+="\n\n\t<ActionQuery>"+
						"\n\t\tName=Select -ve objid for child "+child[i]+
						"\n\t\tTableName="+child[i]+
						"\n\t\tDescription=Select -ve objid for child "+child[i]+
						"\n\t\tStepNo="+count+
						"\n\t\tInput=@Objid"+
						"\n\t\tInputDataType=@Integer"+
						"\n\t\tOutput=$objid $name $"+child[i]+"id"+
						"\n\t\tQueryType=0"+
						"\n\t\tHasRecordSet=0"+
						"\n\t\tOracleQuery=select objid, name ,objid "+child[i]+"id from table_"+child[i]+
						" where objid like \\'-%\\' and "+child[i]+"2"+parent+"=@objid"+
						"\n\t\tMssqlQuery=select objid, name ,objid "+child[i]+"id from table_"+child[i]+
						" where objid like \\'-%\\' and "+child[i]+"2"+parent+"=@objid"+
						"\n\t\tStatus=0"+
						"\n\t</ActionQuery>"+
						"\n\n\t<ActionQuery>"+
						"\n\t\tName=Update -ve objid for child "+child[i]+
						"\n\t\tTableName="+child[i]+
						"\n\t\tDescription=Update -ve objid for child "+child[i]+
						"\n\t\tStepNo="+count2+
						"\n\t\tInput=@objid @name @"+child[i]+"id"+
						"\n\t\tInputDataType=@Integer @Varchar @Integer"+
						"\n\t\tOutput=$count"+
						"\n\t\tQueryType=0"+
						"\n\t\tHasRecordSet=0"+
						"\n\t\tOracleQuery=update table_"+child[i]+" set objid=@objid where objid=@"+child[i]+"id and name=@name"+
						"\n\t\tMssqlQuery=update table_"+child[i]+" set objid=@objid where objid=@"+child[i]+"id and name=@name"+
						"\n\t\tStatus=0"+
						"\n\t</ActionQuery>\n";
				count=count+2;
			}

			sp+="\n\n\t<ActionQuery>"+
					"\n\t\tName=Call Upadte_"+parent+" procedure"+
					"\n\t\tTableName="+parent+
					"\n\t\tDescription=Call Update_"+parent+" procedure"+
					"\n\t\tStepNo="+count+
					"\n\t\tInput=@Objid"+
					"\n\t\tInputDataType=@Integer"+
					"\n\t\tOutput=$activityId"+
					"\n\t\tQueryType=0"+
					"\n\t\tHasRecordSet=0"+
					"\n\t\tOracleQuery=exec Update_"+parent+"(@objid)"+
					"\n\t\tMssqlQuery=exec Update_"+parent+" @objid"+
					"\n\t\tStatus=0"+
					"\n\t</ActionQuery>";

			//System.out.print(sp);
		}

		return(sp);
	}



	public String makeServiceBean(String parent, String[] child) {
		System.out.println("###### Started Executing makeServiceBean() ...");
		StringBuffer buffer =new StringBuffer();
		String header=""+
				"\n\t\tpackage com."+getAppName()+".service;"+
				"\n\n\t\timport javax.ws.rs.GET;"+
				"\n\t\timport javax.ws.rs.Consumes;"+
				"\n\t\timport javax.ws.rs.FormParam;"+
				"\n\t\timport com.sun.jersey.multipart.FormDataParam;"+
				"\n\t\timport javax.ws.rs.POST;"+
				"\n\t\timport javax.ws.rs.Path;"+
				"\n\t\timport org.json.JSONException;"+
				"\n\t\timport org.json.JSONObject;"+
				"\n\t\timport javax.ws.rs.core.Response;"+
				"\n\t\timport javax.ws.rs.Produces;"+
				"\n\t\timport javax.ws.rs.core.Context;"+
				"\n\t\timport javax.ws.rs.core.HttpHeaders;"+
				"\n\t\timport javax.ws.rs.core.MediaType;"+
				"\n\t\timport javax.ws.rs.core.UriInfo;"+
				"\n\t\timport org.apache.commons.logging.Log;"+
				"\n\t\timport org.apache.commons.logging.LogFactory;"+
				"\n\t\timport cms.service.dhtmlx.Rows;"+
				"\n\t\timport cms.service.template.TemplateUtility;"+
				"\n\t\timport cms.service.exceptions.DaoException;"+
				"\n\t\timport cms.service.exceptions.AuthenticationException;"+
				"\n\t\timport com."+getAppName()+".dao."+parent+"Dao;"+
				"\n\t\t/*"+
				"\n\t\t*  URL Parameters:"+
				"\n\t\t*  "+
				"\n\t\t*  Mandatory : loginname, groupuser, token i.e  {Base URL}/project/{id}/estimation?loginname=abc@example.com&groupuser=cdf@eaxmple.com&token=2343434334444"+
				"\n\t\t*  "+
				"\n\t\t*  Optional : id= parent objid for any child url i.e {Base URL}/project/{id}/estimation?loginname=abc@example.com&groupuser=cdf@eaxmple.com&token=2343434334444"+
				"\n\t\t*  "+
				"\n\t\t*  Optional: page, pagesize for search i.e {Base URL}/project/{id}/estimation?loginname=abc@example.com&groupuser=cdf@eaxmple.com&token=2343434334444&page=1&pagesize=50"+
				"\n\t\t*  "+
				"\n\t\t*  Optional: name for filter i.e {Base URL}/project/{id}/estimation?loginname=abc@example.com&groupuser=cdf@eaxmple.com&token=2343434334444&page=1&pagesize=50&name=Alex"+
				"\n\t\t*  "+
				"\n\t\t*  Optional: fields=column1,column2,...  i.e {Base URL}/project/{id}/estimation?loginname=abc@example.com&groupuser=cdf@eaxmple.com&"+
				"\n\t\t*  				token=2343434334444&page=1&pagesize=50&name=Alex&fields=name,title,projectcode..."+
				"\n\t\t*  "+
				"\n\t\t*/"+
				"\n\n\t\t//Use this URI resource with Base URL to access "+parent+
				"\n\t\t@Path(\"/"+parent.toLowerCase()+"\")"+
				"\n\t\tpublic class "+parent+"Service {"+
				"\n\t\t\tstatic Log logger = LogFactory.getLog("+parent+"Service.class);"+
				"\n\n\t\t\t// Get all contextual objects for this class"+
				"\n\t\t\t@Context UriInfo uriInfo;"+
				"\n\t\t\t@Context  HttpHeaders header;"+
				"\n\t\t\t "+
				"\n\t\t\t// Get all rows for "+parent+
				"\n\t\t\t@GET"+
				"\n\t\t\t@Path(\"/rows\")"+
				"\n\t\t\t@Produces({\"application/xml\"})"+
				"\n\t\t\tpublic Rows get"+parent+"Rows() {"+
				"\n\t\t\t\tRows rows = null;"+
				"\n\t\t\t\ttry {"+
				"\n\t\t\t\t\trows=new "+parent+"Dao(uriInfo,header).get"+parent+"Rows();"+
				"\n\t\t\t\t} catch (AuthenticationException e) {"+
				"\n\t\t\t\t\t rows=new TemplateUtility().getFailedMessage(e.getMessage());"+
				"\n\t\t\t\t\t e.printStackTrace();"+
				"\n\t\t\t\t} catch (Exception ex) {"+
				"\n\t\t\t\t\t logger.info( \"Error calling get"+parent+"Rows()\"+ ex.getMessage());"+
				"\n\t\t\t\t\t ex.printStackTrace();"+
				"\n\t\t\t\t}"+
				"\n\t\t\t\treturn rows;"+
				"\n\t\t\t}"+
				"\n\t\t\t "+
				"\n\t\t\t// Get "+parent+" record by id"+
				"\n\t\t\t@GET"+
				"\n\t\t\t@Path(\"/{id}/record\")"+
				"\n\t\t\t@Produces({\"application/xml\"})"+
				"\n\t\t\tpublic Rows get"+parent+"Record() {"+
				"\n\t\t\t\tRows rows = null;"+
				"\n\t\t\t\ttry {"+
				"\n\t\t\t\t\trows=new "+parent+"Dao(uriInfo,header).get"+parent+"Rows();"+
				"\n\t\t\t\t} catch (AuthenticationException e) {"+
				"\n\t\t\t\t\t rows=new TemplateUtility().getFailedMessage(e.getMessage());"+
				"\n\t\t\t\t\t e.printStackTrace();"+
				"\n\t\t\t\t} catch (Exception ex) {"+
				"\n\t\t\t\t\t logger.info( \"Error calling get"+parent+"Record()\"+ ex.getMessage());"+
				"\n\t\t\t\t\t ex.printStackTrace();"+
				"\n\t\t\t\t}"+
				"\n\t\t\t\treturn rows;"+
				"\n\t\t\t}"+
				"\n\t\t\t "+
				
				"\n\t\t\t// Get all rows with filter for "+parent+
				"\n\t\t\t@GET"+
				"\n\t\t\t@Path(\"/filter\")"+
				"\n\t\t\t@Produces({\"application/xml\"})"+
				"\n\t\t\tpublic Rows get"+parent+"RowsByFilter() {"+
				"\n\t\t\t\tRows rows = null;"+
				"\n\t\t\t\ttry {"+
				"\n\t\t\t\t\trows=new "+parent+"Dao(uriInfo,header).get"+parent+"ByFilter();"+
				"\n\t\t\t\t} catch (AuthenticationException e) {"+
				"\n\t\t\t\t\t rows=new TemplateUtility().getFailedMessage(e.getMessage());"+
				"\n\t\t\t\t\t e.printStackTrace();"+
				"\n\t\t\t\t} catch (Exception ex) {"+
				"\n\t\t\t\t\t logger.info( \"Error calling get"+parent+"RowsByFilter()\"+ ex.getMessage());"+
				"\n\t\t\t\t\t ex.printStackTrace();"+
				"\n\t\t\t\t}"+
				"\n\t\t\t\treturn rows;"+
				"\n\t\t\t}"+
				"\n\t\t\t "+
				"\n\t\t\t// Get record count with filter for "+parent+
				"\n\t\t\t@GET"+
				"\n\t\t\t@Path(\"/recordcount\")"+
				"\n\t\t\t@Produces({\"application/json\"})"+
				"\n\t\t\tpublic Response get"+parent+"RecordCount() throws JSONException {"+
				"\n\t\t\t\tJSONObject rows = new JSONObject();"+
				"\n\t\t\t\ttry {"+
				"\n\t\t\t\t\trows=new "+parent+"Dao(uriInfo,header).get"+parent+"RecordCount();"+
				"\n\t\t\t\t} catch (AuthenticationException e) {"+
				"\n\t\t\t\t\t rows.put(\"error\",new TemplateUtility().getFailedMessage(e.getMessage()));"+
				"\n\t\t\t\t\t e.printStackTrace();"+
				"\n\t\t\t\t} catch (Exception ex) {"+
				"\n\t\t\t\t\tlogger.info( \"Error calling get"+parent+"Rows()\"+ ex.getMessage());"+
				"\n\t\t\t\t\t ex.printStackTrace();"+
				"\n\t\t\t\t}"+
				"\n\t\t\t\treturn Response.status(200).entity(rows.toString()).build();"+
				"\n\t\t\t}"+
				"\n\t\t\t "+
				"\n\t\t\t// Get summary row against object ID for "+parent+
				"\n\t\t\t@GET"+
				"\n\t\t\t@Path(\"/{id}/summary\")"+
				"\n\t\t\t@Produces({\"application/xml\"})"+
				"\n\t\t\tpublic Rows get"+parent+"SummaryRows() {"+
				"\n\t\t\t\tRows rows = null;"+
				"\n\t\t\t\ttry {"+
				"\n\t\t\t\t\trows=new "+parent+"Dao(uriInfo,header).get"+parent+"SummaryRows();"+
				"\n\t\t\t\t} catch (AuthenticationException e) {"+
				"\n\t\t\t\t\t rows=new TemplateUtility().getFailedMessage(e.getMessage());"+
				"\n\t\t\t\t\t e.printStackTrace();"+
				"\n\t\t\t\t} catch (Exception ex) {"+
				"\n\t\t\t\t\tlogger.info( \"Error calling get"+parent+"Rows()\"+ ex.getMessage());"+
				"\n\t\t\t\t\t ex.printStackTrace();"+
				"\n\t\t\t\t}"+
				"\n\t\t\t\treturn rows;"+
				"\n\t\t\t}";

        String deletemethod="";
        
        //if(parentproperty.getRowCount()>0 && parentproperty.getFieldValue("allowdelete", count-1).equalsIgnoreCase("yes")){
        if(this.getParentProperty(parent)!=null &&this.getParentProperty(parent).getRowCount()>0){
        	deletemethod="\n\t\t\t// Get "+parent+" record deleted using id"+
    				"\n\t\t\t@GET"+
    				"\n\t\t\t@Path(\"/{id}/delete\")"+
    				"\n\t\t\t@Produces({\"application/xml\"})"+
    				"\n\t\t\tpublic Rows get"+parent+"RowDeleted() {"+
    				"\n\t\t\t\tRows rows = null;"+
    				"\n\t\t\t\ttry {"+
    				"\n\t\t\t\t\trows=new "+parent+"Dao(uriInfo,header).get"+parent+"RowDeleted();"+
    				"\n\t\t\t\t} catch (AuthenticationException e) {"+
    				"\n\t\t\t\t\t rows=new TemplateUtility().getFailedMessage(e.getMessage());"+
    				"\n\t\t\t\t\t e.printStackTrace();"+
    				"\n\t\t\t\t} catch (Exception ex) {"+
    				"\n\t\t\t\t\tlogger.info( \"Error calling get"+parent+"RowDeleted()\"+ ex.getMessage());"+
    				"\n\t\t\t\t\t ex.printStackTrace();"+
    				"\n\t\t\t\t}"+
    				"\n\t\t\t\treturn rows;"+
    				"\n\t\t\t}"+
    				"\n\t\t\t ";
        	
        }
		String childmethod="";

		for(String table:child){
			String childname=table.substring(0, 1).toUpperCase()+table.substring(1,table.length()).toLowerCase();
			childmethod+=
					"\n\t\t\t "+
							"\n\t\t\t// Get all "+childname+" rows against object ID for "+parent+
							"\n\t\t\t@GET"+
							"\n\t\t\t@Path(\"/{id}/"+ table.toLowerCase()+"\")"+
							"\n\t\t\t@Produces({\"application/xml\"})"+
							"\n\t\t\tpublic Rows get"+childname+"Rows(@Context UriInfo uriInfo,@Context  HttpHeaders header) {"+
							"\n\t\t\t\tRows rows = null;"+
							"\n\t\t\t\ttry {"+
							"\n\t\t\t\t\trows=new "+parent+"Dao(uriInfo,header).get"+childname+"Rows();"+
							"\n\t\t\t\t} catch (AuthenticationException e) {"+
							"\n\t\t\t\t\t rows=new TemplateUtility().getFailedMessage(e.getMessage());"+
							"\n\t\t\t\t\t e.printStackTrace();"+
							"\n\t\t\t\t} catch (Exception ex) {"+
							"\n\t\t\t\t\tlogger.info( \"Error calling get"+childname+"Rows()\"+ ex.getMessage());"+
							"\n\t\t\t\t}"+
							"\n\t\t\t\treturn rows;"+
							"\n\t\t\t}";
		}

		String post=""+
				"\n\t\t\t "+
				"\n\t\t\t// Post all data changes in your grid for parent and child together"+
				"\n\t\t\t@POST"+
				"\n\t\t\t@Path(\"/post\")"+
				"\n\t\t\t@Consumes(MediaType.APPLICATION_FORM_URLENCODED)"+
				"\n\t\t\t@Produces({MediaType.APPLICATION_XML})"+
				"\n\t\t\tpublic Rows post"+parent+"(@Context UriInfo uriInfo,@Context  HttpHeaders header,@FormParam(\"body\") String xml) {"+
				"\n\t\t\t\tRows rows = null;"+
				"\n\t\t\t\t"+parent+"Dao post;"+
				"\n\t\t\t\ttry {"+
				"\n\t\t\t\t\tpost=new "+parent+"Dao(uriInfo,header);"+
				"\n\t\t\t\t\tpost.setPostXml(xml.trim());"+
				"\n\t\t\t\t\tpost.post"+parent+"Container();"+
				"\n\t\t\t\t\trows=post.get"+parent+"RowModified();"+
				"\n\t\t\t\t} catch (AuthenticationException e) {"+
				"\n\t\t\t\t\t rows=new TemplateUtility().getFailedMessage(e.getMessage());"+
				"\n\t\t\t\t\t e.printStackTrace();"+
				"\n\t\t\t\t} catch (DaoException d) {"+
				"\n\t\t\t\t\t d.printStackTrace();"+
				"\n\t\t\t\t}"+
				"\n\t\t\t\treturn rows;"+
				"\n\t\t\t}"+
				"\n"+
				"\n\t\t\t// Post all data changes in using form"+
				"\n\t\t\t@POST"+
				"\n\t\t\t@Path(\"/formdata\")"+
				"\n\t\t\t@Consumes(MediaType.MULTIPART_FORM_DATA)"+
				"\n\t\t\t@Produces({MediaType.APPLICATION_XML})"+
				"\n\t\t\tpublic Rows postFormData"+parent+"(@Context UriInfo uriInfo,@Context  HttpHeaders header,@FormDataParam(\"body\") String xml) {"+
				"\n\t\t\t\tRows rows = null;"+
				"\n\t\t\t\t"+parent+"Dao post;"+
				"\n\t\t\t\ttry {"+
				"\n\t\t\t\t\tpost=new "+parent+"Dao(uriInfo,header);"+
				"\n\t\t\t\t\tpost.setPostXml(xml.trim());"+
				"\n\t\t\t\t\tpost.post"+parent+"Container();"+
				"\n\t\t\t\t\trows=post.get"+parent+"RowModified();"+
				"\n\t\t\t\t} catch (AuthenticationException e) {"+
				"\n\t\t\t\t\t rows=new TemplateUtility().getFailedMessage(e.getMessage());"+
				"\n\t\t\t\t\t e.printStackTrace();"+
				"\n\t\t\t\t} catch (DaoException d) {"+
				"\n\t\t\t\t\t d.printStackTrace();"+
				"\n\t\t\t\t}"+
				"\n\t\t\t\treturn rows;"+
				"\n\t\t\t}"+
				"\n\t\t}";


		buffer.append(header+deletemethod+childmethod+post);
		return (buffer.toString());
	}

}