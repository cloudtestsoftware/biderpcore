package cms.service.test;

import cms.service.db.JndiDataSource;
import cms.service.jdbc.DataType;
import cms.service.template.TemplateQuery;

public class TestQuery {

	public static void main(String[] args) {
		
		// TODO Auto-generated method stub
		 JndiDataSource.setContextPath("/home/srimanta/erp/servicecore/src/context.xml");
		 TemplateQuery query= new TemplateQuery();
		 String [] filter={"bqn", "Like", "1234", "name", "Like", "jana", "groupuser", "=", "admin@biderp.com"};
		 String [] column={"objid","quote2phase","quote2messagequeue","name","projectcode","description","bgtrangecode","quotetype","startdate","address","city","zipcode","state","firstname","lastname","phone","phone2","fax","email","othercontact","otherinfo","leadsourcecode","currencycode","pctwaste","pctprofit","status","bidquoteno"};
		 String [] datatype={DataType.VARCHAR,DataType.INTEGER,DataType.INTEGER,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.DATE,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,
				 DataType.VARCHAR,DataType.VARCHAR,DataType.VARCHAR,DataType.NUMBER,DataType.NUMBER,DataType.VARCHAR,DataType.VARCHAR};
		 query.makeSQL("quote", filter, column, datatype);
			

	}

}
