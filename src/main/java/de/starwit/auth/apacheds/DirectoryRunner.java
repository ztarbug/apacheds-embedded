package de.starwit.auth.apacheds;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.directory.api.ldap.model.exception.LdapException;
import org.apache.directory.api.ldap.model.exception.LdapInvalidDnException;
import org.apache.directory.api.ldap.model.name.Dn;
import org.apache.directory.api.ldap.model.schema.SchemaManager;
import org.apache.directory.api.ldap.model.schema.registries.SchemaLoader;
import org.apache.directory.api.ldap.schema.extractor.SchemaLdifExtractor;
import org.apache.directory.api.ldap.schema.extractor.impl.DefaultSchemaLdifExtractor;
import org.apache.directory.api.ldap.schema.loader.LdifSchemaLoader;
import org.apache.directory.api.ldap.schema.manager.impl.DefaultSchemaManager;
import org.apache.directory.api.util.exception.Exceptions;
import org.apache.directory.server.constants.ServerDNConstants;
import org.apache.directory.server.core.DefaultDirectoryService;
import org.apache.directory.server.core.api.CacheService;
import org.apache.directory.server.core.api.DirectoryService;
import org.apache.directory.server.core.api.DnFactory;
import org.apache.directory.server.core.api.InstanceLayout;
import org.apache.directory.server.core.api.partition.Partition;
import org.apache.directory.server.core.api.schema.SchemaPartition;
import org.apache.directory.server.core.partition.impl.btree.jdbm.JdbmPartition;
import org.apache.directory.server.core.partition.ldif.LdifPartition;
import org.apache.directory.server.core.partition.ldif.SingleFileLdifPartition;
import org.apache.directory.server.i18n.I18n;
import org.apache.directory.server.ldap.LdapServer;
import org.apache.directory.server.protocol.shared.store.LdifFileLoader;
import org.apache.directory.server.protocol.shared.transport.TcpTransport;
import org.apache.maven.plugin.logging.Log;

/**
 * Starts a minimal apacheds server and imports an LDIF file.
 */
public class DirectoryRunner 
{
	private DirectoryService directoryService;
	private LdapServer ldapServer;
	
	private File workDir; 
	
	private String instancePath;
	private String pathLdifFile;
	
	private Log log;
	
	public DirectoryRunner(String instancePath, String pathLdifFile, Log log) {
		this.instancePath = instancePath;
		this.pathLdifFile = pathLdifFile;
		this.log = log;
	}
	  
