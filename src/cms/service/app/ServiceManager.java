package cms.service.app;

import java.util.*;
import java.io.*;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import cms.service.dhtmlx.Option;
import cms.service.template.TemplateTable;
import cms.service.template.TemplateUtility;
import cms.service.util.Base64Util;
import cms.service.util.FileUtility;
import cms.service.util.PrintTime;


/**
 * Interface for Managing object
 *
 *
 * @author S.K.jana
 * @version :
 * 
 */

public class ServiceManager {
	    static Log logger = LogFactory.getLog(ServiceManager.class);
	   
	    private static TemplateUtility tu=new TemplateUtility();
	    private static ApplicationConstants ACONST = new ApplicationConstants();
        static LinkedHashMap<String, AccessToken> userstoken=new LinkedHashMap<String, AccessToken>();
        static LinkedHashMap<String, String> groupusertoken=new LinkedHashMap<String, String>();
        public ServiceManager(){
        }
        /*
        // This method verifies the login information for each request the user makes to the server
        // based on Ip address and user's login id
        // when the user login first time a token no is created and the user holds the token during its session
        // If the session timeout is reached the token is expired automatically
        //
        // If this method retuns True that means the user login in first time,
        // once the user logs in always the method will return false
        */
        public static AccessToken verifyLogin(String username,String password, String clientIp){
        	
            return(new ServiceManager().verifyUser( username, password,clientIp));
        }
        
        /*
         *  tokenno which will be send to any specific client will have grouptoken+"@"+clientIp
         *  
         *  @param:
         *  
         *   tokenno= grouptoken+"@"+clientIp which is available using auth service call
         */
      
        public static AccessToken getAccessTokenByGroupUser(String groupuser){
        	
        	return userstoken.get(groupusertoken.get(groupuser));
        }
        
        public static HashMap<String,ArrayList<Option>>  getCustomProperty(String table,String groupuser){
        	AccessToken token=userstoken.get(groupusertoken.get(groupuser));
        	ArrayList<Option> options= new ArrayList<Option>();
        	HashMap<String,ArrayList<Option>> result= new  HashMap<String,ArrayList<Option>>();
        	String currenttable=null;
        	String currentattribute=null;
        	if(token!=null){
        		TemplateTable cpros= token.getCustomprop();
        		
        		 if(cpros != null)
                 {
                     for(int n = 0; n < cpros.getRowCount(); n++){
                    	 if(cpros.getFieldValue("tablename", n).equalsIgnoreCase(table)){
                    		 currenttable=table;
                    		 //logger.info("getCustomProperty table="+currenttable);
                    		 		
                    		 if(currentattribute==null||!currentattribute.equals(cpros.getFieldValue("attributename", n))){
                    			 if(currentattribute!=null){
                    				 result.put(currentattribute.toLowerCase(), options);
                    				 options=null;
                    				 currentattribute=null;
                    				 //logger.info("getCustomProperty attr="+ currentattribute.toLowerCase() +"added option="+options.toArray());
                    			 }
                    			 options= new ArrayList<Option>();
                    			 currentattribute=cpros.getFieldValue("attributename", n);
                    			 //logger.info("getCustomProperty attr assigned="+ currentattribute.toLowerCase() +"added option="+options.toArray());
                         		
                    		 }
                    		 
	                         if(cpros.getFieldValue("attributename", n).equalsIgnoreCase(currentattribute)){
	
	                     		ArrayList<String> optionstr= new ArrayList<String>();
	                     		optionstr.add(cpros.getFieldValue("PropertyString", n));
	                     		options.add(new Option(cpros.getFieldValue("PropertyValue", n),optionstr));
	                         }
                    	 }
                    	 if(currenttable!=null &&currenttable!=table){
                    		 result.put(currentattribute.toLowerCase(), options);
                    		 options= null;
                    		 //logger.info("getCustomProperty attr="+ currentattribute.toLowerCase() +"added option="+options.toArray());
                    		 break; 
                    		 
                    	 }
                            
                     }
                    
                 }
        		 if(options!=null && currentattribute!=null){
        			 result.put(currentattribute.toLowerCase(), options);
        		 }
        	}
        	return result;
        }
        
