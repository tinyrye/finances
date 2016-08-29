#!/bin/bash

. /etc/init.d/functions

APP_LABEL='Soft Wistle Financial Management Web Service'
BASE_DIR='/opt/softwhistle/finances-webservice'
PID_FILE='/var/run/swistle-finman-websrv.pid'
CLASSPATH="$BASE_DIR/classes:$BASE_DIR/libs/*"
MAIN_CLASS='com.softwhistle.Application'
CMD="java -classpath $CLASSPATH $MAIN_CLASS"

case "$1" in
	start)
		echo "Starting $APP_LABEL"
		daemon --pidfile=$PID_FILE "$CMD &"
		;;
	stop)
		echo "Stopping $APP_LABEL"
		killproc -p $PID_FILE -signal
		;;
	status)
		checkpid $(cat $PID_FILE)
		;;
esac