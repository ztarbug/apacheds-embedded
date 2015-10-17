package de.starwit.auth.apacheds;

/**
 * Holds OS specific kill command.
 * 
 * @author Markus Zarbock
 *
 */
public enum OperatingSystems {
	WINDOWS ("taskkill /F /PID"),
	LINUX("kill -9"), 
	NOT_SUPPORTED("");
	
	private String osCommand;
	
	OperatingSystems(String command) {
		this.osCommand = command;
	}
	
	public String getOsCommand() {
		return osCommand;
	}
}
