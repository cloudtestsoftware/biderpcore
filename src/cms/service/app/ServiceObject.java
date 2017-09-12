package cms.service.app;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cms.service.dhtmlx.Option;
import cms.service.template.TemplateTable;
import cms.service.template.TemplateUtility;


/**
 * Title:        Semantic Application
 * Description:  Semantic Main Infrastructure Project
 * Copyright:    Copyright (c) 2001
 * Company:      SemanticJava Soft
 * @author
 * @version 1.0
 */

public class ServiceObject {
	static Log logger = LogFactory.getLog(ServiceObject.class);

	static TemplateUtility tu = new TemplateUtility();

	private static HashMap<String ,TemplateTable> property= new HashMap<String ,TemplateTable>();
	private static HashMap<String ,TemplateTable> chartmap= new HashMap<String ,TemplateTable>();
	private static HashMap<String ,TemplateTable> privilege=new HashMap<String ,TemplateTable>();
	private static TemplateTable[][] objectrule= new TemplateTable[3][3];
	private static TemplateTable mtmrelation=new TemplateTable();
	private static TemplateTable userprivobject=new TemplateTable();

	private String privgroup="";
	private String groupid="";


	public ServiceObject() {
	}

	public void setPrivilegeGroup(String privgroup){
		this.privgroup=privgroup;
	}
	public String getPrivilegeGroup(){
		return(privgroup);
	}
	public void setPrivilegeGroupId(String groupid){
		this.groupid=groupid;
	}
	public String getPrivilegeGroupId(){
		return(groupid);
	}



	public TemplateTable getTableProperty(String table){

		TemplateTable prop=property.get(table);
		if(prop!=null &&prop.getRowCount()>0)
			return prop;
		String object="'"+table.toUpperCase()+"'";

		String sql="select da.TableName,da.Name,sp.PropertyString,sp.PropertyValue,sp.Scope " +
				",sp.PropIndex,sp.GenUser from table_Attribute da,table_ListProperty sp "+
				" where upper(da.TableName) in ("+object+") and sp.ListProperty2Attribute = da.objid "+
				" order by da.TableName, da.name,sp.propindex";
		TemplateTable result=tu.getResultSet(sql);
		if(result!=null){
			property.put(table, result);
			return(result);
		}

		return(null);
	}
	
	public String getChildButtonAction(String table){
		
		String action="";
		TemplateTable props= getTableProperty(table);
		if(props!=null &&props.getRowCount()>0){
			for(int row=0;row<props.getRowCount();row++){
				String[] button=props.getFieldValue("scope", row).split(":");
				
				if(button.length>1 &&button[0].toLowerCase().contains("button")){
					String button_id=table.toLowerCase()+":"+props.getFieldValue("Name", row)+":"+props.getFieldValue("PropertyValue", row);
					
					action+="<item type=\"button\" id=\""+button_id.toLowerCase()+"\" text=\""+button[1]+"\"  img=\"red.gif\"/><item type=\"separator\" id=\"button_separator_"+row+"\" />";
						
				}
					
			}
			
		}
		
		return action;
	}
	public String getButtonAction(String table){
		//add list of tables which can support copy action
		String tables="quote ";
		String copybutton="";
		if(tables.toLowerCase().contains(table.toLowerCase())){
			copybutton=",{ type:\"button\" , name:\"grid_action_duplicate\", value:\"Duplicate\", width:\"100\"  },";
		}
		String commonbuttons="["+
		"{ type:\"fieldset\" , name:\"form_fieldset_commonbtn\", label:\"Grid Action\", list:["+
		"{ type:\"button\" , name:\"grid_action_add\", value:\"Add\", width:\"100\"  },"+
		"{ type:\"button\" , name:\"grid_action_remove\", value:\"Remove\", width:\"100\"  },"+
		"{ type:\"button\" , name:\"grid_action_save\", value:\"Save\", width:\"100\", command:\"save\"  }"+
		copybutton+
		"]  }";
		String action="";
		TemplateTable props= getTableProperty(table);
		if(props!=null &&props.getRowCount()>0){
			for(int row=0;row<props.getRowCount();row++){
				String[] button=props.getFieldValue("scope", row).split(":");
				
				if(button.length>1 &&button[0].toLowerCase().contains("button")){
					String button_id=table.toLowerCase()+":"+props.getFieldValue("Name", row)+":"+props.getFieldValue("PropertyValue", row);
					if(tu.isEmptyValue(action)){
						action+="{ type:\"button\" , name:\""+button_id.toLowerCase()+"\", value:\""+button[1]+"\", command:\""+button_id+"\"  }";
					}else{
						action+=",\n{ type:\"button\" , name:\""+button_id.toLowerCase()+"\", value:\""+button[1]+"\", command:\""+button_id+"\"  }";
					}
				}
					
			}
			if(!tu.isEmptyValue(action)){
				
				action="{ type:\"fieldset\" , name:\"form_fieldset_"+table.toLowerCase()+"\", label:\""+table+" Action\", list:["+action+"\n]  }";
			}
		}
		
		if(!tu.isEmptyValue(action)){
			action=commonbuttons+",\n"+action+"]";
		}else{
			action=commonbuttons+"]";
		}
		return action;
	}
	public HashMap<String,String> getChartProperty(String table, String column){
		TemplateTable prop=getChartMap(table);
		HashMap<String, String> chart=new HashMap<String, String>();
		for(int i=0;i<prop.getRowCount();i++){
			String col=prop.getFieldValue("selectcolumn", i);
			if(!tu.isEmptyValue(col) &&col.equalsIgnoreCase(column)){
				chart.put("chartname", prop.getFieldValue("chartname", i));
				chart.put("charttype", prop.getFieldValue("charttype", i));
				chart.put("fieldnames", prop.getFieldValue("fieldnames", i));
				chart.put("chartdatatype", prop.getFieldValue("chartdatatype", i));
				chart.put("chartalias", prop.getFieldValue("chartalias", i));
				chart.put("x_axis", prop.getFieldValue("x_axis", i));
				chart.put("y_axis", prop.getFieldValue("y_axis", i));
				chart.put("selectcolumn", prop.getFieldValue("selectcolumn", i));
				chart.put("captions", prop.getFieldValue("captions", i));
				chart.put("isdefault", prop.getFieldValue("isdefault", i));
			}
		}
		
		return chart;
	}
	
