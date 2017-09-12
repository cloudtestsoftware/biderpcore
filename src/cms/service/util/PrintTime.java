package cms.service.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import cms.service.app.ApplicationConstants;


public class PrintTime {
	
	private long starttime;
	
	
	public PrintTime(){
		this.starttime=System.currentTimeMillis();
		
	}
	public void reset(){
		starttime=System.currentTimeMillis();
	}
	public String getCurrentDate(){
		return(DateFormat.getDateInstance().format(new Date(System.currentTimeMillis())));
	}
	public long getStartTime(){
		return(this.starttime);
	}
	private long end(){
		return(System.currentTimeMillis());
	}
	public long getTime(){
		return(end()-getStartTime());
	}
	
	public String getPrintTime(){
		return(" ("+getTime()+") in mili Sec ");
	}
	public static String getDateByDeafultFormat(Date date){
		 SimpleDateFormat ft = new SimpleDateFormat (ApplicationConstants.DEFAULT_JAVA_DATE_FORMAT);
		
		 try{	 
			return(ft.format(date));
		 }catch (Exception e){
			 ft=new SimpleDateFormat ("MM/dd/yyyy hh:mm:ss");
			
		 }
		return(ft.format(date));
		
	}
	public String getDateByFormat(long days,String format){
		 SimpleDateFormat ft = null;
		 Date day=new Date(System.currentTimeMillis()+days*24*60*60*1000);  
		 try{
			 if(format!=null){
				 ft=new SimpleDateFormat (format);
			 }else{
				 ft=new SimpleDateFormat ("MM/dd/yyyy hh:mm:ss");
			 }			 
			return(ft.format(day));
		 }catch (Exception e){
			 ft=new SimpleDateFormat ("MM/dd/yyyy hh:mm:ss");
			
		 }
		return(ft.format(day));
	}
  
	public String getTimeInMiliFromHr(long hrs){
		 long time=(System.currentTimeMillis()+hrs*60*60*1000);
		 return(String.valueOf(time)); 
	}
	public String getTimeInMiliFromMiniute(long min){
		 long time=(System.currentTimeMillis()+min*60*1000);
		 return(String.valueOf(time)); 
	}
}
