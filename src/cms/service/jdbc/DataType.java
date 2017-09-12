package cms.service.jdbc;

import java.sql.*;


/** Diffrent datatype:<BR>
 *
 *
 * @author S.K.jana
 * @version $Id: DataType.java,v 1.1 2010/06/19 05:53:59 cvs Exp $
 * @since JDK 1.2.1
 * @since JSDK 2.0
 */

public class DataType {


 public static final String BIT = Integer.toString(java.sql.Types.BIT);
 public static final String TINYINT = Integer.toString(java.sql.Types.TINYINT);
 public static final String SMALLINT = Integer.toString(java.sql.Types.SMALLINT);
 public static final String INTEGER = Integer.toString(java.sql.Types.INTEGER);
 public static final String BIGINT = Integer.toString(java.sql.Types.BIGINT);
 public static final String FLOAT = Integer.toString(java.sql.Types.FLOAT);
 public static final String REAL = Integer.toString(java.sql.Types.REAL);
 public static final String DOUBLE = Integer.toString(java.sql.Types.DOUBLE);
 public static final String NUMERIC = Integer.toString(java.sql.Types.NUMERIC);
 public static final String NUMBER = Integer.toString(java.sql.Types.NUMERIC);
 public static final String DECIMAL = Integer.toString(java.sql.Types.DECIMAL);
 public static final String CLOB = Integer.toString(java.sql.Types.CLOB);
 public static final String BLOB = Integer.toString(java.sql.Types.BLOB);
 public static final String CHAR = Integer.toString(java.sql.Types.CHAR);
 public static final String RAW = Integer.toString(java.sql.Types.VARCHAR);
 public static final String VARCHAR = Integer.toString(java.sql.Types.VARCHAR);
 public static final String LONGVARCHAR = Integer.toString(java.sql.Types.LONGVARCHAR);
 public static final String DATE = Integer.toString(java.sql.Types.DATE);
 public static final String TIME = Integer.toString(java.sql.Types.TIME);
 public static final String TIMESTAMP = Integer.toString(java.sql.Types.TIMESTAMP);
 public static final String BINARY = Integer.toString(java.sql.Types.BINARY);
 public static final String VARBINARY = Integer.toString(java.sql.Types.VARBINARY);
 public static final String LONGVARBINARY = Integer.toString(java.sql.Types.LONGVARBINARY);
 public static final String NULL = Integer.toString(java.sql.Types.NULL);
 public static final String OTHER = Integer.toString(java.sql.Types.OTHER);


}
