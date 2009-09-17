#!/usr/bin/env bash

# Testbilder in ein beschreibbares Testverzeichnis kopieren

IFS="
"

TEST_IMG_SRC=${PROJECTDIR}/JPhotoTagger/Program/res/img
TEST_IMG_TARGET=${HOME}/tmp/img

if ! [ -d ${TEST_IMG_TARGET} ]
then
    mkdir -p ${TEST_IMG_TARGET}
fi

cp -uv ${TEST_IMG_SRC}/* ${TEST_IMG_TARGET}
chmod +w ${TEST_IMG_TARGET}/*
