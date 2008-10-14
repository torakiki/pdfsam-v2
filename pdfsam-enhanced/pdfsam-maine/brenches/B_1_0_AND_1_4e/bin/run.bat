@echo off

set DIRNAME=..\
set PDFSAMJAR=%DIRNAME%\pdfsam-1.0.3.jar

if exist "%PDFSAMJAR%" goto FOUND_PDFSAM_JAR
echo Could not locate %PDFSAMJAR%. Please check that you are in the
echo bin directory when running this script.
goto END

:FOUND_PDFSAM_JAR
if not "%JAVA_HOME%" == "" goto HOME_SET

set JAVA=java

echo JAVA_HOME is not set.  Unexpected results may occur.
echo Set JAVA_HOME to the directory of your local JDK to avoid this message.
goto SKIP_HOME_SET

:HOME_SET
set JAVA=%JAVA_HOME%\bin\java

:SKIP_HOME_SET
set JAVA_OPTS= -Xmx128m

set PDFSAM_CLASSPATH=%PDFSAMJAR%

echo ===============================================================================
echo.
echo   pdfsam
echo.
echo   JAVA: %JAVA%
echo.
echo   JAVA_OPTS: %JAVA_OPTS%
echo.
echo   CLASSPATH: %PDFSAM_CLASSPATH%
echo.
echo ===============================================================================
echo.

:RESTART
@echo on
"%JAVA%" %JAVA_OPTS%  -classpath "%PDFSAM_CLASSPATH%" org.pdfsam.guiclient.GuiClient %*
@echo off
if ERRORLEVEL 10 goto RESTART

:END
if "%NOPAUSE%" == "" pause

:END_NO_PAUSE