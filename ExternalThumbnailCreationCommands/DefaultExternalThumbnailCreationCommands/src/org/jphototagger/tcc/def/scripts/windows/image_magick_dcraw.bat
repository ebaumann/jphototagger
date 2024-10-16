@echo off

rem Author: Matthias Vonken, 2011/05/05

set IMAGE=%1
set MAX_DIM=%2

rem Extracting image suffix
for %%i in (%IMAGE%) DO set THUMB_SUFFIX=%%~xi

rem Deciding whether RAW or not
if /I "%THUMB_SUFFIX%" ==".bmp" GOTO OTHER
if /I "%THUMB_SUFFIX%" ==".dng" GOTO OTHER
if /I "%THUMB_SUFFIX%" ==".gif" GOTO OTHER
if /I "%THUMB_SUFFIX%" ==".jpg" GOTO OTHER
if /I "%THUMB_SUFFIX%" ==".png" GOTO OTHER
if /I "%THUMB_SUFFIX%" ==".psd" GOTO OTHER
if /I "%THUMB_SUFFIX%" ==".tif" GOTO OTHER
if /I "%THUMB_SUFFIX%" ==".tiff" GOTO OTHER
if /I "%THUMB_SUFFIX%" ==".ttf" GOTO OTHER
if /I "%THUMB_SUFFIX%" ==".xcf" GOTO OTHER

"${dcraw.exe}" -e -c %IMAGE% | "${magick.exe}" - -thumbnail %MAX_DIM%x%MAX_DIM% -auto-orient jpg:-
goto END

:OTHER
"${magick.exe}" %IMAGE% -thumbnail %MAX_DIM%x%MAX_DIM% -auto-orient jpg:-

:END
