package cms.service.gen;

import cms.service.app.ApplicationConstants;
import cms.service.db.JndiDataSource;
import cms.service.util.FileUtility;


/**
 * Title:        Semantic Application
 * Description:  Semantic Main Infrastructure Project
 * Copyright:    Copyright (c) 2001
 * Company:      SemanticJava Soft
 * @author
 * @version 1.0
 * set the project property i.e.-import schema -inifile C:\arcproject\jsp\WEB-INF\application.ini -file D:\Javaproject\src\semantic\sql\semantic.dat.txt -dbtype mssql -compile
 *        -table user -log cms.log -compile -file /Users/srimanta.jana/Documents/softlean/SoftleanService/data/cms_service.dat
 *        -import rule  -log cms.log  -file /Users/srimanta.jana/Documents/softlean/SoftleanService/data/cmsruleraw.dat
 *        -import jobcode  -log cms.log  -file /Users/srimanta.jana/Documents/softlean/SoftleanService/data/cmsjobcode.dat
 *        -import schema -table user  -compile -log cms.log -file /Users/srimanta.jana/Documents/softlean/SoftleanService/data/cms_service.dat
 *        -table user -compile -log cms.log -file /Users/srimanta.jana/Documents/softlean/SoftleanService/data/cms_service.dat
 *        -compileview -log cms.log 
 */

public class datagen {

  private static String m_strUserName="";
  private static String m_strPassword="";
  private static String m_strDbType="";
  private static String m_strDSN="";

  private static String m_strImport="";
  private static String m_strCompile="";
  private static String m_strCompileAll="";
  private static String m_strCompileView="";
  private static String m_strFile="";
  private static String m_strUpgrade="";
  private static String m_strAllowDrops="";
  private static String m_strLog="";
  private static String m_strExport="";
  private static String m_strView="";
  private static String m_strApp="";
  private static String  m_contextFile="";
  private static String m_strTable=null;
  private static SchemaGenerator ip =new SchemaGenerator();

  private static ApplicationConstants CONST=new ApplicationConstants();
 
  private static FileUtility iu=new FileUtility();

