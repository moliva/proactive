#!/bin/sh

echo
echo --- C3D ---------------------------------------------

workingDir=`dirname $0`
. ${workingDir}/../env.sh

export XMLDESCRIPTOR=$workingDir/GCMA_User.xml
$JAVACMD org.objectweb.proactive.examples.webservices.c3dWS.WSUser $XMLDESCRIPTOR "$@"



echo
echo ---------------------------------------------------------
