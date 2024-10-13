@echo off

rem Author: Matthias Vonken, 2011/05/05
rem         Extended by Elmar Baumann 2011/07/11 (Videos with mplayer)

set IMAGE=%1
set MAX_DIM=%2

rem Extracting image suffix
for %%i in (%IMAGE%) DO set THUMB_SUFFIX=%%~xi

rem Video
if /I "%THUMB_SUFFIX%" ==".avi" GOTO VIDEO
if /I "%THUMB_SUFFIX%" ==".flv" GOTO VIDEO
if /I "%THUMB_SUFFIX%" ==".m2ts" GOTO VIDEO
if /I "%THUMB_SUFFIX%" ==".mkv" GOTO VIDEO
if /I "%THUMB_SUFFIX%" ==".mov" GOTO VIDEO
if /I "%THUMB_SUFFIX%" ==".mp2" GOTO VIDEO
if /I "%THUMB_SUFFIX%" ==".mp4" GOTO VIDEO
if /I "%THUMB_SUFFIX%" ==".mpeg" GOTO VIDEO
if /I "%THUMB_SUFFIX%" ==".mpg" GOTO VIDEO
if /I "%THUMB_SUFFIX%" ==".mts" GOTO VIDEO
if /I "%THUMB_SUFFIX%" ==".ts" GOTO VIDEO
if /I "%THUMB_SUFFIX%" ==".wmv" GOTO VIDEO

GOTO IMAGES

:VIDEO
set TEMP_VIDEO_FILENAME=00000001.jpg
cd "%TMP%"
"${mplayer.exe}" -really-quiet -nosound -nolirc -vo jpeg:outdir=. -frames 1 -zoom -xy %MAX_DIM% %IMAGE% > NUL
type %TEMP_VIDEO_FILENAME%
del %TEMP_VIDEO_FILENAME%
goto END

:IMAGES
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
