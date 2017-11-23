package it.seisicuro.utils;

import java.sql.*;
import java.util.*;
import oracle.jdbc.*;

//import org.apache.log4j.Logger;
//import org.apache.tomcat.dbcp.dbcp.DelegatingCallableStatement;

/** 
 * 
 * Classe che si occupa dell'effettuare query sul database
 */
public class Query {

public String ERRMSG = "";
private String query="";
private Connection conn;
private Hashtable valParam;
private String lastInsertId = "";
private Vector params = new Vector();


private Hashtable<Integer,Object> oracleParam;
private Hashtable<Integer,Integer> oracleTypes;

//private Logger logger = Logger.getRootLogger();


/** 
 * Costruttore della classe con definizione della query da effettuare
 * 
 * @param query Query SQL da effettuare
 */
public Query(String query){
	this.query = query;
}

/** 
 * Costruttore della classe con definizione della query da effettuare e dei relativi parametri
 * 
 * @param query Query SQL da effettuare
 * @param valParam Parametri da passare alla query
 */
public Query(String query,Hashtable valParam){
	this.query = query;
	this.valParam = valParam;
}

	public Query(String query, Vector params) {
		this.query = query;
		this.params = params;
	}

/** 
 * Esegue query DML (update, insert, delete).
 * 
 * @return int SQLCode risultato dall'operazione (0 --> OK)
 */
public int execUpdate(){
	int ris = 0;
	conn = ConnectionDB.getConnection();
	try
	{
		ris = execUpdate(conn);
		if (ris == 0)
			conn.commit();
	}
	catch(SQLException e)
	{
		//logger.error(e);
                System.out.println(e);
		try
		{
			ris=-1;
			conn.rollback();
		}
		catch(SQLException ex)
		{
			//logger.error(ex);
                        System.out.println(ex);
		}
	}
	/*finally{
		ConnectionDB.freeConnection();
	}*/
	
return ris;
}

/** 
 * Esegue query DML (update, insert, delete) utilizzando la connessione specificata.
 * 
 * @param connection Connessione da utilizzare.
 * @return int SQLCode risultato dall'operazione (0 --> OK)
 */
public int execUpdate(Connection connection) {
		int ris = 0;
		PreparedStatement stat;
		try {
			conn = connection;
			conn.setAutoCommit(false);
			stat = conn.prepareStatement(getQuery());
			setParams(stat);
			stat.executeUpdate();
			stat.close();
		} catch (Exception e) {
			ERRMSG = e.getMessage();
			try {
				conn.rollback();
			} catch (SQLException ex) {
			}
			ris = -1;
		}
		return ris;
	}

public String getLastInsertId()
{
	return lastInsertId;
} 
/** 
 * Esegue query DML (update, insert, delete).
 * 
 * @return int SQLCode risultato dall'operazione (0 --> OK)
 */
public Vector execQuery(){
	Vector ris = new Vector();
	conn = ConnectionDB.getConnection();
	try 
	{
		ris = execQuery(conn);
	}
	catch (Exception ex) 
	{
		//logger.error(ex);
                System.out.println(ex.getMessage());
	}
	/*finally
	{
		ConnectionDB.freeConnection(conn);
	}*/
return ris;
}

/** 
 * Esegue query di interrogazione (select).
 * @return Vector Vettore contenente Hashtable costituite da entry del tipo NOME CAMPO --> VALORE e rappresentano i risultati della query
 */
public Vector execQuery(Connection connection){
	Vector result = new Vector();
	try
	{
		conn = connection;
		conn.setAutoCommit(false);
		PreparedStatement stat = conn.prepareStatement(getQuery());
		ResultSet rs = stat.executeQuery();
		ResultSetMetaData rsmd = rs.getMetaData();
		int numberOfColumns = rsmd.getColumnCount();
		int i;
		while(rs.next())
		{
			i = 1;
			Hashtable row = new Hashtable();
			while (i <= numberOfColumns){
				String column_name = rs.getMetaData().getColumnName(i);
				Object value = rs.getString(i);
				if(value == null)
					value = "";
				row.put(column_name.toUpperCase(),value);
				i++;
			}
			result.addElement(row);
		}
		rs.close();
		stat.close();
	}
	catch(SQLException e){
                e.printStackTrace();
		//logger.error(e);
		//logger.error(getQuery());
                System.out.println(getQuery());
	}
return result;
}


public Vector execPackage(int outparameter)
{
	Vector ris = null;
	conn = ConnectionDB.getConnection();
        
	try 
	{
		ris = execPackage(conn,outparameter);
	}
	catch (Exception ex) 
	{
		//logger.error(ex);
                System.out.println(ex.getMessage());
	}
	/*finally
	{
		ConnectionDB.freeConnection();
	}*/
return ris;	
}

public Vector execPackage(Connection connection,int outparameter){

	Vector rs = new Vector();
	try{
		conn = connection;
		conn.setAutoCommit(false);
                OracleCallableStatement stat =  (OracleCallableStatement)connection.prepareCall(getQuery());

		stat.registerOutParameter(1,outparameter);
		setOracleStatement(stat,conn);
		
		stat.execute();
		rs.add(stat.getString(1));
		stat.close();
	}
	catch(SQLException e){
		//logger.error(e);
		//logger.error(getQuery());
                System.out.println(e.getMessage());
                System.out.println(getQuery());
	}
return rs;
}

/** Setta la query da eseguire (SQL).
* @param query Query SQL da eseguire
*/
public void setQuery (String query){
	this.query = query;
}

/** Ritorna la query SQL da eseguire.
* @return String Query SQL da eseguire.
*/
public String getQuery (){
	return query;
}

/** Setta i parametri di ingresso alla query.
* @param valParam Hashtable con i valori dei parametri
*/
public void setParamValues (Hashtable valParam){
	this.valParam = valParam;
}

/** Ritorna i parametri di ingresso alla query.
* @return Hashtable Query Valori dei parametri passati alla query SQL
*/
public Hashtable getParamValues (){
	return this.valParam;
}

