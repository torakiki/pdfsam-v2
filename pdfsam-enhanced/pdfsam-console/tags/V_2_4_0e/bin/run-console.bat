@echo off

set DIRNAME=..\lib\
set CONSOLEJAR=%DIRNAME%\pdfsam-console-2.0.5e.jar

if exist "%CONSOLEJAR%" goto FOUND_CONSOLE_JAR
echo Could not locate %CONSOLEJAR%. Please check that you are in the
echo bin directory when running this script.
goto END

:FOUND_CONSOLE_JAR
if not "%JAVA_HOME%" == "" goto HOME_SET

set JAVA=java

echo JAVA_HOME is not set.  Unexpected results may occur.
echo Set JAVA_HOME to the directory of your local JDK to avoid this message.
goto SKIP_HOME_SET

:HOME_SET
set JAVA=%JAVA_HOME%\bin\java

:SKIP_HOME_SET
set JAVA_OPTS= -Dlog4j.configuration=console-log4j.xml

set CONSOLE_CLASSPATH=%CONSOLEJAR%

echo ===============================================================================
echo.
echo   pdfsam console
echo.
echo   available properties:
echo   pdfsam.log.console.level
echo   pdfsam.log.file.level
echo   pdfsam.log.file.filename
echo.
echo   JAVA: %JAVA%
echo.
echo   JAVA_OPTS: %JAVA_OPTS%
echo.
echo   CLASSPATH: %CONSOLE_CLASSPATH%
echo.
echo ===============================================================================
echo.

:RESTART
@echo on
"%JAVA%" %JAVA_OPTS%  -classpath "%CONSOLE_CLASSPATH%" org.pdfsam.console.ConsoleClient %*
@echo off
if ERRORLEVEL 10 goto RESTART

:END
if "%NOPAUSE%" == "" pause

:END_NO_PAUSE