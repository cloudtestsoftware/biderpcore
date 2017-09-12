package cms.service.event;

import cms.service.template.TemplateTable;

public  class ServiceEvent {
	
	private String parenttable;
	private String childtable;
	private String dbtype;
	private String eventname;
	private String[] column;
	private String[] datatyp;
	private TemplateTable data;
	private int state;
	private int reason;
	private String objid;
	private String relation;
	
	
	public String getParenttable() {
		return parenttable;
	}
	public void setParenttable(String parenttable) {
		this.parenttable = parenttable;
	}
	public String getChildtable() {
		return childtable;
	}
	public void setChildtable(String childtable) {
		this.childtable = childtable;
	}
	public String getDbtype() {
		return dbtype;
	}
	public void setDbtype(String dbtype) {
		this.dbtype = dbtype;
	}
	public String getEventname() {
		return eventname;
	}
	public void setEventname(String eventname) {
		this.eventname = eventname;
	}
	public String[] getColumn() {
		return column;
	}
	public void setColumn(String[] column) {
		this.column = column;
	}
	public String[] getDatatyp() {
		return datatyp;
	}
	public void setDatatyp(String[] datatyp) {
		this.datatyp = datatyp;
	}
	public TemplateTable getData() {
		return data;
	}
	public void setData(TemplateTable data) {
		this.data = data;
	}
	
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	
	public int getReason() {
		return reason;
	}
	public void setReason(int reason) {
		this.reason = reason;
	}
	public String getObjid() {
		return objid;
	}
	public void setObjid(String objid) {
		this.objid = objid;
	}
	public String getRelation() {
		return relation;
	}
	public void setRelation(String relation) {
		this.relation = relation;
	}
	
	
	

}
