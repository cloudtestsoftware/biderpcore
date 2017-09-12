package cms.service.app;


import java.util.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cms.service.template.TemplateQuery;
import cms.service.template.TemplateTable;
import cms.service.template.TemplateUtility;




public class PartitionObject {


	static Log logger = LogFactory.getLog(PartitionObject.class);
	private String signaturevalue;
	private static String nodeno;
	private static boolean isInitialize=false;
	private static ApplicationConstants ACONST = new ApplicationConstants();
	private static String[][] signature = new String[ACONST.MAX_SIGNATURE_COUNT][2];
	private static HashMap map = new HashMap();

	private static String[] table = new String[ACONST.MAX_TABLE_COUNT];
	private static  int[][] id= new int[ACONST.MAX_TABLE_COUNT][ACONST.MAX_SIGNATURE_COUNT];
	public void PartitionObject(){
		initialize();
	}
	private void initialize(){
		if(!isInitialize){
			TemplateUtility tu=new TemplateUtility();
			String sql="select *from sml_signature where nodeno="+getNodeNo()+" order by signature" ;
			TemplateTable result=tu.getResultSet(sql);
			for(int i=0;i<result.getRowCount();i++){
				signature[i][0]=result.getFieldValue("signature",i);
				signature[i][1]=result.getFieldValue("indexno",i);
			}
			isInitialize=true;
		}

	}
	public void setSignatureValue(String SignatureValue){
		this.signaturevalue=SignatureValue;
	}
	public String getSignatureValue(){
		return(this.signaturevalue);
	}
	public void setNodeNo(String nodeno){
		this.nodeno=nodeno;
	}
	public String getNodeNo(){
		return((nodeno==null||nodeno.isEmpty())?"10":nodeno);
	}

	
	public static String getPrimaryKey(){
		
			TemplateTable val=new TemplateUtility().getResultSet("select sys_guid() as objid from dual");
			if(val.getRowCount()>0){
				return("'"+val.getFieldValue("objid", val.getRowCount()-1)+"'");
			}
		
		return("0");	

	}

	//In future we should partition the table based on different node i.e. each webserver should have
	//only a range of ObjId independently. To implement this we should have a nodeindex as an init param
	/*public String getPrimaryKey(String table,String idfield, String fieldvalue){
		TemplateUtility tu=new TemplateUtility();
		if(table.toLowerCase().indexOf("sml_")<0){
			int idx=table.toLowerCase().indexOf("table_");
			TemplateTable val=tu.getResultSet("select "+(idx>=0?table.substring(6):table)+"_seq.nextval objid from dual");
			if(val.getRowCount()>0)
				return(val.getFieldValue("objid", val.getRowCount()-1));
		}
		return(getPrimaryKeyFromCache(table,idfield,fieldvalue));	

	}
	*/

	public String getPrimaryKey(String table){
		TemplateUtility tu=new TemplateUtility();
		if(table.toLowerCase().indexOf("sml_")<0){
			int idx=table.toLowerCase().indexOf("table_");
			TemplateTable val=tu.getResultSet("select "+(idx>=0?table.substring(6):table)+"_seq.nextval objid from dual");
			if(val.getRowCount()>0)
				return("'"+val.getFieldValue("objid", val.getRowCount()-1)+"'");
		}
		return("0");	

	}

	//In future we should partition the table based on different node i.e. each webserver should have
	//only a range of ObjId independently. To implement this we should have a nodeindex as an init param
	public String getPrimaryKeyFromCache(String table,String idfield, String fieldvalue){
		String tablename=(table.toLowerCase().indexOf("table_")>=0||table.toLowerCase().indexOf("sml_")>=0)?table.toLowerCase():"table_"+table.toLowerCase();
		String objid="";
		try{
			objid=map.get(tablename).toString();
		}catch (Exception e){

		} 

		if(objid==null||objid.equals("")){
			TemplateUtility tu=new TemplateUtility();
			String sql="select max(objid)+1 objid from "+tablename +" where objid>0" ;	
			TemplateTable val=tu.getResultSet(sql);
			if(val.getRowCount()>0)
				objid="'"+val.getFieldValue("objid", val.getRowCount()-1)+"'";
			if(objid==null||objid.equals(""))
				map.put(tablename, 1);
			else{
				map.put(tablename, objid);
			}
		}else{
			int val=(Integer.parseInt(objid)+1);
			map.put(tablename, val);
		}
		String retobjid=map.get(tablename).toString().equals("0")?"1":map.get(tablename).toString();
		if(retobjid.equals("1"))
			map.put(tablename, retobjid);

		return(retobjid);		

	}


