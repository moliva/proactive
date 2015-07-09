#!/bin/sh

echo
echo --- Matrix : nodes initialization -----------------------

workingDir=`dirname $0`
. ${workingDir}/../env.sh

export XMLDESCRIPTOR=$workingDir/GCMA.xml
$JAVACMD org.objectweb.proactive.examples.matrix.Main 300 $XMLDESCRIPTOR

echo
echo ---------------------------------------------------------
