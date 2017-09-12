package cms.service.template;


import java.io.*;
import java.util.*;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cms.service.app.ApplicationConstants;
import cms.service.app.CodeManager;
import cms.service.app.ServiceObject;
import cms.service.dhtmlx.Column;
import cms.service.dhtmlx.Head;
import cms.service.dhtmlx.Option;
import cms.service.dhtmlx.Row;
import cms.service.dhtmlx.Rows;
import cms.service.dhtmlx.Userdata;
import cms.service.jdbc.DataType;
import cms.service.jdbc.DatabaseTransaction;



/**
 * Title:        Semantic Application
 * Description:  Semantic Main Infrastructure Project
 * Copyright:    Copyright (c) 2001
 * Company:      SemanticJava Soft
 * @author
 * @version 1.0
 */

public class TemplateUtility {
	static Log logger = LogFactory.getLog(TemplateUtility.class);
	private static ApplicationConstants ACONST = new ApplicationConstants();
	private Object ruleaction=new Object();
	private Object deleteaction=new Object();
	private Object applyobjectrule=new Object();
	public static String exceptiontables="customlist, customcode";//  default tables visible to everybody to add custom list property

	public TemplateUtility() {
	}

	
	/*
	 * @tab  : Query result in template table
	 * @table : actual table name 
	 */
	public Rows getXMLRows(TemplateTable tab, String table,String codefieldlist[],String propfieldlist[],String relationlist[],
			String captions[],String[] datadomain,String groupuser,String bqn){

		Rows rows=null;
		Head head=null;
		HashMap<String,ArrayList<Option>> codes=null;
		HashMap<String,ArrayList<Option>> props=this.getProperty(table, propfieldlist,groupuser);
		HashMap<String,ArrayList<Option>> relationprops=this.getRelationOptions(relationlist, groupuser, bqn);
		
		
		//logger.info("props="+props.values());
        String exceptiontables="project projectcode quote bidrequest";
        String exceptioncols="mainjobcode subjobcode";
        String codetables="projectcode maincode subcode";
        if(codetables.indexOf(table.toLowerCase())>=0){
        	exceptioncols="projectcode mainjobcode subjobcode";
        }
       
        if(exceptiontables.contains(table.toLowerCase())){
        	codes=this.getCodeObject(table, codefieldlist, null, groupuser);
        }else if(tab.getRowCount()>0){
        	String pcode=tab.getFieldValue("projectcode", tab.getRowCount()-1);
        	
        	if(this.isEmptyValue(pcode) &&!codetables.contains(table)){
        		
        		if(!this.isEmptyValue(tab.getFieldValue("maincode", tab.getRowCount()-1)) &&
        			!this.isEmptyValue(tab.getFieldValue("objid", tab.getRowCount()-1))	){
        			
        		  String sql="select projectcode from table_"+table +" where objid='"+tab.getFieldValue("objid", tab.getRowCount()-1)+"'";
        		  TemplateTable result=this.getResultSet(sql);
        		  if(result!=null &&result.getRowCount()>0){
        			  pcode=result.getFieldValue("projectcode", result.getRowCount()-1);
        		  }
        		}
        	}
        
        	codes=this.getCodeObject(table, codefieldlist, pcode, groupuser);
        		
        	
        }else{
        	codes=this.getCodeObject(table, codefieldlist, null, groupuser);
        }
        //logger.info("codes="+codes.values());
        
		ArrayList<Row> rowlist= new ArrayList<Row>();

		TemplateTable meta=getTableMetaData(table);
		ArrayList<String> moneycols= new ArrayList<String>();
	

		//if(tab.getRowCount()>0){

			List<Column> colls =new ArrayList<Column>();
			boolean isDate=false;
			int idx=0;
			for (String colname:tab.getColumnNames()){
				
				List<String> columnname=new ArrayList<String>();
				List<Option> options= new ArrayList<Option>();
				String caption=captions[idx];
				String domain=datadomain[idx];
				idx++;
				Column col= new Column();
				//col.setOption(options);
				
				col.setSort("str");
				col.setAlign("left");
				int colindex=meta.getColumnIndex(colname);

				if(colindex>=0){
					//System.out.println(colname+"="+meta.getColumnNullables()[colindex]);
					int colsize=Integer.parseInt(meta.getColumnSizes()[colindex]);
					String datatype=meta.getColumnDataTypes()[colindex];
					String nullable=meta.getColumnNullables()[colindex].equals("0")?"false":"true";
					if(nullable.equalsIgnoreCase("true") &&datatype.toLowerCase().contains("char")){
						int index=tab.getColumnIndex(colname);
						if(index>0){
							datadomain[index]="null";
						}
					}
					col.setId(colname.toLowerCase()+":"+nullable+":"+String.valueOf(meta.getColumnDataTypes()[colindex])+":"+colsize);
					col.setWidth("100");
					if (colsize>100){
						col.setType("txt");
						col.setWidth("200");
					}else if(datatype.contains("DATE")){
						col.setType("dhxCalendar");
						
						col.setFormat("%m/%d/%Y");
						col.setSort("date");
						col.setAlign("center");
					
						
					}else if(datatype.contains("NUMBER") ){	 
						col.setAlign("center");
						col.setType("ed");
					}else if(domain.toLowerCase().contains("boolean")){	 
						col.setAlign("center");
						col.setType("ch");
						col.setWidth("100");
					}else{
						col.setType("ed");
					}
				
					
				}else{
					col.setId(colname.toLowerCase());
					col.setType("ro");
					col.setWidth("100");
				}
				
				if(props.get(colname.toLowerCase())!=null){
						columnname.add(caption);
						options=props.get(colname.toLowerCase());
						col.setType("corotxt");
						col.setWidth("200");
				}else if(codes.get(colname.toLowerCase())!=null){
					  columnname.add(caption);
					  options=codes.get(colname.toLowerCase());
					  col.setType("corotxt");
					  col.setWidth("200");
				}else if(relationprops.get(colname.toLowerCase())!=null){
					  columnname.add(caption);
					  options=relationprops.get(colname.toLowerCase());
					  //col.setType("corotxt");
					  col.setType("co");
					  col.setWidth("200");
                }else{
                	columnname.add(caption);
                }
				if(colname.equalsIgnoreCase("projectcode")&&table.equalsIgnoreCase("projectcode")||
						colname.equalsIgnoreCase("mainjobcode")&&table.equalsIgnoreCase("maincode")||
						colname.equalsIgnoreCase("subjobcode")&&table.equalsIgnoreCase("subcode")||
						colname.equalsIgnoreCase("maincode") ||colname.equalsIgnoreCase("subcode")){
					col.setType("ro");
					col.setWidth("100");
					col.setAlign("center");
				}
				if(colname.equalsIgnoreCase("maincode")&&table.equalsIgnoreCase("bidrequest")){
					 col.setType("ed");
					 col.setWidth("100");
					 options=null;
				}
				col.setValue(columnname);
				col.setOption(options);
				colls.add(col);
				
				if(domain.toLowerCase().contains("money")){
					if(moneycols.isEmpty()){
						moneycols.add(0, colname.toLowerCase());
					}else{
						String val=moneycols.get(0)+","+colname;
						moneycols.remove(0);
						moneycols.add(0,val.toLowerCase());
					}
				}
				
				//System.out.println("Col="+colname);
			}
			head= new Head(colls);
			int i=0;
			boolean hasdata=false;
			//if column name and 1st row 1st column is same
			
			if(tab.getRowCount()>0 &&tab.getColumnNames()[0].equalsIgnoreCase(tab.getFieldValue(tab.getColumnNames()[0], 0))){
				i=1;
			}
			for( ;i<tab.getRowCount();i++){
				hasdata=true;
				List<String> cells =new ArrayList<String>();
				for (String coll:tab.getColumnNames()){
					cells.add(tab.getFieldValue(coll, i));
					//System.out.println("Cell="+tab.getFieldValue(coll, i));
				}

				rowlist.add(new Row(String.valueOf(i),cells));

			}
			
			//if has no data fill up dummy row
			if(!hasdata){
				List<String> cells =new ArrayList<String>();
				int colidx=0;
				for (String coll:tab.getColumnNames()){
					if(exceptioncols.contains(coll.toLowerCase())){
						cells.add("0");
					}else{
						cells.add(colidx==0?"newid":"new");
					}
					colidx++;
				}

				rowlist.add(new Row(String.valueOf(i),cells));
			}

			rows= new Rows(rowlist,head);
		//}
		String buttonaction=this.getButtonAction(table);
		if(!this.isEmptyValue(buttonaction)){
			ArrayList<String> data= new ArrayList<String>();
			data.add(buttonaction);
			Userdata button= new Userdata("button.action",data);
			rows.getUserdata().add(button);
		}
		String childbuttonaction=this.getChildButtonAction(table);
		if(!this.isEmptyValue(childbuttonaction)){
			ArrayList<String> data= new ArrayList<String>();
			data.add(childbuttonaction);
			Userdata button= new Userdata("child.action",data);
			rows.getUserdata().add(button);
		}
		if(datadomain.length>0){
			Userdata validator= new Userdata("grid.validator",getValidators(datadomain));
			rows.getUserdata().add(validator);
		}
		if(!moneycols.isEmpty()){
			Userdata moneydata= new Userdata("grid.moneycols",moneycols);
			rows.getUserdata().add(moneydata);
		}
		return rows;
	}
	/*
	 * @tab  : Query result in template table
	 * @table : actual table name 
	 */
	public Rows getXMLFilterRows(TemplateTable tab, String table,String codefieldlist[],String propfieldlist[],
			String relationlist[],String columns[], String captions[],String[] datadomain, String groupuser, String bqn){

		Rows rows=null;
		Head head=null;
		HashMap<String,ArrayList<Option>> codes=null;
		HashMap<String,ArrayList<Option>> props=this.getProperty(table, propfieldlist,groupuser);
		HashMap<String,ArrayList<Option>> relationprops=this.getRelationOptions(relationlist, groupuser,bqn);
		
		//logger.info("key="+props.keySet().toArray());
		//logger.info("props="+props.values());
        String exceptiontables="project projectcode quote bidrequest";
        String exceptioncols="mainjobcode subjobcode";
        String codetables="projectcode maincode subcode";
        if(codetables.indexOf(table.toLowerCase())>=0){
        	exceptioncols="projectcode mainjobcode subjobcode";
        }
        if(exceptiontables.contains(table.toLowerCase())){
        	codes=this.getCodeObject(table, codefieldlist, null, groupuser);
        }else if(tab.getRowCount()>0){
        	String pcode=tab.getFieldValue("projectcode", tab.getRowCount()-1);
        	if(this.isEmptyValue(pcode) &&!codetables.contains(table)){
        		
        		if(!this.isEmptyValue(tab.getFieldValue("maincode", tab.getRowCount()-1)) &&
        			!this.isEmptyValue(tab.getFieldValue("objid", tab.getRowCount()-1))	){
        			
        		  String sql="select projectcode from table_"+table +" where objid='"+tab.getFieldValue("objid", tab.getRowCount()-1)+"'";
        		  TemplateTable result=this.getResultSet(sql);
        		  if(result!=null &&result.getRowCount()>0){
        			  pcode=result.getFieldValue("projectcode", result.getRowCount()-1);
        		  }
        		}
        	}
        	codes=this.getCodeObject(table, codefieldlist, pcode, groupuser);
        	
        }else{
        	codes=this.getCodeObject(table, codefieldlist, null, groupuser);
        }
        //logger.info("codes="+codes.values());
        
		ArrayList<Row> rowlist= new ArrayList<Row>();

		TemplateTable meta=getTableMetaData(table);
		ArrayList<String> moneycols= new ArrayList<String>();

		//if(tab.getRowCount()>0){

			List<Column> colls =new ArrayList<Column>();
			boolean isDate=false;
			int idx=0;
			for (String colname:columns){
				List<String> columnname=new ArrayList<String>();
				List<Option> options= new ArrayList<Option>();
				String caption=captions[idx];
				String domain=datadomain[idx];
				idx++;
				Column col= new Column();
				//col.setOption(options);
				
				col.setSort("str");
				col.setAlign("left");
				int colindex=meta.getColumnIndex(colname);

				if(colindex>=0){
					//System.out.println(colname+"="+meta.getColumnNullables()[colindex]);
					int colsize=Integer.parseInt(meta.getColumnSizes()[colindex]);
					String datatype=meta.getColumnDataTypes()[colindex];
					String nullable=meta.getColumnNullables()[colindex].equals("0")?"false":"true";
					if(nullable.equalsIgnoreCase("true")&&datatype.toLowerCase().contains("char")){
						int index=tab.getColumnIndex(colname);
						if(index>0){
							datadomain[index]="null";
						}
					}
					col.setId(colname.toLowerCase()+":"+nullable+":"+String.valueOf(meta.getColumnDataTypes()[colindex])+":"+colsize);
					
					col.setWidth("100");
					if (colsize>100){
						col.setType("txt");
						col.setWidth("200");
					}else if(datatype.contains("DATE")){
						col.setType("dhxCalendar");
						
						col.setFormat("%m/%d/%Y");
						col.setSort("date");
						col.setAlign("center");
					
					}else if(datatype.contains("NUMBER")){	 
						col.setAlign("center");
						col.setType("ed");
					}else if(domain.toLowerCase().contains("boolean")){	 
						col.setAlign("center");
						col.setType("ch");
						col.setWidth("100");
					}else{
						col.setType("ed");
					}
				
				}else{
					col.setId(colname.toLowerCase());
					col.setType("ro");
					col.setWidth("100");
				}
				if(props.get(colname.toLowerCase())!=null){
						columnname.add(caption);
						options=props.get(colname.toLowerCase());
						col.setType("corotxt");
						col.setWidth("200");
				}else if(codes.get(colname.toLowerCase())!=null){
					  	columnname.add(caption);
					  	options=codes.get(colname.toLowerCase());
					  	col.setType("corotxt");
					  	col.setWidth("200");
				}else if(relationprops.get(colname.toLowerCase())!=null){
					  columnname.add(caption);
					  options=relationprops.get(colname.toLowerCase());
					  //col.setType("corotxt");
					  col.setType("co");
					  col.setWidth("200");
                }else{
                	columnname.add(caption);
                }
				
				if(colname.equalsIgnoreCase("projectcode")&&table.equalsIgnoreCase("projectcode")||
						colname.equalsIgnoreCase("mainjobcode")&&table.equalsIgnoreCase("maincode")||
						colname.equalsIgnoreCase("subjobcode")&&table.equalsIgnoreCase("subcode")||
						colname.equalsIgnoreCase("maincode") ||colname.equalsIgnoreCase("subcode")){
					col.setType("ro");
					col.setWidth("100");
					col.setAlign("center");
				}
				if(colname.equalsIgnoreCase("maincode")&&table.equalsIgnoreCase("bidrequest")){
					 col.setType("ed");
					 col.setWidth("100");
					 options=null;
				}
				col.setValue(columnname);
				col.setOption(options);
				colls.add(col);
				
				if(domain.toLowerCase().contains("money")){
					if(moneycols.isEmpty()){
						moneycols.add(0, colname.toLowerCase());
					}else{
						String val=moneycols.get(0)+","+colname;
						moneycols.remove(0);
						moneycols.add(0,val.toLowerCase());
					}
				}
				//System.out.println("Col="+colname);
			}
			head= new Head(colls);
			int i=0;
			boolean hasdata=false;
			//if column name and 1st row 1st column is same
			
			if(tab.getRowCount()>0 &&tab.getColumnNames()[0].equalsIgnoreCase(tab.getFieldValue(tab.getColumnNames()[0], 0))){
				i=1;
			}
			for( ;i<tab.getRowCount();i++){
				hasdata=true;
				List<String> cells =new ArrayList<String>();
				for (String coll:columns){
					cells.add(tab.getFieldValue(coll, i));
					//System.out.println("Cell="+tab.getFieldValue(coll, i));
				}

				rowlist.add(new Row(String.valueOf(i),cells));

			}
			
			//if has no data fill up dummy row
			if(!hasdata){
				List<String> cells =new ArrayList<String>();
				int colidx=0;
				for (String coll:tab.getColumnNames()){
					if(exceptioncols.contains(coll.toLowerCase())){
						cells.add("0");
					}else{
						cells.add(colidx==0?"newid":"new");
					}
					colidx++;
				}

				rowlist.add(new Row(String.valueOf(i),cells));
			}

			rows= new Rows(rowlist,head);
			
			String buttonaction=this.getButtonAction(table);
			
			if(!this.isEmptyValue(buttonaction)){
				ArrayList<String> data= new ArrayList<String>();
				data.add(buttonaction);
				Userdata button= new Userdata("button.action",data);
				rows.getUserdata().add(button);
			}
			String childbuttonaction=this.getChildButtonAction(table);
			if(!this.isEmptyValue(childbuttonaction)){
				ArrayList<String> data= new ArrayList<String>();
				data.add(childbuttonaction);
				Userdata button= new Userdata("child.action",data);
				rows.getUserdata().add(button);
			}
		//}
		if(datadomain.length>0){
			Userdata validator= new Userdata("grid.validator",getValidators(datadomain));
			rows.getUserdata().add(validator);
		}
		if(!moneycols.isEmpty()){
			Userdata moneydata= new Userdata("grid.moneycols",moneycols);
			rows.getUserdata().add(moneydata);
		}
		return rows;
	}
	
