package cms.service.util;

import java.io.File;
import java.io.FileFilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;

import cms.service.app.ApplicationConstants;
import cms.service.app.ServiceController;
import cms.service.jdbc.DriverUtilities;

public class ResourceUtility {
	
	static Log logger = LogFactory.getLog(DriverUtilities.class);
	static HashMap<String,Element> resourceList= new HashMap<String,Element>();
	private String resourcepath;
	 
	 
	 public ResourceUtility(String resourcepath) {
		 if(resourcepath==null){
			 resourcepath="";
		 }
		 this.resourcepath=this.getRelativeResourcePath(resourcepath);
		 if(resourceList.isEmpty()){
			 loadResource(this.getRelativeResourcePath("META-INF"));
			 loadResource(this.getRelativeResourcePath("USER-INF"));
			 loadResource(this.getRelativeResourcePath("WEB-INF"));
		 }
	 }
	 public static String getResourceAttribute(String resourcename, String attributename){
		 Element res=getResourceElement(resourcename);
		 if(res!=null){
			 return res.attributeValue(attributename);
		 }
		 return null;
	 }
	
	 public static Element getResourceElement(String resourcename){
		 if(resourceList.isEmpty()){
			 new ResourceUtility(".");
		 }
		 return resourceList.get(resourcename);
	 }
	 
	 public static Element getResourceElement(String resourcename, String resourcepath){
		
		 if(resourceList.isEmpty()){
			 new ResourceUtility(resourcepath);
		 }
		 return resourceList.get(resourcename);
	 }
	 
	 
	 private File[] getFileList(String filepath) {
		 	File file=new File(filepath);
		    FileFilter filter = new FileFilter() {
	            @Override
	            public boolean accept(File matchfile) {
	            	if(ApplicationConstants.GENERATE_LOG){
	            		logger.info("*****Matching file="+matchfile.getAbsolutePath());
	            	}
	            	return matchfile.isFile() &&matchfile.getName().toLowerCase().endsWith(".xml");
	            }
	         };
		    
	         if(file!=null){
	        	 return file.listFiles(filter);
	         }
	         logger.info("*****No Matching file found in file path="+filepath);
	         return null;
		   
		}
	 
	 private String getDirResource(){
		
			if(this.resourcepath==null)
				return(".");
			else return resourcepath;

		}
	
	 

	/*
	 * Params:
	 * @path : resource path like current dir="." of the jar or "../. . Use relative path against the jar path
	 * @name : name of the resource
	 */
			 
	 public Element getResource(String path,String resname) {
		 Element resource=null;
		 File context= new File(path);
		 FileUtility ft=new FileUtility();
		 String context_path="";
		 if(context.isDirectory()){
			 context_path=context.getAbsolutePath().replace("/.", "/");
			 if(ApplicationConstants.GENERATE_LOG){
				 logger.info("*****Searching resource file context.xml recursively in path="+context_path);
			 }
			 ArrayList<File> configFiles=ft.scanFiles(context_path, "context.xml");
			  for( File source:configFiles){
				  resource=ft.getRootElement(source);
				  if(resource!=null){
					  List<Element> elements=resource.elements();
					  for( Element target:elements){
						  if(target.attributeValue("name")!=null && target.attributeValue("name").contains(resname)){
							  return target;
						  }
					  }
					  if(resource.asXML().contains(resname)){
						  return resource;
					  }
				  }
			  }
			 
		 }else{
			 resource=getResourceContextByName(path,resname);
		 }
		 logger.info("*****No resource file context.xml is found recursively in path="+context_path);
		 return resource;
	 }
	 
	 
	 private  String getRelativeResourcePath(String relative_path){
		
		 return ServiceController.contextPath.split("WEB-INF")[0]+relative_path;
	 }
	 
	 /*
		 * Params:
		 * @path : resource path like current dir="." of the jar or "../. . Use relative path against the jar path
		 * @name : name of the resource
		 */
				 
		 public static Element getUserResourceElement(String resname) {
			 Element resource=null;
			 if(!resourceList.isEmpty() &&resourceList.get(resname)!=null){
				 return resourceList.get(resname);
			 }
			 resource=new ResourceUtility(null).getUserResourceElement(resname);
			/*
			 String path=new ResourceUtility(null).getRelativeResourcePath(null);
			 
			 File context= new File(path);
			 FileUtility ft=new FileUtility();
			 String context_path="";
			 if(context.isDirectory()){
				 context_path=context.getAbsolutePath().replace("/.", "/");
				 logger.info("*****Searching resource file context.xml recursively in path="+context_path);
				 ArrayList<File> configFiles=ft.scanFiles(context_path, ".xml");
				  for( File source:configFiles){
					  resource=ft.getRootElement(source);
					  if(resource!=null){
						  List<Element> elements=resource.elements();
						  for( Element target:elements){
							  if(target.attributeValue("name")!=null && target.attributeValue("name").contains(resname)){
								  resourceList.put(resname, target);
								  return target;
							  }
						  }
						  if(resource.asXML().contains(resname)){
							  resourceList.put(resname, resource);
							  return resource;
						  }
					  }
				  }
				 
			 }
			 */
			 
			 return  resource;
		 }
	 
	 private Element getResourceContextByName(String filepath, String resname){
		 FileUtility ft=new FileUtility();
		 Element resource=ft.getRootElement(new File(filepath));
		  if(resource!=null){
			  List<Element> elements=resource.elements();
			  for( Element target:elements){
				  if(target.asXML().contains(resname)){
					  return target;
				  }
			  }
			  if(resource.asXML().contains(resname)){
				  return resource;
			  }
		  }
		  return resource;
	 }
	 
	 
	 private void loadResource(String resourcedir) {
		 try{
			 File[]  files=getFileList(resourcedir);
			 FileUtility ft=new FileUtility();
			 for(File source:files){
				if(ApplicationConstants.GENERATE_LOG){
					logger.info("*****Scaning context file="+source.getAbsolutePath());
				}
				 
				 Element resource=ft.getRootElement(source);
				  if(resource!=null){
					  List<Element> elements=resource.elements();
					  for( Element target:elements){
						  String name=target.attributeValue("name");
						  if(name!=null &&!name.isEmpty()){
							 if(ApplicationConstants.GENERATE_LOG){
							  logger.info("*****Added Resource Element Name="+name+ " and XML="+target.asXML());
							 }
							  resourceList.put(name, target);
						  }
					  }
				   } 
			   }
			 }catch (NullPointerException e){
				 logger.info("*****NullPointerException for resourcedir="+resourcedir);
				
			 }catch (Exception e){
				 logger.info("*****No resource found for resourcedir="+resourcedir);
				 
			 }
		}
	 
	 
	

}
