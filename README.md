ApacheDS maven plugin

This project implements a maven plugin that starts an apacheds instance. ApacheDS is a LDAP directory server that can be used for storing users and groups. For more details see https://directory.apache.org/apacheds/ 
This plugin shall be able to start and stop an instance. On startup a ldif file can be provided which then sets up users and groups. This way you can quickly run a LDAP server in your dev environment.

This software is still a very early phase, please do not use it, unless you know exactly what you're doing. No warranty so don't blame me!