	public Rows getDeletedRows(String objid){
		Rows rows=new Rows();
		ArrayList<Row> rowlist= new ArrayList<Row>();
		List<String> cells =new ArrayList<String>();
		cells.add(objid);
		rowlist.add(new Row("1",cells));
		rows.setRow(rowlist);
		return rows;
	}
	
	public Rows getFailedMessage(String msg){
		Rows rows=new Rows();
		ArrayList<Row> rowlist= new ArrayList<Row>();
		List<String> cells =new ArrayList<String>();
		cells.add(msg);
		rowlist.add(new Row("1",cells));
		rows.setRow(rowlist);
		return rows;
	}
	
	public Rows getXMLSummaryRows(TemplateTable tab ,String captions[]){

		Rows rows=null;
		Head head=null;
		String [] colnames={"Key Facts","Values"};
		ArrayList<Row> rowlist= new ArrayList<Row>();


		if(tab.getRowCount()>0){

			List<Column> colls =new ArrayList<Column>();
		   
			for (String colname:colnames){
				List<String> columnname=new ArrayList<String>();
				List<Option> options= new ArrayList<Option>();
				Column col= new Column();
				col.setSort("str");
				col.setAlign("right");
				col.setType("ro");
				col.setWidth("*");
				columnname.add(colname);
				col.setValue(columnname);
				col.setOption(options);
				colls.add(col);
				
			}
			head= new Head(colls);
			
            int i=0;
			for (String coll:tab.getColumnNames()){
				List<String> cells =new ArrayList<String>();
				cells.add(captions[i]);
				cells.add(tab.getFieldValue(coll, 0));
				rowlist.add(new Row(String.valueOf(i),cells));
				i++;
			}


			rows= new Rows(rowlist,head);
		}

		return rows;
	}
    
	public Rows getXMLConsoleRows(String username ){

		Rows rows=null;
		Row master=null;
		ArrayList<Row> rowlist= null;
		ArrayList<Row> masterrowlist= new ArrayList<Row>();
		String sql= "select c.* from table_console c, table_messagequeue mq ,table_user u where "+
		"  mq.objid=c.mqid  and u.objid=mq.messagequeue2user and upper(u.loginname)= upper('"+username+"') "
				+ " order by c.name";
		TemplateTable tab=this.getResultSet(sql);
        String oldname="";  
			for (int i=0; i<tab.getRowCount(); i++){
				String name=tab.getFieldValue( "name",i);
				if(this.isEmptyValue(oldname) &&!isEmptyValue(name)||!oldname.equalsIgnoreCase(name)){
					oldname=name;
					if(master!=null){
						master.setRow(rowlist);
						masterrowlist.add(master);
					}
					rowlist= new ArrayList<Row>();
					master=new Row();
					master.setId(name+"s");
					List<String> cells =new ArrayList<String>();
					cells.add(tab.getFieldValue("name",i));
					cells.add("See All "+ tab.getFieldValue("name",i) + " related tasks");
					master.setCell(cells);
				}
				
				if(oldname.equalsIgnoreCase(name)){
					List<String> cells =new ArrayList<String>();
					cells.add(tab.getFieldValue("name",i)+"("+tab.getFieldValue("elapseday",i)+"):"+tab.getFieldValue("keyobjid",i));
					if(tab.getFieldValue("title",i).equalsIgnoreCase(tab.getFieldValue("description",i))){
						cells.add(tab.getFieldValue("title",i));
					}else{
						cells.add(tab.getFieldValue("title",i)+ " --" +tab.getFieldValue("description",i));
					}
					rowlist.add(new Row(String.valueOf(i+1),cells));
					
				}
			}
			if(master!=null){
				master.setRow(rowlist);
				master.setOpen("1");
				masterrowlist.add(master);
			}
			rows= new Rows();
			rows.setRow(masterrowlist);
		

		return rows;
	}
	
	public ArrayList<String> getSummaryMoneyColsIndex(String[] domians){
		String moneyindex="";
		ArrayList<String> result= new ArrayList<String>();
		for (int i=0; i<domians.length;i++){
			String domain=domians[i];
			if(!this.isEmptyValue(domain) &&domain.toLowerCase().contains("money")){
				if(this.isEmptyValue(moneyindex) ){
					moneyindex+=i;
				}else{
					moneyindex+=","+i;
				}
			}
		}
		result.add(moneyindex);
		
		return result;
	}
	public ArrayList<String> getValidators(String[] domians){
		String validator="";
		ArrayList<String> result= new ArrayList<String>();
		for (int i=0; i<domians.length;i++){
			String domain=domians[i];
			if(this.isEmptyValue(validator)){
				validator+=dhtmlxValidator(domain);
			}else{
				validator+=","+dhtmlxValidator(domain);
			}
		}
		result.add(validator);
		
		return result;
	}
	
	private String dhtmlxValidator(String domain){
		if(domain.toLowerCase().contains("int_")){
			return "ValidInteger";
		}else if(domain.toLowerCase().contains("code_")){
			return "ValidAplhaNumeric";
		}else if(domain.toLowerCase().contains("string")){
			return "NotEmpty";
		}else if(domain.toLowerCase().contains("email_")){
			return "ValidEmail";
		}else if(domain.toLowerCase().contains("money_")){
			return "ValidCurrency";
		}else if(domain.toLowerCase().contains("int_")){
			return "ValidInteger";
		}else if(domain.toLowerCase().contains("date_")){
			return "NotEmpty";
		}else if(domain.toLowerCase().contains("float_")){
			return "ValidNumeric";
		}else if(domain.toLowerCase().contains("status_")){
			return "ValidAplhaNumeric";
		}else if(domain.toLowerCase().contains("phone_")){
			return "ValidAplhaNumeric";
		}else if(domain.toLowerCase().contains("null")){
			return "Empty";
			
		}
		return "NotEmpty";
	}
	public HashMap<String,ArrayList<Option>> getCodeObject(String table, String fieldlist[],String projectcode,String groupuser){
		return new CodeManager().getCodeObject(table, fieldlist, projectcode,groupuser);
	}
	public String getButtonAction(String table){
		return new ServiceObject().getButtonAction(table);
	}
	public String getChildButtonAction(String table){
		return new ServiceObject().getChildButtonAction(table);
	}
	public ArrayList<String> getChartSelectColumns(String table){
		return new ServiceObject().getChartSelectColumns(table);
	}
	public ArrayList<String> getChartPropertyJSON(String table, TemplateTable tabdata, String field){
		HashMap<String,String> chartprop= new ServiceObject().getChartProperty(table, field);
		ArrayList<String> result= new ArrayList<String>();
		
		String data="";
		String grid="";
		if(chartprop!=null &&!chartprop.isEmpty()){
			String chartname=chartprop.get("chartname");
			String charttype=chartprop.get("charttype");
			String[] fieldnames=chartprop.get("fieldnames").split(",");
			String chartdatatype=chartprop.get("chartdatatype");
			String chartalias=chartprop.get("chartalias");
			String x_axis=chartprop.get("x_axis");
			String y_axis=chartprop.get("y_axis");
			String selectcolumn=chartprop.get("selectcolumn");
			String[] captions=chartprop.get("captions").split(",");
			String isdefault=chartprop.get("isdefault");
			
			
			grid+="{"+
					"\n\tview:'"+charttype+"',\n\t"+
					"\n\tlabel:'"+chartalias+"',\n\t"+
					"\n\ttooltip:{"+
					"\n\t\t	template:'#data#'"+
					"\n\t},"+
					"\n\tlegend:{\"template\":\"#caption#\",\"marker\":{\"type\":\"square\",\"width\":25,\"height\":15}},"+
					"\n\tgradient: false,"+
					"\n\tvalue:'#data#'"+
				"\n}";
		
			
			if((fieldnames.length==captions.length) && fieldnames.length>0){
			
				int c=0;
				float balance;
				String val="0.0";
				for(String col:fieldnames){
					if(c==0){
						val=tabdata.getFieldValue(col, tabdata.getRowCount()-1);
						data+="\n\t\t{data:'"+tabdata.getFieldValue(col, tabdata.getRowCount()-1)+"',caption:'"+captions[c]+"'}";
					}else{
						data+=",\n\t\t{data:'"+tabdata.getFieldValue(col, tabdata.getRowCount()-1)+"',caption:'"+captions[c]+"'}";
					}
					c++;
				}
				if(!val.isEmpty() &&fieldnames.length==1 && chartdatatype.equalsIgnoreCase("percent")){
					 balance=100-Float.valueOf(val);
					 data+=",\n\t\t{data:'"+balance+"',caption:'Remain'}";
				}
				
			}
			
			result.add(grid);
			result.add("["+data+"\n]");
		
		
		}
		return result;
	}
	
