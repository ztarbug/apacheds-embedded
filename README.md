ApacheDS maven plugin

This project implements a maven plugin that starts an apacheds instance. ApacheDS is a LDAP directory server that can be used for storing users and groups. For more details see https://directory.apache.org/apacheds/ 
This plugin shall be able to start and stop an instance. On startup a ldif file can be provided which then sets up users and groups. This way you can quickly run a LDAP server in your dev environment.

This software is still in a very early phase, please do not use it, unless you know exactly what you're doing. Please also note that this module is intended to server development purposes only. This should never be used in a productive environment!

install plugin in your local repo with
```bash
mvn install
```
Add plugin in your project by adding
```XML
    <plugin>
      <groupId>de.starwit.apacheds</groupId>
      <artifactId>apacheds-maven-plugin</artifactId>
      <version>0.1</version>
      <configuration>
	      <instanceFolder>/tmp/apacheds</instanceFolder>
	      <partitionName>starwit</partitionName>
	      <partitionSuffix>de</partitionSuffix>
	      <pathToLdifFile>/home/markus/dev/workspaces/authTests/apacheds-embedded/starwit.ldif</pathToLdifFile>
	      <pidFileLocation>pidfile</pidFileLocation>
      </configuration>
    </plugin>
```
to section build->PluginManagement->Plugins


Run command
```bash
mvn': mvn de.starwit.apacheds:apacheds-maven-plugin:0.1:start -Dapacheds.pathtoldiffile=starwit.ldif -Dapacheds.instanceFolder=d:\\tmp\\apacheds -Dapacheds.pidFileLocation=pidfile
```
Stop command
```bash
 mvn de.starwit.apacheds:apacheds-maven-plugin:0.1:stop
```
