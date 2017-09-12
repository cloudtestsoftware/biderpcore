package cms.service.jdbc;



import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cms.service.template.TemplateTable;


public class ParseQuery extends ParseManager {
	    static Log logger = LogFactory.getLog(ParseQuery.class);
	 	private String query ="";
		private String proc ="";

		protected  TemplateTable inputTable = new TemplateTable();
		protected  TemplateTable outputTable = new TemplateTable();

		 	
	protected void mapInputTable(){
	
	 	for (int i=0 ; i< getArrayIndex().length; i++){
			String[] rowvalue =new String[4];
			
			rowvalue[0]=Integer.toString(getArrayIndex()[i]).trim();
			rowvalue[1]=getArrayFieldname()[i].trim();
			rowvalue[2]=getArrayDatatype()[i].trim();
			rowvalue[3]=getArrayFieldValue()[i].trim();
			//logger.info("Map index"+ rowvalue[0] + "Map Value=" +rowvalue[3]);
			inputTable.addRow(rowvalue);
		}
	}
	protected void mapOutputTable(){
		
	 	for (int i=0 ; i< getOutArrayIndex().length; i++){
			String[] rowvalue =new String[3];
			rowvalue[0]=Integer.toString(getOutArrayIndex()[i]).trim();
			rowvalue[1]=getOutArrayFieldname()[i].trim();
			rowvalue[2]=getOutArrayDatatype()[i].trim();
			outputTable.addRow(rowvalue);
		}
	}

	public void setNoOfInput( int number){
		super.setNoOfInput(number);
	}
	public void setNoOfOutput( int number){
		super.setNoOfOutput(number);
	}

	public boolean isParseQuery(){
		return(parse); 
	}
	public boolean isMapQuery(){
		return(mapflag); 
	}
	

	public void setQuery(String sqlQuery){
		
		if(sqlQuery.toLowerCase().contains("table_user user")){
			sqlQuery=sqlQuery.toLowerCase();
			sqlQuery=sqlQuery.replace("table_user user", "table_user su");
			sqlQuery=sqlQuery.replace("table_User user", "table_user su");
			
			sqlQuery=sqlQuery.replaceAll("user\\.", "su.");
			
		}
		
		if (checkQuery(sqlQuery)==true){
			query=parseQuery(sqlQuery);
		}else if(isMapQuery()==true && getArrayIndex().length>0 
			&& getArrayIndex().length ==getArrayFieldValue().length){
			query=prepareQuery(sqlQuery);
		
		}else{
			query=sqlQuery;
		}
	//	logger.info(query);
	
	}
	public void setProc(String Proc){
				
		if(checkProc(Proc)==true && getOutArrayIndex().length>0 
			&& getOutArrayIndex().length ==getOutArrayDatatype().length 
			&& getInputTable().getRowCount() >0){
			proc=Proc;
		}else{
			proc=Proc;
		}
	
	}

	public String getQuery(){
		return(query);
	}
	public String getProc(){
		return(proc);
	}
	public void setInputTable(TemplateTable InputTable){
		inputTable=InputTable;
	}
	public TemplateTable getInputTable(){
		return(inputTable);
	}
	public void setOutputTable(TemplateTable OutputTable){
		outputTable=OutputTable;
	}
	public TemplateTable getOutputTable(){
		return(outputTable);
	}


}
