package cms.service.db;



import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicInteger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.jdbc.datasource.DataSourceUtils;

import cms.service.app.ApplicationConstants;
import cms.service.app.PortalController;
import cms.service.app.ServiceController;
import cms.service.jdbc.DriverUtilities;





public class JndiDataSource {

	static Log logger = LogFactory.getLog(JndiDataSource.class);

	/** The configured datasource for this application instance */
	static ConnectionPool connectionPool;
	static DataSource applDataSource;
	static DataSource readOnlyDataSource;
	static String contextpath;
	static Object pool= new Object();

	public static void setContextPath (String  path) {
		contextpath = path;
	}
	public static String getContextPath () {
		if(ServiceController.contextPath!=null){
			return ServiceController.contextPath;
		}
		else if(PortalController.contextPath!=null){
			return PortalController.contextPath;
		}
		return contextpath;
	}
	/** (for dependency-injection) Set the datasource to use for this app instance */
	public static void setDataSource (DataSource ds) {
		applDataSource = ds;
	}

	/** Get this app's configured datasource.  If one has not yet been explicitly configured,
		automatically set using the datasource derived from JNDI. */
	public static DataSource getDataSource ()   {
		if (applDataSource == null)
			setDataSource(getJndiDataSource());
		return applDataSource;
	}

	/** (for dependency-injection) Set the read only datasource to use for this app instance */
	public  void setReadOnlyDataSource (DataSource ds) {
		readOnlyDataSource = ds;
	}

	/** Get this app's read only datasource.  */
	public static DataSource getReadOnlyDataSource ()   {

		return readOnlyDataSource;
	}



	/** Get the JNDI-defined datasource.
	    THIS CORRESPONDS TO THE CURRENT STATIC INIT FOR Database.DATASOURCE 
	 **/
	public static DataSource getJndiDataSource() {
		DataSource ds=null;
		try {

			ds= (DataSource)new InitialContext().lookup(ApplicationConstants.DATASOURCE_NAME);


		} catch (NamingException  e ) {

			if(ds==null){
				logger.info("Using Local Context");
				DriverUtilities du=new DriverUtilities();
				LocalContext ctx;
				try {
					ctx = LocalContextFactory.createLocalContext(du.getDriver());
					ctx.addDataSource(ApplicationConstants.DATASOURCE_NAME,du.getUrl(), du.getUser(), du.getPassword());
					ds = (LocalDataSource) ctx.lookup(ApplicationConstants.DATASOURCE_NAME);
				} catch (LocalDbException | NamingException e1) {
					// TODO Auto-generated catch block
					logger.error("Cannot find datasource: " + ApplicationConstants.DATASOURCE_NAME + " -- " + e);
					throw new IllegalStateException("Cannot find datasource: " + ApplicationConstants.DATASOURCE_NAME, e);
				}


			}

		}
		logger.info("Using JNDI Data Source " + ApplicationConstants.DATASOURCE_NAME );
		return ds;
	}



	public static WrappedConnection getWrappedConnection() throws SQLException {
		return getConnection(getDataSource()) ;
	}

	public static Connection getPoolConnection() throws SQLException {
       
		
		try {

			if(connectionPool==null ){
				
				DriverUtilities du=new DriverUtilities();
				synchronized(pool){
					if(connectionPool==null ){
						connectionPool =new ConnectionPool(du.getDriver(),
								du.getUrl(), 
								du.getUser(), 
								du.getPassword(),
								du.getInititalConnection(),
								du.getMaxConnection(),
								true);
					}
				}
			}

		
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			logger.info("####Creating Connection Pool using local DataSource ####");
			DriverUtilities du=new DriverUtilities();
			connectionPool =new ConnectionPool(du.getDriver(),
					du.getUrl(), 
					du.getUser(), 
					du.getPassword(),
					du.getInititalConnection(),
					du.getMaxConnection(),
					true);
		}


		if(connectionPool!=null){

			return connectionPool.getConnection() ;
		}

		return getWrappedConnection() ;
	}

