#!/bin/bash

. /etc/init.d/functions

BASE_DIR='/opt/softwhistle/finances-webservice'
PID_FILE='/var/run/softwhistle-finances-webservice.pid'
CLASSPATH='$BASE_DIR/classes:$BASE_DIR/lib/*'
CMD="java -classpath $CLASSPATH com.softwhistle.finances.Application"

case "$1" in
	start)
		daemon --pidfile=$PID_FILE
		;;
	stop)
		killproc -p $PID_FILE -signal
		;;
	status)
		checkpid $(cat $PID_FILE)
		;;
esac