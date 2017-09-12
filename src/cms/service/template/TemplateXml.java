package cms.service.template;

import java.io.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import cms.service.app.ApplicationConstants;
import cms.service.app.PartitionObject;
import cms.service.jdbc.DataType;
import cms.service.util.AttributesImpl;
import cms.service.util.FileUtility;


/**
 * Title:        Semantic Application
 * Description:  Semantic Main Infrastructure Project
 * Copyright:    Copyright (c) 2001
 * Company:      SemanticJava Soft
 * @author
 * @version 1.0
 */

public class TemplateXml extends DefaultHandler{
	static Log logger = LogFactory.getLog(TemplateXml.class);
    private String input="";
    private static String dbobject="";
    private String fieldname="";
    private String fdatatype="";
    private boolean isrequired=false;
    private static String fvalue="";
    private static String insertquery="";
    private static String insertvalue="";
    private static String updatequery="";
    private static String objid="";
    private String querymode;
    private String relation;
    private static String username;
    private static String groupuser;
    private boolean startrecord=false;
    private boolean isAnotherElem=false;
    private static int[] index;
    private static String[] fields;
    private static String[] datatype;
    private static String[] value;
    private static int colcount=0;
    private TemplateUtility tu=new TemplateUtility();
    private PartitionObject key= new PartitionObject();
    private ApplicationConstants ACONST=new ApplicationConstants();
    private static String dbtype="";
    private static boolean isparent=false;
    private static String relfield;
    private static String strBulksql="";
    private static String parentObjId="";
    private static String selectstr="";
    private static String childidlist="";
    private static TemplateTable data;


    /** Default parser name. */
    private static final String  DEFAULT_PARSER_NAME = "org.apache.xerces.parsers.SAXParser";


    private static boolean setValidation    = false; //defaults
    private static boolean setNameSpaces    = true;
    private static boolean setSchemaSupport = true;
    private static boolean setSchemaFullSupport = false;

    // Parsing type for parseXml method
    public final int SOURCE_TYPE_STRING =0;
    public final int SOURCE_TYPE_URI =1;


    //
    // Data
    //

    /** Print writer. */
    protected PrintWriter out;

    /** Canonical output. */
    protected boolean canonical=false;

    //
    // Constructors
    //

    /** Default constructor. */
    public TemplateXml(boolean canonical) throws UnsupportedEncodingException {
        this(null, canonical);
    }

    protected TemplateXml(String encoding, boolean canonical) throws UnsupportedEncodingException {

        if (encoding == null) {
            encoding = "UTF8";
        }

        out = new PrintWriter(new OutputStreamWriter(System.out, encoding));
        this.canonical = canonical;

    } // <init>(String,boolean)

    public TemplateXml() {
    }
    
    public void setRelation(String relation){
      this.relation=relation;
    }
    public String getRelation(){
      return(relation);
    }
    public void setDbType(String dbtype){
      this.dbtype=dbtype;
    }
    public String getDbType(){
      return(dbtype);
    }
    public void setUserName(String uname){
      this.username=uname;
    }
    public String getUserName(){
      return(username);
    }
     public void setGroupUser(String guser){
      this.groupuser=guser;
    }
    public String getGroupUser(){
      return(groupuser);
    }
    public void setParentObjId(String objid){
      parentObjId=objid;
    }
    public String getParentObjId(){
      return(parentObjId);
    }
    public void setInput(String input){
      this.input=input;
    }
    public TemplateTable getTableData(){
        return(data);
    }
    public StringReader getInput(){
      return(new java.io.StringReader(this.input));
    }
    public String getInputString(){
      return(this.input);
    }
    
    public String getPrimaryKey(){
    	return(key.getPrimaryKey());
    	
    }
    
   
    
