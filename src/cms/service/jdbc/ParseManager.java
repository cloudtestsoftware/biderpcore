package cms.service.jdbc;

import java.text.*;
import java.util.*;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cms.service.template.TemplateTable;




public class ParseManager {
	    static Log logger = LogFactory.getLog(ParseManager.class);
	 	private String[] fieldname =new String[100];
	 	private int[] index=new int[100];
	 	private String[] datatype= new String[100];
	 	private String[] value= new String[100];
		protected boolean mapflag =false;
		protected boolean parse =false;


	//For proc Output Table

		private String[] outfieldname =new String[100];
	 	private int[] outindex=new int[100];
	 	private String[] outdatatype= new String[100];
	 	private TemplateTable[] outputTable= new TemplateTable[20];
		private String returntype = "";
		protected boolean procflag =false;




	public static void main(String args[]) {
		if (args.length == 1) {
		     	String stringToExamine = args[0]; //print each word in order
		      BreakIterator 	boundary = BreakIterator.getSentenceInstance(Locale.US);
			boundary.setText(stringToExamine);
		//	 printEachBackward(boundary, stringToExamine);
		//	printFirst(boundary, stringToExamine);

		}
	}

	public void setNoOfInput( int number){
		fieldname =new String[number];
		index=new int[number];
		datatype= new String[number];
		value= new String[number];
		mapflag=false;
		parse=false;
	}

	public void setNoOfOutput( int number){
		outfieldname =new String[number];
		outindex=new int[number];
		outdatatype= new String[number];
		outputTable= new TemplateTable[number];
		procflag=false;
	}

	protected int[] getArrayIndex(){
		if (index.length >0)
			return(index);
		return(null);
	}

	protected void setArrayIndex(int[] Index){
		if (Index.length >0)
			index=Index;
	}
	protected int[] getOutArrayIndex(){
		if (outindex.length >0)
			return(outindex);
		return(null);
	}

	protected void setOutArrayIndex(int[] Index){
		if (Index.length >0)
			outindex=Index;
	}

	protected String[] getArrayFieldname(){
		if (fieldname[0]==null){
		     	for (int i=0; i< index.length; i++)
			 	fieldname[i] = "param" +i;
		}
		if (index.length >0)
			return(fieldname);

		return(null);
	}

	protected void setArrayFieldname(String[] Fieldname){
		if (Fieldname != null && Fieldname.length >0)
			fieldname= Fieldname;
	}
	protected String[] getOutArrayFieldname(){
		if (outfieldname[0]==null){
		     	for (int i=0; i< outindex.length; i++)
			 	outfieldname[i] = "param" +i;
		}
		if (outindex.length >0)
			return(outfieldname);

		return(null);
	}

	protected void setOutArrayFieldname(String[] Fieldname){
		if (Fieldname != null && Fieldname.length >0)
			outfieldname= Fieldname;
	}

	protected String[] getArrayDatatype(){
		if (datatype.length >0)
			return(datatype);
		return(null);
	}

	protected void setArrayDatatype(String[] Datatype){
		if (Datatype.length >0)
			datatype=Datatype;

	}
      protected String[] getOutArrayDatatype(){
		if (outdatatype.length >0)
			return(outdatatype);
		return(null);
	}

	protected void setOutArrayDatatype(String[] Datatype){
		if (Datatype.length >0)
			outdatatype=Datatype;

	}

	protected String[] getArrayFieldValue(){
		if (value.length >0)
			return(value);
		return(null);
	}
	protected void setArrayFieldValue(String[] FieldValue){
		if (FieldValue.length >0)
			value =FieldValue;
	}
	protected TemplateTable[] getOutArrayFieldValue(){
		if (outputTable.length >0)
			return(outputTable);
		return(null);
	}
	protected void setOutArrayFieldValue(TemplateTable[] FieldValue){
		if (FieldValue.length >0)
			outputTable =FieldValue;
	}

	public void setProcReturnType(String type){
		returntype=type;
	}
	public String getProcReturnType(){
		return(returntype);
	}

	protected boolean checkQuery(String query){
		 String testfield="";
	 	 BreakIterator boundary = BreakIterator.getWordInstance();
		 boundary.setText(query);
	 	 int start = boundary.first();

			for (int end = boundary.next();
				end != BreakIterator.DONE;
				start = end,
				end = boundary.next()) {

				testfield=query.substring(start,end);
				if (testfield.equals("?") )
					mapflag=true;
				if (testfield.equals("[") ||testfield.equals("]"))
					parse=true;

			}

			if (parse==true && mapflag==true)
				return(true);

		return(false);
	}

