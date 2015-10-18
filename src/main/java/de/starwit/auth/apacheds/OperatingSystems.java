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
	
	public static OperatingSystems getOSType() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf("win") >= 0) {
			return OperatingSystems.WINDOWS;
		}
		if (os.indexOf("nux") >= 0 || os.indexOf("nix") != 0) {
			return OperatingSystems.LINUX;
		}		
		
		return OperatingSystems.NOT_SUPPORTED;
	}
	
	public static String extractPID(String processInfo) {
		String pid = processInfo.substring(0, processInfo.indexOf("@"));
		return pid;
	}
}
