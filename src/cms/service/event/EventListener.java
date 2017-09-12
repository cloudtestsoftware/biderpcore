package cms.service.event;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import cms.service.app.ApplicationConstants;
import cms.service.jdbc.DatabaseTransaction;
import cms.service.template.TemplateTable;
import cms.service.template.TemplateUtility;

/*
 *  The current implementation of this class is synchronous. 
 *  If required Asynchronous event handler we may use Queue and Deque
 */
public class EventListener {
	 	  static Log logger = LogFactory.getLog(EventListener.class);
	      static TemplateUtility tu = new TemplateUtility();
		  static HashMap<String, String> eventResource=new  HashMap<String, String>();
	      static  ApplicationConstants ACONST= new ApplicationConstants();
	      static boolean isInit=false;
			/*
			*Custom method to register Pre Insert of Event
			*Operation will be performed BEFORE the INSERT of Event record
			*add all your custom code like if you want to update any other table or 
			*want to call any subsystem or external interface or database
			 * 
			 * */
			
			 public static boolean registerPreInsertEvent(String parent,TemplateTable pdata){

				 ServiceEvent event=new ServiceEvent();
				 event.setParenttable(parent);
				 event.setDbtype(DatabaseTransaction.getDbType());
				 event.setEventname(parent.toLowerCase()+".pre_insert_event");
				 event.setState(ACONST.EVENT_STATE_BEFORE);
				 event.setReason(ACONST.EVENT_REASON_INSERT);
				 event.setData(pdata);
				 executeEvent(event,parent);
				 
				return(true);

			}
			 
			/*
			*Custom method to register Post Insert of Event
			*Operation will be performed AFTER the INSERT of Event record
			*add all your custom code like if you want to update any other table or
			*want to call any subsystem or external interface or database
			*/
			 
			 public static boolean registerPostInsertEvent(String parent,TemplateTable pdata){

				
				 ServiceEvent event=new ServiceEvent();
				 event.setParenttable(parent);
				 event.setDbtype(DatabaseTransaction.getDbType());
				 event.setEventname(parent.toLowerCase()+".post_insert_event");
				 event.setState(ACONST.EVENT_STATE_AFTER);
				 event.setReason(ACONST.EVENT_REASON_INSERT);
				 event.setData(pdata);
				 executeEvent(event,parent);

				return(true);

			}
			 /*
			*Custom method to register Pre Delete of Event
			*Operation will be performed BEFORE the DELETE of Event record
			*add all your custom code like if you want to update any other table or 
			*want to call any subsystem or external interface or database
			**/
			 
			 public static boolean registerPreDeleteEvent(String parent,String objid){
				
				 ServiceEvent event=new ServiceEvent();
				 event.setParenttable(parent);
				 event.setDbtype(DatabaseTransaction.getDbType());
				 event.setEventname(parent.toLowerCase()+".pre_delete_event");
				 event.setState(ACONST.EVENT_STATE_BEFORE);
				 event.setReason(ACONST.EVENT_REASON_DELETE);
				 event.setObjid(objid);
				 executeEvent(event,parent);

				return(true);

			}
			/*
			*Custom method to register Post Delete of Event
			*Operation will be performed AFTER the DELETE of Event record
			*add all your custom code like if you want to update any other table or
			*want to call any subsystem or external interface or database
			**/
			 
			 public static boolean registerPostDeleteEvent(String parent,String objid){

				
				 ServiceEvent event=new ServiceEvent();
				 event.setParenttable(parent);
				 event.setDbtype(DatabaseTransaction.getDbType());
				 event.setEventname(parent.toLowerCase()+".post_delete_event");
				 event.setState(ACONST.EVENT_STATE_AFTER);
				 event.setReason(ACONST.EVENT_REASON_DELETE);
				 event.setObjid(objid);
				 executeEvent(event,parent);

				return(true);

			}
			/*
			*Custom method to register Pre Select of Event
			*Operation will be performed BEFORE the QUERY of Event record
			*add all your custom code like if you want to update or query any other table or 
			*want to query any subsystem or external interface or database to sink with your current application database
			*such that your query returns the correct data available in the subsystem by updating your current application database
			*/
			 
