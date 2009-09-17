#
# Zeichenkodierung dieses Skripts: ISO-8859-1
#
# Author : Elmar Baumann <eb@elmar-baumann.de>
# Date   : 2008/08/02
# Doc	: Von einem Bild ein JPEG-Thumbnail ausgeben auf die Standardausgabe unter Windows
#		  1. Parameter: Bilddateiname
#		  2. Paraeter: L�nge der l�ngeren Thumbnailseite in Pixel
#
#		  Ben�tigte Programme: 
#			- Unix Utils f�r Windows mit Bash , z.B. http://sourceforge.net/projects/unxutils/
#			  oder http://cygwin.com/
#			- ImageMagick, davon identify und convert
#			- dcraw
#
# Aufruf des Skripts: <Pfad zur Unix Utils>\sh.exe <dieses Skript> <Bilddateiname> <L�nge TN>
#                     Hat die Shell Probleme mit dem Ermitteln des Pfads zu den
#                     eigenen Programmen (z.B. sed), wird vorher die Umgebung in
#                     einer Batch-Datei gesetzt, die dann den Aufruf erledigt
#
# ACHTUNG: Dieses Skript setzt cygwin-bash voraus!
# Getestet unter Windows XP: 
#     - cygwin 1.5.25-15,
#     - bash Version 3.2.48(21)-release (i686-pc-cygwin)
# Aufruf durch thumbnail2stdoutwin.bat, das Backslashes im Dateinamen ersetzt
################################################################################

# Sollte diese Datei benutzt werden, bitte kopieren und Kopie benutzen, da
# zukünftige Installationen sie überschreiben dürfen

function subst_path_delims_unix() {
	echo $1 | sed 's%\\%/%g'
}

function subst_path_delims_dos() {
	echo $1 | sed 's%/%\\%g'
}

# Leerzeichen etc. in Dateinamen erlauben
IFS="
"
# Pfad zu dcraw
DCRAW='D:/dcraw/dcraw.exe'

# Verzeichnis mit ImageMagick-Bin�rdateien
IMAGEMAGICK_BIN_DIR='D:/ImageMagick'

# Name der Bilddatei, f�r die das Thumbnail erzeugt werden soll (1. Parameter)
image_filename=$(subst_path_delims_unix $1)

# Dateiname des von dcraw ausgegebenen tempor�ren Bilds
# Schreibrechte m�ssen im Verzeichnis existieren!
temp_filename=C:/temp/thumbnail2stdoutwin.sh.$$.ppm

# L�nge der l�ngeren Thumbnailseite in Pixel (2. Parameter)
length=$2

# So viele Pixel muss die l�ngere Thumbnailseite mindestens haben
MIN_LENGTH=50

# Endungen aller Dateinamen, die keine RAW-Datei benennen
not_raw_filename_suffixes="gif
jpeg
jpg
png
tif
tiff"

################################################################################

function check_params() {
	if [ -z $image_filename ] || [ -z $length ]
	then
		echo "Falsche Parameteranzahl (1. Bilddatei, 2. Laenge)!" >&2
		exit 1
	fi
	if [ $length -lt $MIN_LENGTH ]
	then
		echo "Thumbnaillaenge muss mindestens $MIN_LENGTH sein!" >&2
		exit 2
	fi
}

function check_files() {
	if ! [ -f $image_filename ]
	then
		echo "Bilddatei '$image_filename' existiert nicht!" >&2
		exit 3
	fi
}

function is_raw_file() {
	local filename_suffix=$(subst_path_delims_dos $image_filename | sed 's%.*\.\(.*\)%\1%')
	echo $not_raw_filename_suffixes | grep -qiw $filename_suffix
	echo $?
}

function is_landscape() {
	local filename=$(subst_path_delims_dos $1)
	local width=$($IMAGEMAGICK_BIN_DIR/identify -format '%w' "$filename" | tr -d \\r\\n)
	local height=$($IMAGEMAGICK_BIN_DIR/identify -format '%h' "$filename" | tr -d \\r\\n)

	test $width -gt $height
	echo $?
}

function image_to_stdout() {
	local filename=$1
	local length_cmd="x${length}"

	if [ $(is_landscape $filename) -eq 0 ]
	then
		length_cmd="${length}x"
	fi
	

	$IMAGEMAGICK_BIN_DIR/convert \
		"$(subst_path_delims_dos $filename)" \
		-resize $length_cmd \
		-unsharp 0.5x0.5+1.0+0.1 \
		-density 240x240 \
		jpg:-
}

# dcraw-Parameter:
# -c Ausgabe auf stdout
# -h half-size color image (schneller)

function raw_to_stdout() {
	$DCRAW -c -h "$(subst_path_delims_dos $image_filename)" > $temp_filename
	image_to_stdout $temp_filename
}

function thumbnail_to_stdout() {
	if [ $(is_raw_file) -ne 0 ]
	then
		raw_to_stdout
	else
		image_to_stdout $image_filename
	fi
}

function tear_down {
	if [ -f $temp_filename ]
	then
		rm $temp_filename
	fi
}

check_params
check_files
thumbnail_to_stdout
tear_down
