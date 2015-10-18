ApacheDS maven plugin

This project implements a maven plugin that starts an apacheds instance. ApacheDS is a LDAP directory server that can be used for storing users and groups. For more details see https://directory.apache.org/apacheds/ 
This plugin shall be able to start and stop an instance. On startup a ldif file can be provided which then sets up users and groups. This way you can quickly run a LDAP server in your dev environment.

This software is still a very early phase, please do not use it, unless you know exactly what you're doing. No warranty so don't blame me!

install plugin in your repo with
mvn install

Add plugin in your project by adding
				<plugin>
					<groupId>de.starwit.apacheds</groupId>
					<artifactId>apacheds-maven-plugin</artifactId>
					<version>0.1</version>
				</plugin>
to section build->PluginManagement->Plugins


Run command
mvn': mvn de.starwit.apacheds:apacheds-maven-plugin:0.1:start -Dapacheds.pathtoldiffile=starwit.ldif -Dapacheds.instanceFolder=d:\\tmp\\apacheds -Dapacheds.pidFileLocation=pidfile

Stop command
 mvn de.starwit.apacheds:apacheds-maven-plugin:0.1:stop
