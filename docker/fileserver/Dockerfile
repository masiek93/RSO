FROM java:8
MAINTAINER David Sieciński dsiecins@mion.elka.pw.edu.pl

COPY ./rso_file_server.jar /fileserver/rso_file_server.jar
VOLUME  [ "/resources","/storage"]
EXPOSE 13267
EXPOSE 13999
ENTRYPOINT ["java", "-jar", "rso_file_server.jar"]
CMD [""]

# docker build .
# docker run  -d  -v LOCAL_STORAGE:/storage -v PATH_TO_RESOURCES_FILE:/resources  -p 13267:13267 -p 13999:13999 8d4ac62a6e2e 