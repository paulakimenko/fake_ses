#!/bin/bash

function shutdown {
    kill -s SIGTERM $NODE_PID
    wait $NODE_PID
}

java ${JAVA_OPTS} \
    -Dfakeses.port=${PORT} \
    -Dfakeses.threadcount=${THREAD_COUNT} \
    -Dfakeses.workdir=${WORK_DIR} \
    -jar /sesmock.jar &
NODE_PID=$!

trap shutdown SIGTERM SIGINT
wait $NODE_PID