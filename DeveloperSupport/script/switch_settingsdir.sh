#!/usr/bin/env bash

. $PROJECTDIR/JPhotoTagger/Support/script/functions.sh || exit 1

IFS="
"

APP_HOME_DIR=${HOME}/.de.elmar_baumann
TEST_DIR=${APP_HOME_DIR}/ImageMetaDataViewer.test
WORKING_DIR=${APP_HOME_DIR}/ImageMetaDataViewer.working
PRISTINE_DIR=${APP_HOME_DIR}/ImageMetaDataViewer.pristine
LINK=${APP_HOME_DIR}/ImageMetaDataViewer
TEST_IMG_SRC=${PROJECTDIR}/JPhotoTagger/Support/res/img
TEST_IMG_TARGET=${HOME}/tmp/img

function create_link() {
    local settings_dir=$1
    rm ${LINK}
    ln -s ${settings_dir} ${LINK}
}

function switch_to_pristine_settings() {
    echo "Switching to pristine settings..."
    rm -rf ${PRISTINE_DIR}
    mkdir -p ${PRISTINE_DIR}
    check_exists_dir ${PRISTINE_DIR}
    create_link ${PRISTINE_DIR}
}

function switch_to_test_settings() {
    echo "Switching to test settings..."
    check_exists_dir ${TEST_DIR}
    create_link ${TEST_DIR}
}

function switch_to_working_settings() {
    echo "Switching to working settings..."
    check_exists_dir ${WORKING_DIR}
    create_link ${WORKING_DIR}
}

function usage() {
cat << EOF
usage: $(basename $0) options

This script changes JPhotoTagger's settings directory.

OPTIONS:
   -p    Pristine settings
   -t    Test settings
   -w    Working settings
EOF
}

if [ $# -ne 1 ]
then
    usage
    exit 1
fi

while getopts "ptw" opt
do
    case $opt in
        p)
            switch_to_pristine_settings
            exit 0
        ;;
        t)
            switch_to_test_settings
            exit 0
        ;;
        w)
            switch_to_working_settings
            exit 0
        ;;
    esac
done

usage