			 public static boolean registerPreQueryParent(String parent,String[] column,String[] datatype){

				 ServiceEvent event=new ServiceEvent();
				 event.setParenttable(parent);
				 event.setDbtype(DatabaseTransaction.getDbType());
				 event.setEventname(parent.toLowerCase()+".pre_query_parent");
				 event.setState(ACONST.EVENT_STATE_BEFORE);
				 event.setReason(ACONST.EVENT_REASON_QUERY);
				 event.setColumn(column);
				 event.setDatatyp(datatype);
				 executeEvent(event,parent);


				return(true);

			}
			/*
			*Custom method to register Post Select of Event
			*Operation will be performed AFTER the QUERY of Event record
			*add all your custom code like if you want to update or query any other table or
			*want to query any subsystem or external interface or database to sink with your current application database
			*such that your query returns the correct data available in the subsystem by updating your current application database
			*
			*/
			 public static boolean registerPostQueryParent(String parent,String[] column,String[] datatype){
           
				 ServiceEvent event=new ServiceEvent();
				 event.setParenttable(parent);
				 event.setDbtype(DatabaseTransaction.getDbType());
				 event.setEventname(parent.toLowerCase()+".post_query_parent");
				 event.setState(ACONST.EVENT_STATE_AFTER);
				 event.setReason(ACONST.EVENT_REASON_QUERY);
				 event.setColumn(column);
				 event.setDatatyp(datatype);
				 executeEvent(event,parent);

				return(true);

			}
			/*
			*Custom method to register Pre QUERY of Child tables for Event
			*Operation will be performed BEFORE the Query of Child table Event record
			*add all your custom code like if you want to update or query any other table or 
			*want to query any subsystem or external interface or database to sink with your current application database
			*such that your query returns the correct data available in the subsystem by updating your current application database
			*/
			 public static boolean registerPreQueryChild(String parent,String childname,String relfield,String pid,String[] column,String[] datatype){

				 ServiceEvent event=new ServiceEvent();
				 event.setParenttable(parent);
				 event.setDbtype(DatabaseTransaction.getDbType());
				 event.setEventname(parent.toLowerCase()+".pre_query_child");
				 event.setState(ACONST.EVENT_STATE_BEFORE);
				 event.setReason(ACONST.EVENT_REASON_QUERY);
				 event.setColumn(column);
				 event.setDatatyp(datatype);
				 event.setObjid(pid);
				 event.setRelation(relfield);
				 event.setChildtable(childname);
				 executeEvent(event,parent);

				return(true);

			}
			/*
			*Custom method to register Post QUERY of Child object under Event
			*Operation will be performed AFTER the query of Event record
			*add all your custom code like if you want to update or query any other table or
			*want to query any subsystem or external interface or database to sink with your current application database
			*such that your query returns the correct data available in the subsystem by updating your current application database
			*/
			 
			 public static boolean registerPostQueryChild(String parent,String childname,String relfield,String pid,String[] column,String[] datatype){

				 ServiceEvent event=new ServiceEvent();
				 event.setParenttable(parent);
				 event.setDbtype(DatabaseTransaction.getDbType());
				 event.setEventname(parent.toLowerCase()+".post_query_child");
				 event.setState(ACONST.EVENT_STATE_AFTER);
				 event.setReason(ACONST.EVENT_REASON_QUERY);
				 event.setColumn(column);
				 event.setDatatyp(datatype);
				 event.setObjid(pid);
				 event.setRelation(relfield);
				 event.setChildtable(childname);
				 executeEvent(event,parent);
				

				return(true);

			}
			 
			 /*
			    //Custom method for Pre Insert of JobMaster
				//Operation will be performed BEFORE the INSERT of JobMaster record
				//add all your custom code like if you want to update any other table or 
				//want to call any subsystem or external interface or database
				 * */
				 
				 public boolean registerPreInsertChild(String child,TemplateTable cdata){

					 ServiceEvent event=new ServiceEvent();
					 event.setParenttable(child);
					 event.setDbtype(DatabaseTransaction.getDbType());
					 event.setEventname(child.toLowerCase()+".pre_insert_child_event");
					 event.setState(ACONST.EVENT_STATE_BEFORE);
					 event.setReason(ACONST.EVENT_REASON_INSERT);
					 event.setData(cdata);
					 executeEvent(event,child);

					return(true);

				}
               /*
				//Custom method for Post Insert of JobMaster
				//Operation will be performed AFTER the INSERT of JobMaster record
				//add all your custom code like if you want to update any other table or
				//want to call any subsystem or external interface or database
				 * */
				
				 public boolean registerPostInsertChild(String child,TemplateTable cdata){

					 ServiceEvent event=new ServiceEvent();
					 event.setParenttable(child);
					 event.setDbtype(DatabaseTransaction.getDbType());
					 event.setEventname(child.toLowerCase()+".post_insert_child_event");
					 event.setState(ACONST.EVENT_STATE_AFTER);
					 event.setReason(ACONST.EVENT_REASON_INSERT);
					 event.setData(cdata);
					 executeEvent(event,child);

					return(true);

				}
			 private static boolean  executeEvent(ServiceEvent event,String table){
				 
				 String resource=null;
				 
				 if(!isInit){
					 init();
					 resource=eventResource.get(table);
					 isInit=true;
				 }
				 EventHandler handler;
				 
				 if(!tu.isEmptyValue(resource)){
						try {
							handler = (EventHandler)Class.forName(resource).newInstance();
							handler.execute(event);
							 
						} catch (InstantiationException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (IllegalAccessException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ClassNotFoundException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				 }else{
					 if(ACONST.GENERATE_LOG)
						 logger.info("#########Invoking Event="+event.getEventname()+ "for object="+table+ ". Custom Event resource class not configured in database!");
				 }
				return true; 
			 }
			 
			 private static void init(){
				 // inialize resource from database or config file
				 //write your code
			 }

}
