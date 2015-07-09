@echo off
echo.
echo --- Hello World example ---------------------------------------------

goto doit

:usage
echo.
goto end

:doit
SETLOCAL ENABLEDELAYEDEXPANSION
call ..\init.bat

IF NOT DEFINED PROACTIVE set PROACTIVE=%CD%\..\..

:start
set XMLDESCRIPTOR=GCMA.xml

set /A found=0
for %%i in (%*) do (
    if "%%i" == "-d" then set /a found=1
)


:launch

if %found% EQU 1 (
  %JAVA_CMD% org.objectweb.proactive.examples.masterworker.nqueens.NQueensExample %*
) ELSE (
  %JAVA_CMD% org.objectweb.proactive.examples.masterworker.nqueens.NQueensExample -d "%XMLDESCRIPTOR%" %*
)
ENDLOCAL

pause
echo.
echo ----------------------------------------------------------
echo on

:end
