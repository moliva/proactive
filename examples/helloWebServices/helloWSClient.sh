#!/bin/sh

echo
echo --- Hello World Web Service ---------------------------------------------

workingDir=`dirname $0`
. ${workingDir}/../env.sh


$JAVACMD org.objectweb.proactive.examples.webservices.helloWorld.HelloWorldClient "$@"

echo
echo ------------------------------------------------------------
