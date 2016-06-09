echo Copying outputs...

cd ../out/artifacts

# renaming jars
mv Client/RSO.jar Client/Client.jar
mv FileServer/RSO.jar FileServer/FileServer.jar
mv DirServer/RSO.jar DirServer/DirServer.jar

# copying resources
cp -r ../../DirServer/resources Client
cp -r ../../DirServer/resources FileServer
cp -r ../../DirServer/resources DirServer

# copying storage
cp -r ../../DirServer/storage1 FileServer
cp -r ../../DirServer/storage3 FileServer
cp -r ../../DirServer/storage2 FileServer

# for docker
cd ..
mkdir docker
cd docker
cp ../artifacts/Client/Client.jar .
cp ../artifacts/FileServer/FileServer.jar .
cp ../artifacts/DirServer/DirServer.jar .

echo Done

