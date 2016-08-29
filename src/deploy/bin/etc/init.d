#!/bin/bash

APP_LABEL='Soft Wistle Financial Management Web Service'
BASE_DIR='/opt/softwhistle/finances-webservice'
PID_FILE='/var/run/swistle-finman-websrv.pid'
CLASSPATH="$BASE_DIR/classes:$BASE_DIR/libs/*"
MAIN_CLASS='com.softwhistle.Application'
CMD="java -classpath $CLASSPATH $MAIN_CLASS"

function pid {
    if [ -f "$PID_FILE" ]; then
        cat $PID_FILE
    fi
    echo ''
}

function check_running {
    PID=$(pid $PID_FILE)
    if [ "$PID" != "" ]; then
        running=$(ps aux | awk '{ print $2 }' | grep $PID)
        if [ "$running" != "" ]; then
            return 1
        fi
    fi
    return 0
}

function start {
    if [ check_running != 0 ]; then
        echo "Process has already started with PID, $(pid)"
    else
        echo "Starting $APP_LABEL"
        $CMD &
        echo $! > $PID_FILE
    fi
}

function stop {
    echo "Stopping $APP_LABEL"
    if [ check_running != 0 ]; then
        kill $(pid)
    fi
}

function report_status {
    if [ check_running != 0 ]; then
        echo "Process is running with PID, $(pid)"
    else
        echo "Process is not running"
    fi
}

case "$1" in
	start)
		start
        ;;
	stop)
		stop
		;;
	status)
		report_status
		;;
    *)
        echo "Invalid command: start|stop|status"
        ;;
esac
