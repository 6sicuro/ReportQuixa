/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.seisicuro.report;

import it.seisicuro.utils.ConnectionDB;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import it.seisicuro.utils.Util;
import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Vector;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author PISTONE
 */
public class ReportQuixa {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        FileInputStream instream = null;
        FileInputStream queryStream = null;
        Properties props = new Properties();

        String domain = "";
        String hostSmtp = "";
        String from = "";
        String subject = "";
        String mailMessage = "";

        Date dataOggi = new Date();
        GregorianCalendar dataAttuale = new GregorianCalendar();
        dataAttuale.setTime(dataOggi);
        SimpleDateFormat sdf = new SimpleDateFormat("dd_MM_yyyy");
        String nomeFile = "report_quixa_";
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM");

        try {
            instream = new FileInputStream("conf/reportQuixa.properties");
            props.load(instream);
            instream.close();
            ConnectionDB.loadParam(props);

            queryStream = new FileInputStream(props.getProperty("queryReportQuixa"));

            String passaggi = Util.getReport(queryStream);

            ConnectionDB.freeConnection();
            domain = props.getProperty("MAIL_DOMAIN");
            hostSmtp = props.getProperty("MAIL_SMTP");
            from = props.getProperty("MAIL_SENDER");
            String percorso = props.getProperty("percorso");

            dataAttuale.add(Calendar.DAY_OF_MONTH, -7);
            subject = "Report quixa dal " + sdf1.format(dataAttuale.getTime());
            mailMessage = "Buongiorno,<br/> in allegato il report dal " + sdf1.format(dataAttuale.getTime());
            nomeFile += sdf.format(dataAttuale.getTime());

            dataAttuale.add(Calendar.DAY_OF_MONTH, +6);

            subject += " al " + sdf1.format(dataAttuale.getTime());
            mailMessage += " al " + sdf1.format(dataAttuale.getTime());
            mailMessage += ".<br/><br/>Cordiali Saluti.";

            nomeFile += "_" + sdf.format(dataAttuale.getTime()) + ".csv";

            Util.createFile(percorso + nomeFile, passaggi);

            Vector addrTo = Util.getVectorFromString(props.getProperty("MAIL_TO"), ";");
            Vector addrCCn = Util.getVectorFromString(props.getProperty("MAIL_CCN"), ";");
            Vector addrCC = Util.getVectorFromString(props.getProperty("MAIL_CC"), ";");

            InternetAddress[] addressTo = new InternetAddress[addrTo.size()];
            InternetAddress[] addressCCN = new InternetAddress[addrCCn.size()];
            InternetAddress[] addressCC = new InternetAddress[addrCC.size()];

            for (int i = 0; i < addrTo.size(); i++) {
                addressTo[i] = new InternetAddress((String) addrTo.elementAt(i));
            }

            Properties properties = new Properties();
            properties.put("mail.smtp.host", hostSmtp);
            javax.mail.Session sessionMail = javax.mail.Session.getDefaultInstance(properties, null);
            sessionMail.setDebug(true);

            Message message = new MimeMessage(sessionMail);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(Message.RecipientType.TO, addressTo);

            if (addrCCn.size() > 0) {
                for (int i = 0; i < addrCCn.size(); i++) {
                    addressCCN[i] = new InternetAddress((String) addrCCn.elementAt(i));
                }
                message.setRecipients(Message.RecipientType.BCC, addressCCN);
            }

            if (addrCC.size() > 0) {
                for (int i = 0; i < addrCC.size(); i++) {
                    addressCC[i] = new InternetAddress((String) addrCC.elementAt(i));
                }
                message.setRecipients(Message.RecipientType.CC, addressCC);
            }

            message.setSubject(subject);

            message.setContent(mailMessage, "text/html");
            MimeBodyPart messagePart = new MimeBodyPart();

            messagePart.setContent(mailMessage, "text/html");
            Multipart multipart = new MimeMultipart();

            MimeBodyPart attachmentPart = new MimeBodyPart();
            File output = new File(percorso + nomeFile);
            FileDataSource fileDataSource = new FileDataSource(output.getAbsoluteFile());

            attachmentPart.setDataHandler(new DataHandler(fileDataSource));
            attachmentPart.setFileName(nomeFile);

            multipart.addBodyPart(attachmentPart);

            multipart.addBodyPart(messagePart);
            message.setContent(multipart);

            Transport.send(message);

        } catch (Exception e) {
            System.out.println("Eccezione nell'elaborazione...");
            e.printStackTrace();

        } finally {
            ConnectionDB.freeConnection();
        }

    }
}
