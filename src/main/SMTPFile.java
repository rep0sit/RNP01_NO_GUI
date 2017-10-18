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
	private final String SEPARATOR = "################################################################";
	
	
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
			
			writeToLogFile(SEPARATOR);
			
			printAndWriteToLog("S: " + result);
		
			sendCommand("HELO " + hostName);
			
			sendCommand("AUTH PLAIN " + stringToBase64(userName + "\0" + userName + "\0" + password));
			
			

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void sendMail() {

		String boundary = "xyzzy_0123456789_xyzzy";
		
		try {
			
			sendCommand("MAIL FROM: " + userEmail);
			sendCommand("RCPT TO: " + recipient);
			sendCommand("DATA");
			
			printAndWriteToLog("**doing magic MIME stuff to send the attachment**");
		
			// Header
			smtpOut.write("From: " + userEmail + " " + CRLF);
			smtpOut.write("To: " + recipient + " " + CRLF);
			smtpOut.write("Subject: " + "attachment" + " " + CRLF);
			
			
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
			
			
			printAndWriteToLog("**done**");
			
			// Abschliessen
			smtpOut.write(CRLF);
			smtpOut.write("--" + boundary + "--" + CRLF);
			
			smtpOut.write(CRLF + "." + CRLF);
			
			
			
			if(sendCommand("QUIT")) {
				writeToLogFile(SEPARATOR);
			}
		
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
	
	/**
	 * Sends a command to the SMTP server.<br>
	 * 
	 * @param command
	 * @return true if the SMTP server answers, false otherwise
	 * @throws IOException
	 */
	private boolean sendCommand(String command) throws IOException {
		
		String answer = "";
		
		smtpOut.write(command + CRLF);
		smtpOut.flush();
		
		if(!command.startsWith("AUTH PLAIN")) {
			printAndWriteToLog("C: " + command);
		}else {
			printAndWriteToLog("C: " + "AUTH PLAIN [base64 encoded username and password not visible here!]");
		}
		
		answer = smtpIn.readLine();
		printAndWriteToLog("S: " + answer);
		
		return answer.length() > 1;
	}
	
	
	private void printAndWriteToLog(String s) throws IOException {
		System.out.println(s);
		writeToLogFile(s);
	}
	
	
	private void writeToLogFile(String s) throws IOException {
		
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		fw.write(ts.toString() + " ........... "+ s + "\n");
	}
	
	private String stringToBase64(String string) {
		return Base64.getEncoder().encodeToString(string.getBytes());
	}
	
	
	private void closeFile() throws IOException {
		fw.close();
	}
	
}
