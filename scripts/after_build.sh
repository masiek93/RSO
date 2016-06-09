#!/bin/bash
echo Copying outputs...

cd ../out/artifacts

echo "Renaming jars"
mv Client/RSO.jar Client/Client.jar
mv FileServer/RSO.jar FileServer/FileServer.jar
mv DirServer/RSO.jar DirServer/DirServer.jar

echo "Copying resources"
cp -r ../../DirServer/resources .
echo "Emptying db"
rm ./resources/db/*
touch ./resources/db/.DO_NOT_DELETE
echo "Spreading resources across all instances"
cp -r ./resources Client
cp -r ./resources FileServer
cp -r ./resources DirServer
echo "Copying resources to demo script data"
rm -r ../../scripts/data/resources
cp -r ./resources ../../scripts/data
#rm -r ./resources

echo "Creating empty storages"
mkdir  FileServer/storage1
mkdir  FileServer/storage2
mkdir  FileServer/storage3

echo "Copying jars for docker"
cd ..
mkdir docker
cd docker
cp ../artifacts/Client/Client.jar .
cp ../artifacts/FileServer/FileServer.jar .
cp ../artifacts/DirServer/DirServer.jar .

echo "Done"