	public Hashtable<Integer,Object> getOracleParam() {
		return oracleParam;
	}

	public void setOracleParam(Hashtable oracleParam) {
		this.oracleParam = oracleParam;
	}

	public Hashtable<Integer,Integer> getOracleTypes() {
		return oracleTypes;
	}

	public void setOracleTypes(Hashtable oracleTypes) {
		this.oracleTypes = oracleTypes;
	}

	
	private void setOracleStatement(OracleCallableStatement stat,Connection conn)
	{
		try {
			Enumeration<Integer> en = getOracleParam().keys();
			while(en.hasMoreElements())
			{
				int key = en.nextElement().intValue();
				int type = getOracleTypes().get(key).intValue();
				Object value = getOracleParam().get(key);

				
				if(type==oracle.jdbc.OracleTypes.NUMBER)
				{
					stat.setInt(key,((Integer)value).intValue());
				}
				else if(type==oracle.jdbc.OracleTypes.VARCHAR)
				{
					stat.setString(key,((String)value));
					
				}else if(type==oracle.jdbc.OracleTypes.DATE){
					
					stat.setDate(key, (java.sql.Date)value);
				}else if(type==oracle.jdbc.OracleTypes.DOUBLE)
				{
					stat.setDouble(key,(Double)value);
				}else if(type==oracle.jdbc.OracleTypes.PLSQL_INDEX_TABLE)
				{
					/*ArrayDescriptor ar = ArrayDescriptor.createDescriptor("SYS.OWA.VC_ARR", conn);
					ARRAY a = new ARRAY(ar,conn,value);
					stat.setARRAY(key, a);
					 * */
					//oracle.jdbc.OracleTypes.PLSQL_INDEX_TABLE
					String[] v = (String[])value;
					stat.setPlsqlIndexTable(key, value, v.length, v.length, oracle.jdbc.OracleTypes.VARCHAR, 32000);


				}

			}			
		} 
		catch (Exception e) 
		{
			//logger.error(e);
                        System.out.println(e.getMessage());
		}
	}
        
	private void setParams(PreparedStatement st) throws SQLException{
		for (int i = 0; i < params.size(); i++) {
			Object o = params.get(i);
			if(o instanceof String)
				st.setString(i+1, o.toString());
			else
				st.setInt(i+1, (Integer)i);			
		}
	}
        

}
