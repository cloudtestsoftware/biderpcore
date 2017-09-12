package cms.service.jdbc;

import java.io.File;
import java.io.FileFilter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.springframework.core.io.FileSystemResource;

import cms.service.app.ApplicationConstants;
import cms.service.db.JndiDataSource;
import cms.service.util.FileUtility;



public class DriverUtilities {
  static Log logger = LogFactory.getLog(DriverUtilities.class);
  static HashMap<String,Element> resourceList= new HashMap<String,Element>();
  private  Element resource;

  
public DriverUtilities() {
	 init(null);
}

public DriverUtilities(String lookupname) {
	 init(lookupname);
}
 private void init(String lookupname){
	 String contextpath=JndiDataSource.getContextPath();
	 if(contextpath!=null &&!contextpath.isEmpty()){
		 resource=this.getResource(contextpath,lookupname);
	 }else{
		resource=this.getResource(".",lookupname);
		if(resource==null){
			resource=this.getResource("../.",lookupname);
		}
		
	 }
	 setLog();
	 if(ApplicationConstants.GENERATE_LOG){
		 logger.info("found resource="+resource.asXML());
	 }
 }

  public void setLog() {
		 if(resource!=null &&resource.attributeValue("logger")!=null &&resource.attributeValue("logger").equalsIgnoreCase("true")){
			 ApplicationConstants.GENERATE_LOG=true;
		 }
	}

 
 public String getDriver() {
	 if(resource!=null){
		 return (resource.attributeValue("driverClassName"));
	 }
	return null;
}

public String getUser() {
	 if(resource!=null){
		 return (resource.attributeValue("username"));
	 }
	 return null;
}

public String getPassword() {
	 if(resource!=null){
		 return (resource.attributeValue("password"));
	 }
	 return null;
}

public String getUrl() {
	 if(resource!=null){
		 return (resource.attributeValue("url"));
	 }
	 return null;
}

public int getMaxConnection(){
	if(resource!=null && resource.attributeValue("maxActive")!=null){
		 return (Integer.parseInt( resource.attributeValue("maxActive")));
	 }
	 return 1;
}

public int getMaxIdleConnection(){
	if(resource!=null && resource.attributeValue("maxIdle")!=null){
		 return (Integer.parseInt( resource.attributeValue("maxIdle")));
	 }
	 return 1;
}

public int getInititalConnection(){
	return(1);
}


 private Element getResource(String path,String lookupname) {
	 Element resource=null;
	 File context= new File(path);
	 FileUtility ft=new FileUtility();
	 String context_path="";
	 if(lookupname==null ||lookupname.isEmpty()){
		 lookupname=ApplicationConstants.DATASOURCE_NAME;
	 }
	 if(context.isDirectory()){
		 context_path=context.getAbsolutePath();
		 //logger.info("*****Searching resource file context.xml recursively in path="+context_path);
		 ArrayList<File> configFiles=ft.scanFiles(context_path, "context.xml");
		  for( File source:configFiles){
			  resource=ft.getRootElement(source);
			  if(resource!=null){
				  List<Element> elements=resource.elements();
				  for( Element target:elements){
					  if(target.asXML().contains("driverClassName")){
						  return target;
					  }
				  }
				  if(resource.asXML().contains("driverClassName")){
					  return resource;
				  }
			  }
		  }
		 
	 }else{
		 resource=getResourceContext(path);
	 }
	 logger.info("*****No resource file context.xml is found recursively in path="+context_path);
	 return resource;
 }
 
 private void initResourceList(){
	 if(resourceList.isEmpty()){
		 loadResource("/META-INF");
		 loadResource("/WEB-INF");
	 }
 }
 private Element getResourceContext(String filepath){
	 FileUtility ft=new FileUtility();
	 Element resource=ft.getRootElement(new File(filepath));
	  if(resource!=null){
		  List<Element> elements=resource.elements();
		  for( Element target:elements){
			  if(target.asXML().contains("driverClassName")){
				  return target;
			  }
		  }
		  if(resource.asXML().contains("driverClassName")){
			  return resource;
		  }
	  }
	  return resource;
 }
 
 
 private void loadResource(String resourcetag) {
	 File[]  files=getFileList(resourcetag);
	 FileUtility ft=new FileUtility();
	 for(File source:files){
		 Element resource=ft.getRootElement(source);
		  if(resource!=null){
			  List<Element> elements=resource.elements();
			  for( Element target:elements){
				  String name=target.attributeValue("name");
				  if(name!=null &&!name.isEmpty()){
					  resourceList.put(name, target);
				  }
			  }
		   } 
	   }
	}
 
 
 private File[] getFileList(String resourcetag) {
	    URL url = this.getClass().getClassLoader().getResource(resourcetag);
	    File file = null;
	    
	    FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
               return pathname.isFile() &&pathname.getName().toLowerCase().endsWith(".xml");
            }
         };
	    try {
	        file = new File(url.toURI());
	    } catch (URISyntaxException e) {
	        file = new File(url.getPath());
	    } finally {
	    	 
	        return file.listFiles(filter);
	    }
	}
}