    public void runDirectory() throws Exception {
    	log.info("try building apacheds instance in " + instancePath);
    	workDir = new File(instancePath);
    	log.info("check if instance path is existing: " + workDir.exists());
		
    	try {
			FileUtils.deleteDirectory(workDir);
		} catch (IOException e) {
			log.error("Instance path " + instancePath + " could not be cleaned.");
			throw new Exception();
		}

        directoryService = new DefaultDirectoryService();
        directoryService.setShutdownHookEnabled(true);
        directoryService.setInstanceId("Starbuck's LDAP server");
        log.info("instance created ");
               
        ldapServer = new LdapServer();
        ldapServer.setDirectoryService( directoryService );
        
        TcpTransport ldapTransport = new TcpTransport(11389);
        ldapServer.setTransports(ldapTransport);
        
        try {
			directoryService.setInstanceLayout(new InstanceLayout(workDir));
		} catch (IOException e) {
			log.error("Could not create standard folder layout in " + instancePath + " check instance path; " + e.getMessage());
		}
        
        CacheService cacheService = new CacheService();
        cacheService.initialize(directoryService.getInstanceLayout());
        directoryService.setCacheService( cacheService );
        
        initSchemaPartition();
        
        JdbmPartition systemPartition = new JdbmPartition(directoryService.getSchemaManager(), directoryService.getDnFactory());
        systemPartition.setId("system");
        systemPartition.setPartitionPath(new File(directoryService.getInstanceLayout().getPartitionsDirectory(), systemPartition.getId()).toURI());
        try {
			systemPartition.setSuffixDn(new Dn(ServerDNConstants.SYSTEM_DN));
		} catch (LdapInvalidDnException e) {
			log.error("Could not create system partition, exiting; " + e.getMessage());
			throw new Exception();
		}
        systemPartition.setSchemaManager( directoryService.getSchemaManager() );

        directoryService.setSystemPartition(systemPartition);
        
        directoryService.getChangeLog().setEnabled(true);
        directoryService.setDenormalizeOpAttrsEnabled(true);       
        
        SingleFileLdifPartition configPartition = new SingleFileLdifPartition(directoryService.getSchemaManager(), directoryService.getDnFactory());
        configPartition.setId("config");
        configPartition.setPartitionPath(new File(directoryService.getInstanceLayout().getConfDirectory(), "config.ldif").toURI());
        configPartition.setSchemaManager(directoryService.getSchemaManager());
        configPartition.setCacheService(cacheService);
        try {
			configPartition.setSuffixDn(new Dn(directoryService.getSchemaManager(), "ou=config"));
	        configPartition.initialize();
		} catch (LdapInvalidDnException e) {
			log.error("Could not create config partition, exiting; " + e.getMessage());
			throw new Exception();
		} catch (LdapException e) {
			log.error("Could not create config partition, exiting; " + e.getMessage());
			throw new Exception();
		}
        
        log.info("loading config partition ...");
        directoryService.addPartition(configPartition);
        
        log.info("loading starwit paritition...");
        Partition myPartition = addPartition("starwit", "dc=starwit,dc=de", directoryService.getDnFactory());
        directoryService.addPartition(myPartition);
                    
        directoryService.startup();
        ldapServer.start();
        
        File ldifImportFile = new File(pathLdifFile);        
        LdifFileLoader loader = new LdifFileLoader(directoryService.getAdminSession(), ldifImportFile.getAbsolutePath());
        loader.execute();    	
    }
    
    private void initSchemaPartition() throws Exception {
    	InstanceLayout instanceLayout = directoryService.getInstanceLayout();
    	File schemaPartitionDirectory = new File( instanceLayout.getPartitionsDirectory(), "schema" );

        if ( schemaPartitionDirectory.exists() ) {
            System.out.println( "schema partition already exists, skipping schema extraction" );
        } else {
            SchemaLdifExtractor extractor = new DefaultSchemaLdifExtractor(instanceLayout.getPartitionsDirectory());
            try {
				extractor.extractOrCopy();
			} catch (IOException e) {
				log.error("Extracting schema partition failed " + e.getMessage());
				throw new Exception();
			}
        }
        SchemaLoader loader = new LdifSchemaLoader(schemaPartitionDirectory);
        SchemaManager schemaManager = new DefaultSchemaManager( loader );
        
        log.info("loading schmea partition... ");
        schemaManager.loadAllEnabled();
        
        List<Throwable> errors = schemaManager.getErrors();
        if (errors.size() != 0) {
        	log.error("Error creating schema partition");
            throw new Exception(I18n.err(I18n.ERR_317, Exceptions.printErrors(errors)));
        }
        
        directoryService.setSchemaManager(schemaManager);
        
        LdifPartition schemaLdifPartition = new LdifPartition(schemaManager, directoryService.getDnFactory());
        schemaLdifPartition.setPartitionPath(schemaPartitionDirectory.toURI());
        SchemaPartition schemaPartition = new SchemaPartition(schemaManager);
        schemaPartition.setWrappedPartition(schemaLdifPartition);
        
        directoryService.setSchemaPartition(schemaPartition);
    }
    
    private Partition addPartition(String partitionId, String partitionDn, DnFactory dnFactory) throws Exception {
        JdbmPartition partition = new JdbmPartition(directoryService.getSchemaManager(), dnFactory);
        partition.setId(partitionId);
        partition.setPartitionPath(new File(directoryService.getInstanceLayout().getPartitionsDirectory(), partitionId).toURI());
        try {
			partition.setSuffixDn(new Dn(directoryService.getSchemaManager(), partitionDn));
		} catch (LdapInvalidDnException e) {
			log.error("Could not create partition " + partitionId + ", exiting; " + e.getMessage());
			throw new Exception();
		}
        
        return partition;
    }
}
