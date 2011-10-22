#!/usr/bin/env bash
#
# Author : Elmar Baumann <eb@elmar-baumann.de>
# Date   : 2009/09/18
#
# Toggles between test and working data of JPhotoTagger in the home directory
#
################################################################################

. $PROJECTDIR/JPhotoTagger/Support/script/functions.sh || exit 1

APP_HOME_DIR=${HOME}/.de.elmar_baumann
ORIGINAL_DIR=${APP_HOME_DIR}/ImageMetaDataViewer.original
TEST_DIR=${APP_HOME_DIR}/ImageMetaDataViewer.test
LINK=${APP_HOME_DIR}/ImageMetaDataViewer
LINK_TO_ORIGINAL=${LINK}/original
LINK_TO_TEST=${LINK}/test
TEST_IMG_SRC=${PROJECTDIR}/JPhotoTagger/Support/res/img
TEST_IMG_TARGET=${HOME}/tmp/img

function create_link() {
    local toggle_dir=;

    if [ -f $LINK_TO_TEST ]
    then
        echo "Toggle to working data"
        toggle_dir=$ORIGINAL_DIR
        rm -rf ${TEST_IMG_TARGET}
    fi
    if [ -f $LINK_TO_ORIGINAL ]
    then
        echo "Toggle to test data"
        toggle_dir=$TEST_DIR
        copy_test_images
    fi

    # ln -sf does not work
    rm $LINK
    ln -s $toggle_dir $LINK
}

function copy_test_images() {
    mkdir -p ${TEST_IMG_TARGET} \
    && \
    cp --preserve=timestamps ${TEST_IMG_SRC}/*.[Jj][Pp][Gg] \
        ${TEST_IMG_SRC}/*.[Nn][Ee][Ff] \
        ${TEST_IMG_SRC}/*.[Tt][Ii][Ff] \
        ${TEST_IMG_SRC}/*.[Xx][Mm][Pp] \
        ${TEST_IMG_TARGET} \
    && \
    chmod +w ${TEST_IMG_TARGET}/*
}

check_exists_dir  ${ORIGINAL_DIR}
check_exists_dir  ${TEST_DIR}
check_exists_file ${ORIGINAL_DIR}/original
check_exists_file ${TEST_DIR}/test
create_link
