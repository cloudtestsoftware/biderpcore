package cms.service.dhtmlx;

import java.util.ArrayList;
import java.util.List;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement

public class Rows {
	
	@XmlAttribute(required=false) 
	private String parent;
	
	private List<Row> row;
    private ArrayList<Userdata> userdata= new ArrayList<Userdata>();
	private Head head;
	
	
	
	public Rows() {
	}

	public Rows(List<Row> row,Head head) {
	
		this.row = row;
		this.head=head;
	}
	
	public List<Row> getRow() {
		return row;
	}

	public void setRow(List<Row> row) {
		this.row = row;
	}

	public Head getHead() {
		return head;
	}

	public void setHead(Head head) {
		this.head = head;
	}

	public ArrayList<Userdata> getUserdata() {
		return userdata;
	}

	public void setUserdata(ArrayList<Userdata> userdata) {
		this.userdata = userdata;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}
	
	
	

}
