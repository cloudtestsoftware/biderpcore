package cms.service.gen;


public abstract  class BeanGenService {
	

	public abstract void  createServiceBean(String strBean, String strBeanpath);
	public abstract void  createImplBean(String strBean, String strBeanpath);
	public abstract void  createDaoBean(String strBean, String strBeanpath);
	public abstract void  createOracleSpInsert(String strBean, String strBeanpath);
	public abstract void  createOracleSpUpdate(String strBean, String strBeanpath);
	public abstract void  createObjectRule(String strBean, String strBeanpath);
	public abstract void setAppName (String appName);

}
