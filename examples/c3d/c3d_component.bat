@echo off
echo.
echo --- GCM C3D example ----------------------------------------
echo ---

rem if "%1" == "help" goto usage

goto doit

:usage
echo.
echo c3d_component.bat
goto doit


:doit
SETLOCAL ENABLEDELAYEDEXPANSION
IF NOT DEFINED PROACTIVE set PROACTIVE=%CD%\..\..
call "..\init.bat"
set JAVA_CMD=%JAVA_CMD% -Dgcm.provider=org.objectweb.proactive.core.component.Fractive
%JAVA_CMD%  org.objectweb.proactive.examples.components.c3d.Main %*
ENDLOCAL

:end
pause
echo.
echo ---------------------------------------------------------