        public static TemplateTable getCustomGenericCode(String groupuser){
        	AccessToken token=userstoken.get(groupusertoken.get(groupuser));
        	if(token!=null){
        		return token.getCustomcode();
        	}
        	return null;
        }
        
        public static HashMap<String,String> verifyUserToken(String tokenno){
        	HashMap<String,String> userdata=null;
        	String dcriptToken=null;
        	String clientIp="";
        	String username="";
        	String[] tokens=tokenno.split(ACONST.IPSEPERATOR);
        	String grouptoken=tokens[0].trim();
        	if(tokens.length>1){
        		userdata= new HashMap<String,String>();
        		clientIp=tokens[1];
        		username=tokens[2];
        		userdata.put("username", username);
        		userdata.put("clientip", clientIp);
        	}
			try {
				//logger.info("Encripted Token="+grouptoken );
				dcriptToken=new String(Base64Util.decode(grouptoken.getBytes()));
      	    	
				//dcriptToken =EncriptManager.decrypt(grouptoken.trim(),key);
				//logger.info("Decripted Token="+dcriptToken);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	
        	String groupuser=dcriptToken;
        
        	if(!tu.isEmptyValue(dcriptToken)){
        		String [] vals=dcriptToken.split(";");
        		 if(vals.length>1){
        			 groupuser=vals[vals.length-1];
        			 userdata.put("groupuser", groupuser);
             	}
             	
        	}
        	
        	AccessToken token=userstoken.get(grouptoken.trim());
        	PrintTime pt= new PrintTime();
        	String verifygrouptoken=groupusertoken.get(groupuser);
        	String user=username+"@"+clientIp;
        	if(token!=null &&!tu.isEmptyValue(verifygrouptoken) 
        			&&verifygrouptoken.equals(grouptoken.trim()) &&token.getLoginusers().contains(user)){
        		
        		userdata.put("fullname",ServiceManager.getUserFullname(username));
	        	long expiry=token.getTokenexpiry();
	        	
	        	if(expiry>pt.getStartTime()){
	        		
	        		token.setTokenexpiry(Long.parseLong(pt.getTimeInMiliFromMiniute(ACONST.LOGIN_EXPIRED_IN_MINUTE)));
	        		return userdata;
	        	}else{
	        		groupusertoken.remove(groupuser);
	        		userstoken.remove(grouptoken);
	        		logger.info("Removed Expired Groupuser token for Groupuser="+groupuser+ " group tokenno="+grouptoken);
	        	}
        	}
            
            return(null);
        }
        
       
        
        public AccessToken verifyUser(String username, String password, String clientIp){
        	
        	PrintTime pt= new PrintTime();
        	AccessToken token=null;
      	  //verify Privilege Group status later
      		  String sql="select pg.Objid, pg.Name,pg.Status,su.name firstname,su.verifypassword,su.groupuser,su.Status \"userstate\" from "+
      	              "Table_PrivilegeGroup pg,Table_User su where su.User2PrivilegeGroup=pg.ObjId and su.loginName='"+username+"' and "+
      	              "su.Password='"+password+"' and pg.Status='1' and su.Status='1' ";
      		  
      		  String mqsql=" select mq.objid from table_messagequeue mq, table_user u where u.objid=mq.messagequeue2user "+
      				  	 " and u.loginname='"+username+"'";

      	    if(!tu.isEmptyValue(username)&&!tu.isEmptyValue(password)){
      	        TemplateTable result=tu.getResultSet(sql);
      	        TemplateTable mq=tu.getResultSet(mqsql);
      	        if(result!=null && result.getRowCount()>0){	
      	        	String tokenno=groupusertoken.get(result.getFieldValue("groupuser", result.getRowCount()-1));
      	        	String user=username+"@"+clientIp;
      	        	if(!tu.isEmptyValue(tokenno)){
      	        		token=userstoken.get(tokenno);
      	        		token.setFirstname(result.getFieldValue("firstname", result.getRowCount()-1));
      	        		token.setLastname(result.getFieldValue("lastname", result.getRowCount()-1));
      	        		String loginusers=token.getLoginusers();
      	        		if(!tu.isEmptyValue(loginusers) && !loginusers.contains(user)){
      	        			token.setLoginusers(loginusers+";"+user);
      	        		}else if(tu.isEmptyValue(loginusers)){
      	        			token.setLoginusers(user);
      	        		}
      	        		token.setTokenexpiry(Long.parseLong(pt.getTimeInMiliFromMiniute(ACONST.LOGIN_EXPIRED_IN_MINUTE)));
  	        			userstoken.put(tokenno, token);
      	        	}
      	        	
      	        	if(token==null){
      	        		token =new AccessToken();
      	        	    
      	        	    try {
      	        	    	String tokenval=pt.getStartTime()+";"+result.getFieldValue("groupuser", result.getRowCount()-1);
      	        	    	tokenno=new String(Base64Util.encode(tokenval.getBytes()));
      	        	    	//tokenno=EncriptManager.encrypt(result.getFieldValue("groupuser", result.getRowCount()-1), key);
							token.setToken(tokenno);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
      	        	    token.setGroupuser(result.getFieldValue("groupuser", result.getRowCount()-1));
      	        		token.setFirstname(result.getFieldValue("firstname", result.getRowCount()-1));
      	        		token.setLastname(result.getFieldValue("lastname", result.getRowCount()-1));
      	        	    token.setModules(getUserModules(username));
      	        	    if(mq!=null && mq.getRowCount()>0){
      	        	    	token.setMessageque(mq.getFieldValue("objid", mq.getRowCount()-1));
      	        	    }
        	        	token.setLoginusers(user);
        	        	token.setTokenexpiry(Long.parseLong(pt.getTimeInMiliFromMiniute(ACONST.LOGIN_EXPIRED_IN_MINUTE)));
        	        	userstoken.put(tokenno, token);
        	        	groupusertoken.put(result.getFieldValue("groupuser", result.getRowCount()-1), tokenno);
        	        	logger.info("userstoken="+userstoken.toString());
        	        	logger.info("groupusertoken="+groupusertoken.toString());
        	        	if(!password.equalsIgnoreCase(result.getFieldValue("verifypassword",0))){
            	        	  tu.executeQuery("update table_user set password='"+
            	        			  result.getFieldValue("verifypassword",0)+"' where loginname='"+username+"'");
            	          }
      	        	}
      	         
      	        //re initialize custom property and code in every time user logins
      	        	TemplateTable ccode=this.getCustomCustomCodeByLogin(result.getFieldValue("groupuser", result.getRowCount()-1));
      	        	TemplateTable cprop=this.getCustomPropertyByLogin( result.getFieldValue("groupuser", result.getRowCount()-1));
      	        	if(ccode!=null &&token!=null){
      	        		token.setCustomcode(ccode);
      	        		token.setCustomprop(cprop);
      	        	}
      	          return(token);
      	        }
      	    }
      	    return(token);
      	  }
        
        public static String getUserModules(String loginname){
     	   /*if(privilege!=null && !tu.isEmptyValue(privilege.get(privid))){
     		   return privilege.get(privid);
     	   }
     	   */
     	   String modules="";
     	   String sql="select da.TableName,da.Name,sp.PropertyString,'app_'||lower(sp.PropertyValue) PropertyValue,sp.Scope " +
    				",sp.PropIndex,sp.GenUser from table_Attribute da,table_ListProperty sp "+
    				" where upper(da.TableName)='MODULE' and sp.ListProperty2Attribute = da.objid  and upper(da.name)=upper('name')"+
    				" and not exists (select * from table_module m,table_user u where upper(m.name)=upper(sp.PropertyValue) and m.module2privilegegroup =u.user2privilegegroup and u.loginname='"+loginname+"')"+
    				" order by da.TableName, da.name,sp.propindex";
     	   
     	   TemplateTable result=tu.getResultSet(sql);
     	   for(int i=0;i<result.getRowCount();i++){
     		   modules+=result.getFieldValue("PropertyValue", i)+",";
     	   }
     	   //privilege.put(privid, modules);
     	   return modules;
        }
        public static String getUserFullname(String loginname){
        	 TemplateTable result=tu.getResultSet("select name || ' '||lastname as fullname from table_user where loginname='"+loginname+"'" );
        	 if(result!=null && result.getRowCount()>0){
        		 return result.getFieldValue("fullname", result.getRowCount()-1);
        	 }
        	 return "";
        }
        public static String getUserCurrency(String groupuser){
      	  
      	   String currency="";
      	   String sql="select name||':'||country||':'||symbol||':'||conversion||':'||decode(type,'1','default','conversion') as currency from table_currency where groupuser='"+groupuser+"'";
      	   
      	   TemplateTable result=tu.getResultSet(sql);
      	   for(int i=0;i<result.getRowCount();i++){
      		 currency+=result.getFieldValue("currency", i)+",";
      	   }
      	   //privilege.put(privid, modules);
      	   return currency;
         }
        
        public TemplateTable getCustomPropertyByLogin(String groupuser){

    		String sql="select da.TableName,da.Name AttributeName,sp.PropertyString,sp.PropertyValue,sp.Scope " +
    				",sp.PropIndex,sp.GenUser from table_Attribute da,table_CustomList sp "+
    				" where sp.CustomList2Attribute = da.objid "+
    				" and sp.groupuser='"+groupuser+"'"+
    				" order by da.TableName, da.name,sp.propindex";
    		TemplateTable result=tu.getResultSet(sql);
    		if(result!=null){
    			return(result);
    		}

    		return(null);
    	}
        
        public TemplateTable getCustomCustomCodeByLogin(String groupuser){

    		
    		String sql="select ca.name as attributename,c.name, c.codevalue from table_codeattribute ca,table_customcode c "+
    				" where c.customcode2codeattribute =ca.objid and c.groupuser='"+groupuser+"'";
    		TemplateTable result=tu.getResultSet(sql);
    		if(result!=null){
    			return(result);
    		}

    		return(null);
    	}
        public boolean installLicense(String path, String serverip,String dbtype){
            InstallLicense lm=new InstallLicense();
            FileUtility fu=new FileUtility();
          try{
            BufferedReader bf =fu.readInputFile(path);
            Vector vf=fu.parseInputXmlFile(bf,"licensekey");
            lm.installLicenseKey(fu.parseInputValue(vf,"company"),serverip,fu.parseInputValue(vf,"licensekey"),fu.parseInputValue(vf,"custauthcode"),dbtype);
          } catch (IOException e){
              e.printStackTrace();
              logger.error("IOException encountered: " + e.getMessage() + "\n");
            } catch (Exception e){
              e.printStackTrace();
              logger.error("Exception encountered: " + e.getMessage() + "\n");
            }
          return(true);
        }
        
        public boolean isLicenseExpired(){
          String sql="select round((sysdate-gendate)) usedate,expirykey,wbsno from table_installlicense";
          String wsql="select * from table_projectcode";
          TemplateUtility tu=new TemplateUtility();
          InstallLicense lm = new InstallLicense();
          TemplateTable result=tu.getResultSet(sql);
          TemplateTable wbsresult=tu.getResultSet(wsql);
          if (result.getRowCount()==0)
            return(true);
          for (int i=0;i<result.getRowCount();i++){
            String texpday=result.getFieldValue("usedate",i);
            String expirykey=result.getFieldValue("expirykey",i);
            if(ACONST.GENERATE_LOG)
              logger.info("\nusedate="+texpday+" expirykey="+ expirykey);
            int availday=lm.getCountFromKeyValue(expirykey)-Integer.parseInt(texpday);
            //if(wbsresult.getRowCount()>Integer.parseInt(result.getFieldValue("wbsno",i))|| availday<0)
            // Currently disable the project code count which is wbsno
            if(availday<0)
                return(true);
            }
            return(false);
        }
        
}
