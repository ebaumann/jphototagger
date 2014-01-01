@echo off

rem Erzeugt Bilder mit ImageMagick unter Windows
rem Erforderliche Programme mit Download-URLs stehen in der Datei
rem thumbnail2stdoutwin.sh. Pfade und Umgebungsvariablen sind
rem anzupassen!

rem Sollte diese Datei benutzt werden, bitte kopieren und Kopie benutzen, da
rem zukünftige Installationen sie überschreiben dürfen

set file=%1
set length=%2
rem Backslashes  '\' ersetzen durch Slashes "/"
set file=%file:\=/%

D:\cygwin\bin\bash.exe E:/projekte/JPhotoTagger/Program/script/thumbnail2stdoutwin.sh %file% %length%
