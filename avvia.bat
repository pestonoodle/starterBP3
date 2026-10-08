@echo off
title Multichat - Sviluppo

rem Passa dalla cartella backend alla cartella del compose.yaml
pushd "%~dp0.."
if errorlevel 1 (
    echo Impossibile aprire la cartella del progetto.
    pause
    exit /b 1
)

docker compose -f compose.yaml up --build --watch

popd
pause