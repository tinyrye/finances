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
    PID=$(pid)
    if [ "$PID" != "" ]; then
        echo $(ps aux | awk '{ print $2 }' | grep $PID)
    else
        echo ""
    fi
}

function start {
    if [ "$(check_running)" != "" ]; then
        echo "Process has already started with PID, $(pid)"
    else
        echo "Starting $APP_LABEL"
        $CMD &
        echo $! > $PID_FILE
    fi
}

function stop {
    if [ "$(check_running)" != "" ]; then
        echo "Stopping $APP_LABEL"
        kill $(pid)
    else
        echo "Process is not running."
    fi
}

function report_status {
    if [ "$(check_running)" != "" ]; then
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