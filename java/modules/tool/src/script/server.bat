@echo off
rem ---------------------------------------------------------------------------
rem Start script for running the Simple Axis Server
rem
rem ---------------------------------------------------------------------------

rem get the classes for the simple axis server
set AXIS2_CLASS_PATH="%AXIS_HOME%";"%AXIS_HOME%\lib\axis2-M1.jar";"%AXIS_HOME%\lib\axis-wsdl4j-1.2-RC1.jar";"%AXIS_HOME%\lib\commons-logging-1.0.3.jar";"%AXIS_HOME%\lib\log4j-1.2.8.jar";"%AXIS_HOME%\lib\stax-1.1.1-dev.jar";"%AXIS_HOME%\lib\stax-api-1.0.jar"

java -cp %AXIS2_CLASS_PATH% org.apache.axis.transport.http.SimpleHTTPServer %1 %2
