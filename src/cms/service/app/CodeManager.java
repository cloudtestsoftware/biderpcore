package cms.service.app;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cms.service.dhtmlx.Option;
import cms.service.template.TemplateTable;
import cms.service.template.TemplateUtility;

public class CodeManager {
	  static Log logger = LogFactory.getLog(CodeManager.class);
	  private TemplateUtility tu=new TemplateUtility();
	  private static TemplateTable genericcode=new TemplateTable();
	  private static HashMap<String, ArrayList<Option>> projectcode = new HashMap<String, ArrayList<Option>>();
      private static HashMap<String, ArrayList<Option>> maincode = new HashMap<String, ArrayList<Option>>();
      private static HashMap<String, ArrayList<Option>> subcode = new HashMap<String, ArrayList<Option>>();
     
    public CodeManager(){
    	if(projectcode.isEmpty()){
    		synchronized(projectcode){
    			if(projectcode.isEmpty()){
    				this.initProjectCode();
    			}
    		}
    	}
    }
	public ArrayList<Option> getProjectCode(String pcode){
		ArrayList<Option> retpcode=null;
    	try{
    		retpcode=projectcode.get(pcode);
    	}catch (Exception e){
    		retpcode=null;
    	} 
        return(retpcode);
    }
    public void addProjectCode(String pcode,ArrayList<Option> pcodeval){
    	projectcode.put(pcode, pcodeval);
    }
    
    public ArrayList<Option> getMainCode(String pcode){
    	ArrayList<Option> retmcode=null;
    	try{
    		retmcode=maincode.get(pcode);
    	}catch (Exception e){
    		retmcode=null;
    	} 
        return(retmcode);
    }
    public void addMainCode(String pcode,ArrayList<Option> mcode){
        maincode.put(pcode, mcode);
    }
    
    public ArrayList<Option> getSubCode(String pcode){
    	ArrayList<Option> retscode=null;
    	try{
    		retscode=subcode.get(pcode);
    	}catch (Exception e){
    		retscode=null;
    	} 
    	return(retscode);
    }
    public void addSubCode(String pcode,ArrayList<Option> scode){
    	subcode.put(pcode, scode);
    }
     
    /***
     * This method returns all generic code associated with the table_genericcode
     * for all seesion obejct which is having status=valid,
     *
     * */
    public TemplateTable getGenericCode(String privtable){
      //System.out.print("\nI am looking for generic code");
      String sql="select *from table_genericcode where status='1' order by attributename,codevalue " ;
       if((genericcode!=null && genericcode.getRowCount()==0)||(privtable!=null&&privtable.equalsIgnoreCase("genericcode"))){
           //System.out.print("\nI am looking for generic code 100");
        genericcode=tu.getResultSet(sql);
       }else if (genericcode!=null &&genericcode.getRowCount()<1){
          //System.out.print("\nI am looking for generic code 200");
          genericcode=tu.getResultSet(sql);
      }
      return(genericcode);
    }
    
    public void initProjectCode(){
    	String sql="select *from table_projectcode order by name";
    	TemplateTable code=tu.getResultSet(sql);

    	for(int i=0; i<code.getRowCount(); i++){

    		String tpcode=code.getFieldValue("projectcode", i);
    		ArrayList<Option> options= new ArrayList<Option>();

    		ArrayList<String> optionstr= new ArrayList<String>();
    		optionstr.add(code.getFieldValue("name", i));
    		options.add(new Option(tpcode,optionstr));

    		projectcode.put(tpcode, options);
    		initMainCode(tpcode);
    		initSubCode(tpcode);
    	}

    }

    public void initMainCode(String pcode){
    	String sql="select *from table_maincode where projectcode='"+pcode+"' order by name";
        TemplateTable code=tu.getResultSet(sql);
       	maincode.put(pcode, this.getOptionsList(code, "mainjobcode", "name"));
    }
    
    public void initSubCode(String pcode){
    	//String sql="select *from table_subcode where projectcode='"+pcode+"' order by name";
    	String sql="select s.*,g.name umname from table_subcode s,table_genericcode g  where s.projectcode='"+pcode+"' and ltrim(rtrim(g.codevalue))=ltrim(rtrim(s.umcode)) and g.ATTRIBUTENAME='UmCode'  order by s.name";
        TemplateTable code=tu.getResultSet(sql);
        addSubCode(pcode, this.getOptionsList(code, "subjobcode","umname", "name"));
        	
    }
    