	protected boolean checkProc(String proc){
		 String testfield="";
	 	 BreakIterator boundary = BreakIterator.getWordInstance();
		 boundary.setText(proc);
	 	 int start = boundary.first();

			for (int end = boundary.next();
				end != BreakIterator.DONE;
				start = end,
				end = boundary.next()) {

				testfield=proc.substring(start,end);
				if (testfield.equals("?") ||testfield.equals("[") ||testfield.equals("]"))					mapflag=true;
					procflag=true;
			}

		return(procflag);
	}

	protected String prepareQuery(String query){
		 String testfield="";
		 String queryString="";
         //logger.info("\n>>>>>>Preparing Query" + query);
	 	 BreakIterator boundary = BreakIterator.getWordInstance();
		 boundary.setText(query);
	 	 int start = boundary.first();
		 int indexcount =0;
		 
         if(query.toLowerCase().contains("declare") && query.toLowerCase().contains("begin")) return query;
         
		 if ( mapflag ==true && parse==false){
			for (int end = boundary.next();
				end != BreakIterator.DONE;
				start = end,
				end = boundary.next()) {

				testfield=query.substring(start,end);
			
				if (testfield.equals("?") && index.length > indexcount
					 && index[indexcount]>= indexcount
				     	&& !datatype[indexcount].equalsIgnoreCase(DataType.DATE)
				   	&& !datatype[indexcount].equalsIgnoreCase(DataType.TIMESTAMP)){

					try {
				    		int i= Integer.parseInt(value[indexcount]) ;
						testfield= value[indexcount];

						if ( datatype[indexcount].equalsIgnoreCase(DataType.VARCHAR)
						     && datatype[indexcount].equalsIgnoreCase(DataType.CHAR)
						     && datatype[indexcount].equalsIgnoreCase(DataType.LONGVARCHAR)){

							if(!value[indexcount].equals(""))
								testfield= "'" +value[indexcount] +"'";
							else
								testfield="' '";
						}


					}catch(NumberFormatException ne){
						if (value[indexcount].equalsIgnoreCase("null") )
							testfield= "null";
						else if( !value[indexcount].equals(""))
							testfield= "'" +value[indexcount] +"'";
						else
							testfield="' '";
					}
					indexcount++;

				}else if (testfield.equals("?") && index.length > indexcount
					 && index[indexcount]>= indexcount
					 && datatype[indexcount].equalsIgnoreCase(DataType.DATE)){

					testfield= value[indexcount];
					indexcount++;
				}else if (testfield.equals("?") && index.length > indexcount
					 && index[indexcount]>= indexcount
					 && datatype[indexcount].equalsIgnoreCase(DataType.TIMESTAMP)){

					testfield= value[indexcount];
					indexcount++;
				}


				queryString=  queryString +testfield;

			}
			return(queryString);

		}
		return(query);
	}
	protected String prepareProc(String proc){
		 String testfield="";
		 String queryString="";
		 boolean foundreturn=false;
	 	 BreakIterator boundary = BreakIterator.getWordInstance();
		 boundary.setText(proc);
	 	 int start = boundary.first();
		 int indexcount =0;

		 if ( mapflag ==true && parse==false){
			for (int end = boundary.next();
				end != BreakIterator.DONE;
				start = end,
				end = boundary.next()) {
				//logger.info("Index count=" + Integer.toString(indexcount) );

				testfield=proc.substring(start,end);
				if (!getProcReturnType().equals("")
					&& testfield.equals("?") && !foundreturn ){
					//logger.info(testfield);
					foundreturn=true;
				}else if (foundreturn && testfield.equals("?")  || getProcReturnType().equals("") && testfield.equals("?")){
					if(  index.length > indexcount && index[indexcount]>= indexcount
						&& !datatype[indexcount].equalsIgnoreCase(DataType.DOUBLE)
						&& !datatype[indexcount].equalsIgnoreCase(DataType.NUMERIC)
				   		&& !datatype[indexcount].equalsIgnoreCase(DataType.FLOAT)
						&& !datatype[indexcount].equalsIgnoreCase(DataType.REAL)
						&& !datatype[indexcount].equalsIgnoreCase(DataType.DECIMAL)
						&& !datatype[indexcount].equalsIgnoreCase(DataType.INTEGER)
				   		&& !datatype[indexcount].equalsIgnoreCase(DataType.DATE)
						&& !datatype[indexcount].equalsIgnoreCase(DataType.TIME)
				   		&& !datatype[indexcount].equalsIgnoreCase(DataType.TIMESTAMP)){
							//logger.info("Index Length=" + Integer.toString(index.length) );

							//logger.info("value?=" + value[indexcount]);
						try {
				    			int i= Integer.parseInt(value[indexcount]) ;
							testfield= value[indexcount];
						}catch(NumberFormatException ne){
							if(!value[indexcount].equals(""))
								testfield= "'" +value[indexcount] +"'";
							else
								testfield="' '";
						}
					}else{
							testfield= value[indexcount];
					}
					indexcount++;

				}
				queryString=  queryString +testfield;

			}
		//	logger.info(queryString);

			return(queryString);

		}
		return(proc);
	}


