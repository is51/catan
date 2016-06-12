# Catan

## Description
It-catan

## API Documentation
List of methods available via http with real examples can be found under the following link (you need to Start server before):
```
http://localhost:8080/api
or
http://localhost:8091/api
```

## How to create branch

Don't know

## How to run locally

#### Required soft

You can run it under any operating system.

To run the application you should install the following tools:

* Java 1.7 (or newer)
* Maven 3.2.3

####  Instructions

Use the following commands:

To build and compile the project with running test:
```
mvn clean install
```

To start Server without running tests:
```
mvn clean install spring-boot:run -DskipTests -Dproperties.folder=dev
```
Server is available under the following link:
```
http://localhost:8080/
or
http://localhost:8091/
```


To prepare build for deployment to Google Cloud, build JAR file:
```
mvn clean install -DskipTests
```
Check that build works fine:
```
java -Dproperties.folder=dev -jar ./target/IT-nizator.jar
```
Deploy build to gcloud via maven plugin:
```
mvn gcloud:deploy -DskipTests -Dgcloud.gcloud_directory=<path_to_gcloud_installed (e.g. /usr/local/google-cloud-sdk)>
```


## How to deploy a new build to TEST server

#### Windows
Run the following batch file from command line:
```
deploy.bat
```

#### Linux & Mac
Run the following batch file from command line:
```
chmod 777 deploy.sh
./deploy.sh
```