	public static void closeConnection(Connection con){

		if(connectionPool!=null){
			connectionPool.free(con);
		}else{

			try {

				if(!con.isClosed())
					con.close();

			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
	
	public static void releaseConnectionPool(){
		connectionPool.closeAllConnections();
		connectionPool=null;
	}
	static ThreadLocal<ConnectionHolder> nonTxConnectionHolder = new ThreadLocal<ConnectionHolder>();
	static HashSet<ConnectionHolder> allHolders = new HashSet();
	static AtomicInteger totalLogicalOpenCount = new AtomicInteger();
	static AtomicInteger totalRawOpenCount = new AtomicInteger();

	public static Connection getSpringConnection(){
		return  DataSourceUtils.getConnection(getDataSource());
	}
	public static WrappedConnection getConnection(DataSource dataSource) throws SQLException {
		boolean inTx = TransactionService.isInTransaction();

		Connection conn;
		ConnectionHolder holder = null;

		if (inTx) {
			//logger.info("####Getting Transactional database connection!");
			conn = DataSourceUtils.getConnection(dataSource);
		}
		else {
			holder = nonTxConnectionHolder.get();
			if (holder == null) {
				holder = new ConnectionHolder(dataSource);
				nonTxConnectionHolder.set(holder);
			}
			conn = holder.open();
		}

		// Should never happen, and don't know what to do if it does,
		// but just in case we should at least know about it
		if (conn.isClosed())
			logger.warn("Got a connection that is already closed; inTx=" + inTx);

		int totalCnt = totalLogicalOpenCount.incrementAndGet();
		if (logger.isTraceEnabled())
			logger.trace("On open: logical=" + totalCnt + ", raw=" + totalRawOpenCount.get());

		WrappedConnection wconn = new WrappedConnection(dataSource, conn, holder, inTx);


		return wconn;
	}

	/** WrappedConnection.close() calls this.  Only WrappedConnection should call this. */
	static void closeWrrapedConnection(WrappedConnection wconn) {
		if (wconn.connHolder==null) {
			try {
				DataSourceUtils.releaseConnection(wconn.conn, wconn.dataSource);
			} catch (Exception e) {
				logger.warn("Failure to close wrapped tx connection", e);
			}
		}
		else {
			wconn.connHolder.close();
		}

		int totalCnt = totalLogicalOpenCount.decrementAndGet();
		if (logger.isTraceEnabled())
			logger.trace("On close: logical=" + totalCnt + ", raw=" + totalRawOpenCount.get());
	}


	static long holderNameRover = 0;

	/** We want to maintain a single connection per-thread for non-transaction use.
	 * Instances of ConnectionHolder are set as thread-locals.  These also maintain
	 * open counts so that the underlying connection is removed once all references
	 * are closed.
	 * It is important to note that we do not remove these holders from their thread
	 * even when the connection is closed.  It is possible that the final close that
	 * causes the wrapped connection to close is performed in another thread
	 * as a finalize().  This is rare but possible.  So it is not possible to remove
	 * the holder from the thread because we may be in another thread.  Just leave
	 * the holder there, albeit with the connection closed and removed.  The next
	 * time this thread needs a connection, the holder will be there, ready and
	 * waiting, for a new connection to be made.
	 * 
	 * @author srimanta
	 */
	static class ConnectionHolder {
		final String name;
		final DataSource dataSource;
		Connection conn;
		long lastAction;
		long openTime;
		OpenTracker openDescs = new OpenTracker();

		ConnectionHolder(DataSource dataSource) throws SQLException {
			this.dataSource = dataSource;
			name = Thread.currentThread().getName() + "(" + (holderNameRover++) + ")"; // rover insures uniqueness
		}

		// Note on the synchronizes - these should really only be done on the same thread,
		// since connection holders are meant to be thread-local elements.  The synchronizes
		// are just insurance against something strange happening.

		synchronized Connection open() throws SQLException {
			if (conn == null) {
				conn = dataSource.getConnection();

				totalRawOpenCount.incrementAndGet();
				openTime = System.currentTimeMillis();
				synchronized (allHolders) {
					allHolders.add(this);
				}

				if (!openDescs.isEmpty()) {
					logger.warn("Odd open count when holder had no connection: "+openDescs.size(), new RuntimeException());
					openDescs.clear();
				}
			}
			else if (openDescs.isEmpty()) {
				logger.warn("Connection is already open, but openCount is 0", new RuntimeException());
			}

			lastAction = System.currentTimeMillis();
			openDescs.push(new OpenDesc());

			if (logger.isTraceEnabled())
				logger.trace("Holder up to " + openDescs.size() + "  on " + name);

			return conn;
		}

		synchronized void close() {
			if (openDescs.isEmpty()) {
				logger.warn("Closing connection, but it is already closed");
			}
			else {
				openDescs.pop();
				if (openDescs.isEmpty()) {
					totalRawOpenCount.decrementAndGet();

					try {
						conn.close();
					} catch (SQLException e) {
						logger.warn("Failure to close wrapped non-tx connection", e);
					}
					conn = null;
					openDescs.clear();
					synchronized (allHolders) {
						allHolders.remove(this);
					}
				}
			}

			lastAction = System.currentTimeMillis();
			if (logger.isTraceEnabled())
				logger.trace("Holder down to " + openDescs.size() + "  on " + name);
		}

		@Override
		protected void finalize() throws Throwable {
			if (conn != null) {
				logger.warn("!!! Finalizing a connection holder that still has an open connection - " + name, new RuntimeException());
				try {
					conn.close();
				} catch (SQLException e) {
					logger.warn("Failure to close wrapped non-tx connection IN FINALIZE", e);
				}
				conn = null;
				openDescs = null;
			}
		}

		synchronized void healthCheck() {
			long now = System.currentTimeMillis();
			if (conn != null && lastAction + INACTION_WARNING_INTERNVAL < now) {
				logger.warn("Inactive connection holder: " + name + " for "
						+ (System.currentTimeMillis() - lastAction)/60000 + " minutes" + openDescs.getLogStr());
			}

			if (logger.isDebugEnabled()) {
				logger.debug("Health check on " + name + ": " + " openCount=" + openDescs.size()
						+ ", lastActivity=" + (now-lastAction)/60000 + " minutes ago, openned="
						+ (now-openTime)/60000 + " minutes ago" + openDescs.getLogStr()
						);
			}
		}
	}


	static long WATCH_INTERVAL = 5 * 60 * 1000; // 5 minutes
	static long INACTION_WARNING_INTERNVAL = 5 * 60 * 1000; // 5 minutes
	static final Timer connectionWatcher = new Timer("Cnxt Watcher", true);
	static {
		connectionWatcher.schedule(new ConnectionWatcher(), 0, WATCH_INTERVAL);
	}

	static class ConnectionWatcher extends TimerTask  {

		@Override
		public void run() {
			logger.debug("Watcher is checking connections: logical=" 
					+ totalLogicalOpenCount.get() + ", raw=" + totalRawOpenCount.get());

			ArrayList<ConnectionHolder> holders;
			synchronized (allHolders) {
				holders = new ArrayList(allHolders);
			}

			for (ConnectionHolder holder : holders) {
				holder.healthCheck();
			}
		}
	}

	static class OpenTracker extends Stack<OpenDesc> {
		public String getLogStr() {
			if (isEmpty())
				return "";

			StringBuffer strbuf = new StringBuffer();
			for (int i=size(); i-->0; ) {
				strbuf.append("\n   [").append(i).append("] ").append(get(i));
			}
			return strbuf.toString();
		}
	}

	static class OpenDesc {
		StackTraceElement[] openningStack;

		OpenDesc() {
			StackTraceElement[] stackElems = Thread.currentThread().getStackTrace();
			for (int i=2; i<stackElems.length; i++) {
				String clname = stackElems[i].getClassName();
				if (clname.startsWith("cms."))
					continue;
				if (clname.equals("semantic."))
					continue;
				if (clname.equals("cms.service."))
					continue;

				int captureN = Math.min(3, stackElems.length - i);
				openningStack = new StackTraceElement[captureN];
				System.arraycopy(stackElems, i, openningStack, 0, captureN);
				break;
			}
		}

		@Override
		public String toString() {
			StringBuffer strbuf = new StringBuffer();
			for (StackTraceElement elem : openningStack) {
				if (strbuf.length() > 0)
					strbuf.append("; ");
				strbuf.append(elem.getClassName()).append(".").append(elem.getMethodName())
				.append("()#").append(elem.getLineNumber());
			}
			return strbuf.toString();
		}
	}
}