	public HashMap<String,ArrayList<Option>> getRelationOptions (String relationlist[],String groupuser,String bqn)
	{
		HashMap<String,ArrayList<Option>> props= new HashMap<String,ArrayList<Option>>();

		for(String relation:relationlist){
		    String [] tables=relation.split(":");
		    if(tables.length>=3 && tables[2].equals("list")){
		    	String relationname=tables[1];
		    	String globaltable=relationname.split("2")[0];
		    	String sql="";
		    	String filterVal="";
		    	if(tables.length==4 &&!this.isEmptyValue(bqn) &&!this.isEmptyValue(tables[3]) &&tables[3].toLowerCase().contains("select")){
		    		
		    		sql=tables[3].replaceAll("@bqn", "'"+bqn+"'") +" order by 2";
		    		sql=sql.replaceAll("@groupuser",  "'"+groupuser+"'");
		    	}else if(!this.isEmptyValue(bqn)&&bqn.length()>=5){
		    		sql= "select *from table_"+tables[0]+ " where groupuser='"+groupuser+"' and upper(bqn)=upper('"+bqn+"') or bqn is null order by 2";
		    	}else if(relationname.equalsIgnoreCase("customlist2attribute")  && globaltable.equalsIgnoreCase("customlist")){
		    		sql="select distinct objid, tablename||' : '||name as name from table_attribute "
		    				+ "where upper(HASPROPERTY)=upper('yes') and upper(hascodeobject)=upper('no') and groupuser='admin@biderp.com' order by tablename";
		    	}else if(relationname.equalsIgnoreCase("customlist2object")  && globaltable.equalsIgnoreCase("customlist")){
		    		sql= "select distinct attribute2object objid, tablename as name from table_attribute "
		    				+ " where upper(HASPROPERTY)=upper('yes') and upper(hascodeobject)=upper('no') and groupuser='admin@biderp.com' order by 2";
		    	}else if(this.exceptiontables.toLowerCase().contains(globaltable.toLowerCase())){
		    		sql= "select *from table_"+tables[0]+ " where groupuser='admin@biderp.com' order by 2";
		    	
		    	}else{
		    		sql= "select *from table_"+tables[0]+ " where groupuser='"+groupuser+"' order by 2";
		    	}
		    	TemplateTable prop=this.getResultSet(sql);
			
					ArrayList<Option> options= new ArrayList<Option>();
		            if( prop.getRowCount()>0){
						for(int n = 0; n < prop.getRowCount(); n++){
							
							ArrayList<String> optionval= new ArrayList<String>();
							if(tables[0].toLowerCase().equals("messagequeue")){
								optionval.add(prop.getFieldValue("firstname", n)+ " "+prop.getFieldValue("lastname", n));
							}else{
								optionval.add(prop.getFieldValue("name", n));
							}
							options.add(new Option(prop.getFieldValue("objid", n),optionval));
			
							props.put(tables[1].toLowerCase(), options);
						}
		            }else{
		            	
		            	ArrayList<String> optionval= new ArrayList<String>();
						
						optionval.add("--No Selection Available--");
						
						options.add(new Option("null",optionval));
		
						props.put(tables[1].toLowerCase(), options);
		            	
		            }
		
				
		   }
		}
		return props;
	}
	public HashMap<String,ArrayList<Option>> getRelationOptions (String relationlist[],String groupuser)
	{
		HashMap<String,ArrayList<Option>> props= new HashMap<String,ArrayList<Option>>();

		for(String relation:relationlist){
		    String [] tables=relation.split("2");
		    if(tables.length==2){
		    	String sql= "select *from table_"+tables[1]+ " where groupuser='"+groupuser+"' order by 2";
		    	TemplateTable prop=this.getResultSet(sql);
			
					ArrayList<Option> options= new ArrayList<Option>();
		
					for(int n = 0; n < prop.getRowCount(); n++){
						
						ArrayList<String> optionval= new ArrayList<String>();
						if(relation.contains("2messagequeue")){
							optionval.add(prop.getFieldValue("firstname", n)+ " "+prop.getFieldValue("lastname", n));
						}else{
							optionval.add(prop.getFieldValue("name", n));
						}
						options.add(new Option(prop.getFieldValue("objid", n),optionval));
		
						props.put(relation.toLowerCase(), options);
					}
		
				
		    }
		}
		return props;
	}
	public HashMap<String,ArrayList<Option>> getProperty(String table, String fieldlist[],String groupuser){
		return new ServiceObject().getProperty(table, fieldlist,groupuser);
	}
	public String getRandomNumber(){
		 Random generator = new Random();      
		return(String.valueOf(generator.nextInt(999999999)));
	}

	public static boolean isEmptyValue(String val){
		if(val==null||val.isEmpty()){
			return true;
		}
		return false;
	}

	/****************************************************************************************
	 * the method getLogFileList take a file's path, and lists all files with .log extension
	 * return a array of file names which matches the filter.
	 ****************************************************************************************/
	public String[] getLogFileList(String aPath) {

		String [] m_List ;
		String [] m_Log=null;
		int findex=0;
		try{
			File m_File = new File(aPath);
			if (m_File.isAbsolute() && m_File.isDirectory()){

				m_List =m_File.list();
				for (int i=0; i<m_List.length; i++){
					int index = m_List[i].indexOf(".log");
					if (m_List[i].substring(index+1).equalsIgnoreCase("log")){
						// m_Log[findex]=m_List[i];
						findex++;
					}
				}
				if (findex>0){
					int k=0;
					m_Log=new String[findex];
					for (int i=0; i<m_List.length; i++){
						int index = m_List[i].indexOf(".log");
						if (m_List[i].substring(index+1).equalsIgnoreCase("log")){
							m_Log[k]=m_List[i];
							k++;
						}
					}
				}
			}

		}catch(Exception e) {
			e.printStackTrace();
			logger.info(e.getMessage());
		}
		return m_Log;
	}
	// parses the input file and puts the key-value pairs in the vector.
	//  Also, resets the bufreader to the point marked before.
	public Vector parseInputFile(BufferedReader bufReader, String strHeader) throws IOException
	{
		int i=0;
		strHeader =  "[" + strHeader + "]";
		Vector vBuf = new Vector();
		String str = bufReader.readLine();
		while(str != null)
		{
			if(str.equalsIgnoreCase(strHeader))
			{
				while(!str.equalsIgnoreCase("[end]"))
				{
					vBuf.addElement(str);
					str = bufReader.readLine();
				}
				bufReader.reset();
				return vBuf;
			}
			str = bufReader.readLine();
		}
		bufReader.reset();
		return vBuf;
	}
	// parses the input file and puts the key-value pairs in the vector.
	//  Also, resets the bufreader to the point marked before.
	public Vector parseInputXmlFile(BufferedReader bufReader, String strHeader) throws IOException
	{
		int i=0;
		String endTag="</"+strHeader+">";
		strHeader =  "<" + strHeader + ">";
		Vector vBuf = new Vector();
		String str = bufReader.readLine();
		while(str != null)
		{
			if(str.equalsIgnoreCase(strHeader))
			{
				while(!str.equalsIgnoreCase(endTag))
				{
					vBuf.addElement(str);
					str = bufReader.readLine();
				}
				bufReader.reset();
				return vBuf;
			}
			str = bufReader.readLine();
		}
		bufReader.reset();
		return vBuf;
	}
	// Parses the vector for the value, given the key.
	public String parseInputValue(Vector vBuf, String sKey)
	{
		for (int i=0; i<vBuf.size(); i++)
		{
			String strElm = new String();
			strElm = (String) vBuf.elementAt(i);
			if(strElm.toUpperCase().startsWith(sKey.toUpperCase()))
			{
				int idx = strElm.indexOf("=");
				strElm = strElm.substring(idx+1);
				return strElm;
			}
		}
		return null;
	}
	public Vector getGridRowData(String data,String rowseperator){
		StringTokenizer st = new StringTokenizer(data, rowseperator);
		Vector vRet=new Vector();
		int count=0;
		while (st.hasMoreTokens() ) {
			vRet.addElement(st.nextToken());
			count++;
		}
		if(count==0)
			vRet.addElement(data);

		return(vRet);

	}
	public Vector getGridColumnData(Vector rowData,String columnSeperator,int rowno){

		StringTokenizer st = new StringTokenizer((String)rowData.elementAt(rowno), columnSeperator);
		Vector vRet=new Vector();
		int count=0;
		while (st.hasMoreTokens() ) {
			vRet.addElement(st.nextToken());
			count++;
		}
		if(count==0)
			vRet.addElement(rowData.elementAt(rowno));

		return(vRet);

	}
	public String[] getGridDataArray(String data,String rowseperator,String columnSeperator){
		Vector rowData=getGridRowData(data, rowseperator);
		String [] retStr=new String[1];
		int count=0;
		for (int i=0; i<rowData.size(); i++){
			StringTokenizer st = new StringTokenizer((String)rowData.elementAt(i), columnSeperator);
			int length=st.countTokens()*rowData.size();
			retStr=new String[length];

			while (st.hasMoreTokens() ) {
				//logger.info("Col value="+st.nextToken());
				retStr[count]=st.nextToken();
				count++;
			}
			if(count==0)
				retStr[count]=(String)rowData.elementAt(i);
		}
		return(retStr);
	}
	
	//In query mode it will return the date for mssql not time but while update it will insert time with date
	public String getConvertDate(String dbtype,String querytype,String queryfield,String format,String fieldvalue){
		//logger.info("dbtype="+dbtype+" querytype="+ querytype+" queryfield="+queryfield +" format="+format );
		int index=queryfield.indexOf(".");
		String aliasname="";
		String fldalis[]=this.getString2TokenArray(queryfield," ");
		if (fldalis.length>1){
			queryfield=fldalis[0];
			aliasname=fldalis[1];
		}else{
			aliasname=queryfield.substring(index+1);
		}
		if (dbtype.equalsIgnoreCase("Oracle")&&querytype.equalsIgnoreCase("select")){
			return("to_char("+queryfield+",'"+format+"')\""+ aliasname+"\"");
		}else if (dbtype.equalsIgnoreCase("mysql")&&querytype.equalsIgnoreCase("select")){
			return(queryfield+" `"+ aliasname+"`");

		}else if (dbtype.equalsIgnoreCase("Mssql")&&querytype.equalsIgnoreCase("select")){
			return("convert(char,"+queryfield+",101) \""+ aliasname+"\"");
		}else if (dbtype.equalsIgnoreCase("Oracle")&&querytype.equalsIgnoreCase("update") ||
				dbtype.equalsIgnoreCase("Oracle")&&querytype.equalsIgnoreCase("insert")){
			return("to_date('"+fieldvalue+"','"+format+"')");
		}else if (dbtype.equalsIgnoreCase("mysql")&&querytype.equalsIgnoreCase("update") ||
				dbtype.equalsIgnoreCase("mysql")&&querytype.equalsIgnoreCase("insert")){
			return("date('"+fieldvalue+"')");
		}else if (dbtype.equalsIgnoreCase("Mssql")&&querytype.equalsIgnoreCase("update") ||
				dbtype.equalsIgnoreCase("Mssql")&&querytype.equalsIgnoreCase("insert")){
			return("cast('" + fieldvalue + "' as datetime)") ;

		}
		return(null);
	}
	//This datetime is used for query fields for select or update
	public String getConvertDateTime(String dbtype,String querytype,String queryfield,String format,String fieldvalue){
		//logger.info(">>>>Calling getConvertDateTime() fieldvalue="+fieldvalue);
		int index=queryfield.indexOf(".");
		if (dbtype.equalsIgnoreCase("Oracle")&&querytype.equalsIgnoreCase("select")){
			return("to_char("+queryfield+",'"+format+"')\""+ queryfield.substring(index+1)+"\"");
		}else if (dbtype.equalsIgnoreCase("mysql")&&querytype.equalsIgnoreCase("select")){
			return(queryfield+" `"+ queryfield.substring(index+1)+"`");

		}else if (dbtype.equalsIgnoreCase("Mssql")&&querytype.equalsIgnoreCase("select")){
			return("convert(datetime,"+queryfield+",101) \""+ queryfield.substring(index+1)+"\"");
		}

		if(fieldvalue!=null &&!fieldvalue.isEmpty() && !fieldvalue.equalsIgnoreCase("0")){
			if (dbtype.equalsIgnoreCase("Oracle")&&querytype.equalsIgnoreCase("update") ||
					dbtype.equalsIgnoreCase("Oracle")&&querytype.equalsIgnoreCase("insert")){
				return("to_date('"+fieldvalue+"','"+format+"')");
			}else if (dbtype.equalsIgnoreCase("mysql")&&querytype.equalsIgnoreCase("update") ||
					dbtype.equalsIgnoreCase("mysql")&&querytype.equalsIgnoreCase("insert")){
				return("date('"+fieldvalue+"')");

			}else if (dbtype.equalsIgnoreCase("Mssql")&&querytype.equalsIgnoreCase("update") ||
					dbtype.equalsIgnoreCase("Mssql")&&querytype.equalsIgnoreCase("insert")){
				return("cast('" + fieldvalue + "' as datetime)") ;	
			}
		}
		return(null);
	}
	//This datetime is used to compare datetime in filter condition of the where clause of the query
	//for Oracle database
	public String getVarchar2DateTime(String dbtype,boolean isFilterCondition,String queryfield,String format,String fieldvalue){
		int index=queryfield.indexOf(".");

		if(fieldvalue==null||fieldvalue.equals("")){
			if (dbtype.equalsIgnoreCase("Oracle")&&isFilterCondition)
				return("to_date(to_char("+queryfield+",'"+format+"'),'"+format+"')");
			else if(dbtype.equalsIgnoreCase("Oracle")&&!isFilterCondition)
				return("to_date(to_char("+queryfield+",'"+format+"'),'"+format+"')\""+ queryfield.substring(index+1)+"\"");
			else if (dbtype.equalsIgnoreCase("mssql")&&isFilterCondition)
				return("cast(" + queryfield + " as datetime)") ;
			else if(dbtype.equalsIgnoreCase("mssql")&&!isFilterCondition)
				return("cast(" + queryfield + " as datetime) \""+ queryfield+"\"");
			else if (dbtype.equalsIgnoreCase("mysql")&&isFilterCondition)
				return( "date("+queryfield + ")") ;
			else if(dbtype.equalsIgnoreCase("mysql")&&!isFilterCondition)
				return( queryfield + " `"+ queryfield+"`");
		}else if(fieldvalue!=null&&!fieldvalue.equals("")){
			int flddotindex=(fieldvalue.indexOf(".")<0?fieldvalue.length():fieldvalue.indexOf("."));
			if (dbtype.equalsIgnoreCase("Oracle")&&isFilterCondition)
				return("to_date('"+fieldvalue.substring(0,flddotindex)+"','"+format+"')");
			else if(dbtype.equalsIgnoreCase("Oracle")&&!isFilterCondition)
				return("to_date(to_char('"+fieldvalue+"','"+format+"'),'"+format+"')\""+ queryfield.substring(index+1)+"\"");
			else if (dbtype.equalsIgnoreCase("mssql")&&isFilterCondition)
				return("cast('" + fieldvalue + "' as datetime)") ;
			else if(dbtype.equalsIgnoreCase("mssql")&&!isFilterCondition)
				return("convert(datetime,'"+fieldvalue+"',101) \""+ queryfield+"\"");
			else if (dbtype.equalsIgnoreCase("mysql")&&isFilterCondition)
				return( "date('"+fieldvalue + "')") ;
			else if(dbtype.equalsIgnoreCase("mysql")&&!isFilterCondition)
				return( "date('"+queryfield + "') `"+ queryfield+"`");
		}
		return(null);
	}
	public boolean executeQuery(String sql){
		//logger.info(sql);
		try{
			TemplateQuery query =new TemplateQuery();
			query.setQuery(sql);
			//logger.info("\nExecuting Query: "+query.getQuery()+"\n");
			TemplateTable output =new TemplateTable();
			output = query.getTableResultset();
			if (output.getRowCount()>0 ){
				return(true);
			}else{
				if(ACONST.GENERATE_LOG)
					logger.info("Query Failed! :" + sql);
			}
		}catch (Exception e){
			logger.info("Query Failed! :" + sql);
			e.printStackTrace();
		}
		return(false);
	}

