@echo off

set IMAGE=%1
set MAX_DIM=%2

rem Extracting image suffix
for %%i in (%IMAGE%) DO set THUMB_SUFFIX=%%~xi

rem Deciding whether RAW or not 
if /I "%THUMB_SUFFIX%" ==".jpg" GOTO OTHER
if /I "%THUMB_SUFFIX%" ==".tif" GOTO OTHER
if /I "%THUMB_SUFFIX%" ==".gif" GOTO OTHER
if /I "%THUMB_SUFFIX%" ==".png" GOTO OTHER
if /I "%THUMB_SUFFIX%" ==".psd" GOTO OTHER

${dcraw.exe} -e -c %IMAGE% | ${convert.exe} - -thumbnail %MAX_DIM%x%MAX_DIM% -auto-orient jpg:-
goto END

:OTHER
${convert.exe} %IMAGE% -thumbnail %MAX_DIM%x%MAX_DIM% -auto-orient jpg:-

:END
