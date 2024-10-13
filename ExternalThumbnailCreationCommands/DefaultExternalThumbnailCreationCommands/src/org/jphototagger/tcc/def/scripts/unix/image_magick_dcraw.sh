#!/usr/bin/env bash
#
# Author : Elmar Baumann <eb@elmar-baumann.de>
# Date   : 2008/08/02
# Doc    : Create from an image a JPEG thumbnail and write it to stdout
#          1st parameter: image filename
#          2nd Parameter: maximum pixel count of the thumbnail width or height
#
#          Required Programs: - ImageMagick, esp. identify and magick
#                             - dcraw
#
################################################################################

# allow spaces within filenames
IFS="
"

# image file name (1st parameter)
image_filename=$1

# maximum pixel count of the thumbnail width or height (2nd parameter)
length=$2

# directory for temporary dcraw file
temp_dir=$HOME/tmp

# filename of dcraw temporary file
temp_filename=$temp_dir/$(basename $0).$$.ppm

# minimum pixel count of the thumbnail width or height
MIN_LENGTH=50

# filename suffixes of not RAW files
not_raw_filename_suffixes="bmp
dng
gif
jpeg
jpg
png
tif
tiff
xcf"

################################################################################

function check_params() {
    if [ -z $image_filename ] || [ -z $length ]
    then
        echo "Wrong count of parameters (1st image file name, 2nd thumbnail width in pixel)!" >&2
        exit 1
    fi
    if [ $length -lt $MIN_LENGTH ]
    then
        echo "Thumbnail width has to be at least minimum $MIN_LENGTH pixels!" >&2
        exit 2
    fi
}

function check_files() {
    if ! [ -f $image_filename ]
    then
        echo "image file '$image_filename' does not exist!" >&2
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
    touch $temp_filename
    if [ $? -ne 0 ]
    then
        echo "Temporary directory $temp_dir is not writable!" >&2
        exit 5
    fi
    rm $temp_filename
}

function is_raw_file() {
    local filename_suffix=$(echo $image_filename | sed 's%.*\.\(.*\)%\1%')
    echo $not_raw_filename_suffixes | grep -qivw $filename_suffix
    echo $?
}

function is_landscape() {
    local filename=$1
    width=$(${identify} -format '%w' $filename)
    height=$(${identify} -format '%h' $filename)
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
    ${magick} $filename -resize $length_cmd -unsharp 0.5x0.5+1.0+0.1 jpg:-
}

# dcraw-Parameter:
# -c write to stdout
# -h half-size color image (faster)

function raw_to_stdout() {
    ${dcraw} -c -h $image_filename > $temp_filename
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
    if [ -f $temp_filename ]
    then
        rm $temp_filename
    fi
}

check_params
check_files
thumbnail_to_stdout
tear_down
