# aloha
Hello microservice using Vert.X

Build and Deploy aloha
-------------------------

1. Open a command prompt and navigate to the root directory of this microservice.
2. Type this command to build and execute the service:

        mvn clean compile exec:java


Access the application
----------------------

The application will be running at the following URL: <http://localhost:8080/api/aloha>

Deploy the application in Openshift
-----------------------------------

1. Make sure to be connected to the Docker Daemon
2. Execute

		mvn clean package docker:build fabric8:json fabric8:apply
