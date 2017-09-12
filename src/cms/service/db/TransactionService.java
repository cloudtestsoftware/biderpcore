package cms.service.db;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;


public class TransactionService {
	static Log logger = LogFactory.getLog(TransactionService.class);

	/** The configured transaction manager for this application instance */
	static PlatformTransactionManager pTxMgr;

	/** The configured default transaction definition for this application instance */
	static TransactionDefinition pTxDef = new DefaultTransactionDefinition();

	/** (for dependency-injection) Set the transaction manager for this app instance */
	public static void setTxManager(PlatformTransactionManager platformTxMgr) {
		pTxMgr = platformTxMgr;
	}	

	/** Get this app's configured transaction manager. */
	public static PlatformTransactionManager getTxManager() {
		if (pTxMgr == null) {
			// A reasonable default for apps that did not explicitly set a transaction manager
			pTxMgr = new DataSourceTransactionManager();
			((DataSourceTransactionManager)pTxMgr).setDataSource(JndiDataSource.getDataSource());
		}
		
		return pTxMgr;
	}

	/** (for dependency-injection) Set the default trans-def for this app instance */
	public static void setTxDef(TransactionDefinition definition) {
		pTxDef = definition;
	}	

	/** Get this app's configured default transaction definition. */
	public static TransactionDefinition getTxDef() {
		return pTxDef;
	}

	// Keep a count of open transactions as our own double-check of transaction status.
	static ThreadLocal<Integer> txCount = new ThreadLocal<Integer>();

	public static boolean isInTransaction() {
		return txCount.get() != null;
	}
	
	/** Begin a transaction. */
	public static TransactionStatus beginTransaction() throws TransactionException {
		TransactionStatus status = getTxManager().getTransaction(pTxDef);
		
		Integer txc = txCount.get();
		txCount.set((txc==null) ? 1 : txc+1);
		
		return status;
	}

	/** End the current transaction by committing updates */
	public static void commitCurrent(TransactionStatus status) throws TransactionException {
		if (status==null)
			throw new IllegalStateException("No transaction is active");

		getTxManager().commit(status);
		decCount(status);
	}

	/** End the current transaction by rolling back updates */
	public static void rollbackCurrent(TransactionStatus status) throws TransactionException {
		if (status==null)
			throw new IllegalStateException("No transaction is active");

		getTxManager().rollback(status);
		decCount(status);
	}

	static void decCount(TransactionStatus status) {
		Integer txc = txCount.get();
		if (txc == null)
			logger.warn("TX count is off - too many ends");
		else {
			int dec = txc-1;
			if (dec <= 0)
				txCount.remove();
			else
				txCount.set(dec);
			
			if (status.isNewTransaction() != (dec==0))
				logger.warn("TX count is off while closing - cnt=" + dec + ", isNew=" + status.isNewTransaction());
		}
	}
}