#!/usr/bin/env bash
#
# Author : Elmar Baumann <eb@elmar-baumann.de>,
#          extended by Fabian Ritzmann (Video files, mplayer) 2011/07/25
# Date   : 2008/08/02
# Doc    : Writes a JPEG thumbnail into the standard output
#          1. Parameter: Name of image file or video file
#          2. Parameter: Length in pixels of the largest thumbnail dimension
#
#          Required programms: - ImageMagick (identify and convert)
#                              - dcraw
#                              - MPlayer
#
################################################################################

# Allowing spaces whithin file names
IFS="
"

# Name of the image file (1. Parameter)
image_filename="$1"

# Length of the thumbnail's largest dimension in pixels (2. Parameter)
length=$2

# Temproary file folder for dcraw und MPlayer
temp_dir=$HOME/tmp

# Name of the temporary file written by dcraw
temp_filename="$temp_dir/$(basename $0).$$.ppm"

# Name of the temporary file written by MPlayer
temp_video_filename="$temp_dir/00000001.jpg"

# Minimum of the thumbnail's largest dimension in pixel
MIN_LENGTH=50

# File name suffixes of non RAW files
not_raw_filename_suffixes="gif
jpeg
jpg
png
tif
tiff"

# File name suffixes of video files
video_filename_suffixes="m2ts
mts
mpg
mpeg
mp2
mp4
mov
wmv
avi"

################################################################################

function check_params() {
    if [ -z "$image_filename" ] || [ -z $length ]
    then
        echo "Wrong parameter count (1. file name, 2. length of thumbnail)!" >&2
        exit 1
    fi
    if [ $length -lt $MIN_LENGTH ]
    then
        echo "Length of thumbnail is lower than $MIN_LENGTH pixels!" >&2
        exit 2
    fi
}

function check_files() {
    if ! [ -f "$image_filename" ]
    then
        echo "File '$image_filename' does not exist!" >&2
        exit 3
    fi
    if ! [ -d $temp_dir ]
    then
        mkdir -p $temp_dir
    fi
    if ! [ -d $temp_dir ]
    then
        echo "Temporary directory '$temp_dir' does not exist!" >&2
        exit 4
    fi
    touch "$temp_filename"
    if [ $? -ne 0 ]
    then
        echo "$temp_dir is not writable!" >&2
        exit 5
    fi
    rm "$temp_filename"
}

function is_video_file() {
    local filename_suffix=$(echo $image_filename | sed 's%.*\.\(.*\)%\1%')
    echo $video_filename_suffixes | grep -qiw $filename_suffix
    echo $?
}

function is_raw_file() {
    local filename_suffix=$(echo $image_filename | sed 's%.*\.\(.*\)%\1%')
    echo $not_raw_filename_suffixes | grep -qivw $filename_suffix
    echo $?
}

function is_landscape() {
    local filename="$1"
    width=$(/usr/bin/identify -format '%w' "$filename")
    height=$(/usr/bin/identify -format '%h' "$filename")
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

    /usr/bin/convert "$filename" -resize $length_cmd -unsharp 0.5x0.5+1.0+0.1 jpg:-
}

# dcraw-Parameter:
# -c write to stdout
# -h half-size color image (faster)

function raw_to_stdout() {
    /usr/bin/dcraw -c -h "$image_filename" > "$temp_filename"
    image_to_stdout "$temp_filename"
    rm "$temp_filename"
}

function video_to_stdout() {
    /usr/bin/mplayer -really-quiet -nolirc -vo jpeg:outdir="$temp_dir" -frames 1 -zoom -xy ${length} "$image_filename"
    cat "$temp_video_filename"
    rm "$temp_video_filename"
}

function thumbnail_to_stdout() {
    if [ $(is_video_file) -eq 0 ]
    then
        video_to_stdout
    elif [ $(is_raw_file) -eq 0 ]
    then
        raw_to_stdout
    else
        image_to_stdout "$image_filename"
    fi
}

function tear_down {
    if [ -f "$temp_filename" ]
    then
        rm "$temp_filename"
    fi
    if [ -f "$temp_video_filename" ]
    then
        rm "$temp_video_filename"
    fi
}

check_params
check_files
thumbnail_to_stdout
tear_down
