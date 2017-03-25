# ApacheDS maven plugin

This project implements a maven plugin that starts an apacheds instance. It is able to start a directory instance. On startup a ldif file can be provided which then sets up users and groups. This way you can quickly run a LDAP server in your dev environment. As a Maven plugin it can also be integrated in your continuous integration tool chain. 

## About Apache Directory Server
ApacheDS is a LDAP directory server that can be used for storing users and groups. For more details see <http://https://directory.apache.org/apacheds/>. ApacheDS is a great piece of software, so please consider contributing to that project. Details how to contribute can be found at <http://directory.apache.org/contribute.html>.


## Disclaimer
This software is still in a very early phase, please do not use it, unless you know exactly what you're doing. Please also note that this module is intended to serve development purposes only. ApacheDS is started with default credentials (admin/secret). Therefore this tool should **never** be used in a productive environment! 


## How to use
Plugin is not yet released to central Maven repository so see section how to compile to make plugin available on your machine.

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
mvn de.starwit.apacheds:apacheds-maven-plugin:0.1:start -Dapacheds.pathtoldiffile=starwit.ldif -Dapacheds.instanceFolder=d:\\tmp\\apacheds -Dapacheds.pidFileLocation=pidfile
```
Stop command
```bash
 mvn de.starwit.apacheds:apacheds-maven-plugin:0.1:stop
```

## How to compile plugin from source code
If you want to modify this plugin or debug it, you can install a individual version to your local Maven repository. In order to do that checkout this repository on your local machine and run 
```bash
mvn install
```
