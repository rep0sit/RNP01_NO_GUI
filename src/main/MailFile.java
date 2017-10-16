package main;

import java.io.IOException;
import java.util.Properties;

public class MailFile {
	
	
	public static void main(String...args) throws IOException {
		if(args.length != 2) {
			System.out.println("You must enter 2 Parameters: email and attachement");
		}else {
			Properties props = Utils.getProps();
			
			String recipient = args[0];
			String attachement = args[1];
			
			
			String userName = props.getProperty(Utils.USER_NAME);
			String userEmail = props.getProperty(Utils.MAIL_ADDRESS);
			String hostName = props.getProperty(Utils.HOST_NAME);
			int port = Integer.parseInt(props.getProperty(Utils.PORT));
			String password = Utils.readPassWordFromConsole();
			
			SMTP.send(recipient, attachement, userName, userEmail, hostName, port, password);
			
			
			
			
		    
			
		}
	}
}
