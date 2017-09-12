package cms.service.dhtmlx;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement
public class Head {
	
	private List<Column> column;
	private Setting setting;
	
	private List<Userdata> userdata;
	
	

	
	
	public Head() {
		
	}

   public Head(List<Column> column) {
		
		this.column = column;
		
	}
	public Head(List<Column> column, Setting setting) {
		
		this.column = column;
		this.setting = setting;
	}

	public List<Column> getColumn() {
		return column;
	}

	public void setColumn(List<Column> column) {
		this.column = column;
	}
	
	public List<Userdata> getUserdata() {
		return userdata;
	}

	public void setUserdata(List<Userdata> userdata) {
		this.userdata = userdata;
	}

	public Setting getSetting() {
		return setting;
	}

	public void setSetting(Setting setting) {
		this.setting = setting;
	}



}
