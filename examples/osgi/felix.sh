#!/bin/sh

#
# The following variable should be automatically
# assigned during install, if not, edit it to reflect
# your Java installation.
#
workingDir=`dirname $0`



#
# You don't need to edit the following line
#

export FELIX_HOME="."
. ${FELIX_HOME}/../env.sh

export MYHOSTNAME=`hostname --long`

rm -rf cache/${MYHOSTNAME}/*

JVM_ARGS="-Dorg.osgi.framework.storage=${FELIX_HOME}/cache/${MYHOSTNAME} \
	-Dfelix.config.properties=file:${FELIX_HOME}/conf/config.properties \
	-Djava.security.manager \
	-Djava.security.policy=conf/java.policy \
	-Dproactive.http.port=8081 \
	-Dproactive.communication.protocol=http \
	-Dcom.sun.management.jmxremote \
	-Dproactive.hostname=localhost \
	-Dproactive.http.jetty.xml=jetty.xml"

${JAVA_HOME}/bin/java $JVM_ARGS -jar "${FELIX_HOME}/../../dist/lib/felix.jar"
