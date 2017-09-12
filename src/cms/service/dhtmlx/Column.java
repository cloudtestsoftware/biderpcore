package cms.service.dhtmlx;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlMixed;





@XmlAccessorType(XmlAccessType.FIELD)

public class Column {
	
	 @XmlAttribute(required=true) 
	private String type;
	 @XmlAttribute(required=true) 
	private String align;
	 @XmlAttribute(required=true) 
	private String sort;
	 @XmlAttribute(required=true) 
	private String width;
	 @XmlAttribute(required=true) 
	private String id;
	
	 @XmlAttribute(required=false) 
	private String format;
	 
	 @XmlAttribute(required=false) 
	private String source;
	 
	 @XmlAttribute(required=false) 
	 private String auto;
	 
	 @XmlAttribute(required=false) 
	 private String cache;
	 
	 @XmlAttribute(required=false) 
	 private String editable;

	@XmlElementRef(name = "head", type = Head.class)
	@XmlMixed
	private List<String> value;

	private List<Option> option;
	
	

	
    
	public String getEditable() {
		return editable;
	}



	public void setEditable(String editable) {
		this.editable = editable;
	}



	public String getSource() {
		return source;
	}



	public void setSource(String source) {
		this.source = source;
	}



	public String getAuto() {
		return auto;
	}



	public void setAuto(String auto) {
		this.auto = auto;
	}



	public String getCache() {
		return cache;
	}



	public void setCache(String cache) {
		this.cache = cache;
	}



	public String getFormat() {
		return format;
	}



	public void setFormat(String format) {
		this.format = format;
	}



	public String getId() {
		return id;
	}



	public void setId(String id) {
		this.id = id;
	}



	public List<String> getValue() {
		return value;
	}



	public void setValue(List<String> value) {
		this.value = value;
	}



	
	public List<Option> getOption() {
		return option;
	}



	public void setOption(List<Option> option) {
		this.option = option;
	}



	
	
	public String getWidth() {
		return width;
	}



	public void setWidth(String width) {
		this.width = width;
	}



	public String getType() {
		return type;
	}



	public void setType(String type) {
		this.type = type;
	}



	public String getAlign() {
		return align;
	}



	public void setAlign(String align) {
		this.align = align;
	}



	public String getSort() {
		return sort;
	}



	public void setSort(String sort) {
		this.sort = sort;
	}



	
	public Column(String value) {
	
	}
	public Column(List<Option> option) {
		this.option=option;
	}
	
	
	public Column() {
		
	}

	

	public Column(String format, String id,String type, String align, String sort, String width,List<String> value,
			List<Option> options) {
		this.id=id;
		this.format=format;
		this.type = type;
		this.align = align;
		this.sort = sort;
		this.width = width;
		this.value = value;
		this.option = option;
	}



	public Column(String format,String id,String width, String type, String align, String sort,
			List<String> value) {
		this.format=format;
		this.id=id;
		this.width = width;
		this.type = type;
		this.align = align;
		this.sort = sort;
		this.value = value;
	}
	
	
	
	

}