    public boolean parseXml(String source, int sourcetype){
        String uri="";
        String parserName=DEFAULT_PARSER_NAME;
        logger.info("XML Data="+source);
        try {
            DefaultHandler handler = new TemplateXml(canonical);

            XMLReader parser = (XMLReader)Class.forName(parserName).newInstance();

            parser.setFeature( "http://xml.org/sax/features/validation",
                                                setValidation);
            parser.setFeature( "http://xml.org/sax/features/namespaces",
                                                setNameSpaces );
            parser.setFeature( "http://apache.org/xml/features/validation/schema",
                                                setSchemaSupport );
            parser.setFeature( "http://apache.org/xml/features/validation/schema-full-checking",
                                                setSchemaFullSupport );

            setInput(source);
            parser.setContentHandler(handler);
            parser.setErrorHandler(handler);
            if(sourcetype==SOURCE_TYPE_URI){
              parser.parse(source);
            }else{
              parser.parse(new org.xml.sax.InputSource(getInput()));
            }
            //logger.info("Before Calling makesql()");
            if(colcount>0){
              makeSql();
              fields=new String[200];
              datatype=new String[200];
              value=new String[200];
              index=new int[200];
            }
            //logger.info("after Calling makesql()");
            if(!isparent &&colcount>0)
              strBulksql+="\n\t\tupdate Table_"+dbobject+" set "+getRelation()+"="+(dbtype.equalsIgnoreCase("Oracle")?"parentid":"@parentid")+
                          " where objid in("+childidlist+(getDbType().equalsIgnoreCase("oracle")?");\n":")\n");
            //reset column count
            //logger.info("colcount="+colcount);
            colcount=0;
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
            return(false);
        }
        return(true);
    }

    //
    // Public static methods
    //

    /** Prints the output from the SAX callbacks. */
    public static void print(String parserName, String uri, boolean canonical) {

        try {
            DefaultHandler handler = new TemplateXml(canonical);

            XMLReader parser = (XMLReader)Class.forName(parserName).newInstance();

            parser.setFeature( "http://xml.org/sax/features/validation",
                                                setValidation);
            parser.setFeature( "http://xml.org/sax/features/namespaces",
                                                setNameSpaces );
            parser.setFeature( "http://apache.org/xml/features/validation/schema",
                                                setSchemaSupport );
            parser.setFeature( "http://apache.org/xml/features/validation/schema-full-checking",
                                                setSchemaFullSupport );


            parser.setContentHandler(handler);
            parser.setErrorHandler(handler);
            parser.parse(uri);
            //makeSql();
            //System.out.print("fields[0]="+fields[0]);


        }
        catch (Exception e) {
            e.printStackTrace(System.err);
        }

    } // print(String,String,boolean)

    //
    // DocumentHandler methods
    //

    /** Processing instruction. */
    public void processingInstruction(String target, String data) {
    	if(ACONST.GENERATE_LOG){
		    out.print("<?");
		    out.print(target);
		    if (data != null && data.length() > 0) {
		        out.print(' ');
		        out.print(data);
		    }
		    out.print("?>");
		    out.flush();
       }

    } // processingInstruction(String,String)

