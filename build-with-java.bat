@echo off
REM Setup Java environment for Gradle builds
set "JAVA_HOME=C:\Program Files\Android\Android Studio\jbr"
set "PATH=%JAVA_HOME%\bin;%PATH%"

REM Run Gradle with all arguments
call gradlew.bat %*