	public String getObjId(String sql){
		try{
			//logger.info(sql);
			TemplateQuery query =new TemplateQuery();
			query.setQuery(sql);
			TemplateTable output =new TemplateTable();
			output = query.getTableResultset();
			if (output.getRowCount()>0 ){
				return("'"+output.getRow(0)[0]+"'");
			}else{
				logger.info("Query Failed! :" + sql);
			}
		}catch (Exception e){
			logger.info("Query Failed! :" + sql);
			e.printStackTrace();
		}
		return("");
	}
	public String getUniqueFieldValue(String sql,String fieldname){
		//logger.info(sql);
		try{
			TemplateQuery query =new TemplateQuery();
			query.setQuery(sql);
			TemplateTable output =new TemplateTable();
			output = query.getTableResultset();
			if (output.getRowCount()==1 ){
				return(output.getFieldValue(fieldname,0));
			}else{
				logger.info("Query Failed! :" + sql);
			}
		}catch (Exception e){
			logger.info("Query Failed! :" + sql);
			e.printStackTrace();
		}
		return("");
	}

	public TemplateTable getTableMetaData(String table){
		if(!table.contains("table_")){
			table="table_"+table;
		}
		TemplateQuery query =new TemplateQuery();
		query.setQuery(table);
		//return(new TemplateQuery().getTableMetaData(table));
		return(query.getTableMetaData(table));

	}
	public TemplateTable getResultSet(String sql){

		try{
			TemplateQuery query =new TemplateQuery();
			query.setQuery(sql);
			TemplateTable output=query.getTableResultset();
			if (output.getRowCount()>0 ){
				if(ACONST.GENERATE_LOG)
					logger.info("\n***Query="+sql);
				return(output);
			}else{
				if(ACONST.GENERATE_LOG)
					logger.info("\n>>>Query Failed! No Record Exists:\n" + sql);
			}
		}catch (Exception e){
			logger.info("\n>>>Query Failed! No Record Exists:\n" + sql);
			e.printStackTrace();
		}
		return(new TemplateTable());
	}
	public int getArrayFieldIndex(String name, String[] array){
		int index=-1;
		for(int ip=0;ip<array.length;ip++){
			//System.out.print("\n Arry field index for="+array[ip] + "name="+name);
			if(array[ip].equalsIgnoreCase(name)){
				index=ip;
				break;
			}
		}
		return(index);
	}
	public String[] getString2TokenArray(String data,String seperator){

		String [] retStr;
		int count=0;
		if(data!=null && !data.equals("")){
			StringTokenizer st = new StringTokenizer(data, seperator);
			int length=st.countTokens();
			retStr=new String[length];

			while (st.hasMoreTokens() ) {
				//logger.info("token="+st.nextToken());
				retStr[count]=st.nextToken().trim();
				count++;
			}
			return(retStr);
		}
		return(null);
	}

	//Returns a String array size of given length
	public String[] getStringArrayByLength(String[] array,int length){
		String[] tmparray=new String[length];
		for(int i=0;i<length;i++){
			tmparray[i]=array[i];
			//logger.info("String index="+i +" value="+array[i]);
		}
		return(tmparray);
	}
	//Returns a array size of given length
	public int[] getIntegerArrayByLength(int[] array,int length){
		int[] tmparray=new int[length];
		for(int i=0;i<length;i++){
			tmparray[i]=array[i];
			//logger.info("Int index="+i +" value="+array[i]);
		}
		return(tmparray);
	}
	public String replaceSingleQouteForDatabase(String s){
		boolean isfirst=true;
		String retStr="";
		if(s!=null && !s.equals("")){
			if(s.indexOf("'")>0){
				StringTokenizer st= new StringTokenizer(s,"'");
				while (st.hasMoreTokens() ) {
					if(isfirst){
						isfirst=false;
						retStr=st.nextToken();
					}else{
						retStr+="\\''"+st.nextToken();
					}
				}
			}else{
				retStr=s;
			}
		}
		return(retStr);
	}

	/*   public String replaceSingleQouteForDatabase(String s){
          boolean isfirst=true;
          String ms=s;
          if(s!=null&&!s.equals("")&&s.indexOf("default")>0){
            int dlength=s.length();
            int defualtlength="default".length();
            int dcut=s.indexOf("default");
            ms=( dlength>0 && dlength>defualtlength)?s.substring(0,dcut):s;
          }
          String retStr="";
          if(ms!=null && !ms.equals("")){
              if(ms.indexOf("'")>0){
                StringTokenizer st= new StringTokenizer(ms,"'");
                while (st.hasMoreTokens() ) {
                    if(isfirst){
                      isfirst=false;
                      retStr=st.nextToken();
                    }else{
                      retStr+="\\''"+st.nextToken();
                    }
                  }
                }else{
                  retStr=ms;
                }
              }
              return(retStr);
          }
	 */

	//This method will replace same as replaceStringWith plus special charecter like (+,>,< etc) with new delimitor
	public String replaceStringWithPlus(String s,String oldDelimitor,String newDelimitor){
		boolean isfirst=true;
		String retStr="";
		//System.out.print(s);
		if(s!=null && !s.equals("")){
			if(s.indexOf(oldDelimitor)>0){
				StringTokenizer st= new StringTokenizer(s,oldDelimitor);
				while (st.hasMoreTokens() ) {
					//logger.info(retStr);
					if(isfirst){
						isfirst=false;
						retStr=st.nextToken();
					}else{
						String tmpstr=st.nextToken();
						if(tmpstr!=null && tmpstr.indexOf("+")>=0 &&oldDelimitor.equalsIgnoreCase("+"))
							retStr+=oldDelimitor+tmpstr;
						else if(tmpstr!=null && tmpstr.indexOf(">")>=0&&!oldDelimitor.equalsIgnoreCase(">"))
							retStr+=oldDelimitor+tmpstr;
						else if(tmpstr!=null && tmpstr.indexOf("<")>=0&&!oldDelimitor.equalsIgnoreCase("<"))
							retStr+=oldDelimitor+tmpstr;
						else
							retStr+= newDelimitor+tmpstr;
					}
				}
			}else{
				retStr=s;
			}
		}
		return(retStr);
	}

	//This method will replace a string with new delimitor
	public String replaceStringWith(String s,String oldDelimitor,String newDelimitor){
		boolean isfirst=true;
		String retStr="";
		if(s!=null && !s.equals("")){
			if(s.indexOf(oldDelimitor)>0){
				StringTokenizer st= new StringTokenizer(s,oldDelimitor);
				while (st.hasMoreTokens() ) {
					if(isfirst){
						isfirst=false;
						retStr=st.nextToken();
					}else{
						retStr+= newDelimitor+st.nextToken();
					}
				}
			}else{
				retStr=s;
			}
		}
		return(retStr);
	}

