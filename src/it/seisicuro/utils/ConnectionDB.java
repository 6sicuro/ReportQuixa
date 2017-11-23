/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.seisicuro.utils;

/**
 *
 * @author PISTONE
 */

import databeans2.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConnectionDB {

    private static final String DEF_DRIVERNAME = "oracle.jdbc.driver.OracleDriver";
    private static final String DEF_DBURL = "jdbc:oracle:oci8:@";
    private static final String DEF_DBUSER = "";
    private static final String DEF_DBPWD = "";
    private static final String DEF_MAXCONN = "20";
    private static final String DEF_MINCONN = "10";

    private static Connection m_cd = null;
    private static String m_strDbUrl = "";
    private static String strPwd = "";
    private static String strUser = "";
    private static String strDriverName = "";

	/** Creates a new instance of ConnectionDB */
    public ConnectionDB() {}

    public static void loadParam(Properties props) {

        try {

            strDriverName = props.getProperty("seisicuro.jdbc_driver", DEF_DRIVERNAME);
            strPwd = props.getProperty("seisicuro.db_pwd", DEF_DBPWD);
            strUser = props.getProperty("seisicuro.db_user", DEF_DBUSER);
            m_strDbUrl = props.getProperty("seisicuro.db_url", DEF_DBURL);
            m_cd = DriverManager.getConnection(m_strDbUrl, strUser, strPwd);

            System.out.println("START_0 parameters:\ndrivername: " + strDriverName +
                                "\nusername: " + strUser + "\npassword: ***\ndb url: " + m_strDbUrl );

        } catch(Exception ex2) {
            System.out.println (ex2.getMessage()+" - ECCEZIONE GENERICA DURANTE IL BINDING DI resourceconnectionmonitor");
	}
    }
    
    public static void loadParamLocale(Properties props) {

        try {

            strDriverName = props.getProperty("seisicuro.jdbc_driver", DEF_DRIVERNAME);
            strPwd = props.getProperty("seisicuro.db_pwd", DEF_DBPWD);
            strUser = props.getProperty("seisicuro.db_user", DEF_DBUSER);
            m_strDbUrl = props.getProperty("seisicuro.db_url_locale", DEF_DBURL);
            m_cd = DriverManager.getConnection(m_strDbUrl, strUser, strPwd);

            System.out.println("START_0 parameters:\ndrivername: " + strDriverName +
                                "\nusername: " + strUser + "\npassword: ***\ndb url: " + m_strDbUrl );

        } catch(Exception ex2) {
            System.out.println (ex2.getMessage()+" - ECCEZIONE GENERICA DURANTE IL BINDING DI resourceconnectionmonitor");
	}
    }

	/**
	*
	*	Metodo richiamato dalle altre classi per prendere una connessione per effettuare
	*	operazioni sul db.
	*
	*	@return		conn	Connessione
	*	@version	1.0
	*	@throws		SQLException
	**/

	public static Connection getConnection() {
        // connessione da restituire
             try {

                if (m_cd == null) {
                    Class.forName(strDriverName);
                    m_cd = DriverManager.getConnection(m_strDbUrl, strUser, strPwd);
                 }
                
        } catch(Exception ex2) {
            System.out.println (ex2+" - ECCEZIONE GENERICA DURANTE l'apertura della connessione");
	}

            return m_cd;
	}


	/**
	*
	*	Una volta effettuata l'operazione sul db con la connessione precedentemente presa,
	*	questo metodo rilascia la connessione utilizzata.
	*
	*	@param		conn	Connessione utilizzata per l'operazione sul db
	*	@version	1.0
	*	@throws		SQLException,Exception	eccezione
	*/

	public static void freeConnection(){

		try{
                        if (m_cd != null){
			//chiudo la connessione
                            m_cd.close();
                            System.out.println("connessione rilasciata");
                        }

		}catch (SQLException ex){

			System.out.println (ex.getMessage()+" - IMPOSSIBILE RILASCIARE LA CONNESSIONE!");

		}catch(Exception e){

			System.out.println(e.getMessage()+" - IMPOSSIBILE RILASCIARE LA CONNESSIONE!");

		}

	}
}
