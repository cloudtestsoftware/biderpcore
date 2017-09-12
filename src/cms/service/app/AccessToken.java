package cms.service.app;

import cms.service.template.TemplateTable;

public class AccessToken {
	
	private String groupuser;
	private String loginusers;
	private String remoteips;
	private String token;
	private long tokenexpiry;
	private String messageque;
	private String modules;
	private String firstname;
	private String lastname;
	
	
	public String getFullname() {
		return firstname+" "+lastname;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		return lastname;
	}
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	TemplateTable customprop;
	TemplateTable customcode;
	
	public TemplateTable getCustomprop() {
		return customprop;
	}
	public void setCustomprop(TemplateTable customprop) {
		this.customprop = customprop;
	}
	public TemplateTable getCustomcode() {
		return customcode;
	}
	public void setCustomcode(TemplateTable customcode) {
		this.customcode = customcode;
	}
	public String getModules() {
		return modules;
	}
	public void setModules(String modules) {
		this.modules = modules;
	}
	public String getMessageque() {
		return messageque;
	}
	public void setMessageque(String messageque) {
		this.messageque = messageque;
	}
	public String getGroupuser() {
		return groupuser;
	}
	public void setGroupuser(String groupuser) {
		this.groupuser = groupuser;
	}
	public String getLoginusers() {
		return loginusers;
	}
	public void setLoginusers(String loginusers) {
		this.loginusers = loginusers;
	}
	public String getRemoteips() {
		return remoteips;
	}
	public void setRemoteips(String remoteips) {
		this.remoteips = remoteips;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public long getTokenexpiry() {
		return tokenexpiry;
	}
	public void setTokenexpiry(long tokenexpiry) {
		this.tokenexpiry = tokenexpiry;
	}
		
	
	

}