	//This method verify if the string is a date if it matches one of the valid date format
	//dd/mm/yyyy,dd/mm/yy,mm/dd/yyyy,mm/dd/yy,dd-mm-yyyy,dd-mm-yy,mm-dd-yyyy,mm-dd-yy
	public boolean isDate(String s){
		boolean isfirst=true;
		boolean backslas=false;
		boolean dash=false;
		String delimitor="";
		String retStr="";
		if(s!=null && !s.equals("")){
			if(s.indexOf("/")>0){
				backslas=true;
				delimitor="/";
			}
			if(s.indexOf("-")>0){
				dash=true;
				delimitor="-";
			}

			StringTokenizer st= new StringTokenizer(s,delimitor);
			while (st.hasMoreTokens()&&st.countTokens()>=2 ) {
				try {
					int j= Integer.parseInt(st.nextToken()) ;

				}catch(NumberFormatException ne){
					return(false);
				}
			}
		}
		return(true);
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

	public String copyParentBQN2Child(String parenttable,String childtable,String relation, String parentId ){
		String dbtype=DatabaseTransaction.getDbType();
		String sql="\nbegin\n\t\t update table_"+childtable + " set bqn=(select bqn from table_"+parenttable +
				" where objid='"+parentId+"') where "+ relation +"= '"+parentId+ "' and bqn is null"+
				(dbtype!=null &&dbtype.equalsIgnoreCase("oracle")?";\n\t\t\tException\n\t\t\twhen no_data_found then \n\t\t\t null;\n\t\tend;":"");
		
		return sql;
	}
	public String copyParent2Child(TemplateTable parent, String childname,String[] childfield,String relation,String parentid){
		String dbtype=DatabaseTransaction.getDbType();
		String sql="";
		String val="";
		
		if(parent!=null &&parent.getRowCount()>0 &&childfield.length>0 ){
			for(int i=0;i<childfield.length; i++){
				//System.out.print("getting val");
				val=parent.getFieldValue(relation.indexOf("Code")>0? childfield[i]:relation.indexOf("Code")>0&&childfield[i].equalsIgnoreCase("MainCode")?"MainJobCode"
						:relation.indexOf("Code")>0&&childfield[i].equalsIgnoreCase("SubCode")?"SubJobCode"
								:relation.indexOf("Code")>0&&childfield[i].equalsIgnoreCase("TaskCode")?"TaskJobCode":childfield[i],parent.getRowCount()-1);
				//System.out.print("val="+val +" Relation="+relation.indexOf("Code"));
				if(!sql.equals("")&&!val.equals(""))
					sql=sql+",";
				try {
					int j= Integer.parseInt(val) ;
					sql+=(val!=null&&val.equals("")? "" :childfield[i]+"=" +j) ;
				}catch(NumberFormatException ne){
					sql+=(val!=null&&val.equals("")? "" :childfield[i]+"='"+val+"'");
				}

			}
		}
		if(!sql.equals(""))
			sql="begin\n\t\t\tupdate table_"+childname+" set "+sql +" where "+relation+"='"+parentid +"'"+
			(dbtype!=null &&dbtype.equalsIgnoreCase("oracle")?";\n\t\t\tcommit;\n\t\t\tException\n\t\t\twhen no_data_found then \n\t\t\t null;\n\t\tend;":"");
		//System.out.print("copy2="+sql);
		return(sql);
	}
	public String copyParent2Child(TemplateTable parent, String childname,String[] childfield){
		String sql="";
		String val="";
		
		if(parent!=null &&parent.getRowCount()>0 &&childfield.length>0 ){
			for(int i=0;i<childfield.length; i++){
				//System.out.print("getting val");
				val=parent.getFieldValue(childfield[i],parent.getRowCount()-1);
				//System.out.print("val="+val);
				if(!sql.equals("")&&!val.equals(""))
					sql=sql+",";
				try {
					int j= Integer.parseInt(val) ;
					sql+=(val!=null&&val.equals("")? "" :childfield[i]+"=" +j) ;
				}catch(NumberFormatException ne){
					sql+=(val!=null&&val.equals("")? "" :childfield[i]+"='"+val+"'");
				}

			}
		}
		if(!sql.equals(""))
			sql="update table_"+childname+" set "+sql ;
		//System.out.print("copy2="+sql);
		return(sql);
	}
	/**
	 * This method will be used to find total no of rows for any sql passed on the
	 * DogetPostSelect method in the screen object
	 * This method will return total no of row count for this query
	 */

	public int getRowCountBySelect(String query){
		//System.out.print("\nquery="+query);
		int length= ((query.indexOf("order by")>query.indexOf("group by") &&query.indexOf("group by")>0)?query.indexOf("group by"):query.indexOf("order by"));
		//System.out.print("\nlength="+length);
		String sql=" select count(*) \"count\" "+ (length>0?query.substring(query.indexOf("from"),length):query.substring(query.indexOf("from")));
		//System.out.print("\nsql="+sql);
		int count=0;
		//System.out.print("childname="+childname);
		TemplateTable tcount=this.getResultSet(sql);
		if(tcount!=null &&tcount.getRowCount()>0 ){
			for(int i=0;i<tcount.getRowCount(); i++){
				try {
					count= Integer.parseInt(tcount.getRow(i)[0]) ;

				}catch(NumberFormatException ne){
					count=0;
				}

			}
		}

		return(count);
	}
	//This method will replace a string with new value
	public String replaceSqlParamValue(String s,String paramname,String value){
		boolean isfirst=true;
		String retStr="";

		int beginlength=(s!=null&&!s.equals("")?s.toLowerCase().indexOf(paramname.toLowerCase().trim()):0);
		int endlength=beginlength+(paramname!=null&&!paramname.equals("")?paramname.length():0);
		if(beginlength>=0&&endlength>=0)
			retStr=s.substring(0,beginlength)+" "+value+" "+s.substring(endlength);
		else if(beginlength==-1)
			retStr=s;
		return(retStr);
	}
	//This method will replace a string with new value
	public String replaceSqlParamValue(String dbtype,String s,String paramname,String value,String datatype){
		boolean isfirst=true;
		String retStr="";
		String tmpval="";
		int tmpint=0;
		float tmpfloat;
		double tmpdouble;
		if(s!=null&&s.indexOf("exec")>=0 &&s.indexOf("exec")<6){
			if(datatype.equalsIgnoreCase("INTEGER")){
				tmpint=Integer.valueOf(value.substring(1,value.length()-1)).intValue();
				tmpval=String.valueOf(tmpint);
			}else if(datatype.equalsIgnoreCase("FLOAT")){
				tmpfloat=Float.valueOf(value.substring(1,value.length()-1)).floatValue();
				tmpval=String.valueOf(tmpfloat);
			}else if(datatype.equalsIgnoreCase("DOUBLE")){
				tmpdouble=Double.valueOf(value.substring(1,value.length()-1)).doubleValue();
				tmpval=String.valueOf(tmpdouble);
			}else if(datatype.equalsIgnoreCase("VARCHAR")){
				tmpval=value;
			}else if(datatype.equalsIgnoreCase("DATE") && !dbtype.equals("mssql")){
				int length=value.length();
				String tmpstr=value.substring(1,length-1);
				tmpval=getConvertDate(dbtype,"insert",paramname,"mm/dd/yyyy",tmpstr);
			}else{
				tmpval=value;
			}
		}else{
			tmpval=value;
		}

		int beginlength=(s!=null&&!s.equals("")?s.toLowerCase().indexOf(paramname.toLowerCase().trim()):0);
		int endlength=beginlength+(paramname!=null&&!paramname.equals("")?paramname.length():0);
		if(beginlength>=0&&endlength>=0)
			retStr=s.substring(0,beginlength)+" "+tmpval+" "+s.substring(endlength);
		return(retStr.equals("")?s:retStr);
	}
	/**
	 * This method will return the list of rule action which are valid for a particular query type
	 * for a specific database object based on the state of event
	 * Parameter:
	 * objectname=name of the table for which this list is required
	 * reason=type of the query like insert, update
	 * event=before or after the query is actually executed
	 * statusvalue=the current value of the status filed passed from the object
	 * objid=the objid of the current object for which this action is applicable
	 * return=this method will return the rule action sql
	 *
	 * Note: this method will work with the folowing constraints
	 * 1)The first input parameter should be @objid of the current object. If any other input parameter is assocaited with the 1st step
	 * the parameter should be assictaed with their values and there should not be any other seperatorbetween 2 consecutive parameters
	 * 2)All the query should have associated with their input parameter strating with @<paramname>
	 * 3)If there is a insert the previous query should fetch all the the associated parameter value using select state.
	 * This routine will insert or update the no of record selected in the previous select statement
	 * 4) in the insert or update query all the field which needs to be inserted or update should be listed in the input parameter
	 * and also in the values clause, If this is mis
	 * 5) While updating make sure that the previous select query brings only one record set if it is more than 1
	 * than only the first recordset values will be applied for the update
	 * */
	public boolean applyRuleAction(String objectname,String reason, String event,String statusval,String objid){
		String dbtype=DatabaseTransaction.getDbType();
		String actionsql="";
		String input[];
		String inputdatatype[];
		String inputval="";
		TemplateTable result[];
		TemplateQuery tq=new TemplateQuery();
		String sql="select tr.Name \"ActionName\",tr.TableName,tr.effectedtable,tr.Description,tr.reason,tr.ActionEvent,"+
				" tr.ActionIndex,tr.Status \"ActionStatus\",tr.RuleAction2ObjectRule,tro.Name \"RuleName\","+
				" tro.PropertyString,tro.PropertyValue,aq.stepno, aq.input,aq.InputDataType,aq.output,aq.querytype,aq.hasrecordset,aq.oraclequery,aq.mssqlquery"+
				" from table_ruleaction tr, table_objectrule tro,table_actionquery aq "+
				" where "+(dbtype.equalsIgnoreCase("oracle")?"upper(":"upper(") + "tro.name)='STATUS' and tro.propertyvalue='"+statusval+"' and upper(tr.tablename)=upper('"+
				objectname +"') and tr.reason='"+reason+"' and tr.ActionEvent='"+event+"'"+
				" and tro.objid=tr.ruleaction2objectrule and tr.objid=aq.actionquery2ruleaction order by aq.stepno";

		TemplateTable rule=(statusval!=null &&!statusval.equals("")?getResultSet(sql):null);
		if(rule!=null &&rule.getRowCount()>0){
			
			synchronized(ruleaction){
				result=new TemplateTable[rule.getRowCount()];
				for(int i=0;i<rule.getRowCount();i++){
					input=getString2TokenArray(rule.getFieldValue("input",i),"@");
					inputdatatype=getString2TokenArray(rule.getFieldValue("inputdatatype",i),"@");
					actionsql=(dbtype.equalsIgnoreCase("oracle")?rule.getFieldValue("oraclequery",i):rule.getFieldValue("mssqlquery",i));
					if(input!=null && input.length>0 &&actionsql.toLowerCase().indexOf("insert")<0
							&&actionsql.toLowerCase().indexOf("update")<0&&actionsql.toLowerCase().indexOf("delete")<0){
						for(int j=0; j<input.length;j++){
							if(input[j]!=null &&!input[j].equals("")){
								//String tmpval=result[i-1].getFieldValue(input[j].trim(),0);
								inputval=(input[j]!=null && input[j].equalsIgnoreCase("objid")?objid:"'"+((i-1)>=0&&result[i-1]!=null &&result[i-1].getRowCount()>0?result[i-1].getFieldValue(input[j],0)+"'":
									(input[j].indexOf("=")>0?input[j].substring(input[j].indexOf("="))+"'":"'")));
							}
							String datatype=(inputdatatype!=null &&inputdatatype.length>=j?inputdatatype[j]:"Varchar");
							if(inputval!=null&& !inputval.equals(""))
								actionsql=replaceSqlParamValue(dbtype,actionsql,"@"+input[j],inputval,datatype);
						}
						if(actionsql!=null &&!actionsql.equals(""))
							result[i]=(actionsql.toLowerCase().indexOf("select")>=0?getResultSet(replaceStringWith(actionsql,"\\","")):null);

					}
					if(input!=null && input.length>0 &&(actionsql.toLowerCase().indexOf("insert")>=0
							||actionsql.toLowerCase().indexOf("update")>=0||actionsql.toLowerCase().indexOf("delete")>=0)){
						if((i-1)>=0 && result[i-1]!=null &&result[i-1].getRowCount()>0)
							for(int k=0;k<result[i-1].getRowCount();k++){
								for(int j=0; j<input.length;j++){
									if(input[j]!=null &&!input[j].equals("")){
										String tmpval=result[i-1].getFieldValue(input[j].trim(),k);

										/*inputval=(input[j]!=null && input[j].equalsIgnoreCase("objid")?(actionsql.toLowerCase().indexOf("insert")>=0?
                                    tq.getPrimaryKey("table_"+rule.getFieldValue("effectedtable",i),"objid",result[i-1].getFieldValue("name",k)):result[i-1].getFieldValue(input[j].trim(),k))
                                    :"'"+((i-1)>=0&&result[i-1]!=null &&result[i-1].getRowCount()>0?result[i-1].getFieldValue(input[j].trim(),k)+"'":
                                    (input[j].indexOf("=")>0?input[j].substring(input[j].indexOf("="))+"'":"'")));
										 */
										inputval=(input[j]!=null && input[j].equalsIgnoreCase("objid")?(actionsql.toLowerCase().indexOf("insert")>=0?
												tq.getPrimaryKey():result[i-1].getFieldValue(input[j].trim(),k))
												:"'"+((i-1)>=0&&result[i-1]!=null &&result[i-1].getRowCount()>0?result[i-1].getFieldValue(input[j].trim(),k)+"'":
													(input[j].indexOf("=")>0?input[j].substring(input[j].indexOf("="))+"'":"'")));
									}
									//Added datatype here
									String datatype=(inputdatatype!=null &&inputdatatype.length>=j?inputdatatype[j]:"Varchar");
									if(input[j].toLowerCase().indexOf(("2"+objectname).toLowerCase().trim())>0)
										inputval=objid;
									if(inputval!=null&& !inputval.equals(""))
										actionsql=replaceSqlParamValue(dbtype,actionsql.trim(),("@"+input[j]).trim(),inputval.trim(),datatype);
									//actionsql=replaceSqlParamValue(actionsql.trim(),("@"+input[j]).trim(),inputval.trim());

								}
								executeQuery(replaceStringWith(actionsql,"\\",""));
								actionsql=(dbtype.equalsIgnoreCase("oracle")?rule.getFieldValue("oraclequery",i):rule.getFieldValue("mssqlquery",i));
							}
						if(actionsql!=null &&!actionsql.equals(""))
							result[i]=null;
					}

				}
			}
		}
		return(true);
	}

	/**
	 * This method will return the list of rule action which are valid for a particular query type
	 * for a specific database object based on the state of event
	 * Parameter:
	 * objectname=name of the table for which this list is required
	 * reason=type of the query like insert, update
	 * event=before or after the query is actually executed
	 * objid=the objid of the current object for which this action is applicable
	 * return=this method will return the rule action sql
	 *
	 * Note: this method will work with the folowing constraints
	 * 1)The first input parameter should be @objid of the current object. If any other input parameter is assocaited with the 1st step
	 * the parameter should be assictaed with their values and there should not be any other seperatorbetween 2 consecutive parameters
	 * 2)All the query should have associated with their input parameter strating with @<paramname>
	 * 3)If there is a insert the previous query should fetch all the the associated parameter value using select state.
	 * This routine will insert or update the no of record selected in the previous select statement
	 * 4) in the insert or update query all the field which needs to be inserted or update should be listed in the input parameter
	 * and also in the values clause, If this is mis
	 * 5) While updating make sure that the previous select query brings only one record set if it is more than 1
	 * than only the first recordset values will be applied for the update
	 * */
	public boolean applyObjectRuleForDelete(String objectname,int reason, int state,String objid){
		String dbtype=DatabaseTransaction.getDbType();
		String retstring="";
		String actionsql="";
		String oldword="";
		String newword="";
		String input[];
		String inputval="";
		TemplateTable result[];
		TemplateQuery tq=new TemplateQuery();
		try{
			//First verify whether any object rule associated with the current tablename
			ServiceObject so =new ServiceObject();
			if(!so.verifyObjectRule(objectname,reason,state))
				return(false);

			String sql="select tr.Name \"ActionName\",tr.TableName,tr.effectedtable,tr.Description,tr.reason,tr.ActionState,"+
					" tr.RuleIndex,tr.Status \"ActionStatus\","+
					" aq.stepno, aq.input,aq.output,aq.querytype,aq.hasrecordset,aq.oraclequery,aq.mssqlquery"+
					" from table_objectrule tr,table_actionquery aq "+
					" where upper(tr.tablename)=upper('"+ objectname +"') and tr.reason='"+reason+"' and tr.ActionState='"+state+"'"+
					" and tr.objid=aq.actionquery2objectrule and tr.objid='"+objid+"' order by tr.objid,aq.stepno";


			TemplateTable rule=(objid!=null &&!objid.equals("")?getResultSet(sql):null);
			if(rule!=null &&rule.getRowCount()>0){
				//synchronized this block against objectname
				synchronized(deleteaction){
					result=new TemplateTable[rule.getRowCount()];
					for(int i=0;i<rule.getRowCount();i++){
						input=getString2TokenArray(rule.getFieldValue("input",i),"@");
						actionsql=(dbtype.equalsIgnoreCase("oracle")?rule.getFieldValue("oraclequery",i):rule.getFieldValue("mssqlquery",i));
						if(input!=null && input.length>0 &&actionsql.toLowerCase().indexOf("insert")<0
								&&actionsql.toLowerCase().indexOf("update")<0&&actionsql.toLowerCase().indexOf("delete")<0){
							for(int j=0; j<input.length;j++){
								if(input[j]!=null &&!input[j].equals("")){
									//String tmpval=result[i-1].getFieldValue(input[j].trim(),0);
									inputval=(input[j]!=null && input[j].equalsIgnoreCase("objid")?objid:"'"+((i-1)>=0&&result[i-1]!=null &&result[i-1].getRowCount()>0?result[i-1].getFieldValue(input[j],0)+"'":
										(input[j].indexOf("=")>0?input[j].substring(input[j].indexOf("="))+"'":"'")));
								}
								if(inputval!=null&& !inputval.equals(""))
									actionsql=replaceSqlParamValue(actionsql,"@"+input[j],inputval);
							}
							if(actionsql!=null &&!actionsql.equals(""))
								result[i]=(actionsql.toLowerCase().indexOf("select")>=0?getResultSet(replaceStringWith(actionsql,"\\","")):null);

						}
						if(input!=null && input.length>0 &&(actionsql.toLowerCase().indexOf("insert")>=0
								||actionsql.toLowerCase().indexOf("update")>=0||actionsql.toLowerCase().indexOf("delete")>=0)){
							if((i-1)>=0 && result[i-1]!=null &&result[i-1].getRowCount()>0)
								for(int k=0;k<result[i-1].getRowCount();k++){
									for(int j=0; j<input.length;j++){
										if(input[j]!=null &&!input[j].equals("")){
											String tmpval=result[i-1].getFieldValue(input[j].trim(),k);
											inputval=(input[j]!=null && input[j].equalsIgnoreCase("objid")?(actionsql.toLowerCase().indexOf("insert")>=0?
													tq.getPrimaryKey():result[i-1].getFieldValue(input[j].trim(),k))
													:"'"+((i-1)>=0&&result[i-1]!=null &&result[i-1].getRowCount()>0?result[i-1].getFieldValue(input[j].trim(),k)+"'":
														(input[j].indexOf("=")>0?input[j].substring(input[j].indexOf("="))+"'":"'")));
										}
										if(input[j].toLowerCase().indexOf(("2"+objectname).toLowerCase().trim())>0)
											inputval=objid;
										if(inputval!=null&& !inputval.equals(""))
											actionsql=replaceSqlParamValue(actionsql.trim(),("@"+input[j]).trim(),inputval.trim());
										//System.out.print("\n"+actionsql);
									}
									executeQuery(replaceStringWith(actionsql,"\\",""));
									actionsql=(dbtype.equalsIgnoreCase("oracle")?rule.getFieldValue("oraclequery",i):rule.getFieldValue("mssqlquery",i));
								}
							if(actionsql!=null &&!actionsql.equals(""))
								result[i]=null;
						}

					}
				}
			}
			return(true);
		} catch(Exception e) {
			e.printStackTrace();
			return(false);
		}
	}

	/**
	 * This method will fire event against each row based on certain condition
	 * The event can be fired before or after the reason (Insert, Update, Delete)
	 * for a specific database object based on the state of event
	 * Parameter:
	 * objectname=name of the table for which this list is required
	 * reason=type of the query like insert, update
	 * state=before or after the query is actually executed
	 * statusvalue=the current value of the status filed passed from the object
	 * objid=the objid of the current object for which this action is applicable
	 * return=this method will return the rule action sql
	 *
	 * Note: this method will work with the folowing constraints
	 * 1)The first input parameter should be @objid of the current object. If any other input parameter is assocaited with the 1st step
	 * the parameter should be assictaed with their values and there should not be any other seperatorbetween 2 consecutive parameters
	 * 2)All the query should have associated with their input parameter strating with @<paramname>
	 * 3)If there is a insert the previous query should fetch all the the associated parameter value using select state.
	 * This routine will insert or update the no of record selected in the previous select statement
	 * 4) in the insert or update query all the field which needs to be inserted or update should be listed in the input parameter
	 * and also in the values clause, If this is mis
	 * 5) While updating make sure that the previous select query brings only one record set if it is more than 1
	 * than only the first recordset values will be applied for the update
	 *
	 * Limitation: You can use a Stored procedure to update or delete record.
	 * But if the stored procedure  have any "Insert" than all the objid should be (-ve) because we can not generate objid in SP
	 * You should attach a rule to update those -ve objid after executing those stored procedure
	 * The steps are 1) Execute The SP
	 *               2) Select those records which has -ve objid with name
	 *               3) Update Objid with a update statement in next rule
	 * We use getPrimaryKey() method based on the partition object principle
	 * */
	public boolean applyObjectRule(String objectname,int reason, int state,TemplateTable tabledata){
		String dbtype=DatabaseTransaction.getDbType();
		String actionsql="";
		String sql="";
		boolean isobjidinput=true;
		String input[];
		String inputdatatype[];
		String inputval="";
		TemplateTable result[];
		TemplateQuery tq=new TemplateQuery();
		//Apply the license rule here
		//InstallLicense lm=new InstallLicense();
		//lm.applyLicenseRule(objectname,dbtype,tabledata);
		//First verify whether any object rule associated with the current tablename
		ServiceObject so =new ServiceObject();
		try{
			if(!so.verifyObjectRule(objectname,reason,state))
				return(false);
			//then verify if any condition is valid against each row set
			//for which this rule is getting fired
			String consql="select *from table_objectrule where "+ (dbtype.equalsIgnoreCase("Oracle")? "upper(":"upper(")+ "tablename)=" +(dbtype.equalsIgnoreCase("Oracle")? "upper('":"upper('")+objectname+"')"
					+ " and reason="+reason + " and actionstate="+state + " order by ruleindex";
			TemplateTable objectrule=getResultSet(consql);
			for(int r=1;r<tabledata.getRowCount();r++){
				//validate condition for each rowdata of the table
				// This may return an array of ObjectRule.objid which needs to be fiered against
				// this current row data which is validated against ObjectRule.Condition
				String ruleobjid=validateCondition(objectrule,tabledata,r);
				//Once the rule objid is found look for the actionquery against each ObjectRule.ObjId
				//Order by all actionquery against objid and stepno
				if(!ruleobjid.equals(""))
					sql="select tr.Name \"ActionName\",tr.TableName,tr.effectedtable,tr.Description,tr.reason,tr.ActionState,"+
							" tr.RuleIndex,tr.Status \"ActionStatus\","+
							" aq.tablename \"ActionTable\" ,aq.stepno, aq.input, aq.InputDataType,aq.output,aq.querytype,aq.hasrecordset,aq.oraclequery,aq.mssqlquery"+
							" from table_objectrule tr,table_actionquery aq "+
							" where upper(tr.tablename)=upper('"+ objectname +"') and tr.reason='"+reason+"' and tr.ActionState='"+state+"'"+
							" and tr.objid=aq.actionquery2objectrule and tr.objid in('"+ruleobjid+"') order by tr.ruleindex,tr.objid,aq.stepno";


				if(!sql.equals("")){
					//synchronized(applyobjectrule){
						String objid="'"+tabledata.getFieldValue("objid",r)+"'";
						TemplateTable rule=(!sql.equals("")?getResultSet(sql):null);
						if(rule!=null &&rule.getRowCount()>0){
							result=new TemplateTable[rule.getRowCount()];
							for(int i=0;i<rule.getRowCount();i++){
								input=getString2TokenArray(rule.getFieldValue("input",i),"@");
								inputdatatype=getString2TokenArray(rule.getFieldValue("inputdatatype",i),"@");
								actionsql=(dbtype.equalsIgnoreCase("oracle")?rule.getFieldValue("oraclequery",i):rule.getFieldValue("mssqlquery",i));
								if(actionsql==null||actionsql.equalsIgnoreCase("none")||actionsql.equals(""))
									continue;
								//logger.info("\n Action Query="+actionsql);
								if(input!=null && input.length>0 &&actionsql.toLowerCase().indexOf("insert")<0&&actionsql.toLowerCase().indexOf("exec")<0
										&&actionsql.toLowerCase().indexOf("update")<0&&actionsql.toLowerCase().indexOf("delete")<0){
									for(int j=0; j<input.length;j++){

										if(input[j]!=null &&!input[j].equals("")){
											//String tmpval=result[i-1].getFieldValue(input[j].trim(),0);
											inputval=(input[j]!=null && input[j].equalsIgnoreCase("objid")?objid:"'"+((i-1)>=0&&result[i-1]!=null &&result[i-1].getRowCount()>0?result[i-1].getFieldValue(input[j],0)+"'":
												(input[j].indexOf("=")>0?input[j].substring(input[j].indexOf("="))+"'":"'")));
											//Add any missing input from the current tabledata
											if(inputval==null||inputval.equals("''"))
												inputval="'"+tabledata.getFieldValue(input[j],r)+"'";

										}

										if(inputval!=null&& !inputval.equals(""))
											actionsql=replaceSqlParamValue(actionsql,"@"+input[j],inputval);
									}
									//logger.info("\n EXEC Action Query="+actionsql);
									//logger.info("\n Inputvalue="+objid);
									if(actionsql!=null &&!actionsql.equals(""))
										result[i]=(actionsql.toLowerCase().indexOf("select")>=0?getResultSet(replaceStringWith(actionsql,"\\","")):null);
									//if(result[i]==null&&actionsql.toLowerCase().indexOf("exec")>=0)
									//  executeQuery(replaceStringWith(actionsql,"\\",""));

								}else if(actionsql.toLowerCase().indexOf("exec")>=0&&actionsql.toLowerCase().indexOf("exec")<6&&input[0].equalsIgnoreCase("objid")){
									actionsql=replaceSqlParamValue(actionsql,"@"+input[0],objid);
									actionsql="begin\n "+actionsql.trim().substring(4)+";\nend;";
									//logger.info("\n EXEC Action Query="+actionsql);
									executeQuery(replaceStringWith(actionsql,"\\",""));
								}
								if(input!=null && input.length>0 &&(actionsql.toLowerCase().indexOf("insert")>=0
										||actionsql.toLowerCase().indexOf("exec")>=0 &&actionsql.toLowerCase().indexOf("exec")<6
										||actionsql.toLowerCase().indexOf("update")>=0||actionsql.toLowerCase().indexOf("delete")>=0)){
									if((i-1)>=0 && result[i-1]!=null &&result[i-1].getRowCount()>0)
										//if all input parameter are not objid do the following

										for(int k=0;k<result[i-1].getRowCount();k++){
											for(int j=0; j<input.length;j++){
												if(input[j]!=null &&!input[j].equals("")){
													String tmpval=result[i-1].getFieldValue(input[j].trim(),k);
													//Verify if the objid of the current result having (-ve) no
													// If the current actionsql is a update statement and objid is (-ve) that means
													// You need to set the objid for those records which are created externally using stored proc
													if(actionsql.toLowerCase().indexOf("update")>=0 &&input[j].equalsIgnoreCase("objid")
															&&tmpval!=null&&!tmpval.equals("")&&Integer.parseInt(tmpval)<0)
														inputval=tq.getPrimaryKey();
													//check if the tmpval is blank then the current objid should be a input to the update statement
													else if(actionsql.toLowerCase().indexOf("update")>=0 &&input[j].equalsIgnoreCase("objid")
															&&tmpval!=null&&tmpval.equals(""))
														inputval=objid;
													else
														inputval=(input[j]!=null && input[j].equalsIgnoreCase("objid")?(actionsql.toLowerCase().indexOf("insert")>=0?
																tq.getPrimaryKey():result[i-1].getFieldValue(input[j].trim(),k))
																:"'"+((i-1)>=0&&result[i-1]!=null &&result[i-1].getRowCount()>0?result[i-1].getFieldValue(input[j].trim(),k)+"'":
																	(input[j].indexOf("=")>0?input[j].substring(input[j].indexOf("="))+"'":"'")));
												}
												String datatype=(inputdatatype!=null &&inputdatatype.length>=j?inputdatatype[j]:"VARCHAR");
												if(input[j].toLowerCase().indexOf(("2"+objectname).toLowerCase().trim())>0)
													inputval=objid;
												if(inputval!=null&& !inputval.equals(""))
													actionsql=replaceSqlParamValue(dbtype,actionsql.trim(),("@"+input[j]).trim(),inputval.trim(),datatype.trim());
												//System.out.print("\n Action Sql="+actionsql);
											}
											if(dbtype!=null&&dbtype.equalsIgnoreCase("Oracle")&&actionsql.indexOf("exec")>=0 &&actionsql.indexOf("exec")<6){
												actionsql="begin\n "+actionsql.trim().substring(4)+";\nend;";
											}
											//System.out.print("\n EXEC Length="+actionsql.indexOf("exec"));
											executeQuery(replaceStringWith(actionsql,"\\",""));
											actionsql=(dbtype.equalsIgnoreCase("oracle")?rule.getFieldValue("oraclequery",i):rule.getFieldValue("mssqlquery",i));
										}

									if(actionsql.indexOf("update")>=0){
										//If all the input parameters are objid and query is update
										isobjidinput=true;
										for(int j=0; j<input.length;j++)
											if(input[j]!=null &&!input[j].equals("")&&input[j].equalsIgnoreCase("objid"))
												actionsql=replaceSqlParamValue(actionsql,"@"+input[j],objid);
											else
												isobjidinput=false;
										if(isobjidinput){
											String update_sql=replaceStringWith(actionsql,"\\","");
											if(ACONST.GENERATE_LOG)
												logger.info(update_sql);
											executeQuery(update_sql);
										}
										actionsql=(dbtype.equalsIgnoreCase("oracle")?rule.getFieldValue("oraclequery",i):rule.getFieldValue("mssqlquery",i));
									}
									if(actionsql!=null &&!actionsql.equals(""))
										result[i]=null;
								}

							}
						}
					//}

				}
			}

			return(true);
		} catch(Exception e) {
			e.printStackTrace();
			return(false);
		}
	}

	/**
	 * This method validate the condition against the current rowset with
	 * all the object rules associated with this table
	 * Each condition is seperated by ";" in the condition string
	 * The condition format will be <fieldname>$<operator>$<value>
	 * $ is the seperator between fieldname, operator and value
	 * The operator should be written in this format
	 * ">"= gt
	 * "<"= lt
	 * ">="= gteq
	 * "<="= lteq
	 * "=" = eq
	 *return: this method returns all the ruleobjects objid seperated with ","
	 *        which are valid for this condition and needs to be executed
	 *
	 * Limitation: The current limitation of this method is that it validates all condition like AND
	 *             We need to put the OR condition also
	 */
	private String validateCondition(TemplateTable objectrule, TemplateTable tabledata, int r){
		String ruleobjid="";
		boolean validate=false;
		for(int i=0; i<objectrule.getRowCount(); i++){
			String[] condition=getString2TokenArray(objectrule.getFieldValue("Condition",i),";");
			if(condition!=null&&condition.length>0)
				for(int k=0;k<condition.length;k++){
					String[] conditionarr=getString2TokenArray(condition[k],"$");
					if(conditionarr!=null&&conditionarr.length==3){

						try {
							validate=false;
							double conditionval=Double.parseDouble(conditionarr[2]);
							double fieldval=conditionarr[0]!=null&&!conditionarr[0].equals("")?Double.parseDouble(tabledata.getFieldValue(conditionarr[0],r)):0;
							//compare value here
							if(conditionarr[1]!=null &&conditionarr[1].equalsIgnoreCase("gt") &&fieldval>conditionval)
								validate=true;
							if(conditionarr[1]!=null &&conditionarr[1].equalsIgnoreCase("lt") &&fieldval<conditionval)
								validate=true;
							if(conditionarr[1]!=null &&conditionarr[1].equalsIgnoreCase("gteq") &&fieldval>=conditionval)
								validate=true;
							if(conditionarr[1]!=null &&conditionarr[1].equalsIgnoreCase("lteq") &&fieldval<=conditionval)
								validate=true;
							if(conditionarr[1]!=null &&conditionarr[1].equalsIgnoreCase("eq") &&fieldval==conditionval)
								validate=true;
							if(conditionarr[1]!=null &&conditionarr[1].equalsIgnoreCase("nteq") &&fieldval!=conditionval)
								validate=true;
						}catch(NumberFormatException ne){
							validate=false;
							String fieldval=(conditionarr[0]!=null&&!conditionarr[0].equals("")?tabledata.getFieldValue(conditionarr[0],r):"");
							if(!this.isEmptyValue(conditionarr[1]) && conditionarr[0].equalsIgnoreCase("objid")&&fieldval.length()>=32)
								validate=true;
							if(conditionarr[2]!=null&&conditionarr[2].equals(fieldval))
								validate=true;
							//check not null and not eqauls
							if(conditionarr[2]!=null&&conditionarr[1]!=null
									&&conditionarr[1].equalsIgnoreCase("nteq")&&!conditionarr[2].equals(fieldval))
								validate=true;
							//check null value and not equal
							fieldval=(fieldval!=null &&fieldval.equals("")?null:fieldval);
							if(conditionarr[2]!=null&&conditionarr[2].equalsIgnoreCase("null")&&fieldval==null)
								validate=true;
							if(conditionarr[2]!=null&&conditionarr[2].equalsIgnoreCase("null")
									&&conditionarr[1]!=null&&conditionarr[1].equalsIgnoreCase("nteq")&&fieldval!=null)
								validate=true;
						}
					}

				}
			if(validate)
				ruleobjid=ruleobjid+(ruleobjid.equals("")?objectrule.getFieldValue("objid",i):"','"+objectrule.getFieldValue("objid",i));
		}
		return(ruleobjid);
	}

	/**
	 *  unicode to big5 or any charset while input from client to dbserver
	 *  pass proper charecter setting like BIG5, UTF-8 etc
	 *  **/
	public String convertCharsetClient2db(String str2convert,String charset) throws IOException {
		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		Writer out=new OutputStreamWriter(stream, charset);
		out.write(str2convert);
		out.close();
		return stream.toString();
	}


	/**
	 * big5 or any database charecter setting to unicode conversion while reading
	 * data from database, pass proper charecter setting like BIG5, UTF-8 etc
	 * */
	public String convertCharsetDb2client(String str2convert,String charset)throws IOException {

		StringBuffer buffer = new StringBuffer();
		if (str2convert!=null ){
			byte[] targetBytes = str2convert.getBytes();

			ByteArrayInputStream stream = new ByteArrayInputStream(targetBytes);
			InputStreamReader isr=new InputStreamReader(stream, charset);
			Reader in = new BufferedReader(isr);
			int chInt;
			while ( (chInt = in.read()) > -1 ) {
				buffer.append((char)chInt);
			}
			in.close();
		}
		return buffer.toString();
	}

	/**
	 * This method will create MTM records for those table which has MTM relation
	 * Input:
	 * dbobject=name of the dbobject for which this MTM relation is applicable
	 * relationame=name of the relation which should be verified for MTM
	 * tabledata=the data for the current dbobject
	 * parentid= Objid of the current parent object associated with this dbobject
	 * dbtype=database type either oracle or mssql
	 *
	 */
	/*
         Example1: With parent objectid
         Sql 1=select bidinfo.objid bidinfoid,bidinvitation.objid bidinvitationid
  from table_bidinfo bidinfo,table_bidinvitation bidinvitation,table_bidrequest bidrequest
  where bidrequest.objid=1110091 and bidinvitation.bidinvitation2bidrequest=bidinfo.bidinfo2bidrequest
  and bidrequest.objid=bidinfo.bidinfo2bidrequest and not exists(
  select *from bidinfo2bidinvitation mtm
  where nvl(mtm.bidinfoid,0)!=bidinfo.objid
  and nvl(mtm.bidinvitationid,0)!=bidinvitation.objid)

  Example2 with child objectid:
  select bidinfo.objid bidinfoid,bidinvitation.objid bidinvitationid
from table_bidinfo bidinfo,table_bidinvitation bidinvitation,table_bidrequest bidrequest
 where bidinvitation.objid=1110172 and bidinvitation.bidinvitation2bidrequest=bidinfo.bidinfo2bidrequest and bidrequest.objid=bidinfo.bidinfo2bidrequest and not exists(
select *from bidinfo2bidinvitation mtm
where nvl(mtm.bidinfoid,0)!=bidinfo.objid
and nvl(mtm.bidinvitationid,0)!=bidinvitation.objid)
	 */
	public boolean applyMTMRelation(String dbobject,String mainobject,String mainobjid){
		//First verify if any MTM relation exists with this dbobject name then get the relation name and relation path
		String dbtype=DatabaseTransaction.getDbType();
		String relname="";
		String relpath="";
		String leftobject="";
		String rightobject="";
		boolean ismain=(dbobject!=null&&mainobject!=null&&dbobject.equalsIgnoreCase(mainobject)?true:false);
		boolean isparent=false;
		ServiceObject so=new ServiceObject();
		TemplateTable relation=so.getMTMRelation();
		//logger.info("\n>>>>>>>>>Applying MTM for "+dbobject);
		try{
			for (int i=0;i<relation.getRowCount();i++){
				String tmprelname=relation.getFieldValue("relationname",i).toLowerCase();
				//logger.info("\n>>>>>>>>>Table Name "+tmprelname);
				if(tmprelname!=null &&(tmprelname.indexOf(dbobject.toLowerCase()+"2")>=0||tmprelname.indexOf("2"+dbobject.toLowerCase())>=0)
						&&relation.getFieldValue("relationtype",i).equalsIgnoreCase("MTM")){
					relname=tmprelname;
					//logger.info("\n>>>>>>>>>Relation Name "+relname);
					relpath=relation.getFieldValue("DefaultFilter",i);

					//verify if this dbobject is a child object as per relation path
					if(relname!=null &&!relname.equals("")&&relpath!=null &&relpath.indexOf("@")>0){

						int relpathlen=relpath!=null&&relpath.length()>0 &&relpath.indexOf("@")>=0?relpath.indexOf("@"):0;
						String relationobject=relpathlen>0?relpath.substring(0,relpathlen):"";
						String objectlist[]=this.getString2TokenArray(relationobject,":");
						String relationpath=relpathlen>0&&relpath.length()>relpathlen+1? relpath.substring(relpathlen+1):"";
						isparent=tmprelname.indexOf(dbobject.toLowerCase()+"2")>=0?true:false;
						String table="";
						String addfilter="";
						boolean hasmainobject=false;
						//make table list which will be used for sql
						if(objectlist!=null&&objectlist.length>0)
							for(int k=0;k<objectlist.length;k++){
								table+=(k==0?"table_"+objectlist[k].toLowerCase()+" "+objectlist[k].toLowerCase() :",table_"+objectlist[k].toLowerCase()+" "+objectlist[k].toLowerCase());
								if(!hasmainobject)
									hasmainobject=mainobject!=null&&!mainobject.equals("")&& objectlist[k].toLowerCase().equalsIgnoreCase(mainobject)?true:false;
							}
						//if main object does not exist in the relation object list then add it to the table
						if(!hasmainobject&&mainobject!=null&&!mainobject.equals("")){
							table+=",table_"+mainobject.toLowerCase()+" "+mainobject.toLowerCase();

						}
						addfilter=ismain&&mainobject!=null&&!mainobject.equals("")?mainobject.toLowerCase()+".objid='"+mainobjid+"'":dbobject.toLowerCase()+"."+dbobject.toLowerCase()+"2"+mainobject.toLowerCase()+"='"+mainobjid+"'"+
								" and "+mainobject.toLowerCase()+".objid="+dbobject.toLowerCase()+"."+dbobject.toLowerCase()+"2"+mainobject.toLowerCase();
						int len2=relname.indexOf("2");
						String parent=relname.substring(0,len2);
						//logger.info("\n>>>>>>>>>Parent MTM Object "+parent);
						String child=relname.substring(len2+1);

						String objid=mainobjid;
						String joinsign=dbtype.equalsIgnoreCase("oracle")?"||":"+";
						//logger.info("\n>>>>>>>>>MTM Ref Objid= "+objid);
						String sql="insert into "+relname+ " select  distinct "+parent+".objid "+","+child+".objid from "+table+" where "+addfilter+" and "+relationpath +
								" and  "+parent+".objid"+joinsign+child+".objid not in (select "+parent+"id"+joinsign+child+"id from "+relname+")";

						//logger.info("\n>>>>>>>>>MTM Sql= "+sql);
						executeQuery(sql);

					}

				}
			}

			//If child object as per relation path then more than one record will be
			return(true);
		} catch(Exception e) {
			e.printStackTrace();
			return(false);
		}
	}
	public boolean applyMTMRelation(String dbobject,TemplateTable tabledata){
		//First verify if any MTM relation exists with this dbobject name then get the relation name and relation path
		String dbtype=DatabaseTransaction.getDbType();
		String relname="";
		//String relpath="";
		String leftobject="";
		String rightobject="";
		boolean isparent=false;
		ServiceObject so=new ServiceObject();
		TemplateTable relation=so.getMTMRelation();
		//logger.info("\n>>>>>>>>>Applying MTM for "+dbobject);
		try{
			for (int i=0;i<relation.getRowCount();i++){
				String tmprelname=relation.getFieldValue("relationname",i).toLowerCase();
				if(tmprelname!=null &&(tmprelname.indexOf(dbobject.toLowerCase()+"2")>=0||tmprelname.indexOf("2"+dbobject.toLowerCase())>=0)
						&&relation.getFieldValue("relationtype",i).equalsIgnoreCase("MTM")){
					relname=tmprelname;
					//logger.info("\n>>>>>>>>>Relation Name "+relname);
					//relpath=relation.getFieldValue("DefaultFilter",i);
					isparent=tmprelname.indexOf(dbobject.toLowerCase()+"2")>=0?true:false;
					//verify if this dbobject is a child object as per relation path
					if(relname!=null &&!relname.equals("")){
						int len2=relname.indexOf("2");
						String parent=relname.substring(0,len2);
						//logger.info("\n>>>>>>>>>Parent MTM Object "+parent);
						String child=relname.substring(len2+1);
						for(int j=1;j<tabledata.getRowCount();j++){
							String objid="'"+tabledata.getFieldValue("objid",j)+"'";
							//logger.info("\n>>>>>>>>>MTM Ref Objid= "+objid);
							String sql="insert into "+relname+ " select "+parent+".objid "+(dbtype.equalsIgnoreCase("oracle")?parent+"id":"'"+parent+"id'")+","+child+".objid "+(dbtype.equalsIgnoreCase("oracle")?child+"id":"'"+child+"id'")
									+" from table_"+parent+" "+(dbtype.equalsIgnoreCase("oracle")?parent:"'"+parent+"'") +",table_"+child+" "+(dbtype.equalsIgnoreCase("oracle")?child:"'"+child+"'")+","+relname+
									(dbtype.equalsIgnoreCase("oracle")?" mtm ":" 'mtm' ")+"where mtm."+parent+"id!="+parent+".objid and mtm."+child+"id!="+child+".objid and "+(isparent? parent+".objid=":child+".objid=")+objid;
							//logger.info("\n>>>>>>>>>MTM Sql= "+sql);
							executeQuery(sql);
						}
					}

				}
			}

			//If child object as per relation path then more than one record will be
			return(true);

		} catch(Exception e) {
			e.printStackTrace();
			return(false);
		}
	}
	/**
	 * This method will populate data to WhatToDo object based on the authorized group
	 * A Authorized group is set of users who are autorized to handle any issues related to a particular job
	 * based on the project code, main code, sub code and project no.
	 * The object will flow from one message queue to another based on the user index available in authorized group
	 * To maintain a object flow continuous the user can preset the maximum time to waiting in a queue
	 * and the object should move to another queue automatically with proper rule.
	 */

	public boolean applyAuthGroup(String dbobject,TemplateTable tabledata){

		return(true);
	}
	/*This method is created by Brinda
        It will create or update console record based on
        any table to messagequeue relation
        @ param String tabale_name
        @ param int table2messagequeue
        @ param boolean hasRelationm
	 */
	public void createConsole(String table, String objid){
		boolean hasRelation=false;
		String sqlString=null;
		TemplateTable tcount;
		try{
			sqlString="SELECT objid keyobjid, name,title,projectcode, " + table+"2messagequeue mqid " + "FROM table_"+table
					+" WHERE OBJID = '"+ objid + "' and not exists(select objid from"+
					" table_console where keyObjid='"+objid+ "' )" ;
			//logger.info("the sqlString="+sqlString);
			tcount=this.getResultSet(sqlString);
			if(tcount.getRowCount()>0)
				hasRelation=true;
			if(hasRelation){
				//logger.info("1st column="+tcount.getFieldValue("keyobjid",0));
				sqlString="insert into table_console values('"+objid+"','"+tcount.getFieldValue("name",0)+"','"
						+tcount.getFieldValue("title",0)+"','"+tcount.getFieldValue("projectcode",0)+"',"+
						"'time',"+"1,"+objid+","+0+","+tcount.getFieldValue("mqid",0)+")";
				executeQuery(sqlString);
				//logger.info("the insertquery="+sqlString);

			}
		}
		catch(Exception e){
			e.printStackTrace();
		}


	}

	/*this method is done by Brinda
   This will create or update console record
   @param objectname
   @param reason
   @param state
   @param tabledata
   @param dbtype
	 */
	public boolean applyConsoleObject(String objectname,TemplateTable tabledata,String username,String groupuser,boolean isParent){
		String dbtype=DatabaseTransaction.getDbType();
		String mqId="";
		String keyobjid="";
		TemplateTable consoledata;
		TemplateTable console2messagequeue;
		String sql="select *from table_console where upper(name)='"+objectname.toUpperCase()+"' and keyobjid=";
		//logger.info("the sql in applyConsole=" + sql);
		String updatesql="";
		String insertsql="";
		String deletesql="";
		TemplateQuery tq=new TemplateQuery();
		//if the Object is WhatToDo then do nothing
		if(objectname.equalsIgnoreCase("WhatToDo"))
			return(false);
		//verify if there is a relation to messagequeue
		try{

			for (int i=1;i<tabledata.getRowCount();i++){

				mqId=tabledata.getFieldValue(objectname+"2messagequeue",i);
				keyobjid="'"+tabledata.getFieldValue("objid",i)+"'";
				TemplateTable mqiddata=getResultSet("select "+objectname+"2messagequeue mqid from table_"+objectname +" where objid="+keyobjid);
				mqId=mqiddata!=null?mqiddata.getFieldValue("mqid",mqiddata.getRowCount()-1):mqId;
				mqId=(mqId==null||mqId.equals(""))?"0":mqId;
				consoledata=getResultSet(sql+keyobjid);
				//System.out.print("\n consoledata.getRowCount()="+consoledata.getRowCount());
				if(consoledata.getRowCount()>0 && isParent){
					//if status=completed then delete console record
					//Completed=4 , declare a constant
					if(tabledata.getFieldValue("status",i).equals(ApplicationConstants.STATUS_UNASSIGN)||
							tabledata.getFieldValue("status",i).equals(ApplicationConstants.STATUS_UNDECIDED)||
							tabledata.getFieldValue("status",i).equals(ApplicationConstants.STATUS_CLOSED)){
						//delete console record
						deletesql = "delete table_console where status in('"+
								ApplicationConstants.STATUS_UNASSIGN+"','"+ApplicationConstants.STATUS_UNDECIDED+"','"+ApplicationConstants.STATUS_CLOSED+"')"; ;
								executeQuery(deletesql);
								//delete console2messagequeue table record
								executeQuery("delete console2messagequeue where consoleid='"
										+consoledata.getFieldValue("objid",consoledata.getRowCount()-1)+"'");
								//executeQuery("update table_"+objectname+" set "+objectname+"2messagequeue=0 where objid="+keyobjid);
					}else{
						updatesql= "update table_console set mqid='"+tabledata.getFieldValue(objectname+"2messagequeue",i)+
								"',title='"+(tabledata.getFieldValue("title",i).equals("")||tabledata.getFieldValue("title",i)==null?tabledata.getFieldValue("name",i):tabledata.getFieldValue("title",i))+
								"',status='"+tabledata.getFieldValue("status",i)+"',elapseday="+
								(dbtype.equalsIgnoreCase("Oracle")? "to_date(sysdate,'dd/mm/yyyy')-to_date(entrydate,'dd/mm/yyyy')":"entrydate-getdate()")+
								",moduser='"+username+"',moddate="+(dbtype.equalsIgnoreCase("Oracle")? "sysdate":"getdate()")+
								" where upper(name)='"+objectname.toUpperCase()+"' and keyobjid="+keyobjid;
						//logger.info("the updatesql="+updatesql);
						executeQuery(updatesql);
						console2messagequeue=getResultSet("select consoleid from console2messagequeue where consoleid='"+
								consoledata.getFieldValue("objid",consoledata.getRowCount()-1) + "' and messagequeueid='"+mqId+"'");
						if(console2messagequeue.getRowCount()==1){
							String update_con2msg="update console2messagequeue set messagequeueid='"+mqId+
									"' where consoleid='"+consoledata.getFieldValue("objid",consoledata.getRowCount()-1)+"'";

							executeQuery(update_con2msg);
							// if no console2messagequeue insert one
						}else if(console2messagequeue.getRowCount()==0 &&!mqId.equals("")&&!mqId.equals("0")){
							String con2msg="insert into console2messagequeue(consoleid,messagequeueid)values('"+
									consoledata.getFieldValue("objid",consoledata.getRowCount()-1)+"','"+mqId+"')";
							executeQuery(con2msg);
						}

						//If the status=PUBLISH_TO_GROUP insert all the messagequeueid
						//of the teammember associated with same maincode and subcode
						//>>>>>>>Future Enhancement
					}
				}else if (consoledata.getRowCount()==0){
					String objid =tq.getPrimaryKey();

					insertsql="insert into table_console(OBJID,NAME,TITLE,DESCRIPTION,STATUS,KEYOBJID,ELAPSEDAY,ENTRYDATE,"+
							"mqid,ORIGINID,DESTINITIONID,GROUPUSER,GENUSER,GENDATE,MODUSER,MODDATE) values("
							+objid+",'"+objectname+"','"+((tabledata.getFieldValue("title",i).equals("")||tabledata.getFieldValue("title",i)==null)?tabledata.getFieldValue("name",i):tabledata.getFieldValue("title",i))+" ','"+
							tabledata.getFieldValue("name",i)+"','"+
							tabledata.getFieldValue("status",i)+" ','"+
							tabledata.getFieldValue("objid",i)+"',"+(tabledata.getFieldValue("ELAPSEDAY",i).equals("")?"0":tabledata.getFieldValue("ELAPSEDAY",i))+","+
							(dbtype.equalsIgnoreCase("Oracle")? "sysdate":"getdate()")+",'"+
							(mqId.equals("")?"0":mqId)+"','0','0','"+
							groupuser+"','"+username+"',"+(dbtype.equalsIgnoreCase("Oracle")? "sysdate":"getdate()")+",null,null)";

					executeQuery(insertsql);
					//Keep the console.objid in the current table destinitionId
					//executeQuery( "update table_"+objectname +" set destinitionid="+objid +" where objid="+ tabledata.getFieldValue("objid",i));

					//insert into Console2Messagequeue
					String con2msg="insert into console2messagequeue(consoleid,messagequeueid)values("+objid+",'"+mqId+"')";
					//System.out.print("mqid="+mqId);
					if(!mqId.equals("")&&!mqId.equals("0"))
						executeQuery(con2msg);

				}

			}
			//If relation exists verify if there is a record exists
			//with objectname and keyobjid in the console table
			//if record exists then update console table
			//else insert a console record if <object>2messagequeue >0
			//if status=completed delete record from console table and messagequeue2console
			//table with KeyObjid and Object name
			//if tabledata.status=notify_group then create record in messagequeue2console for all group memeber

			return(true);

		} catch(Exception e) {
			e.printStackTrace();
			return(false);
		}

	}

	public boolean updatePayment(String [] custom){
		//need validation for the package
		String companyId=""; 
		String amt="";
		String user="";
		String status="";

		if(custom.length==6){		   
			companyId=custom[0]; 
			amt=custom[1];
			user=custom[3];
			status="";

		}
		String sql= "select s.objid billing2subscription,sc.groupuser,sc.loginname,"+
				" 'Service charge for '||sc.name ||', Package='||sc.packagename as description"+
				" ,'Fee charged for '||sc.name || 'on '||to_char(sysdate, 'mm/dd/yyyy') as name," +
				" to_number(to_char(sysdate,'mm')) as monthcode"+
				" from table_servicecharge sc,table_subscription s "+
				" where sc.companyid="+ companyId +" and sc.loginname=s.groupuser "+
				" and not exists (select *from table_billing b where b.billing2subscription=s.objid "+
				" and b.originid="+ companyId + " and gendate= sysdate)";
		logger.info(sql);
		TemplateTable res= this.getResultSet(sql);

		if(res.getRowCount()>0){

			String insert_sql="insert into table_billing(OBJID,NAME,DESCRIPTION,MONTHCODE,BILLDATE,"+
					"AMOUNT,STATUS,BILLING2SUBSCRIPTION,ORIGINID,DESTINITIONID,GROUPUSER,GENUSER,GENDATE,MODUSER,MODDATE)values("+
					"billing_seq.nextval,'"+res.getFieldValue("name", res.getRowCount()-1)+"','"+
					res.getFieldValue("description", res.getRowCount()-1)+"','"+
					res.getFieldValue("monthcode", res.getRowCount()-1)+"',sysdate,"+
					amt+",'Paid',"+res.getFieldValue("billing2subscription", res.getRowCount()-1)+","+
					companyId+","+companyId+",'"+res.getFieldValue("groupuser", res.getRowCount()-1)+"','"+
					res.getFieldValue("loginname", res.getRowCount()-1)+"',sysdate,null,sysdate)";

			logger.info(insert_sql);

			this.executeQuery(insert_sql); 
		}

		logger.info("Company Id="+companyId +", user="+user+", amt="+amt);

		return(true);
	}
	public boolean addSubscription(String login,String itemnum, String itemname){
		//need validation for the package
		String substype="";
		String subscribedays="";
		String subscribecode="";  //1=monthly, 2=yearly
		String privilegegroup="";
		if (itemnum.equalsIgnoreCase("MonthlyBid")){
			substype="1";
			subscribedays="33";
			subscribecode="1";
			privilegegroup="Bidding";
		}else if(itemnum.equalsIgnoreCase("MonthlyQuote")){
			substype="3";
			subscribedays="33";
			subscribecode="1";
			privilegegroup="Quote";
		}else if(itemnum.equalsIgnoreCase("MonthlyBidSubmit")){
			substype="3";
			subscribedays="33";
			subscribecode="1";
			privilegegroup="Bid Plus Own";
		}else if(itemnum.equalsIgnoreCase("MonthlyERPNoBid")){
			substype="4";
			subscribedays="33";
			subscribecode="1";
			privilegegroup="ERP No Bidding";
		}else if(itemnum.equalsIgnoreCase("MonthlyERPWithBid")){
			substype="5";
			subscribedays="33";
			subscribecode="1";
			privilegegroup="ERP With Bidding";
		}else if (itemnum.equalsIgnoreCase("YearlyBid")){
			substype="1";
			subscribedays="367";
			subscribecode="2";
			privilegegroup="Bidding";
		}else if (itemnum.equalsIgnoreCase("YearlyQuote")){
			substype="1";
			subscribedays="367";
			subscribecode="2";
			privilegegroup="Quote";
		}else if(itemnum.equalsIgnoreCase("YearlyBidSubmit")){
			substype="3";
			subscribedays="367";
			subscribecode="2";
			privilegegroup="Bid Plus Own";
		}else if(itemnum.equalsIgnoreCase("YearlyERPNoBid")){
			substype="4";
			subscribedays="367";
			subscribecode="2";
			privilegegroup="ERP No Bidding";
		}else if(itemnum.equalsIgnoreCase("YearlyERPWithBid")){
			substype="5";
			subscribedays="367";
			subscribecode="2";
			privilegegroup="ERP With Bidding";
		}
		String sql="update table_subscription set subscribecode='"+subscribecode+"',status='2',startdate=sysdate,enddate=sysdate+"+subscribedays+" where name='"+login+"'";

		this.executeQuery(sql);
		String upsql="update table_user set user2privilegegroup=(select objid from table_privilegegroup where upper(name)=upper('"+privilegegroup+"')) where loginname='"+login+"'";
		this.executeQuery(upsql);
		return(true);
	}
	
	public String decodeSpecialChar(String str ) {
		 if(str.indexOf("!L")>=0){
			 str=str.replaceAll("!L", "<");
		 }
		 if(str.indexOf("!G")>=0){
			 str=str.replaceAll("!G", ">");
		 }
		 if(str.indexOf("!M")>=0){
			 str=str.replaceAll("!M", "&");
		 }
		 if(str.indexOf("!D")>=0){
			 str=str.replaceAll("!D", "\"");
		 }
		
		 if(str.indexOf("!S")>=0){
			 str=str.replaceAll("!S", "'");
		 }
		return str;
	}

}