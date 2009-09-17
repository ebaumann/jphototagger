#!/usr/bin/env bash

IFS="
"

IMAGE_FILE=$1
TARGET_FILE=${HOME}/tmp/filenames.txt

echo "${IMAGE_FILE}" >> ${TARGET_FILE}

