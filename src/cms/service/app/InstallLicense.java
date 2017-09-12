package cms.service.app;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


import cms.service.template.TemplateTable;
import cms.service.template.TemplateUtility;

public class InstallLicense {
	
	  static final String adminuser="admin@biderp.com";
	  static Log logger = LogFactory.getLog(InstallLicense.class);
	  private static String singature[]={"AX3","BZX","HK#","LK6","MDR","GK8","RPD","LSX","RY7","GV9"};
	  private int liccount=0;
	  private int licvaliddays=0;
	  private int wbsno=0; //total no of project code record allowed against this license
	 
	
	//Constant for LicenseCount for module
	  private static int LIC_ADMIN_COUNT=1;
	  private static int LIC_GCL_COUNT=1;
	  private static int LIC_LDL_COUNT=1;
	  private static int LIC_TM_COUNT=1;
	  private static int LIC_CM_COUNT=1;
	  private static int LIC_CRM_COUNT=1;
	  private static int LIC_MRK_COUNT=1;
	  private static int LIC_ERP_COUNT=1;
	  private static int LIC_GC_COUNT=1;
	  
	//Hask Key
	  private static String companyhashkey="";
	  private static String serverhashkey="";
	  
	  private static String licseperator="%";
	  private static String digitsepertor="O";
	  private static String authsepertor="@";
	  private static String authcode="AC#8-UHK$-9GP*-HSX^-GKF!";
	  private static String auth1="AC#8";
	  private static String auth2="UHK$";
	  private static String auth3="9GP*";
	  private static String auth4="HSX^";
	  private static String auth5="GKF!";
	  private static String custAuthCode=""; //customer will be supplied authoriztion code
	  private static String verifycustAuthCode=""; //the authorization code will be generted from the license key which is verified against supplied authorization key

	  
	  private TemplateUtility tu=new TemplateUtility();
	  private PartitionObject key = new PartitionObject();
	  
	  private String getIntegerFromStringSignature(String keyval){
		    // System.out.print("\n Key Value="+keyval);
		    for (int i=0;i<singature.length;i++){
		      //System.out.print("\n Signature="+singature[i]+" keyval="+keyval+ " Sig length="+singature[i].length());
		      if(singature[i].equals(keyval))
		        return(String.valueOf(i));
		    }
		        return("");
		  }
	  
	  public int getCountFromKeyValue(String key){
		    int count=0;
		    //System.out.print("\n Key="+key);
		    String [] keyval=tu.getString2TokenArray(key,digitsepertor);
		    String value="";
		    for (int i=0;i<keyval.length;i++)
		      value+=getIntegerFromStringSignature(keyval[i]);
		     // System.out.print("\n Value="+value);
		      return(Integer.parseInt(value));
		  }
	  
	  private String getStringKeyValue(int intval){
		    String val="";
		    char[] tmpval=Integer.toString(intval).toCharArray();
		    for (int i=0;i<tmpval.length;i++)
		      val+=(tmpval[i]!='-'?singature[Integer.parseInt(String.valueOf(tmpval[i]))]+digitsepertor:"");
		      return(val);
		  }
	  
