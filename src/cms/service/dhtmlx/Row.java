package cms.service.dhtmlx;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Row {
	
	 @XmlAttribute(required=true) 
	 private String id;
	 
	 @XmlAttribute(required=false) 
	 private String open;
	 
	 @XmlAttribute(required=false) 
	 private String xmlkids;
	 
	 private List<String> cell;
	 
	 private List<Row> row;
	 
	 
	public Row() {
	}

	public Row(String id, List<String> cell) {
		
		this.id = id;
		this.cell = cell;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<String> getCell() {
		return cell;
	}
	public void setCell(List<String> cell) {
		this.cell = cell;
	}

	public String getOpen() {
		return open;
	}

	public void setOpen(String open) {
		this.open = open;
	}

	public String getXmlkids() {
		return xmlkids;
	}

	public void setXmlkids(String xmlkids) {
		this.xmlkids = xmlkids;
	}

	public List<Row> getRow() {
		return row;
	}

	public void setRow(List<Row> row) {
		this.row = row;
	}
	
	
	
	
}
