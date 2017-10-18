package main;

import java.io.IOException;
import java.util.Properties;
/**
 * 
 * @author Nelli Welker, Etienne Onasch
 *
 */
public class MailFile {
	
	
	public static void main(String...args) throws IOException {
		if(args.length != 2) {
			System.out.println("You must enter 2 Parameters: email and attachment");
		}else {
			Properties props = Utils.getProps();
			
			String recipient = args[0];
			String attachment = args[1];
			
			
			String userName = props.getProperty(Utils.USER_NAME);
			String userEmail = props.getProperty(Utils.MAIL_ADDRESS);
			
			
			String password = Utils.readPassword();
			String hostName = props.getProperty(Utils.HOST_NAME);
			int port = Integer.parseInt(props.getProperty(Utils.PORT));
		
			
			SMTPFile smtpFile = new SMTPFile(recipient, attachment, userName, userEmail, password, hostName, port);
		    
			smtpFile.sendFile();
		}
	}
}
