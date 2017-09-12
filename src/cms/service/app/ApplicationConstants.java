package cms.service.app;

public class ApplicationConstants {
    //log setting
	public static boolean GENERATE_LOG = false;
	public static final String IPSEPERATOR=";";
	public static final long LOGIN_EXPIRED_IN_MINUTE=60;
	//Data source
	public static final String DATASOURCE_NAME = "java:comp/env/jdbc/SoftleanDs";
	public static final int MAX_SIGNATURE_COUNT = 500;
	public static final String DEFAULT_DATE_FORMAT = "mm/dd/yyyy";
	public static final String DEFAULT_JAVA_DATE_FORMAT = "MM/dd/yyyy";
	public static final int NO_PRIMARY_KEY = -1;
	public static final String STATUS_CLOSED="100";
	public static final String STATUS_UNDECIDED="50";
	public static final String STATUS_UNASSIGN="0";
	public static final int MAX_TABLE_COUNT = 500;
	public static final String MIN_PARTION_LIMIT = "1";
	public static final String MAX_PARTION_LIMIT = "99999999999";
	public static final int MAX_NUM_ROWS=200;
	
	//Constant for firing events
    public static final int EVENT_REASON_INSERT=1;
    public static final int EVENT_REASON_DELETE=2;
    public static final int EVENT_REASON_QUERY=3;
    public static final int EVENT_STATE_AFTER=1;
    public static final int EVENT_STATE_BEFORE=2;


    //Bulk Delete mode

    public static final int BULK_DELETE_MODE_REMOVE_RELATION=1;
    public static final int BULK_DELETE_MODE_DELETE_RECORD=2;


}
