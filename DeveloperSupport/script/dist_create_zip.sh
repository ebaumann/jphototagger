#!/usr/bin/env bash
#
# Author : Elmar Baumann <eb@elmar-baumann.de>
# Date   : 2009/09/18
#
# Creates a ZIP distribution
#
################################################################################

. $PROJECTDIR/JPhotoTagger/Support/script/functions.sh || exit 1

IFS="
"

PROJECT=JPhotoTagger
PROJECT_DIR=${PROJECTDIR}/${PROJECT}
PROGRAM_DIR=${PROJECT_DIR}/main-repository/Program
DIST_FILES_DIR=${PROJECT_DIR}/dist_files
SCRIPT_SRC_DIR=${DIST_FILES_DIR}/script
DCRAW_DIR=${DIST_FILES_DIR}/dcraw/bin
MANUAL_SRC_DIR=${DIST_FILES_DIR}/manual
PROGRAM_DIST_DIR=${PROGRAM_DIR}/dist
UPLOAD_DIR=${DIST_FILES_DIR}/upload
ZIPFILE=${UPLOAD_DIR}/JPhotoTagger.zip
TMP_DIR=${HOME}/tmp
TMP_FILE=${TMP_DIR}/$(basename $0)-$$

check_exists_dir  ${PROGRAM_DIR}
check_exists_dir  ${PROGRAM_DIST_DIR}
check_exists_dir  ${SCRIPT_SRC_DIR}
check_exists_dir  ${MANUAL_SRC_DIR}
check_exists_dir  ${DCRAW_DIR}
check_exists_dir  ${DIST_FILES_DIR}
check_exists_dir  ${UPLOAD_DIR}
check_can_write   ${TMP_DIR}
check_can_write   ${UPLOAD_DIR}

if [ -d ${TMP_DIR}/${PROJECT} ]
then
    echo "'${TMP_DIR}/${PROJECT}' already exists!"
    exit 1
fi

function make_zip() {
    rm -f ${ZIPFILE}
    rm -f ${PROGRAM_DIST_DIR}/README.TXT
    if ! [ -d ${PROGRAM_DIST_DIR}/scripts ]; then mkdir ${PROGRAM_DIST_DIR}/scripts || exit 1; fi
    cp -a ${SCRIPT_SRC_DIR}/* ${PROGRAM_DIST_DIR}/scripts
    cp -a ${MANUAL_SRC_DIR}/* ${PROGRAM_DIST_DIR}
    if ! [ -d ${PROGRAM_DIST_DIR}/lib/dcraw ]; then mkdir -p ${PROGRAM_DIST_DIR}/lib/dcraw; fi
    cp -a ${DCRAW_DIR}/* ${PROGRAM_DIST_DIR}/lib/dcraw
    mkdir ${TMP_DIR}/${PROJECT} || exit 1
    cp -a ${PROGRAM_DIST_DIR}/* ${TMP_DIR}/${PROJECT}
    cd ${TMP_DIR}
    zip  -r -m $ZIPFILE ${PROJECT}
}

umask 0022
make_zip