    //make sql
    private void makeSql(){
                String parentid="";
		String parentname="";
		String mycount="";
                //String bulksql="";
                String updateFilter="";
                String relvalue="";
            //verify if the current object is parent object and do the following
            if(dbtype.equalsIgnoreCase("Oracle")){
			parentid="parentid";
			parentname="parentname";
			mycount="mycount";
		}else if(dbtype.equalsIgnoreCase("Mssql")){
			parentid="@parentid";
			parentname="@parentname";
			mycount="@mycount";
		}

                    if(isparent){
                      int relindex=(relfield!=null &&!relfield.equals("")?tu.getArrayFieldIndex(relfield,fields):0);

                      relvalue=value[relindex];
                    }else{
                      relvalue=parentid;
                    }
                    updateFilter=((relfield!=null && !relfield.equals("")&& relvalue!=null && !relvalue.equals("")) ? (relfield+"="+relvalue +" and ") : "" );
                //Identify the parent table to declare the stored proc variable seting parent objid
		    if(isparent){

			if(dbtype.equalsIgnoreCase("Oracle")){
                                strBulksql="\n \t \t declare \n \t\t\t "+ mycount +" integer :=0; "+
                                        "\n \t\t\t "+parentid +" raw(16);" + "\n \t\t\t "+ parentname+ " nvarchar2(50);" + "\n \t\t Begin \n " +
                                        "\n\t\t\t "+ parentid +" := '" +parentObjId+"';" + "\n\t\t\t "+parentname +" := '" +dbobject+"';" ;

                        }else if(dbtype.equalsIgnoreCase("Mssql")){
				strBulksql="\n \t\t\t declare  "+ mycount +" integer; "+
						"\n \t\t\t declare  "+parentid +" integer;" + "\n \t\t\t declare  "+ parentname+ " varchar;" + "\n \t\t Begin \n " +
						"\n\t\t\t select  "+ parentid +" = " + parentObjId+";" + "\n\t\t\t select "+parentname +" = '" + dbobject+"';" ;


                        }

		    }
		    
            selectstr=" \n\t\t\t select " + (dbtype.equalsIgnoreCase("Oracle")==true ?  " count(ObjId)  into "+ mycount :mycount+"= count(ObjId)") + "  from Table_"+ dbobject +
                                    " where " + //((updateFilter.equals("")&& !isparent)==true ? (relfield +"="+parentid + " and "): updateFilter )+
                                    "objid ='"+value[tu.getArrayFieldIndex("objid",fields)] +(getDbType().equalsIgnoreCase("oracle")?"';":"");
                                    //"upper(ltrim(rtrim(name))) = upper(ltrim(rtrim('"+value[tu.getArrayFieldIndex("name",fields)]+"')))" +(getDbType().equalsIgnoreCase("oracle")?";":"");
            //complete the update and insert query
            insertquery+=",groupuser,genuser,gendate"+insertvalue+",'"+getGroupUser()+"','"+getUserName()+"',"+
            (getDbType().equalsIgnoreCase("oracle")?"sysdate);":"getdate())");
            //logger.info("insertquery="+insertquery);
            updatequery+=",ModUser='"+getUserName()+"',ModDate="+(getDbType().equalsIgnoreCase("oracle")?"sysdate where objid='"+value[0]+"';":"getdate() where objid='"+value[0]+"'");
            TemplateQuery query = new TemplateQuery();

            query.setInputTable(tu.getIntegerArrayByLength(index,colcount),tu.convertDataType(tu.getStringArrayByLength(datatype,colcount)),tu.getStringArrayByLength(value,colcount));
            query.setQuery(insertquery);
            String tmpinsert=query.getQuery();
            query.setQuery(updatequery);
            String tmpupdate=query.getQuery();
            //added these row to return all row set for the current atble
            if(data!=null&&data.getRowCount()<1)
              data.addColumns(tu.getStringArrayByLength(fields,colcount));
              data.addRow(tu.getStringArrayByLength(value,colcount));
            strBulksql+="\n\n\t\tBegin \n \t\t\t\t\t" + selectstr +
                      "\n\t\t\t if( "+ mycount +">0)"+ (dbtype.equalsIgnoreCase("Oracle")==true ?  " then " :"" )+
                      "\n\t\t\t"+tmpupdate+  "\n\t\t\t else \n \t\t\t " +
                      tmpinsert +(dbtype.equalsIgnoreCase("Oracle")==true ?  "\n \t\t\t end if;" :"" )   +
                      "\n\t\t\t" + (dbtype.equalsIgnoreCase("Oracle")==true ? ("\t exception \n\t\t\t\t when no_data_found then \n \t\t\t\t "+ mycount + ":=0; \n \t\tend;" ): ("\n\t\tend;")) ;

           // logger.info(strBulksql);
    }
    /** Start document. */
    public void startDocument() {
        dbobject="";
        strBulksql="";
        childidlist="";
        if(ACONST.GENERATE_LOG){
	        if (!canonical) {
	            out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
	            out.flush();
	        }
        }

    } // startDocument()


