#!/usr/bin/env bash

workingDir=`dirname $0`
. $workingDir/env.sh


VM_ARGS=""

# Fine GC tuning: 
# 1- Most object are short lived (messages)
# 2- Very few objects go to the old generation (client information, few kB per client)
# 3- Try to avoid stop the world (concurrent mark & sweep, full GC at 50%)
# 4- It's better to have more slower young gc than a long one
VM_ARGS="${VM_ARGS} -server -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:CMSInitiatingOccupancyFraction=50 -XX:NewRatio=2 -Xms512m -Xmx512m"

$JAVACMD $VM_ARGS org.objectweb.proactive.extensions.pamr.router.Main "$@"