  public datagen() {

      m_strUserName="";
      m_strPassword="";
      m_strDbType="oracle";
      m_strDSN="";
      m_strImport="";
      m_strCompile="";
      m_strFile="";
      m_strUpgrade="";
      m_strAllowDrops="";
      m_strLog="";
      m_strTable="";
      m_strExport="";
      m_strApp="";
       m_contextFile="";     
  }
  public static void main(String[] args){
    //parse the arguments first
     parseCmdLine(args);
    // Initialize ServiceContext
   
     JndiDataSource.setContextPath(m_contextFile);
     //ip.setAppServerNode(svx.getAppServerNode());
     //execute commands
     executeCommand();
    

  }
  // Parses the command line arguments and stores them in as data members of this class
  private static void parseCmdLine(String[] args){

    try
    {
      if(args.length>0){
        for (int i=0; i < args.length; i++){
          System.out.println("\n Argument="+args[i]);
          if(args[i].equalsIgnoreCase("-username")){
              i++;
              m_strUserName =new String(args[i]);
          }else if(args[i].equalsIgnoreCase("-password")){
              i++;
              m_strPassword = new String(args[i]);
          }else if(args[i].equalsIgnoreCase("-dbtype")){
              i++;
              m_strDbType = new String(args[i]);
              ip.setDbType(m_strDbType);
          }else if(args[i].equalsIgnoreCase("-dsn")){
              i++;
              m_strDSN = new String(args[i]);          
          }else if(args[i].equalsIgnoreCase("-import")){
              i++;
              m_strImport = new String(args[i]);
          }else if(args[i].equalsIgnoreCase("-compile")){
              m_strCompile = "compile";
          }else if(args[i].equalsIgnoreCase("-compileall")){  // very dangerous command, drops all tables
              m_strCompileAll = "compileAll";
          }else if(args[i].equalsIgnoreCase("-compileview")){
              m_strCompileView = "compileView";
          }else if(args[i].equalsIgnoreCase("-file")){
              i++;
              m_strFile = new String(args[i]);
          }else if(args[i].equalsIgnoreCase("-inifile")){
              i++;
               m_contextFile = new String(args[i]);
               JndiDataSource.setContextPath(m_contextFile);
          }else if(args[i].equalsIgnoreCase("-upgrade")){
              m_strUpgrade = "upgrade";
          }else if(args[i].equalsIgnoreCase("-allow_drops")){
              m_strAllowDrops ="allow_drops";
          }else if(args[i].equalsIgnoreCase("-log")){
              i++;
              m_strLog = new String(args[i]);
          }else if(args[i].equalsIgnoreCase("-export")){
              i++;
              m_strExport = new String(args[i]);
          }else if(args[i].equalsIgnoreCase("-table")){
              i++;
              m_strTable = new String(args[i]);
          }else if(args[i].equalsIgnoreCase("-app")){
              i++;
              m_strApp = new String(args[i]);
          }else{
              System.out.println("Wrong Arguments "+args[i]);
              
          }
        }
      }else{
            System.out.println("Datagen uses:");
            System.out.println("java datagen -import <schema> <view> <rule> <jobcode> or <data> -file <filename> -inifile <inifilename>");
            System.out.println("java datagen -export <schema> <view> <rule> <jobcode> or <data> -file <filename> -inifile <inifilename>");
            System.out.println("java datagen -import <sequence> <view> <rule> <jobcode> or <data> -file <filename> -inifile <inifilename>");
            System.out.println("Note: -username  -password -dbtype -dsn are mandatory option if application.ini file is not set");
            System.out.println("Optional uses:");
            System.out.println("-username -> Database <user> or <schema name>");
            System.out.println("-password -> <Password> for database user");
            System.out.println("-dbtype -> Database type <Oracle> or <Mssql>");
            System.out.println("-dsn-> Database <instance> or <host>");
            System.out.println("-file-> <file name> including path if not in current directory");
            System.out.println("-inifile-> <application.ini> including path if not in current directory");
            System.out.println("-log-> <logfile name> including path if not in current directory");
            System.out.println("-export-> the vaild option are <schema> <rule> <view> <rule> <jobcode>, enter all option without < & >");
            System.out.println("-import-> the vaild option are <schema> <rule> <view> <rule> <jobcode>, enter all option without < & >");
            System.out.println("-table-> <table name> if you want to import a specific table otherwise don't give this option , by default all table");
            System.out.println("-compile-> To compile the schema");
            System.out.println("-upgrade-> To upgrade the database schema from one product relaese version to another");
            System.out.println("-allow_drops->Drops any existiong column from the table from one baseline to another");
            System.out.println("-app-> Mention the application name if not the deafult application in your semantic.ini file");
            System.out.println("Example:");
            System.out.println("-export rule -file /Users/srimanta.jana/Documents/softlean/SoftleanService/WebContent/src/data/cmsrule.dat");
            System.out.println("-import schema -file /Users/srimanta.jana/Documents/softlean/SoftleanService/data/cms_service.dat");
            System.out.println("-import schema -table Project -file /Users/srimanta.jana/Documents/softlean/SoftleanService/data/cms_service.dat");

      }
      }catch(Exception e){
          e.printStackTrace();
      }
  }
  private static boolean  executeCommand(){
    //First import data and schema
    boolean retVal=false;

    if(!m_strLog.equals("")){
      iu.openFile(m_strLog+".log",false);
      ip.setIOUtility(iu);
      ip.islog=true;
    }

   /* if ( m_strImport.equalsIgnoreCase("sequence")){       
        retVal=ip.createallSequences();       
      }
    */
    if ( m_strImport.equalsIgnoreCase("schema")){
      ip.setImportTable(m_strTable);
      retVal=ip.importSchemaData(m_strFile);
     
    }
    if (m_strImport.equalsIgnoreCase("rule")){
      retVal=ip.createGenericCodeRuleObject();
      retVal=ip.importRuleData(m_strFile);
      retVal=ip.createPrivgroup();
      retVal=ip.createCalendarAndMonth();
      //retVal=ip.createWeekMap();
    }
    if (m_strImport.equalsIgnoreCase("jobcode")){
      retVal=ip.importJobCode(m_strFile);
    }
    //view metadata not stored in SML_View
    //This option currently ignored
    if (m_strImport.equalsIgnoreCase("view"))
      retVal=ip.importViewData(m_strFile);
    if (retVal && m_strCompile.equalsIgnoreCase("compile")&& !m_strUpgrade.equalsIgnoreCase("upgrade")){
      retVal=ip.compileSchema();
      retVal=ip.compileView();
      retVal=ip.updateApplication();
    }
    if ( m_strExport.equalsIgnoreCase("schema")){
      if(m_strFile!=null && !m_strFile.equals("")){
	iu.openFile(m_strFile,false);
        ip.setIOUtility(iu);
        retVal=ip.syncSchemaData();
	iu.closeFile();
      }else
	System.out.print("\n Please specify file a file name with path!");
    }
    if ( m_strExport.equalsIgnoreCase("rule")){
      if(m_strFile!=null && !m_strFile.equals("")){
        iu.openFile(m_strFile,false);
        ip.setIOUtility(iu);
        retVal=ip.exportRuleData();
	iu.closeFile();
      }else
	System.out.print("\n Please specify file a file name with path!");
    }
    if ( m_strExport.equalsIgnoreCase("jobcode")){
      if(m_strFile!=null && !m_strFile.equals("")){
        iu.openFile(m_strFile,false);
        ip.setIOUtility(iu);
        retVal=ip.exportJobCode();
	iu.closeFile();
      }else
	System.out.print("\n Please specify a file name with path!");
    }
    if ( m_strExport.equalsIgnoreCase("data") && m_strTable!=null &&!m_strTable.equals("")){
      if(m_strFile!=null && !m_strFile.equals("")){
        iu.openFile(m_strFile,false);
        ip.setIOUtility(iu);
        retVal=ip.exportTableData(m_strTable);
        iu.closeFile();
      }else
	System.out.print("\n Please specify file a file name with path!");
    }

    if (m_strUpgrade.equalsIgnoreCase("upgrade")&& m_strAllowDrops.equalsIgnoreCase(""))
      retVal=ip.upgradeSchema();
    if (m_strUpgrade.equalsIgnoreCase("upgrade")&& m_strAllowDrops.equalsIgnoreCase("allow_drops"))
        retVal=ip.upgradeSchemaWithDrop();
    if (!retVal && m_strCompileAll.equalsIgnoreCase("compileall")){
        retVal=ip.compileSchema();
        retVal=ip.compileView();
      }
    if (!retVal && m_strCompileView.equalsIgnoreCase("compileview")){
        retVal=ip.compileView();
      }
    if(ip.islog)
      iu.closeFile();
    return(retVal);

  }
}