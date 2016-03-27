#!/bin/bash
## Uncomment if Java and Maven env variables are not set
export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_73.jdk/Contents/Home
export M2_HOME=/Users/a1/Documents/programs/apache-maven-3.3.9
export M2=$M2_HOME/bin
export PATH=$M2:$PATH
java -version
mvn -v
cd src/main/webapp/new
npm install  || true
npm run tsc  || true
cd ../../../../
mvn clean package -DskipTests
chmod 400 deploy/catan1_private_key.ppk
scp -vCq -i deploy/catan1_private_key.ppk target/ROOT.war 55fe79942d5271339400003a@catan-1server.rhcloud.com:/var/lib/openshift/55fe79942d5271339400003a/app-root/runtime/dependencies/jbossews/webapps/
scp -vCq -i deploy/catan1_private_key.ppk target/ROOT-classes.jar 55fe79942d5271339400003a@catan-1server.rhcloud.com:/var/lib/openshift/55fe79942d5271339400003a/app-root/runtime/dependencies/jbossews/webapps/
# [WHEN PROMPTED, ENTER PASSWORD: 12345]
#
# ( The key fingerprint is: a1:d1:b5:fe:8f:32:62:6a:f5:0e:84:f4:a8:64:bb:38 atomix@kievnet.com.ua



