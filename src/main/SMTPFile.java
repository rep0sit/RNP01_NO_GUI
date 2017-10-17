package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.util.Base64;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * 
 * @author Nelli Welker, Etienne Onasch
 *
 */
final class SMTPFile {
	
	//DATA FOR MAIL
	private final String recipient;
	private final String attachment;
	private final String userName;
	private final String userEmail;
	private final String password;
	private final String hostName;
	private final int port;
	
	//STREAMS & READERS & SOCKET
	private SSLSocket smtpSocket;
	private BufferedReader smtpIn;
	private OutputStreamWriter smtpOut;
	
	//LINEBREAK
	private final String CRLF = "\r\n";
	
	//LOGFILE
	private File logFile;
	private FileWriter fw;
	
	
	
	public SMTPFile(String recipient, String attachment, String userName, String userEmail, String password, String hostName, int port) {
		super();
		this.recipient = recipient;
		this.attachment = attachment;
		this.userName = userName;
		this.userEmail = userEmail;
		this.password = password;
		this.hostName = hostName;
		this.port = port;
		
		logFile = new File("logFile.txt");
		try {
			fw = new FileWriter(logFile,true);
		} catch (IOException e) {
			
			System.out.println("Unable to create logfile");
			e.printStackTrace();
		}
		
	}
	
	
	public void sendFile() {
		loginSMTP();
		sendMail();
		quitSMTP();
		
		try {
			closeFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	
	private void loginSMTP() {
		try {
			smtpSocket = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(hostName, port);

			smtpIn = new BufferedReader(new InputStreamReader(smtpSocket.getInputStream()));
			smtpOut = new OutputStreamWriter(smtpSocket.getOutputStream());
			String result = smtpIn.readLine();
			System.out.println("receive ---> " + result);
			// Um sicherzustellen, dass die beiden hosts kommunizieren
			// HELO <SP> <domain> <CRLF>
			sendCommand("HELO " + hostName);
			

			// Base64 encodete Login-Daten
			String userNameEncoded = Base64.getEncoder().encodeToString(userName.getBytes());
			String passwordEncoded = Base64.getEncoder().encodeToString(password.getBytes());
			// AUTH PLAIN
			sendCommand("AUTH PLAIN " + "\0" + userNameEncoded + "\0" + passwordEncoded);
			

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendMail() {

		String boundary = "xyzzy_0123456789_xyzzy";
		//String textTest = "Hello...i am here.";

		try {
			

			
			sendCommand("MAIL FROM: " + userEmail);
			sendCommand("RCPT TO: " + recipient);
			sendCommand("DATA");

			// Header
			smtpOut.write("From: " + userEmail + " " + CRLF);
			smtpOut.write("To: " + recipient + " " + CRLF);
			smtpOut.write("Subject: " + "Test" + " " + CRLF);
			// Body
			smtpOut.write("MIME-Version: 1.0 " + CRLF);
			smtpOut.write("Content-Type: multipart/mixed; boundary= " + boundary + " " + CRLF);
			smtpOut.write(CRLF);
			smtpOut.write("--" + boundary + CRLF);
			// Text der Nachricht
			smtpOut.write("Content-Type: text/plain; charset=ISO-8859-1 " + CRLF);
			smtpOut.write(CRLF);
			smtpOut.write("*ATTACHMENT SENT*" + CRLF);
			smtpOut.write(CRLF);
			smtpOut.write("--" + boundary + CRLF);
			// Datei als Anhang
			String dateiName = attachment.substring(attachment.lastIndexOf("\\") + 1, attachment.length());
			smtpOut.write("Content-Type: application/octet-stream " + CRLF);
			smtpOut.write("Content-Transfer-Encoding: base64 " + CRLF);
			smtpOut.write("Content-Disposition: attachment;" + CRLF + " filename=" + dateiName + CRLF);
			smtpOut.write(CRLF);

			// File kodieren
			File file = new File(attachment);
			String encoded = "";
			@SuppressWarnings("resource")
			FileInputStream fileInputStreamReader = new FileInputStream(file);
			byte[] bytes = new byte[(int) file.length()];
			fileInputStreamReader.read(bytes);
			encoded = Base64.getMimeEncoder().encodeToString(bytes);
			// Senden der kodierten File-Datei
			smtpOut.write(encoded);

			// Abschließen
			smtpOut.write(CRLF);
			smtpOut.write("--" + boundary + "--" + CRLF);
			smtpOut.write(CRLF + "." + CRLF);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void quitSMTP() {
		try {
			smtpIn.close();
			smtpOut.close();
			smtpSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private void sendCommand(String command) throws IOException {
		smtpOut.write(command + CRLF);
		smtpOut.flush();
		System.out.println("send ---> "+ command);
		writeToLogFile(command);
		String result = smtpIn.readLine();
		System.out.println("receive ---> "+ result);	
		writeToLogFile(result);
		
//		if(command.substring(0, 10).equals("AUTH PLAIN")){
//			return (result.substring(0,  3).equals("334"));
//		}
		
		//return (result.substring(0,  3).equals("250"));
	}
	
	private void writeToLogFile(String command) throws IOException {
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		fw.write(ts.toString() + " ---> "+ command + "\n");
	}
	
	private void closeFile() throws IOException {
		fw.close();
	}
	
	
}