    /** Start element. */
    public void startElement(String uri, String local, String raw,Attributes attrs) {
        dbobject=(dbobject.isEmpty()?raw:dbobject);

       if(raw.equalsIgnoreCase("record") && !startrecord){
          //logger.info("Start Element record begin");
            startrecord=true;
            colcount=0;
            fields=new String[200];
            datatype=new String[200];
            value=new String[200];
            index=new int[200];
            objid="";
            //insertquery="\tinsert into table_"+ dbobject+ "(";
            //insertvalue=")values(";
            insertquery="\tinsert into table_"+ dbobject+ "("+(isparent?"":relfield+",");
            insertvalue=")values("+(isparent?"":"'0',");
            updatequery="\tupdate table_"+ dbobject+ " set ";

        }else if (raw.equalsIgnoreCase("record")&& startrecord){
            //logger.info("Start Element record");
            makeSql();
            fields=new String[200];
            index=new int[200];
            datatype=new String[200];
            value=new String[200];
            objid="";
            colcount=0;
            //insertquery="\tinsert into table_"+ dbobject+ "(";
            //insertvalue=")values(";
            insertquery="\tinsert into table_"+ dbobject+ "( "+(isparent?"":relfield+",");
            insertvalue=")values("+(isparent?"":"'0',");
            updatequery="\tupdate table_"+ dbobject+ " set ";
        }else if(!raw.equalsIgnoreCase("record")&& startrecord){
        	  fieldname=raw;
              isAnotherElem=true;
        	
          for (int i = 0; i < attrs.getLength(); i++) {
            if(attrs.getQName(i).equalsIgnoreCase("type")){
              fdatatype=normalize(attrs.getValue(i));
              datatype[colcount]=fdatatype;
              fields[colcount]=fieldname;
             
            }else if(attrs.getQName(i).equalsIgnoreCase("isRequired"))
              isrequired=(fieldname.equalsIgnoreCase("objid")||normalize(attrs.getValue(i)).equalsIgnoreCase("true"))?true:false;
            }
          }
          //colcount++;

        //comment out
       if(ACONST.GENERATE_LOG){
	        out.print('<');
	        out.print(raw);
	        if (attrs != null) {
	            attrs = sortAttributes(attrs);
	            int len = attrs.getLength();
	            for (int i = 0; i < len; i++) {
	                out.print(' ');
	                out.print(attrs.getQName(i));
	                out.print("=\"");
	                out.print(normalize(attrs.getValue(i)));
	                out.print('"');
	            }
	        }
	        out.print('>');
	        out.flush();
       }

    } // startElement(String,String,String,Attributes)

    /** Characters. */
    public void characters(char ch[], int start, int length) {
       
        fvalue= normalize(new String(ch, start, length)).replaceAll("(\\t|\\r?\\n)+", "");
       
        
        //verify if field is objid
        if(!tu.isEmptyValue(fieldname) &&this.fieldname.equalsIgnoreCase("objid")){
        	
        	if(!tu.isEmptyValue(this.fvalue) && this.fvalue.trim().length()>=32){
        		objid=this.fvalue;
        	}else if(tu.isEmptyValue(objid)){
        		 objid=getPrimaryKey().replaceAll("'", "");
        	}
        	 fvalue=objid;
             value[0]=objid;
             childidlist+=(childidlist.equals("")?"'"+objid+"'":",'"+objid+"'");
             if(isparent)
             setParentObjId(objid);
             
        }else  if(!tu.isEmptyValue(fieldname) &&!this.fieldname.equalsIgnoreCase("objid")){
        	if(this.isrequired &&tu.isEmptyValue(this.fvalue)){
        		if(fdatatype.equalsIgnoreCase("NUMBER")||fdatatype.equalsIgnoreCase("INTEGER"))
                    fvalue="0";
                  else
                    fvalue="' '";
        	}
        }
        
           
           if(isAnotherElem&&!tu.isEmptyValue(fvalue)){
              //childidlist+=(childidlist.equals("")?objid:","+objid);
              index[colcount]=colcount;
              value[colcount]=(datatype[colcount].equalsIgnoreCase("DATE")?tu.getConvertDateTime(dbtype,"Insert",fields[colcount],ACONST.DEFAULT_DATE_FORMAT,fvalue): tu.replaceSingleQouteForDatabase(fvalue));
              insertquery+=(colcount==0? fieldname:","+fieldname);
              insertvalue+=(colcount==0? "?":",?");
              updatequery+=(colcount==0? fieldname+"=?":","+fieldname+"=?");
              isAnotherElem=false;
              colcount++;
           }
           if(ACONST.GENERATE_LOG){
        	   out.print(fvalue);
        	   out.flush();
           }

    } // characters(char[],int,int);


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

