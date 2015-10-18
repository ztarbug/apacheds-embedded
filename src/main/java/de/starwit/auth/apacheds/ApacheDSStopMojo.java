package de.starwit.auth.apacheds;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "stop")
public class ApacheDSStopMojo extends AbstractMojo {
	
	@Parameter(property = "apacheds.pidFileLocation")
	private String pidFileLocation;

	public void execute() throws MojoExecutionException, MojoFailureException {
		if (pidFileLocation == null) {
			throw new MojoExecutionException("pid file location not provided - exit.");
		}
		
		String pid = getPidFromFile(pidFileLocation);
		OperatingSystems currentOS = OperatingSystems.getOSType();
		
		try {
			Runtime.getRuntime().exec(currentOS.getOsCommand() + " " + pid);
		} catch (IOException e) {
			throw new MojoExecutionException("Couldn't kill apacheds process... " + e.getMessage());
		}
	}
	
	private String getPidFromFile(String pidFilePath) throws MojoExecutionException {
		
		File pidFile = new File(pidFilePath);
		try {
			String pid = new String(Files.readAllBytes(pidFile.toPath()),"UTF-8");
			getLog().info("Killing apacheds instance with pid " + pid);
			return pid;
		} catch (IOException e1) {
			throw new MojoExecutionException("Couldn't read pidfile - can't shutdown instance. Exit.");
		}
	}
}
