package main;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Properties;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
/**
 * 
 * @author Nelli Welker, Etienne Onasch
 *
 */
final class Utils {
	private Utils() {}
	
	//Strings for reading properties file
	public static final String MAIL_ADDRESS = "mailaddress";
	public static final String USER_NAME = "username";
	public static final String HOST_NAME = "hostname";
	public static final String PORT = "port";
	
	//Location and name of properties file
	private static final String CONFIG_FILE = "config.properties";

	public static String readPassword(){
		
		
		String password = "";
		
		JPanel panel = new JPanel();
		
		JLabel label = new JLabel("Enter a password for the email in "+ CONFIG_FILE +":");
		JPasswordField pass = new JPasswordField(10);
		
		
		panel.add(label);
		panel.add(pass);
		String[] options = new String[]{"OK", "Cancel"};
		int option = JOptionPane.showOptionDialog(null, panel, "Security",
		                         JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE,
		                         null, options, options[1]);
		if(option == 0) {
		    char[] cp = pass.getPassword();
		    password = new String(cp);
		    
		}
		
		return password;
	}
	
	
	public static Properties getProps() {
		Properties props = new Properties();

		
		FileInputStream inputStr = null;
		try {
			inputStr = new FileInputStream(CONFIG_FILE);
			// Laden des Properties File
			props.load(inputStr);
		} catch (IOException io) {
			io.printStackTrace();
		} finally {
			if (inputStr != null) {
				try {
					inputStr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return props;
	}
	
	/**
	 * Returns the first three digits of a positive SMPT Server answer to 
	 * a SMTP command.
	 * @param command 
	 * @return
	 */
	public static String positiveAnswerFirst3digits(String command) {
		
		if (command.startsWith("HELO") || 
				command.startsWith("MAIL FROM") || 
				command.startsWith("RCPT TO") || 
				command.startsWith("QUIT")) {
			return "250";
		}else if(command.startsWith("AUTH PLAIN")) {
			return "235";
		}else if(command.startsWith("DATA")) {
			return "354";
		}else {
			return "";
		}
		
		
	}
	

	
	
	
}
