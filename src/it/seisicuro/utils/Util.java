/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.seisicuro.utils;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.Vector;

/**
 *
 * @author PISTONE
 */
public class Util {

    public static String getReport(FileInputStream queryStream) {

        StringBuffer csv = new StringBuffer();
        Vector<String> dataPreventivo = new Vector<String>();
        Vector<String> numeroPreventivi = new Vector<String>();
        Vector<String> numeroPreventiviMoto = new Vector<String>();

        try {
            String query = getQueryFromFile(queryStream);
            Query qobj = new Query(query);
            Vector<Hashtable> res = qobj.execQuery();

            /*for (int i = 0; i < res.size(); i++){
            dataPreventivo.add(res.get(i).get("DATA_SALVATAGGIO").toString());
            numeroPreventivi.add(res.get(i).get("FORZATI").toString());
            }*/

            /*query = query.replace("sei.tab_prev_save_forzati", "sei_moto.tab_prev_save_forzati");
            qobj = new Query(query);
            res = qobj.execQuery();*/

            csv.append("DATA;NUMERO_QUOTAZIONI_RICEVUTE;NUMERO_PREVENTIVI_BESTPRICE;NUMERO_SALVATAGGI_NATURALI;NUMERO_SALVATAGGI_FORZATI;NUMERO_SALVATAGGI_RICALCOLO;FOOTPRINT(%);SALVATAGGI_NATURALI_FALLITI(%);SALVATAGGI_FORZATI_FALLITI(%);SALVATAGGI_FALLITI_RICALCOLO(%);PRODOTTO\n");

            for (int i = 0; i < res.size(); i++) {
                /*csv.append(res.elementAt(i));*/
                csv.append(res.elementAt(i).get("DATA").toString() + ";");             
                csv.append(res.elementAt(i).get("NUMERO_QUOTAZIONI_RICEVUTE").toString() + ";");
                csv.append(res.elementAt(i).get("NUMERO_PREVENTIVI_BESTPRICE").toString() + ";");
                csv.append(res.elementAt(i).get("NUMERO_SALVATAGGI_NATURALI").toString() + ";");
                csv.append(res.elementAt(i).get("NUMERO_SALVATAGGI_FORZATI").toString() + ";");
                csv.append(res.elementAt(i).get("NUMERO_SALVATAGGI_RICALCOLO").toString() + ";");
                csv.append(formatCurrency(res.elementAt(i).get("FOOTPRINT").toString()) + ";");
                csv.append(formatCurrency(res.elementAt(i).get("SALVATAGGI_NATURALI_FALLITI").toString()) + ";");
                csv.append(formatCurrency(res.elementAt(i).get("SALVATAGGI_FORZATI_FALLITI").toString()) + ";");
                csv.append(formatCurrency(res.elementAt(i).get("SALVATAGGI_FALLITI_RICALCOLO").toString()) + ";");
                csv.append(res.elementAt(i).get("PRODOTTO").toString());
                csv.append("\n");
                
            }
            /*numeroPreventiviMoto.add(res.get(i).get("FORZATI").toString());
            }
            
            /*for (int i = 0; i < dataPreventivo.size(); i++) {
            csv.append(dataPreventivo.elementAt(i));
            csv.append(";");
            if (!numeroPreventivi.isEmpty()) {
            csv.append(numeroPreventivi.elementAt(i));
            csv.append(";");
            } else {
            csv.append("0");
            csv.append(";");
            }
            if (!numeroPreventiviMoto.isEmpty()) {
            csv.append(numeroPreventiviMoto.elementAt(i));
            csv.append("\n");
            } else {
            csv.append("0");
            csv.append("\n");
            }
            }*/

        } catch (Exception e) {
            e.printStackTrace();
        }

        return csv.toString();
    }

    public static void createFile(String nomeFile, String csv) throws IOException {

        File f = new File(nomeFile);
        FileOutputStream fout = new FileOutputStream(f);
        PrintStream output = new PrintStream(fout);
        output.print(csv);

        output.flush();
        output.close();
        fout.close();
    }

    public static String getQueryFromFile(FileInputStream queryStream) {

        String query = "";
        try {

            DataInputStream in = new DataInputStream(queryStream);
            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            String app = "";
            while ((app = br.readLine()) != null) {
                query += app + " ";
            }

            in.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return query;

    }

    public static Vector getVectorFromString(String token, String sep) {
        Vector elementi = new Vector();

        StringTokenizer st = new StringTokenizer(token, sep);
        while (st.hasMoreTokens()) {
            elementi.add(st.nextToken());
        }

        return elementi;

    }
    
   public static String formatCurrency(String str){
		String strOut = str;
		double priceDouble = 0.00d;

		try {
			if(!str.equals("")){
				priceDouble = Double.parseDouble(str);
				DecimalFormatSymbols difs = new DecimalFormatSymbols(Locale.ITALIAN);
				DecimalFormat dif = new DecimalFormat("#,###,##0.00", difs);
				strOut = dif.format(priceDouble);
			}
		}catch (Exception ex) {
			ex.printStackTrace();
		}
		return strOut;
	} 
}
