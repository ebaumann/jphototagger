#!/usr/bin/env bash
#
# Author : Elmar Baumann <eb@elmar-baumann.de>
# Date   : 2009/09/18
# Doc    : Functions used by other scripts
#
################################################################################

# Checks whether a file exists and is a file. Terminates the script, if the
# file does not exist or is not a file.
# Parameter: 1. filename

function check_exists_file() {
    test -f "$1"
    ret=$?
    if [ $ret != 0 ]
    then
        echo "File '$1' does not exist!"
        exit $ret
    fi
    return $ret
}

export -f check_exists_file

# Checks whether a directory exists and is a directory. Terminates the script,
# if the directory does not exist or is not a directory.
# Parameter: 1. directoryname

function check_exists_dir() {
    test -d "$1"
    ret=$?
    if [ $ret != 0 ]
    then
        echo "Directory '$1' does not exist!"
        exit $ret
    fi
    return $ret
}

export -f check_exists_dir

# Checks wheter a directory is writable. Terminates the script, if the directory
# is not writable.
# Parameter: 1. directoryname

function check_can_write() {
    check_exists_dir "$1"
    tmpfile="$1/$(basename $0).$$.tmp"
    touch "$tmpfile"
    ret=$?
    rm -f "$tmpfile"
    if [ $ret != 0 ]
    then
        echo "Directory '$1' is not writeable!"
        exit $ret
    fi
    return $ret
}

export -f check_can_write
