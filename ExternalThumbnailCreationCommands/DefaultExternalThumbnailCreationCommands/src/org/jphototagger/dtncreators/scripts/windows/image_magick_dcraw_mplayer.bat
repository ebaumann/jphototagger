@echo off

rem Author: Matthias Vonken, 2011/05/05
rem         Extended by Elmar Baumann 2001/07/11 (Videos with mplayer)

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

rem Video
if /I "%THUMB_SUFFIX%" ==".m2ts" GOTO VIDEO
if /I "%THUMB_SUFFIX%" ==".mts" GOTO VIDEO
if /I "%THUMB_SUFFIX%" ==".mpg" GOTO VIDEO
if /I "%THUMB_SUFFIX%" ==".mpeg" GOTO VIDEO
if /I "%THUMB_SUFFIX%" ==".mp2" GOTO VIDEO
if /I "%THUMB_SUFFIX%" ==".mp4" GOTO VIDEO
if /I "%THUMB_SUFFIX%" ==".mov" GOTO VIDEO
if /I "%THUMB_SUFFIX%" ==".wmv" GOTO VIDEO
if /I "%THUMB_SUFFIX%" ==".avi" GOTO VIDEO

:VIDEO
set TEMP_VIDEO_FILENAME=00000001.jpg
cd "%TMP%"
"${mplayer.exe}" -really-quiet -nosound -nolirc -vo jpeg:outdir=. -frames 1 -zoom -xy %MAX_DIM% %IMAGE% > NUL
type %TEMP_VIDEO_FILENAME%
del %TEMP_VIDEO_FILENAME%
goto END

"${dcraw.exe}" -e -c %IMAGE% | "${convert.exe}" - -thumbnail %MAX_DIM%x%MAX_DIM% -auto-orient jpg:-
goto END

:OTHER
"${convert.exe}" %IMAGE% -thumbnail %MAX_DIM%x%MAX_DIM% -auto-orient jpg:-

:END
