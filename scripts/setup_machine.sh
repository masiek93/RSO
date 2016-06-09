#!/bin/bash

USER=rso
SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
DATA_DIR=${SCRIPT_DIR}/data
MARK=_____

if [ "$EUID" -ne 0 ]
  then echo ${MARK}"Uruchom mnie przez sudo"
  exit
fi

echo ${MARK}"Instaluję serwer SSH"
apt-get install openssh-server

echo ${MARK}"Usuwam użytkownika ${USER} jeśli istnieje"
deluser -q --remove-home ${USER}

echo ${MARK}"Tworzę użytkowika ${USER}"
adduser ${USER} -q --disabled-password -gecos ""

echo ${MARK}"Dodaję użytkowika ${USER} do grupy docker"
usermod -aG docker ${USER}

echo ${MARK}"Loguję się na użytkownika ${USER}"
su rso <<EO_SU_RSO
    echo ${MARK}"Dodaję klucz SSH"
    cd ~
    mkdir .ssh
    echo "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDgWZcv6OX7TXXdRiNRp3jUx50ALKWg+raPHX+F1qP7dpdnEbknAAi04UZw0428LxTUmsjIp+vfK1OUh+C72CaiTNXLczGUEjcAqWNXRCnPUwPLHyS2KIwC+u+/vOxkyEjsOBsJBksfj9FD2pV1qJF3o59ywRuqZms1eV7reuG5Qu6lVB1+IwS2rTGfPuRNbJjTP6VlLWXaKV7H7nP2R1J95rKNHW/qlaUVNAylO+0DsvlyAFQygcS2k3nFcpXNwH59IH2zqSDvbXQG1KnIGAnr2ccx9X/1hjhVtQNxQvF1V723sTbUAYDY6mrpoxg4SDq+sxKBEVbBO7a94BoyrhVB" >> ~/.ssh/authorized_keys
EO_SU_RSO

cd /home/rso
for INSTANCE in fileServer1 fileServer2 fileServer3 dirServer1 dirServer2; do
    mkdir ${INSTANCE}
    echo ${MARK}"Przenoszę resources do "${INSTANCE}
    cp -r ${DATA_DIR}/resources ${INSTANCE}
done
for INSTANCE in fileServer1 fileServer2 fileServer3; do
    echo ${MARK}"Tworzę storage dla "${INSTANCE}
    mkdir ${INSTANCE}/storage
done
