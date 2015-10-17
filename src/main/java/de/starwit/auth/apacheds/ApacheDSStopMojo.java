package de.starwit.auth.apacheds;

import java.io.IOException;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "stop")
public class ApacheDSStopMojo extends AbstractMojo {
	
	@Parameter(property = "apacheds.pid")
	private String pid;

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("executing " + getOSName().getOsCommand() + pid);
		
		try {
			Runtime.getRuntime().exec(getOSName().getOsCommand() + " " + pid);
		} catch (IOException e) {
			System.out.println("Couldn't kill apacheds process... " + e.getMessage());
		}
	}
	
	private OperatingSystems getOSName() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.indexOf("win") >= 0) {
			return OperatingSystems.WINDOWS;
		}
		if (os.indexOf("nux") >= 0 || os.indexOf("nix") != 0) {
			return OperatingSystems.LINUX;
		}		
		
		return OperatingSystems.NOT_SUPPORTED;
	}

}
