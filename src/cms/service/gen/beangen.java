package cms.service.gen;


import cms.service.app.ApplicationConstants;

import cms.service.db.JndiDataSource;
import cms.service.template.TemplateTable;
import cms.service.template.TemplateUtility;


/**
 * Title:        Semantic Application
 * Description:  Semantic Main Infrastructure Project
 * Copyright:    Copyright (c) 2001
 * Company:      SemanticJava Soft
 * @author
 * @version 1.0
 * set Project Parameter = -bean Property -beanpath D:\Javaproject\src -jsppath D:\Javaproject\jsp\semantic\src\esolution -app ebuilder -inifile d:\javaproject\src\application.ini
 * set Project Parameter = -report Property -beanpath D:\Javaproject\src -jsppath D:\Javaproject\jsp\semantic\src\esolution -app cms -inifile d:\javaproject\src\application.ini
 */

public class beangen {

  private static String m_strBean="";
  private static String m_strBeanpath="";
  
  private static String m_strApp="";

  private static String m_contextFile="";
  private static BeanGeneratorService bg =new BeanGeneratorService();
 
  private static ApplicationConstants CONST=new ApplicationConstants();
  //private static ServiceContext svx;

  public beangen() {

      m_strBean="";
      m_strBeanpath="";
     
  }
  public static void main(String[] args){
    //parse the arguments first
     parseCmdLine(args);
    // Initialize ServiceContext
    
     JndiDataSource.setContextPath(m_contextFile);
    
      bg.setAppName(m_strApp);
     //execute commands
     if(executeCommand())
        System.out.println("\n Generated beans successfully.");
     else
      System.out.println("\n Beangen failed.");

  }
  // Parses the command line arguments and stores them in as datamembers of this class
  private static void parseCmdLine(String[] args){

    try
    {
      if(args.length>0){
        for (int i=0; i < args.length; i++){
          System.out.println("\n Argument="+args[i]);
            if(args[i].equalsIgnoreCase("-bean")){
              i++;
              m_strBean = new String(args[i]);
              
           }else if(args[i].equalsIgnoreCase("-beanpath")){
              i++;
              m_strBeanpath = new String(args[i]);
           
           }else if(args[i].equalsIgnoreCase("-inifile")){
              i++;
              m_contextFile = new String(args[i]);
              JndiDataSource.setContextPath(m_contextFile);
           }else if(args[i].equalsIgnoreCase("-app")){
              i++;
              m_strApp = new String(args[i]);
           }else{
              System.out.println("Wrong Arguments");
              return;
          }
        }
      }else{
            System.out.println("beangen uses:");
            System.out.println("java beangen -bean <Parent Object> -beanpath <beanpath> -jsppath <jsppath>  ");
            System.out.println("-bean-> <Parent Table> or <All> for all objects");
            System.out.println("-inifile-> <application.ini> including path if not in current directory");
            System.out.println("-beanpath-> Path where the generated screen files will be stored without package name i.e. source root");         
            System.out.println("-app-> Mention the application name if not the deafult application in your semantic.ini file");

      }
      }catch(Exception e){
          e.printStackTrace();
      }
  }
  private static boolean  executeCommand(){
    //First import data and schema
  
    // Generate Jsp and bean
    if (m_strBean==null|| m_strBean.equals("") ){
    	TemplateTable objlist= new TemplateUtility().getResultSet("select objectname from sml_dbobject order by objectname");
    	if(objlist!=null &&objlist.getRowCount()>0){
    		for(int i=0; i<objlist.getRowCount(); i++){
    			m_strBean=objlist.getFieldValue("objectname", i);
    			if(m_strBean!=null &&!m_strBean.isEmpty()){
    				bg.init();
	    			bg.createDaoBean(m_strBean,m_strBeanpath);
	    	        bg.createImplBean(m_strBean,m_strBeanpath);
	    	        bg.createServiceBean(m_strBean,m_strBeanpath);
	    	        bg.createOracleSpInsert(m_strBean,m_strBeanpath);
	    	        bg.createOracleSpUpdate(m_strBean,m_strBeanpath);
	    	        bg.createObjectRule(m_strBean,m_strBeanpath);
    			}
    		}
    	}
    }else{
    	
      if( !m_strBeanpath.equals("")){
        bg.createDaoBean(m_strBean,m_strBeanpath);
        bg.createImplBean(m_strBean,m_strBeanpath);
        bg.createServiceBean(m_strBean,m_strBeanpath);
        bg.createOracleSpInsert(m_strBean,m_strBeanpath);
        bg.createOracleSpUpdate(m_strBean,m_strBeanpath);
        bg.createObjectRule(m_strBean,m_strBeanpath);
      }
      
    //Generate report jsp and bean
    }
    
    return(true);

  }
}