	public String makeBulkSQL(String  dbtype,boolean isparent,String xml,
                             String relfield,String username,String groupuser){
                this.dbtype=dbtype;
                this.isparent=isparent;
                this.querymode=querymode;
                this.relfield=relfield;
                data=new TemplateTable();
               // System.out.print(">>>>>>>>>>>>>xml="+xml);
                setDbType(dbtype);
               
                setRelation(relfield);
                setUserName(username);
                setGroupUser(groupuser);
                //System.out.print(">>>>>>>>>>>>>relation="+getRelation());
                if(xml==null||xml.equals(""))
                  return("");
                if(parseXml(xml,SOURCE_TYPE_STRING)){
                    dbtype="";
                    username="";
                    return(strBulksql);
                }
            return("");

        }
        /** Ignorable whitespace. */
       public void ignorableWhitespace(char ch[], int start, int length) {

          characters(ch, start, length);
          out.flush();

        } // ignorableWhitespace(char[],int,int);

        /** End element. */
        public void endElement(String uri, String local, String raw) {
        	if(ACONST.GENERATE_LOG){
	          out.print("</");
	          out.print(raw);
	          out.print('>');
	          out.flush();
        	}
        } // endElement(String)

      //
      // ErrorHandler methods
      //

      /** Warning. */
      public void warning(SAXParseException ex) {
          System.err.println("[Warning] "+
                           getLocationString(ex)+": "+
                           ex.getMessage());
      }

      /** Error. */
      public void error(SAXParseException ex) {
          System.err.println("[Error] "+
                           getLocationString(ex)+": "+
                           ex.getMessage());
      }

    /** Fatal error. */
    public void fatalError(SAXParseException ex) throws SAXException {
        System.err.println("[Fatal Error] "+
                           getLocationString(ex)+": "+
                           ex.getMessage());
        throw ex;
    }

    /** Returns a string of the location. */
    private String getLocationString(SAXParseException ex) {
        StringBuffer str = new StringBuffer();

        String systemId = ex.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1)
                systemId = systemId.substring(index + 1);
            str.append(systemId);
        }
        str.append(':');
        str.append(ex.getLineNumber());
        str.append(':');
        str.append(ex.getColumnNumber());

        return str.toString();

    } // getLocationString(SAXParseException):String

    //
    // Protected static methods
    //

    /** Normalizes the given string. */
    protected String normalize(String s) {
        StringBuffer str = new StringBuffer();
       
        int len = (s != null) ? s.length() : 0;
        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '<': {
                    str.append("&lt;");
                    break;
                }
                case '>': {
                    str.append("&gt;");
                    break;
                }
                case '&': {
                    str.append("&amp;");
                    break;
                }
                case '"': {
                    str.append("&quot;");
                    break;
                }
                case '\r':
                case '\n': {
                    if (canonical) {
                        str.append("&#");
                        str.append(Integer.toString(ch));
                        str.append(';');
                        break;
                    }
                    // else, default append char
                }
                default: {
                    str.append(ch);
                }
            }
        }
       
        return str.toString();

    } // normalize(String):String

    /** Returns a sorted list of attributes. */
    protected Attributes sortAttributes(Attributes attrs) {

        AttributesImpl attributes = new AttributesImpl();

        int len = (attrs != null) ? attrs.getLength() : 0;
        for (int i = 0; i < len; i++) {
            String name = attrs.getQName(i);
            int count = attributes.getLength();
            int j = 0;
            while (j < count) {
                if (name.compareTo(attributes.getQName(j)) < 0) {
                    break;
                }
                j++;
            }
            attributes.insertAttributeAt(j, name, attrs.getType(i),
                                         attrs.getValue(i));
        }

        return attributes;

    } // sortAttributes(AttributeList):AttributeList

   

} // class TemplateXml