	public int getTableIndex(String TableName){
		int i;
		for ( i=0; i<table.length; i++){
			//logger.info("index=" +i  + "Input table=" + TableName + "Table[]=" + table[i]);
			if (!TableName.equals("") && table[i]==null ){
				table[i]=TableName;
				return(i);
			}else if ( !table[i].trim().equals("")&& table[i].trim().equalsIgnoreCase(TableName) )
				return(i);
		}

		return(-1);
	}

	public int getSignatureIndex(String SignatureValue){
		int retvalue=0;
		int i=0;
		setSignatureValue(SignatureValue);
		for (i=0; i<signature.length; i++){
			if (!SignatureValue.equals("") &&signature[i][0]!=null &&signature[i][1]!=null&& signature[i][0].equalsIgnoreCase(SignatureValue.trim().substring(0,1))){
				retvalue= Integer.parseInt(signature[i][1]);
				break;
			}else if(signature[i][0]==null &&signature[i][1]==null)
				break;
		}
		if (retvalue >0)
			return(retvalue);
		else{
			TemplateUtility tu=new TemplateUtility();
			int indexval=i+1000;
			String sql="insert into sml_signature values("+i+",'"+SignatureValue.trim().substring(0,1)+"',"+indexval+","+getNodeNo()+")";
			tu.executeQuery(sql);
			signature[i][0]=SignatureValue.trim().substring(0,1);
			signature[i][1]=String.valueOf(indexval);
			return(indexval);
		}

	}

	public int getSignatureIndex(){
		int retvalue=0;
		int i=0;
		for ( i=0; i<signature.length; i++){
			if (!getSignatureValue().equals("") &&signature[i][0]!=null &&signature[i][1]!=null&& signature[i][0].equalsIgnoreCase(getSignatureValue().trim().substring(0,1))){
				retvalue= Integer.parseInt(signature[i][1]);
				break;
			}else if(signature[i][0]==null &&signature[i][1]==null)
				break;
		}
		if (retvalue >0)
			return(retvalue);
		else{
			TemplateUtility tu=new TemplateUtility();
			int indexval=1+1000;
			String sql="insert into sml_signature values("+i+",'"+getSignatureValue().trim().substring(0,1)+"',"+indexval+")";
			tu.executeQuery(sql);
			signature[i][0]=getSignatureValue().trim().substring(0,1);
			signature[i][1]=String.valueOf(indexval);
			return(indexval);
		}
	}
	public String getPartitionMinLimit(int signtureindex){
		return(getNodeNo()+Integer.toString(signtureindex)+ACONST.MIN_PARTION_LIMIT);
	}
	public String getPartitionMaxLimit(int signtureindex){
		return(getNodeNo()+Integer.toString(signtureindex)+ACONST.MAX_PARTION_LIMIT );
	}

	public int ValidatePrimaryKey(String TableName,  String SignatureValue,int signatureindex){

		//Signature value should be unique alpha numeric value
		//System.out.print("\n ValidatePrimaryKey-signatureindex"+signatureindex);
		setSignatureValue(SignatureValue);

		String sql = "Select max(objid) from " + TableName + " where objid like '" +
				getNodeNo()+signatureindex + "%'" ;
		//logger.info("\n Sql:= " + sql );

		TemplateQuery query =new TemplateQuery();
		query.setQuery(sql);
		TemplateTable output =new TemplateTable();

		output = query.getTableResultset();

		if (output!=null&&output.getRowCount()>0){

			String[] idrow = output.getRow(0);
			int idvalue=0;
			try {
				idvalue=idrow[0]!=null &&idrow[0].length()>6? Integer.parseInt(idrow[0].substring(6)):idvalue ;
				return(idvalue+1);
			}catch(NumberFormatException ne){
				logger.error("\n Error: No Record exists in database for Object = " + TableName);
			}catch (Exception e) {
				logger.error("\n Error: No Record exists in database for Object = " + TableName);
			}
		}else{
			logger.info("\n Error: No Record exists in database for Object = " + TableName );
		}


		return(Integer.parseInt(ACONST.MIN_PARTION_LIMIT));

	}


}
