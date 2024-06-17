@echo off
REM Nginx 실행 파일 경로
set "nginx_path=D:\developFuture\nginx-1.25.5\nginx.exe"

REM Nginx 설정 파일 경로
set "nginx_conf=D:\developFuture\spring\demoboard\src\main\resources\nginx\nginx.conf"

REM 로그 파일과 임시 디렉터리 경로
set "log_path=D:\developFuture\nginx-1.25.5\logs"
set "temp_path=D:\developFuture\nginx-1.25.5\temp"

REM 로그 파일과 임시 디렉터리 생성
if not exist "%log_path%" mkdir "%log_path%"
if not exist "%log_path%\error.log" type nul > "%log_path%\error.log"
if not exist "%temp_path%" mkdir "%temp_path%"
if not exist "%temp_path%\client_body_temp" mkdir "%temp_path%\client_body_temp"
if not exist "%temp_path%\proxy_temp" mkdir "%temp_path%\proxy_temp"
if not exist "%temp_path%\fastcgi_temp" mkdir "%temp_path%\fastcgi_temp"
if not exist "%temp_path%\uwsgi_temp" mkdir "%temp_path%\uwsgi_temp"
if not exist "%temp_path%\scgi_temp" mkdir "%temp_path%\scgi_temp"

REM Nginx 실행
"%nginx_path%" -c "%nginx_conf%"

REM Nginx 시작 메시지
echo Nginx has been started with the configuration file: %nginx_conf%