    public void resetProjectCode(String pcode,String groupuser)
    {
    	String reccount="0";
    	String sql="select projectcode from table_projectcode where (to_char(sysdate,'DDMM')-to_char(gendate,'DDMM'))=0 "
    			+ "or (to_char(sysdate,'DDMM')-to_char(moddate,'DDMM'))=0 and groupuser='"+groupuser+"'";
    	TemplateTable count=tu.getResultSet(sql);
    	if(count.getRowCount()>0 && tu.isEmptyValue(pcode))
    		pcode=count.getFieldValue("projectcode", count.getRowCount()-1);
    	
    	if(count.getRowCount()>0 &&!tu.isEmptyValue(pcode)){
    		
            String psql = String.valueOf(String.valueOf((new StringBuffer("select *from table_projectcode where projectcode='")).append(pcode).append("' order by name")));
            TemplateTable code = tu.getResultSet(psql);
            addProjectCode(pcode, this.getOptionsList(code, "projectcode", "name"));
        
    	}else if(count.getRowCount()==0 &&!tu.isEmptyValue(pcode)){
    		synchronized(projectcode){
    			this.projectcode.remove(pcode);
    			this.maincode.remove(pcode);
    			this.subcode.remove(pcode);
    		}
    	}
       
        
    }
    
    public void resetMainCode(String pcode,String groupuser)
    {
    	String reccount="0";
    	String sql="select count(*) count from table_maincode "
    			+ "where (to_char(sysdate,'DDMM')-to_char(gendate,'DDMM'))=0 "
    			+ "or (to_char(sysdate,'DDMM')-to_char(moddate,'DDMM'))=0"
    			+ " and groupuser='"+groupuser+"'";
    	TemplateTable count=tu.getResultSet(sql);
    	if(count.getRowCount()>0)
    		reccount=count.getFieldValue("count", count.getRowCount()-1);
    	
    	if(!reccount.equals("0")){
    		
            String psql = String.valueOf(String.valueOf((new StringBuffer("select *from table_maincode where projectcode='")).append(pcode).append("' order by name")));
            TemplateTable code = tu.getResultSet(psql);
            String tmppcode = "";
            addMainCode(pcode, this.getOptionsList(code, "mainjobcode", "name"));
    	}
        
    }
    
    public void resetSubCode(String pcode,String groupuser)
    {
    	String reccount="0";
    	String sql="select count(*) count from table_subcode "
    			+ "where (to_char(sysdate,'DDMM')-to_char(gendate,'DDMM'))=0 "
    			+ "or (to_char(sysdate,'DDMM')-to_char(moddate,'DDMM'))=0" 
    			+ " and groupuser='"+groupuser+"'";
    	
    	TemplateTable count=tu.getResultSet(sql);
    	if(count.getRowCount()>0)
    		reccount=count.getFieldValue("count", count.getRowCount()-1);
    	
    	if(!reccount.equals("0")){
    		
            //String psql = String.valueOf(String.valueOf((new StringBuffer("select *from table_subcode where projectcode='")).append(pcode).append("' order by name")));
    		String psql = String.valueOf(String.valueOf((new StringBuffer("select s.*,g.name umname from table_subcode s,table_genericcode g  where s.projectcode='"+pcode+"' and ltrim(rtrim(g.codevalue))=ltrim(rtrim(s.umcode)) and g.ATTRIBUTENAME='UmCode'  order by s.name"))));
            TemplateTable code = tu.getResultSet(psql);
            addSubCode(pcode,this.getOptionsList(code, "subjobcode","umname", "name"));
    	}
        
    }
    
