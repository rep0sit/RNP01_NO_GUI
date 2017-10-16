package main;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

import jline.console.ConsoleReader;
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
	private static final String CONFIG_NAME = "config.txt";
	
	private static final String CONFIG_PATH = CONFIG_NAME;
	
	public static String readPassWordFromConsole() throws IOException {
		
	    System.out.println("Enter password for the email address in " + CONFIG_NAME + ":");
	    String password = new ConsoleReader().readLine(new Character('*'));
	    return password;
	}
	
	/**
	 * 
	 * @return
	 */
	public static Properties getProps() {
		Properties props = new Properties();

		
		FileInputStream inputStr = null;
		try {
			inputStr = new FileInputStream(CONFIG_PATH);
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
}
