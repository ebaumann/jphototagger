#!/usr/bin/env bash
#
# Author : Elmar Baumann <eb@elmar-baumann.de>
#          Martin Pohlack <martinp@gmx.de>
# Date   : 2008/08/02
# Doc    : Von einem Bild ein JPEG-Thumbnail ausgeben auf die Standardausgabe
#          1. Parameter: Bilddateiname
#          2. Paraeter: Länge der längeren Thumbnailseite in Pixel
#
#          Benötigte Programme: - ImageMagick, davon identify und convert
#                               - dcraw
#
#          Falls von anderen Programmen generierte Thumbnails
#          existieren, werden diese genutzt um die JPEG thumbnails zu
#          erzeugen.  Das geht um Größenordnungen schneller.
################################################################################

# Sollte diese Datei benutzt werden, bitte kopieren und Kopie benutzen, da
# zukünftige Installationen sie überschreiben dürfen

# Leerzeichen etc. in Dateinamen erlauben
IFS="
"

# Name der Bilddatei, für die das Thumbnail erzeugt werden soll (1. Parameter)
image_filename="$1"

# Länge der längeren Thumbnailseite in Pixel (2. Parameter)
length="$2"

# Speicherort für die Ausgabe von dcraw
temp_dir="$HOME"/tmp

# Dateiname des von dcraw ausgegebenen temporären Bilds
temp_filename="$temp_dir"/$(basename "$0").$$.ppm

# So viele Pixel muss die längere Thumbnailseite mindesten haben
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
	echo "Falsche Parameteranzahl (1. Bilddatei, 2. Länge)!" >&2
	exit 1
    fi
    if [ $length -lt $MIN_LENGTH ]
    then
	echo "Thumbnaillänge muss mindestens $MIN_LENGTH sein!" >&2
	exit 2
    fi
}

function check_files() {
    if ! [ -f "$image_filename" ]
    then
	echo "Bilddatei '$image_filename' existiert nicht!" >&2
	exit 3
    fi
    if ! [ -d "$temp_dir" ]
    then
	echo "Temporärverzeichnis '$temp_dir' existiert nicht!" >&2
	exit 4
    fi
    if [ ! -w "$temp_dir" ]
    then
	echo "$temp_dir lässt sich nicht beschreiben!" >&2
	exit 5
    fi
}

function is_raw_file() {
    local filename_suffix=$(echo $image_filename | sed 's%.*\.\(.*\)%\1%')
    echo $not_raw_filename_suffixes | grep -qivw $filename_suffix
    echo $?
}

function is_landscape() {
    local filename=$1
    width=$(identify -format '%w' $filename)
    height=$(identify -format '%h' $filename)
    test $width -gt $height
    echo $?
}

function image_to_stdout() {
    local filename="$1"
    local length_cmd="x${length}"

    if [ $(is_landscape "$filename") -eq 0 ]
    then
	length_cmd="${length}x"
    fi

    # compute location of thumbnails according to:
    #   "Thumbnail Managing Standard",
    #   http://jens.triq.net/thumbnail-spec/index.html
    fd_thumb_name_normal=~/".thumbnails/normal/`echo -n "file://$image_filename" | md5sum | cut -f1 -d\ `.png"
    fd_thumb_name_large=~/".thumbnails/large/`echo -n "file://$image_filename" | md5sum | cut -f1 -d\ `.png"

    if [ -r "$fd_thumb_name_large" ]
    then
        filename=$fd_thumb_name_large
        #echo "Large" >&2
    elif [ -r "$fd_thumb_name_normal" ]
    then
        filename=$fd_thumb_name_normal
        #echo "Normal" >&2
    fi

    convert "$filename" -resize $length_cmd -unsharp 0.5x0.5+1.0+0.1 jpg:-
}

# dcraw-Parameter:
# -c Ausgabe auf stdout
# -h half-size color image (schneller)

function raw_to_stdout() {
    dcraw -c -h $image_filename > $temp_filename
    image_to_stdout $temp_filename
    rm $temp_filename
}

function thumbnail_to_stdout() {
    if [ $(is_raw_file) -eq 0 ]
    then
	raw_to_stdout
    else
	image_to_stdout $image_filename
    fi
}

function tear_down {
    if [ -f "$temp_filename" ]
    then
	rm "$temp_filename"
    fi
}

check_params
check_files
thumbnail_to_stdout
tear_down
