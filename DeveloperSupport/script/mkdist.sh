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
WEBSITE_DIR=${PROJECT_DIR}/main-repository/Website
SUPPORT_DIR=${PROJECT_DIR}/Support
SCRIPT_SRC_DIR=${PROJECT_DIR}/dist_files/script
MANUAL_SRC_DIR=${PROJECT_DIR}/dist_files/manual
PROGRAM_DIST_DIR=${PROGRAM_DIR}/dist
HTML_DOWNLOAD_DOC=${WEBSITE_DIR}/download.html
VERSION_FILE=${WEBSITE_DIR}/jphototagger-version.txt
DOWNLOAD_DIR=${WEBSITE_DIR}/dist
ZIPFILE=${DOWNLOAD_DIR}/JPhotoTagger.zip
APP_VERSION_FILE=${PROGRAM_DIR}/src/org/jphototagger/program/app/AppInfo.java
TMP_DIR=${HOME}/tmp
TMP_FILE=${TMP_DIR}/$(basename $0)-$$

check_exists_dir  ${PROGRAM_DIR}
check_exists_dir  ${PROGRAM_DIST_DIR}
check_exists_dir  ${SCRIPT_SRC_DIR}
check_exists_dir  ${MANUAL_SRC_DIR}
check_exists_dir  ${DOWNLOAD_DIR}
check_exists_file ${APP_VERSION_FILE}
check_exists_file ${HTML_DOWNLOAD_DOC}
check_can_write   ${TMP_DIR}
check_can_write   ${DOWNLOAD_DIR}

if [ -d ${TMP_DIR}/${PROJECT} ]
then
    echo "'${TMP_DIR}/${PROJECT}' existiert bereits!"
    exit 1
fi

function make_complete_distribution() {
    rm -f ${ZIPFILE}
    rm -f ${PROGRAM_DIST_DIR}/README.TXT
    if ! [ -d ${PROGRAM_DIST_DIR}/scripts ]; then mkdir ${PROGRAM_DIST_DIR}/scripts || exit 1; fi
    cp -a ${SCRIPT_SRC_DIR}/* ${PROGRAM_DIST_DIR}/scripts
    cp -a ${MANUAL_SRC_DIR}/* ${PROGRAM_DIST_DIR}
    mkdir ${TMP_DIR}/${PROJECT} || exit 1
    cp -a ${PROGRAM_DIST_DIR}/* ${TMP_DIR}/${PROJECT}
    cd ${TMP_DIR}
    zip  -r -m $ZIPFILE ${PROJECT}
}

function set_version_to_html_project_files() {
	echo Aktualisiere Versionsinfo in Projektseite ...
    version=$(grep APP_VERSION ${APP_VERSION_FILE} \
        | sed 's%.*"\([0-9].*\.[0-9].*\.[0-9].*\)".*%\1%')
    rfc_date=$(date +"%Y-%m-%dT%H:%M:%S%:z")

    sed -e "s%\(<span class=\"version\">\).*\(</span>\)%\1$version\2%" \
	-e "s%\(<meta name=\"date\" content=\"\)[0-9].*[0-9]\(\" />\)%\1$rfc_date\2%" \
	$HTML_DOWNLOAD_DOC \
     > $TMP_FILE
    mv $TMP_FILE $HTML_DOWNLOAD_DOC

    echo "<span class=\"version\">$version</span>" > $VERSION_FILE
}

umask 0022
make_complete_distribution
set_version_to_html_project_files
