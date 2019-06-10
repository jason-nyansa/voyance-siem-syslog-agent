#!/bin/bash

###
# #%L
# VoyanceSiemSyslogAgent
# %%
# Copyright (C) 2019 Nyansa, Inc.
# %%
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
# #L%
###

JAR_NAME=${artifactId}-${version}.jar
AGENT_NAME=${artifactId}
DB_NAME=voyance-agent.mv.db
LOG_PATH=logs/voyance-agent.log


agent_pid() {
#    ps ax | grep "$AGENT_NAME" | grep -v "grep" | awk '{ print $1 }'
    jps -m | grep "$AGENT_NAME" | awk '{ print $1 }'
}

agent_start() {
    PID=$(agent_pid)
    if [[ -n "$PID" ]]; then
        echo "$AGENT_NAME is already running"
        exit 1
    fi
    echo "Starting $AGENT_NAME ..."
    [[ -e "$LOG_PATH" ]] || mkdir -p "$(dirname "$LOG_PATH")" && touch "$LOG_PATH"
    java -Dfile.encoding=UTF-8 -Xms1g -Xmx2g -jar "jars/$JAR_NAME" 2>&1 >> "$LOG_PATH" &
    echo "$AGENT_NAME started, please check $LOG_PATH for any errors"
}

agent_stop() {
    PID=$(agent_pid)
    if [[ -z "$PID" ]]; then
        echo "$AGENT_NAME is already stopped"
        exit 1
    fi
    echo "Stopping $AGENT_NAME with PID: $PID ..."
    kill -9 ${PID}
    [[ $? -eq 0 ]] && echo "$AGENT_NAME stopped" || echo "Error occurred when stopping $AGENT_NAME"
}

agent_restart() {
    PID=$(agent_pid)
    if [[ -n "$PID" ]]; then
        agent_stop
    fi
    agent_start
}

agent_status() {
    PID=$(agent_pid)
    if [[ -n "$PID" ]]; then
        echo "$AGENT_NAME is RUNNING, PID: $PID"
    else
        echo "$AGENT_NAME is STOPPED"
    fi
}

agent_db_reset() {
    PID=$(agent_pid)
    if [[ -n "$PID" ]]; then
        echo "$AGENT_NAME is running, please stop tha agent before resetting the database"
        exit 1
    fi
    echo "$AGENT_NAME local database and all API fetch progress will be reset, proceed? [y/n]"
    read ANS
    if [[ ${ANS} == "y" ]]; then
        [[ -e "$DB_NAME" ]] && rm "$DB_NAME"
        echo "$AGENT_NAME database has been reset"
    else
        echo "No action performed"
    fi
}


case "$1" in
    start)
        agent_start
        ;;
    stop)
        agent_stop
        ;;
    restart)
        agent_restart
        ;;
    status)
        agent_status
        ;;
    db_reset)
        agent_db_reset
        ;;
    *)
        echo "Usage: $0 { start | stop | restart | status | db_reset }"
        echo "      start:      starting the the agent"
        echo "      stop:       stopping the the agent"
        echo "      restart:    restart the agent"
        echo "      status:     check whether the agent is running"
        echo "      db_reset:   resetting the agent's local database and all API fetch progress to its initial state"
        echo ""
        ;;
esac