	public ArrayList<String> getChartSelectColumns(String table){
		TemplateTable prop=getChartMap(table);
		ArrayList<String> cols=new ArrayList<String>();
		for(int i=0;i<prop.getRowCount();i++){
				cols.add( prop.getFieldValue("selectcolumn", i).toLowerCase());
		}
		
		return cols;
	}
	private TemplateTable getChartMap(String table){

		TemplateTable chart=chartmap.get(table);
		if(chart!=null &&chart.getRowCount()>0)
			return chart;
		String object="'"+table.toUpperCase()+"'";

		String sql="select ca.* from sml_chartmap ca"+
				" where upper(ca.TableName) ="+object;
		TemplateTable result=tu.getResultSet(sql);
		if(result!=null){
			chartmap.put(table, result);
			return(result);
		}

		return(null);
	}
	

	// This method initialize all privilege group objects
	public TemplateTable getUserPrivilegeObject(){
		String sql="select distinct op.name TableName ,op.Value,op.Type,op.IsRecursive,pg.name groupname,pg.objid groupid"+
				" from  table_privilegegroup pg,table_objectprivilege op "+
				" where  pg.objid=op.Objectprivilege2privilegegroup order by groupid ";

		if(userprivobject==null ||userprivobject.getRowCount()<=0){
			userprivobject=tu.getResultSet(sql);
		}
		return(userprivobject);
	}
	/**
	 * This method returns all the attribute and their privilege based on the user's privilege group
	 * it query the table_Objectprivilege,Table_Object,Table_attribute,Table_attributeprivildge metadata tables
	 */
	public TemplateTable getObjectPrivilege(String table){
		TemplateTable priv=privilege.get(table);
		if(priv!=null &&priv.getRowCount()>=0){
			return priv;
		}
		String object="'"+table+"'";

		String sql="select op.ObjId,ap.Value,op.Name,ta.Name \"AttributeName\" "+
				" from table_PrivilegeGroup pg,table_AttrPrivilege ap,"+
				" table_objectprivilege op ,table_attribute ta "+
				" where op.Name = "+object+" and pg.objid="+getPrivilegeGroupId()+
				" and pg.objid=op.ObjectPrivilege2PrivilegeGroup "+
				" and op.objid = ap.AttrPrivilege2ObjectPrivilege"+
				" and ta.objid = ap.AttrPrivilege2Attribute " +
				" order by op.Name ";

		TemplateTable result=tu.getResultSet(sql);
		if(result!=null){
			privilege.put(table,result);
			return(result);
		}
		return(null);
	}




	/***
	 * This method will verify whether there is a rule object associated with
	 * a current object name which needs to be verified
	 * Parameter:
	 * tablename= name of the table for which this verification is applicable
	 * return=true or false
	 */
	public boolean verifyObjectRule(String tablename,int reason, int state){
		String sql="select distinct tablename from table_objectrule where status='1' and reason='"+reason+"' and actionstate='"+state+"'";
		if(objectrule[reason][state]==null ||objectrule[reason][state].getRowCount()<1)
			objectrule[reason][state]=tu.getResultSet(sql);
		if(objectrule[reason][state]!=null &&objectrule[reason][state].getRowCount()>0)
			for(int i=0;i<objectrule[reason][state].getRowCount();i++)
				if(objectrule[reason][state].getFieldValue("tablename",i).equalsIgnoreCase(tablename))
					return(true);
		return(false);
	}
	/**
	 * This method will hold all MTM relation list from the metadata
	 */
	public TemplateTable getMTMRelation(){
		String sql="select *from sml_attributerelation where relationtype like 'MTM%'";
		if(mtmrelation!=null&&mtmrelation.getRowCount()>0)
			return(mtmrelation);
		else{
			mtmrelation=tu.getResultSet(sql);
			return(mtmrelation);
		}
	}

	public HashMap<String,ArrayList<Option>> getProperty(String table, String fieldlist[],String groupuser)
	{
		HashMap<String,ArrayList<Option>> props= new HashMap<String,ArrayList<Option>>();

		TemplateTable prop=this.getTableProperty( table);
		
		HashMap<String,ArrayList<Option>> cprops=ServiceManager.getCustomProperty(table,groupuser);
		for(int m = 0; m < fieldlist.length; m++)
		{

			ArrayList<Option> options= new ArrayList<Option>();
			
			//fill up custom options first
			ArrayList<Option> custoptions=cprops.get(fieldlist[m]);
			
			if(custoptions!=null){
				for(Option coption:custoptions){
					options.add( coption);
				}
			}
			for(int n = 0; n < prop.getRowCount(); n++){
				if(fieldlist[m].equalsIgnoreCase(prop.getFieldValue("name", n))){
					ArrayList<String> optionval= new ArrayList<String>();
					optionval.add(prop.getFieldValue("PropertyString", n));
					options.add(new Option(prop.getFieldValue("PropertyValue", n),optionval));
				}

				props.put(fieldlist[m].toLowerCase(), options);
			}
			
			

		}
		return props;
	}


}
