cd src\main\webapp\new
call npm install
call npm run tsc
cd ../../../../
rem set JAVA_HOME=C:\Program Files\Java\jdk1.8.0_45
rem set PATH=%JAVA_HOME%\bin;%PATH%
rem echo %JAVA_HOME%
call java -version
call mvn -v
call mvn clean package -DskipTests
cd deploy
mkdir tmp
COPY %~dp0target\ROOT.war  %~dp0deploy\tmp\
COPY %~dp0target\ROOT-classes.jar  %~dp0deploy\tmp\
winscp.com /command ^
    "open sftp://55fe79942d5271339400003a@catan-1server.rhcloud.com/ -privatekey=catan1_private_key.ppk  -hostkey=""ssh-rsa 2048 a1:d1:b5:fe:8f:32:62:6a:f5:0e:84:f4:a8:64:bb:38""" ^
    "cd /var/lib/openshift/55fe79942d5271339400003a/app-root/runtime/dependencies/jbossews/webapps" ^
    "option transfer binary" ^
    "put %~dp0deploy\tmp\ROOT-classes.jar" ^
    "put %~dp0deploy\tmp\ROOT.war" ^
    "close" ^
    "exit"
# [WHEN PROMPTED, ENTER PASSWORD: 12345]
#
# ( The key fingerprint is: a1:d1:b5:fe:8f:32:62:6a:f5:0e:84:f4:a8:64:bb:38 atomix@kievnet.com.ua
rmdir /s /q tmp
cd ..
