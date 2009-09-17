#!/usr/bin/env bash

# User-Settings-Vereichnis umschalten zwischen Test- und Arbeitsdaten

APP_HOME_DIR=${HOME}/.de.elmar_baumann
ORIGINAL_DIR=${APP_HOME_DIR}/ImageMetaDataViewer.original
TEST_DIR=${APP_HOME_DIR}/ImageMetaDataViewer.test
LINK=${APP_HOME_DIR}/ImageMetaDataViewer
LINK_TO_ORIGINAL=${LINK}/original
LINK_TO_TEST=${LINK}/test
TEST_IMG_SRC=${PROJECTDIR}/JPhotoTagger/Program/res/img
TEST_IMG_TARGET=${HOME}/tmp/img

function check_exists_dir() {
    local dir=$1
    if ! [ -d $dir ]
    then
        echo "Verzeichnis '${dir}' existiert nicht!"
        exit 1
    fi
}

function check_exists_file() {
    local file=$1
    if ! [ -f $file ]
    then
        echo "Datei '${file}' existiert nicht!"
        exit 1
    fi
}

function create_link() {
    local toggle_dir=;

    if [ -f $LINK_TO_TEST ]
    then
        echo "Schalte um auf Arbeitsdaten"
        toggle_dir=$ORIGINAL_DIR
        rm -rf ${TEST_IMG_TARGET}
    fi
    if [ -f $LINK_TO_ORIGINAL ]
    then
        echo "Schalte um auf Testdaten"
        toggle_dir=$TEST_DIR
        copy_test_images
    fi

    # ln -sf funktioniert nicht
    rm $LINK
    ln -s $toggle_dir $LINK
}

function copy_test_images() {
    mkdir -p ${TEST_IMG_TARGET} \
    && \
    cp ${TEST_IMG_SRC}/*.jpg \
        ${TEST_IMG_SRC}/*.NEF \
        ${TEST_IMG_SRC}/*.xmp \
        ${TEST_IMG_TARGET} \
    && \
    chmod +w ${TEST_IMG_TARGET}/*
}

check_exists_dir $ORIGINAL_DIR
check_exists_dir $TEST_DIR
check_exists_file ${ORIGINAL_DIR}/original
check_exists_file ${TEST_DIR}/test
create_link