    public ArrayList<Option> getGroupProjectCode(String groupuser){
    	String sql="select *from table_projectcode where groupuser='"+groupuser+"' order by name";
        TemplateTable pcode=tu.getResultSet(sql);
        return (this.getOptionsList(pcode, "projectcode", "name"));
    }
    /*
     *  Parameters:
     *  table: name of the object without prefix table_ . i.e.  Project 
     *  fieldlist: list of fields which is having code
     *  projectcode: pass value of projectcode in record , if projectcode is NULL then populate all project code for the group
     *  groupuser: pass groupuser if projectcode is null otherwise pass null
     */
    public HashMap<String,ArrayList<Option>> getCodeObject(String table, String fieldlist[],String projectcode,String groupuser)
    {
    	HashMap<String,ArrayList<Option>> codes= new HashMap<String,ArrayList<Option>>();
        
        TemplateTable gcode = new TemplateTable();
        
        
        //if table is projectcode, maincode or subcode
        // then verify whether code is updated or added and call reset
        if(table.equalsIgnoreCase("projectcode")){
        	this.resetProjectCode(projectcode,groupuser);
        	
        } 
        else if(table.equalsIgnoreCase("maincode")){
        	this.resetMainCode(projectcode,groupuser);
        	
        }
        else if(table.equalsIgnoreCase("subcode")){
        	this.resetSubCode(projectcode,groupuser);
        	
        }
        
        
        //verify islogin
      
        for(int i = 0; i < fieldlist.length; i++)
        {
            
        	if(fieldlist[i].equalsIgnoreCase("projectcode") )
            {
               // pageprojectcode =  getProjectCode(projectcode);
        		if(tu.isEmptyValue(projectcode) &&!tu.isEmptyValue(groupuser)){
        			codes.put(fieldlist[i].toLowerCase(), getGroupProjectCode(groupuser));
        		}else{
        			codes.put(fieldlist[i].toLowerCase(), getProjectCode(projectcode));
        		}
                //retcode = String.valueOf(retcode) + String.valueOf(String.valueOf(String.valueOf((new StringBuffer("\n\t\t\t var ")).append(fieldlist[i].toLowerCase()).append("=").append(pageprojectcode))));
            }
          
        	if((fieldlist[i].equalsIgnoreCase("maincode") || fieldlist[i].equalsIgnoreCase("mainjobcode")) )
                
            {
                //pagemaincode = getMainCode(projectcode);
        		if(!tu.isEmptyValue(projectcode))
        			codes.put(fieldlist[i].toLowerCase(), getMainCode(projectcode));
                //retcode = String.valueOf(retcode) + String.valueOf(String.valueOf(String.valueOf((new StringBuffer("\n\t\t\t var ")).append(fieldlist[i].toLowerCase()).append("=").append(pagemaincode))));
                
            }
         
        	if((fieldlist[i].equalsIgnoreCase("subcode") || fieldlist[i].equalsIgnoreCase("subjobcode")) )
            {  
        		if(!tu.isEmptyValue(projectcode))
        			codes.put(fieldlist[i].toLowerCase(),  getSubCode(projectcode));
                //pagesubcode =  getSubCode(projectcode);
                //retcode = String.valueOf(retcode) + String.valueOf(String.valueOf(String.valueOf((new StringBuffer("\n\t\t\t var ")).append(fieldlist[i].toLowerCase()).append("=").append(pagesubcode))));
            }
            if( fieldlist[i].equalsIgnoreCase("subcode") || fieldlist[i].equalsIgnoreCase("subjobcode") || fieldlist[i].equalsIgnoreCase("Maincode") || fieldlist[i].equalsIgnoreCase("Mainjobcode") || fieldlist[i].equalsIgnoreCase("projectcode"))
                continue;
           
            gcode = this.getGenericCode(table);
            ArrayList<Option> options= new ArrayList<Option>();
            //String tmppropg = String.valueOf(String.valueOf((new StringBuffer("\n\t\t\t var ")).append(fieldlist[i].toLowerCase()).append("=<option value=0>---Choose ---</option>")));
            if(gcode != null)
            {
                for(int n = 0; n < gcode.getRowCount(); n++){
                    if(gcode.getFieldValue("attributename", n).equalsIgnoreCase(fieldlist[i])){

                		ArrayList<String> optionstr= new ArrayList<String>();
                		optionstr.add(gcode.getFieldValue("name", n));
                		options.add(new Option(gcode.getFieldValue("codevalue", n),optionstr));
                    }
                       
                }
                TemplateTable ccode=ServiceManager.getCustomGenericCode(groupuser);
                if(ccode!=null){
                	 for(int n = 0; n < ccode.getRowCount(); n++){
                         if(ccode.getFieldValue("attributename", n).equalsIgnoreCase(fieldlist[i])){

                     		ArrayList<String> optionstr= new ArrayList<String>();
                     		optionstr.add(ccode.getFieldValue("name", n));
                     		options.add(new Option(ccode.getFieldValue("codevalue", n),optionstr));
                         }
                            
                     }
                }
                codes.put(fieldlist[i].toLowerCase(),  options);
            }
            //retcode = String.valueOf(String.valueOf((new StringBuffer(String.valueOf(String.valueOf(retcode)))).append(tmppropg).append("")));
        }

        return codes;
    }
    
    public ArrayList<Option> getOptionsList(TemplateTable code, String propValueFld,String propStringFld)
	{
			ArrayList<Option> options= new ArrayList<Option>();

			for(int n = 0; n < code.getRowCount(); n++){
			
					ArrayList<String> optionstr= new ArrayList<String>();
					optionstr.add(code.getFieldValue(propStringFld, n));
					options.add(new Option(code.getFieldValue(propValueFld, n),optionstr));
		}
		return options;
	}
    
    public ArrayList<Option> getOptionsList(TemplateTable code, String propValueFld1,String propValueFld2, String propStringFld)
	{
			ArrayList<Option> options= new ArrayList<Option>();

			for(int n = 0; n < code.getRowCount(); n++){
			
					ArrayList<String> optionstr= new ArrayList<String>();
					optionstr.add(code.getFieldValue(propStringFld, n)+"/"+code.getFieldValue(propValueFld2, n));
					String value=code.getFieldValue(propValueFld1, n);
					options.add(new Option(value,optionstr));
		}
		return options;
	}
    /*
    public HashMap<String,ArrayList<Option>> createCodeOptions(String table, String fieldlist[],TemplateTable code)
	{
		HashMap<String,ArrayList<Option>> props= new HashMap<String,ArrayList<Option>>();
	

		for(int m = 0; m < fieldlist.length; m++)
		{

			ArrayList<Option> options= new ArrayList<Option>();

			for(int n = 0; n < code.getRowCount(); n++){
				if(fieldlist[m].equalsIgnoreCase(code.getFieldValue("name", n))){
					ArrayList<String> optionval= new ArrayList<String>();
					optionval.add(code.getFieldValue("PropertyString", n));
					options.add(new Option(code.getFieldValue("PropertyValue", n),optionval));
				}

				props.put(fieldlist[m].toLowerCase(), options);
			}

		}
		return props;
	}
	*/

}
