package cms.service.gen;

import java.sql.*;
import java.io.*;
import java.util.*;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cms.service.app.ApplicationConstants;
import cms.service.template.TemplateQuery;
import cms.service.template.TemplateTable;

/** Diffrent SemanticTag:<BR>
 *
 *
 * @author S.K.jana
 * @version 
 * @since 2005-2013
 * @since 
 */

public class SemanticTag {

	//These Tags are used to import the schema and Object data to the Semantic tables
	//The Maximun number of Container Object is currently 8

	static Log logger = LogFactory.getLog(SemanticTag.class);
	
	private String[] tag={"<Application>","</Application>","<SemanticObject>","</SemanticObject>","<FieldMap>","</FieldMap>",
				    "<SemanticData>","</SemanticData>","<ConvertObject>","</ConvertObject>","<ListProperty>","</ListProperty>","<ImplementObject>","</ImplementObject>",
				    "<ObjectRelation>","</ObjectRelation>","<Validation>","</Validation>","<Index>","</Index>","<Attribute>","</Attribute>",
				    "<Domain>","</Domain>","<ObjectRule>","</ObjectRule>","<GenericCode>","</GenericCode>","<ActionQuery>","</ActionQuery>",
				    "<ControlMap>","</ControlMap>","<AttributeRelation>","</AttributeRelation>","<Record>","</Record>",
                                    "<SemanticView>","</SemanticView>","<ViewRelation>","</ViewRelation>","<ViewAttribute>","</ViewAttribute>",
                                    "<ProjectCode>","</ProjectCode>","<MainCode>","</MainCode>","<SubCode>","</SubCode>","<TaskCode>","</TaskCode>",
                                    "<CallerMap>","</CallerMap>","<ValueMap>","</ValueMap>","<ChartMap>","</ChartMap>"};

	private String tagvalue;
	private ApplicationConstants ACONST = new ApplicationConstants();
	public void SemanticTag(){};
	public void setTagValue(String TagValue){
		this.tagvalue=TagValue;
	}
	public String getTagValue(){
		return(this.tagvalue);
	}

	public int getTagIndex(String TagValue){
	int retvalue=0;
		setTagValue(TagValue);
		for (int i=0; i<tag.length; i++){
	     	if (!TagValue.equals("") && tag[i].equalsIgnoreCase(TagValue))
			retvalue= i;
		}
		if (retvalue >=0)
			return(retvalue);

		return(-1);
	}

	public int getTagIndex(){
	int retvalue=0;

		for (int i=0; i<tag.length; i++){
	     		if (!getTagValue().equals("") && tag[i].equalsIgnoreCase(getTagValue()))
				retvalue= i;
		}
		if (retvalue >=0)
			return(retvalue);

		return(-1);
	}
	public boolean isTag(String TagValue){
		int length= TagValue.length();
                if(TagValue.indexOf("<")>=0 &&TagValue.indexOf(">")>=0)
	     	  return(true);

		return(false);
	}
	public String getTagType(String TagValue){
		int length= TagValue.length();
		String tagvalue= TagValue.trim();
	     	if (!tagvalue.equals("") && tagvalue.substring(0,1).equalsIgnoreCase("<") && !tagvalue.substring(0,2).equalsIgnoreCase("</")  )
			return("BEGIN");
		else if (!tagvalue.equals("") && tagvalue.substring(0,2).equalsIgnoreCase("</") )
			return("END");

		return("NONE");
	}

	public String getBeginTag(String Line){
		String line=Line.trim();
		int length= line.length();
		String tag ="";
		boolean first=true;
	     	if (!line.equals("") && getTagType(line).equalsIgnoreCase("BEGIN") ){
			for (int i=0; i<length; i++){
			     if (first==true && line.substring(i,1).equals("<") && !line.substring(i,1).equals(">") ){
					tag += line.substring(i,1);
					if (line.substring(i,1).equals(">"))
						first=false;
				}
			  	i++;
			}

			return(tag+">");
		}
		return(tag);
	}

	public String getEndTag(int Index){
		return(tag[Index+1]);
	}
	public String getEndTag(String TagValue){
		return(tag[getTagIndex(TagValue)+1]);
	}

	public int ValidatePrimaryKey(String TableName, String PrimaryField, String TagValue){

		//Signature value should be unique alpha numeric value

		setTagValue(TagValue);

		String sql = "Select max("+ PrimaryField + ") from " + TableName + " where " + PrimaryField + " between  " ;
		logger.info("\n Sql:= " + sql );

		TemplateQuery query =new TemplateQuery();
		query.setQuery(sql);
		TemplateTable output =new TemplateTable();
		output = query.getTableResultset();
		if (output.getRowCount()>0 ){
			String[] idrow = output.getRow(0);
			int idvalue=0;
			try {
				      idvalue= Integer.parseInt(idrow[0]) ;
					return(idvalue+1);
				}catch(NumberFormatException ne){
					logger.error("\n Error: No Record exists in database for Object = " + TableName);
			}
		}else{
			logger.error("\n Error: No Record exists in database for Object = " + TableName );
		}


	   return(0);

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
				while(!str.equals("[end]"))
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

}
