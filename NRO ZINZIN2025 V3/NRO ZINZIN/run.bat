@echo off
setlocal EnableExtensions
chcp 65001 >nul

REM === về thư mục chứa run.bat (gốc dự án)
cd /d "%~dp0"

REM === đảm bảo thư mục logs tồn tại
if not exist "logs" mkdir "logs"

REM === cấu hình bộ nhớ JVM (tuỳ máy)
set "INITIAL_MEM=112m"
set "MAX_MEM=200m"

REM === tuỳ chọn JVM (tối giản & ổn định, KHÔNG headless để tránh crash MenuAuto)
set JAVA_OPTS=-Xms%INITIAL_MEM% -Xmx%MAX_MEM% ^
 -Dfile.encoding=UTF-8 ^
 -Dsun.java2d.d3d=false -Dsun.java2d.noddraw=true ^
 -XX:+UseG1GC ^
 -XX:+UseStringDeduplication

REM === kiểm tra JAR
if not exist "dist\NRO_ZINZIN.jar" (
  echo ERROR: Khong tim thay dist\NRO_ZINZIN.jar
  pause
  endlocal
  exit /b 1
)

echo [%date% %time%] ================================================
echo [%date% %time%] Starting Java with:
echo java %JAVA_OPTS% -jar "dist\NRO_ZINZIN.jar" %*
echo Logs -> %~dp0logs\server.log  (them 'debug' sau lenh de xem tren console)
echo ================================================================

REM === nếu gọi với tham số "debug" thì in trực tiếp ra console (không redirect)
if /i "%~1"=="debug" (
  if defined JAVA_HOME (
    "%JAVA_HOME%\bin\java" %JAVA_OPTS% -jar "dist\NRO_ZINZIN.jar" %*
  ) else (
    java %JAVA_OPTS% -jar "dist\NRO_ZINZIN.jar" %*
  )
) else (
  if defined JAVA_HOME (
    "%JAVA_HOME%\bin\java" %JAVA_OPTS% -jar "dist\NRO_ZINZIN.jar" %* 1>> "logs\server.log" 2>&1
  ) else (
    java %JAVA_OPTS% -jar "dist\NRO_ZINZIN.jar" %* 1>> "logs\server.log" 2>&1
  )
)

set "EXIT_CODE=%ERRORLEVEL%"

echo.
echo [%date% %time%] = Server da dung voi ma loi %EXIT_CODE%. Xem log tai %~dp0logs\server.log =
if /i "%~1"=="debug" echo (DEBUG MODE: khong redirect log)
pause

endlocal
exit /b %EXIT_CODE%
