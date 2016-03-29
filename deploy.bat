@ECHO OFF
:: ---------------------------------------------------------------------
:: Read input data from console
:: ---------------------------------------------------------------------

:: Check if install npm and compile javascript required
:setNpmInstall
set /p npmInstall=Do you want to install npm and compile javascript (y/n)?
if /i "%npmInstall:~,1%" NEQ "n" if /i "%npmInstall:~,1%" NEQ "y" goto setNpmInstall

:: Check if Maven build required
:setMvnInstall
set /p mvnInstall=Do you want to perform Maven build (y/n)?
if /i "%mvnInstall:~,1%" NEQ "n" if /i "%mvnInstall:~,1%" NEQ "y" goto setMvnInstall

:: Check if send file to SFTP required
:setSendFile
set /p sendFile=Do you want to send file to SFTP (y/n)?
if /i "%sendFile:~,1%" NEQ "n" if /i "%sendFile:~,1%" NEQ "y" goto setSendFile

:: Check Java and Maven version
if /i "%mvnInstall:~,1%" EQU "n" goto npm

call java -version
call mvn -v
echo JAVA_HOME=%JAVA_HOME%
:: Check JAVA_HOME and Maven versions correct
:setJavaHomeCorrect
set /p javaHomeCorrect=Is JAVA_HOME and Maven versions correct (y - Proceed with current values / n - Exit deployment script / e - Edit JAVA_HOME path)?
if /i "%javaHomeCorrect:~,1%" NEQ "n" if /i "%javaHomeCorrect:~,1%" NEQ "y" if /i "%javaHomeCorrect:~,1%" NEQ "e" goto setJavaHomeCorrect

IF NOT EXIST "%JAVA_HOME%" SET javaHomeCorrect=n
if /i "%javaHomeCorrect:~,1%" EQU "y" goto npm
SET /p javaHomePath= "Enter path to Java home (e.g. C:\Program Files\Java\jdk1.8.0_45): "
SET JAVA_HOME=%javaHomePath%
SET PATH=%JAVA_HOME%\bin;%PATH%
call java -version
call mvn -v


:: ---------------------------------------------------------------------
:: Install NPM
:: ---------------------------------------------------------------------
:npm
if /i "%npmInstall:~,1%" EQU "n" goto mvn

cd src\main\webapp\new
call npm install
call npm run tsc
cd ../../../../


:: ---------------------------------------------------------------------
:: Maven Build
:: ---------------------------------------------------------------------
:mvn
if /i "%mvnInstall:~,1%" EQU "n" goto send

call mvn clean package -DskipTests


:: ---------------------------------------------------------------------
:: Upload files to server
:: ---------------------------------------------------------------------
:send
if /i "%sendFile:~,1%" EQU "n" exit /b

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

rmdir /s /q tmp
cd ..

