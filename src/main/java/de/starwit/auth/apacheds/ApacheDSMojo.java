package de.starwit.auth.apacheds;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "start")
public class ApacheDSMojo extends AbstractMojo {
	
	@Parameter(property = "apacheds.pathtoldiffile")
	private String pathToLdifFile;
	
	@Parameter(property = "apacheds.instanceFolder")
	private String instanceFolder;
	
	@Parameter(property = "apacheds.pidFileLocation")
	private String pidFileLocation;

	public void execute() throws MojoExecutionException, MojoFailureException {
		getLog().info("starting apacheds instance ...");
		DirectoryRunner dr = new DirectoryRunner(instanceFolder, pathToLdifFile, getLog());
		
		File pidFile = new File(pidFileLocation);
		try {
			FileWriter out = new FileWriter(pidFile, false);
			out.write(ManagementFactory.getRuntimeMXBean().getName());
			out.close();
		} catch (IOException e) {
			System.out.println("Could not write to pid file " + e.getMessage());
			throw new MojoExecutionException("Directory could not be started");
		}
		
		try {
			dr.runDirectory();
			while(true) {
				//loop forever...
				Thread.sleep(1000);
			}
		} catch (Exception e) {
			getLog().error("Directory could not be started " + e.getMessage());
			throw new MojoExecutionException("Directory could not be started");
		}
	}
}