	  public void installLicenseKey(String company,String serverip,String licensekey,String custauthcode ,String dbtype){
		    String [] licensetoken=tu.getString2TokenArray(licensekey,licseperator);
		    String [] authcodelist=tu.getString2TokenArray(authcode,"-");
		    String liccountkey="";
		    String vliddayskey="";
		    String wbskey="";
		    String sql="";
		   logger.info("\n Installing License Key!");

		    String serverhashFromIP=String.valueOf(serverip.hashCode()); //hashcode generated from sever ip address at runtime
		    //serverhashFromIP=Integer.parseInt(serverhashFromIP);
		     //logger.info("\n serverhashFromIP="+serverhashFromIP);
		    if(licensetoken.length==5 &&authcodelist.length==5){
		      //company hask key from license
		      companyhashkey=tu.getString2TokenArray(licensetoken[0],authsepertor)[1].equals(auth1)?tu.getString2TokenArray(licensetoken[0],authsepertor)[0]:"";
		     //logger.info("\n Icompanyhashkey="+companyhashkey);
		      //server hask key from license
		      serverhashkey=tu.getString2TokenArray(licensetoken[1],authsepertor)[1].equals(auth2)?tu.getString2TokenArray(licensetoken[1],authsepertor)[0]:"";
		      //logger.info("\n serverhashkey="+serverhashkey);
		       //server license count from license
		      liccountkey=tu.getString2TokenArray(licensetoken[2],authsepertor)[1].equals(auth3)?tu.getString2TokenArray(licensetoken[2],authsepertor)[0]:"";
		      //logger.info("\n liccountkey="+liccountkey);
		      vliddayskey=tu.getString2TokenArray(licensetoken[3],authsepertor)[1].equals(auth4)?tu.getString2TokenArray(licensetoken[3],authsepertor)[0]:"";
		       //logger.info("\n vliddayskey="+vliddayskey);
		       wbskey=tu.getString2TokenArray(licensetoken[4],authsepertor)[1].equals(auth5)?tu.getString2TokenArray(licensetoken[4],authsepertor)[0]:"";
		       //logger.info("\n wbskey="+wbskey);
		    }
		    //server hashcode generated from server ip should match the generated hashcode from license key by serverhashFromIP.equals(serverhashkey)
		    verifycustAuthCode=(!companyhashkey.equals("")&&!serverhashkey.equals("")&&serverhashFromIP.equals(serverhashkey)? getStringKeyValue(Integer.parseInt(companyhashkey))+"-"+getStringKeyValue(Integer.parseInt(serverhashkey)):"");
		    logger.info("\n verifycustAuthCode="+verifycustAuthCode);
		    if(verifycustAuthCode.equals(custauthcode) &&!liccountkey.equals("")&& !vliddayskey.equals("")){
		      liccount=getCountFromKeyValue(liccountkey);
		      licvaliddays=getCountFromKeyValue(vliddayskey);
		      wbsno=getCountFromKeyValue(wbskey);
		      String vsql="select *from table_installlicense where  LicenseCount>0 and ServerIp='"+serverip
		      +"' and ExpiryDate>"+(dbtype.equalsIgnoreCase("oracle")? "sysdate":"getdate()");
		      TemplateTable result=tu.getResultSet(vsql);
		      if(result.getRowCount()>0 &&!result.getFieldValue("LicenseKey",result.getRowCount()-1).equalsIgnoreCase(liccountkey) &&liccount>0){
		        sql="update table_installlicense set LicenseKey='"+licensekey+"', LicCountKey='"+liccountkey +"',LicenseCount="+liccount +",ExpiryKey='"+vliddayskey+"',ExpiryDate="+
		          "gendate+"+licvaliddays+",WbsKey='"+wbskey+"',wbsno="+wbsno+
		           " where objid='"+result.getFieldValue("objid",result.getRowCount()-1)+"'";
		          tu.executeQuery(sql);
		      }else if (result.getRowCount()==0){
		    	  String objid=key.getPrimaryKey();
		        //String objid=key.getPrimaryKey("table_installlicense","objid",company);
		        sql="insert into table_installlicense(objid,name,serverip,LicenseKey,LicCountKey,LicenseCount,ExpiryKey,ExpiryDate,WbsKey,WbsNo,GenUser,GenDate,Moduser,ModDate)values("+
		            objid+",'"+company+"','"+serverip+"','"+licensekey+"','"+liccountkey+"',"+liccount+",'"+vliddayskey+"',"+
		            (dbtype.equalsIgnoreCase("oracle")? "sysdate+"+licvaliddays:"getdate()+"+licvaliddays)+",'"+wbskey+"',"+wbsno+",'"+adminuser+"',"+(dbtype.equalsIgnoreCase("oracle")? "sysdate":"getdate()")+
		            ",'"+adminuser+"',"+(dbtype.equalsIgnoreCase("oracle")? "sysdate)":"getdate())");
		        tu.executeQuery(sql);
		      }
              
		      //Install Module licenses
		      String mvsql="select distinct objid, name from table_module";
		      //for every startup of the webserver reinstll the module license to avoide the user interaction if anybody change the license informtion
		      String msql="delete table_modulelicense";
		      tu.executeQuery(msql);
		      result= new TemplateTable();
		      result=tu.getResultSet(mvsql);
		      if (result.getRowCount()>0){
		        for(int k=0;k<result.getRowCount();k++){
		        	String objid=key.getPrimaryKey();
		            String name=result.getFieldValue("name",k);
		            String moduleid=result.getFieldValue("objid",k);
		            sql="insert into table_modulelicense(objid,name,MODULEKEY,LICENSECOUNT,MODULEID,ORIGINID,DESTINITIONID,GENUSER,GENDATE,MODUSER,MODDATE)values("+
		                objid+",'"+name+"','"+getStringKeyValue(getLicenseCount(name))+"',"+getLicenseCount(name)+",'"+moduleid+"',"+objid+",'"+moduleid+
		                "','"+adminuser+"',"+(dbtype.equalsIgnoreCase("oracle")? "sysdate":"getdate()")+
		                ",'"+adminuser+"',"+(dbtype.equalsIgnoreCase("oracle")? "sysdate)":"getdate())");
		            tu.executeQuery(sql);
		        }
		      }

		      //Install Privilege Group Licesne
		      //Install Module licenses
		      String pvsql="select *from table_privilegegroup";
		      //for every startup of the webserver reinstall the module license to avoid the user interaction if anybody change the license information
		      String gsql="delete table_grouplicense";
		      tu.executeQuery(gsql);
		      result= new TemplateTable();
		      result=tu.getResultSet(pvsql);
		      String groupid="";
		      if (result.getRowCount()>0){
		        for(int k=0;k<result.getRowCount();k++){
		        	String objid=key.getPrimaryKey();
		            
		            String name=result.getFieldValue("name",k);
		            groupid=result.getFieldValue("objid",k);
		            //First get the license count
		             String glsql="select nvl(sum(ms.licensecount),0) count from table_privilegegroup pg,table_module md, table_modulelicense ms  where pg.objid='"+groupid+
		            "' and pg.objid=md.module2privilegegroup and md.objid=ms.moduleid";
		            TemplateTable respv= tu.getResultSet(glsql);
		            String groupliccount="0";
		            if (respv.getRowCount()>0)
		              groupliccount=respv.getFieldValue("count",respv.getRowCount()-1);

		            //insert the group license record
		            sql="insert into table_grouplicense(objid,name,GROUPKEY,LICENSECOUNT,GROUPID,ORIGINID,DESTINITIONID,GENUSER,GENDATE,MODUSER,MODDATE)values("+
		                objid+",'"+name+"','"+getStringKeyValue( Integer.parseInt( groupliccount))+"',"+groupliccount+","+groupid+","+objid+",'"+groupid+
		                ",'"+adminuser+"',"+(dbtype.equalsIgnoreCase("oracle")? "sysdate":"getdate()")+
		                "','"+adminuser+"',"+(dbtype.equalsIgnoreCase("oracle")? "sysdate)":"getdate())");
		            tu.executeQuery(sql);

		            //update user license table for all users ssociated to the group having same license count as group
		            String ussql="update table_userlicense set licensekey='"+getStringKeyValue( Integer.parseInt( groupliccount))+"',LICENSECOUNT="+groupliccount +
		              " where groupid="+groupid;
		           tu.executeQuery(ussql);

		        }
		      }
		      //update entity license count
		      String esql="update table_entity e set e.licensecount="+liccount+",e.licenseused=(select sum(ul.licensecount) "+
		        " from table_user u, table_userlicense ul where u.user2entity=e.objid and ul.destinitionid=u.objid)";
		      tu.executeQuery(esql);
		    }
		  }
		  public void applyLicenseRule(String objectname, String dbtype, TemplateTable data){
		    String groupid="";
		    TemplateTable result= new TemplateTable();
		    if(objectname.equalsIgnoreCase("PrivilegeGroup")){
		       //Install Module licenses
		      String mvsql="select distinct objid, name from table_module";
		      //for every startup of the webserver reinstll the module license to avoide the user interaction if anybody change the license informtion
		      String msql="delete table_modulelicense";
		      tu.executeQuery(msql);
		      result= new TemplateTable();
		      result=tu.getResultSet(mvsql);
		      if (result.getRowCount()>0){
		        for(int k=0;k<result.getRowCount();k++){
		            //String objid=key.getPrimaryKey("table_modulelicense","objid",result.getFieldValue("name",k));
		        	String objid=key.getPrimaryKey();
		            String name=result.getFieldValue("name",k);
		            String moduleid=result.getFieldValue("objid",k);
		            String sql="insert into table_modulelicense(objid,name,MODULEKEY,LICENSECOUNT,MODULEID,ORIGINID,DESTINITIONID,GENUSER,GENDATE,MODUSER,MODDATE)values("+
		                objid+",'"+name+"','"+getStringKeyValue(getLicenseCount(name))+"',"+getLicenseCount(name)+",'"+moduleid+"',"+objid+",'"+moduleid+
		                "','"+adminuser+"',"+(dbtype.equalsIgnoreCase("oracle")? "sysdate":"getdate()")+
		                ",'"+adminuser+"',"+(dbtype.equalsIgnoreCase("oracle")? "sysdate)":"getdate())");
		            tu.executeQuery(sql);
		        }
		      }
		      String pvsql="select *from table_privilegegroup";
		      //for every startup of the webserver reinstll the module license to avoide the user interaction if anybody change the license informtion
		      String gsql="delete table_grouplicense";
		      tu.executeQuery(gsql);

		      result=tu.getResultSet(pvsql);

		      if (result.getRowCount()>0){
		        for(int k=0;k<result.getRowCount();k++){
		        	String objid=key.getPrimaryKey();
		            //String objid=key.getPrimaryKey("table_grouplicense","objid",result.getFieldValue("name",k));
		            String name=result.getFieldValue("name",k);
		            groupid=result.getFieldValue("objid",k);
		            //First get the license count
		             String glsql="select nvl(sum(ms.licensecount),0) count from table_privilegegroup pg,table_module md, table_modulelicense ms  where pg.objid='"+groupid+
		            "' and pg.objid=md.module2privilegegroup and md.objid=ms.moduleid";
		            TemplateTable respv= tu.getResultSet(glsql);
		            String groupliccount="0";
		            if (respv.getRowCount()>0)
		              groupliccount=respv.getFieldValue("count",respv.getRowCount()-1);

		            //insert the group license record
		            String sql="insert into table_grouplicense(objid,name,GROUPKEY,LICENSECOUNT,GROUPID,ORIGINID,DESTINITIONID,GENUSER,GENDATE,MODUSER,MODDATE)values("+
		                objid+",'"+name+"','"+getStringKeyValue( Integer.parseInt( groupliccount))+"',"+groupliccount+","+groupid+","+objid+","+groupid+
		                ",'"+adminuser+"',"+(dbtype.equalsIgnoreCase("oracle")? "sysdate":"getdate()")+
		                ",'"+adminuser+"',"+(dbtype.equalsIgnoreCase("oracle")? "sysdate)":"getdate())");
		            tu.executeQuery(sql);

		            //update user license table for all users ssociated to the group having same license count as group
		            String ussql="update table_userlicense set licensekey='"+getStringKeyValue( Integer.parseInt( groupliccount))+"',LICENSECOUNT="+groupliccount +
		              " where groupid="+groupid;
		           tu.executeQuery(ussql);

		        }
		      }
		      //update entity license count
		      //String esql="update table_entity e set e.licenseused=(select sum(ul.licensecount) "+
		      //  " from table_user u, table_userlicense ul where u.user2entity=e.objid and ul.destinitionid=u.objid)";
		      //tu.executeQuery(esql);
		    }else if(objectname.equalsIgnoreCase("User") &&data!=null &&data.getRowCount()>0){

		      for(int m=1;m<data.getRowCount();m++){
		      //delete the user license

		      String userobjid=data.getFieldValue("objid",m);

		      String entityid=data.getFieldValue("user2entity",m);
		      String pvsql="select pg.* from table_privilegegroup pg ,table_user u where u.objid='"+userobjid +"' and u.user2privilegegroup=pg.objid";
		      result=tu.getResultSet(pvsql);
		      //for every startup of the webserver reinstall the module license to avoid the user interaction if anybody change the license information
		      String gsql="delete table_userlicense where destinitionid="+userobjid;
		      tu.executeQuery(gsql);

		       if (result.getRowCount()>0){
		        for(int k=0;k<result.getRowCount();k++){
		        	String objid=key.getPrimaryKey();
		          
		            String name=data.getFieldValue("name",k);
		            groupid=result.getFieldValue("objid",result.getRowCount()-1);
		            //First get the license count
		             String glsql="select nvl(sum(ms.licensecount),0) count from table_privilegegroup pg,table_module md, table_modulelicense ms  where pg.objid='"+groupid+
		            "' and pg.objid=md.module2privilegegroup and md.objid=ms.moduleid";
		            TemplateTable respv= tu.getResultSet(glsql);
		            String groupliccount="0";
		            if (respv.getRowCount()>0)
		              groupliccount=respv.getFieldValue("count",respv.getRowCount()-1);

		              //insert the user license record
		              String sql="insert into table_userlicense(objid,name,LICENSEKEY,LICENSECOUNT,GROUPID,ORIGINID,DESTINITIONID,GENUSER,GENDATE,MODUSER,MODDATE)values("+
		                  objid+",'"+name+"','"+getStringKeyValue( Integer.parseInt( groupliccount))+"',"+groupliccount+","+groupid+","+objid+","+userobjid+
		                  ",'"+adminuser+"',"+(dbtype.equalsIgnoreCase("oracle")? "sysdate":"getdate()")+
		                  ",'"+adminuser+"',"+(dbtype.equalsIgnoreCase("oracle")? "sysdate)":"getdate())");
		              tu.executeQuery(sql);

		              //update user license table for all users associated to the group having same license count as group
		              String ussql="update table_userlicense set licensekey='"+getStringKeyValue( Integer.parseInt( groupliccount))+"',LICENSECOUNT="+groupliccount +
		                " where groupid="+groupid;
		             tu.executeQuery(ussql);

		             //update entity license count
		            String entityselect="select (licensecount-licenseused) licensebalabce from table_entity where objid="+entityid ;
		            TemplateTable entres=tu.getResultSet(entityselect);
		            String esql="";
		            if (entres.getRowCount()>0 &&Integer.parseInt(entres.getFieldValue("licensebalabce",entres.getRowCount()-1))>0){
		              esql="update table_entity e set e.licenseused=(select sum(ul.licensecount) "+
		              " from table_user u, table_userlicense ul where u.user2entity=e.objid and ul.destinitionid=u.objid ) where e.objid="+entityid;
		              tu.executeQuery(esql);
		            }else{
		              //update user record set status=NO_License
		              String usql="update table_user set status=3 where objid="+ userobjid;
		              tu.executeQuery(usql);
		            }

		          }
		        }

		      }
		    }
		  }
		//This method will  return you the license count based on the module
		  private int getLicenseCount(String modulename){
		        if(modulename.equalsIgnoreCase("Admin"))
		          return(LIC_ADMIN_COUNT);
		        else if(modulename.equalsIgnoreCase("Profile"))
		          return(LIC_GCL_COUNT);
		        else if(modulename.equalsIgnoreCase("Setup"))
		          return(LIC_LDL_COUNT);
		        else if(modulename.equalsIgnoreCase("Planning"))
		          return(LIC_TM_COUNT);
		        else if(modulename.equalsIgnoreCase("Execution"))
		          return(LIC_CM_COUNT);
		        else if(modulename.equalsIgnoreCase("Tender"))
		          return(LIC_CRM_COUNT);
		        else if(modulename.equalsIgnoreCase("Bidder"))
		          return(LIC_MRK_COUNT);
		        else if(modulename.equalsIgnoreCase("Budget"))
		          return(LIC_ERP_COUNT);
		        else if(modulename.equalsIgnoreCase("Estimation"))
		          return(LIC_GC_COUNT);
		        else if(modulename.equalsIgnoreCase("Warehouse"))
		            return(LIC_GCL_COUNT);
		      else if(modulename.equalsIgnoreCase("Asset"))
		        return(LIC_LDL_COUNT);
		      else if(modulename.equalsIgnoreCase("Purchase"))
		        return(LIC_TM_COUNT);
		      else if(modulename.equalsIgnoreCase("Maintenance"))
		        return(LIC_CM_COUNT);
		      else if(modulename.equalsIgnoreCase("Report"))
		        return(LIC_CRM_COUNT);     
		       return(1);

		  }

}
