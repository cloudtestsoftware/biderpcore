package cms.service.dhtmlx;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
//import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlMixed;

@XmlAccessorType(XmlAccessType.FIELD)
public class Userdata {
	
	 @XmlAttribute(required=true) 
	String name;
	 
	
	@XmlMixed
	private List<String> text;
	
	public Userdata(String name, List<String> text ){
		this.name=name;
		this.text=text;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public List<String> getText() {
		return text;
	}

	public void setText(List<String> text) {
		this.text = text;
	}
	

}
