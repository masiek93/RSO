#!/bin/bash

USER=rso
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
DATA_DIR=${SCRIPT_DIR}/data
MARK="_____"
# docker build version
DOCKER_V=4

if [ "$EUID" -ne 0 ]
  then echo ${MARK}"Uruchom mnie przez sudo"
  exit
fi


function runDirServer {
    echo ${MARK}"Loguję się przez SSH na użytkownika ${USER}@${HOST}"
    ssh -D ${SSH_PORT} -i ${DATA_DIR}/ssh_private.key -o "StrictHostKeyChecking no" ${USER}@${HOST} <<-EO_SSH_RSO
    cd ~
#    docker run -i -v ~/dirServer${DIR_SRV_ID}/resources:/resources -p ${PORT1}:${PORT1} -p ${PORT2}:${PORT2} davidsie/rso-directoryserver:v${DOCKER_V} java -DmyIp="${HOST}" -jar DirServer.jar resources/gen/id.txt ${PORT1} ${PORT2} > ~/dirServer${DIR_SRV_ID}/resources/log.txt 2>&1 &
    java -DmyIp="${HOST}" -jar ~/dirServer${DIR_SRV_ID}/DirServer.jar ~/dirServer${DIR_SRV_ID}/resources/gen/id.txt ${PORT1} ${PORT2} > ~/dirServer${DIR_SRV_ID}/resources/log.txt 2>&1 &
    echo "after docker"
EO_SSH_RSO
echo "after ssh"
}

function runFileServer {
    echo ${MARK}"Loguję się przez SSH na użytkownika ${USER}@${HOST}"
    ssh -D ${SSH_PORT} -i ${DATA_DIR}/ssh_private.key -o "StrictHostKeyChecking no" ${USER}@${HOST} <<-EO_SSH_RSO
    cd ~
#    docker run -i -v ~/fileServer${FILE_SRV_ID}/storage:/storage -v ~/fileServer${FILE_SRV_ID}/resources:/resources -p ${PORT1}:${PORT1} -p ${PORT2}:${PORT2} davidsie/rso-fileserver:v${DOCKER_V} java -DmyIp="${HOST}" -jar FileServer.jar storage resources/gen/id.txt ${PORT1} > ~/fileServer${FILE_SRV_ID}/resources/log.txt 2>&1 &
    java -DmyIp="${HOST}" -jar ~/fileServer${FILE_SRV_ID}/FileServer.jar ~/fileServer${FILE_SRV_ID}/storage ~/fileServer${FILE_SRV_ID}/resources/gen/id.txt ${PORT1} > ~/fileServer${FILE_SRV_ID}/resources/log.txt 2>&1 &
    echo "after docker"
EO_SSH_RSO
echo "after ssh"
}

SSH_PORT=22

# rafal
DIR_SRV_ID=1
HOST=192.168.1.2
PORT1=1234
PORT2=4321
runDirServer

## m kedrz
DIR_SRV_ID=2
HOST=192.168.1.4
PORT1=3242
PORT2=2423
runDirServer

##################################
# FILE SERVERS
FILE_SRV_ID=1
HOST=192.168.1.2
PORT1=10001
PORT2=10002
runFileServer

FILE_SRV_ID=2
HOST=192.168.1.4
PORT1=10001
PORT2=10002
runFileServer

FILE_SRV_ID=3
HOST=192.168.1.4
PORT1=10003
PORT2=10004
runFileServer


echo "end of script"