package cms.service.dhtmlx;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlMixed;



@XmlAccessorType(XmlAccessType.FIELD)

public class Option {
	 @XmlAttribute(required=true) 
	private String value;
	 
	@XmlElementRef(name = "head", type =Head.class)
	@XmlMixed
	private List<String> text;
	
	
	public Option() {
		
	}
	public Option(String text) {
		
	}

	public Option(String value, List<String> text) {
		
		this.value = value;
		this.text = text;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public List<String> getText() {
		return text;
	}

	public void setText(List<String> text) {
		this.text = text;
	}
	
	

}