	protected  String parseQuery( String source) {
	 	String testfield="";
	 	String queryString="";
	 	int count=-1;
                //logger.info("\n>>>>>>Parsing Query");
	 	BreakIterator boundary = BreakIterator.getWordInstance();
	 	boundary.setText(source);
	 	int start = boundary.first();
	    for (int end = boundary.next();
			end != BreakIterator.DONE;
			start = end,
			end = boundary.next()) {

		testfield=source.substring(start,end);
		//	logger.info(source.substring(start,end));

		if (testfield.equalsIgnoreCase("where") || testfield.equalsIgnoreCase("and")){

		 //logger.info(source.substring(start,end)+ count);
			if (count>=0 && value[count]!=null || count==-1 ){
			    	count++;
				index[count]=count;
				//logger.info("Index=" + count);
			}else{
				queryString= queryString +	datatype[count] + "  ";
				fieldname[count]=null;
				datatype[count]=null;
			}
		}
		if ( count==0 && !testfield.equalsIgnoreCase("where") && !testfield.equalsIgnoreCase("and")
			 &&    fieldname[count]==null && !testfield.trim().equals("")){
			//logger.info("fieldName=" + testfield);
			fieldname[count] =testfield;

		}else if (count>0 && !testfield.equalsIgnoreCase("and") && !testfield.equalsIgnoreCase("where")
			 && fieldname[count]==null && !testfield.trim().equals("")){
			fieldname[count] =testfield;
			//logger.info("fieldName=" + testfield);

		}else if (count>=0 && !testfield.equalsIgnoreCase("?")  && fieldname[count]!=null
			&& !testfield.equalsIgnoreCase("=") && datatype[count]==null
			&& !testfield.trim().equals("")) {
			if (testfield.trim().equals("(") || testfield.trim().equalsIgnoreCase("select")  ) {
				datatype[count]=null;
				fieldname[count]=null;
			//	index[count]=count-1;
				count--;
			}else{
			datatype[count] =testfield;
			//logger.info("DataType=" + testfield);
			}

		}else if (count>=0 &&  !testfield.equalsIgnoreCase("[")  && datatype[count]!=null
			&& !datatype[count].equals(testfield) && !testfield.equalsIgnoreCase("]")
			&& value[count]==null  &&  !testfield.equalsIgnoreCase("?")
			&& !testfield.equalsIgnoreCase("=") && !testfield.trim().equals("")){


			value[count] =testfield;
			//logger.info("Value=" + testfield);
		}
		if (count>=0 && !testfield.equalsIgnoreCase("[") && !testfield.equalsIgnoreCase("]")
			&& !testfield.equalsIgnoreCase("?") ){
		     if (testfield.equals(datatype[count] ))
				testfield="";
			else if (testfield.equals(value[count]) && !datatype[count].equalsIgnoreCase("Date")
				&& !datatype[count].equalsIgnoreCase("timestamp")){
				try {
				     int i= Integer.parseInt(testfield) ;
				}catch(NumberFormatException ne){
						testfield= "'" +testfield +"'";
				}
			}
				queryString=  queryString +testfield;

			}else if (count <0 || testfield.equalsIgnoreCase("select")){
			queryString= queryString +testfield;
			}
		}
			//logger.info(queryString);

		return(queryString);

       }


}
